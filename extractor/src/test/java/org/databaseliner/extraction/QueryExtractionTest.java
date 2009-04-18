package org.databaseliner.extraction;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class QueryExtractionTest {

	private QueryExtraction extraction;
	@Mock DatabaseConnector databaseConnector;
	
	@Before public void setup() {
		MockitoAnnotations.initMocks(this);
		extraction = new QueryExtraction(null, null, null, null, null);
	}
	
	@Test public void shouldGenerateSelectExtractionSqlWhenJustTableAndWhereClauseProvided() {
		
		extraction = new QueryExtraction("schema", "table", null, null, "expected clause");
		extraction.extract(databaseConnector);
		verify(databaseConnector).processSqlAndAddResultsToTable(eq("SELECT * FROM schema.table WHERE expected clause"), (Table)anyObject());
	}
	
	@Test public void shouldGenerateSelectExtractionSqlUsingWhereAndFromClausesProvided() {
		
		extraction = new QueryExtraction(null, null, "t", "table t, other o", "expected clause");
		extraction.extract(databaseConnector);
		verify(databaseConnector).processSqlAndAddResultsToTable(eq("SELECT t.* FROM table t, other o WHERE expected clause"), (Table)anyObject());
	}
	
	@Test public void shouldFillTableAddedByModel() {
		Table seededTable = new Table(new TableName("schema", "table"));
		extraction.setTableToFill(seededTable);
		
		extraction.extract(databaseConnector);
		verify(databaseConnector).processSqlAndAddResultsToTable(anyString(), eq(seededTable));
	}
}
