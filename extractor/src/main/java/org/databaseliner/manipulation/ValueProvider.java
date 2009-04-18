package org.databaseliner.manipulation;

import org.databaseliner.extraction.model.Row;

public interface ValueProvider {

	Object getValue(Row row);

}
