package com.anbank.totomi.handle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.anbank.totomi.assist.TimeRecorder;
import com.anbank.totomi.config.TotomiConfigure;
import com.anbank.totomi.po.TotomiTableColumn;

public class DbTransfer {
	
	private Connection connection1;
	private Connection connection2;
	private long theDataSelectedCount;
	private TimeRecorder timeRecorder;
	private MySQLRecorder mySQLRecorder;
	private EncodingEngine encodingEngine;
	
	public DbTransfer() {
		try {
			Class.forName(TotomiConfigure.DB2_CLASS_NAME);
			connection1 = DriverManager.getConnection(TotomiConfigure.DB2_INST1_URL, TotomiConfigure.DB2_INST1_USERNAME, TotomiConfigure.DB2_INST1_PASSWORD);
			connection2 = DriverManager.getConnection(TotomiConfigure.DB2_INST2_URL, TotomiConfigure.DB2_INST2_USERNAME, TotomiConfigure.DB2_INST2_PASSWORD);
			theDataSelectedCount = 0;
			timeRecorder = new TimeRecorder();
			mySQLRecorder = new MySQLRecorder();
			encodingEngine = new EncodingEngine();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getAllTableNames() {
		try {
			List<String> tableNameList = new ArrayList<String>();
			DatabaseMetaData databaseMetaData1 = connection1.getMetaData();
			ResultSet resultSet = databaseMetaData1.getTables(null, TotomiConfigure.DB2_INST1_SCHEMA, null, new String[] { "TABLE" });  
	        
	        while (resultSet.next()) {  
	            String tableName=resultSet.getString("TABLE_NAME");  
	            tableNameList.add(tableName);
	        }
	        
	        return tableNameList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public void handle() {
		List<String> tableNameList = this.getAllTableNames();
		for (String tableName : tableNameList) {
			try {
				handleOneTable(tableName);
			} catch (Exception e) {
				mySQLRecorder.updateFailed(tableName);
			}
		}
	}
	
	public void handleOneTable(String tableName) {
		if (mySQLRecorder.CheckSucceedSolved(tableName)) {
			return;
		}
		mySQLRecorder.updateProcessing(tableName);
		tableName = tableName.toUpperCase();
		List<TotomiTableColumn> columnList = new ArrayList<TotomiTableColumn>();
		String checkString = null;
		try {
			DatabaseMetaData databaseMetaData1 = connection1.getMetaData();
			ResultSet resultSet = databaseMetaData1.getColumns(null, TotomiConfigure.DB2_INST1_SCHEMA, tableName, "%");
			// DELETE TABLE SQL
			String deleteTableSQL = String.format("DROP TABLE %s.%s", TotomiConfigure.DB2_INST2_SCHEMA, tableName);
			// CREATE TABLE SQL
			String createTableSQL = String.format("CREATE TABLE %s.%s (", TotomiConfigure.DB2_INST2_SCHEMA, tableName);
			// SELECT SQL
			String selectSQL = "SELECT ";
			boolean flag = false;
			boolean hasClobOrBlob = false;
			while (resultSet.next()) {
				if (flag) {
					createTableSQL += ",\n\t";
					selectSQL += ",";
				}
				else 
					createTableSQL += "\n\t";
				flag = true;
				String columnName = resultSet.getString("COLUMN_NAME");
				String typeName = resultSet.getString("TYPE_NAME");
				int columnSize = resultSet.getInt("COLUMN_SIZE");
				int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
				columnList.add(new TotomiTableColumn(columnName, typeName, columnSize, decimalDigits));
//				System.out.println(new TotomiTableColumn(columnName, typeName, columnSize, decimalDigits).toString());
				
				selectSQL += columnName;
				
				String oneColumnDesc = columnName + " " + typeName;
				if (typeName.equals("CHARACTER") || typeName.equals("VARCHAR") || typeName.equals("CHAR") || typeName.equals("VARGRAPHIC") || typeName.equals("GRAPHIC") 
						|| typeName.equals("BLOB") || typeName.equals("CLOB") ) {
					oneColumnDesc += "(" + columnSize + ")";
				}
				else if (typeName.equals("DECIMAL")) {
					oneColumnDesc += "(" + columnSize + "," + decimalDigits + ")";
				}
				else 
				if (typeName.equals("DATE") || typeName.equals("TIMESTAMP") || typeName.equals("INTEGER") || typeName.equals("SMALLINT") || typeName.equals("BIGINT")) {
					;
				}
				createTableSQL += oneColumnDesc;
				
				// check if contains Clob or Blob
				if (typeName.equals("CLOB") || typeName.equals("BLOB")) {
					hasClobOrBlob = true;
				}
			}
			
			createTableSQL += "\n)";
			selectSQL += " FROM " + TotomiConfigure.DB2_INST1_SCHEMA + "." + tableName;
            
            // DB2 Inst2 insert
            String insertPrefix = "INSERT INTO " + TotomiConfigure.DB2_INST2_SCHEMA + "." + tableName + " (";
            for (int i = 0; i < columnList.size(); i ++) {
            	if (i > 0) {
            		insertPrefix += ",";
            	}
            	TotomiTableColumn column = columnList.get(i);
            	insertPrefix += column.getColumnName();
            }
            insertPrefix += ") VALUES (";
            
//			// test
//			System.out.println("DEL SQL:\n" + deleteTableSQL);
//			System.out.println("CREATE SQL:\n" + createTableSQL);
//			System.out.println("SELECT SQL:\n" + selectSQL);
//			System.out.println("INSERT Prefix: " + insertPrefix);
            
			Statement statement1 = connection1.createStatement();
			Statement statement2 = connection2.createStatement();
			
			
			try {
				statement2.execute(deleteTableSQL);
			} catch (com.ibm.db2.jcc.am.SqlSyntaxErrorException e1) {
//				System.out.println("删除 " + TotomiConfigure.DB2_INST2_SCHEMA + "." + tableName + " 失败，很有很能是因为没有这个表 !!");
			}
			statement2.execute(createTableSQL);
			
			
            
            resultSet = statement1.executeQuery(selectSQL);
            int cnt = 0;
            Map<Integer, Clob> tmpClobMap = new HashMap<Integer, Clob>();
//        	Map<Integer, Blob> tmpBlobMap = new HashMap<Integer, Blob>();
        	Set<Integer> tmpBlobSet = new HashSet<Integer>();
            while (resultSet.next()) {
            	theDataSelectedCount ++;
            	if (theDataSelectedCount % 10000 == 0) {
            		System.out.println("获取 " + theDataSelectedCount + " 条数据，历时：\t" + timeRecorder.getTime() + "\t当前表名称：" + tableName);
            	}
            	int bcIdx = 0;
            	tmpClobMap.clear();
//            	tmpBlobMap.clear();
            	tmpBlobSet.clear();
            	
                String insertSuffix = "";
                for (int i = 0; i < columnList.size(); i ++) {
                	if (i > 0) {
                		insertSuffix += ",";
                	}
                	TotomiTableColumn column = columnList.get(i);
                	String typeName = column.getTypeName();
                	boolean needQuot = false;
                	if (typeName.equals("CHARACTER") || typeName.equals("VARCHAR") || typeName.equals("CHAR") || typeName.equals("DATE") || typeName.equals("TIMESTAMP")
                			|| typeName.equals("XML") || typeName.equals("DATE") || typeName.equals("TIME") 
                			|| typeName.equals("VARGRAPHIC") || typeName.equals("GRAPHIC") ) {
                		needQuot = true;
                	}
                	String value = resultSet.getString(column.getColumnName());
                	if (value == null || value.toUpperCase().equals("NULL")) {
                		needQuot = false;
                	}
                	// CLOB type special judge
                	if (value != null && column.getTypeName().equals("CLOB")) {
                		Clob clob = resultSet.getClob(column.getColumnName());
                		bcIdx ++;
                		tmpClobMap.put(bcIdx, clob);
                		value = "?";
                	}
                	// BLOB type special judge
                	if (value != null && column.getTypeName().equals("BLOB")) {
                		Blob blob = resultSet.getBlob(column.getColumnName());
                		bcIdx ++;
//                		tmpBlobMap.put(bcIdx, blob);
                		tmpBlobSet.add(bcIdx);
                		value = "?";
                		
                		File f = new File(String.format(TotomiConfigure.Tmp_Blob_file_Path_Format, bcIdx));
            			OutputStream out = null ;
            			out = new FileOutputStream(f) ;
        			    out.write(blob.getBytes(1,(int)blob.length())) ;
        			    out.close() ;
                	}
                	insertSuffix += (needQuot ? "'" : "");
                	if (value != null && (typeName.equals("CHARACTER") || typeName.equals("VARCHAR") || typeName.equals("CHAR"))) {
                		value = value.trim();
                		value = encodingEngine.encode(tableName, column.getColumnName(), value);
                	}
                	insertSuffix += (value == null ? null : value.replaceAll("'", "''"));
                	insertSuffix += (needQuot ? "'" : "");
                }
                insertSuffix += ")";
            	String insertSQL = insertPrefix + insertSuffix;
//            	System.out.println("insert SQL: " + insertSQL);
            	if (hasClobOrBlob) {
            		PreparedStatement preparedStatement = connection2.prepareStatement(insertSQL);
            		for (Entry<Integer, Clob> entry : tmpClobMap.entrySet()) {
            			int tmpIdx = entry.getKey();
            			Clob clob = entry.getValue();
            			preparedStatement.setClob(tmpIdx, clob);
            		}
//            		for (Entry<Integer, Blob> entry : tmpBlobMap.entrySet()) {
//            			int tmpIdx = entry.getKey();
//            			Blob blob = entry.getValue();
//            			preparedStatement.setBlob(tmpIdx, blob.getBinaryStream());
//            		}
            		for (int tmpIdx : tmpBlobSet) {
            			File f = new File(String.format(TotomiConfigure.Tmp_Blob_file_Path_Format, tmpIdx)) ;    // 图片文件
            	        InputStream input = null ;
            	        input = new FileInputStream(f) ;
            	        preparedStatement.setBinaryStream(tmpIdx, input, (int)f.length()) ;    // 设置输入流
            		}
            		preparedStatement.executeUpdate();
            	}
            	else {
            		checkString = insertSQL;
                	statement2.addBatch(insertSQL);
//                	System.out.println(insertSQL);
                	cnt ++;
                	if (cnt >= 1) {
                		cnt = 0;
                		statement2.executeBatch();
                	}
            	}
            }
            if (cnt > 0) {
            	statement2.executeBatch();
            }
            
            // for test on real environment because data is too big for me to store !!
            statement2.execute(deleteTableSQL);
            
            mySQLRecorder.updateSucceed(tableName);
			
		} catch (Exception e) {
			System.out.println("check String: " + checkString);
			mySQLRecorder.updateFailed(tableName);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DbTransfer transfer = new DbTransfer();
//		transfer.handle("TESTTB001");
//		transfer.handleOneTable("act");
//		transfer.getAllTableNames();
		transfer.handle();
//		transfer.handleOneTable("A00003_TH");
	}
	
}
