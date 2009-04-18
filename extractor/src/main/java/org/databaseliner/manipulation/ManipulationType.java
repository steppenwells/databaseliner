package org.databaseliner.manipulation;

import org.databaseliner.manipulation.parser.AddColumnManipulationParser;
import org.databaseliner.manipulation.parser.ManipulationParser;
import org.databaseliner.manipulation.parser.RemoveColumnManipulationParser;
import org.databaseliner.manipulation.parser.RenameColumnManipulationParser;
import org.databaseliner.manipulation.parser.RenameTableManipulationParser;
import org.databaseliner.manipulation.parser.UpdateFieldManipulationParser;
import org.dom4j.Node;

public enum ManipulationType {
	
	/**
	 * Manipulation that will rename an extracted table prior to outputting.
	 * 
	 * <p>This manipulation is used when a table in the target schema has a different name to table
	 * extracted from. This situation arises when data is extracted from more than one schema and 
	 * inserted into a single consolidated schema. In this case the tables in different schemas can
	 * have the same name and the table in one schema be referenced by a synonym. Using this
	 * manipulation the table can be renamed to have the same name as the synonym and be used 
	 * transparently in the target schema.</p>
	 * 
	 * @see RenameTableManipulationParser
	 */
	renameTable(new RenameTableManipulationParser()),
	
	/**
	 * Manipulation that will remove a column on an extracted table prior to outputting.
	 * 
	 * <p>This is used if the column is not present in the target schema or if you want to
	 * leave it blank (or let the database default the values for you)</p>
	 * 
	 * @see RemoveColumnManipulationParser
	 */
	removeColumn(new RemoveColumnManipulationParser()),
	
	/**
	 * Manipulation that will add a new column on an extracted table prior to outputting.
	 * 
	 * <p>This is used if you need to add a new column to a table, this can occur if the target 
	 * schema is different from the source schema (this can occur if work development work has been
	 * done on the target schema which has not been applied to the source schema yet)</p>
	 * 
	 * @see AddColumnManipulationParser
	 */
	addColumn(new AddColumnManipulationParser()),
	
	/**
	 * Manipulation that will rename a column on an extracted table prior to outputting.
	 * 
	 * <p>This is used if you need to add a new column to a table, this can occur if the target 
	 * schema is different from the source schema (this can occur if work development work has been
	 * done on the target schema which has not been applied to the source schema yet)</p>
	 * 
	 * @see RenameTableManipulationParser
	 */
	renameColumn(new RenameColumnManipulationParser()),
	
	/**
	 * Manipulation that will update the value of fields in a given column in a table prior to outputting.
	 * 
	 * <p>This is used when data in the source schema is not appropriate for use in the target schema. For example
	 * you might be extracting from a live database with real user's data in it, the update field manipulator can be
	 * used to change their passwords to a known value in the target schema (so you can log in as them) and to obfuscate
	 * their credit card details.</p>
	 * 
	 * <p>Fields can be set to explicitly defined values, values from other columns on the table, arbitrary
	 * sql statements or output of a given FieldManipulator class.</p>
	 * 
	 * @see UpdateFieldManipulationParser
	 */
	updateField(new UpdateFieldManipulationParser());
	
	private final ManipulationParser manipulationParser;
	
	private ManipulationType(ManipulationParser manipulationParser) {
		this.manipulationParser = manipulationParser;
	}

	private Manipulation parseFromNode(Node manipulationNode) {
		return manipulationParser.parse(manipulationNode);
	}

	public static Manipulation getManipulationForNode(Node manipulationNode) {
		String type = manipulationNode.selectSingleNode("@type").getText().trim();
		ManipulationType manipulationType = ManipulationType.valueOf(type);
		
		return manipulationType.parseFromNode(manipulationNode); 
	}


}
