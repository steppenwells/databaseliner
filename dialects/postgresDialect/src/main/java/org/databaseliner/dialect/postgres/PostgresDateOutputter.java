package org.databaseliner.dialect.postgres;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.databaseliner.output.SQLOutputter;

public class PostgresDateOutputter implements SQLOutputter {

	private SimpleDateFormat postgresDateFormat = new SimpleDateFormat("''yyyy-MM-dd''");
	
	@Override
	public String asSqlString(Object fieldObject) {
		if (fieldObject == null || !(fieldObject instanceof Date)) {
			return "NULL";
		}
		Date date = (Date) fieldObject;
		return postgresDateFormat.format(date);
	}

	@Override
	public String asPlaceholder(String tableName, String columnName,
			String rowSelector, Object fieldObject, String outputDirectory) {
		return asSqlString(fieldObject);
	}

	@Override
	public boolean shouldOutputPlaceholder() {
		return false;
	}

}
