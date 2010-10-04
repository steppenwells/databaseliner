package org.databaseliner;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.database.XMLConfigDatabaseConnector;
import org.databaseliner.extraction.model.ExtractionModel;
import org.databaseliner.extraction.parser.ExtractionPlanParser;
import org.databaseliner.manipulation.ManipulationService;
import org.databaseliner.manipulation.parser.ManipulationConfigParser;
import org.databaseliner.output.ScriptOutputter;
import org.databaseliner.output.ScriptOutputterConfigParser;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class Extractor {

	private ExtractionModel extractionModel;
	private ManipulationService manipulationService;
	private ScriptOutputter outputter;

	public Extractor(String configFilename) {
		Document configDocument = getDocumentFromFile(configFilename);
		DatabaseConnector databaseConnector = new XMLConfigDatabaseConnector(configDocument);
		outputter = ScriptOutputterConfigParser.parse(configDocument);
		manipulationService = ManipulationConfigParser.parse(configDocument);
		extractionModel = ExtractionPlanParser.parse(configDocument, databaseConnector); 
	}

	private void extract() {
		extractionModel.extract();
		manipulationService.updateModel(extractionModel);
		outputter.output(extractionModel);
	}

	private Document getDocumentFromFile(String filename) {
		try {
			SAXReader reader = new SAXReader();
	        Document document = reader.read(filename);
	        return document;
		} catch (Exception e) {
			throw new RuntimeException("can't read configuration " + filename, e);
		}
	}
	
	/**
	 * usage: Extractor [config_filename]
	 */
	public static void main(String[] args) {

    	Extractor extractor = null;
        if (args.length != 1) {
        	System.err.println("usage: Extractor [config_filename]");
        } else {
        	extractor = new Extractor(args[0]);
            extractor.extract();
            System.exit(extractor.extractionModel.failed() ? 1 : 0);
        }
	}
}
