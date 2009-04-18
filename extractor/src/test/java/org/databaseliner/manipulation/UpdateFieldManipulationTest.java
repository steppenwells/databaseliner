package org.databaseliner.manipulation;

import static org.mockito.Mockito.when;

import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateFieldManipulationTest {

	@Mock private ExtractionModel model;
	
	private UpdateFieldManipulation manipulation;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
		manipulation = new UpdateFieldManipulation("foo", "bar", "baz");
	}
		
	@Test
	public void shouldNotFailIfColumnNotFoundOnTable() throws Exception {
		
		Table table = new Table(manipulation.getOriginalTableName());
		when(model.getTableWithName(manipulation.getOriginalTableName())).thenReturn(table);
		
		manipulation.updateModel(model);
			
	}
	
	@Test
	public void shouldNotFailIfTableNotfound() throws Exception {
		
		UpdateFieldManipulation manipulation = new UpdateFieldManipulation("foo", "bar", "baz");
				
		manipulation.updateModel(model);
	}
}
