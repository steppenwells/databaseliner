package org.databaseliner.placeholderloader;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;


public class StringPlaceholderLoaderAntTask extends Task {

	private String dbDriver;
	private String dbUser;
	private String dbPassword;
	private String dbLocation;
	
	private Connection connection;
	
	private List<FileSet> filesets = new ArrayList<FileSet>();

	public void setDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public void setUrl(String dbLocation) {
		this.dbLocation = dbLocation;
	}

	public void setPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public void setUserid(String dbUser) {
		this.dbUser = dbUser;
	}
	
	public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }
	
	public void execute() throws BuildException {
		try {
			for (FileSet fileset : filesets) {
				
				DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
				String[] files = ds.getIncludedFiles();
				for(int i=0; i<files.length; i++) {
					File dataFile = new File(ds.getBasedir(), files[i]);
					processFile(dataFile);
				}
				log("updated " + files.length + " entries");
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
    }

	private void processFile(File dataFile) throws Exception {
		String sql = buildUpdateSql(dataFile);
		
		PreparedStatement updateStatement = getConnection().prepareStatement(sql.toString());
		try {
			updateStatement.setString(1, fileAsString(dataFile));
			updateStatement.executeUpdate();
		} catch (Exception e) {
			System.err.println("error updating using sql: " + sql.toString());
			e.printStackTrace();
			throw e;
		} finally {
			updateStatement.close();
		}
	}

	private String fileAsString(File dataFile) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		InputStream inputStream = new FileInputStream(dataFile);
		
        byte[] buf = new byte[256];
        int read = 0;
        while ((read = inputStream.read(buf)) > 0) {
            stringBuilder.append(new String(buf, 0, read, "UTF-8"));
        }
        
        inputStream.close();
        return stringBuilder.toString();
	}

	private String buildUpdateSql(File dataFile) throws IOException {
		String[] fileParts = dataFile.getCanonicalPath().replace(File.separatorChar, '/').split("/");
		String fileName = fileParts[fileParts.length-1];
		String field = fileParts[fileParts.length-2];
		String table = fileParts[fileParts.length-3];
		
		String whereClause = buildWhereClause(fileName);
		
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ").append(table);
		sql.append(" SET ").append(field).append(" = ?");
		sql.append(" WHERE ").append(whereClause);
		return sql.toString();
	}

	private String buildWhereClause(String fileName) {
		String whereData = fileName.substring(0, fileName.lastIndexOf('.'));
		String[] pkStrings = whereData.split("&");
		StringBuffer whereClause = new StringBuffer();
		String whereDelimiter = ""; 
		for (int i = 0; i < pkStrings.length; i++) {
			String[] pkParts = pkStrings[i].split("=");
			String pkColumn = pkParts[0];
			String pkValue = pkParts[1];
			whereClause.append(whereDelimiter).append(pkColumn).append(" = ").append(pkValue);
			whereDelimiter = " AND ";
		}
		return whereClause.toString();
	}
	
	private Connection getConnection() throws Exception {
		if (connection == null) {
			Class.forName(dbDriver);
			connection = DriverManager.getConnection(dbLocation, dbUser, dbPassword);
		}
		return connection;
	}
}
