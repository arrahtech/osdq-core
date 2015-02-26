package org.arrah.framework.ndtable;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* 
 * This file shows interactive SQL data
 * in ReportTableModel structure, MD5 
 *
 */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Vector;

import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_NewConn;
import org.arrah.framework.rdbms.Rdbms_conn;

public class ResultsetToRTM {

	public ResultsetToRTM() {
	}

	public static ReportTableModel getSQLValue(ResultSet rs, boolean format)
			throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String[] col_name = new String[numberOfColumns];
		int[] col_type = new int[numberOfColumns];
		Vector<Object> row_v;

		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnLabel(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}
		ReportTableModel rt;
		if (format == true)
			rt = new ReportTableModel(col_name, false, true);
		else
			rt = new ReportTableModel(col_name, false, false);
		while (rs.next()) {
			row_v = new Vector<Object>();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				switch (col_type[i - 1]) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.TINYINT:
				case java.sql.Types.SMALLINT:
					row_v.add(i - 1, new Integer(rs.getInt(i)));
					break;
				case java.sql.Types.DOUBLE:
				case java.sql.Types.REAL:
				case java.sql.Types.DECIMAL:
				case java.sql.Types.NUMERIC:
				case java.sql.Types.BIGINT:
					row_v.add(i - 1, new Double(rs.getDouble(i)));
					break;
				case java.sql.Types.FLOAT:
					row_v.add(i - 1, new Float(rs.getFloat(i)));
					break;
				case java.sql.Types.CLOB:
					row_v.add(i - 1, rs.getClob(i));
					break;
				case java.sql.Types.BLOB:
					row_v.add(i - 1, rs.getBlob(i));
					break;
				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					row_v.add(i - 1, new Boolean(rs.getBoolean(i)));
					break;
				case java.sql.Types.DATE:
					row_v.add(i - 1, rs.getDate(i));
					break;
				case java.sql.Types.TIME:
					row_v.add(i - 1, rs.getTime(i));
					break;
				case java.sql.Types.TIMESTAMP:
					row_v.add(i - 1, rs.getTimestamp(i));
					break;
				case java.sql.Types.ARRAY:
					row_v.add(i - 1, rs.getArray(i));
					break;
				case java.sql.Types.REF:
					row_v.add(i - 1, rs.getRef(i));
					break;
				case java.sql.Types.BINARY:
					row_v.add(i - 1, rs.getByte(i));
					break;
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					row_v.add(i - 1, rs.getBytes(i));
					break;
				case java.sql.Types.DATALINK:
				case java.sql.Types.DISTINCT:
				case java.sql.Types.JAVA_OBJECT:
				case java.sql.Types.NULL:
				case java.sql.Types.OTHER:
				case java.sql.Types.STRUCT:
					row_v.add(i - 1, rs.getObject(i));
					break;
				default:
					row_v.add(i - 1, rs.getString(i));

				}
			}
			rt.addFillRow(row_v);
		}
		return rt;
	}
	
	// Return MD5 hash
	synchronized public static  Vector<BigInteger> getMD5Value(ResultSet rs)
			throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String[] col_name = new String[numberOfColumns];
		int[] col_type = new int[numberOfColumns];
		Vector<BigInteger> row_v = new Vector<BigInteger>();

		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnLabel(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}
		
		while (rs.next()) {
			String rowString="";
			
			for (int i = 1; i < numberOfColumns + 1; i++) {
				
				switch (col_type[i - 1]) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.TINYINT:
				case java.sql.Types.SMALLINT:
					rowString +=  new Integer(rs.getInt(i)).toString();
					break;
				case java.sql.Types.DOUBLE:
				case java.sql.Types.REAL:
				case java.sql.Types.DECIMAL:
				case java.sql.Types.NUMERIC:
				case java.sql.Types.BIGINT:
					rowString +=  new Double(rs.getDouble(i)).toString();
					break;
				case java.sql.Types.FLOAT:
					rowString += new Float(rs.getFloat(i)).toString();
					break;
				case java.sql.Types.CLOB:
					rowString +=  rs.getClob(i).toString();
					break;
				case java.sql.Types.BLOB:
					rowString += rs.getBlob(i).toString();
					break;
				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					rowString += new Boolean(rs.getBoolean(i)).toString();
					break;
				case java.sql.Types.DATE:
					rowString += rs.getDate(i).toString();
					break;
				case java.sql.Types.TIME:
					rowString += rs.getTime(i).toString();
					break;
				case java.sql.Types.TIMESTAMP:
					rowString += rs.getTimestamp(i).toString();
					break;
				case java.sql.Types.ARRAY:
					rowString += rs.getArray(i).toString();
					break;
				case java.sql.Types.REF:
					rowString += rs.getRef(i).toString();
					break;
				case java.sql.Types.BINARY:
					rowString += rs.getByte(i);
					break;
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					rowString += rs.getBytes(i).toString();
					break;
				case java.sql.Types.DATALINK:
				case java.sql.Types.DISTINCT:
				case java.sql.Types.JAVA_OBJECT:
				case java.sql.Types.NULL:
				case java.sql.Types.OTHER:
				case java.sql.Types.STRUCT:
					rowString += rs.getObject(i).toString();
					break;
				default:
					rowString += rs.getString(i);
				}
				
			} // End of For Loop
			
			// Give the string to create MD5 BigInteger
			BigInteger number = getMD5(rowString);
			if (number != null)
				row_v.add(number);
		}
		return row_v;
	}
	
	public static BigInteger getMD5(String input) {
		BigInteger number = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");            
			byte[] messageDigest = md.digest(input.getBytes());            
			 number = new BigInteger(1, messageDigest);                           
			} catch (NoSuchAlgorithmException e) {
		         throw new RuntimeException(e);        
			}
		// System.out.println("Big Integer is:"+number);
		return number;
		}
	
	synchronized public static ReportTableModel matchMD5Value(ResultSet rs, Vector<BigInteger> hashValue, boolean match)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String[] col_name = new String[numberOfColumns];
		int[] col_type = new int[numberOfColumns];
		Vector<Object> row_v;

		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnLabel(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}
		ReportTableModel  rt = new ReportTableModel(col_name, false, true);
		
		while (rs.next()) {
			String rowString = "";
			row_v = new Vector<Object>();
			
			for (int i = 1; i < numberOfColumns + 1; i++) {
				switch (col_type[i - 1]) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.TINYINT:
				case java.sql.Types.SMALLINT:
					Integer intval = new Integer(rs.getInt(i));
					row_v.add(i - 1,intval );
					rowString += intval.toString();
					break;
				case java.sql.Types.DOUBLE:
				case java.sql.Types.REAL:
				case java.sql.Types.DECIMAL:
				case java.sql.Types.NUMERIC:
				case java.sql.Types.BIGINT:
					Double dobval = new Double(rs.getDouble(i));
					row_v.add(i - 1, dobval);
					rowString += dobval.toString();
					break;
				case java.sql.Types.FLOAT:
					Float floval = new Float(rs.getFloat(i));
					row_v.add(i - 1, floval);
					rowString += floval.toString();
					break;
				case java.sql.Types.CLOB:
					Clob cloval = rs.getClob(i);
					row_v.add(i - 1,cloval );
					rowString += cloval.toString();
					break;
				case java.sql.Types.BLOB:
					Blob bloval = rs.getBlob(i);
					row_v.add(i - 1,bloval );
					rowString += bloval.toString();
					break;
				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					Boolean booval = new Boolean(rs.getBoolean(i));
					row_v.add(i - 1, booval);
					rowString += booval.toString();
					break;
				case java.sql.Types.DATE:
					Date datval = rs.getDate(i);
					row_v.add(i - 1, datval);
					rowString += datval.toString();
					break;
				case java.sql.Types.TIME:
					Time timval = rs.getTime(i);
					row_v.add(i - 1,timval );
					rowString += timval.toString();
					break;
				case java.sql.Types.TIMESTAMP:
					Timestamp tstval = rs.getTimestamp(i);
					row_v.add(i - 1,tstval );
					rowString += tstval.toString();
					break;
				case java.sql.Types.ARRAY:
					Array araval =  rs.getArray(i);
					row_v.add(i - 1,araval);
					rowString += araval.toString();
					break;
				case java.sql.Types.REF:
					Ref refval = rs.getRef(i);
					row_v.add(i - 1, refval);
					rowString += refval.toString();
					break;
				case java.sql.Types.BINARY:
					byte bytval = rs.getByte(i);
					row_v.add(i - 1, bytval);
					rowString += bytval;
					break;
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					byte[] btsval =  rs.getBytes(i);
					row_v.add(i - 1,btsval);
					rowString += btsval.toString();
					break;
				case java.sql.Types.DATALINK:
				case java.sql.Types.DISTINCT:
				case java.sql.Types.JAVA_OBJECT:
				case java.sql.Types.NULL:
				case java.sql.Types.OTHER:
				case java.sql.Types.STRUCT:
					Object objval = rs.getObject(i);
					row_v.add(i - 1,objval );
					rowString += objval.toString();
					break;
				default:
					String strval = rs.getString(i);
					row_v.add(i - 1,strval );
					rowString += strval;
				} // End of switch
			}
			// Give the string to create MD5 BigInteger
						BigInteger number = getMD5(rowString);
						if (number == null ) continue ;
						if (match == true && hashValue.contains(number) == true)
							rt.addFillRow(row_v);
						if (match == false && hashValue.contains(number) == false)
							rt.addFillRow(row_v);
		}
		return rt;
	}

	synchronized public static ReportTableModel compareTable(String lTable, Hashtable<String,String> newDBParam, String rtable,
					boolean match) throws SQLException  {
		
		ReportTableModel rtm = null;
		Vector avector[] = Rdbms_conn.populateColumn(lTable,null);
		QueryBuilder qb = new QueryBuilder(
				Rdbms_conn.getHValue("Database_DSN"), lTable,
				Rdbms_conn.getDBType());
		String s1 = qb.get_selCol_query(avector[0].toArray(),"");
		
	
		Rdbms_conn.openConn();
		ResultSet resultset = Rdbms_conn.runQuery(s1); 
		Vector<BigInteger> hashNumber = ResultsetToRTM.getMD5Value(resultset);
		resultset.close();
		Rdbms_conn.closeConn();
		
		// Query to another table

		Rdbms_NewConn newConn = new Rdbms_NewConn(newDBParam);
		qb = new QueryBuilder(
				newConn.getHValue("Database_DSN"), rtable,
				newConn.getDBType());
		Vector avectorR[] = newConn.populateColumn(rtable,null);
		s1 = qb.get_selCol_query(avectorR[0].toArray(),"");
		
		if ( newConn.openConn() == true ) {
			resultset = newConn.runQuery(s1);
			rtm = ResultsetToRTM.matchMD5Value(resultset,hashNumber,match);
			resultset.close();
			newConn.closeConn();
			
		}
		return rtm;
	}
	
	
} // End of class
