package org.arrah.framework.profile;

/***********************************************
 *     Copyright to Vivek Kumar Singh 2013     *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is used for creating metadata and aggregate
 * data for the table selected
 * 
 * 
 */

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_conn;
import org.arrah.framework.rdbms.SqlType;
import org.arrah.framework.rdbms.TableRelationInfo;

public class TableMetaInfo {
	private static DatabaseMetaData dbmd;

	public TableMetaInfo() {
	}

	public static ReportTableModel populateTable(int i, int j, int k,
			ReportTableModel reporttable) {
		try {
			Rdbms_conn.openConn();
			dbmd = Rdbms_conn.getMetaData();
			switch (i) {
			case 1: // '\001'
				reporttable = IndexQuery(j, k, reporttable);
				break;

			case 2: // '\002'
				reporttable = MetaDataQuery(j, k, reporttable);
				break;

			case 3: // '\003'
				reporttable = PrivilegeQuery(j, k, reporttable);
				break;

			case 4: // '\004'
				reporttable = DataQuery(j, k,reporttable);
				break;
			}
			Rdbms_conn.closeConn();
		} catch (SQLException sqlexception) {
			System.out.println(sqlexception.getMessage());
			System.out.println("\n WARNING: Exception in Variable Query Panel ");
			return reporttable;
		} 
		return reporttable;
	}

	public static Vector<?>[] populateTable(int i, int j, int k,
			Vector<?> avector[])

	{
		try {
			Rdbms_conn.openConn();
			dbmd = Rdbms_conn.getMetaData();
			switch (i) {
			case 5: // '\005'
				avector = ColumnDataQuery(j, k, avector);
				// fall through

			default:
				Rdbms_conn.closeConn();
				break;
			}
		} catch (SQLException sqlexception) {
			System.out.println(sqlexception.getMessage());
			System.out
					.println("\n WARNING: Exception in Variable Column Data Query Panel ");
			return avector;
			
		}
		return avector;
	}

	private static ReportTableModel IndexQuery(int i, int j,
			ReportTableModel reporttable) throws SQLException {
		String s = Rdbms_conn.getHValue("Database_Catalog");
		s = "";
		String s1 = Rdbms_conn.getHValue("Database_SchemaPattern");
		s = s.compareTo("") != 0 ? s : null;
		s1 = s1.compareTo("") != 0 ? s1 : null;
		Vector<String> vector = Rdbms_conn.getTable();
		if (reporttable == null)
			reporttable = new ReportTableModel(new String[] { "Table",
					"Column", "Index", "Type", "Qualifier", "IsUnique",
					"Asc/Dsc", "Cardinality", "Pages", "Filter" });
		else
			reporttable.cleanallRow();
		try {
		for (int k = i; k < j; k++) {
			String s2 = (String) vector.elementAt(k);
			ResultSet resultset = dbmd.getIndexInfo(s, s1, s2, false, true);

			while (resultset.next()) {
				boolean flag = resultset.getBoolean(4);
				String s3 = !flag ? "False" : "True";
				String s4 = resultset.getString(5);
				String s5 = resultset.getString(6);
				String s6 = "";
				short word0 = resultset.getShort(7);
				switch (word0) {
				case 0: // '\0'
					s6 = "Statistic";
					break;

				case 1: // '\001'
					s6 = "Clustered";
					break;

				case 2: // '\002'
					s6 = "Hashed";
					break;

				default:
					s6 = "Type UnKnown";
					break;
				}
				String s7 = resultset.getString(9);
				String s8 = resultset.getString(10);
				String s9 = resultset.getString(11);
				String s10 = resultset.getString(12);
				String s11 = resultset.getString(13);
				if (s7 != null && s5 != null) {
					String as[] = { s2, s7, s5, s6, s4, s3, s8, s9, s10, s11 };
					reporttable.addFillRow(as);
				}
			} // End of While
			resultset.close();
		} } catch (SQLException ee) {
			System.out.println("Exception:" + ee.getMessage());
			return reporttable;
			
		}

		return reporttable;
	}

