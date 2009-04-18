package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.Manipulation;
import org.databaseliner.manipulation.RenameTableManipulation;
import org.dom4j.Node;

/**
 * Creates a RenameTableManipulation from the equivalent XML node.
 * 
 * <p>A rename table manipulation is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;manipulation type="renameTable" originalSchema="originalSchema" originalTableName="originalTableName" newTableName="newTableName" /&gt;
 * </code>
 * </p>
 * <p>The <code>originalSchema</code> and <code>originalTableName</code>attributes specify the table
 * that is to be renamed. These correspond to the values that were set up in the extractions or derived
 * during the extraction run (if you are having trouble getting the manipulation to find the table check
 * the log output to see what databaseliner has determined the table is really called). If schema is not
 * being used in your databaseliner configuration the attribute can be omitted</p>
 * 
 * <p>The <code>newTableName</code> attribute specifies the desired name of the table in the output script.
 * It is a required field.</p>
 * 
 * <p>It is not possible to specify an output schema name as databaseliner currently assumes all data is
 * output to a single schema, this is under the user's control to run the output script into the correct
 * schema (which is likely to vary on a user by user basis)</p>
 * 
 */
public class RenameTableManipulationParser implements ManipulationParser {

	@Override
	public Manipulation parse(Node manipulationNode) {
		
		Node originalSchemaNode = manipulationNode.selectSingleNode("@originalSchema");
		String originalSchemaName = originalSchemaNode != null ? originalSchemaNode.getText().trim() : null;
		
		String originalTableName = manipulationNode.selectSingleNode("@originalTableName").getText().trim();
		String newTableName = manipulationNode.selectSingleNode("@newTableName").getText().trim();
		
		return new RenameTableManipulation(originalSchemaName, originalTableName, newTableName);
	}

}
