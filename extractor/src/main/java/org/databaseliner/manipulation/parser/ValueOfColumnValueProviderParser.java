package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.ValueOfColumnValueProvider;
import org.databaseliner.manipulation.ValueProvider;
import org.dom4j.Node;

/**
 * Creates a ValueOfColumnValueProviderfrom the equivalent XML node.
 * 
 * <p>A value of column value provider is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;value type="valueOfColumn" column="columnName" /&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>column</code> attribute specifies the column to get data from to output
 * for this fields value.
 * It is a required field.</p>
 * 
 */
public class ValueOfColumnValueProviderParser implements ValueProviderParser {

	@Override
	public ValueProvider parse(Node valueNode) {
		
		String columnName = valueNode.selectSingleNode("@column").getText().trim();
		return new ValueOfColumnValueProvider(columnName);
	}

}
