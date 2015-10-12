package org.arrah.framework.datagen;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2014    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for create aggregate data
 * and cumulative data - aggregate data sum,
 * count, avg, min and max
 * Cumulative data sum, count and avg
 * Utilitiies functions are also supported here
 */

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;

public class AggrCumRTM {

	public AggrCumRTM() {
		
	} // Constructor
	
	/* Sum for column Index which will come in Vector */
	public static double getSum(Vector<Double> colList) {
		double sum=0.00D;
		
		if (colList == null || colList.size() == 0)
			return sum;
		
		for (double colVal:colList) {
			sum +=colVal;
		}
		return sum;
	}
	
	/* Avg for column Index which will come in Vector */
	public static double getAverage(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return 0.00D;
		
		long count = colList.size();
		double sum = getSum(colList);
		
		if (sum != 0.00D && count != 0)
			return  (sum/count);
		else
			return sum;
	}
	
	/* get Max and Min value for ColIndex which will come in Vector */
	public static double[] getMinMax(Vector<Double> colList) {
		double[] minMax = new double[2];
		minMax[0] = 0.00D; minMax[1] = 0.00D;
		
		if (colList == null || colList.size() == 0)
			return minMax;
		
		double sum = getSum(colList);
		long count = colList.size();
		
		if (sum == 0.00D || count == 0) {
			return minMax;
		}
		
		Double[] newList = new Double[colList.size()];
		newList = colList.toArray(newList);
		Arrays.sort(newList); // Ascending order
		minMax[0] = newList[0];
		minMax[1] = newList[newList.length - 1];
		
		return minMax;
		
	}
	
	/* Cumulative Sum for column Index which will come in Vector */
	public static Vector<Double> getCumSum(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		Vector<Double> cumSum = new Vector<Double>();
		double csum = 0.00D;
		
		for (double colVal:colList) {
			csum +=colVal;
			cumSum.add(csum);
		}
		return cumSum;
	}
	
	/* Cumulative Avg for column Index which will come in Vector */
	public static Vector<Double> getCumAvg(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		long count = colList.size();
		Vector<Double> cumAvg = new Vector<Double>();
		double csum = 0.00D;
		
		for (int i = 1; i <= count; i++) { // dividing by zero will NaN
			csum +=colList.get(i-1);
			cumAvg.add(csum/i);
		}
		
		return cumAvg;
	}
	
	/* Get next value value in vector Vector */
	public static Vector<Double> putNextVal(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		long count = colList.size();
		Vector<Double> nextVal = new Vector<Double>();
		
		for (int i = 0; i < (count -1) ; i++) { // Last value will be 0
			nextVal.add(colList.get(i+1));
		}
		nextVal.add(0.0D); // no next so put zero
		
		return nextVal;
	}
	
	/* Get prev value value in vector Vector */
	public static Vector<Double> putPrevVal(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		long count = colList.size();
		Vector<Double> prevVal = new Vector<Double>();
		prevVal.add(0.0D); // no prev so put zero
		
		for (int i = 1; i < count  ; i++) { // First value will be 0
			prevVal.add(colList.get(i-1));
		}
		
		return prevVal;
	}
	
	
	/* This is utility function to get ReportTableModel data into vector */
	public static Vector<Double> getColumnNumberData(ReportTableModel rpt, int columnIndex) {
		Vector<Double> columnDataVector = new Vector<Double>();
		int rowCount = rpt.getModel().getRowCount();
		
		for (int i=0; i < rowCount; i++ ){
			Object obj = rpt.getModel().getValueAt(i, columnIndex);
			if (obj == null) columnDataVector.add(0D);
			try {
				Double d = Double.parseDouble(obj.toString());
				columnDataVector.add(d);
			} catch (Exception e) {
				columnDataVector.add(0D);
			}
		}
		
		return columnDataVector;	
	}
	
	/* This is a utility function to split String in two parts only */
	public static String[] splitColString(String value, String regex) {
		String[] output= new String[2];
		output[0] = ""; output[1] = "";
		
		if (value == null || "".equals(value) )
				return output;
	
		return output = value.split(regex,2);
	}
	
	/* This is a utility function to split String in subString*/
	public static String[] splitColSubString(String value, String regex) {
		String[] output = null;
		
		if (value == null || "".equals(value) )
				return output;
	
		return output = value.split(regex);
	}
	
