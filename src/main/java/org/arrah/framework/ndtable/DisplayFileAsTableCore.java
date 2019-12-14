package org.arrah.framework.ndtable;

/***********************************************
 *     Copyright to Vivek Kumar Singh	       *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This a core function file for Swing file 
 * DisplayFileAsTable. Some core , reusable 
 * functions will move here.
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.arrah.framework.dataquality.SimilarityCheckLucene;

public class DisplayFileAsTableCore {
	
	public List<Integer> standardisationRegex(Hashtable<String,String> filterHash, ReportTableModel rtm, String options, int index) {
		
		// Initiate the indexed class information
		Object replace = null;
		int row_c = rtm.getModel().getRowCount();
		Class<?> cclass  = rtm.getModel().getColumnClass(index);
		
		// To hold matched Index
		List<Integer> matchedI = new ArrayList<Integer>();
		
		Enumeration<String> en = filterHash.keys();
		
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
		
		for (int i = 0; i < row_c; i++) {
			Object obj = rtm.getModel().getValueAt(i, index);
			if (obj == null)
				continue;
			
			String value = obj.toString().trim()
					.replaceAll("\\s+", " "); // Split for White Space
			
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
						if (m.matches() == true ) {
							String newvalue = (String) filterHash
									.get(key);
							valueTok[j] = newvalue;
							matchFound = true;
							continue;
						}
					} else { // find
						if (m.find() == true) {
							String newvalue = (String) filterHash.get(key);
							newvalue = m.replaceAll(newvalue);
							valueTok[j] = newvalue;
							matchFound = true;
							continue;
						}
						
					}
					} catch (PatternSyntaxException pe) {
						System.out.println(" Pattern Compile Exception:"+ pe.getMessage());
						break;
					}
				}
				if (matchFound == true) {
					String newValue = "";
					for (int j = 0; j < valueTok.length; j++) {
						if (newValue.equals("") == false)
							newValue += " "; // Put space
						newValue += valueTok[j];
					}
					try {
						if (cclass.getName().toUpperCase()
								.contains("DOUBLE")) {
							replace = Double.parseDouble(newValue);
						} else if (cclass.getName().toUpperCase()
								.contains("DATE")) {
							replace = new SimpleDateFormat("dd-MM-yyyy")
									.parse(newValue);
						} else {
							replace = new String(newValue);
						}
					} catch (Exception exp) {
						System.out.println(" WANING: Could not Parse Input String:"+ newValue);
					}
					rtm.getModel().setValueAt(replace, i, index);
					matchedI.add(i);
					
				}
			}
		}
		
		return matchedI;
	}
	
	public List<Integer> standardisationFuzzy(Hashtable<String,String> filterHash, ReportTableModel rtm, int index) {
		// Initiate the indexed class information
		Object replace = null;
		Class<?> cclass  = rtm.getModel().getColumnClass(index);
		
		// To hold matched Index
		List<Integer> matchedI = new ArrayList<Integer>();
		String colTitle = rtm.getModel().getColumnName(index);
		
		/* Build the index here and search in that index */
		SimilarityCheckLucene _simcheck = new SimilarityCheckLucene(rtm);
		_simcheck.makeIndex();

		Enumeration<String> en = filterHash.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			
			if (key == null || "".equals(key) ) {
				System.out.println("Key is NULL");
					continue;
			}
			String fuzzyquery =  _simcheck.prepareLQuery(key, colTitle);
			
			Object[][] matchedrow = _simcheck.searchTableObject(fuzzyquery);
			if (matchedrow == null) continue;
			
			Vector<Integer> matchedIndex = _simcheck.getMatchedRowIndex(); // matched Index for this set
			if (matchedIndex == null || matchedIndex.size() == 0) continue;
			
			for (int i=0; i < matchedrow.length ; i++) {
				int matchedIndexVal = matchedIndex.get(i);
				
				String newValue = filterHash.get(key);
				if (newValue == null) break; // No value to replace
					try {
						if (cclass.getName().toUpperCase()
								.contains("DOUBLE")) {
							replace = Double.parseDouble(newValue);
						} else if (cclass.getName().toUpperCase()
								.contains("DATE")) {
							replace = new SimpleDateFormat("dd-MM-yyyy")
									.parse(newValue);
						} else {
							replace = new String(newValue);
						}
					} catch (Exception exp) {
						System.out.println(" WANING: Could not Parse Input String:"+ newValue);
					}
					rtm.getModel().setValueAt(replace, matchedIndexVal, index);
					matchedI.add(matchedIndexVal);
				}
		}
		return matchedI;
		
	}
  
} // End of Class DisplayFileAsTableCore