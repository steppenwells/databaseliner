package org.databaseliner.dialect.oracle;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.junit.Test;

public class OracleDateOutputterTest {

	private OracleDateOutputter outputter = new OracleDateOutputter();
	
	@SuppressWarnings("deprecation")
	@Test public void shouldOutputDateInOracleFormat() throws Exception {
		assertEquals("to_date('2009-01-24 00:00:00', 'DD-MON-YYYY HH24:MI:SS')", outputter.asSqlString(new Date(109, 0, 24)));
	}
	
	@Test public void shouldOutputNullValueIfInputIsNull() throws Exception {
		assertEquals("NULL", outputter.asSqlString(null));
	}
	
	@Test public void shouldOutputNullIfInputIsNotADateType() throws Exception {
		assertEquals("NULL", outputter.asSqlString("String"));
	}
}
