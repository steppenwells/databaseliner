package org.databaseliner.extraction;

import java.util.ArrayList;
import java.util.List;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.Row;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.output.SqlStringOutputter;


public abstract class BaseRelationship implements Relationship {

	private static final int WHERE_CLAUSE_SIZE = 500;
	protected Table tableToFill;
	protected final String tableName;
	
	public BaseRelationship(String tableName) {
		this.tableName = tableName;
	}

	public void setTableToFill(Table tableToFill) {
		this.tableToFill = tableToFill;		
	}

	public void satisfyForRows(List<Row> dirtyRows, Table dirtyTable, DatabaseConnector databaseConnector) {
		List<String> sqlStrings = getExtractionSqlStrings(dirtyRows, dirtyTable, SqlStringOutputter.instance(databaseConnector));
		for (String sql : sqlStrings) {
			databaseConnector.processSqlAndAddResultsToTable(sql, tableToFill);
		}
	}

	protected abstract List<String> getExtractionSqlStrings(List<Row> dirtyRows, Table dirtyTable, SqlStringOutputter sqlStringOutputter);

	public String getTableName() {
		return tableName;
	}

	protected List<List<Row>> splitListIntoChunksSafeForWhereClauses(List<Row> dirtyRows) {
		ArrayList<List<Row>> chunkedLists = new ArrayList<List<Row>>();
		
		for(int i = 0; i <= dirtyRows.size(); i += WHERE_CLAUSE_SIZE) {
			chunkedLists.add(dirtyRows.subList(i, Math.min(i + WHERE_CLAUSE_SIZE, dirtyRows.size())));
		}
		return chunkedLists;
	}

	protected String getInValuesString(List<Row> rows, Column seedColumn, SqlStringOutputter sqlStringOutputter) {
		return getInValuesString(rows, seedColumn, ConverterType.noAction.getConverter(), sqlStringOutputter);
	}
	
	protected String getInValuesString(List<Row> rows, Column seedColumn, Converter converter, SqlStringOutputter sqlStringOutputter) {
		String delimiter = "";
		StringBuilder inValues = new StringBuilder();
		for (Row row : rows) {
			Object columnValue = converter.convert(row.getColumnValue(seedColumn));
			inValues.append(delimiter).append(sqlStringOutputter.asSqlString(columnValue));
			delimiter = ",";
		}
		return inValues.toString();
	}

}
