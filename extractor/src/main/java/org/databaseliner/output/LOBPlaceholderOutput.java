package org.databaseliner.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class LOBPlaceholderOutput implements SQLOutputter {


	@Override
	public String asSqlString(Object fieldObject) {
		throw new UnsupportedOperationException("unable to output " + fieldObject + " as inline sql");
	}

	@Override
	public boolean shouldOutputPlaceholder() {
		return true;
	}

	@Override
	public String asPlaceholder(String tableName, String columnName, String rowSelector, Object fieldObject, String outputDirectory) {
		try {
			
			OutputStream fileOutput = getOutputStreamForDataFile(outputDirectory, tableName, columnName, rowSelector);
	        
	        writeToFile(fieldObject, fileOutput);
	      
	        fileOutput.flush();
			fileOutput.close();
			return "'placeholder'";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	protected abstract void writeToFile(Object placeheldObject, OutputStream fileOutput);

	protected OutputStream getOutputStreamForDataFile(String outputDirectory, String tableName, String columnName, String uniqueRowSelector) throws IOException {
		StringBuffer filePath = new StringBuffer();
		filePath.append(outputDirectory).append(File.separator);
		filePath.append(tableName).append(File.separator);
		filePath.append(columnName).append(File.separator);
		filePath.append(uniqueRowSelector).append(".data");
		File outputFile = new File(filePath.toString());
		outputFile.getParentFile().mkdirs();
		outputFile.createNewFile();
		FileOutputStream fileOutput = new FileOutputStream(outputFile);
		return fileOutput;
	}
}
