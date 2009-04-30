package org.databaseliner.extraction.parser;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.IgnoredRelationship;
import org.databaseliner.extraction.RefersToRelationship;
import org.databaseliner.extraction.Relationship;
import org.dom4j.Node;

/**
 * Creates a IgnoredRelationship from the equivalent XML node.
 * 
 * <p>An ignored relationship is defined with the following XML:</p>
 * 
 * <p>
 * <code>
 * 	&lt;relationship seedSchema="seed_schema_name" seedTable="seed_table_name" seedColumn="seed_column" type="ignored" toSchema="schema_name" toTable="table_name" toColumn="column" /&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>seed</code> attributes define the table that refers to the table defined by the 
 * <code>to</code> table attributes.
 * 
 * <p>The <code>schema</code> attributes specify the schema the table a is found in.
 * This is an optional attribute and allows cross schema relationships to be ignored.</p>
 * 
 * <p>The <code>table</code> attributes specify the tables on either side of the relationship. These
 * are required attributes.</p>
 * 
 * <p>The <code>column</code> attributes specify which columns on the <code>table</code>s are
 * involved in this relationship. This is a required attribute.</p>
 */
public class IgnoredRelationshipParser implements RelationshipParser{

	private final static Logger LOG = Logger.getLogger(IgnoredRelationshipParser.class);
	
	@Override
	public Relationship parse(Node relationshipNode) {		

		Node seedSchemaNode = relationshipNode.selectSingleNode("@seedSchema");
		String seedSchemaName = seedSchemaNode != null ? seedSchemaNode.getText().trim() : null;
		String seedTableName = relationshipNode.selectSingleNode("@seedTable").getText().trim();
		String seedColumn = relationshipNode.selectSingleNode("@seedColumn").getText().trim();

		Node schemaNode = relationshipNode.selectSingleNode("@toSchema");
		String toSchemaName = schemaNode != null ? schemaNode.getText().trim() : null;
		String toTableName = relationshipNode.selectSingleNode("@toTable").getText().trim();
		String toColumn = relationshipNode.selectSingleNode("@toColumn").getText().trim();
		
		LOG.info("creating ignored relationship " + seedTableName + " -> " + toTableName);
		
		return new IgnoredRelationship(seedSchemaName, seedTableName, seedColumn,
										toSchemaName, toTableName, toColumn);
	}

}
