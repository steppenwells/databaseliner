package org.databaseliner.extraction.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.database.DatabaseUtils;
import org.databaseliner.extraction.IgnoredRelationship;
import org.databaseliner.extraction.Relationship;
import org.databaseliner.extraction.SeedExtraction;
import org.databaseliner.output.SqlStringOutputter;

public class ExtractionModel {

	private final DatabaseConnector databaseConnector;
	
	private final List<SeedExtraction> seeds;
	private final List<TableName> ignoredTableNames;
	private final List<Relationship> relationships;
	
	private final Map<TableName, Table> tables;
	private final boolean dryRunMode;
	
	private final static Logger LOG = Logger.getLogger(ExtractionModel.class);


	public ExtractionModel(DatabaseConnector databaseConnector, List<SeedExtraction> seeds, List<TableName> ignoredTableNames, List<Relationship> relationships, boolean dryRunMode) {
		this.databaseConnector = databaseConnector;
		
		this.seeds = seeds;
		this.ignoredTableNames = ignoredTableNames;
		this.relationships = relationships;
		this.dryRunMode = dryRunMode;
		
		this.tables = new HashMap<TableName, Table>();
	}

	public void build() {

		for (SeedExtraction seedExtraction : seeds) {
			Table seededTable = addTable(seedExtraction.getTableName());
			seedExtraction.setTableToFill(seededTable);
		}

        // all relationships should now be correctly bound in the model
        for (Relationship relationship : relationships) {
            relationship.verify();                
        }
	}

	private Table addTable(TableName tableName) {
		
		if (tables.get(tableName) != null) {
			LOG.debug("Table " + tableName + " already in model returning");
			return tables.get(tableName);
		}
		
		Table table = new Table(tableName);
		tables.put(table.getName(), table);
		
		DatabaseMetaData databaseMetaData = databaseConnector.getDatabaseMetaData();
		populateTableColumnData(databaseMetaData, table);
		markPrimaryKeyColumns(databaseMetaData, table);
		
		addReferencedTablesAndRelationships(databaseMetaData, table);
		addUserDefinedRelationshipsForTable(table);
		
		LOG.debug("Added table to model:\n" + table);
		
		return table;
	}
	
	private void populateTableColumnData(DatabaseMetaData databaseMetaData, Table table) {
		ResultSet columnSet = null;
		try {
			columnSet = databaseMetaData.getColumns(null, table.getName().getSchemaName(), table.getName().getTableName(), null);

            boolean tableExists = false;

	        while (columnSet.next()) {
                tableExists = true;
	            int nullability = columnSet.getInt("NULLABLE");
	            table.addColumn(new Column(columnSet.getString("COLUMN_NAME"), (nullability == DatabaseMetaData.columnNullable)));
	        }

            if (!tableExists) {
                throw new RuntimeException("Unable to get column data for " + table);
            }
	        
		} catch (SQLException e) {
			
			LOG.error("error getting column data for " + table, e);
			throw new RuntimeException("error getting column data for " + table, e);
			
		} finally {
			DatabaseUtils.close(columnSet);
		}
    }

	private void markPrimaryKeyColumns(DatabaseMetaData databaseMetaData, Table table) {
		ResultSet pkColumnSet = null;
		try {
			pkColumnSet = databaseMetaData.getPrimaryKeys(null, table.getName().getSchemaName(), table.getName().getTableName());
			
			while (pkColumnSet.next()) {
				table.markColumnAsPrimaryKey(pkColumnSet.getString("COLUMN_NAME"));
			}
			
		} catch (SQLException e) {
			
			LOG.error("error getting column data for " + table, e);
			throw new RuntimeException("error getting column data for " + table, e);
			
		} finally {
			DatabaseUtils.close(pkColumnSet);
		}
		
	}
	
