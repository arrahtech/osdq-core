package org.arrah.framework.rdbms;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This is a utility class for New Database connection  
 * running SQL and fetching data 
 * This class will not have static functions
 * 
 *
 */

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class Rdbms_NewConn {
	private  Connection conn;
	private  String _d_type = "";
	private  String _d_dsn = "";
	private  String __d_protocol = "";
	private  String _d_user = "";
	private  String _d_passwd = "";
	private  String _d_driver = "";
	private  String _d_url = "";
	private  Hashtable<String, String> _h;
	private  Vector<String> table_v;
	private  Vector<String> tableDesc_v;

	public Rdbms_NewConn(Hashtable<String, String> hashtable) throws SQLException {
		init(hashtable);
		
	}

	public boolean openConn() throws SQLException {
		// Don't open if already opened
		if (conn != null && conn.isClosed() == false)
			return true;
		if (_d_driver == null || _d_driver.equals("")) {
			System.out.println("Driver Value Not Found - Check DB Driver field");
			System.out.println("\n ERROR: Driver Value Not Found");
			System.exit(0);
		}
		try {
			Class.forName(_d_driver);
		} catch (ClassNotFoundException classnotfoundexception) {
			System.out.println("Driver Class Not Found.");
			System.out
					.println("Look into System DSN if using jdbc:odbc bridge");
			System.out
					.println("Make sure Driver class is in classpath if using native");
			System.out.println("Driver Class Not Found:"
					+ classnotfoundexception.getMessage());
			return false;
		}
		try {
			String s = _d_url;
			if (s == null || "".equals(s)) {
			if (_d_type.compareToIgnoreCase("oracle_native") == 0) {
				conn = DriverManager.getConnection(__d_protocol + ":@" + _d_dsn,
						_d_user, _d_passwd);
			}
			else {
				conn = DriverManager.getConnection(__d_protocol + ":" + _d_dsn,
						_d_user, _d_passwd);
			} } else {
				conn = DriverManager.getConnection(_d_url,_d_user, _d_passwd);
			}
		} catch (Exception exception) {
			System.out.println("\n ERROR:Connection can not be created");
			System.out.println("DSN:"+_d_dsn);
			System.out.println(exception.getMessage());
			// System.exit(0);
			return false;
		}
		return true;
	}

	public  DatabaseMetaData getMetaData() throws SQLException {
		DatabaseMetaData databasemetadata = null;
		if (conn == null || conn.isClosed() == true) return null;
		try {
			databasemetadata = conn.getMetaData();
		} catch (SQLException sqlexception) {
			System.out.println("\n ERROR: MetaData SQL Exception");
			System.out.println(sqlexception.getMessage());
		}
		return databasemetadata;
	}

	public  void closeConn() throws SQLException {
		if (_d_type.compareToIgnoreCase("oracle_native") == 0)
			if (conn != null && conn.isClosed() == false) {
				conn.close(); // Oracle native has issues with static values in
								// thread
				conn = null;
			}
	}

	public  void exitConn() throws SQLException {
		if (conn != null && conn.isClosed() == false)
			conn.close();
	}

	public  PreparedStatement createQuery(String s) throws SQLException {
		if (conn == null || conn.isClosed() == true) return null;
		PreparedStatement preparedstatement = conn.prepareStatement(s);
		return preparedstatement;
	}

	public  ResultSet executePreparedQuery(PreparedStatement preparedstatement)
			throws SQLException {
		ResultSet resultset = preparedstatement.executeQuery();
		return resultset;
	}

	public  ResultSet runQuery(String s) throws SQLException {
		if (s.indexOf(" ?") == -1) {
			Statement statement;
			if (_d_type.compareToIgnoreCase("ms_access") == 0
					|| _d_type.compareToIgnoreCase("oracle_odbc") == 0
					|| _d_type.compareToIgnoreCase("hive") == 0 
					|| _d_type.compareToIgnoreCase("Informix") == 0 
					|| _d_type.compareToIgnoreCase("Splice") == 0)
				statement = conn.createStatement();
			else if(_d_type.compareToIgnoreCase("db2") == 0)
				statement = conn.createStatement(1003, 1007);
			else if(_d_type.compareToIgnoreCase("Others") == 0)
				statement = conn.createStatement(Integer.parseInt(getHValue("Database_ResultsetType")), 
						Integer.parseInt(getHValue("Database_ResultsetConcur")));
			else
				statement = conn.createStatement(1004, 1007);
			ResultSet resultset = statement.executeQuery(s);
			return resultset;
		} else {
			PreparedStatement preparedstatement = conn.prepareStatement(s);
			Vector<?>[] dateVar = QueryBuilder.getDateCondition();
			for (int i = 0; i < dateVar[0].size(); i++) {
				String s1 = (String) dateVar[1].get(i);
				if (s1.compareToIgnoreCase("time") == 0)
					preparedstatement.setTime(i + 1, new Time(
							((Date) dateVar[0].get(i)).getTime()));
				if (s1.compareToIgnoreCase("date") == 0)
					preparedstatement.setDate(i + 1, new java.sql.Date(
							((Date) dateVar[0].get(i)).getTime()));
				if (s1.compareToIgnoreCase("timestamp") == 0)
					preparedstatement.setTimestamp(i + 1, new Timestamp(
							((Date) dateVar[0].get(i)).getTime()));
			}
			ResultSet resultset = preparedstatement.executeQuery();
			return resultset;
		}
	}

	public  ResultSet runQuery(String s, int i) throws SQLException {
		Statement statement;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0
				|| _d_type.compareToIgnoreCase("hive") == 0 
				|| _d_type.compareToIgnoreCase("Informix") == 0
				|| _d_type.compareToIgnoreCase("Splice") == 0)
			statement = conn.createStatement();
		else if(_d_type.compareToIgnoreCase("db2") == 0)
			statement = conn.createStatement(1003, 1007);
		else if(_d_type.compareToIgnoreCase("Others") == 0)
			statement = conn.createStatement(Integer.parseInt(getHValue("Database_ResultsetType")), 
					Integer.parseInt(getHValue("Database_ResultsetConcur")));
		else
			statement = conn.createStatement(1004, 1007);
		statement.setMaxRows(i);
		ResultSet resultset = statement.executeQuery(s);
		return resultset;
	}

	public  int executeUpdate(String s) throws SQLException {
		Statement statement;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0
				|| _d_type.compareToIgnoreCase("hive") == 0
				|| _d_type.compareToIgnoreCase("Informix") == 0
				|| _d_type.compareToIgnoreCase("Splice") == 0)
			statement = conn.createStatement();
		else if(_d_type.compareToIgnoreCase("db2") == 0)
			statement = conn.createStatement(1003, 1007);
		else if(_d_type.compareToIgnoreCase("Others") == 0)
			statement = conn.createStatement(Integer.parseInt(getHValue("Database_ResultsetType")), 
					Integer.parseInt(getHValue("Database_ResultsetConcur")));
		else
			statement = conn.createStatement(1004, 1007);
		return statement.executeUpdate(s);
	}

	public  ResultSet execute(String s) throws SQLException {
		Statement statement = null;
		boolean flag = false;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0
				|| _d_type.compareToIgnoreCase("hive") == 0 
				|| _d_type.compareToIgnoreCase("Informix") == 0 
				|| _d_type.compareToIgnoreCase("Splice") == 0)
			statement = conn.createStatement();
		else if(_d_type.compareToIgnoreCase("db2") == 0)
			statement = conn.createStatement(1003, 1007);
		else if(_d_type.compareToIgnoreCase("Others") == 0)
			statement = conn.createStatement(Integer.parseInt(getHValue("Database_ResultsetType")), 
					Integer.parseInt(getHValue("Database_ResultsetConcur")));
		else
			statement = conn.createStatement(1004, 1007);
		flag = statement.execute(s);
		if (flag)
			return statement.getResultSet();
		else
			return null;
	}

	private void init(Hashtable<String, String> hashtable)
			throws SQLException {
		_d_type = (String) hashtable.get("Database_Type");
		_d_dsn = (String) hashtable.get("Database_DSN");
		__d_protocol = (String) hashtable.get("Database_Protocol");
		_d_driver = (String) hashtable.get("Database_Driver");
		_d_user = (String) hashtable.get("Database_User");
		_d_passwd = (String) hashtable.get("Database_Passwd");
		_d_url = (String) hashtable.get("Database_JDBC");
		_h = hashtable;
		table_v = new Vector<String>();
		tableDesc_v = new Vector<String>();
		exitConn();
	}

	public  String getDBType() {
		return _d_type;
	}

	public  String getHValue(String s) {
		return (String) _h.get(s);
	}

	public  String setHValue(String key, String value) {
		return (String) _h.put(key, value);
	}

	public  String checkAndReturnSql(String s) throws SQLException {
		Statement statement = null;
		conn.setAutoCommit(false);
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0
				|| _d_type.compareToIgnoreCase("hive") == 0 
				|| _d_type.compareToIgnoreCase("Informix") == 0 
				|| _d_type.compareToIgnoreCase("Splice") == 0)
			statement = conn.createStatement();
		else if(_d_type.compareToIgnoreCase("db2") == 0)
			statement = conn.createStatement(1003, 1007);
		else if(_d_type.compareToIgnoreCase("Others") == 0)
			statement = conn.createStatement(Integer.parseInt(getHValue("Database_ResultsetType")), 
					Integer.parseInt(getHValue("Database_ResultsetConcur")));
		else
			statement = conn.createStatement(1004, 1007);
		statement.execute(s);
		conn.rollback();
		conn.setAutoCommit(true);
		return conn.nativeSQL(s);
	}

	public  void populateTable( ) throws SQLException {
		/** Get New connection and populate tables **/
		String s = (String) _h.get("Database_SchemaPattern");
		String s1 = (String) _h.get("Database_TablePattern");
		String s2 = (String) _h.get("Database_TableType");
		String s3 = (String) _h.get("Database_Catalog");
		s3 = "";
		if (s == null || "".equals(s)) s= null;
		if (s3 == null || "".equals(s3)) s3 = null;
		if (s1 == null || "".equals(s1)) s1 = null;
		if (s2 == null || "".equals(s2)) s2 = "TABLE"; //show only tables
		
		if (openConn() == true) {
		DatabaseMetaData databasemetadata = getMetaData();
		ResultSet resultset = databasemetadata.getTables(s3, s,s1,s2.split(","));
		while (resultset.next()) {
			String s5 = resultset.getString(3);
			addTable(s5);
			String s7 = resultset.getString(5);
			addTableDesc(s7);
		}
		resultset.close();
		closeConn();
		} else {
			System.out.println("Table Can not be populated");
		}
	}
	
	public  Vector<?>[] populateColumn( String tableName,String colPattern) throws SQLException {
		// We are not supporting column pattern for Now
		
		Vector avector[] = new Vector[2];
		avector[0] = new Vector<String>();
		avector[1] = new Vector<Integer>();
		
		/** Get New connection and populate tables **/
		String s = (String) _h.get("Database_SchemaPattern");
		String s1 = (String) _h.get("Database_TablePattern");
		String s3 = (String) _h.get("Database_Catalog");
		s3 = "";
		if (s == null || "".equals(s)) s= null;
		if (s3 == null || "".equals(s3)) s3 = null;
		if (s1 == null || "".equals(s1)) s1 = null;
		
		
		if (openConn() == true) {
		DatabaseMetaData databasemetadata = getMetaData();
		ResultSet resultset = databasemetadata.getColumns(s3, s,tableName, null);

		int k = 0;
		while (resultset.next()) {
			String s31 = resultset.getString(3);
			if (s31.equals(tableName)) {
				String s4 = resultset.getString(4);
				int i1 = resultset.getInt(5);
				avector[0].add(k, s4);
				avector[1].add(k, new Integer(i1));
				k++;
			} }
		
		resultset.close();
		closeConn();
		} else {
			System.out.println("Column can not be populated");
		}
		
		return avector;
	}
	

	public  void addTable(String s) {
		if (s == null)
			return;
		table_v.add(table_v.size(), s);
	}

	public  void addTableDesc(String s) {
		if (s == null)
			tableDesc_v.add(tableDesc_v.size(), "");
		else
			tableDesc_v.add(tableDesc_v.size(), s);
	}

	public  Vector<String> getTable() {
		return table_v;
	}

	public  int getTableCount() {
		return table_v.size();
	}

	public  Vector<String> getTableDesc() {
		return tableDesc_v;
	}

	public  String getProtocol() {
		return __d_protocol;
	}

	public  String getUser() {
		return _d_user;
	}
	public  String get_JDBC_URL() {
		return _d_url;
	}

	public  void set_JDBC_URL(String url) {
		_d_url = url;
	}

	public  String testConn() throws SQLException {
		String status =" Connection Failed. \n\n"; // Will send status of connection
		
		// Close if connection is already open
		exitConn();
		
		if (_d_driver == null || _d_driver.equals("")) {
			return status += "Driver Value Not Found - Check DB Driver field";
		}
		try {
			Class.forName(_d_driver);
		} catch (ClassNotFoundException classnotfoundexception) {
			status += "Driver Class Not Found. \n";
			status += "Look into System DSN if using jdbc:odbc bridge \n";
			status += "Make sure Driver class is in classpath if using native \n";
			status += "\n System Message:"
					+ classnotfoundexception.getMessage();
			
			return status;
		}
		try {
			String s = _d_url;
			if (s == null || "".equals(s)) {
				if (_d_type.compareToIgnoreCase("oracle_native") == 0)
					conn = DriverManager.getConnection(__d_protocol + ":@" + _d_dsn,
							_d_user, _d_passwd);
				else {
					conn = DriverManager.getConnection(__d_protocol + ":" + _d_dsn,
							_d_user, _d_passwd);
			}} else {
				conn = DriverManager.getConnection(_d_url,_d_user, _d_passwd);

			}
		} catch (Exception exception) {
			status += "\n System Message:";
			return status += exception.getMessage();
		}
		return status = "Connection Successful";
	}


}
