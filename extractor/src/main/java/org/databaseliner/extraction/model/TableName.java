package org.databaseliner.extraction.model;


public class TableName {
	private final String tableName;
	private String schemaName;

	public TableName(String tableName, String schemaName) {
		this.tableName = tableName;
		this.schemaName = schemaName;
	}
	
	public String getTableName() {
		return tableName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	@Override
	public String toString() {
		if (schemaName != null) {
			return schemaName + "." + tableName;
		}
		return tableName;
	}
	
	public String getHtmlIdSafeName() {
		String rawTableName = this.toString();
		return rawTableName.replace('.', '-').toLowerCase();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((schemaName == null) ? 0 : schemaName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableName other = (TableName) obj;
		if (schemaName == null) {
			if (other.schemaName != null)
				return false;
		} else if (!schemaName.equals(other.schemaName))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}
}
