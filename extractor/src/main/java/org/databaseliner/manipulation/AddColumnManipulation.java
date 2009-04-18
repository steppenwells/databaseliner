package org.databaseliner.manipulation;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.extraction.model.Table.ColumnMissingException;

public class AddColumnManipulation implements Manipulation {

	private final static Logger LOG = Logger.getLogger(AddColumnManipulation.class);
	
	private final String columnToAdd;
	private TableName originalTableName;
	private ValueProvider valueProvider;

	public AddColumnManipulation(String originalSchemaName, String originalTableName, String columnToAdd) {
		this.originalTableName = new TableName(originalTableName, originalSchemaName);
		this.columnToAdd = columnToAdd;
	}

	@Override
	public ManipulationScope getManipulationScope() {
		return ManipulationScope.COLUMN;
	}

	public String getColumnToAdd() {
		return columnToAdd;
	}

	public TableName getOriginalTableName() {
		return originalTableName;
	}

	public void setValueProvider(ValueProvider valueProvider) {
		this.valueProvider = valueProvider;
	}

	public ValueProvider getValueProvider() {
		return valueProvider;
	}

	@Override
	public void updateModel(ExtractionModel extractionModel) {
		Table table = extractionModel.getTableWithName(originalTableName);
		
		if (table == null) {
			LOG.warn(String.format("could not add column %s.%s as table is not present", originalTableName, columnToAdd));
			return;
		}
		
		try {
			table.getColumnWithName(columnToAdd);
			LOG.warn(String.format("could not add column %s.%s as column already exists on table %s", originalTableName, columnToAdd, table));
		} catch (ColumnMissingException e) {

			// column is not already on table fo we're clear to add it.
			LOG.debug(String.format("adding column %s.%s", originalTableName, columnToAdd));
			table.addColumn(new Column(columnToAdd, true)); 
			table.updateValuesInColumn(columnToAdd, valueProvider);
		}
	}
}
