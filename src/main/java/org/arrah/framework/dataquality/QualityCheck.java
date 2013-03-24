package org.arrah.framework.dataquality;

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
 * This class is used for validating data quality
 * rules like search/replace, null replace, pattern
 * match etc.
 *
 */
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.JDBCRowset;
import org.arrah.framework.rdbms.SqlType;
import org.arrah.framework.util.StringCaseFormatUtil;

public class QualityCheck {
	// Constructor
	private int matchI = -1;
	private Vector<Integer> mrowI;

	public QualityCheck() {
		// Do nothing
	}

	// This function find a key matching the filter and replaces with
	// value of the filter (that is in Hashtable
	// String condition will tell if the query has to run with some condition

	public ReportTableModel searchReplace(JDBCRowset rows, String col,
			Hashtable<String, String> filter) throws SQLException {

		String[] col_name = rows.getColName();
		String[] colType = rows.getColType();
		String[] add_col = new String[col_name.length + 1];

		for (int j = 0; j < col_name.length; j++) {
			if (col.equals(col_name[j])) {
				matchI = j;
				break;
			}
		}
		if (matchI < 0) {
			System.out.println("Selected Column is Not matching Database Columns");
			return null;

		}
		Object replace = null;
		String metaType = SqlType.getMetaTypeName(colType[matchI]);

		add_col[0] = col_name[matchI] + " Editable";
		for (int i = 0; i < col_name.length; i++) {
			add_col[i + 1] = col_name[i];
		}
		ReportTableModel rt = new ReportTableModel(add_col);
		mrowI = new Vector<Integer>();

		int rowC = rows.getRowCount();
		int mrowC = 0;

		for (int i = 0; i < rowC; i++) {
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null)
				continue;
			String value = obj.toString().trim().replaceAll("\\s+", " "); // Split
																			// for
																			// White
																			// Space
			String valueTok[] = value.split(" ");
			Enumeration<String> en = filter.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement().toString();
				boolean matchFound = false;

				for (int j = 0; j < valueTok.length; j++) {
					try {
						if (Pattern.matches(key, valueTok[j]) == true) {
							String newvalue = (String) filter.get(key);
							valueTok[j] = newvalue;
							matchFound = true;
							continue;
						}
					} catch (PatternSyntaxException pe) {
						System.out.println("\n Pattern Compile Exception:"
								+ pe.getMessage());
						break;
					}
				}
				if (matchFound == true) {
					try {
						String newValue = "";
						for (int j = 0; j < valueTok.length; j++) {
							if (newValue.equals("") == false)
								newValue += " "; // Put space
							newValue += valueTok[j];
						}
						try {
							if (metaType.toUpperCase().contains("NUMBER")) {
								replace = Double.parseDouble(newValue);
							} else if (metaType.toUpperCase().contains("DATE")) {
								replace = new SimpleDateFormat("dd-MM-yyyy")
										.parse(newValue);
							} else {
								replace = new String(newValue);
							}
						} catch (Exception exp) {
							System.out
									.println("\n WANING: Could not Parse Input String:"
											+ newValue);
						}
						Object[] objA = rows.getRow(i + 1);
						Object[] add_obj = new Object[objA.length + 1];
						add_obj[0] = replace;
						for (int k = 0; k < objA.length; k++) {
							add_obj[k + 1] = objA[k];
						}
						rt.addFillRow(add_obj);
						mrowI.add(mrowC++, (i + 1));
						break;
					} catch (SQLException se) {
						System.out.println("\n Exception :" + se.getMessage());
						continue;
					} catch (Exception ex) {
						System.out.println("\n Exception :" + ex.getMessage());
						ex.printStackTrace();
					}
				} // if match found
			}
		}
		return rt;
	}

	public ReportTableModel nullReplace(JDBCRowset rows, String col,
			String replaceWith) throws SQLException {

		String[] col_name = rows.getColName();
		String[] colType = rows.getColType();
		String[] add_col = new String[col_name.length + 1];

		for (int j = 0; j < col_name.length; j++) {
			if (col.equals(col_name[j])) {
				matchI = j;
				break;
			}
		}
		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;
		}

		Object replace = null;
		String metaType = SqlType.getMetaTypeName(colType[matchI]);
		try {
			if (metaType.toUpperCase().contains("NUMBER")) {
				replace = Double.parseDouble(replaceWith);
			} else if (metaType.toUpperCase().contains("DATE")) {
				replace = new SimpleDateFormat("dd-MM-yyyy").parse(replaceWith);
			} else {
				replace = new String(replaceWith);
			}
		} catch (Exception exp) {
			System.out.println("\n WANING: Could not Parse Input String:"
					+ replaceWith);
		}
		add_col[0] = col_name[matchI] + " Editable";
		for (int i = 0; i < col_name.length; i++) {
			add_col[i + 1] = col_name[i];
		}
		ReportTableModel rt = new ReportTableModel(add_col);
		mrowI = new Vector<Integer>();

		int rowC = rows.getRowCount();
		int mrowC = 0;

		for (int i = 0; i < rowC; i++) {
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null || "".equals(obj.toString())) {
				try {
					Object[] objA = rows.getRow(i + 1);
					Object[] add_obj = new Object[objA.length + 1];
					add_obj[0] = replace;
					for (int k = 0; k < objA.length; k++) {
						add_obj[k + 1] = objA[k];
					}
					rt.addFillRow(add_obj);
					mrowI.add(mrowC++, (i + 1));
				} catch (SQLException se) {
					System.out.println("\n Exception :" + se.getMessage());
					continue;
				} catch (Exception ex) {
					System.out.println("\n Exception :" + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
		return rt;
	}

	public ReportTableModel patternMatch(JDBCRowset rows, String col,
			String type, Object[] pattern, boolean isMatch) throws SQLException {
		String[] col_name = rows.getColName();
		String[] add_col = new String[col_name.length + 1];

		for (int j = 0; j < col_name.length; j++) {
			if (col.equals(col_name[j])) {
				matchI = j;
				break;
			}
		}
		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;
		}
		add_col[0] = col_name[matchI] + " Editable";
		for (int i = 0; i < col_name.length; i++) {
			add_col[i + 1] = col_name[i];
		}
		ReportTableModel rt = new ReportTableModel(add_col);
		mrowI = new Vector<Integer>();

		int rowC = rows.getRowCount();
		int mrowC = 0;
		for (int i = 0; i < rowC; i++) {
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null)
				continue;
			boolean isObjMatch = false;

			if (type.equals("Number")) {
				Double d = FormatCheck.parseNumber(obj.toString(), pattern);
				if (d != null && isMatch == true)
					isObjMatch = true;
				if (d == null && isMatch == false)
					isObjMatch = true;
			} else if (type.equals("Date")) {
				Date d = FormatCheck.parseDate(obj.toString(), pattern);
				if (d != null && isMatch == true)
					isObjMatch = true;
				if (d == null && isMatch == false)
					isObjMatch = true;
			} else {
				Object d = FormatCheck.parseString(obj.toString(), pattern);
				if (d != null && isMatch == true)
					isObjMatch = true;
				if (d == null && isMatch == false)
					isObjMatch = true;

			}
			if (isObjMatch == true) {
				try {
					Object[] objA = rows.getRow(i + 1);
					Object[] add_obj = new Object[objA.length + 1];
					add_obj[0] = obj;
					for (int k = 0; k < objA.length; k++) {
						add_obj[k + 1] = objA[k];
					}
					rt.addFillRow(add_obj);
					mrowI.add(mrowC++, (i + 1));
				} catch (SQLException se) {
					System.out.println("\n Exception :" + se.getMessage());
					continue;
				} catch (Exception ex) {
					System.out.println("\n Exception :" + ex.getMessage());
					ex.printStackTrace();
				}
			} // if Pattern matches
		}
		return rt;
	}

	public ReportTableModel caseFormat(JDBCRowset rows, String col,
			int formatType, char defChar) throws SQLException {

		String[] col_name = rows.getColName();
		String[] add_col = new String[col_name.length + 1];

		for (int j = 0; j < col_name.length; j++) {
			if (col.equals(col_name[j])) {
				matchI = j;
				break;
			}
		}
		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;

		}
		add_col[0] = col_name[matchI] + " Editable";
		for (int i = 0; i < col_name.length; i++) {
			add_col[i + 1] = col_name[i];
		}

		ReportTableModel rt = new ReportTableModel(add_col);
		mrowI = new Vector<Integer>();

		int rowC = rows.getRowCount();
		int mrowC = 0;

		for (int i = 0; i < rowC; i++) {
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null)
				continue;
			String searchFormat = obj.toString();
			String valueFormat = null;
			boolean isObjMatch = true;

			switch (formatType) {
			case 1:
				if (StringCaseFormatUtil.isUpperCase(searchFormat) == false) {
					valueFormat = StringCaseFormatUtil
							.toUpperCase(searchFormat);
					isObjMatch = false;
				}
				break;
			case 2:
				if (StringCaseFormatUtil.isLowerCase(searchFormat) == false) {
					valueFormat = StringCaseFormatUtil
							.toLowerCase(searchFormat);
					isObjMatch = false;
				}
				break;
			case 3:
				if (StringCaseFormatUtil.isTitleCase(searchFormat) == false) {
					valueFormat = StringCaseFormatUtil
							.toTitleCase(searchFormat);
					isObjMatch = false;
				}
				break;
			case 4:
				if (StringCaseFormatUtil.isSentenceCase(searchFormat, defChar) == false) {
					valueFormat = StringCaseFormatUtil.toSentenceCase(
							searchFormat, defChar);
					isObjMatch = false;
				}

				break;
			default:
			}

			if (isObjMatch == false) {
				try {
					String value = valueFormat;
					Object[] objA = rows.getRow(i + 1);
					Object[] add_obj = new Object[objA.length + 1];
					add_obj[0] = value;
					for (int k = 0; k < objA.length; k++) {
						add_obj[k + 1] = objA[k];
					}
					rt.addFillRow(add_obj);
					mrowI.add(mrowC++, (i + 1));
				} catch (SQLException se) {
					System.out.println("\n Exception :" + se.getMessage());
					continue;
				} catch (Exception ex) {
					System.out.println("\n Exception :" + ex.getMessage());
					ex.printStackTrace();
				}

			} // if Pattern match
		}
		return rt;
	}

	public ReportTableModel discreetSearch(JDBCRowset rows, String col,
			Vector<String> token, boolean match) throws SQLException {
		String[] col_name = rows.getColName();
		String[] colType = rows.getColType();
		String[] add_col = new String[col_name.length + 1];

		for (int j = 0; j < col_name.length; j++) {
			if (col.equals(col_name[j])) {
				matchI = j;
				break;
			}
		}
		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;

		}
		Object replace = null;
		String metaType = SqlType.getMetaTypeName(colType[matchI]);

		add_col[0] = col_name[matchI] + " Editable";
		for (int i = 0; i < col_name.length; i++) {
			add_col[i + 1] = col_name[i];
		}
		ReportTableModel rt = new ReportTableModel(add_col);
		mrowI = new Vector<Integer>();

		int rowC = rows.getRowCount();
		int mrowC = 0;

		for (int i = 0; i < rowC; i++) {
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null)
				continue;
			String value = obj.toString();
			int tokenI = 0;
			boolean matchFound = false;

			while (tokenI < token.size()) {
				String key = token.elementAt(tokenI++);
				try {
					if (Pattern.matches(key, value) == true) {
						matchFound = true;
						break;
					}
				} catch (PatternSyntaxException pe) {
					System.out.println("\n Pattern Compile Exception:"
							+ pe.getMessage());
					continue;
				}
			}
			if (matchFound == match) {
				try {
					String newValue = value;
					try {
						if (metaType.toUpperCase().contains("NUMBER")) {
							replace = Double.parseDouble(newValue);
						} else if (metaType.toUpperCase().contains("DATE")) {
							replace = new SimpleDateFormat("dd-MM-yyyy")
									.parse(newValue);
						} else {
							replace = new String(newValue);
						}
					} catch (Exception exp) {
						System.out
								.println("\n WANING: Could not Parse Input String:"
										+ newValue);
					}
					Object[] objA = rows.getRow(i + 1);
					Object[] add_obj = new Object[objA.length + 1];
					add_obj[0] = replace;
					for (int k = 0; k < objA.length; k++) {
						add_obj[k + 1] = objA[k];
					}
					rt.addFillRow(add_obj);
					mrowI.add(mrowC++, (i + 1));
				} catch (SQLException se) {
					System.out.println("\n Exception :" + se.getMessage());
					continue;
				} catch (Exception ex) {
					System.out.println("\n Exception :" + ex.getMessage());
					ex.printStackTrace();
				}
			} // if match found
		}
		return rt;

	}

	public void setrowIndex(Vector<Integer> mrowI) {
		this.mrowI = mrowI;
	}

	public Vector<Integer> getrowIndex() {
		return mrowI;
	}

	public int getColMatchIndex() {
		return matchI;
	}
}
