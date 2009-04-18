package org.databaseliner.extraction.parser;

import java.util.List;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.CompositeReferingToMultipleTablesRelationship;
import org.databaseliner.extraction.Relationship;
import org.dom4j.Node;

/**
 * Creates a CompositeReferingToMultipleTables from the equivalent XML node.
 * 
 * <p>A composite refers to relationship is defined with the following XML:</p>
 * 
 * <p>
 * <code>
 * 	&lt;relationship table="table" type="compositeReferingToMultipleTables"&gt;<br/>
 *    &lt;relate column="column" seedTable="seed_table_name" seed_column="seedColumn"/&gt;<br/>
 *    &lt;relate column="column" seedTable="seed_table_name" seed_column="seedColumn"/&gt;<br/>
 *  &lt;/relationship&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>table</code> attribute specifies which table to extract data from. This
 * is a required attribute.</p>
 * 
 * <p>The <code>relate</code> element defines the relationship between the table being extracted and another
 * table. By using multiple of these elements a multiple relationships can be defined that need to be satisfied
 * when extracting data. If just one relate element is defined the relationship produced is that same as the
 * RefersTo relationship type.</p>
 * 
 * <p>At least one <code>relate</code> element must be defined.
 * 
 * <p>The <code>column</code> attribute specifies which column on the <code>table</code> contains the
 * values that are matched with values in the seed table. This is a required attribute</p> 
 * 
 * <p>The <code>seed_table</code> attribute specifies which table contains data which is related to the the table
 * which data is being extracted from.</p>
 * 
 * <p>The <code>seed_column</code> attribute specifies which column on the <code>seed_table</code> contains data that
 * is used to extract data from the <code>table</code>.</p>
 * 
 * <h4>Example</h4>
 * <p>The following relationship definition:</p>
 * <p>
 * <code>
 * 	&lt;relationship table="AUDIT_EVENT" type="compositeReferingToMultipleTables"&gt;<br/>
 *    &lt;relate column="AUDIT_TYPE" seedTable="AUDIT_TYPE" seedColumn="NAME"/&gt;<br/>
 *    &lt;relate  column="OBJECT_ID" seedTable="OBJECT" seedColumn="ID"/&gt;<br/>
 *  &lt;/relationship&gt;
 * </code>
 * </p>
 * <p>will extract data for the audit event table when it matches extracted audit types and extracted objects.
 * This equivalent SQL query for this would be:</p>
 * <p>
 * <code>
 * 	select *<br/>
 *  from AUDIT_EVENT<br/>
 *  where <br/>
 *    AUDIT_TYPE IN (<em>previously extracted values for AUDIT_TYPE.NAME</em>)<br/>
 *    AND OBJECT_ID IN (<em>previously extracted values for OBJECT.ID</em>)<br/>
 *  
 * </code>
 * </p>
 * 
 */
public class CompositeReferingToMultipleTablesParser implements RelationshipParser{


	private final static Logger LOG = Logger.getLogger(CompositeReferingToMultipleTablesParser.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public Relationship parse(Node relationshipNode) {
		String tableName = relationshipNode.selectSingleNode("@table").getText().trim();
		
		LOG.info("creating composite to extract " + tableName);
		CompositeReferingToMultipleTablesRelationship relationship = new CompositeReferingToMultipleTablesRelationship(tableName);

		List<Node> relateNodes = relationshipNode.selectNodes("relate");
		for (Node relateNode : relateNodes) {
			
			String column = relateNode.selectSingleNode("@column").getText().trim();
			String seedTableName = relateNode.selectSingleNode("@seedTable").getText().trim();
			String seedColumn = relateNode.selectSingleNode("@seedColumn").getText().trim();
			
			LOG.info(String.format("\tadding relationship to composite %s.%s ->%s.%s",
					seedTableName, seedColumn, tableName, column));
			
			relationship.addTableRelationship(column, seedTableName, seedColumn);
		}
		

		return relationship;
	}

}
