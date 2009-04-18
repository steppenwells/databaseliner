package org.databaseliner.output;

public class ToStringOutputter implements SQLOutputter {

	public String asSqlString(Object fieldObject) {
		return fieldObject == null ? "NULL" : fieldObject.toString();
	}

	@Override
	public String asPlaceholder(String tableName, String columnName, String rowSelector, Object fieldObject, String outputDirectory) {
		return asSqlString(fieldObject);
	}

	@Override
	public boolean shouldOutputPlaceholder() {
		return false;
	}
}
