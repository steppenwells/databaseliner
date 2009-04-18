package org.databaseliner.manipulation.parser;

import static org.junit.Assert.assertEquals;

import org.databaseliner.extraction.model.TableName;
import org.databaseliner.manipulation.RemoveColumnManipulation;
import org.databaseliner.manipulation.Manipulation.ManipulationScope;
import org.databaseliner.test.XMLParsingTest;
import org.junit.Assert;
import org.junit.Test;

public class RemoveColumnManipulationParserTest extends XMLParsingTest {

	private static final String RENAME_TABLE_XML = "<manipulation type=\"removeColumn\" originalSchema=\"foo\" originalTableName=\"bar\" removeColumn=\"baz\" />\n";
	private RemoveColumnManipulationParser parser = new RemoveColumnManipulationParser();
	
	@Test public void shouldParseOriginalTableDetails() throws Exception {
		RemoveColumnManipulation manipulation = (RemoveColumnManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals(new TableName("bar", "foo"), manipulation.getOriginalTableName());
	}
	
	@Test public void shouldParseColumnToRemove() throws Exception {
		RemoveColumnManipulation manipulation = (RemoveColumnManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals("baz", manipulation.getColumnToRemove());
	}
	
	@Test public void removeColumnManipulatorShouldHaveColumnScope() throws Exception {
		RemoveColumnManipulation manipulation = (RemoveColumnManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		assertEquals(ManipulationScope.COLUMN, manipulation.getManipulationScope());
	}
}
