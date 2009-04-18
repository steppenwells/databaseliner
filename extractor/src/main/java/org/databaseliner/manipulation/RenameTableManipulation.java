package org.databaseliner.manipulation;

import java.util.Map;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;

public class RenameTableManipulation implements Manipulation {
	
	private final static Logger LOG = Logger.getLogger(RenameTableManipulation.class);

	private final String newTableName;
	private TableName originalTableName;

	public RenameTableManipulation(String originalSchemaName, String originalTableName, String newTableName) {
		this.originalTableName = new TableName(originalTableName, originalSchemaName);
		this.newTableName = newTableName;
	}

	@Override
	public ManipulationScope getManipulationScope() {
		return ManipulationScope.TABLE;
	}

	public String getNewTableName() {
		return newTableName;
	}

	public TableName getOriginalTableName() {
		return originalTableName;
	}

	@Override
	public void updateModel(ExtractionModel extractionModel) {
		TableName newTableName = new TableName(this.newTableName, null);
		Table tableToRename = extractionModel.getTableWithName(originalTableName);
		
		if (tableToRename == null) {
			LOG.warn("Could not rename " + originalTableName + " table not found");
			return;
		}
		
		LOG.debug(String.format("renaming %s to %s and updating output dependencies", originalTableName, newTableName));
		tableToRename.setName(newTableName);
		
		extractionModel.registerTableWithNewName(tableToRename, originalTableName, newTableName);
		
		for (Table referencingTable : extractionModel.getAllTables()) {
			updateOutputDependencies(newTableName, referencingTable);
		}
	}

	private void updateOutputDependencies(TableName newTableName, Table table) {
		
		Map<Column, TableName> outputDependencies = table.getOutputDependencies();
		for (Column column : outputDependencies.keySet()) {
			TableName referencingTableName = outputDependencies.get(column);
			if (referencingTableName.equals(originalTableName)) {
				LOG.debug(String.format("updating %s.%s to reference %s", table.getName(), column, newTableName));
				outputDependencies.put(column, newTableName);
			}
		}
	}

}
