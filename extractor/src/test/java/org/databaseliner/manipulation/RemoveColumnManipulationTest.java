package org.databaseliner.manipulation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.databaseliner.extraction.model.Table.ColumnMissingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RemoveColumnManipulationTest {

	@Mock private ExtractionModel model;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldRemoveSpecifiedColumnFromTable() throws Exception {
		try {
			RemoveColumnManipulation manipulation = new RemoveColumnManipulation("foo", "bar", "baz");
			
			Table table = new Table(manipulation.getOriginalTableName());
			Column columnToRemove = new Column("baz", false);
			table.addColumn(columnToRemove);
			when(model.getTableWithName(manipulation.getOriginalTableName())).thenReturn(table);
			
			manipulation.updateModel(model);
			
			// this line throws exception when the column is missing
			table.getColumnWithName("baz");
			fail("expected column missing excepion");
		} catch (ColumnMissingException e) {
			// expected
		}
	}
	
	@Test
	public void shouldNotFailIfColumnNotFoundOnTable() throws Exception {
		
		RemoveColumnManipulation manipulation = new RemoveColumnManipulation("foo", "bar", "baz");
		
		Table table = new Table(manipulation.getOriginalTableName());
		when(model.getTableWithName(manipulation.getOriginalTableName())).thenReturn(table);
		
		manipulation.updateModel(model);
			
	}
	
	@Test
	public void shouldNotFailIfTableNotfound() throws Exception {
		
		RemoveColumnManipulation manipulation = new RemoveColumnManipulation("foo", "bar", "baz");
				
		manipulation.updateModel(model);
	}
}
