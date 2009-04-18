package org.databaseliner.dialect.oracle;

import java.util.HashMap;
import java.util.Map;

import org.databaseliner.dialect.Dialect;
import org.databaseliner.output.SQLOutputter;

public class OracleDialect implements Dialect {

	public Map getTypeOutputMap() {
		HashMap<String, SQLOutputter> postgresTypeOutputters = new HashMap<String, SQLOutputter>();
		
		postgresTypeOutputters.put("java.sql.Date", new OracleDateOutputter());
		postgresTypeOutputters.put("oracle.sql.CLOB", new OracleCLOBOutputter());
		
		return postgresTypeOutputters;
	}

}
