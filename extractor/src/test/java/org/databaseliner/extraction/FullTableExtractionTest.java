package org.databaseliner.extraction;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class FullTableExtractionTest {
	
	private FullTableExtraction extraction;
	@Mock DatabaseConnector databaseConnector;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
		extraction = new FullTableExtraction("schema", "table");
	}
	
	@Test public void shouldGenerateSelectExtractionSqlUsingNameAndSchema() {
		extraction.extract(databaseConnector);
		verify(databaseConnector).processSqlAndAddResultsToTable(eq("SELECT * FROM schema.table"), (Table)anyObject());
	}
	
	@Test public void shouldFillTableAddedByModel() {
		Table seededTable = new Table(new TableName("schema", "table"));
		extraction.setTableToFill(seededTable);
		
		extraction.extract(databaseConnector);
		verify(databaseConnector).processSqlAndAddResultsToTable(anyString(), eq(seededTable));
	}

}
