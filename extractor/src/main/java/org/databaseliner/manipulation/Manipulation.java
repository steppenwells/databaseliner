package org.databaseliner.manipulation;

import org.databaseliner.extraction.model.ExtractionModel;

public interface Manipulation {

	public enum ManipulationScope {
		TABLE,
		COLUMN,
		FIELD;
	}

	public ManipulationScope getManipulationScope();

	public void updateModel(ExtractionModel extractionModel);
}
