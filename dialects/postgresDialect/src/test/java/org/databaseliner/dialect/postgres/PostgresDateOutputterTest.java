package org.databaseliner.dialect.postgres;

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.Test;

public class PostgresDateOutputterTest {

	private PostgresDateOutputter outputter = new PostgresDateOutputter();
	
	@SuppressWarnings("deprecation")
	@Test public void shouldOutputDateInPostgresFormat() throws Exception {
		assertEquals("'2009-01-24'", outputter.asSqlString(new Date(109,0,24)));
	}
	
	@Test public void shouldOutputNullValueIfInputIsNull() throws Exception {
		assertEquals("NULL", outputter.asSqlString(null));
	}
	
	@Test public void shouldOutputNullIfInputIsNotADateType() throws Exception {
		assertEquals("NULL", outputter.asSqlString("String"));
	}
}
