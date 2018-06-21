# TOTOMI使用说明
TOTOMI -- a simple project for Data desensitization on IBM DB2.

TOTOMI：适用于DB2的数据库脱敏软件。  

### Requirements
- Redis on localhost
- MySQL on localhost
- DB2 on localhost
- JDK 1.8

### 使用说明
相关配置见TotomiConfigure.java文件。  
主函数：TotomiApplication.main()，调用该函数会将数据库1中的指定schema中的所有表结构和数据导入数据库2中的指定schema中。  

加密引擎：EncodingEngine.java。  

doc/key_words.txt中包含关键字码表；  
doc/mysql.sql中包含一个MySQL的建表语句，其中的TOTOMI表用于记录说及转移情况：

- 如果states为SUCCEED则全部转移并脱敏成功；
- 如果是PROCESSING则正在进行该表的处理；
- 如果是FAILED则说明该表转移的时候出现了问题从而导致失败了。

### 其他说明
Still many bugs.