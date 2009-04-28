package org.databaseliner.dialect.postgres;

import java.util.HashMap;
import java.util.Map;

import org.databaseliner.dialect.Dialect;
import org.databaseliner.output.SQLOutputter;

public class PostgresDialect implements Dialect {

	public Map<String, SQLOutputter> getTypeOutputMap() {

		HashMap<String, SQLOutputter> postgresTypeOutputters = new HashMap<String, SQLOutputter>();
		
		postgresTypeOutputters.put("java.sql.Date", new PostgresDateOutputter());
		
		return postgresTypeOutputters;
	}

	@Override
	public String getScriptFooter() {
		return "";
	}

	@Override
	public String getScriptHeader() {
		return "";
	}

}
