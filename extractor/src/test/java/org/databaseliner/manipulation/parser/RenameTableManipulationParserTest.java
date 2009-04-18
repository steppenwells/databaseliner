package org.databaseliner.manipulation.parser;

import static org.junit.Assert.*;

import org.databaseliner.extraction.model.TableName;
import org.databaseliner.manipulation.RenameTableManipulation;
import org.databaseliner.manipulation.Manipulation.ManipulationScope;
import org.databaseliner.test.XMLParsingTest;
import org.junit.Assert;
import org.junit.Test;

public class RenameTableManipulationParserTest extends XMLParsingTest {

	private static final String RENAME_TABLE_XML = "<manipulation type=\"renameTable\" originalSchema=\"foo\" originalTableName=\"bar\" newTableName=\"baz\" />\n";
	private RenameTableManipulationParser parser = new RenameTableManipulationParser();
	
	@Test public void shouldParseOriginalTableDetails() throws Exception {
		RenameTableManipulation manipulation = (RenameTableManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals(new TableName("bar", "foo"), manipulation.getOriginalTableName());
	}
	
	@Test public void shouldParseNewTableDetails() throws Exception {
		RenameTableManipulation manipulation = (RenameTableManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals("baz", manipulation.getNewTableName());
	}
	
	@Test public void renameTableManipulatorShouldHaveTableScope() throws Exception {
		RenameTableManipulation manipulation = (RenameTableManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		assertEquals(ManipulationScope.TABLE, manipulation.getManipulationScope());
	}
}
