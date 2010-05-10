package info.kamann.samples.groovy.ast.pcs;

import groovyjarjarasm.asm.Opcodes;

import java.beans.PropertyChangeEvent 
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.codehaus.groovy.ast.ASTNode 
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

class PTest {
	private PropertyChangeListener listener
	private Map result = [name:"", oldValue:"", newValue:""]
	
	@Before
	final void setup(){
		listener = [propertyChange:{PropertyChangeEvent e -> 
			result.name = e.propertyName
			result.oldValue = e.oldValue
			result.newValue = e.newValue
		}] as PropertyChangeListener
		
	}
	

	@Test
	final void SimpleTest(){
		BeanWithoutPropertyChangeSupport bean = new BeanWithoutPropertyChangeSupport()
		bean.addPropertyChangeListener( listener)
		
		println bean.support.listeners
		
		bean.setName "jjjj"
		println "$result.name $result.oldValue $result.newValue"
		
		bean.setName "gggg"
		println "$result.name $result.oldValue $result.newValue"
		
		bean.removePropertyChangeListener( listener)
		bean.setName "kkkk"
		println "$result.name $result.oldValue $result.newValue"
	}

}
