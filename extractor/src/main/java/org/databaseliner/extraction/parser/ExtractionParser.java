package org.databaseliner.extraction.parser;

import org.databaseliner.extraction.SeedExtraction;
import org.dom4j.Node;

public interface ExtractionParser {

	SeedExtraction parse(Node extractionNode);

}
