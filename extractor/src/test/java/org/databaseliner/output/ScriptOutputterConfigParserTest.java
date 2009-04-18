package org.databaseliner.output;

import java.io.StringReader;

import org.databaseliner.test.XMLParsingTest;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;

public class ScriptOutputterConfigParserTest extends XMLParsingTest {
	
	private static final String FULL_CONFIG_XML = "<databaseliner><outputDetails>\n" +
			"<outputDirectory>directoryName</outputDirectory>\n" +
			"<script>outputFileName</script>\n" +
			"<preserveDatabaseIntegrity>false</preserveDatabaseIntegrity>\n" +
			"</outputDetails></databaseliner>";
	
	private static final String CONFIG_WITHOUT_OPTIONAL_FIELDS = "<databaseliner><outputDetails>\n" +
		"<outputDirectory>directoryName</outputDirectory>\n" +
		"<script>outputFileName</script>\n" +
		"</outputDetails></databaseliner>";
	
	@Test public void shouldParseFileName() throws Exception {
		ScriptOutputter scriptOutputter = ScriptOutputterConfigParser.parse(getDocument(FULL_CONFIG_XML));
		Assert.assertEquals("outputFileName", scriptOutputter.getOutputFilename());
	}
	
	@Test public void shouldParseOutputDirectory() throws Exception {
		ScriptOutputter scriptOutputter = ScriptOutputterConfigParser.parse(getDocument(FULL_CONFIG_XML));
		Assert.assertEquals("directoryName", scriptOutputter.getOutputDirectory());
	}
	
	@Test public void shouldParsePreserveIntregritySetting() throws Exception {
		ScriptOutputter scriptOutputter = ScriptOutputterConfigParser.parse(getDocument(FULL_CONFIG_XML));
		Assert.assertFalse("preserveDatabaseIntegrity should be set to false", scriptOutputter.isPreserveDatabaseIntegrity());
	}

	@Test public void shouldDefaultPreserveIntregritySettingToTrueIfNodeIsMissing() throws Exception {
		ScriptOutputter scriptOutputter = ScriptOutputterConfigParser.parse(getDocument(CONFIG_WITHOUT_OPTIONAL_FIELDS));
		Assert.assertTrue("preserveDatabaseIntegrity should be set to true", scriptOutputter.isPreserveDatabaseIntegrity());
	}

}
