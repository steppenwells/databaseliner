package org.databaseliner.extraction.model;

public class Column {

	private String columnName;
	private final boolean nullable;
	private boolean primaryKey = false;

	public Column(String columnName, boolean nullable) {
		this.columnName = columnName;
		this.nullable = nullable;
	}

	public String getName() {
		return columnName;
	}

	public void setName(String newColumnName) {
		columnName = newColumnName;
	}

	public void markAsPrimaryKey() {
		primaryKey = true;		
	}
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	
	public boolean isNullable() {
		return nullable;
	}

	@Override
	public String toString() {
		return columnName 
			+ (primaryKey ? " *" : "") 
			+ (nullable ? " NULL" : "");
	}


}
