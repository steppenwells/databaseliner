package org.databaseliner.manipulation;

import static org.junit.Assert.*;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.Row;
import org.junit.Before;
import org.junit.Test;

public class ValueOfColumnValueProviderTest {

	private ValueOfColumnValueProvider valueProvider;
	
	@Before public void setup() {
		valueProvider = new ValueOfColumnValueProvider("columnToCopy");
	}
	
	@Test
	public void shouldReturnValueFromSpecifiedColumn() throws Exception {
		
		Column columnToCopy = new Column("columnToCopy", true);
		Column otherColumn = new Column("otherColumn", true);
		Row row = new Row();
		row.addFieldData(otherColumn, "other" );
		row.addFieldData(columnToCopy, "expected");
		
		assertEquals("expected", valueProvider.getValue(row));
	}
	
	@Test
	public void shouldReturnNullIfcolumnNotPresentInRow() throws Exception {
		
		Column otherColumn = new Column("otherColumn", true);
		Row row = new Row();
		row.addFieldData(otherColumn, "other" );
		
		assertNull(valueProvider.getValue(row));
	}
}
