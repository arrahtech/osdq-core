package org.arrah.framework.rdbms;

public class SqlUserTables {
    public static final String HIVE_USERTABLES="SELECT tbl_name FROM TBLS";
    public static final String ORACLE_USERTABLES="select table_name from user_tables";
    public static final String DB2_USERTABLES="select * from sysibm.systables where owner = 'SCHEMA' and name like '%CUR%' and type = 'T'";
	public static final String MSSQL_USERTABLES="SELECT name FROM sysobjects WHERE xtype='U'";
	public static final String INFORMIX_USERTABLES="select tabname from systables where tabid > 99";
	public static final String SPLICE_USERTABLES="select tablename from sys.systables where tabletype='T'";
    public static final String POSTGRESS_USERTABLES="SELECT table_name FROM information_schema.tables WHERE table_schema='public'";
    public static final String MSACCESS_USERTABLES="SELECT MSysObjects.Name AS table_name FROM MSysObjects WHERE (((Left([Name],1))<>\"~\") AND ((Left([Name],4))<>\"MSys\") AND ((MSysObjects.Type) In (1,4,6))) order by MSysObjects.Name";
}
