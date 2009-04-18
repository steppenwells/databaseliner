package org.databaseliner.output;

/**
 * Helper class that contains an arbitrary string to output in the insert script which should not be
 * quoted or escaped. This is used to include arbitrary SQL statements in the insert script and potentially
 * as a return type from user provided FieldProcessors.
 * 
 * @see FieldProcessor 
 */
public class AsIsSQLString {

	private final String sqlString;

	public AsIsSQLString(String sqlString) {
		this.sqlString = sqlString;
	}
	
	@Override
	public String toString() {
		return sqlString;
	}

}
