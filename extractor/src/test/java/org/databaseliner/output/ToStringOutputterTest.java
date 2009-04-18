package org.databaseliner.output;

import org.junit.Assert;
import org.junit.Test;

public class ToStringOutputterTest {
	
	private ToStringOutputter outputter = new ToStringOutputter();
	
	@Test public void shouldUseToStringMethodToOutputObject() {
		Assert.assertEquals("37", outputter.asSqlString(37));
	}
	
	@Test public void shouldOutputSqlNullStringIfInputIsNull() {
		Assert.assertEquals("NULL", outputter.asSqlString(null));
	}
}
