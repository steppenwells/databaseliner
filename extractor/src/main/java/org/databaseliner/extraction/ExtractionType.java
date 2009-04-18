package org.databaseliner.extraction;

import org.databaseliner.extraction.parser.ExtractionParser;
import org.databaseliner.extraction.parser.FullTableExtractionParser;
import org.databaseliner.extraction.parser.MatchingIdsExtractionParser;
import org.databaseliner.extraction.parser.QueryExtractionParser;
import org.dom4j.Node;

public enum ExtractionType {
	
	/** 
	 * Seed extraction that will extract the full contents of a table. 
	 * <p>This is intended to be used to extract static or system configuration data from your
	 * database </p>
	 * 
	 * <p>A full table extraction is defined by an extraction xml node with a <code>type</code>
	 * attribute of <code>all</code>.</p>
	 * 
	 * <p><code>
	 *   &lt;extraction type="all" ... /&gt;
	 * </code></p>
	 * 
	 * @see FullTableExtractionParser
	 */
	all(new FullTableExtractionParser()),
	
	/** 
	 * Seed extraction that will extract rows from a table where the supplied column
	 * matches the supplied data.
	 * 
	 * <p>This is intended to be used when you only want to extract data relating to a
	 * finite set of entities in your domain.</p>
	 *
	 * <p>A matching ids extraction is defined by an extraction xml node with a <code>type</code>
	 * attribute of <code>matchingIds</code>.</p>
	 * 
	 * <p><code>
	 *   &lt;extraction type="matchingIds" ... /&gt;
	 * </code></p>
	 * 
	 * @see MatchingIdsExtractionParser
	 */
	matchingIds(new MatchingIdsExtractionParser()),
	
	/** 
	 * Seed extraction that will extract rows from a table using an arbitrary SQL query
	 * 
	 * <p>This is intended to be used when the data you want can not be simply defined by one 
	 * table and a finite set of ids. This includes when the seed data set is defined by joining
	 * multiple tables or when you want to extract data based on more complex 'where' logic as opposed
	 * to a known data set (for example extracting all the data for the last 7 days).</p>
	 * 
	 * <p>A query extraction is defined by an extraction xml node with a <code>type</code>
	 * attribute of <code>query</code>.</p>
	 * 
	 * <p><code>
	 *   &lt;extraction type="query" ... /&gt;
	 * </code></p>
	 * 
	 * @see QueryExtractionParser
	 */
	query(new QueryExtractionParser());
	
	private final ExtractionParser extractionParser;
	
	private ExtractionType(ExtractionParser extractionParser) {
		this.extractionParser = extractionParser;
	}

	private SeedExtraction parseFromNode(Node extractionNode) {
		return extractionParser.parse(extractionNode);
	}
	
	public static SeedExtraction getExtractionForNode(Node extractionNode) {
		
		String type = extractionNode.selectSingleNode("@type").getText().trim();
		ExtractionType extractionType = ExtractionType.valueOf(type);
		
		return extractionType.parseFromNode(extractionNode); 
	}
	
}
