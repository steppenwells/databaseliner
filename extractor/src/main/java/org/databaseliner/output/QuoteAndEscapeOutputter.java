package org.databaseliner.output;

public class QuoteAndEscapeOutputter implements SQLOutputter {

	public String asSqlString(Object fieldObject) {
		if (fieldObject == null) {
			return "NULL";
		}
		else {
			return "'" + escape(fieldObject) + "'";
		}
	}
	
	private String escape(Object fieldObject) {
		return fieldObject.toString().replaceAll("'", "''");
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
