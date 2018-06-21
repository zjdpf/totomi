package com.anbank.totomi.config;

public class TotomiConfigure {
	
    // DB2
    public static final String DB2_CLASS_NAME = "com.ibm.db2.jcc.DB2Driver";
    
    // DB2 inst 1
    public static final String DB2_INST1_URL = "jdbc:db2://localhost:50000/sample";
    public static final String DB2_INST1_USERNAME = "zifeiy";
    public static final String DB2_INST1_PASSWORD = "izzzyc";
    public static final String DB2_INST1_SCHEMA = "ZIFEIY";
    
//    // DB2 inst1 on real environment
//    public static final String DB2_INST1_URL = "jdbc:db2://154.84.100.115:60000/andwdb";
//    public static final String DB2_INST1_USERNAME = "andw";
//    public static final String DB2_INST1_PASSWORD = "andw";
//    public static final String DB2_INST1_SCHEMA = "ANDW";
    
    // DB2 inst 2
    public static final String DB2_INST2_URL = "jdbc:db2://localhost:50000/sample";
    public static final String DB2_INST2_USERNAME = "zifeiy";
    public static final String DB2_INST2_PASSWORD = "izzzyc";
    public static final String DB2_INST2_SCHEMA = "TEST2";
    
    // MySQL
    private static final String APPENDED_MySQL_INFO 
    = "?useUnicode=true&characterEncoding=UTF8" 
            + "&rewriteBatchedStatements=true" 
            + "&useLegacyDatetimeCode=false" 
            + "&serverTimezone=Asia/Shanghai"
            + "&useSSL=false";
    
	public static final String MySQL_CLASS_NAME = "com.mysql.cj.jdbc.Driver"; // "com.mysql.jdbc.Driver";
	public static final String MySQL_URL = "jdbc:mysql://localhost:3306/anbank" + APPENDED_MySQL_INFO;
	public static final String MySQL_USERNAME = "root";
	public static final String MySQL_PASSWORD = "password";
    
    public static final String Tmp_Blob_file_Path_Format = "C:\\projects\\totomi\\resources\\blob_%d.jpg";
    public static final String Tmp_Clob_file_Path_Format = "C:\\projects\\totomi\\resources\\clob_%d.jpg";
    
    public static final String Key_Word_File_Path = "C:\\projects\\totomi\\doc\\key_words.txt";
}
