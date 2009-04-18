package org.databaseliner.output;

import org.dom4j.Document;
import org.dom4j.Node;

/**
 * 
 * <p>Configures the output from databaseliner from the configuration XML:</p>
 * 
 * <p>
 * <code>
 * &lt;databaseliner&gt;<br/>
 *	<br/>
 *	...<br/>
 *	<br/>
 *	&lt;outputDetails&gt;<br/>
 *		&lt;outputDirectory&gt;directoryName&lt;/outputDirectory&gt;<br/>
 *		&lt;script&gt;outputFileName&lt;/script&gt;<br/>
 *		&lt;preserveDatabaseIntegrity&gt;boolean&lt;/preserveDatabaseIntegrity&gt;<br/>
 *	&lt;/outputDetails&gt;<br/>
 *	<br/>
 * 	...<br/>
 * 	<br/>
 * &lt;/databaseliner&gt;
 * </code>
 * </p>
 * 
 * <p>The <code>outputDirectory</code> element defines the directory where databaseliner outputs the 
 * insert script and any LOB data files. LOB data files will be output in a directory structure that 
 * reflects what where the LOB is to be inserted, this structure can be examined by the LOB insertion 
 * ant task and standalone tool to add the LOB to the correct place in the baselined schema.</p>
 * 
 * <p>The <code>outputDirectory</code> element is a required field and can be a path relative to where
 * databaseliner runs or an absolute path depending on how it is declared.</p>
 * 
 * <p>For more information refer to @see LOBInsertionTool.</p>
 * 
 * <p>The <code>script</code> element defines the filename of the main data insertion script output
 * by databaseliner. This is a required field.</p>
 * 
 * <p>The <code>preserveDatabaseIntegrity</code> element defines whether the output script preserves
 * database integrity as the script runs.</p>
 * 
 * <p>Setting this to <code>false</code> will cause the script to produce only <code>INSERT</code> 
 * statements and not order inserts by relationships. This mode can only be used if you plan to disable
 * foreign key referential integrity before inserting data and enabling this after the inserts. The 
 * advantages of this mode are:</p>
 * <ul>
 * 	<li>The output script is smaller.</li>
 * 	<li>Inserting into the database is faster as each insert does not have to be checked for integrity.
 * 		Re-enabling foreign key integrity checking will check everything but is in a single smaller operation.</li>
 * 	<li>Creating the output script is more efficient in this mode.</li>
 * </ul>
 * 
 * <p>Setting this to <code>true</code> will cause the script to produce <code>INSERT</code> and 
 * <code>UPDATE</code> statements and will order the statements so tables are only output if all the
 * tables they depend on are already output. If cycles are detected (I.E. table_a depends on table_b, 
 * and table_b depends on table_a) then one of the foreign key fields is output as <code>NULL</code>
 * (obviously only if the field is actually nullable) and then the <code>NULL</code> field is populated
 * using an <code>UPDATE</code> statement once the referenced table is output. The advantages of this
 * mode are:</p>
 * <ul>
 * 	<li>Database integrity is maintained at all times during the script run</li>
 * 	<li>There is no need to run scripts to enable and disable foreign key checking before and after 
 * 		running the script.</li>
 * </ul>
 *
 * <p>If your database has no foreign keys then both modes are equivalent.</p>
 * 
 * <p>The <code>preserveDatabaseIntegrity</code> element is not required and defaults to <code>true</code></p>
 */
public class ScriptOutputterConfigParser {

	public static ScriptOutputter parse(Document configDocument) {
		
		Node outputDirectoryNode = configDocument.selectSingleNode("//databaseliner/outputDetails/outputDirectory");
		String outputDirectory = outputDirectoryNode.getText().trim();
		
		Node scriptFilenameNode = configDocument.selectSingleNode("//databaseliner/outputDetails/script");
		String outputFilename = scriptFilenameNode.getText().trim();
		
		Node preserveDatabaseIntegrityNode = configDocument.selectSingleNode("//databaseliner/outputDetails/preserveDatabaseIntegrity");
		boolean preserveDatabaseIntegrity = true;
		if (preserveDatabaseIntegrityNode != null) {
			preserveDatabaseIntegrity = Boolean.parseBoolean(preserveDatabaseIntegrityNode.getText().trim());
		}
		
		return new ScriptOutputter(outputDirectory, outputFilename, preserveDatabaseIntegrity);
	}

}
