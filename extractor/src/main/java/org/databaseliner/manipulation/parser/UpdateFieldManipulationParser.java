package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.Manipulation;
import org.databaseliner.manipulation.UpdateFieldManipulation;
import org.databaseliner.manipulation.ValueProvider;
import org.databaseliner.manipulation.ValueProviderType;
import org.dom4j.Node;

/**
 * Creates a UpdateFieldManipulation from the equivalent XML node.
 * 
 * <p>A update field manipulation is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;manipulation type="updateField" originalSchema="originalSchema" originalTableName="originalTableName" updateColumn="columnName" &gt;
 * 		&lt;value type="valueProviderType".../&gt;
 *  &lt;/manipulation&gt;
 * </code>
 * </p>
 * <p>The <code>originalSchema</code> and <code>originalTableName</code>attributes specify the table
 * that contains the column to update the values of. These correspond to the values that were set up in the extractions or derived
 * during the extraction run (if you are having trouble getting the manipulation to find the table check
 * the log output to see what databaseliner has determined the table is really called). If schema is not
 * being used in your databaseliner configuration the attribute can be omitted</p>
 * 
 * <p>The <code>updateColumn</code> attribute specifies the column contains the data to update.
 * It is a required field.</p>
 * 
 * <p>The <code>value</code> element specifies the data to set for field. The different settings for this element
 * are defined in the ValueProviderType documentation. The value element is required and only one can be configured
 * per manipulation</p>
 * 
 * @see ValueProviderType
 * 
 */
public class UpdateFieldManipulationParser implements ManipulationParser {

	@Override
	public Manipulation parse(Node manipulationNode) {
		Node originalSchemaNode = manipulationNode.selectSingleNode("@originalSchema");
		String originalSchemaName = originalSchemaNode != null ? originalSchemaNode.getText().trim() : null;
		
		String originalTableName = manipulationNode.selectSingleNode("@originalTableName").getText().trim();
		String columnToUpdate = manipulationNode.selectSingleNode("@updateColumn").getText().trim();
		
		ValueProvider valueProvider = ValueProviderType.getValueProviderForNode(manipulationNode.selectSingleNode("value"));
		UpdateFieldManipulation manipulation = new UpdateFieldManipulation(originalSchemaName, originalTableName, columnToUpdate);
		
		manipulation.setValueProvider(valueProvider);
		return manipulation;
	}

}
