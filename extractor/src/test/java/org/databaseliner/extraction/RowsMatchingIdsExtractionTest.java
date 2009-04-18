package org.databaseliner.extraction;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class RowsMatchingIdsExtractionTest {
	
	private RowsMatchingIdsExtraction extraction;
	@Mock DatabaseConnector databaseConnector;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
		extraction = new RowsMatchingIdsExtraction("schema", "table", "column", "i,d,s");
	}
	
	@Test public void shouldGenerateSelectExtractionSqlUsingInputData() {
		extraction.extract(databaseConnector);
		verify(databaseConnector).processSqlAndAddResultsToTable(eq("SELECT * FROM schema.table WHERE column IN (i,d,s)"), (Table)anyObject());
	}
	
	@Test public void shouldFillTableAddedByModel() {
		Table seededTable = new Table(new TableName("schema", "table"));
		extraction.setTableToFill(seededTable);
		
		extraction.extract(databaseConnector);
		verify(databaseConnector).processSqlAndAddResultsToTable(anyString(), eq(seededTable));
	}
}
