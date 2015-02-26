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

/* This file is used for doing operations that is
 * across the tables - like load data into multiple
 * tables, bring data from multiple tables, do match 
 * across table
 * 
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.JDBCRowset;
import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_conn;
import org.arrah.gui.swing.ConsoleFrame;

public class InterTableInfo {

	public static String[] getMatchCount(String table1, String col1,
			String table2, String col2, byte multiple, int mX) {

		QueryBuilder qb = new QueryBuilder(
				Rdbms_conn.getHValue("Database_DSN"), table1, col1,
				Rdbms_conn.getDBType());
		qb.setCTableCol(table2, col2);
		String q1 = qb.get_match_count(multiple, mX);
		String st[] = new String[6];

		try {
			Rdbms_conn.openConn();

			ResultSet rs = Rdbms_conn.runQuery(q1);
			while (rs.next()) {
				String row_count = rs.getString("row_count");
				st[0] = row_count;
				String row_sum = rs.getString("row_sum");
				st[3] = row_sum;

			}
			rs.close();

			q1 = qb.get_nullCount_query_w("Null");
			rs = Rdbms_conn.runQuery(q1);
			while (rs.next()) {
				String null_count = rs.getString("equal_count");
				st[1] = null_count;
			}
			rs.close();

			q1 = qb.count_query_w(true, "row_count");
			rs = Rdbms_conn.runQuery(q1);
			while (rs.next()) {
				String row_count = rs.getString("row_count");
				st[2] = row_count;
			}
			rs.close();

			qb = new QueryBuilder(Rdbms_conn.getHValue("Database_DSN"), table2,
					col2, Rdbms_conn.getDBType());

			q1 = qb.get_nullCount_query_w("Null");
			rs = Rdbms_conn.runQuery(q1);
			while (rs.next()) {
				String null_count = rs.getString("equal_count");
				st[4] = null_count;
			}
			rs.close();

			q1 = qb.count_query_w(false, "row_count");
			rs = Rdbms_conn.runQuery(q1);
			while (rs.next()) {
				String row_count = rs.getString("row_count");
				st[5] = row_count;
			}
			rs.close();

			Rdbms_conn.closeConn();
		} catch (SQLException e) {
			System.out.println("\n match count execution failed");
			System.out.println(e.getMessage());
		}
		return st;
	}

	/*
	 * load the multiple query in String[] ReportTableModel will have to data to
	 * load unique_table_s will give table name to load while _ht will give the
	 * index of Table+Col combination
	 */
	public static void loadQuery(final String[] query,
			final ReportTableModel _rt, final Vector<String> unique_table_s,
			final Hashtable<String, Integer> _ht) {
		final int count = _rt.getModel().getRowCount();
		
		// This method is not supported for hive
		if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
			ConsoleFrame.addText("\n Load Query is not supported for Hive Data Storage");
			JOptionPane.showMessageDialog(null,
					"Load Query is not supported for Hive Data Storage", "Hive Support Error",
					JOptionPane.ERROR_MESSAGE);
			
			return;
		} else {

		Thread[] tid = new Thread[query.length];

		for (int qIndex = 0; qIndex < query.length; qIndex++) {
			final int cIndex = qIndex; // I can not make cIndex final
			tid[cIndex] = new Thread(new Runnable() {
				public void run() {
					int fcount = 0;
					String tbl[] = null;
					String col[] = null;
					boolean insertflag = false;
					try {
						JDBCRowset rs = new JDBCRowset(query[cIndex], 0, true); // Fetch
																				// no
																				// Row
																				// as
																				// we
																				// have
																				// to
																				// Insert
						tbl = rs.getTableName(); /*
												 * Some Native driver does not
												 * return Table Name
												 */
						col = rs.getColName();
						/* Ready to Insert */
						rs.moveToFirst();

						for (int c = 0; c < count; c++) {
							Object[] obj = new Object[col.length];
							for (int i = 0; i < col.length; i++) {
								Integer index = _ht.get(unique_table_s
										.get(cIndex) + col[i]);
								if (index == null)
									continue;
								obj[i] = _rt.getModel().getValueAt(c,
										index.intValue());
							}
							try {
								rs.insertRow(obj);
								insertflag = true;
							} catch (SQLException sql_e) {
								System.out.println("\n Row Id:" + (c + 1)
										+ " Error-" + sql_e.getMessage()
										+ " For Table: "
										+ unique_table_s.get(cIndex));
								fcount++;
								continue;
							}
						} // For loop
						rs.close();
					} catch (SQLException e) {
						System.out.println("\n Error-" + e.getMessage()
								+ " For Table: " + unique_table_s.get(cIndex));
					}
					if (insertflag==true)
					System.out.println("\n " + (count - fcount) + " of Total "
							+ count + " Rows Inserted Successfully in table :"
							+ unique_table_s.get(cIndex));
				}
			});
			tid[cIndex].start();
		}

		for (int i = 0; i < query.length; i++) {
			try {
				tid[i].join();
			} catch (Exception e) {
				System.out.println("\n Thread Error:" + e.getMessage());
			}
		}
		} // Not for HIVE
	} // End of Load query

	/*
	 * Synch the multiple query in String[] ReportTableModel will have to data
	 * to be synched String and Column will have name of tables and columns
	 * while _ht will give the index of Table+Col combination QueryString will
	 * have condition to fetch the data
	 */
	public static void synchQuery(final String[] query,
			final ReportTableModel _rt, final Vector<String> table_s,
			final Vector<String> column_s,
			final Hashtable<String, Integer> _ht, final String[] queryString) {

		final int count = _rt.getModel().getRowCount();
		final Object[][] stored = new Object[query.length][count];
		final int[] cI = new int[query.length];
		Thread[] tid = new Thread[query.length];
		
		// Hive Thrift server has issues with Multi-threaded programme
		if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {

		for (int qindex = 0; qindex < query.length; qindex++) {
			final int cIndex = qindex;
			 tid[cIndex] = new Thread(new Runnable() {
				public void run() {
					String tbl = table_s.get(cIndex);
					String col = column_s.get(cIndex);
					String newQuery = query[cIndex];
					Integer tab_index = _ht.get(tbl + col);
					if (tab_index == null)
						return;
					if (!(queryString[tab_index] == null || ""
							.equals(queryString[tab_index])))
						newQuery = newQuery + " WHERE "
								+ queryString[tab_index];
					cI[cIndex] = tab_index;

					try {
						JDBCRowset rs = new JDBCRowset(newQuery, count, false);
						for (int c = 0; c < count; c++)
							stored[cIndex][c] = rs.getObject(c + 1, 1);
						rs.close();
					} catch (SQLException e) {
						System.out.println("\n Error-" + e.getMessage()
								+ " For Query: " + newQuery);
					}
				}
			});
			tid[cIndex].start();
		}
		for (int i = 0; i < query.length; i++) {
			try {
				tid[i].join();
			} catch (Exception e) {
				System.out.println("\n Thread Error:" + e.getMessage());
			}
		}
		} else { // For Hive
			
			for (int qindex = 0; qindex < query.length; qindex++) {
				final int cIndex = qindex;
				String tbl = table_s.get(cIndex);
				String col = column_s.get(cIndex);
				String newQuery = query[cIndex];
				Integer tab_index = _ht.get(tbl + col);
				
				if (tab_index == null)
					return;
				if (!(queryString[tab_index] == null || ""
						.equals(queryString[tab_index])))
						newQuery = newQuery + " WHERE "
								+ queryString[tab_index];
					cI[cIndex] = tab_index;

				try {
					JDBCRowset rs = new JDBCRowset(newQuery, count, false);
					for (int c = 0; c < count; c++)
						stored[cIndex][c] = rs.getObject(c + 1, 1);
						rs.close();
					} catch (SQLException e) {
						System.out.println("\n Error-" + e.getMessage()
							+ " For Query: " + newQuery);
					}
			
		} } // End of Hive
		
		for (int c = 0; c < count; c++)
			for (int j = 0; j < query.length; j++)
				_rt.getModel().setValueAt(stored[j][c], c, cI[j]);
	} // End of Synch Query

}
