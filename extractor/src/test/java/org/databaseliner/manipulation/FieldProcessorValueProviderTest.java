package org.databaseliner.manipulation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.Row;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FieldProcessorValueProviderTest {

	@Mock private FieldProcessor fieldProcessor;
	private FieldProcessorValueProvider valueProvider;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
		valueProvider = new FieldProcessorValueProvider(fieldProcessor, "columnToCopy");
	}
	
	@Test
	public void shouldReturnValueFromSpecifiedColumn() throws Exception {
		
		Column columnToCopy = new Column("columnToCopy", true);
		Column otherColumn = new Column("otherColumn", true);
		Row row = new Row();
		row.addFieldData(otherColumn, "other" );
		row.addFieldData(columnToCopy, "expected");
		
		valueProvider.getValue(row);
		verify(fieldProcessor).getFieldValue("expected");
	}
	
	@Test
	public void shouldReturnNullIfcolumnNotPresentInRow() throws Exception {
		
		Column otherColumn = new Column("otherColumn", true);
		Row row = new Row();
		row.addFieldData(otherColumn, "other" );
		
		assertNull(valueProvider.getValue(row));
		verify(fieldProcessor, never()).getFieldValue("expected");
	}
}
