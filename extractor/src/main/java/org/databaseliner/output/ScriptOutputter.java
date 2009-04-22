package org.databaseliner.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;
import org.databaseliner.extraction.model.TableName;

public class ScriptOutputter {

	private final String reportOutputFilename;
	private final String scriptOutputFilename;
	private final String outputDirectory;
	private final boolean preserveDatabaseIntegrity;
	
	private final static Logger LOG = Logger.getLogger(ScriptOutputter.class);

	public ScriptOutputter(String outputDirectory, String reportOutputFilename, String scriptOutputFilename, boolean preserveDatabaseIntegrity) {
		this.reportOutputFilename = reportOutputFilename;
		this.scriptOutputFilename = scriptOutputFilename;
		this.outputDirectory = outputDirectory;
		this.preserveDatabaseIntegrity = preserveDatabaseIntegrity;
	}

	public String getOutputFilename() {
		return scriptOutputFilename;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public boolean isPreserveDatabaseIntegrity() {
		return preserveDatabaseIntegrity;
	}

	public void output(ExtractionModel extractionModel) {
		outputReport(extractionModel);
		outputDataScript(extractionModel);
	}

	private void outputReport(ExtractionModel extractionModel) {
		FileWriter reportWriter = null;
		try {
			File reportFile = new File(outputDirectory, reportOutputFilename);
			reportFile.getParentFile().mkdirs();
			reportFile.createNewFile();
			reportWriter = new FileWriter(reportFile);
			HtmlReportOutputter htmlReportOutputter = new HtmlReportOutputter();
			
			htmlReportOutputter.writeReport(extractionModel, reportWriter);
			
		} catch (Exception e) {
			throw new RuntimeException("failed to output report file", e);
		} finally {
			if (reportWriter != null) {
				try {
					reportWriter.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
	
	private void outputDataScript(ExtractionModel extractionModel) {
		FileWriter scriptWriter = null;
		try {
			File outputFile = new File(outputDirectory, scriptOutputFilename);
			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
			scriptWriter = new FileWriter(outputFile);
			SqlStringOutputter sqlStringOutputter = extractionModel.getSqlStringOutputter();
			
			if (preserveDatabaseIntegrity) {
				outputAllTablesPreservingDatabaseIntegrity(extractionModel, scriptWriter, sqlStringOutputter);
			} else {
				outputAllTablesAsInsertStatements(extractionModel, scriptWriter, sqlStringOutputter);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("failed to output script file", e);
		} finally {
			if (scriptWriter != null) {
				try {
					scriptWriter.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	private void outputAllTablesAsInsertStatements(ExtractionModel extractionModel, FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter) {
		List<Table> allTables = extractionModel.getAllTables();
		
		for (Table table : allTables) {
			table.writeAsInsert(scriptWriter, sqlStringOutputter, outputDirectory);
		}
	}

	private void outputAllTablesPreservingDatabaseIntegrity(ExtractionModel extractionModel, FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter) {
		outputNonCyclicTables(extractionModel, scriptWriter, sqlStringOutputter);
		outputTablesInCycles(extractionModel, scriptWriter, sqlStringOutputter);
	}

	// structurally recurses all tables to output simple non cyclic tables
	private void outputNonCyclicTables(ExtractionModel extractionModel,	FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter) {
		
		List<Table> allTables = extractionModel.getAllTables();
		
		for (Table table : allTables) {
			outputTableAndDependencies(table, new ArrayList<Table>(), extractionModel, scriptWriter, sqlStringOutputter);
		}
		
	}

	public boolean outputTableAndDependencies(Table table, ArrayList<Table> dependentTables, ExtractionModel extractionModel,
			FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter) {
	
		// table might have been output already if a previously output table depended on it.
		if (table.isOutput()) {
			return true;
		}
		
		// cycle detection
		if (dependentTables.contains(table)) {
			LOG.debug("cycle detected. " + describeCycle(dependentTables, table));
			return false;
		}
		
		dependentTables.add(table);
		boolean allDependenciesAreOutput = true;
		for (TableName tableDependedUponLocator : table.getOutputDependencies().values()) {
			
			LOG.debug(String.format("table %s depends on %s. Outputting %s first", table.getName(), tableDependedUponLocator, tableDependedUponLocator));
			
			Table tableDependedUpon = extractionModel.getTableWithName(tableDependedUponLocator);
			if (tableDependedUpon != null && !tableDependedUpon.isOutput()) {
				// don't shortcut the "and" as we do want to output as much as possible.
				allDependenciesAreOutput = allDependenciesAreOutput & 
					outputTableAndDependencies(tableDependedUpon, dependentTables, extractionModel, scriptWriter, sqlStringOutputter);
			}
		}
		
		dependentTables.remove(table);
		
		if (allDependenciesAreOutput) {
			table.writeAsInsert(scriptWriter, sqlStringOutputter, outputDirectory);
			return true;
		}
		return false;
		
	}
	
	private void outputTablesInCycles(ExtractionModel extractionModel, FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter) {
		List<Table> unoutputTables = extractionModel.getUnoutputTables();
		while(!unoutputTables.isEmpty()) {
			
			Table breakCycleTable = getTableWhereAllUnoutputDependenciesNullable(unoutputTables, extractionModel);
			List<Column> columnsToMakeNull = getColumnsToMakeNull(breakCycleTable, extractionModel);
			
			LOG.debug("outputting " + breakCycleTable.getName() + " to break cycle");
			
			breakCycleTable.writeAsInsertWithNullColumns(columnsToMakeNull, scriptWriter, sqlStringOutputter, outputDirectory);
			outputNonCyclicTables(extractionModel, scriptWriter, sqlStringOutputter);
			
			outputUpdatesForTablesWithNulledFields(extractionModel, scriptWriter, sqlStringOutputter);
			
			unoutputTables = extractionModel.getUnoutputTables();
		}
	}

	
	private void outputUpdatesForTablesWithNulledFields(ExtractionModel extractionModel, FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter) {
		List<Table> allTables = extractionModel.getAllTables();
		
		for (Table table : allTables) {
			outputUpdatesIfAllNulledDependenciesSatisfied(table, extractionModel, scriptWriter, sqlStringOutputter);
		}
	}

	private void outputUpdatesIfAllNulledDependenciesSatisfied(Table table,	ExtractionModel extractionModel, 
			FileWriter scriptWriter, SqlStringOutputter sqlStringOutputter) {
		
		if (table.isNeedsUpdate() && areAllNulledDependenciesSatisfied(table, extractionModel)) {
			table.writeNulledFieldsAsUpdate(scriptWriter, sqlStringOutputter, outputDirectory);
		}
		
	}

	private boolean areAllNulledDependenciesSatisfied(Table table,	ExtractionModel extractionModel) {
		List<Column> nulledColumns = table.getNulledColumns();
		
		for (Column column : nulledColumns) {
			TableName dependedOnTableName = table.getOutputDependencies().get(column);
			Table dependedOnTable = extractionModel.getTableWithName(dependedOnTableName);
			if (!dependedOnTable.isOutput()) {
				return false;
			}
		}
		return true;
	}

	private Table getTableWhereAllUnoutputDependenciesNullable(List<Table> unoutputTables, ExtractionModel extractionModel) {
		for (Table table : unoutputTables) {
			if (areAllUnoutputDependenciesNullable(table, extractionModel)) {
				return table;
			}
		}
		return null;
	}

	private boolean areAllUnoutputDependenciesNullable(Table table, ExtractionModel extractionModel) {
		Map<Column, TableName> outputDependencies = table.getOutputDependencies();
		
		for (Column dependencyColumn : outputDependencies.keySet()) {
			TableName dependedOnTableName = outputDependencies.get(dependencyColumn);
			Table dependedOnTable = extractionModel.getTableWithName(dependedOnTableName);
			
			if (dependedOnTable != null && !dependedOnTable.isOutput() && !dependencyColumn.isNullable()) {
				return false;
			}
		}
		
		return true;
	}
	
	private List<Column> getColumnsToMakeNull(Table table, ExtractionModel extractionModel) {
		ArrayList<Column> columnsToMakeNull = new ArrayList<Column>();
		Map<Column, TableName> outputDependencies = table.getOutputDependencies();
		
		for (Column dependencyColumn : outputDependencies.keySet()) {
			TableName dependedOnTableName = outputDependencies.get(dependencyColumn);
			Table dependedOnTable = extractionModel.getTableWithName(dependedOnTableName);
			
			if (dependedOnTable != null && !dependedOnTable.isOutput()) {
				columnsToMakeNull.add(dependencyColumn);
			}
		}
		
		return columnsToMakeNull;
	}

	private String describeCycle(ArrayList<Table> cycle, Table endTable) {
		StringBuilder cycleString = new StringBuilder();
		for (Table table : cycle) {
			cycleString.append(table.getName()).append(" -> ");
		}
		cycleString.append(endTable.getName());
		return cycleString.toString();
	}

}
