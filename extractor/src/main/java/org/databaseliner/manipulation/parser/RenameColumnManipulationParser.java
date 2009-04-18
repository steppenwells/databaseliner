package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.Manipulation;
import org.databaseliner.manipulation.RenameColumnManipulation;
import org.dom4j.Node;

/**
 * Creates a RenameColumnManipulation from the equivalent XML node.
 * 
 * <p>A rename column manipulation is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;manipulation type="renameColumn" originalSchema="originalSchema" originalTableName="originalTableName" renameColumn="originalColumnName" newColumnName="newColumnName" /&gt;
 * </code>
 * </p>
 * <p>The <code>originalSchema</code> and <code>originalTableName</code>attributes specify the table
 * that contains the column to rename. These correspond to the values that were set up in the extractions or derived
 * during the extraction run (if you are having trouble getting the manipulation to find the table check
 * the log output to see what databaseliner has determined the table is really called). If schema is not
 * being used in your databaseliner configuration the attribute can be omitted</p>
 * 
 * <p>The <code>renameColumn</code> attribute specifies the column to rename,
 * it is a required field.</p>
 * 
 * <p>The <code>newColumnName</code> attribute specifies the name to rename the column to,
 * it is a required field.</p>
 * 
 */
public class RenameColumnManipulationParser implements ManipulationParser {

	@Override
	public Manipulation parse(Node manipulationNode) {
		Node originalSchemaNode = manipulationNode.selectSingleNode("@originalSchema");
		String originalSchemaName = originalSchemaNode != null ? originalSchemaNode.getText().trim() : null;
		
		String originalTableName = manipulationNode.selectSingleNode("@originalTableName").getText().trim();
		String columnToRename = manipulationNode.selectSingleNode("@renameColumn").getText().trim();
		String newColumnName = manipulationNode.selectSingleNode("@newColumnName").getText().trim();
		
		return new RenameColumnManipulation(originalSchemaName, originalTableName, columnToRename, newColumnName);
	}

}
