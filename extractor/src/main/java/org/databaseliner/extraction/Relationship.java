package org.databaseliner.extraction;

import java.util.List;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.Row;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;

public interface Relationship {

	TableName getTableName();
	
	boolean hasSeedTable(Table table);
	
	void addSeedTable(Table seedTable);
	void setTableToFill(Table tableToFill);

	void satisfyForRows(List<Row> dirtyRows, Table dirtyTable, DatabaseConnector databaseConnector);

}
