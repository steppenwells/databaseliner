package org.databaseliner.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.databaseliner.dialect.Dialect;
import org.databaseliner.extraction.model.Table;
import org.dom4j.Document;
import org.dom4j.Node;

public class XMLConfigDatabaseConnector implements DatabaseConnector {

	private final static Logger LOG = Logger.getLogger(XMLConfigDatabaseConnector.class);
	private Connection connection;
	private Dialect dialect;

	public XMLConfigDatabaseConnector() {
		// do nothing used by the ui.
	}
	
	public XMLConfigDatabaseConnector(Document configDoc) {
			Node driverNode = configDoc.selectSingleNode("//databaseliner/databaseConnection/driver");
			String driverClass = driverNode.getText().trim();
			Node urlNode = configDoc.selectSingleNode("//databaseliner/databaseConnection/connectionUrl");
			String url = urlNode.getText().trim();
			Node userNode = configDoc.selectSingleNode("//databaseliner/databaseConnection/user");
			String user = userNode.getText().trim();
			Node passwordNode = configDoc.selectSingleNode("//databaseliner/databaseConnection/password");
			String password = passwordNode.getText().trim();
			
			establishConnection(driverClass, url, user, password);
			
			Node dialectNode = configDoc.selectSingleNode("//databaseliner/databaseConnection/dialect");
			String dialectClass = dialectNode.getText().trim();
			
			useDialect(dialectClass);
	}
	
	protected void establishConnection(String driverClass, String databaseUrl, String username, String password) {
		try {
			LOG.debug("establishing connection to " + databaseUrl);
			
			Class.forName(driverClass);
			connection = DriverManager.getConnection(databaseUrl, username, password);
			
			LOG.debug("established connection successfully");			
			
		} catch (Exception e) {
			LOG.error("failed to establish connection", e);
			throw new UnsupportedOperationException("database connection configuration invalid", e);
		}
	}
	
	protected void useDialect(String dialectClass) {
		try {
			LOG.debug("loading dialect");
			dialect = (Dialect) Class.forName(dialectClass).newInstance();
		} catch (Exception e) {
			LOG.error("failed to load dialect", e);
			throw new UnsupportedOperationException("dialect configuration invalid", e);
		}
	}
	
	@Override
	public DatabaseMetaData getDatabaseMetaData() {
		try {
			return connection.getMetaData();
		} catch (SQLException e) {
			LOG.error("error getting database metadata", e);
			throw new RuntimeException("error getting database metadata", e);
		}
	}

	@Override
	public PreparedStatement getPreparedStatement(String sql) {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			LOG.error("error getting prepared statement", e);
			throw new RuntimeException("error getting prepared statement", e);
		}
	}
	
	@Override
	public void processSqlAndAddResultsToTable(String sql, Table tableToFill) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			LOG.debug(String.format("extracting data for %s using sql %s", tableToFill.getName(), sql));
			int loadedCount = 0;
			long startMillis = new Date().getTime();
			
			preparedStatement = getPreparedStatement(sql);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				tableToFill.processRow(resultSet);
				loadedCount++;
			}
			
			long endMillis = new Date().getTime();
			LOG.debug(String.format("extracted %d rows in %dms", loadedCount, endMillis - startMillis));
			
		} catch (Exception e) {
			LOG.error("Failed to extract seed data using sql " + sql, e);
			throw new RuntimeException("Failed to extract seed data using sql " + sql, e);
		} finally {
			DatabaseUtils.close(preparedStatement);
			DatabaseUtils.close(resultSet);
		}
	}

	public Dialect getDialect() {
		return dialect;
	}
}
