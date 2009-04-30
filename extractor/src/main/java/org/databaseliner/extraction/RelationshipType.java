package org.databaseliner.extraction;

import org.databaseliner.extraction.parser.CompositeReferingToMultipleTablesParser;
import org.databaseliner.extraction.parser.ConditionalOnSeedTableParser;
import org.databaseliner.extraction.parser.RefersToRelationshipParser;
import org.databaseliner.extraction.parser.RelationshipParser;
import org.dom4j.Node;

public enum RelationshipType {

	/**
	 * Relationship from a single table to another table. This acts like the default foreign key
	 * extraction that gets data from a table that is referred to by the seed table except that
	 * no foreign need exist in the database.
	 * 
	 * <p>This can be used to model the inverse of an existing
	 * foreign key, extracting all the data in a table that refers to the seed table, or where no
	 * foreign key relationship is defined (for example where audit data about an object outlives
	 * the object itself so no foreign key can be defined).</p>
	 * 
	 * <p>A refers to relationship is defined by an relationship xml node with a <code>type</code>
	 * attribute of <code>refersTo</code>.</p>
	 * 
	 * <p><code>
	 *   &lt;relationship type="refersTo" ... /&gt;
	 * </code></p>
	 * 
	 * @see RefersToRelationshipParser
	 */
	refersTo(new RefersToRelationshipParser()),
	
	/**
	 * Relationship where the data in the target table is defined by 2 or more tables. This allows
	 * data to be restricted so that it is only extracted when it refers to data both in the defined tables.
	 * 
	 * <p>By using this relationship you can manage the join tables between different areas of your 
	 * database extraction without them blowing up and pulling in undesired data.
	 * 
	 * <p>For example in an audit system that logs different events against objects this extraction can
	 * retrieve all audit records for extracted objects with the extracted audit types. Without using this
	 * relationship type extracting data for the join could retrieve every audit of the desired type and 
	 * cause every object in the system to be extracted. This could then in turn pull in every audit type
	 * in the system resulting in the entire database being extracted.</p> 
	 * 
	 * <p>A compositeReferingToMultipleTables relationship is defined by an relationship xml node with a <code>type</code>
	 * attribute of <code>compositeReferingToMultipleTables</code>.</p>
	 * 
	 * <p><code>
	 *   &lt;relationship type="compositeReferingToMultipleTables" ... /&gt;
	 * </code></p>
	 * 
	 * @see CompositeReferingToMultipleTablesParser
	 */
	compositeReferingToMultipleTables(new CompositeReferingToMultipleTablesParser()),
	
	/**
	 * Relates a single table to another table but only populates data when the data in seed table
	 * matches the provided condition.
	 * 
	 * <p>This is used when a table is used to provide a layer of indirection to another table or
	 * tables. By adding conditionalOnSeedTable relationships with conditions you can define how to follow
	 * the indirection and extract desired data.</p>
	 * 
	 * <p>A conditional on seed table relationship is defined by an relationship xml node with a <code>type</code>
	 * attribute of <code>conditionalOnSeedTable</code>.</p>
	 * 
	 * <p><code>
	 *   &lt;relationship type="conditionalOnSeedTable" ... /&gt;
	 * </code></p>
	 * 
	 * @see ConditionalOnSeedTableParser
	 */
	conditionalOnSeedTable(new ConditionalOnSeedTableParser()),
	
	/**
	 * Defines a relationship between two tables that will be ignored by the data extraction.
	 * 
	 * <p>This can be used when a table is depended on by another table that would normally be extracted
	 * automatically by the extraction run. It allows the individual relationship to be suppressed without
	 * ignoring the entire table using an <code>ignore</code> definition on the table, this allows the
	 * table to be populated using data in other areas of the schema.</p>
	 * 
	 * <p>This relationship is usually used in conjunction with the <code>removeColumn</code> or 
	 * <code>updateField</code> manipulations to ensure that the ignored relationship is satisfied
	 * in the output data set. This relationship is also useful if your overall data extraction is performed
	 * by making a number of smaller extractions with different configurations.</p>
	 * 
	 * <p>An ignored relationship is defined by an relationship xml node with a <code>type</code>
	 * attribute of <code>ignored</code>.</p>
	 * 
	 * <p><code>
	 *   &lt;relationship type="ignored" ... /&gt;
	 * </code></p>
	 * 
	 * @see IgnoredRelationshipParser
	 */
	ignoredRelationship(new IgnoredRelationshipParser());

	private final RelationshipParser relationshipParser;
	
	private RelationshipType(RelationshipParser relationshipParser) {
		this.relationshipParser = relationshipParser;
	}

	private Relationship parseFromNode(Node relationshipNode) {
		return relationshipParser.parse(relationshipNode);
	}

	public static Relationship getRelationshipForNode(Node relationshipNode) {
		String type = relationshipNode.selectSingleNode("@type").getText().trim();
		RelationshipType relationshipType = RelationshipType.valueOf(type);
		
		return relationshipType.parseFromNode(relationshipNode); 
	}
	
}
