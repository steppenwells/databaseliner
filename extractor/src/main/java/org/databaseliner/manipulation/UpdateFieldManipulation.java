package org.databaseliner.manipulation;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.extraction.model.Table.ColumnMissingException;

public class UpdateFieldManipulation implements Manipulation {

	private final static Logger LOG = Logger.getLogger(UpdateFieldManipulation.class);
	
	private final String columnToUpdate;
	private TableName originalTableName;
	private ValueProvider valueProvider;

	public UpdateFieldManipulation(String originalSchemaName, String originalTableName, String columnToUpdate) {
		this.originalTableName = new TableName(originalTableName, originalSchemaName);
		this.columnToUpdate = columnToUpdate;
	}

	@Override
	public ManipulationScope getManipulationScope() {
		return ManipulationScope.FIELD;
	}

	public String getColumnToUpdate() {
		return columnToUpdate;
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
			LOG.warn(String.format("could not update field %s.%s as table is not present", originalTableName, columnToUpdate));
			return;
		}
		
		try {
			LOG.debug(String.format("updating field %s.%s", originalTableName, columnToUpdate));
			table.updateValuesInColumn(columnToUpdate, valueProvider);
		} catch (ColumnMissingException e) {
			LOG.warn(String.format("could not rename column %s.%s as column not found on table %s", originalTableName, columnToUpdate, table));
		}
	}

	
}
