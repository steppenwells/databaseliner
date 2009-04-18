package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.FieldProcessor;
import org.databaseliner.manipulation.FieldProcessorValueProvider;
import org.databaseliner.manipulation.ValueProvider;
import org.dom4j.Node;

/**
 * Creates a FieldProcessorValueProvider from the equivalent XML node.
 * 
 * <p>A field processor value provider is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;value type="processor" class="fully.qualified.classname" dataSourceColumn="columnName" /&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>class</code> attribute specifies a fully qualified class name of the FieldProcessor
 * to be used to generate values for this field.
 * It is a required field.</p>
 *
 * <p>The <code>dataSourceColumn</code> attribute specifies the column to get data from which is input
 * to the FieldProcessor. This does not have to be the column being manipulated.
 * It is a required field.</p>
 * 
 * @see FieldProcessor 
 */
public class FieldProcessorValueProviderParser implements ValueProviderParser {

	@SuppressWarnings("unchecked")
	@Override
	public ValueProvider parse(Node valueNode) {
		try {
		String className = valueNode.selectSingleNode("@class").getText().trim();
		Class processorClass = Class.forName(className);
		FieldProcessor processor = (FieldProcessor) processorClass.newInstance();
		
		String columnName = valueNode.selectSingleNode("@dataSourceColumn").getText().trim();
		return new FieldProcessorValueProvider(processor, columnName);
		
		} catch (Exception e) {
			throw new RuntimeException("could not configure FieldProcessorValueProvider", e);
		}
	}

}
