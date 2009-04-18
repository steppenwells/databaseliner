package org.databaseliner.manipulation.parser;


import org.databaseliner.manipulation.ManipulationService;
import org.databaseliner.test.XMLParsingTest;
import org.junit.Assert;
import org.junit.Test;

public class ManipulationConfigParserTest extends XMLParsingTest {

	private static final String CONFIG_WITH_MANIPULATIONS = "<databaseliner><manipulations>\n" +
			"<manipulation type=\"renameTable\" originalSchema=\"\" originalTableName=\"\" newTableName=\"\" />\n" +
		"</manipulations></databaseliner>";

	@Test public void shouldParseTableManipulations() throws Exception {
		ManipulationService manipulationService = ManipulationConfigParser.parse(getDocument(CONFIG_WITH_MANIPULATIONS));
		Assert.assertEquals(1, manipulationService.getTableManipulations().size());
	}
}
