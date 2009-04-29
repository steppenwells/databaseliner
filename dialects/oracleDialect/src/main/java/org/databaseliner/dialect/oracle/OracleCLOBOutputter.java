package org.databaseliner.dialect.oracle;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import oracle.sql.CLOB;

import org.databaseliner.output.LOBPlaceholderOutput;

public class OracleCLOBOutputter extends LOBPlaceholderOutput {

	@Override
	protected void writeToFile(Object placeheldObject, OutputStream fileOutput) {
		try {
			CLOB clob = (CLOB) placeheldObject;
			Reader reader = clob.getCharacterStream();
			
	        char[] buf = new char[256];
	        int read = 0;
	        while ((read = reader.read(buf)) > 0) {
	        	
	        	String string = new String(buf, 0, read);
	            fileOutput.write(string.getBytes("UTF-8"));
	        }
		} catch (Exception e) {
			throw new RuntimeException("could not output CLOB", e);
		}
	}
}
