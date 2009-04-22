package org.databaseliner.extraction.model;

import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.BaseRelationship;
import org.databaseliner.extraction.RefersToRelationship;
import org.databaseliner.extraction.Relationship;
import org.databaseliner.manipulation.ValueProvider;
import org.databaseliner.output.SqlStringOutputter;

public class Table {

	private TableName tableName;
	private final List<Column> columns;
	
	private final Set<Row> rows;
	
	private final List<Relationship> foreignKeyRelationships;
	private final List<Relationship> relationshipsSeededFromThisTable;
	
	// this is the full list of foreign keys used for output. we can't rely on the relationships list
	// as some relationships might be ignored and configured by the user.
	private final Map<Column, TableName> outputDependencies;
	private boolean hasBeenOutput = false;
	private List<Column> nulledColumns;
	private boolean needsUpdate = false;
	
	private final static Logger LOG = Logger.getLogger(Table.class);

	public Table(TableName tableName) {
		this.tableName = tableName;
		this.columns = new ArrayList<Column>();
		this.rows = new HashSet<Row>();
		this.foreignKeyRelationships = new ArrayList<Relationship>();
		this.relationshipsSeededFromThisTable = new ArrayList<Relationship>();
		this.outputDependencies = new HashMap<Column, TableName>();
		this.nulledColumns = new ArrayList<Column>();
	}

	public TableName getName() {
		return tableName;
	}
	
	public void setName(TableName newTableName) {
		tableName = newTableName;
	}

	public boolean isOutput() {
		return hasBeenOutput;
	}
	
	public Map<Column, TableName> getOutputDependencies() {
		return outputDependencies;
	}
	
	public List<Column> getColumns() {
		return columns;
	}

	public List<Relationship> getForeignKeyRelationships() {
		return foreignKeyRelationships;
	}

	public List<Relationship> getRelationshipsSeededFromThisTable() {
		return relationshipsSeededFromThisTable;
	}

	public List<Column> getNulledColumns() {
		return nulledColumns;
	}

	public boolean isNeedsUpdate() {
		return needsUpdate;
	}

	public void addColumn(Column column) {
		columns.add(column);		
	}

	public void markColumnAsPrimaryKey(String primaryKeyColumnName) {	
		for (Column column : columns) {
			if (column.getName().equals(primaryKeyColumnName)) {
				column.markAsPrimaryKey();
				break;
			}
		}
	}
	
	private List<Column> getPKColumns() {
		List<Column> pkColumns = new ArrayList<Column>();
		for (Column column : columns) {
			if (column.isPrimaryKey()) {
				pkColumns.add(column);
			}
		}
		return pkColumns;
	}

	public Column getColumnWithName(String seedColumnName) {
		for (Column column : columns) {
			if (column.getName().equals(seedColumnName)) {
				return column;
			}
		}
		throw new ColumnMissingException("attept to reference non existant column " + seedColumnName + " in table " + this.toString());
	}
	
	
	public void removeColumnWithName(String columnNameToRemove) {
		Column columnWithName = getColumnWithName(columnNameToRemove);
		
		columns.remove(columnWithName);
		outputDependencies.remove(columnWithName);
		// nullable columns is used for output only and this is called at the manipulation phase
		// so we dont do anything here.
	}

	public void renameColumn(String columnToRename, String newColumnName) {
		// column objects are shared so we only need to rename in one place.
		Column columnWithName = getColumnWithName(columnToRename);
		columnWithName.setName(newColumnName);
		
	}
	
	public void updateValuesInColumn(String columnToUpdate,	ValueProvider valueProvider) {
		Column columnWithName = getColumnWithName(columnToUpdate);
		for (Row row : rows) {
			row.addFieldData(columnWithName, valueProvider.getValue(row));
		}
		
	}

	public void addForeignKeyDependency(Table dependedOnTable, String dependedOnTableColumn, String localColumnName) {
	
		BaseRelationship foreignKeyRelationship = new RefersToRelationship(
				dependedOnTable.getName().getSchemaName(), dependedOnTable.getName().getTableName(), dependedOnTableColumn, 
				tableName.getTableName(), localColumnName);
		
		foreignKeyRelationship.addSeedTable(this);
		foreignKeyRelationship.setTableToFill(dependedOnTable);
		
		foreignKeyRelationships.add(foreignKeyRelationship);
	}
	
	public void addSeededRelationship(Relationship relationship) {

		relationship.addSeedTable(this);
		relationshipsSeededFromThisTable.add(relationship);
	}
	
	public void addOutputDependency(TableName dependedOnTableLocator, String localTableColumnName) {
		outputDependencies.put(getColumnWithName(localTableColumnName), dependedOnTableLocator);
	}


