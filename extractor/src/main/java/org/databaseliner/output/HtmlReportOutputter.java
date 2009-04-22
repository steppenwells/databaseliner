package org.databaseliner.output;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.databaseliner.extraction.Relationship;
import org.databaseliner.extraction.model.Column;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.Table;

public class HtmlReportOutputter {

	private static final String HEADER_FORMAT = 
			"<html>" +
				"<head>" +
					"<title>dataBaseliner report</title>" +
				"</head>" +
			"<body>" +
			"<h1>Report for dataBaseliner run at %1</h1>\n";
	
	private static final String FOOTER = "</body></html>";

	private static final String TABLE_HEADER = "<h2 id=\"%s\">%s</h2>";

	public void writeReport(ExtractionModel extractionModel, FileWriter reportWriter) throws IOException {
		writeHeader(reportWriter);
		writeTableReports(extractionModel, reportWriter);
		writeFooter(reportWriter);
	}

	private void writeHeader(FileWriter reportWriter) throws IOException {
		reportWriter.write(String.format(HEADER_FORMAT, new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date())));
	}

	private void writeTableReports(ExtractionModel extractionModel,	FileWriter reportWriter) throws IOException {
		List<Table> sortedListOfAllTables = getSortedListOfAllTables(extractionModel);
		
		for (Table table : sortedListOfAllTables) {
			String tableReport = generateReportForTable(table);
			reportWriter.write(tableReport);
		}
		
	}

	private String generateReportForTable(Table table) {
		StringBuilder tableReport = new StringBuilder();
		tableReport.append(String.format(TABLE_HEADER, table.getName().getHtmlIdSafeName(), table.getName()));
		tableReport.append("<p>Extracted ").append(table.getRows().size()).append(" rows</p>\n");
		tableReport.append("<p>table columns:</p>\n");
		tableReport.append("<ul>\n");
		tableReport.append(generateColumnsList(table));
		tableReport.append("</ul>\n");
		tableReport.append("<p>table relationships:</p>\n");
		tableReport.append("<ul>\n");
		tableReport.append(generateRelationshipList(table));
		tableReport.append("</ul>\n");
		
		return tableReport.toString();
		
	}

	private String generateColumnsList(Table table) {
		StringBuilder columnList = new StringBuilder();
		
		for (Column column : table.getColumns()) {
			columnList.append("<li>").append(column).append("</li>\n");
		}
		
		return columnList.toString();
	}
	
	private String generateRelationshipList(Table table) {
		StringBuilder relationshipList = new StringBuilder();
		
		for (Relationship relationship : table.getForeignKeyRelationships()) {
			relationshipList.append("<li>").append(relationship).append("</li>\n");
		}
		
		for (Relationship relationship : table.getRelationshipsSeededFromThisTable()) {
			relationshipList.append("<li>").append(relationship).append("</li>\n");
		}
		
		return relationshipList.toString();
	}

	private List<Table> getSortedListOfAllTables(ExtractionModel extractionModel) {
		List<Table> allTables = extractionModel.getAllTables();
		Collections.sort(allTables, new Comparator<Table>() {

			@Override
			public int compare(Table table1, Table table2) {
				return 	table1.getName().toString()
					.compareTo(
						table2.getName().toString());
			}
			
		});
		
		return allTables;
	}
	
	private void writeFooter(FileWriter reportWriter) throws IOException{
		reportWriter.write(FOOTER);
	}

}
