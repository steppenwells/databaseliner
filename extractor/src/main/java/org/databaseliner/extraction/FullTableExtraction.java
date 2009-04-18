package org.databaseliner.extraction;

import org.databaseliner.extraction.model.TableName;

public class FullTableExtraction extends BaseSeedExtraction implements SeedExtraction {

	public FullTableExtraction(String schemaName, String tableName) {

		super(new TableName(tableName, schemaName));
	}

	@Override
	protected String getExtractionSqlString() {
		return String.format("SELECT * FROM %s", tableName);
	}
	
}
