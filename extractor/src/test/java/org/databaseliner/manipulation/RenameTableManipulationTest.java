package org.databaseliner.manipulation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RenameTableManipulationTest {
	
	@Mock private ExtractionModel model;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldRenameTableInModel() throws Exception {
	
		RenameTableManipulation manipulation = new RenameTableManipulation("foo", "bar", "baz");
		
		TableName originalTableName = manipulation.getOriginalTableName();
		TableName newTableName = new TableName("baz", null);

		Table table = new Table(originalTableName);
		when(model.getTableWithName(originalTableName)).thenReturn(table);
		
		manipulation.updateModel(model);
		
		assertEquals(newTableName, table.getName());
		verify(model).registerTableWithNewName(table, originalTableName, newTableName);
	}
	
	@Test
	public void shouldNotFailIfTableCantBeFound() throws Exception {
		
		RenameTableManipulation manipulation = new RenameTableManipulation("foo", "bar", "baz");
		
		when(model.getTableWithName(manipulation.getOriginalTableName())).thenReturn(null);
		
		manipulation.updateModel(model);
		
	}

}
