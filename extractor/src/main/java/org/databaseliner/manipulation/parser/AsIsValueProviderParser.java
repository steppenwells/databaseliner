package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.FixedObjectValueProvider;
import org.databaseliner.manipulation.ValueProvider;
import org.databaseliner.output.AsIsSQLString;
import org.dom4j.Node;

/**
 * Creates an "asIs" value provider from the equivalent XML node.
 * 
 * <p>A asIs value provider is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;value type="asIs" data="sysdate()" /&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>data</code> attribute specifies the string to return. This string
 * will not be quoted or escaped so can potentially contain sql that will evaluate 
 * when the data insertion script is run.
 * It is a required field.</p>
 * 
 */
public class AsIsValueProviderParser implements ValueProviderParser {

	@Override
	public ValueProvider parse(Node valueNode) {
		
		String asIsData = valueNode.selectSingleNode("@data").getText().trim();
		return new FixedObjectValueProvider(new AsIsSQLString(asIsData));
	}

}
