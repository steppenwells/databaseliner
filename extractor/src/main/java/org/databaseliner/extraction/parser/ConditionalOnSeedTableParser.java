package org.databaseliner.extraction.parser;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.ConditionalOnSeedTableRelationship;
import org.databaseliner.extraction.Converter;
import org.databaseliner.extraction.ConverterType;
import org.databaseliner.extraction.Relationship;
import org.dom4j.Node;

/**
 * Creates a ConditionalOnSeedTable relationship from the equivalent XML node.
 * 
 * <p>A conditional on seed table to relationship is defined with the following XML:</p>
 * 
 * <p>
 * <code>
 * 	&lt;relationship schema="schema" table="table" column="column" type="conditionalOnSeedTable" 
 *			seedTable="seed table" seedColumn="seed column" convertTo="data type" &gt;
 *    &lt;condition whenColumn="column in seed table" equals="value" /&gt;
 *  &lt;/relationship&gt;
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
 * <p>The <code>seedTable</code> attribute specifies the table that contains data which is used to
 * extract data from the <code>table</code>. This is a required attribute</p>
 * 
 * <p>The <code>seedColumn</code> attribute specifies which column on the <code>seedTable</code> contains data that
 * is used to extract data from the <code>table</code>. This is a required attribute</p>
 * 
 * <p>The <code>convertTo</code> attribute specifies an optional data transformation to be applied to the data
 * in the <code>seedColumn</code>. This is used when the data type of the <code>seedColumn</code> does not match
 * the data type of the <code>column</code>. Supported values of this attribute are <code>integer</code>
 * and <code>string</code> which will output the <code>seedColumn</code> data as a numeric or string
 * value. This is a optional attribute</p>
 * 
 * <p>The <code>condition</code> element defines the criteria which will be applied to data in the
 * <code>seedTable</code> to determine if the data should be used to extract data. This is a required element.</p>
 * 
 * <p>The <code>whenColumn</code> attribute specifies the column in the seed table that is checked by this
 * condition, it is a required attribute.</p>
 * 
 * <p>The <code>equals</code> attribute defines what the value of the <code>whenColumn</code> should be
 * of this relationship to extract data from the <code>table</code>.</p>
 * 
 */
public class ConditionalOnSeedTableParser implements RelationshipParser{

	private final static Logger LOG = Logger.getLogger(ConditionalOnSeedTableParser.class);
	
	@Override
	public Relationship parse(Node relationshipNode) {
		Node schemaNode = relationshipNode.selectSingleNode("@schema");
		String schemaName = schemaNode != null ? schemaNode.getText().trim() : null;
		String tableName = relationshipNode.selectSingleNode("@table").getText().trim();
		String columnName = relationshipNode.selectSingleNode("@column").getText().trim();
		String seedTable = relationshipNode.selectSingleNode("@seedTable").getText().trim();
		String seedColumn = relationshipNode.selectSingleNode("@seedColumn").getText().trim();
		
		LOG.info("creating conditional on seed table extraction " + tableName);
		ConditionalOnSeedTableRelationship relationship = new ConditionalOnSeedTableRelationship(schemaName, tableName, columnName, seedTable, seedColumn);

		Node convertToNode = relationshipNode.selectSingleNode("@convertTo");
		if (convertToNode != null) {
			Converter converter = ConverterType.getConverterForNode(convertToNode);
			relationship.setValueConverter(converter);
		}
		
		// might want to make this a list later
		Node conditionNode = relationshipNode.selectSingleNode("condition");
		
		String whenColumnName = conditionNode.selectSingleNode("@whenColumn").getText().trim();
		String valueToEqual = conditionNode.selectSingleNode("@equals").getText().trim();
		
		relationship.addCondition(whenColumnName, valueToEqual);

		return relationship;
	}

}
