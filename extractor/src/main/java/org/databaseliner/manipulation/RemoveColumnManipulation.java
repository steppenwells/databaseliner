package org.databaseliner.manipulation;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.extraction.model.Table.ColumnMissingException;

public class RemoveColumnManipulation implements Manipulation {

	private final static Logger LOG = Logger.getLogger(RemoveColumnManipulation.class);
	
	private final String columnToRemove;
	private TableName originalTableName;

	public RemoveColumnManipulation(String originalSchemaName, String originalTableName, String columnToRemove) {
		this.originalTableName = new TableName(originalTableName, originalSchemaName);
		this.columnToRemove = columnToRemove;
	}

	@Override
	public ManipulationScope getManipulationScope() {
		return ManipulationScope.COLUMN;
	}

	public String getColumnToRemove() {
		return columnToRemove;
	}

	public TableName getOriginalTableName() {
		return originalTableName;
	}

	@Override
	public void updateModel(ExtractionModel extractionModel) {
		
		Table table = extractionModel.getTableWithName(originalTableName);
		
		if (table == null) {
			LOG.warn(String.format("could not remove column %s.%s as table is not present", originalTableName, columnToRemove));
			return;
		}
		
		try {
			LOG.debug(String.format("removing column %s.%s", originalTableName, columnToRemove));
			table.removeColumnWithName(columnToRemove);
		} catch (ColumnMissingException e) {
			LOG.warn(String.format("could not remove column %s.%s as column not found on table %s", originalTableName, columnToRemove, table));
		}
		
	}

}
