package org.databaseliner.extraction;


import java.util.ArrayList;
import java.util.List;

import org.databaseliner.extraction.model.Row;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.output.SqlStringOutputter;

public class IgnoredRelationship extends BaseRelationship {

	
	private TableName seedTableName;
	private String seedColumn;
	private TableName toTableName;
	private String toColumn;

	public IgnoredRelationship(String seedSchemaName, String seedTableName,	String seedColumn,
								String toSchemaName, String toTableName, String toColumn) {
		
		super(new TableName(toTableName, toSchemaName));
		
		this.seedTableName = new TableName(seedTableName, seedSchemaName);
		this.seedColumn = seedColumn;
		this.toTableName = new TableName(toTableName, toSchemaName);
		this.toColumn = toColumn;
		
	}

	@Override
	protected List<String> getExtractionSqlStrings(List<Row> dirtyRows,	Table dirtyTable, SqlStringOutputter sqlStringOutputter) {
		return new ArrayList<String>(); // ignored relationships don't extract
	}

	@Override
	public void addSeedTable(Table seedTable) {
		// do nothing, this is a marker relationship
	}

	@Override
	public boolean hasSeedTable(Table table) {
		// always return false as we don't want to bind this relationship into the extractions seeded by the seed table
		return false; 
	}

    @Override
    public void verify() {
        // nothing can go wrong here
    }

    @Override
	public String toHtmlString() {
		return String.format("relationship between [%s.%s] and  <a href=\"#%s\">[%s.%s]</a> is ignored", seedTableName, seedColumn, toTableName.getHtmlIdSafeName(), toTableName, toColumn);
	}

	public boolean shouldIgnoreRelationship(TableName fromTableName, String fromColumn, TableName toTableName, String toColumn) {
		
		return this.seedTableName.equals(fromTableName)
			&& this.seedColumn.equals(fromColumn)
			&& this.toTableName.equals(toTableName)
			&& this.toColumn.equals(toColumn);
	}
}
