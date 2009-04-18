package org.databaseliner.manipulation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.Table.ColumnMissingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RenameColumnManipulationTest {

	@Mock private ExtractionModel model;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldRenameSpecifiedColumnInTable() throws Exception {
			
		RenameColumnManipulation manipulation = new RenameColumnManipulation("foo", "bar", "baz", "newname");
			
		Table table = new Table(manipulation.getOriginalTableName());
		Column columnToRename = new Column("baz", false);
		table.addColumn(columnToRename);
		when(model.getTableWithName(manipulation.getOriginalTableName())).thenReturn(table);
		
		manipulation.updateModel(model);
		
		assertNotNull(table.getColumnWithName("newname"));
		try {
			// this line throws exception when the column is missing
			table.getColumnWithName("baz");
			fail("expected column missing excepion");
		} catch (ColumnMissingException e) {
			// expected
		}
	}
	
	@Test
	public void shouldNotFailIfColumnNotFoundOnTable() throws Exception {
		
		RenameColumnManipulation manipulation = new RenameColumnManipulation("foo", "bar", "baz", "newname");
		
		Table table = new Table(manipulation.getOriginalTableName());
		when(model.getTableWithName(manipulation.getOriginalTableName())).thenReturn(table);
		
		manipulation.updateModel(model);
			
	}
	
	@Test
	public void shouldNotFailIfTableNotfound() throws Exception {
		
		RenameColumnManipulation manipulation = new RenameColumnManipulation("foo", "bar", "baz", "newname");
				
		manipulation.updateModel(model);
	}
}
