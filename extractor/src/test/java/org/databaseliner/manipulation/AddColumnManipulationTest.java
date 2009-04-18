package org.databaseliner.manipulation;

import static org.mockito.Mockito.when;

import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AddColumnManipulationTest {

@Mock private ExtractionModel model;
	
	private AddColumnManipulation manipulation;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
		manipulation = new AddColumnManipulation("foo", "bar", "baz");
	}
		
	@Test
	public void shouldNotFailIfColumnFoundOnTable() throws Exception {
		
		Table table = new Table(manipulation.getOriginalTableName());
		table.addColumn(new Column("baz", true));
		when(model.getTableWithName(manipulation.getOriginalTableName())).thenReturn(table);
		
		manipulation.updateModel(model);
			
	}
	
	@Test
	public void shouldNotFailIfTableNotfound() throws Exception {
		
		UpdateFieldManipulation manipulation = new UpdateFieldManipulation("foo", "bar", "baz");
				
		manipulation.updateModel(model);
	}
}
