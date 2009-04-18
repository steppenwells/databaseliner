package org.databaseliner.extraction;


import java.util.ArrayList;
import java.util.List;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.Row;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.output.SqlStringOutputter;

public class RefersToRelationship extends BaseRelationship {

	protected final String column;
	protected final String seedTableName;
	protected final String seedColumnName;
	
	protected Table seedTable;
	
	public RefersToRelationship(String schemaName, String tableName, String column, String seedTableName, String seedColumn) {
		super(new TableName(tableName, schemaName));
		this.column = column;
		this.seedTableName = seedTableName;
		this.seedColumnName = seedColumn;
	}

	public void addSeedTable(Table seedTable) {
		this.seedTable = seedTable;	
	}

	@Override
	public boolean hasSeedTable(Table table) {
		return seedTableName.equals(table.getName().getTableName());
	}

	protected Column getSeedColumn() {
		return seedTable.getColumnWithName(seedColumnName);
	}

	@Override
	public String toString() {
		return String.format("data in [%s.%s] will populate [%s.%s]", seedTableName, seedColumnName, tableName, column);
	}

	@Override
	protected List<String> getExtractionSqlStrings(List<Row> dirtyRows, Table dirtyTable, SqlStringOutputter sqlStringOutputter) {
	
		List<String> sqlStatements = new ArrayList<String>();
		List<Row> indicativeRows = getRowsWithUniqueValueForColumn(dirtyRows, getSeedColumn());
		List<List<Row>> rowChunks = splitListIntoChunksSafeForWhereClauses(indicativeRows);
		
		for (List<Row> rows : rowChunks) {
			if (rows.size() > 0) {
				sqlStatements.add(String.format("SELECT * FROM %s WHERE %s IN (%s)", tableName, column, getInValuesString(rows, getSeedColumn(), sqlStringOutputter)));
			}
		}

		return sqlStatements;
	}

	protected List<Row> getRowsWithUniqueValueForColumn(List<Row> dirtyRows, Column seedColumn) {
		List<Row> dedupedRows = new ArrayList<Row>();
		List<Object> foundUniques = new ArrayList<Object>();
		
		for (Row row : dirtyRows) {
			Object value = row.getColumnValue(seedColumn);
			if (value != null && !foundUniques.contains(value)) {
				foundUniques.add(value);
				dedupedRows.add(row);
			}
		}
		
		return dedupedRows;
	}

}
