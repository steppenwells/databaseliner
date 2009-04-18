package org.databaseliner.extraction.parser;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.RowsMatchingIdsExtraction;
import org.databaseliner.extraction.SeedExtraction;
import org.dom4j.Node;

/**
 * Creates a MatchingIdsExtraction from the equivalent XML node.
 * 
 * <p>A matching ids extraction is defined with the following XML:</p>
 * 
 * <p>
 * <code>
 * 	&lt;extraction table="table_name" schema="schema_name" type="matchingIds" &gt;<br/>
 *    &lt;column&gt;column_name&lt;/column&gt;<br/>
 *    &lt;ids&gt;ids&lt;/ids&gt;<br/>
 *  &lt;/extraction&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>table</code> attribute specifies which table to extract data from. This
 * is a required attribute.</p>
 * 
 * <p>The <code>schema</code> attribute specifies which schema the table is in. This is an
 * optional attribute, leaving this blank will assume the table is in the default connection
 * schema. This attribute allows data to be extracted from more than one schema in the database
 * and to disambiguate tables when tables with the same name exist in multiple visible schemas.</p>
 * 
 * <p>The <code>column</code> element specifies which column on the <code>table</code> contains the
 * values specified in the <code>ids</code> element.</p>
 * 
 * <p>The <code>ids</code> element contains a comma separated list of ids that represent the
 * data to be extracted. The contents of this element are used to generate an <code>IN</code>
 * clause in a sql query so each element in the comma separated list should be understood by the
 * database (I.E strings should be surrounded by single quotes ('), dates should be parsable by
 * the database).</p>
 * 
 * <h4>Example:</h4>
 * <p>The following xml:</p>
 * <p>
 * <code>
 * 	&lt;extraction table="foo" type="matchingIds" &gt;<br/>
 *    &lt;column&gt;name&lt;/column&gt;<br/>
 *    &lt;ids&gt;'Jim', 'Bob', 'Mary'&lt;/ids&gt;<br/>
 *  &lt;/extraction&gt;
 * </code>
 * </p>
 * <p>Will result in the following query being used to extract data from the database:</p>
 * <code>
 * SELECT *<br/>
 * FROM foo<br/>
 * WHERE name IN ('Jim', 'Bob', 'Mary');
 * </code>
 */
public class MatchingIdsExtractionParser implements ExtractionParser {

	private final static Logger LOG = Logger.getLogger(MatchingIdsExtractionParser.class);
	
	@Override
	public SeedExtraction parse(Node extractionNode) {
		
		Node schemaNode = extractionNode.selectSingleNode("@schema");
		String schemaName = schemaNode != null ? schemaNode.getText().trim() : null;
		String tableName = extractionNode.selectSingleNode("@table").getText().trim();
		String column = extractionNode.selectSingleNode("column").getText().trim();
		String ids = extractionNode.selectSingleNode("ids").getText().trim();
		
		LOG.info("creating matching ids extraction for " + tableName);
		return new RowsMatchingIdsExtraction(schemaName, tableName, column, ids);
	}

}
