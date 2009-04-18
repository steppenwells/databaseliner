package org.databaseliner.extraction.parser;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.QueryExtraction;
import org.databaseliner.extraction.SeedExtraction;
import org.dom4j.Node;

/**
 * Creates a QueryExtraction from the equivalent XML node.
 * 
 * <p>A query extraction is defined with the following XML:</p>
 * 
 * <p>
 * <code>
 * 	&lt;extraction table="table_name" schema="schema_name" type="query" &gt;<br/>
 *    &lt;selectAlias&gt;alias&lt;/selectAlias&gt;<br/>
 *    &lt;fromClause&gt;from_clause&lt;/fromClause&gt;<br/>
 *    &lt;whereClause&gt;where_clause&lt;/whereClause&gt;<br/>
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
 * <p>The <code>selectAlias</code> element specifies which of the tables defined in the <code>
 * fromClause</code> corresponds to the table to extract data from as specified in the <code>table</code>
 * attribute. This element is an optional element, although it is required if a <code>fromClause</code>
 * is used</p>
 * 
 * <p>The <code>fromClause</code> element specifies the tables used in the extraction query
 * and their aliases. The contents of this element are used to generate a FROM clause in a query and 
 * should be understood by the database. This element is optional, if it is missing the extraction query
 * will operate on only the table defined by the <code>table</code> attribute.</p>
 * 
 * <p>The <code>whereClause</code> element specifies the selection criteria to apply to obtain the data to
 * extract. If a <code>fromClause</code> is specified the criteria should refer to the aliases defined there, if
 * no <code>fromClause</code> is defined the criteria refers to the columns of the table specified in the <code>table</code>
 * attribute directly. The contents of this element are used to generate a WHERE clause in a query and 
 * should be understood by the database. The <code>whereClause</code> element is a required element</p>
 * 
 * <h4>Examples:</h4>
 * <p>Get all employees with names beginning with 'A'</p>
 * <p>
 * <code>
 * 	&lt;extraction table="employee" type="query" &gt;<br/>
 *    &lt;whereClause&gt;name LIKE 'A%'&lt;/whereClause&gt;<br/>
 *  &lt;/extraction&gt;
 * </code>
 * </p>
 * <p>Will result in the following query being used to extract data from the database:</p>
 * <code>
 * SELECT *<br/>
 * FROM employee<br/>
 * WHERE name LIKE 'A%';
 * </code>
 * </p>
 * <p>Get all employees in the HR department'</p>
 * <p>
 * <code>
 * 	&lt;extraction table="employee" type="query" &gt;<br/>
 *    &lt;selectAlias&gt;emp&lt;/selectAlias&gt;<br/>
 *    &lt;fromClause&gt;employee emp, department dept&lt;/fromClause&gt;<br/>
 *    &lt;whereClause&gt;emp.department_id = dept.id AND dept.name = 'HR'&lt;/whereClause&gt;<br/>
 *  &lt;/extraction&gt;
 * </code>
 * </p>
 * <p>Will result in the following query being used to extract data from the database:</p>
 * <code>
 * SELECT emp.*<br/>
 * FROM employee emp, department dept<br/>
 * WHERE emp.department_id = dept.id AND dept.name = 'HR';
 * </code>
 * </p>
 */
public class QueryExtractionParser implements ExtractionParser {

	private final static Logger LOG = Logger.getLogger(QueryExtractionParser.class);
	
	@Override
	public SeedExtraction parse(Node extractionNode) {
		
		Node schemaNode = extractionNode.selectSingleNode("@schema");
		String schemaName = schemaNode != null ? schemaNode.getText().trim() : null;
		
		String tableName = extractionNode.selectSingleNode("@table").getText().trim();
		
		Node aliasNode = extractionNode.selectSingleNode("selectAlias");
		String alias = aliasNode != null ? aliasNode.getText().trim() : null;

		Node fromNode = extractionNode.selectSingleNode("fromClause");
		String fromClause = fromNode != null ? fromNode.getText().trim() : null;
		
		String whereClause = extractionNode.selectSingleNode("whereClause").getText().trim();
		
		LOG.info("creating query extraction for " + tableName);
		return new QueryExtraction(schemaName, tableName, alias, fromClause, whereClause);
	}

}
