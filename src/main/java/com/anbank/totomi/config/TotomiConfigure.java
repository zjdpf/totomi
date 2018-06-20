package com.anbank.totomi.config;

public class TotomiConfigure {
	
    // DB2
    public static final String DB2_CLASS_NAME = "com.ibm.db2.jcc.DB2Driver";
    
    // DB2 inst 1
    public static final String DB2_INST1_URL = "jdbc:db2://localhost:50000/sample";
    public static final String DB2_INST1_USERNAME = "zifeiy";
    public static final String DB2_INST1_PASSWORD = "izzzyc";
    public static final String DB2_INST1_SCHEMA = "ZIFEIY";
    
    // DB2 inst 2
    public static final String DB2_INST2_URL = "jdbc:db2://localhost:50000/sample";
    public static final String DB2_INST2_USERNAME = "zifeiy";
    public static final String DB2_INST2_PASSWORD = "izzzyc";
    public static final String DB2_INST2_SCHEMA = "TEST2";
    
    public static final String Tmp_Blob_file_Path_Format = "C:\\projects\\totomi\\resources\\blob_%d.jpg";
    public static final String Tmp_Clob_file_Path_Format = "C:\\projects\\totomi\\resources\\clob_%d.jpg";
}
