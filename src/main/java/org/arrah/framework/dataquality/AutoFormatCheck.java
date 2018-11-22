package org.arrah.framework.dataquality;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.JDBCRowset;
import org.arrah.framework.rdbms.Rdbms_conn;

/***************************************************
 *     Copyright to Vivek Kumar Singh 2018         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                       	   *
 *                                                 *
 **************************************************/

/*
 * This class is used for Auto validation format.
 * It is take group of regext and then validate 
 * formats against it
 *
 */

public class AutoFormatCheck {
	
	private List<Pattern> compileP= new ArrayList<Pattern>();
	private List<String> valueStr= new ArrayList<String>();
	private List<String> sampleStr= new ArrayList<String>();
	
	
	// Constructor
	public AutoFormatCheck(Hashtable<String,String> filterHash) {
		
	
		// Do nothing
		Enumeration<String> en = filterHash.keys();
		while (en.hasMoreElements()) {
			Pattern p = null;
			String key = null;
		
			try { 
				 key = en.nextElement().toString();
				 p = Pattern.compile(filterHash.get(key));
				
			} catch (PatternSyntaxException pe)  {
				System.out.println("Pattern Compile Exception:"
					+ pe.getMessage());
				continue;
			} catch (IllegalArgumentException ee) {
				System.out.println("Illegal Argument Exception:"
						+ ee.getMessage());
					continue;
			} catch (Exception ee) {
				System.out.println("Exception:"
						+ ee.getMessage());
					continue;
			}
			if (p== null ) {
				System.out.println("Pattern is NULL");
					continue;
			}
			compileP.add(p);
			valueStr.add(key);
			sampleStr.add("");
		}
		sampleStr.add(0,""); // first is null or empty
	}
	
	
	public List<Boolean> ismatch (Object toMatch) {
		 List<Boolean> matchres = new  ArrayList<Boolean>(compileP.size() +1); // one extra for null
		 java.util.Collections.fill(matchres,false);
		 
		 int i=0;
		 // first one is always null or empty check
		 if (toMatch == null || "".equals(toMatch.toString())) {
			 matchres.add(i++, true);
			 return matchres; // nothing to match 
		 }
		 else
			 matchres.add(i++, false);
		 
		 for (Pattern p: compileP) {
			 try {
				 boolean b = p.matcher(toMatch.toString()).matches();
				// if match then add sample here
				 if (b == true && sampleStr.get(i).equals("")) {
					 sampleStr.set(i, toMatch.toString());
				 }
				 matchres.add(i++, b);
				 
			 } catch (Exception e) {
				 System.out.println(" Matching Exception:"+ e.getLocalizedMessage());
				 matchres.add(i++, false);
			 }
		 }
		 
		 
		 return matchres;
		
	}
	
	public List<Integer> ismatch ( Object[] toMatch) {
		
		 List<Integer> matchresC = new  ArrayList<Integer>();
		 for (int i =0; i < compileP.size()+1; i++)
			 matchresC.add(i, 0);
		 
		 for(Object o: toMatch) {
			 List<Boolean> matres = ismatch(o);
			 for (int i=0; i < matres.size(); i++ ) {
				 boolean b = matres.get(i);
				 if (b == true) {
					 matchresC.set(i, matchresC.get(i)+1);
				 }
			 }
		 }
		 
		 return matchresC;
		
	}
	
	public ReportTableModel getCountintoRTM (Object[] toMatch) {
		String [] colname = new String[] {"Format","Count","Sample"};
		ReportTableModel rtm = new ReportTableModel(colname,true,false);
		List<Integer> matchCount = ismatch (toMatch);
		
		Object[] firstR = new Object[3]; firstR[0] = "Empty Or Null";firstR[1] = matchCount.get(0);firstR[2] ="";
		rtm.addFillRow(firstR);
		
		int i=1;
		for (String str: valueStr) {
			Object[] row = new Object[3]; row[0] = str;row[1] = matchCount.get(i);row[2] = sampleStr.get(i++);
			rtm.addFillRow(row);
		}
		
		
		return rtm;
	}
	
	 public ReportTableModel getRowsetCountintoRTM(JDBCRowset rows, String col) throws SQLException {
		 
		String[] col_name = rows.getColName();
		int matchI = -1;
		
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
		
		int rowC = rows.getRowCount();
		
		List<Integer> matchresC = new  ArrayList<Integer>();
		 for (int i =0; i < compileP.size()+1; i++)
			 matchresC.add(i, 0);
		
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
			} else  // RDBMS
			 obj = rows.getObject(i + 1, matchI + 1);
			
			
			List<Boolean> matchList =ismatch (obj);
			for (int matchListI=0; matchListI < matchList.size(); matchListI++ ) {
				 boolean b = matchList.get(matchListI);
				 if (b == true) {
					 matchresC.set(matchListI, matchresC.get(matchListI)+1);
				 }
			}
		}
		
		// Now fill the RTM
		String [] colname = new String[] {"Format","Count","Sample"};
		ReportTableModel rtm = new ReportTableModel(colname,true,false);
		Object[] firstR = new Object[3]; firstR[0] = "Empty Or Null";firstR[1] = matchresC.get(0);firstR[2] ="";
		rtm.addFillRow(firstR);
		
		int i=1;
		for (String str: valueStr) {
			Object[] row = new Object[3]; row[0] = str;row[1] = matchresC.get(i);row[2] = sampleStr.get(i++);
			rtm.addFillRow(row);
		}
		return rtm;
		 
	 }

	
}
