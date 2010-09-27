package org.databaseliner.dialect.oracle.fieldProcessors;

import oracle.sql.CLOB;
import org.databaseliner.manipulation.FieldProcessor;

import java.io.Reader;

public class ShortCLOBToStringProcessor implements FieldProcessor {

	@Override
	public Object getFieldValue(Object inputValue) {
		
		if (inputValue instanceof CLOB) {
			CLOB clob = (CLOB) inputValue;
			try {
				if (clob.length() < 1500) {
					
					return getClobAsString(clob);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// do nothing, we'll just return the clob upwards.
			}
			
		}
		
		return inputValue;
	}

	private String getClobAsString(CLOB clob) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		Reader reader = clob.getCharacterStream();
		
        char[] buf = new char[256];
        int read = 0;
        while ((read = reader.read(buf)) > 0) {
            stringBuilder.append(new String(buf, 0, read));
        }
        
        return stringBuilder.toString();
	}

}
