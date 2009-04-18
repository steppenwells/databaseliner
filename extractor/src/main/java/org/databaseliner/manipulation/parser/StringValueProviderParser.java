package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.FixedObjectValueProvider;
import org.databaseliner.manipulation.ValueProvider;
import org.dom4j.Node;

/**
 * Creates a String Value Provider from the equivalent XML node.
 * 
 * <p>A string value provider is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;value type="string" data="the quick brown fox" /&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>data</code> attribute specifies the string to return.
 * It is a required field.</p>
 * 
 */
public class StringValueProviderParser implements ValueProviderParser {

	@Override
	public ValueProvider parse(Node valueNode) {
		
		String returnString = valueNode.selectSingleNode("@data").getText().trim();
		return new FixedObjectValueProvider(returnString);
	}

}
