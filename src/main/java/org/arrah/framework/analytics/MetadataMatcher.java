package org.arrah.framework.analytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.arrah.framework.dataquality.PIIValidator;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_conn;
import org.arrah.framework.util.StringCaseFormatUtil;
import org.simmetrics.metrics.JaroWinkler;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2016    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used to Match Matadata from
 * column headers and see if they are close 
 * to ( edit distance) standard names
 * then the are matched with data to understand PII
 */


public class MetadataMatcher {
	private Hashtable<String,String> _ht;
	private confidenceL confidenceLevel;
	
	private enum confidenceL {Low,Medium,High}; 

	public MetadataMatcher() { // Constructor
		_ht = new Hashtable<String,String>();
		
	} 
	public MetadataMatcher(Hashtable<String,String> ht) { // Constructor
		_ht = ht;
		
	} 
	
	// Parse CS and return String array  - utility function
	public String[] getCSValue (String key) {
		String val = _ht.get(key);
		if (val == null || "".equals(val))
			return new String[] {""};
		String[] newval = val.split(",");
		return newval;
	}
	
	// Will add a new Value to Key-value pair -  - utility function
	public void addnewValue(String key, String value) {
		
		if (key == null || "".equals(key) || value == null )
			return;
		String val = _ht.get(key);
		val = val +","+value;
		_ht.put(key,val);
		
	}
	
	// Will delete an existing Value to Key Value pair -  - utility function
	public void deleteValue(String key, String value) {
		String newval="";
		String[] val = getCSValue(key);
		for (int i=0; i <val.length; i++) {
			if (val[i].equalsIgnoreCase(value) != true) {
				if (newval.equals("") == true)
					newval = val[i];
				else
					newval = newval +","+val[i];
			}
		}
		_ht.put(key,newval);
	}
	
	// Will display existing Value to Key Value pair
	public Hashtable<String,String> showKeyValueAsTable() {
		return _ht;
			
	}
	
	// This function will return keys of matched column values using fuzziness
	public Vector<String> matchedKeys(String colName, float nearness) {
		JaroWinkler simAlgo  = new JaroWinkler(); // Using JaroWinkler for testing
		Vector<String> matchV = new Vector<String>();
		
		for (Enumeration<String> e = _ht.keys(); e.hasMoreElements();) {
			String key = e.nextElement();
			String[] val = getCSValue(key);
			for (String newval: val) {
				float simI = simAlgo.compare(newval.toLowerCase(), colName.toLowerCase());
				// System.out.println("Val:"+newval +" ColN:" + colName + " SimI:" +simI);
				if (simI >= nearness) { 
					if (matchV.indexOf(key) == -1) // if not there add it
						matchV.add(key);
				}
			}
		}
		return matchV;
	}
	// This function will return keys of matched columns values using fuzziness
		public Hashtable<String,Vector<String>> matchedKeys(String[] colName, float nearness) {
			Hashtable<String,Vector<String>> newVal = new Hashtable<String,Vector<String>>();
			
			for (String val : colName) {
				Vector<String> matchV = matchedKeys(val,nearness);
				newVal.put(val, matchV);
			}
				
			return newVal;
		}
	
	// This function will save keys with each values of CS values
	// which can be used for standardization
	public Hashtable<String,String> tableAsFlatKey() {
		Hashtable <String, String> newht = new Hashtable <String, String>();
		for (Enumeration<String> e = _ht.keys(); e.hasMoreElements();) {
			String key = e.nextElement();
			String[] val = getCSValue(key);
			for (String newval: val)
				newht.put(newval,key);
		}
		return newht;
	
	}
	
	// This function will take the matched metadata ; match with data
	// and then show the data matched with high confidence
	public Object[] getPIIColData(Object table, String col, String piigrp, Object[] colD) {
		 int datamatchCount = 0;
		
		// For the columns matching get all the attributes they are matching
		
		// then fetch top 10 data and run thru all the attributes they are matching
		// right now we do not have random fetch to get top 10
		 if (table instanceof String)
			 colD = getDataforCol_sample10( table.toString(),  col);
		 else if (table instanceof ReportTableModel)
			 colD = ((ReportTableModel)table).getColDataRandom(col,10);
		 else {
			 System.out.println("Table Type not recognised.");
			 return colD;
		 }
				 
		PIIValidator piiv= new PIIValidator();
		
		for (int i=0; i <colD.length; i++ ) {
			Object s = colD[i];
			if (s == null) continue; // may not have data
			boolean isMatch = false;
			String onlyd = StringCaseFormatUtil.digitString(s.toString());
			switch(piigrp) {
			
			// if CC
			case "CreditCard":
				isMatch = piiv.isCreditCard(onlyd);
				break;
				
			// if SSN
			case "SocialSecurity":
				isMatch = piiv.isSSN(onlyd);
				break;
				
			// if DoB
			case "DoB":
				if (s instanceof java.util.Date)
					isMatch = piiv.isDoB((java.util.Date)s);
				break;
			
			// if Email
			case "Email":
				isMatch = piiv.isEmail(s.toString());
				break;
			// if Phone Number
			case "PhoneNumber":		
				isMatch = piiv.isPhone(onlyd);	
				break;
			// if Phone Number
			case "Salary":		
				isMatch = piiv.isAccountingNumber(onlyd);	
				break;
			
			default:
				
				break;
				
				
			}
			if (isMatch == true)  {// one more data match
				datamatchCount++;
			}
		} // For Loop
		
		// if matched confidence is high, only based on metadata confidence is low
		// only count number user can take informed decision
		// return object array;
		if (datamatchCount < 5)
			setConfidenceLevel(confidenceL.Low);
		else if (datamatchCount < 9)
			setConfidenceLevel(confidenceL.Medium);
		else
			setConfidenceLevel(confidenceL.High);
		return colD;
	}
	
	public Object[] getDataforCol_sample10(String table, String col) {
		Object[] coldata = new Object[10];
		QueryBuilder querybuilder = new QueryBuilder(Rdbms_conn.getHValue("Database_DSN"), 
				table, col, Rdbms_conn.getDBType());
		String top_sel_query = querybuilder.top_query(true,"top_count", "10");
		try {
			Rdbms_conn.openConn();
			ResultSet rs = Rdbms_conn.runQuery(top_sel_query);
			int counter = 0;
			while (rs.next() && counter < 10) {
				//String top_val = rs.getString("top_count");
				Object top_val = rs.getObject("top_count");
				// System.out.println("Value:" +top_val.toString() );
				coldata[counter++] = top_val;
			}
			rs.close();
			Rdbms_conn.closeConn();
		} catch (SQLException exp) {
			System.out.println("Column fetch error:"+ exp.getLocalizedMessage());
			return coldata;

		}
		return coldata;
	}
	public String getConfidenceLevel() {
		return confidenceLevel.toString();
	}
	public void setConfidenceLevel(confidenceL confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}
	
	
} // End of MetadataMatcher
