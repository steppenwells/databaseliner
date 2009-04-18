package org.databaseliner.manipulation;

import org.databaseliner.extraction.model.Row;

public class FieldProcessorValueProvider implements ValueProvider {

	private final FieldProcessor processor;
	private final String columnName;

	public FieldProcessorValueProvider(FieldProcessor processor, String columnName) {
		this.processor = processor;
		this.columnName = columnName;
	}
	
	@Override
	public Object getValue(Row row) {
		Object seedFieldValue = row.getColumnValue(columnName);
		if (seedFieldValue != null) {
			return processor.getFieldValue(seedFieldValue);
		}
		return null;
	}


}
