package org.databaseliner.dialect.oracle;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.databaseliner.output.SQLOutputter;

public class OracleDateOutputter implements SQLOutputter {

	private SimpleDateFormat oracleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	private String oracleDateSqlFormatString = "to_date('%s', 'DD-MON-YYYY HH24:MI:SS')";
	
	@Override
	public String asSqlString(Object fieldObject) {
		if (fieldObject == null || !(fieldObject instanceof Date)) {
			return "NULL";
		}
		Date date = (Date) fieldObject;
		return String.format(oracleDateSqlFormatString, oracleDateFormat.format(date));
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
