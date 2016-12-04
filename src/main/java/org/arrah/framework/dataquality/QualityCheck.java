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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.JDBCRowset;
import org.arrah.framework.rdbms.Rdbms_conn;
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
			Hashtable<String, String> filter, String options) throws SQLException {

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

		
		Enumeration<String> en = filter.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			Pattern p= null;
		/* We will compile the pattern here so that once compile it can match all rows.*/
		try {
			if (options.charAt(0) == '0' && options.charAt(2) == '1' )  // case insensitive and literal true
				p =Pattern.compile(key, Pattern.LITERAL|Pattern.CASE_INSENSITIVE);
			else if ( options.charAt(0) == '1' && options.charAt(2) == '1' ) // case sensitive and literal true
				p = Pattern.compile(key, Pattern.LITERAL);
			else if (options.charAt(0) == '0' && options.charAt(2) == '0') // case insensitive and literal false
				p = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
			else // case sensitive and literal false
				p = Pattern.compile(key); // no flag
			
		} catch (PatternSyntaxException pe) {
			System.out.println("Pattern Compile Exception:"
				+ pe.getMessage());
			continue;
		} catch (IllegalArgumentException ee) {
			System.out.println("Illegal Argument Exception:"
					+ ee.getMessage());
				continue;
		}
		if (p== null ) {
			System.out.println("Pattern is NULL");
				continue;
		}
		for (int i = 0; i < rowC; i++) {
			/* Hive does not ensure sequence of rows. Also columns are independent
			 * so getObject will be out of synch for getRow and getObject. For Hive
			 * call get Rows and extract object from there.
			 */
			Object obj = null;
			Object[] objA = null;
			
			if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
				objA = rows.getRow(i + 1);
				obj=objA[matchI];
				if (obj == null)  {
					 continue; // empty objects
				}
			} else { // RDBMS
			 obj = rows.getObject(i + 1, matchI + 1);
			 if (obj == null) 
				 continue; // empty objects
			}
			
			String value = obj.toString().trim().replaceAll("\\s+", " "); // Split for White Space
			
			/* This search takes the key - searches the key in the multi-word string
			 *  If it finds the key ( regex search) it replaces the only that word with Value
			 */
			
			String[] valueTok = new String[1];
			valueTok[0] = value;
			
			if (options.charAt(1) == '0' ) // multi-word not chosen
				 valueTok = value.split(" ");

				boolean matchFound = false;

				for (int j = 0; j < valueTok.length; j++) {
					try {
						// needs to add case sensitive and word search replace
					Matcher m = p.matcher(valueTok[j]);
					if (options.charAt(3) == '1' ){ // Full sequence match
						if (m.matches() == true) {
							String newvalue = (String) filter.get(key);
							valueTok[j] = newvalue;
							matchFound = true;
							continue;
						}
					} else { // find
						if (m.find() == true) {
							String newvalue = (String) filter.get(key);
							newvalue = m.replaceAll(newvalue);
							valueTok[j] = newvalue;
							matchFound = true;
							continue;
						}
						
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
								newValue += " "; // Put space and put back the multi-word
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
						if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) 
							objA = rows.getRow(i + 1); // For Hive it already filled.
						
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
						continue;
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
			Object obj = null;
			Object[] objA = null;
			
			if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
				objA = rows.getRow(i + 1);
				obj=objA[matchI];
			} else { // RDBMS
			 obj = rows.getObject(i + 1, matchI + 1);
			}
			if (obj == null || "".equals(obj.toString())) {
				try {
					if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
						objA = rows.getRow(i + 1);
					}
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
			Object obj = null;
			Object[] objA = null;
			
			if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
				objA = rows.getRow(i + 1);
				obj=objA[matchI];
				if (obj == null)  {
					 continue; // empty objects
				}
			} else { // RDBMS
			 obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null)
				continue;
			}
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
					if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
						objA = rows.getRow(i + 1);
					}
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
			// System.out.println(col+ " "+col_name[j]);
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
			Object obj = null;
			Object[] objA = null;
			
			if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
				objA = rows.getRow(i + 1);
				obj=objA[matchI];
				if (obj == null)  {
					 continue; // empty objects
				}
			} else { // RDBMS
			// getObject will move cursor for hive
			obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null)
				continue;
			}
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
					if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
						objA = rows.getRow(i + 1); // Already selected for Hive
					}
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
			
			Object obj = null;
			Object[] objA = null;
			
			if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
				objA = rows.getRow(i + 1);
				obj=objA[matchI];
				if (obj == null)  {
					 continue; // empty objects
				}
			} else { // RDBMS
			 obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null)
				continue;
			}
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
					if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
					 objA = rows.getRow(i + 1);
					}
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
	
	// This function find a key matching the filter and replaces with
	// value of the filter (that is in Hashtable
	// It will do fuzzy matches

	public ReportTableModel searchReplaceFuzzy(JDBCRowset rows, String col,Hashtable<String, String> filter) throws SQLException {

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
		
		int mrowC = 0;

		/* Build the index here and search in that index */
		SimilarityCheckLucene _simcheck = new SimilarityCheckLucene(rows);
		_simcheck.makeIndex();
		
		// Now loop the keys
		Enumeration<String> en = filter.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			if (key == null || "".equals(key) ) {
				System.out.println("Key is NULL");
					continue;
			}
			String fuzzyquery =  _simcheck.prepareLQuery(key, col);
			
			Object[][] matchedrow = _simcheck.searchTableObject(fuzzyquery);
			if (matchedrow == null) continue;
			
			Vector<Integer> matchedIndex = _simcheck.getMatchedRowIndex(); // matched Index for this set
			if (matchedIndex == null || matchedIndex.size() == 0) continue;
			
			for (int i=0; i < matchedrow.length ; i++) {
				
				try {
					int matchedIndexVal = matchedIndex.get(i);
					if (mrowI.indexOf(matchedIndexVal) != -1) continue; // This index is already there
					
					String newValue = filter.get(key);
					if (newValue == null) break; // No value to replace
					
					try {
						if (metaType.toUpperCase().contains("NUMBER")) {
							replace = Double.parseDouble(newValue);
						} else if (metaType.toUpperCase().contains("DATE")) {
							replace = new SimpleDateFormat("dd-MM-yyyy").parse(newValue);
						} else {
							replace = new String(newValue);
						}
					} catch (Exception exp) {
						System.out.println("\n WANING: Could not Parse Input String:"+ newValue);
					}
					
					Object[] add_obj = new Object[matchedrow[i].length + 1];
					add_obj[0] = replace;
					for (int k = 0; k < matchedrow[i].length; k++) {
						add_obj[k + 1] = matchedrow[i][k];
					}
					rt.addFillRow(add_obj);
					mrowI.add(mrowC++, matchedIndexVal);
				}  catch (Exception ex) {
					System.out.println("\n Exception :" + ex.getMessage());
					continue;
				}
			} // if match found	
		} // end of while
		return rt;
	}
	
}
