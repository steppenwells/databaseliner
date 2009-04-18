package org.databaseliner.dialect.oracle;

import java.io.InputStream;
import java.io.OutputStream;

import oracle.sql.CLOB;

import org.databaseliner.output.LOBPlaceholderOutput;

public class OracleCLOBOutputter extends LOBPlaceholderOutput {

	@Override
	protected void writeToFile(Object placeheldObject, OutputStream fileOutput) {
		try {
			CLOB clob = (CLOB) placeheldObject;
			InputStream inputStream = clob.getAsciiStream();
			
	        byte[] buf = new byte[256];
	        int read = 0;
	        while ((read = inputStream.read(buf)) > 0) {
	            fileOutput.write(buf, 0, read);
	        }
		} catch (Exception e) {
			throw new RuntimeException("could not output CLOB", e);
		}
	}
}
