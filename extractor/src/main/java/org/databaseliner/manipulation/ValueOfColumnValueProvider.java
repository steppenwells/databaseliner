package org.databaseliner.manipulation;

import org.databaseliner.extraction.model.Row;

public class ValueOfColumnValueProvider implements ValueProvider {

	private final String columnName;

	public ValueOfColumnValueProvider(String columnName) {
		this.columnName = columnName;
	}

	@Override
	public Object getValue(Row row) {
		return row.getColumnValue(columnName);
	}

}
