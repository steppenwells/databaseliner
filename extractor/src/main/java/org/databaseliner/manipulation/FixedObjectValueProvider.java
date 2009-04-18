package org.databaseliner.manipulation;

import org.databaseliner.extraction.model.Row;

public class FixedObjectValueProvider implements ValueProvider {

	private final Object fixedObject;

	public FixedObjectValueProvider(Object object) {
		this.fixedObject = object;
	}

	@Override
	public Object getValue(Row row) {
		return fixedObject;
	}

}
