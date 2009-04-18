package org.databaseliner.output;


public interface SQLOutputter {

	public String asSqlString(Object fieldObject);
	
	public String asPlaceholder(String tableName, String columnName, String rowSelector, Object fieldObject, String outputDirectory);
	
	public boolean shouldOutputPlaceholder();
}
