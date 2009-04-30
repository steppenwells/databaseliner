package org.databaseliner.extraction;


import java.util.ArrayList;
import java.util.List;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.Row;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.output.SqlStringOutputter;

public class IgnoredRelationship extends BaseRelationship {


	
	
	public IgnoredRelationship(String seedSchemaName, String seedTableName,	String seedColumn,
								String toSchemaName, String toTableName, String toColumn) {
		
	}

	@Override
	protected List<String> getExtractionSqlStrings(List<Row> dirtyRows,
			Table dirtyTable, SqlStringOutputter sqlStringOutputter) {
		
		return new ArrayList<String>();
	}

	@Override
	public void addSeedTable(Table seedTable) {
		// do nothing, this is a marker relationship
	}

	@Override
	public boolean hasSeedTable(Table table) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toHtmlString() {
		// TODO Auto-generated method stub
		return null;
	}


}
