package org.databaseliner.database;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;

import org.databaseliner.dialect.Dialect;
import org.databaseliner.extraction.model.Table;

public interface DatabaseConnector {
	
    PreparedStatement getPreparedStatement(String sql);
    
    DatabaseMetaData getDatabaseMetaData();
    
    void processSqlAndAddResultsToTable(String sql, Table tableToFill);

	Dialect getDialect();

}
