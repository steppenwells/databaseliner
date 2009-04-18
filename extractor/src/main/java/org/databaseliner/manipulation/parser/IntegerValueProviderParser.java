package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.FixedObjectValueProvider;
import org.databaseliner.manipulation.ValueProvider;
import org.dom4j.Node;

/**
 * Creates a integer value provider from the equivalent XML node.
 * 
 * <p>A integer value provider is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;value type="integer" data="1234" /&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>data</code> attribute specifies the integer to return.
 * It is a required field.</p>
 * 
 */
public class IntegerValueProviderParser implements ValueProviderParser {

	@Override
	public ValueProvider parse(Node valueNode) {
		
		String integerString = valueNode.selectSingleNode("@data").getText().trim();
		return new FixedObjectValueProvider(new Integer(integerString));
	}

}