	public void processRow(ResultSet resultSet) {
		try {
			Row readRow = new Row();
			for (Column column : columns) {
				Object fieldValue = resultSet.getObject(column.getName());
				readRow.addFieldData(column, fieldValue);
			}
			rows.add(readRow);
		} catch (Exception e) {
			LOG.warn("attempting to add invalid row to table " + tableName + " skipping", e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("table [").append(tableName).append("] with columns:\n");
		
		for (Column column : columns) {
			builder.append("\t").append(column).append("\n");
		}
		
		builder.append("Relationships:\n");
		
		for (Relationship relationship : foreignKeyRelationships) {
			builder.append("\t").append(relationship).append("\n");
		}
		
		for (Relationship relationship : relationshipsSeededFromThisTable) {
			builder.append("\t").append(relationship).append("\n");
		}
		return builder.toString();
	}

	public boolean isDirty() {
		for (Row row : rows) {
			if (row.isDirty()) {
				return true;
			}
		}
		return false;
	}
	
	private List<Row> getDirtyRows() {
		ArrayList<Row> dirtyRows = new ArrayList<Row>();
		
		for (Row row : rows) {
			if (row.isDirty()) {
				dirtyRows.add(row);
			}
		}
		
		return Collections.unmodifiableList(dirtyRows);
	}

	public List<Row> getRows() {
		
		return new ArrayList<Row>(rows);
	}

	public void satisfyRelationships(DatabaseConnector databaseConnector) {
		List<Row> dirtyRows = getDirtyRows();
		
		for (Relationship relationship : foreignKeyRelationships) {
			LOG.debug("satisfiying " + relationship);
			relationship.satisfyForRows(dirtyRows, this, databaseConnector);
		}
		
		for (Relationship relationship : relationshipsSeededFromThisTable) {
			LOG.debug("satisfiying " + relationship);
			relationship.satisfyForRows(dirtyRows, this, databaseConnector);
		}
		
		for (Row row : dirtyRows) {
			row.markClean();
		}
		
	}

	public void writeAsInsert(FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter, String outputDirectory) {
		writeAsInsertWithNullColumns(new ArrayList<Column>(), scriptWriter, sqlStringOutputter, outputDirectory);
	}
	
	public void writeAsInsertWithNullColumns(List<Column> columnsToMakeNull, FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter, String outputDirectory) {
		try {
			String insertHeader = buildInsertHeader(columnsToMakeNull);
		
			scriptWriter.write("---INSERTS FOR " + tableName.getTableName() + " ---\n\n");
			
			for (Row row : rows) {
				scriptWriter.write(insertHeader + "\n");
				scriptWriter.write(buildValuesStatement(row, columnsToMakeNull, sqlStringOutputter, outputDirectory) +"\n\n");
			}
			
			this.hasBeenOutput = true;
			this.nulledColumns = columnsToMakeNull;
			this.needsUpdate = !columnsToMakeNull.isEmpty();
		} catch (Exception e) {
			throw new RuntimeException("error outputting insert statement" ,e);
		}
	}
	
	private String buildInsertHeader(List<Column> columnsToMakeNull) {
		StringBuilder insertHeaderBuilder = new StringBuilder("INSERT INTO ").append(tableName.getTableName()).append(" (");
		String delimeter = "";
		for (Column column: columns) {
			if (!columnsToMakeNull.contains(column)) {
				insertHeaderBuilder.append(delimeter).append(column.getName());
				delimeter = ", ";
			}
		}
		insertHeaderBuilder.append(")");
		return insertHeaderBuilder.toString();
	}

	private String buildValuesStatement(Row row, List<Column> columnsToMakeNull, SqlStringOutputter sqlStringOutputter,String outputDirectory) {
		StringBuilder valuesBuilder = new StringBuilder("VALUES(");
		String delimeter = "";
		for (Column column: columns) {
			if (!columnsToMakeNull.contains(column)) {
				valuesBuilder.append(delimeter);
				valuesBuilder.append(sqlStringOutputter.asSqlString(tableName.getTableName(), column.getName(), buildRowIdentifier(row, sqlStringOutputter), row.getColumnValue(column), outputDirectory));
				delimeter = ", ";
			}
		}
		valuesBuilder.append(")");
		return valuesBuilder.toString();
	}

	public void writeNulledFieldsAsUpdate(FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter, String outputDirectory) {
		
		try {
			scriptWriter.write("---UPDATES FOR " + tableName.getTableName() + " ---\n\n");
			
			for (Row row : rows) {
				scriptWriter.write("UPDATE " + tableName.getTableName() + " set\n");
				String delimiter = "";
				for (Column column : nulledColumns) {
					scriptWriter.write(delimiter + column.getName() + " = " +
							sqlStringOutputter.asSqlString(tableName.getTableName(), column.getName(), buildRowIdentifier(row, sqlStringOutputter), row.getColumnValue(column), outputDirectory));
					delimiter = ", ";
				}
				scriptWriter.write("\n" + buildWhereClause(row, sqlStringOutputter) + "\n\n");
			}
			this.nulledColumns = new ArrayList<Column>();
			this.needsUpdate = false;
			
		} catch (Exception e) {
			throw new RuntimeException("error outputting update statement", e);
		}
		
	}

	private String buildWhereClause(Row row, SqlStringOutputter sqlStringOutputter) {
		StringBuilder whereBuilder = new StringBuilder("WHERE ");
		String delimiter = "";
		
		for (Column pkColumn : getPKColumns()) {
			whereBuilder.append(delimiter)
				.append(pkColumn.getName())
				.append(" = ")
				.append(sqlStringOutputter.asSqlString(row.getColumnValue(pkColumn)));
			delimiter = " AND ";
		}
		return whereBuilder.toString();
	}


	private String buildRowIdentifier(Row row, SqlStringOutputter sqlStringOutputter) {
		StringBuilder whereBuilder = new StringBuilder("");
		String delimiter = "";
		
		for (Column pkColumn : getPKColumns()) {
			whereBuilder.append(delimiter)
				.append(pkColumn.getName())
				.append("=")
				.append(sqlStringOutputter.asSqlString(row.getColumnValue(pkColumn)));
			delimiter = "&";
		}
		return whereBuilder.toString();
	}

	public class ColumnMissingException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ColumnMissingException(String message) {
			super(message);
		}

	}
}
