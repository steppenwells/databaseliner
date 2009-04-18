package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.Manipulation;
import org.databaseliner.manipulation.RemoveColumnManipulation;
import org.dom4j.Node;

/**
 * Creates a RemoveColumnManipulation from the equivalent XML node.
 * 
 * <p>A remove column manipulation is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;manipulation type="removeColumn" originalSchema="originalSchema" originalTableName="originalTableName" removeColumn="columnName" /&gt;
 * </code>
 * </p>
 * <p>The <code>originalSchema</code> and <code>originalTableName</code>attributes specify the table
 * that contains the column to remove. These correspond to the values that were set up in the extractions or derived
 * during the extraction run (if you are having trouble getting the manipulation to find the table check
 * the log output to see what databaseliner has determined the table is really called). If schema is not
 * being used in your databaseliner configuration the attribute can be omitted</p>
 * 
 * <p>The <code>removeColumn</code> attribute specifies the column to remove.
 * It is a required field.</p>
 * 
 */
public class RemoveColumnManipulationParser implements ManipulationParser {

	@Override
	public Manipulation parse(Node manipulationNode) {
		Node originalSchemaNode = manipulationNode.selectSingleNode("@originalSchema");
		String originalSchemaName = originalSchemaNode != null ? originalSchemaNode.getText().trim() : null;
		
		String originalTableName = manipulationNode.selectSingleNode("@originalTableName").getText().trim();
		String columnToRemove = manipulationNode.selectSingleNode("@removeColumn").getText().trim();
		
		return new RemoveColumnManipulation(originalSchemaName, originalTableName, columnToRemove);
	}

}
