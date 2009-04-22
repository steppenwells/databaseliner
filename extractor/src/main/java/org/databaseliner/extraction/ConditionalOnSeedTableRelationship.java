package org.databaseliner.extraction;

import java.util.ArrayList;
import java.util.List;

import org.databaseliner.extraction.model.Row;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.output.SqlStringOutputter;

public class ConditionalOnSeedTableRelationship extends RefersToRelationship {

	private Converter converter;
	private Condition condition;

	public ConditionalOnSeedTableRelationship(String schemaName, String tableName, String columnName, String seedTable, String seedColumn) {
		super(schemaName, tableName, columnName, seedTable, seedColumn);
	}
	
	@Override
	protected List<String> getExtractionSqlStrings(List<Row> dirtyRows,
			Table dirtyTable, SqlStringOutputter sqlStringOutputter) {
		
		List<String> sqlStatements = new ArrayList<String>();

		List<Row> dirtyRowsMatchingCriteria = filterRowsMatchingCriteria(dirtyRows);
		List<Row> indicativeRows = getRowsWithUniqueValueForColumn(dirtyRowsMatchingCriteria, getSeedColumn());
		List<List<Row>> rowChunks = splitListIntoChunksSafeForWhereClauses(indicativeRows);
		
		for (List<Row> rows : rowChunks) {
			if (rows.size() > 0) {
				sqlStatements.add(String.format("SELECT * FROM %s WHERE %s IN (%s)", tableName, column, getInValuesString(rows, getSeedColumn(), converter, sqlStringOutputter)));
			}
		}

		return sqlStatements;
	}

	private List<Row> filterRowsMatchingCriteria(List<Row> dirtyRows) {

		List<Row> filteredRows = new ArrayList<Row>();
		
		for (Row row : dirtyRows) {
			if (condition.rowMatchesCondition(row)) {
				filteredRows.add(row);
			}
		}
		
		return filteredRows;
	}

	public void setValueConverter(Converter converter) {
		this.converter = converter;
	}

	public void addCondition(String whenColumnName, String valueToEqual) {
		this.condition = new Condition(whenColumnName, valueToEqual);
	}
	
	@Override
	public String toHtmlString() {
		
		return super.toHtmlString() + " " + condition;
	}
	
	public class Condition {

		private final String whenColumnName;
		private final String valueToEqual;

		public Condition(String whenColumnName, String valueToEqual) {
			this.whenColumnName = whenColumnName;
			this.valueToEqual = valueToEqual;
		}

		public boolean rowMatchesCondition(Row row) {
			Object columnValue = row.getColumnValue(whenColumnName);
			return valueToEqual.equals(columnValue.toString());
		}
		
		@Override
		public String toString() {
			return "when " + whenColumnName + " = " + valueToEqual;
		}

	}
}
