package org.databaseliner.extraction;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;

public abstract class BaseSeedExtraction implements SeedExtraction {

	protected final TableName tableName;
	protected Table seededTable;
	

	public BaseSeedExtraction(TableName tableName) {
		this.tableName = tableName;
	}

	public TableName getTableName() {
		return tableName;
	}

	public void setTableToFill(Table seededTable) {
		this.seededTable = seededTable;
	}

	public void extract(DatabaseConnector databaseConnector) {
		String sql = getExtractionSqlString();
		databaseConnector.processSqlAndAddResultsToTable(sql, seededTable);
	}
	
	protected abstract String getExtractionSqlString();

}