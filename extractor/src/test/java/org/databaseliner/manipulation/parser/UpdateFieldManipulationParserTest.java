package org.databaseliner.manipulation.parser;

import static org.junit.Assert.assertEquals;

import org.databaseliner.extraction.model.TableName;
import org.databaseliner.manipulation.UpdateFieldManipulation;
import org.databaseliner.manipulation.Manipulation.ManipulationScope;
import org.databaseliner.test.XMLParsingTest;
import org.junit.Assert;
import org.junit.Test;

public class UpdateFieldManipulationParserTest extends XMLParsingTest {

	private static final String RENAME_TABLE_XML = 
			"<manipulation type=\"updateField\" originalSchema=\"foo\" originalTableName=\"bar\" updateColumn=\"baz\" >\n" +
				"<value type=\"asIs\" data=\"blah\" />\n" +
			"</manipulation>";
	private UpdateFieldManipulationParser parser = new UpdateFieldManipulationParser();
	
	@Test public void shouldParseOriginalTableDetails() throws Exception {
		UpdateFieldManipulation manipulation = (UpdateFieldManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals(new TableName("bar", "foo"), manipulation.getOriginalTableName());
	}
	
	@Test public void shouldParseColumnToUpdate() throws Exception {
		UpdateFieldManipulation manipulation = (UpdateFieldManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertEquals("baz", manipulation.getColumnToUpdate());
	}
	
	@Test public void shouldAddValueProvider() throws Exception {
		UpdateFieldManipulation manipulation = (UpdateFieldManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		Assert.assertNotNull(manipulation.getValueProvider());
	}
	
	@Test public void updateFieldManipulatorShouldHaveFieldScope() throws Exception {
		UpdateFieldManipulation manipulation = (UpdateFieldManipulation) parser.parse(getNode("manipulation", RENAME_TABLE_XML));
		assertEquals(ManipulationScope.FIELD, manipulation.getManipulationScope());
	}

}
