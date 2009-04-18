package org.databaseliner.manipulation;

import java.util.ArrayList;
import java.util.List;

import org.databaseliner.extraction.model.ExtractionModel;

public class ManipulationService {
	
	private final List<Manipulation> tableManipulations;
	private final List<Manipulation> columnManipulations;
	private final List<Manipulation> fieldManipulations;
	
	public ManipulationService() {
		tableManipulations = new ArrayList<Manipulation>();
		columnManipulations = new ArrayList<Manipulation>();
		fieldManipulations = new ArrayList<Manipulation>();
	}

	public List<Manipulation> getTableManipulations() {
		return tableManipulations;
	}

	public List<Manipulation> getColumnManipulations() {
		return columnManipulations;
	}

	public List<Manipulation> getFieldManipulations() {
		return fieldManipulations;
	}

	public void add(Manipulation manipulation) {
		switch (manipulation.getManipulationScope()) {
		case TABLE:
			tableManipulations.add(manipulation);
			break;
		case COLUMN:
			columnManipulations.add(manipulation);
			break;
		case FIELD:
			fieldManipulations.add(manipulation);
			break;
		default:
			break;
		}
		
	}

	public void updateModel(ExtractionModel extractionModel) {
		processManipulations(fieldManipulations, extractionModel);
		processManipulations(columnManipulations, extractionModel);
		processManipulations(tableManipulations, extractionModel);
	}

	private void processManipulations(List<Manipulation> manipulations, ExtractionModel extractionModel) {
		for (Manipulation manipulation : manipulations) {
			manipulation.updateModel(extractionModel);
		}
		
	}

}
