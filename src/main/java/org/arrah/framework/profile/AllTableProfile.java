package org.arrah.framework.profile;

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
 * This is Table  which shows profiler data
 * for all tables and columns in Table structure
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

public class AllTableProfile {
	private ReportTableModel __rt = new ReportTableModel(new String[] {
			"Table", "Column", "Record", "Unique", "Pattern", "Null", "Zero",
			"Empty","Max","Min" }); // it is extended from Panel

	public AllTableProfile() {

		try {
			createRows();
		} catch (SQLException e) { // Handle exception
			System.out.println("\n SQL exception in Data Info");
			System.out.println(e.getMessage());
		}
	}

	private void createRows() throws SQLException {
		Vector<String> vc = new Vector<String>(); // For table

		// Open the connection
		Rdbms_conn.openConn();
		DatabaseMetaData dbmd = Rdbms_conn.getMetaData();

		// Get the metaDataSet
		String s_pattern = Rdbms_conn.getHValue("Database_SchemaPattern");
		String t_pattern = Rdbms_conn.getHValue("Database_TablePattern");
		String t_type = Rdbms_conn.getHValue("Database_TableType");
		String n_catalog = Rdbms_conn.getHValue("Database_Catalog");
		n_catalog = ""; // Oracle ODBC does not support
		String d_dsn = Rdbms_conn.getHValue("Database_DSN");

		// Add table names here
		// TODO - need to make it multi threaded

		// Get tables
		ResultSet rs = dbmd.getTables(
				n_catalog.compareTo("") == 0 ? n_catalog = null : n_catalog,
				s_pattern.compareTo("") == 0 ? s_pattern = null : s_pattern,
				t_pattern.compareTo("") == 0 ? t_pattern = null : t_pattern,
				t_type.split(","));

		String tbl = "";
		while (rs.next()) {
			tbl = rs.getString(3);
			vc.add(tbl);
		} // MS SQL Does not support multiple query
		rs.close();
		// Add count to top
		String dbType = Rdbms_conn.getDBType();

		ResultSet rs_col;
		Vector<String> vc_c;
		QueryBuilder c_prof;
		String all_c, dist_c, null_c, zero_c, empty_c, pattern_c,top_sel_query_c,bot_sel_query_c;
		String all_v, dist_v, null_v, zero_v, empty_v, pattern_v,top_sel_query_v,bot_sel_query_v;

		Enumeration<String> e = vc.elements();
		while (e.hasMoreElements()) {
			tbl = (String) e.nextElement();
			vc_c = new Vector<String>(); // For columns
			rs_col = dbmd.getColumns(n_catalog, s_pattern, tbl, null);
			while (rs_col.next()) {
				String tmp = rs_col.getString(4);
				vc_c.add(tmp);
			}
			rs_col.close();

			// In loop for all the children of table node
			Enumeration<String> et = vc_c.elements();
			while (et.hasMoreElements()) {
				all_v = "0";
				dist_v = "0";
				null_v = "0";
				zero_v = "0";
				empty_v = "0";
				pattern_v = "0";
				top_sel_query_v = "";
				bot_sel_query_v = "";

				String col = (String) et.nextElement();

				c_prof = new QueryBuilder(d_dsn, tbl, col, dbType);
				all_c = c_prof.count_query_w(false, "row_count");
				dist_c = c_prof.count_query_w(true, "row_count");
				null_c = c_prof.get_nullCount_query_w("Null");
				zero_c = c_prof.get_zeroCount_query_w("0");
				empty_c = c_prof.get_zeroCount_query_w("''");
				pattern_c = c_prof.get_pattern_query();
				top_sel_query_c = c_prof.top_query(false,"top_count", "1");
				bot_sel_query_c = c_prof.bottom_query(false,"bot_count", "1");

				try {
					rs_col = Rdbms_conn.runQuery(all_c);
					while (rs_col.next()) {
						all_v = rs_col.getString("row_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					all_v = "N/A";
				}

				try {
					rs_col = Rdbms_conn.runQuery(dist_c);
					while (rs_col.next()) {
						dist_v = rs_col.getString("row_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					dist_v = "N/A";
				}
				try {
					rs_col = Rdbms_conn.runQuery(null_c);
					while (rs_col.next()) {
						null_v = rs_col.getString("equal_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					null_v = "N/A";
				}
				try {
					rs_col = Rdbms_conn.runQuery(zero_c);
					while (rs_col.next()) {
						zero_v = rs_col.getString("equal_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					zero_v = "N/A";
				}
				try {
					rs_col = Rdbms_conn.runQuery(empty_c);
					while (rs_col.next()) {
						empty_v = rs_col.getString("equal_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					empty_v = "N/A";
				}
				try {
					rs_col = Rdbms_conn.runQuery(pattern_c);
					while (rs_col.next()) {
						pattern_v = rs_col.getString("row_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					pattern_v = "N/A";
				}
				try {
					rs_col = Rdbms_conn.runQuery(top_sel_query_c);
					while (rs_col.next()) {
						top_sel_query_v = rs_col.getString("top_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					top_sel_query_v = "N/A";
				}
				try {
					rs_col = Rdbms_conn.runQuery(bot_sel_query_c);
					while (rs_col.next()) {
						bot_sel_query_v = rs_col.getString("bot_count");
					}
					rs_col.close();
				} catch (SQLException s_exp) {
					bot_sel_query_v = "N/A";
				}
				
				String[] row = { tbl, col, all_v, dist_v, pattern_v, null_v,
						zero_v, empty_v,top_sel_query_v,bot_sel_query_v};
				__rt.addFillRow(row);
			} // Close column
		} // Close table
		Rdbms_conn.closeConn();
	}

	public ReportTableModel getTable() {
		return __rt;
	}

}
