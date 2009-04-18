package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.FixedObjectValueProvider;
import org.databaseliner.manipulation.ValueProvider;
import org.dom4j.Node;

/**
 * Creates a decimal value provider from the equivalent XML node.
 * 
 * <p>A decimal value provider is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;value type="decimal" data="12.34" /&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>data</code> attribute specifies the decimal to return.
 * Decimals are converted to Doubles internally. Data is a required field.</p>
 * 
 */
public class DecimalValueProviderParser implements ValueProviderParser {

	@Override
	public ValueProvider parse(Node valueNode) {
		
		String decimalString = valueNode.selectSingleNode("@data").getText().trim();
		return new FixedObjectValueProvider(new Double(decimalString));
	}

}
