package info.kamann.samples.groovy.ast.pcs


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import groovyjarjarasm.asm.Opcodes;

import org.codehaus.groovy.ast.ASTNode 
import org.codehaus.groovy.ast.ClassNode 
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode 
import org.codehaus.groovy.ast.builder.AstBuilder 
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit 
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.ASTTransformation;

/**
 * @author kamann
 *
 */

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
class PropertyChangeSupportTransformation implements ASTTransformation, Opcodes {
	static int PUBLIC = 1
	
	public PropertyChangeSupportTransformation(){
		String.metaClass.toFirstLower << {
			delegate[0].toLowerCase() + delegate.substring(1)
		}
	}
	
	public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
		
		println "Start"
		List clazzNodes = astNodes.findAll{ASTNode astNode ->
			if (astNode instanceof ClassNode && astNode.getAnnotations(new ClassNode(WithPropertySupport))){
				return astNode
			}
		}
		
		clazzNodes.each{ClassNode clazzNode ->
			createPropertyChangeSupportField clazzNode
			createPropertyChangeFieldAdd clazzNode
			createPropertyChangeFieldRemove clazzNode
			
			List methodNodes = clazzNode.getAllDeclaredMethods()
			println methodNodes
			
			methodNodes.each{MethodNode methodNode ->
				if (methodNode.name.startsWith("set")){
					createSetterAddition methodNode
				}
			}
		}
		
	}
	
	private void createSetterAddition(MethodNode methodNode){
		String methodName = methodNode.name.substring(3).toFirstLower()
		
		def ast = new AstBuilder().buildFromSpec {
			expression{
				methodCall{
					variable "support"
					constant "firePropertyChange"
					argumentList{
						constant methodName
						attribute {
			                variable "this"
			                constant "name"
			            }
						variable "name"
					}
				}
			}
		}
		methodNode.code.statements.add(0, ast[0])
	}
	
	private void createPropertyChangeSupportField(ClassNode classNode){
		def ast = new AstBuilder().buildFromSpec {
			field {
                fieldNode "support", ACC_PUBLIC, PropertyChangeSupport, this.class, {
                    constructorCall(PropertyChangeSupport){
                    	variable "this"
                    }
                }
            }
		}
		classNode.addField ast[0].field
	}
	
	private void createPropertyChangeFieldAdd(ClassNode classNode){
		def ast = new AstBuilder().buildFromSpec {
			method('addPropertyChangeListener', PUBLIC, Object) {
				parameters { parameter 'listener': PropertyChangeListener.class }
				exceptions {}
				block { 
					expression {
                        methodCall {
                        	variable "support"
                            constant "addPropertyChangeListener"
                            argumentList {
                                variable "listener"
                            }
                        }
                    }
                }
                      
					
			}
		}
		MethodNode target = ast[0]
		classNode.addMethod target
	}
	
	private void createPropertyChangeFieldRemove(ClassNode classNode){
		def ast = new AstBuilder().buildFromSpec {
			method('removePropertyChangeListener', PUBLIC, Object) {
				parameters { parameter 'listener': PropertyChangeListener.class }
				exceptions {}
				block { 
					expression {
                        methodCall {
                        	variable "support"
                            constant "removePropertyChangeListener"
                            argumentList {
                                variable "listener"
                            }
                        }
                    }
                }
                      
					
			}
		}
		MethodNode target = ast[0]
		classNode.addMethod target
	}
	
	MethodNode makeMainMethod(MethodNode source) {
		def ast = new AstBuilder().buildFromSpec {
			method('testMmrtw1', PUBLIC, Void.TYPE) {
				parameters {
					parameter 'args': String[].class
				}
				exceptions {}
				block { }
			}
		}
		MethodNode target = ast[0]
		//target.code = source.code
		target
	}
	
}
