package org.databaseliner.output;

import org.junit.Assert;
import org.junit.Test;

public class QuoteAndEscapeOutputterTest {

	private QuoteAndEscapeOutputter outputter = new QuoteAndEscapeOutputter();
	
	@Test public void shouldOutputSqlNullStringIfInputIsNull() {
		Assert.assertEquals("NULL", outputter.asSqlString(null));
	}

	@Test public void shouldUseToStringMethodToOutputObjectAndWrapInSqlQuotes() {
		Assert.assertEquals("'word'", outputter.asSqlString("word"));
	}
	
	@Test public void shouldEscapeAnySqlStyleQuotesInInput() {
		Assert.assertEquals("'word''s are good'", outputter.asSqlString("word's are good"));
	}
	
}
