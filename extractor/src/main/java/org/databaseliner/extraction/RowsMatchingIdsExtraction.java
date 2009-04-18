package org.databaseliner.extraction;

import org.databaseliner.extraction.model.TableName;

public class RowsMatchingIdsExtraction extends BaseSeedExtraction implements SeedExtraction {

	private final String column;
	private final String ids;
	
	public RowsMatchingIdsExtraction(String schemaName, String tableName, String column, String ids) {
		
		super(new TableName(tableName, schemaName));
		
		this.column = column;
		this.ids = ids;
	}

	@Override
	protected String getExtractionSqlString() {
		return String.format("SELECT * FROM %s WHERE %s IN (%s)", tableName, column, ids);
	}
	
}
