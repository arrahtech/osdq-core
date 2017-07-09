package org.arrah.framework.rdbms;

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
 * This class is used for displaying  
 * rowsets (editable) with JDBC Connected Rowset 
 *
 */

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

public class JDBCRowset {
	private UpdatableJdbcRowsetImpl rows;
	private String[] col_name;
	private String[] tbl_name;
	private int[] col_type;
	private int rowC = 0;
	private int numberOfColumns = 0;
	private int rowPPage = 100; // Default rows per page

	/*
	 * Constructor for simple query Rdbms_NewConn.get() - connection on which rowset is
	 * required query - query to be run maxRow - maxrow to be fetched if > 0
	 * editable - if you want rowset to be editable for insert
	 */
	public JDBCRowset(String query, int maxRow, boolean editable)
			throws SQLException {
		try {
			String protocol = Rdbms_NewConn.get().getProtocol();
			String url = Rdbms_NewConn.get().get_JDBC_URL();
			if (url == null || "".equals(url)) {
			if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase(
					"oracle_native") == 0)
				rows = new UpdatableJdbcRowsetImpl(protocol + ":@"
						+ Rdbms_NewConn.get().getHValue("Database_DSN"),
						Rdbms_NewConn.get().getHValue("Database_User"),
						Rdbms_NewConn.get().getHValue("Database_Passwd"));
			else
				rows = new UpdatableJdbcRowsetImpl(protocol + ":"
						+ Rdbms_NewConn.get().getHValue("Database_DSN"),
						Rdbms_NewConn.get().getHValue("Database_User"),
						Rdbms_NewConn.get().getHValue("Database_Passwd"));
			} else {
				rows = new UpdatableJdbcRowsetImpl(url,
						Rdbms_NewConn.get().getHValue("Database_User"),
						Rdbms_NewConn.get().getHValue("Database_Passwd"));
						
				
			}
			rows.setReadOnly(editable);
			rows.setCommand(query);
			if (maxRow > 1) // only if positive maxRow is set
				rows.setMaxRows(maxRow);

			if (query.indexOf(" ?") != -1) {
				Vector<?>[] dateVar = QueryBuilder.getDateCondition();
				for (int i = 0; i < dateVar[0].size(); i++) {
					String s1 = (String) dateVar[1].get(i);
					if (s1.compareToIgnoreCase("time") == 0)
						rows.setTime(i + 1,
								new Time(((Date) dateVar[0].get(i)).getTime()));
					if (s1.compareToIgnoreCase("date") == 0)
						rows.setDate(i + 1, new java.sql.Date(
								((Date) dateVar[0].get(i)).getTime()));
					if (s1.compareToIgnoreCase("timestamp") == 0)
						rows.setTimestamp(i + 1, new Timestamp(
								((Date) dateVar[0].get(i)).getTime()));
				}
			}
			rows.execute();
			if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
				if (rows.last() == true)
					rowC = rows.getRow();
			} else { // call count function explicitly 
				Rdbms_NewConn.get().openConn();
				String orgQuery = QueryBuilder.hive_count_query(query);
				ResultSet rs = Rdbms_NewConn.get().runQuery(orgQuery);
				while (rs.next()) {
					rowC = rs.getInt("total_count");
				}
				rs.close();
				Rdbms_NewConn.get().closeConn();
			}
			createMD();
			
		} catch (SQLException e) {
			System.err.println("Error in JDBCRowset Constructor:"+e.getLocalizedMessage());
			throw e;
		}
		if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
			rows.setAutoCommit(true);
		}
	}

	/* IF YOU WANT Prepared query Rowset */
	// Hive does not support prepared rowset yet
	public JDBCRowset(String query, boolean editable, Vector<Integer> vc_t,
			Vector<Object> vc_v) throws SQLException {
		try {
			String protocol = Rdbms_NewConn.get().getProtocol();
			String url = Rdbms_NewConn.get().get_JDBC_URL();
			if (url == null || "".equals(url)) {
			if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase(
					"oracle_native") == 0)
				rows = new UpdatableJdbcRowsetImpl(protocol + ":@"
						+ Rdbms_NewConn.get().getHValue("Database_DSN"),
						Rdbms_NewConn.get().getHValue("Database_User"),
						Rdbms_NewConn.get().getHValue("Database_Passwd"));
			else
				rows = new UpdatableJdbcRowsetImpl(protocol + ":"
						+ Rdbms_NewConn.get().getHValue("Database_DSN"),
						Rdbms_NewConn.get().getHValue("Database_User"),
						Rdbms_NewConn.get().getHValue("Database_Passwd"));
			} else {
				rows = new UpdatableJdbcRowsetImpl(url,
						Rdbms_NewConn.get().getHValue("Database_User"),
						Rdbms_NewConn.get().getHValue("Database_Passwd"));
			}
			rows.setReadOnly(editable);
			rows.setCommand(query);

			int fromIndex = 0;
			for (int i = 0; i < vc_t.size(); i++) {
				fromIndex = query.indexOf(" ?", fromIndex);
				if (fromIndex == -1)
					return;
				fromIndex += 2; // move Index
				setQuery(i, vc_t.get(i), vc_v.get(i));
			}
			if (query.indexOf(" ?", fromIndex) != -1) {
				Vector<?>[] dateVar = QueryBuilder.getDateCondition();
				for (int i = vc_t.size(); i < (vc_t.size() + dateVar[0].size()); i++) {
					String s1 = (String) dateVar[1].get(i - vc_t.size());
					if (s1.compareToIgnoreCase("time") == 0)
						rows.setTime(
								i + 1,
								new Time(
										((Date) dateVar[0].get(i - vc_t.size()))
												.getTime()));
					if (s1.compareToIgnoreCase("date") == 0)
						rows.setDate(
								i + 1,
								new java.sql.Date(((Date) dateVar[0].get(i
										- vc_t.size())).getTime()));
					if (s1.compareToIgnoreCase("timestamp") == 0)
						rows.setTimestamp(
								i + 1,
								new Timestamp(((Date) dateVar[0].get(i
										- vc_t.size())).getTime()));
				}
			}
			rows.execute();
			if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
				if (rows.last() == true)
					rowC = rows.getRow();
			} else {
				Rdbms_NewConn.get().openConn();
				String orgQuery = QueryBuilder.hive_count_query(query);
				ResultSet rs = Rdbms_NewConn.get().runQuery(orgQuery);
				while (rs.next()) {
					rowC = rs.getInt("total_count");
				}
				rs.close();
				Rdbms_NewConn.get().closeConn();
			}
		} catch (SQLException e) {
			System.err.println("Error in Prepared JDBCRowset Constructor:"+e.getLocalizedMessage());
			throw e;
		}
		if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
			rows.setAutoCommit(true);
		}
	}

	/* This will give 2 dimensional array 
	 * one will have cols and second will have rows
	 * however it will fails in hive as it has abosute and previous
	 * functions which is not supported by hive
	 * 
	 */
	public Vector<Object>[] getRowCol(int fromIndex, int toIndex,
			Vector<Object>[] row_v) throws SQLException {
		rows.absolute(fromIndex);
		rows.previous();
		int counter = 0;

		// if a Vector<Object> following code does not work
		if (toIndex >= fromIndex)
			row_v = new Vector[toIndex - fromIndex + 1];

		while (rows.next() && (toIndex >= fromIndex + counter)) {
			row_v[counter] = new Vector<Object>();
			for (int i = 1; i < col_name.length + 1; i++) {
				switch (col_type[i - 1]) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.TINYINT:
				case java.sql.Types.SMALLINT:
					row_v[counter].add(i - 1, new Integer(rows.getInt(i)));
					break;
				case java.sql.Types.DOUBLE:
				case java.sql.Types.REAL:
				case java.sql.Types.DECIMAL:
				case java.sql.Types.NUMERIC:
				case java.sql.Types.BIGINT:
					row_v[counter].add(i - 1, new Double(rows.getDouble(i)));
					break;
				case java.sql.Types.FLOAT:
					row_v[counter].add(i - 1, new Float(rows.getFloat(i)));
					break;
				case java.sql.Types.CLOB:
					row_v[counter].add(i - 1, rows.getClob(i));
					break;
				case java.sql.Types.BLOB:
					row_v[counter].add(i - 1, rows.getBlob(i));
					break;
				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					row_v[counter].add(i - 1, new Boolean(rows.getBoolean(i)));
					break;
				case java.sql.Types.DATE:
					row_v[counter].add(i - 1, rows.getDate(i));
					break;
				case java.sql.Types.TIME:
					row_v[counter].add(i - 1, rows.getTime(i));
					break;
				case java.sql.Types.TIMESTAMP:
					row_v[counter].add(i - 1, rows.getTimestamp(i));
					break;
				case java.sql.Types.ARRAY:
					row_v[counter].add(i - 1, rows.getArray(i));
					break;
				case java.sql.Types.REF:
					row_v[counter].add(i - 1, rows.getRef(i));
					break;
				case java.sql.Types.BINARY:
					row_v[counter].add(i - 1, rows.getByte(i));
					break;
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					row_v[counter].add(i - 1, rows.getBytes(i));
					break;
				case java.sql.Types.DATALINK:
				case java.sql.Types.DISTINCT:
				case java.sql.Types.JAVA_OBJECT:
				case java.sql.Types.NULL:
				case java.sql.Types.OTHER:
				case java.sql.Types.STRUCT:
					row_v[counter].add(i - 1, rows.getObject(i));
					break;
				default:
					row_v[counter].add(i - 1, rows.getString(i));
				}
			}
			counter++;
		}
		return row_v;
	}

	public boolean updateCellVal(int row, int col, Object obj)
			throws SQLException {
		rows.absolute(row);
		boolean update = updateCell(col, obj);
		rows.updateRow();
		try { 
			rows.refreshRow();
		} catch(Exception e){
			System.out.println("WARNING :: ResultSet.refreshRow() is not Supported");
		}
		
		return update;
	}

	public boolean updateCell(int i, Object o) throws SQLException {
		boolean changeToNull = false;
		try {
			switch (col_type[i]) {
			case java.sql.Types.INTEGER:
			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
				rows.updateInt(i + 1, ((Number) o).intValue());
				break;
			case java.sql.Types.DOUBLE:
			case java.sql.Types.REAL:
			case java.sql.Types.DECIMAL:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.BIGINT:
				rows.updateDouble(i + 1, ((Number) o).doubleValue());
				break;
			case java.sql.Types.FLOAT:
				rows.updateFloat(i + 1, ((Number) o).floatValue());
				break;
			case java.sql.Types.CLOB:
				rows.updateClob(i + 1, (Clob) o);
				break;
			case java.sql.Types.BLOB:
				rows.updateBlob(i + 1, (Blob) o);
				break;
			case java.sql.Types.BOOLEAN:
			case java.sql.Types.BIT:
				rows.updateBoolean(i + 1, ((Boolean) o).booleanValue());
				break;
			case java.sql.Types.DATE:
				rows.updateDate(i + 1, new java.sql.Date(((Date) o).getTime()));
				break;
			case java.sql.Types.TIME:
				rows.updateTime(i + 1, new Time(((Date) o).getTime()));
				break;
			case java.sql.Types.TIMESTAMP:
				rows.updateTimestamp(i + 1, new Timestamp(((Date) o).getTime()));
				break;
			case java.sql.Types.ARRAY:
				rows.updateArray(i + 1, (Array) o);
				break;
			case java.sql.Types.REF:
				rows.updateRef(i + 1, (Ref) o);
				break;
			case java.sql.Types.LONGVARBINARY:
			case java.sql.Types.BINARY:
			case java.sql.Types.VARBINARY:
			case java.sql.Types.DATALINK:
			case java.sql.Types.DISTINCT:
			case java.sql.Types.JAVA_OBJECT:
			case java.sql.Types.NULL:
			case java.sql.Types.OTHER:
			case java.sql.Types.STRUCT:
				rows.updateObject(i + 1, o);
				break;
			default:
				if ("".equals(o.toString())) {
					rows.updateString(i + 1, null);
					changeToNull = true;
				} else
					rows.updateString(i + 1, o.toString());
			}
		} catch (ClassCastException e) {
			try {
				rows.updateString(i + 1, o.toString());
			} catch (Exception exp) {
				rows.updateObject(i + 1, null);
				System.out.println("\n Update Casting exception");
			}
		} catch (NullPointerException e) {
			rows.updateObject(i + 1, o);
		}
		return changeToNull;
	}

	private void setQuery(int i, int type, Object o) throws SQLException {
		try {
			switch (type) {
			case java.sql.Types.INTEGER:
			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
				rows.setInt(i + 1, ((Number) o).intValue());
				break;
			case java.sql.Types.DOUBLE:
			case java.sql.Types.REAL:
			case java.sql.Types.DECIMAL:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.BIGINT:
				rows.setDouble(i + 1, ((Number) o).doubleValue());
				break;
			case java.sql.Types.FLOAT:
				rows.setFloat(i + 1, ((Number) o).floatValue());
				break;
			case java.sql.Types.CLOB:
				rows.setClob(i + 1, (Clob) o);
				break;
			case java.sql.Types.BLOB:
				rows.setBlob(i + 1, (Blob) o);
				break;
			case java.sql.Types.BOOLEAN:
			case java.sql.Types.BIT:
				rows.setBoolean(i + 1, ((Boolean) o).booleanValue());
				break;
			case java.sql.Types.DATE:
				rows.setDate(i + 1, new java.sql.Date(((Date) o).getTime()));
				break;
			case java.sql.Types.TIME:
				rows.setTime(i + 1, new Time(((Date) o).getTime()));
				break;
			case java.sql.Types.TIMESTAMP:
				rows.setTimestamp(i + 1, new Timestamp(((Date) o).getTime()));
				break;
			case java.sql.Types.ARRAY:
				rows.setArray(i + 1, (Array) o);
				break;
			case java.sql.Types.REF:
				rows.setRef(i + 1, (Ref) o);
				break;
			case java.sql.Types.LONGVARBINARY:
			case java.sql.Types.BINARY:
			case java.sql.Types.VARBINARY:
			case java.sql.Types.DATALINK:
			case java.sql.Types.DISTINCT:
			case java.sql.Types.JAVA_OBJECT:
			case java.sql.Types.NULL:
			case java.sql.Types.OTHER:
			case java.sql.Types.STRUCT:
				rows.setObject(i + 1, o);
				break;
			default:
				rows.setString(i + 1, o.toString());
			}
		} catch (ClassCastException e) {
			try {
				rows.setString(i + 1, o.toString());
			} catch (Exception exp) {
				rows.setObject(i + 1, null);
				System.out.println("\n Set Casting exception");
			}
		} catch (NullPointerException e) {
			rows.setObject(i + 1, o);
		}

	}

	public int pageCount() {
		if (rowC == 0)
			return 1;
		if (rowC % rowPPage == 0)
			return rowC / rowPPage;
		else
			return (rowC / rowPPage) + 1;
	}

	public void close() {
		try {
			if (rows != null)
				rows.close();
		} catch (SQLException e) {
			System.out.println("\n Message:" + e.getMessage());
			System.out
					.println("\n WARNING: Rowset Connection can not be closed.");
		}
	}

	private void createMD() throws SQLException {
		ResultSetMetaData rsmd = rows.getMetaData();
		numberOfColumns = rsmd.getColumnCount();
		col_name = new String[numberOfColumns];
		tbl_name = new String[numberOfColumns];
		col_type = new int[numberOfColumns];

		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnName(i);
			// Hive no support
			if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
				tbl_name[i - 1] = rsmd.getTableName(i);
			}
			col_type[i - 1] = rsmd.getColumnType(i);
		}
	}

	public String[] getColName() {
		/* Hive appends table names to column name some time
		 * which will make some col name check to fail
		 */
		if (Rdbms_NewConn.get().getHValue("Database_Type") != null && 
					Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
			for (int i=0; i < col_name.length; i++) {
				int li = col_name[i].lastIndexOf('.'); //Table.colname
				if ( li != -1)
					col_name[i] = col_name[i].substring(li+1);
			}
		}
		return col_name;
	}

	public String[] getColType() {
		String[] colType = new String[col_type.length];
		for (int i = 0; i < col_type.length; i++)
			colType[i] = SqlType.getTypeName(col_type[i]);

		return colType;
	}

	public String[] getTableName() {
		return tbl_name;
	}

	public void moveToFirst() throws SQLException {
		rows.first();
	}

	synchronized public void insertRow(Object[] obj) throws SQLException {
		rows.moveToInsertRow();
		for (int j = 0; j < obj.length; j++)
			updateCell(j, obj[j]);
		rows.insertRow();
		rows.moveToCurrentRow();
	}
	
	/* This will give objects of next row in hive as it has rs.next() call 
	 * If we have to call from same row then rs.next() should not be there
	 */
	synchronized public Object getObject(int row, int col) throws SQLException {
		if (row > rowC)
			return null;
		// Hive does not support moving cursors
		if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
			rows.absolute(row);
		} else {
			if (rows.next() == false)
				return null;
		}
		Object obj = rows.getObject(col);
		if (obj == null) {
			try {
				String ret = rows.getString(col);
				return ret;
			} catch (Exception e) {
				System.out.println("Exeception:" + e.getLocalizedMessage());
				return null;
			}
		} else
			return obj;
	}

	/* This function will not move cursor */
	synchronized public Object getRowObject(int col) throws SQLException {

		Object obj = rows.getObject(col);
		if (obj == null) {
			try {
				String ret = rows.getString(col);
				return ret;
			} catch (Exception e) {
				System.out.println("Exeception:" + e.getLocalizedMessage());
				return null;
			}
		} else
			return obj;
	}
	
	public int getRowCount() {
		return rowC;
	}

	synchronized public Object[] getRow(int rowId) throws SQLException {
		Object[] objA = new Object[numberOfColumns];
		if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
			if (rows.next() == false) return null;
		}
		for (int i = 0; i < numberOfColumns; i++) {
			if (Rdbms_NewConn.get().getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
				objA[i] = getObject(rowId, i + 1);
			} else { // for hive
				// Hive does not support absolute function so we have 
				// to getObject while not moving the cursor
				// rowId has no meaning here it will always fetch next row
				objA[i] = getRowObject(i + 1);
			}
			
		}
		return objA;
	}

	synchronized public void deleteRow(int rowId) {
		try {
			rows.absolute(rowId);
			rows.deleteRow();
		} catch (Exception e) {
			System.out.println("\n RowSet Delete Error for RowID:" + rowId);
			System.out.println("\n Error Message:" + e.getMessage());
		}
	}

	public UpdatableJdbcRowsetImpl getRowset() {
		return rows;
	}

	synchronized public void setRowset(UpdatableJdbcRowsetImpl rowset) {
		rows = rowset;
	}

	public void setrowPPage(int rows) {
		rowPPage = rows;
	}

	public int getrowPPage() {
		return rowPPage;
	}
}
