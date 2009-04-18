package org.databaseliner.test;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;

public class XMLParsingTest {

	protected Document getDocument(String xmlString) throws Exception {
		SAXReader reader = new SAXReader();
		
	    Document document = reader.read(new StringReader(xmlString));
	    return document;
	}

	protected Node getNode(String selector, String xml) throws Exception {
		return getDocument(xml).selectSingleNode(selector);
	}

	@Test
	public void whyIsthisNeeded() throws Exception {
		// I suspect the wrong runner is configured in maven or somesuch
	}
}
