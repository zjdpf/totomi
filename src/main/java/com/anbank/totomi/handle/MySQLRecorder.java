package com.anbank.totomi.handle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.anbank.totomi.config.TotomiConfigure;

public class MySQLRecorder {
	
	private Connection connection;
	
	public MySQLRecorder() {
		try {
			Class.forName(TotomiConfigure.MySQL_CLASS_NAME);
			connection = DriverManager.getConnection(TotomiConfigure.MySQL_URL, TotomiConfigure.MySQL_USERNAME, TotomiConfigure.MySQL_PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean CheckSucceedSolved(String tableName) {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(String.format("SELECT COUNT(1) FROM TOTOMI WHERE tablename='%s' and states='SUCCEED'", tableName));
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				return count > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void updateSucceed(String tableName) {
		try {
			Statement statement = connection.createStatement();
			statement.execute(String.format("REPLACE INTO TOTOMI (tablename,states) VALUES ('%s','SUCCEED')", tableName));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateFailed(String tableName) {
		try {
			Statement statement = connection.createStatement();
			statement.execute(String.format("REPLACE INTO TOTOMI (tablename,states) VALUES ('%s','FAILED')", tableName));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateProcessing(String tableName) {
		try {
			Statement statement = connection.createStatement();
			statement.execute(String.format("REPLACE INTO TOTOMI (tablename,states) VALUES ('%s','PROCESSING')", tableName));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void forceClean() {
		try {
			Statement statement = connection.createStatement();
			statement.execute("DELETE FROM TOTOMI");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// main for test
	public static void main(String[] args) {
		MySQLRecorder recorder = new MySQLRecorder();
		recorder.forceClean();
//		recorder.updateFailed("WANG1");
//		recorder.updateSucceed("WANG2");
//		recorder.updateProcessing("WANG3");
	}
}