	/* This is a utility function to Remove MetaChar from String */
	public static String removeMetaCharString(String oldString, String skipString) {
		String newString="";
		for (int curIndex = 0; curIndex < oldString.length(); curIndex++ ) {
			 char c = oldString.charAt(curIndex);
			 Character ch = new Character(c);
			 if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
			 newString += ch.toString();
		 }
		return newString;
	}
	// From Start, end and inBetween
	public static String removeMetaCharString(String oldString, String skipString, boolean start,  boolean inBtw, boolean end) {
		String newString="";
		int len =  oldString.length();
		
		{ // Start Block
			char c = oldString.charAt(0); // First char
		 	Character ch = new Character(c);
		 	
			if (start == true) {
			 	if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
					 newString += ch.toString();
			} else 
				newString += ch.toString();
		}
		
		if (len > 1) { // inBet Block
			for (int curIndex = 1; curIndex < len - 1 ; curIndex++ ) {
				 char c = oldString.charAt(curIndex);
				 Character ch = new Character(c);
				 if (inBtw == true ) {	
					 if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
					 newString += ch.toString();
				 } else
					 newString += ch.toString();	 
			}
		}
		
		if ((len > 1)) { // end block
			char c = oldString.charAt(len -1); // last char
		 	Character ch = new Character(c);
			if ( end == true) {
			 	if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
					 newString += ch.toString();
			} else
				 newString += ch.toString();
		}
		
		return newString;
	}
	
	/* This is a utility function to Remove Character from String */
	public static String removeCharacterString(String oldString, String skipString) {
		String newString="";
		for (int curIndex = 0; curIndex < oldString.length(); curIndex++ ) {
			 char c = oldString.charAt(curIndex);
			 Character ch = new Character(c);
			 if ( skipString.contains(ch.toString()) == false)
			 newString += ch.toString();
		 }
		return newString;
	}
	// From Start, end and inBetween
	public static String removeCharacterString(String oldString, String skipString, boolean start,  boolean inBtw, boolean end) {
		String newString="";
		int len =  oldString.length();
		
		for (int curIndex = 0 ; curIndex < len ; curIndex++ ) {
			 char c = oldString.charAt(curIndex);
			 Character ch = new Character(c);
			 
			 if (curIndex == 0) { //Start block
				 if (start == true ) {
					 if ( skipString.contains(ch.toString()) == false)
						 newString += ch.toString();
				 } else 
					 newString += ch.toString();
				 
				 continue; // will work for single byte character
			 }
			 
			 if (curIndex < len -1 ) {
				 if (inBtw == true) { // middle block
					 if ( skipString.contains(ch.toString()) == false)
						 newString += ch.toString();
				 } else 
					 newString += ch.toString();
			 } else {
				 if (end == true) { // end block
					 if ( skipString.contains(ch.toString()) == false)
						 newString += ch.toString();
				 } else 
					 newString += ch.toString();
			 }
			 
		 } // For Loop
		
		return newString;
	}
	
	/* This is a utility function which will take date and give output as millisecond */
	public static long dateIntoSecond(java.util.Date date) {
		if (date == null ) return 0;
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.setTime(date);
		return cal.getTimeInMillis();
	}
	
	/* This is a utility function which will take millisecond  and give output as current date */
	// Default TimeZone
	public static java.util.Date secondIntoDate(long millisec) {
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.setTimeInMillis(millisec);
		return cal.getTime();
	}
	
	/* This is a utility function which will take millisecond  and give output as current date */
	public static java.util.Date secondIntoDate(long millisec, TimeZone tz) {
		Calendar cal = Calendar.getInstance(tz);
		cal.setLenient(true);
		cal.setTimeInMillis(millisec);
		return cal.getTime();
	}
	
	/* This is a utility function to Remove Character from String */
	public static String replaceString(String fullString, String matchString, String replaceString, boolean fromStart) {
		String newString ="";
		if ( fromStart == true) 
			newString = fullString.replaceFirst(matchString, replaceString);
		 else 
			newString = fullString.replaceAll(matchString, replaceString);
		
		return newString;
	}
	
	
	
} // End of AggrCumRTM
