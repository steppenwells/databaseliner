package org.databaseliner.extraction;

import java.util.ArrayList;
import java.util.List;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.Row;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.output.SqlStringOutputter;

public class CompositeReferingToMultipleTablesRelationship extends BaseRelationship {

	private final List<TableRelationship> tableRelationships;
	
	public CompositeReferingToMultipleTablesRelationship(String schemaName, String tableName) {
		
		super(new TableName(tableName, schemaName));
		tableRelationships = new ArrayList<TableRelationship>();
	}

	public void addTableRelationship(String column, String seedTableName, String seedColumn) {
	
		tableRelationships.add(new TableRelationship(column, seedTableName, seedColumn));		
	}
	
	@Override
	public boolean hasSeedTable(Table table) {
		return getRelationshipForSeedTable(table) != null;
	}
	
	public void addSeedTable(Table table) {
		TableRelationship relationshipForSeedTable = getRelationshipForSeedTable(table);
		if (relationshipForSeedTable != null) {
			relationshipForSeedTable.setSeedTable(table);
		}
	}

	private TableRelationship getRelationshipForSeedTable(Table seedTable) {
		for (TableRelationship tableRelationship : tableRelationships) {
			if(tableRelationship.seedTableName.equals(seedTable.getName().getTableName())) {
				return tableRelationship;
			}
		}
		return null;
	}
	
	@Override
	protected List<String> getExtractionSqlStrings(List<Row> dirtyRows, Table dirtyTable, SqlStringOutputter sqlStringOutputter) {
		List<String> sqlStatements = new ArrayList<String>();
		
		if (dirtyRows.size() == 0) {
			return new ArrayList<String>();
		}
		
		List<List<Row>> dirtyChunks = splitListIntoChunksSafeForWhereClauses(dirtyRows);
		
		sqlStatements.add(createSqlTempate());
		
		for (TableRelationship relatedTable : tableRelationships) {
			List<List<Row>> rowchunks;
			if (relatedTable.getSeedTable().equals(dirtyTable)) {
				rowchunks = dirtyChunks;
			} else {
				
				List<Row> relatedRows = relatedTable.getSeedTable().getRows();
				if (relatedRows.size() == 0) {
					return new ArrayList<String>();
				}
				
				rowchunks = splitListIntoChunksSafeForWhereClauses(relatedRows);
			}
			
			sqlStatements = expandTemplateFor(rowchunks, relatedTable, sqlStatements, sqlStringOutputter);
		}

		return sqlStatements;
	}

	private List<String> expandTemplateFor(List<List<Row>> chunks, TableRelationship relatedTable, List<String> sqlStatements, SqlStringOutputter sqlStringOutputter) {
		
		List<String> expandedSqlStatements = new ArrayList<String>();
		
		for (String template : sqlStatements) {
			for (List<Row> chunk : chunks) {
				expandedSqlStatements.add(
						template.replaceAll("\\{" + relatedTable.getSeedIdentifier() + "\\}",
						getInValuesString(chunk, relatedTable.getSeedColumn(), sqlStringOutputter))
					);
			}
		}
		
		return expandedSqlStatements;
	}

	private String createSqlTempate() {
		StringBuilder sqlStatementTemplate = new StringBuilder("SELECT * FROM ").append(tableName).append(" WHERE ");
		String delimeter = " ";
		for (TableRelationship relatedTable : tableRelationships) {
			sqlStatementTemplate
				.append(delimeter)
				.append(relatedTable.column)
				.append(" IN ({")
				.append(relatedTable.getSeedIdentifier())
				.append("})");
			delimeter = " AND ";
		}
		return sqlStatementTemplate.toString();
	}
	
	@Override
	public String toHtmlString() {
		StringBuilder builder = new StringBuilder();
		builder.append("data in the following tables: ");
		for (TableRelationship tableRelationship : tableRelationships) {
			TableName seedTableName = tableRelationship.seedTable.getName();
			builder.append(String.format("<a href=\"#%s\">%s.%s</a> ", 
					seedTableName.getHtmlIdSafeName(), 
					seedTableName.toString(), 
					tableRelationship.getSeedColumn().getName()));
		}
		builder.append(String.format("polulates data in <a href=\"#%s\">%s</a>", tableName.getHtmlIdSafeName(), tableName));
		return builder.toString();
	}
	

	private class TableRelationship {

		private final String column;
		private final String seedTableName;
		private final String seedColumn;
		private Table seedTable;

		public TableRelationship(String column, String seedTableName, String seedColumn) {
			this.column = column;
			this.seedTableName = seedTableName;
			this.seedColumn = seedColumn;
		}

		public Table getSeedTable() {
			// TODO Auto-generated method stub
			return seedTable;
		}

		public void setSeedTable(Table table) {
			this.seedTable = table;
		}
		
		public Column getSeedColumn() {
			return seedTable.getColumnWithName(seedColumn);
		}
		
		public String getSeedIdentifier() {
			return seedTableName + "." + seedColumn;
		}
	}

}
