package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.AddColumnManipulation;
import org.databaseliner.manipulation.Manipulation;
import org.databaseliner.manipulation.ValueProvider;
import org.databaseliner.manipulation.ValueProviderType;
import org.dom4j.Node;

/**
 * Creates an AddColumnManipulation from the equivalent XML node.
 * 
 * <p>An add column manipulation is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;manipulation type="addColumn" originalSchema="originalSchema" originalTableName="originalTableName" addColumn="columnName" &gt;
 *  	&lt;value type="valueProviderType".../&gt;
 *  &lt;/manipulation&gt;
 * </code>
 * </p>
 * <p>The <code>originalSchema</code> and <code>originalTableName</code>attributes specify the table
 * to add the column to. These correspond to the values that were set up in the extractions or derived
 * during the extraction run (if you are having trouble getting the manipulation to find the table check
 * the log output to see what databaseliner has determined the table is really called). If schema is not
 * being used in your databaseliner configuration the attribute can be omitted</p>
 * 
 * <p>The <code>addColumn</code> attribute specifies name of the column to add.
 * It is a required field.</p>
 * 
 * <p>The <code>value</code> element specifies the data to set for field. The different settings for this element
 * are defined in the ValueProviderType documentation. The value element is required and only one can be configured
 * per manipulation</p>
 * 
 * @see ValueProviderType
 */
public class AddColumnManipulationParser implements ManipulationParser {

	@Override
	public Manipulation parse(Node manipulationNode) {
		
		Node originalSchemaNode = manipulationNode.selectSingleNode("@originalSchema");
		String originalSchemaName = originalSchemaNode != null ? originalSchemaNode.getText().trim() : null;
		
		String originalTableName = manipulationNode.selectSingleNode("@originalTableName").getText().trim();
		String columnToAdd = manipulationNode.selectSingleNode("@addColumn").getText().trim();
		
		ValueProvider valueProvider = ValueProviderType.getValueProviderForNode(manipulationNode.selectSingleNode("value"));
		AddColumnManipulation manipulation = new AddColumnManipulation(originalSchemaName, originalTableName, columnToAdd);
		
		manipulation.setValueProvider(valueProvider);
		return manipulation;
	}

}
