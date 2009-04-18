package org.databaseliner.output;

import java.util.HashMap;
import java.util.Map;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.dialect.Dialect;

public class SqlStringOutputter {

	private static SqlStringOutputter outputter = null;
	private Map<String, SQLOutputter> javaTypeMap = new HashMap<String, SQLOutputter>();
	
	public SqlStringOutputter(DatabaseConnector connector) {
		
		createDefaultMappings();
		createDialectSpecificMappings(connector.getDialect());
	}


	private void createDefaultMappings() {
		SQLOutputter toStringOutputter = new ToStringOutputter();
		javaTypeMap.put("java.math.BigDecimal", toStringOutputter);
		javaTypeMap.put("java.lang.Double", toStringOutputter);
		javaTypeMap.put("java.lang.Float", toStringOutputter);
		javaTypeMap.put("java.lang.Integer", toStringOutputter);
		javaTypeMap.put("java.lang.Short", toStringOutputter);
		javaTypeMap.put("java.lang.Long", toStringOutputter);
		javaTypeMap.put("org.databaseliner.output.AsIsSQLString", toStringOutputter);
		
		SQLOutputter quoteAndEscapeOutputter = new QuoteAndEscapeOutputter();
		javaTypeMap.put("java.lang.String", quoteAndEscapeOutputter);
		
		javaTypeMap.put("java.lang.Boolean", new BooleanAsBitOutputter());
	}
	
	private void createDialectSpecificMappings(Dialect dialect) {
		Map<String, SQLOutputter> dialectTypeMap = dialect.getTypeOutputMap();
		javaTypeMap.putAll(dialectTypeMap);
	}

	public static SqlStringOutputter instance(DatabaseConnector connector) {
		if (outputter == null){
			outputter = new SqlStringOutputter(connector);
		}
		return outputter;
	}

	public String asSqlString(Object fieldObject) {
		if(fieldObject == null) {
			return "NULL";
		}
		
		return getSqlOutputterFor(fieldObject).asSqlString(fieldObject);		
	}
	
	public String asSqlString(String tableName, String columnName, String rowSelector, Object fieldObject, String outputDirectory) {
		if(fieldObject == null) {
			return "NULL";
		}
		
		SQLOutputter sqlOutputterForObject = getSqlOutputterFor(fieldObject);
		if (sqlOutputterForObject.shouldOutputPlaceholder()) {
			return sqlOutputterForObject.asPlaceholder(tableName, columnName, rowSelector, fieldObject, outputDirectory);
		} else {
			return sqlOutputterForObject.asSqlString(fieldObject);
		}
	}

	private SQLOutputter getSqlOutputterFor(Object fieldObject) {
		SQLOutputter outputterForObject = javaTypeMap.get(fieldObject.getClass().getName());
		if (outputterForObject != null) {
			return outputterForObject;	
		}
		throw new RuntimeException("No outputter exists for java type " + fieldObject.getClass().getName());
	}
}
