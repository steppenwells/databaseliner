package org.databaseliner.extraction;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;

public interface SeedExtraction {
	
	TableName getTableName();
	void setTableToFill(Table seededTable);
	void extract(DatabaseConnector databaseConnector);

}