	private void addReferencedTablesAndRelationships(DatabaseMetaData databaseMetaData, Table table) {
		ResultSet fkColumnSet = null;
		try {
			fkColumnSet = databaseMetaData.getImportedKeys(null, table.getName().getSchemaName(), table.getName().getTableName());
			
			while (fkColumnSet.next()) {
				
				String dependedOnTableName = fkColumnSet.getString("PKTABLE_NAME");
				String dependedOnTableSchema = fkColumnSet.getString("PKTABLE_SCHEM");
				String dependedOnTableColumn = fkColumnSet.getString("PKCOLUMN_NAME");
				String localTableSchema = fkColumnSet.getString("FKTABLE_SCHEM");
				String localTableColumnName = fkColumnSet.getString("FKCOLUMN_NAME");
				
				String dependedOnSchemaName = tablesInSameSchema(dependedOnTableSchema, localTableSchema) ? table.getName().getSchemaName() : dependedOnTableSchema;
				
				TableName dependedOnTableLocator = new TableName(dependedOnTableName, dependedOnSchemaName);
				table.addOutputDependency(dependedOnTableLocator, localTableColumnName);
				
				if (isIgnoredTableOrRelationship(table.getName(), localTableColumnName, dependedOnTableLocator, dependedOnTableColumn)) {
					LOG.debug("found reference to ignored table, skipping");
					continue;
				}
				
				Table dependedOnTable = addTable(dependedOnTableLocator);
				
				table.addForeignKeyDependency(dependedOnTable, dependedOnTableColumn, localTableColumnName);
			}
			
		} catch (SQLException e) {
			
			LOG.error("error getting foreign key data for " + table, e);
			throw new RuntimeException("error getting foreign key data for " + table, e);
			
		} finally {
			DatabaseUtils.close(fkColumnSet);
		}
	}

	private boolean isIgnoredTableOrRelationship(TableName fromTableName, String fromColumn, TableName toTableName, String toColumn) {
		if (ignoredTableNames.contains(toTableName)) {
			LOG.debug("found reference to ignored table, skipping");
			return true;
		}
		
		for (Relationship relationship : relationships) {
			if (relationship instanceof IgnoredRelationship) {
				IgnoredRelationship ignoredRelationship = (IgnoredRelationship) relationship;
				
				if (ignoredRelationship.shouldIgnoreRelationship(fromTableName, fromColumn, toTableName, toColumn)) {
					LOG.debug("found reference to ignored relationship, skipping");
					return true;
				}
			}
		}
		return false;
	}
	
	private void addUserDefinedRelationshipsForTable(Table table) {
		List<Relationship> userRelationshipsForTable = new ArrayList<Relationship>();
		for (Relationship relationship : relationships) {
			if (relationship.hasSeedTable(table)) {
				userRelationshipsForTable.add(relationship);
			}
		}
		
		for (Relationship relationship : userRelationshipsForTable) {

			TableName tableNameToFill = relationship.getTableName();
			if (tableNameToFill.getSchemaName() == null) {
				tableNameToFill.setSchemaName(table.getName().getSchemaName());
			}
			Table tableToFill = addTable(tableNameToFill);
			relationship.setTableToFill(tableToFill);

			table.addSeededRelationship(relationship);
			
		}
	}
	
	private boolean tablesInSameSchema(String dependedOnTableSchema, String localTableSchema) {
		if(dependedOnTableSchema != null) {
			return dependedOnTableSchema.equals(localTableSchema);
		}
		return localTableSchema == null;
	}

	public void extract() {
		if (dryRunMode) {
			LOG.debug("dryRun is set to true, not extracting data. Please review table and relationships output above.");
			return;
		}
		
		for (SeedExtraction seedExtraction : seeds) {
			seedExtraction.extract(databaseConnector);
		}
		
		while(tablesHaveUnsatisfiedRelationships()) {
			extractUnsatisfiedRelationshipData();
		}
	}
	
	private void extractUnsatisfiedRelationshipData() {
		for (Table table : getDirtyTables()) {
			table.satisfyRelationships(databaseConnector);
		}
	}

	private boolean tablesHaveUnsatisfiedRelationships() {
		
		for (Table table : tables.values()) {
			if (table.isDirty()) {
				return true;
			}
		}
		return false;
	}

	private List<Table> getDirtyTables() {
		ArrayList<Table> dirtyTables = new ArrayList<Table>();
		for (Table table : tables.values()) {
			if (table.isDirty()) {
				dirtyTables.add(table);
			}
		}
		return dirtyTables;
	}
	
	public List<Table> getUnoutputTables() {
		ArrayList<Table> unoutputTables = new ArrayList<Table>();
		for (Table table : tables.values()) {
			if (!table.isOutput()) {
				unoutputTables.add(table);
			}
		}
		return unoutputTables;
	}

	public List<Table> getAllTables() {
		return new ArrayList<Table>(tables.values());
	}

	public Table getTableWithName(TableName tableName) {
		return tables.get(tableName);
	}
	
	public void registerTableWithNewName(Table table, TableName originalTableName, TableName newTableName) {
		tables.remove(originalTableName);
		tables.put(newTableName, table);
	}

	public SqlStringOutputter getSqlStringOutputter() {
		return SqlStringOutputter.instance(databaseConnector);
	}

}
