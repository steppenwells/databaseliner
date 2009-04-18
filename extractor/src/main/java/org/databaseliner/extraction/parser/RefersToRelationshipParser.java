package org.databaseliner.extraction.parser;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.RefersToRelationship;
import org.databaseliner.extraction.Relationship;
import org.dom4j.Node;

/**
 * Creates a RefersToRelationship from the equivalent XML node.
 * 
 * <p>A refers to relationship is defined with the following XML:</p>
 * 
 * <p>
 * <code>
 * 	&lt;relationship schema="schema_name" table="table_name" column="column" type="refersTo" seed_table="seed_table_name" seed_column="seed_column"/&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>schema</code> attribute specifies the schema the table to extract data from is found in.
 * This is an optional attribute and will default to the same schema the seed table is in, if this is 
 * specified it allows cross schema relationships to be defined.</p>
 * 
 * <p>The <code>table</code> attribute specifies which table to extract data from. This
 * is a required attribute.</p>
 * 
 * <p>The <code>column</code> attribute specifies which column on the <code>table</code> contains the
 * values that are matched with values in the seed table. This is a required attribute</p> 
 * 
 * <p>The <code>seed_table</code> attribute specifies which table contains data which is related to the the table
 * which data is being extracted from.</p>
 * 
 * <p>The <code>seed_column</code> attribute specifies which column on the <code>seed_table</code> contains data that
 * is used to extract data from the <code>table</code>.</p>
 */
public class RefersToRelationshipParser implements RelationshipParser{

	private final static Logger LOG = Logger.getLogger(RefersToRelationshipParser.class);
	
	@Override
	public Relationship parse(Node relationshipNode) {
		
		Node schemaNode = relationshipNode.selectSingleNode("@schema");
		String schemaName = schemaNode != null ? schemaNode.getText().trim() : null;
		String tableName = relationshipNode.selectSingleNode("@table").getText().trim();
		String column = relationshipNode.selectSingleNode("@column").getText().trim();

		String seedTableName = relationshipNode.selectSingleNode("@seedTable").getText().trim();
		String seedColumn = relationshipNode.selectSingleNode("@seedColumn").getText().trim();

		LOG.info("creating refering relationship " + seedTableName + " -> " + tableName);
		return new RefersToRelationship(schemaName, tableName, column, seedTableName, seedColumn);
	}

}
