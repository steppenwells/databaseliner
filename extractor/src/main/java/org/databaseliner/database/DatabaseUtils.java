package org.databaseliner.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseUtils {
		
	public static void close(PreparedStatement preparedStatement) {
		try {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		} catch (Exception e) {
			// can't do anything here
		}
	}
	
	public static void close(ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (Exception e) {
			// can't do anything here
		}
	}
	
}
