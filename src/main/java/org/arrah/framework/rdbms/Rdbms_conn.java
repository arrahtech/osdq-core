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
 * This is a utility class for Database connection  
 * running SQL and fetching data 
 * This class will be used to open multiple connection
 * so should be thread safe
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

public class Rdbms_conn {
	private static Connection conn;
	private static String _d_type = "";
	private static String _d_dsn = "";
	private static String __d_protocol = "";
	private static String _d_user = "";
	private static String _d_passwd = "";
	private static String _d_driver = "";
	private static String _d_url = "";
	private static Hashtable<String, String> _h;
	private static Vector<String> table_v;
	private static Vector<String> tableDesc_v;

	public Rdbms_conn() {
		// Do nothing
	}

	public static void openConn() throws SQLException {
		// Don't open if already opened
		if (conn != null && conn.isClosed() == false)
			return;
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
			System.exit(0);
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
			} } else {
				conn = DriverManager.getConnection(_d_url,_d_user, _d_passwd);
			}
		} catch (SQLException exception) {
			System.out.println("\n ERROR:Connection can not be created");
			System.out.println(exception.getMessage());
			throw exception ;
			// System.exit(0);
		}
	}

	public static DatabaseMetaData getMetaData() throws SQLException {
		DatabaseMetaData databasemetadata = null;
		try {
			databasemetadata = conn.getMetaData();
		} catch (SQLException sqlexception) {
			System.out.println("\n ERROR: MetaData SQL Exception");
			System.out.println(sqlexception.getMessage());
		}
		return databasemetadata;
	}

	public static void closeConn() throws SQLException {
		if (_d_type.compareToIgnoreCase("oracle_native") == 0) {
			if (conn != null && conn.isClosed() == false) {
				conn.close(); // Oracle native has issues with static values in threads
				conn = null;
			}
		} else {
			if (conn != null && conn.isClosed() == false) 
				conn.close();
		}
	}

	public static void exitConn() throws SQLException {
		if (conn != null && conn.isClosed() == false)
			conn.close();
	}

	public static PreparedStatement createQuery(String s) throws SQLException {
		PreparedStatement preparedstatement = conn.prepareStatement(s);
		return preparedstatement;
	}

	public static ResultSet executeQuery(PreparedStatement preparedstatement)
			throws SQLException {
		ResultSet resultset = preparedstatement.executeQuery();
		return resultset;
	}

	public static ResultSet runQuery(String s) throws SQLException {
		if (conn == null || conn.isClosed() == true) return (ResultSet) null;
		if (s == null || "".equals(s)) return (ResultSet) null;
		if (s.indexOf(" ?") == -1) {
			Statement statement;
			if (_d_type.compareToIgnoreCase("ms_access") == 0
					|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
				statement = conn.createStatement();
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

	public static ResultSet runQuery(String s, int i) throws SQLException {
		if (conn == null || conn.isClosed() == true) return (ResultSet) null;
		if (s == null || "".equals(s)) return (ResultSet) null;
		Statement statement;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		statement.setMaxRows(i);
		ResultSet resultset = statement.executeQuery(s);
		return resultset;
	}

	public static int executeUpdate(String s) throws SQLException {
		Statement statement;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		return statement.executeUpdate(s);
	}

	public static ResultSet execute(String s) throws SQLException {
		Statement statement = null;
		boolean flag = false;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		flag = statement.execute(s);
		if (flag)
			return statement.getResultSet();
		else
			return null;
	}

	public static void init(Hashtable<String, String> hashtable)
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

	public static String getDBType() {
		return _d_type;
	}

	public static String getHValue(String s) {
		return (String) _h.get(s);
	}

	public static String setHValue(String key, String value) {
		return (String) _h.put(key, value);
	}

	public static String checkAndReturnSql(String s) throws SQLException {
		Statement statement = null;
		conn.setAutoCommit(false);
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		statement.execute(s);
		conn.rollback();
		conn.setAutoCommit(true);
		return conn.nativeSQL(s);
	}

	public static void populateTable(String cat, String schema,
			String tableNamePattern, String[] types) throws SQLException {
		openConn();
		DatabaseMetaData databasemetadata = getMetaData();
		ResultSet resultset = databasemetadata.getTables(cat, schema,
				tableNamePattern, types);

		while (resultset.next()) {
			String s5 = resultset.getString(3);
			addTable(s5);
			String s7 = resultset.getString(5);
			addTableDesc(s7);
		}
		resultset.close();
		closeConn();
	}

	public static void addTable(String s) {
		if (s == null)
			return;
		table_v.add(table_v.size(), s);
	}

	public static void addTableDesc(String s) {
		if (s == null)
			tableDesc_v.add(tableDesc_v.size(), "");
		else
			tableDesc_v.add(tableDesc_v.size(), s);
	}

	public static Vector<String> getTable() {
		return table_v;
	}

	public static int getTableCount() {
		return table_v.size();
	}

	public static Vector<String> getTableDesc() {
		return tableDesc_v;
	}

	public static String getProtocol() {
		return __d_protocol;
	}

	public static String getUser() {
		return _d_user;
	}
	public static String testConn() throws SQLException {
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

	public static String get_JDBC_URL() {
		return _d_url;
	}

	public static void set_JDBC_URL(String _d_url) {
		Rdbms_conn._d_url = _d_url;
	}
	
	public  static Vector<?>[] populateColumn( String tableName,String colPattern) throws SQLException {
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
		
		
		openConn() ;
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
		return avector;
		} 

}
