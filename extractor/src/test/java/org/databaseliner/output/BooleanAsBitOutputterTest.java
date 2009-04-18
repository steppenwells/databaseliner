package org.databaseliner.output;

import static org.junit.Assert.*;

import org.junit.Test;

public class BooleanAsBitOutputterTest {
	
	private BooleanAsBitOutputter outputter = new BooleanAsBitOutputter();

	@Test public void shouldOutputTrueAs1() throws Exception {
		assertEquals("1", outputter.asSqlString(true));
	}

	@Test public void shouldOutputFalseAs0() throws Exception {
		assertEquals("0", outputter.asSqlString(false));
	}
	
	@Test public void shouldOutputNullAsNULL() throws Exception {
		assertEquals("NULL", outputter.asSqlString(null));
	}
	
	@Test public void shouldOutputNoBooleanAsNULL() throws Exception {
		assertEquals("NULL", outputter.asSqlString("this should not happen"));
	}
}
