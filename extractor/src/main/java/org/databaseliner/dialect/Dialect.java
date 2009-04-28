package org.databaseliner.dialect;

import java.util.Map;

import org.databaseliner.output.SQLOutputter;

/**
 * <p>The Dialect interface is the mechanism by which databaseliner is configured to work with
 * different database systems. Examples of this are different databases require dates and times to
 * be formatted differently, the dialect provides a mechanism for outputting dates in the correct 
 * format for the database being used.</p>
 * 
 * <p>To add support for a new database:</p>
 * <ul>
 * <li>Create a new project in the databaseliner/dialects directory. This should be named after
 * the rdbms the dialect is for.</li>
 * <li>Create a maven pom.xml for your dialect, see examples in the existing supported dialects.
 * The dialect pom is the only place that is allowed to explicitly import any database specific
 * jdbc dependencies, the core of databaseliner must remain rdbms agnostic.</li>
 * <li>Create an implementation of Dialect that exposes database specific behaviour.</li>
 * <li>Configure databaseliner to use you dialect implementation in your configuration file</li>
 * <li>If all works well submit a patch</li>
 * </ul>
 *
 */
public interface Dialect {

	/**
	 * <p>Gets a map of java types to outputters that comprise the database specific types used and specific
	 * output rules. For example the oracle dialect has a outputter mapped to java.util.Date the outputs 
	 * the oracle to_date function as well as an outputter configured for the oracle.sql.CLOB class which the 
	 * oracle driver returns</p>
	 * 
	 * <p>The core databaseliner extractor has a default set of output mappings:</p>
	 * <ul>
	 * <li>java.lang.Double, Float, Integer, Short, Long and BigDecimal - these numeric types are output by performing a simple toString</li>
	 * <li>java.lang.String - is wrapped in single quotes (') and any existing single quote character is escaped ('')</li>
	 * <li>java.lang.Boolean - is output as a bit, 1 for true, 0 for false</li>
	 * </ul>
	 * <p>Entries in the dialect's map override the default mappings.
	 * 
	 * @return A map of strings, representing java class names, to SQLOutputters that can output objects of the
	 * given class. Entries in this class override the default output mappings in databaseliner.
	 */
	Map<String, SQLOutputter> getTypeOutputMap();
	
	/**
	 * 
	 * <p>Gets a database specific header to use when outputting the SQL script. For example the oracle dialect 
	 * includes a directive to turn of variable substitution when running in SQL plus</p>
	 * 
	 * @return the header to put at the start of the output SQL script
	 */
	String getScriptHeader();
	
	/**
	 * 
	 * <p>Gets a database specific footer to use when outputting the SQL script.</p>
	 * 
	 * @return the footer to put at the end of the output SQL script
	 */
	String getScriptFooter();

}
