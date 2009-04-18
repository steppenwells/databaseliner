package org.databaseliner.manipulation.parser;

import static org.junit.Assert.*;

import org.databaseliner.manipulation.ValueProvider;
import org.databaseliner.test.XMLParsingTest;
import org.junit.Test;

public class IntegerValueProviderParserTest extends XMLParsingTest {

	private final static String VALUE_PROVIDER_XML = "<value type=\"integer\" data=\"1234\" />";
	private IntegerValueProviderParser parser = new IntegerValueProviderParser();
	
	@Test public void shouldParseIntegerValueProvider() throws Exception {
		ValueProvider valueProvider = parser.parse(getNode("value", VALUE_PROVIDER_XML));
	}
}
