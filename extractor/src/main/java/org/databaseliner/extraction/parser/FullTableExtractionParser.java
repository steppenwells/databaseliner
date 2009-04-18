package org.databaseliner.extraction.parser;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.FullTableExtraction;
import org.databaseliner.extraction.SeedExtraction;
import org.dom4j.Node;

/**
 * Creates a FullTableExtraction from the equivalent XML node.
 * 
 * <p>A full table extraction is defined with the following XML:</p>
 * <p>
 * <code>
 * 	&lt;extraction table="table_name" schema="schema_name" type="all" /&gt;
 * </code>
 * </p>
 * <p>The <code>table</code> attribute specifies which table to extract data from. This
 * is a required attribute.</p>
 * 
 * <p>The <code>schema</code> attribute specifies which schema the table is in. This is an
 * optional attribute, leaving this blank will assume the table is in the default connection
 * schema. This attribute allows data to be extracted from more than one schema in the database
 * and to disambiguate tables when tables with the same name exist in multiple visible schemas.</p>
 * 
 */
public class FullTableExtractionParser implements ExtractionParser {

	private final static Logger LOG = Logger.getLogger(FullTableExtractionParser.class);
	
	@Override
	public SeedExtraction parse(Node fullTableExtractionNode) {
		Node schemaNode = fullTableExtractionNode.selectSingleNode("@schema");
		String schemaName = schemaNode != null ? schemaNode.getText().trim() : null;
		String tableName = fullTableExtractionNode.selectSingleNode("@table").getText().trim();
		
		LOG.info("creating full table extraction for " + tableName);
		return new FullTableExtraction(schemaName, tableName);
	}

}
