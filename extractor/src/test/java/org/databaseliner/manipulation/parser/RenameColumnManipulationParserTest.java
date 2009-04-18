package org.databaseliner.manipulation.parser;

import static org.junit.Assert.assertEquals;

import org.databaseliner.extraction.model.TableName;
import org.databaseliner.manipulation.RenameColumnManipulation;
import org.databaseliner.manipulation.Manipulation.ManipulationScope;
import org.databaseliner.test.XMLParsingTest;
import org.junit.Assert;
import org.junit.Test;

public class RenameColumnManipulationParserTest extends XMLParsingTest {

	private static final String RENAME_TABLE_XML = "<manipulation type=\"renameColumn\" originalSchema=\"foo\" originalTableName=\"bar\" renameColumn=\"baz\" newColumnName=\"hux\" />\n";
	private RenameColumnManipulationParser parser = new RenameColumnManipulationParser();
	
	@Test public void shouldParseOriginalTableDetails() throws Exception {
		RenameColumnManipulation manipulation = (RenameColumnManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals(new TableName("bar", "foo"), manipulation.getOriginalTableName());
	}
	
	@Test public void shouldParseOldColumnName() throws Exception {
		RenameColumnManipulation manipulation = (RenameColumnManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals("baz", manipulation.getColumnToRename());
	}

	@Test public void shouldParseNewColumnName() throws Exception {
		RenameColumnManipulation manipulation = (RenameColumnManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals("hux", manipulation.getNewColumnName());
	}
	
	@Test public void removeColumnManipulatorShouldHaveColumnScope() throws Exception {
		RenameColumnManipulation manipulation = (RenameColumnManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		assertEquals(ManipulationScope.COLUMN, manipulation.getManipulationScope());
	}
}