	private static ReportTableModel MetaDataQuery(int i, int j,
			ReportTableModel reporttable) throws SQLException {
		String s = Rdbms_conn.getHValue("Database_SchemaPattern");
		String s1 = Rdbms_conn.getHValue("Database_Catalog");
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector<String> vector = Rdbms_conn.getTable();
		ResultSet resultset = null;
		if (reporttable == null)
			reporttable = new ReportTableModel(new String[] { "Table",
					"Column", "DBType","SQLType","Size", "Precision", "Radix", "Remark",
					"Default", "Bytes", "Ordinal Pos", "Nullable" });
		else
			reporttable.cleanallRow();

		for (int k = i; k < j; k++) {
			String s2 = (String) vector.elementAt(k);
			resultset = dbmd.getColumns(s1, s, s2, null);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					String s4 = resultset.getString(4);
					int i5 = resultset.getInt(5);
					String s52 = SqlType.getTypeName(i5);
					String s5 = resultset.getString(6);
					String s6 = resultset.getString(7);
					String s7 = resultset.getString(9);
					String s8 = resultset.getString(10);
					String s9 = resultset.getString(12);
					String s10 = resultset.getString(13);
					String s11 = resultset.getString(16);
					String s12 = resultset.getString(17);
					String s13 = resultset.getString(18);
					String as[] = { s2, s4, s5,s52, s6, s7, s8, s9, s10, s11, s12,s13 };
					reporttable.addFillRow(as);
				}
			} // End of While
			resultset.close();
		}

		return reporttable;
	}

	private static ReportTableModel PrivilegeQuery(int i, int j,
			ReportTableModel reporttable) throws SQLException {
		String s = Rdbms_conn.getHValue("Database_SchemaPattern");
		String s1 = Rdbms_conn.getHValue("Database_Catalog");
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		int k = 0;
		Vector<String> vector = Rdbms_conn.getTable();
		ResultSet resultset = null;
		if (reporttable == null)
			reporttable = new ReportTableModel(new String[] { "Table",
					"Grantor", "Grantee", "Privileges", "Grantable" });
		else
			reporttable.cleanallRow();
		for (int l = i; l < j; l++) {
			String s2 = (String) vector.elementAt(l);
			resultset = dbmd.getTablePrivileges(s1, s, s2);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					k++;
					String s4 = resultset.getString(4);
					String s5 = resultset.getString(5);
					String s6 = resultset.getString(6);
					String s7 = resultset.getString(7);
					String as[] = { s3, s4, s5, s6, s7 };
					reporttable.addFillRow(as);
				}
			} // End of While
			resultset.close();
		}
		
		if (k == 0) System.out.println("Tables do not Exist \n Or You might not have permisson to run this query ");
		return reporttable;
	}

	private static ReportTableModel DataQuery(int i, int j,ReportTableModel reporttable
			) throws SQLException {
		String s = Rdbms_conn.getHValue("Database_SchemaPattern");
		String s1 = Rdbms_conn.getHValue("Database_Catalog");
		s1 = "";
		String s2 = Rdbms_conn.getHValue("Database_DSN");
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector<String> vector = Rdbms_conn.getTable();
		String s3 = Rdbms_conn.getDBType();
		if (reporttable == null)
		 reporttable  = new ReportTableModel(new String[] { "Table",
					"Column", "Record", "Unique", "Pattern", "Null", "Zero",
					"Empty","Max","Min","Avg" });
		else
			reporttable.cleanallRow();
		
		synchronized (Rdbms_conn.class){
		for (int k = i; k < j; k++) {
			// for ORA:1000 error maximum number of open cursor
			try {
			if (s3.compareToIgnoreCase("oracle_native") == 0) {
				Rdbms_conn.openConn();
				dbmd = Rdbms_conn.getMetaData();
			} 
			String s17 = (String) vector.elementAt(k);
			Vector<String> vector1 = new Vector<String>();
			ResultSet resultset = dbmd.getColumns(s1, s, s17, null);
			do {
				if (!resultset.next())
					break;
				String s18 = resultset.getString(3);
				if (s18.equals(s17)) {
					String s19 = resultset.getString(4);
					vector1.add(s19);
				}
			} while (true);
			resultset.close();
			if (s3.compareToIgnoreCase("oracle_native") == 0) {
				Rdbms_conn.closeConn();
			} 
			String as[];
			for (Enumeration<String> enumeration = vector1.elements(); enumeration
					.hasMoreElements(); reporttable.addFillRow(as)) {
				String s10 = "0";
				String s11 = "0";
				String s12 = "0";
				String s13 = "0";
				String s14 = "0";
				String s15 = "0";
				String top_sel_query_v = "";
				String bot_sel_query_v = "";
				String avg_query_v = "";
				
				String s20 = (String) enumeration.nextElement();
				QueryBuilder querybuilder = new QueryBuilder(s2, s17, s20, s3);
				String s4 = querybuilder.count_query_w(false, "row_count");
				String s5 = querybuilder.count_query_w(true, "row_count");
				String s6 = querybuilder.get_nullCount_query_w("Null");
				String s7 = querybuilder.get_zeroCount_query_w("0");
				String s8 = querybuilder.get_zeroCount_query_w("''");
				String s9 = querybuilder.get_pattern_query();
				String top_sel_query_c = querybuilder.top_query(false,"top_count", "1");
				String bot_sel_query_c = querybuilder.bottom_query(false,"bot_count", "1");
				//index,Count,Avg,Max,Min,Sum Example : 1NYNNNN
				String avg_query_c = querybuilder.aggr_query("1NYNNNN", 0, "0", "0");
				if (s3.compareToIgnoreCase("oracle_native") == 0)
					Rdbms_conn.openConn();
				try {
					for (resultset = Rdbms_conn.runQuery(s4); resultset.next();)
						s10 = resultset.getString("row_count");

					resultset.close();
				} catch (SQLException sqlexception) {
					s10 = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(s5); resultset.next();)
						s11 = resultset.getString("row_count");

					resultset.close();
				} catch (SQLException sqlexception1) {
					s11 = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(s6); resultset.next();)
						s12 = resultset.getString("equal_count");

					resultset.close();
				} catch (SQLException | NullPointerException sqlexception2) {
					s12 = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(s7); resultset.next();)
						s13 = resultset.getString("equal_count");

					resultset.close();
				} catch (SQLException sqlexception3) {
					s13 = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(s8); resultset.next();)
						s14 = resultset.getString("equal_count");

					resultset.close();
				} catch (SQLException sqlexception4) {
					s14 = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(s9); resultset.next();)
						s15 = resultset.getString("row_count");

					resultset.close();
				} catch (SQLException sqlexception5) {
					s15 = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(top_sel_query_c); resultset.next();)
						top_sel_query_v = resultset.getString("top_count");
					
					resultset.close();
				} catch (SQLException s_exp) {
					top_sel_query_v = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(bot_sel_query_c); resultset.next();)
						bot_sel_query_v = resultset.getString("bot_count");
					
					resultset.close();
				} catch (SQLException s_exp) {
					bot_sel_query_v = "N/A";
				}
				try {
					for (resultset = Rdbms_conn.runQuery(avg_query_c); resultset.next();)
						avg_query_v = resultset.getString("avg_count");
					
					resultset.close();
				} catch (SQLException s_exp) {
					avg_query_v = "N/A";
				}
				as = (new String[] { s17, s20, s10, s11, s15, s12, s13, s14,top_sel_query_v,bot_sel_query_v,avg_query_v });
				
				if (s3.compareToIgnoreCase("oracle_native") == 0)
					Rdbms_conn.closeConn(); 
			} } catch (SQLException ee) {
				return reporttable;
			}
		} }// end of for loop

		return reporttable;
	}

	private static Vector<?>[] ColumnDataQuery(int i, int j, Vector avector[])
			throws SQLException {
		String s = Rdbms_conn.getHValue("Database_SchemaPattern");
		String s1 = Rdbms_conn.getHValue("Database_Catalog");
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector<String> vector = Rdbms_conn.getTable();
		avector = new Vector<?>[2];
		avector[0] = new Vector<String>();
		avector[1] = new Vector<Integer>();
		int k = 0;

		for (int l = i; l < j; l++) {
			String s2 = (String) vector.elementAt(l);
			ResultSet resultset = dbmd.getColumns(s1, s, s2, null);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					String s4 = resultset.getString(4);
					int i1 = resultset.getInt(5);
					avector[0].add(k, s4);
					avector[1].add(k, new Integer(i1));
					k++;
				}
			} // End of while
			resultset.close();
		}
		return avector;
	}

	public static TableRelationInfo getTableRelationInfo(String cat,
			String sch, String table) throws SQLException {
		TableRelationInfo TableRelationInfo = new TableRelationInfo(table);
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		if (Rdbms_conn.getDBType().compareToIgnoreCase("ms_access") == 0) {
			ResultSet resultset9 = dbmd.getIndexInfo(cat, sch, table, false,
					true);
			do {
				if (!resultset9.next())
					break;
				String s57 = resultset9.getString(9);
				String s63 = resultset9.getString(6);
				String s71 = resultset9.getString(3);
				if (s57 != null && s63 != null)
					if (s63.compareToIgnoreCase("primarykey") == 0) {
						TableRelationInfo.pk[TableRelationInfo.pk_c] = s57;
						TableRelationInfo.pk_index[TableRelationInfo.pk_c] = s63;
						TableRelationInfo.hasPKey = true;
						TableRelationInfo.pk_c++;
						TableRelationInfo.isRelated = true;
					} else if (s63.endsWith(table)) {
						TableRelationInfo.fk[TableRelationInfo.fk_c] = s57;
						TableRelationInfo.fk_pKey[TableRelationInfo.fk_c] = null;
						TableRelationInfo.fk_pTable[TableRelationInfo.fk_c] = s63
								.substring(0, s63.lastIndexOf(table));
						TableRelationInfo.hasFKey = true;
						TableRelationInfo.fk_c++;
						TableRelationInfo.isRelated = true;
					}
			} while (true);
			resultset9.close();
		} else {
			int l3 = 0;
			ResultSet resultset10 = dbmd.getPrimaryKeys(cat, sch, table);
			do {
				if (!resultset10.next())
					break;
				String s64 = resultset10.getString(4);
				String s72 = resultset10.getString(6);
				if (s64 != null && s72 != null) {
					TableRelationInfo.pk[l3] = s64;
					TableRelationInfo.pk_index[l3] = s72;
					TableRelationInfo.hasPKey = true;
					l3++;
					TableRelationInfo.pk_c++;
				}
			} while (true);
			resultset10.close();

			l3 = 0;
			for (resultset10 = dbmd.getImportedKeys(cat, sch, table); resultset10
					.next();) {
				String s65 = resultset10.getString(3);
				String s73 = resultset10.getString(4);
				String s79 = resultset10.getString(7);
				String s83 = resultset10.getString(8);
				TableRelationInfo.fk[l3] = s83;
				TableRelationInfo.fk_pKey[l3] = s73;
				TableRelationInfo.fk_pTable[l3] = s65;
				TableRelationInfo.hasFKey = true;
				TableRelationInfo.fk_c++;
				TableRelationInfo.isRelated = true;
				l3++;
			}
			resultset10.close();

			l3 = 0;
			for (resultset10 = dbmd.getExportedKeys(cat, sch, table); resultset10
					.next();) {
				String s66 = resultset10.getString(3);
				String s74 = resultset10.getString(4);
				String s80 = resultset10.getString(7);
				String s84 = resultset10.getString(8);
				TableRelationInfo.pk_ex[l3] = s74;
				TableRelationInfo.pk_exKey[l3] = s84;
				TableRelationInfo.pk_exTable[l3] = s80;
				TableRelationInfo.hasExpKey = true;
				TableRelationInfo.exp_c++;
				TableRelationInfo.isRelated = true;
				l3++;
			}
			resultset10.close();
		}
		Rdbms_conn.closeConn();
		return TableRelationInfo;
	}
	
	//Give info in RTM
	public static ReportTableModel tableKeyInfo(String table) 
			throws SQLException {
		String sch = Rdbms_conn.getHValue("Database_SchemaPattern");
		String cat = Rdbms_conn.getHValue("Database_Catalog");
		cat="";
		cat = cat.compareTo("") != 0 ? cat : null;
		sch = sch.compareTo("") != 0 ? sch : null;

		TableRelationInfo TableRelationInfo = TableMetaInfo
				.getTableRelationInfo(cat, sch, table);
		ReportTableModel rtm = new ReportTableModel(new String[] { "Primary Key",
				"Foreign Key", "Foreign Table", "Exported Key",
				"Exported Table" });
		int rowI = 0;

		for (int i = 0; i < TableRelationInfo.pk_c; i++) {
			rtm.addRow();
			rowI = i;
			rtm.setValueAt(TableRelationInfo.pk[i], i, 0);
		}
		for (int i = 0; i < TableRelationInfo.fk_c; i++) {
			if (i > rowI) {
				rtm.addRow();
				rowI = i;
			}
			rtm.setValueAt(TableRelationInfo.fk[i], i, 1);
			rtm.setValueAt(TableRelationInfo.fk_pTable[i], i, 2);
		}
		for (int i = 0; i < TableRelationInfo.exp_c; i++) {
			if (i > rowI) {
				rtm.addRow();
				rowI = i;
			}
			rtm.setValueAt(TableRelationInfo.pk_ex[i], i, 3);
			rtm.setValueAt(TableRelationInfo.pk_exTable[i], i, 4);
		}
		
		return rtm;

	}
	

	public static ReportTableModel getSuperTableInfo(String cat, String sch,
			String _table) throws SQLException {

		ReportTableModel _rt = new ReportTableModel(new String[] { "Table",
				"Super Table" });
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();

		ResultSet rs = dbmd.getSuperTables(cat, sch, _table);
		while (rs.next()) {
			String table = rs.getString(3);
			if (_table.equals(table)) {
				String[] row = new String[] { table, rs.getString(4) };
				_rt.addFillRow(row);
			}
		}
		rs.close();
		Rdbms_conn.closeConn();
		return _rt;
	}

	public static ReportTableModel getColumnDefaultValue(String cat,
			String sch, String _table) throws SQLException {
		ReportTableModel _rt = new ReportTableModel(new String[] { "Column",
				"Default Value" });
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();

		ResultSet rs = dbmd.getColumns(cat, sch, _table, null);
		while (rs.next()) {
			String table = rs.getString(3);
			if (_table.equals(table)) {
				String col = rs.getString(4);
				String def = rs.getString(13);
				String[] row = new String[] { col, def };
				_rt.addFillRow(row);
			}
		}
		rs.close();
		Rdbms_conn.closeConn();
		return _rt;
	}
	/* Do like query on given table and specified columns */
	
	synchronized public static ReportTableModel queryTable(String query, String tableName, Vector<String> colName) {
		
		ReportTableModel _rt = new ReportTableModel(new String[] { "Count","Table" });
			QueryBuilder qb = new QueryBuilder(
					Rdbms_conn.getHValue("Database_DSN"), tableName, Rdbms_conn.getDBType());
			
			String toQuery = qb.get_like_table_cols(query,colName, true);
			int matcount =0;
			if (toQuery == null || "".equals(toQuery)) return _rt;
			
			try {	
				synchronized (Rdbms_conn.class) {
				Rdbms_conn.openConn();
				ResultSet rs = Rdbms_conn.runQuery(toQuery);
				
					while (rs.next())
						 matcount = rs.getInt(1);
					
				 rs.close();
				 Rdbms_conn.closeConn();  
				}
			} catch (SQLException ee) {
				System.out.println("\n ResultSet SQL Error :" + ee.getMessage());
			}	finally {
			}
			try {
				Rdbms_conn.closeConn();
			} catch (SQLException ee) {
				System.out.println("\n SQL Error:" + ee.getMessage());
			}
			
			Object[] obj = new Object[2];
			obj[0] = matcount;
			obj[1] = tableName;
			_rt.addFillRow(obj);
		return _rt;
	}

}
