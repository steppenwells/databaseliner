package org.databaseliner.extraction;

import org.databaseliner.extraction.model.TableName;

public class QueryExtraction extends BaseSeedExtraction implements SeedExtraction {

	private final String alias;
	private final String fromClause;
	private final String whereClause;

	public QueryExtraction(String schemaName, String tableName, String alias, String fromClause, String whereClause) {

		super(new TableName(tableName, schemaName));
		
		this.alias = alias;
		this.fromClause = fromClause;
		this.whereClause = whereClause;
	}

	@Override
	protected String getExtractionSqlString() {
		if (alias != null && fromClause != null) {
			return String.format("SELECT %s.* FROM %s WHERE %s", alias, fromClause, whereClause);
		} else {
			return String.format("SELECT * FROM %s WHERE %s", tableName, whereClause);
		}
	}
	
}
