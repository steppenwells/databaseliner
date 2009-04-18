package org.databaseliner.manipulation;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.extraction.model.Table.ColumnMissingException;

public class RenameColumnManipulation implements Manipulation {

	private final static Logger LOG = Logger.getLogger(RenameColumnManipulation.class);
	
	private TableName originalTableName;
	private final String columnToRename;
	private final String newColumnName;

	public RenameColumnManipulation(String originalSchemaName, String originalTableName, String columnToRename, String newColumnName) {
		this.originalTableName = new TableName(originalTableName, originalSchemaName);
		this.columnToRename = columnToRename;
		this.newColumnName = newColumnName;
	}

	@Override
	public ManipulationScope getManipulationScope() {
		return ManipulationScope.COLUMN;
	}

	public String getColumnToRename() {
		return columnToRename;
	}
	
	public String getNewColumnName() {
		return newColumnName;
	}

	public TableName getOriginalTableName() {
		return originalTableName;
	}

	@Override
	public void updateModel(ExtractionModel extractionModel) {
		Table table = extractionModel.getTableWithName(originalTableName);
		
		if (table == null) {
			LOG.warn(String.format("could not rename column %s.%s as table is not present", originalTableName, columnToRename));
			return;
		}
		
		try {
			LOG.debug(String.format("renaming column %s.%s to %s", originalTableName, columnToRename, newColumnName));
			table.renameColumn(columnToRename, newColumnName);
		} catch (ColumnMissingException e) {
			LOG.warn(String.format("could not rename column %s.%s as column not found on table %s", originalTableName, columnToRename, table));
		}
	}

}
