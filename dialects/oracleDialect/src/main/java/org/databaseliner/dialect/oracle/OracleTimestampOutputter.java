package org.databaseliner.dialect.oracle;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import oracle.sql.TIMESTAMP;

import org.databaseliner.output.SQLOutputter;

public class OracleTimestampOutputter implements SQLOutputter {

	private SimpleDateFormat oracleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String oracleDateSqlFormatString = "to_date('%s', 'DD-MON-YYYY HH24:MI:SS')";
	
	@Override
	public String asSqlString(Object fieldObject) {
		if (fieldObject == null || !(fieldObject instanceof TIMESTAMP)) {
			return "NULL";
		}
		try {
			TIMESTAMP date = (TIMESTAMP) fieldObject;
			Timestamp timestampValue = date.timestampValue();
		return String.format(oracleDateSqlFormatString, oracleDateFormat.format(timestampValue));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "NULL";
		}
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
