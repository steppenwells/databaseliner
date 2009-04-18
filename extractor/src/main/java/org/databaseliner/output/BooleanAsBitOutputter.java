package org.databaseliner.output;

public class BooleanAsBitOutputter implements SQLOutputter {

	@Override
	public String asSqlString(Object fieldObject) {
		
		if (fieldObject != null && fieldObject instanceof Boolean) {
			Boolean bool = (Boolean) fieldObject;
			return bool ? "1" : "0";
		}
		return "NULL";
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
