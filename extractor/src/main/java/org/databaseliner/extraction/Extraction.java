package org.databaseliner.extraction;

import java.util.ArrayList;
import java.util.List;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.model.TableName;
import org.dom4j.Document;
import org.dom4j.Node;

// this is a manipulatable version of the extraction plan to be used by the GUI.
public class Extraction {
	
	private ExtractionModel extractionModel;

	public Extraction(Document configDocument, DatabaseConnector databaseConnector) {
		
		
		List<SeedExtraction> seeds = parseSeedExtractions(configDocument);
		List<TableName> ignoredTableNames = parseIgnoredTables(configDocument);
		List<Relationship> relationships = parseRelationships(configDocument);
		
		extractionModel = new ExtractionModel(databaseConnector, seeds, ignoredTableNames, relationships, false);
		extractionModel.build();
	}

	@SuppressWarnings("unchecked")
	private List<SeedExtraction> parseSeedExtractions(Document configDocument) {

		List<SeedExtraction> seeds = new ArrayList<SeedExtraction>();
		
		List<Node> selectNodes = configDocument.selectNodes("//databaseliner/extractionPlan/extraction");
		for (Node extractionNode : selectNodes) {
			seeds.add(ExtractionType.getExtractionForNode(extractionNode));
		}
		
		return seeds;
	}
	
	@SuppressWarnings("unchecked")
	private List<TableName> parseIgnoredTables(Document configDocument) {
		
		List<TableName> ignoredTableNames = new ArrayList<TableName>();
		
		List<Node> selectNodes = configDocument.selectNodes("//databaseliner/extractionPlan/ignore");
		for (Node node : selectNodes) {
			
			Node schemaNode = node.selectSingleNode("@seedSchema");
			String schemaName = schemaNode != null ? schemaNode.getText().trim() : null;
			
			String tableName = node.selectSingleNode("@table").getText().trim();
			ignoredTableNames.add(new TableName(tableName, schemaName));
		}
		return ignoredTableNames;
	}
	
	@SuppressWarnings("unchecked")
	private List<Relationship> parseRelationships(Document configDocument) {
		List<Relationship> relationships = new ArrayList<Relationship>();
		
		List<Node> selectNodes = configDocument.selectNodes("//databaseliner/extractionPlan/relationship");
		for (Node extractionNode : selectNodes) {
			relationships.add(RelationshipType.getRelationshipForNode(extractionNode));
		}
		
		return relationships;
	}

}
