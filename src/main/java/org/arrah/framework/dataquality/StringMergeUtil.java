package org.arrah.framework.dataquality;


/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for creating utility 
 * functions will be used by string merge
 * utility like most common, strlen based
 * or timeliness of data
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StringMergeUtil {

	
	public StringMergeUtil() {
		
	} // Constructor
	
	
	public static String aggrString(ArrayList<String> list, int action) {
		double result=0;
	
		for (String a : list) {
			if (a == null || "".equals(a)) continue; // null value not counted
			try {	
				double dv = Double.parseDouble(a);
				switch(action) {
				case 1: // Sum
					result = result+dv;
					break;
				case 2: // Count
					result = result+1;
					break;
				case 3: // Min
					if (result > dv )
						result = dv;
					break;
				case 4: // max
					if (result < dv )
						result = dv;
					break;
				default:
					break;
				}
			} catch (Exception pe) {
				
				if (action == 2 ) // count
					result = result+1; // count is not dependent of 
				else 
					System.out.println("Format Exception for Number:"+ a);
			}
		}
		
		return new Double(result).toString();
		
	}
	public static String aggrAvgString(ArrayList<String> list) {
		double result=0;
		String sumStr = aggrString(list,1); // 1 for sum
		double dv = Double.parseDouble(sumStr);
		if (list.size() == 0) // 0/0 is NaN
			return new Double(result).toString();
		else
			return new Double(dv/list.size()).toString();
	}
	
	public static HashMap<String, Integer> freqCount(ArrayList<String> list) {
		HashMap<String, Integer> h = new HashMap<String, Integer>();
		for (String a : list) {
			if (h.containsKey(a) == true) {
				int count = h.get(a);
				count++; h.put(a, count); // increase the count
			} else {
				h.put(a, 1); // First Count
			}		
		}
		return h;
	}
	
	public static HashMap<String, Integer> strLenCount(ArrayList<String> list) {
		HashMap<String, Integer> h = new HashMap<String, Integer>();
		for (String a : list) {
			int strLen = a.length();
				h.put(a, strLen); // Strlen
		}
		return h;
	}
	
	public static ArrayList<String> topValueList(HashMap<String, Integer> mapList) {
		ArrayList<String> result = new ArrayList<String>();
		int maxVal = 0;
		
		for (Iterator<String> it = mapList.keySet().iterator(); it.hasNext();) {
			String keyStr = it.next();
			int val = mapList.get(keyStr);
			
			if (val > maxVal) {
				result.removeAll(result);
				result.add(keyStr);
				maxVal = val;
			} else if (val == maxVal) {
				result.add(keyStr);
			} else {
				// ignore as it is less value
			}
		}
		return result;
	}
	
	// This function will take a list and give merged value
	// based on strlen and frequency algo
	public static String mergeValue(ArrayList<String> list) {
		String result="";
		int topFreqVal = 0, topStrVal = 0;
		float maxweightage = 0.00f;
		
		HashMap<String, Integer> freqCountHash = freqCount(list);
		HashMap<String, Integer> strLenCountHash = strLenCount(list);
		
		List<String> freqVal = topValueList(freqCountHash);
		List<String> strLenVal = topValueList(strLenCountHash);
		
		topFreqVal = freqCountHash.get(freqVal.get(0));
		topStrVal = strLenCountHash.get(strLenVal.get(0));
		
		if (topStrVal > 0 )
		for (int i=0 ; i < freqVal.size(); i++ ) { // get strlen
			int val= strLenCountHash.get(freqVal.get(i));
			float weightage = (float)val / (float)topStrVal;
			if (weightage > maxweightage) {
				result = freqVal.get(i);
				maxweightage = weightage;
			}
		}
		
		if (topFreqVal > 0 )
		for (int i=0 ; i < strLenVal.size(); i++ ) { // get frequency
			int val= freqCountHash.get(strLenVal.get(i));
			float weightage = (float)val / (float)topFreqVal;
			if (weightage > maxweightage) {
				result = strLenVal.get(i);
				maxweightage = weightage;
			}
		}
		
		return result;
	}
	
	// change row based store to columns based Transpose
	public static ArrayList<ArrayList<String>> trasposeValue(ArrayList<String[]> list) {
		int row = list.size();
		String[] firstVal = list.get(0);
		int column = firstVal.length;
		String[][] newStorage = new String[column][row];
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		for (int i=0; i < list.size(); i++) {
			String[] val = list.get(i);
			for ( int j=0; j < column ; j++) {
				// replace null with empty string
				if ( val[j] == null)
					 val[j] = "";
				newStorage[j][i] = val[j] ;
			}
		}
		for (int i=0; i<column; i++) {
			ArrayList<String> col = new ArrayList<String>();
			for (int j=0; j<row; j++)
				col.add(newStorage[i][j]);
			
			result.add(col);
		}
		return result;
		
	}
	
	// This function will a set of matched values
	// and return the golden copy - single copy
	// "Ignore","Take Any","Most Common","Sum","Count","Min","Max","Average"
	
	public static String[] getGoldenValue(ArrayList<String[]> list, Integer[] actionType) {
		ArrayList<ArrayList<String>> trasVal = trasposeValue(list);
		String[] result = new String[trasVal.size()];
		
		for (int i=0 ; i < trasVal.size(); i++  ) {
			ArrayList<String> col = trasVal.get(i);
			int action = actionType[i];
			String goldVal ="";
			
			switch (action) {
				case 0: // Ignore
					break;
				case 1: // Take Any
					for (String a : col) {
						goldVal = a; // first data if not empty or null
						if (goldVal != null && "".equals(goldVal) == false)
							break;
					}
					break;
				case 2: // Most Common
					HashMap<String, Integer> freqCountHash = freqCount(col);
					List<String> freqVal = topValueList(freqCountHash);
					for (String a : freqVal) {
						goldVal = a; // first data if not empty or null
						if (goldVal != null && "".equals(goldVal) == false)
							break;
					}
					break;
				case 3: // Sum 
				case 4: // Count
				case 5: // Min
				case 6: // Max
					goldVal= aggrString(col,action -2); // sum 1 count 2 min 3 max 4
					break;
				case 7: // Avg
					goldVal= aggrAvgString(col);
					break;
				case 100 :
					goldVal = mergeValue(col);
					break;
				
				default:
			}
			result[i] = goldVal;
		}
		
		return result;
			
	}
	
	/* This function will take a collection of Result (RecordMatch.Result)
	 * and scan the whole list. It will find the most appropriate left row
	 * and right row combination based on similarity Value of two rows. It 
	 * will be like inner join where only one row of left is mapped with
	 * one row at right.
	 * 
	 */
	public static HashMap<List<String>, List<String>> innerJoinResult(List<RecordMatch.Result> resultSet) {
		int prevIndex = -1;
		float simMatchIndex = 0;
		HashMap<List<String>, List<String>> innerJoinHM = new HashMap<List<String>, List<String>> ();
		HashMap<Integer,Integer> leftRightMap = new HashMap<Integer, Integer> ();
		
		for (RecordMatch.Result res: resultSet) {
			if (res.isMatch() == false)
				continue; // Only displaying matched one
			
			int leftI = res.getLeftMatchIndex();
			int rightI = res.getRightMatchIndex();
			List<String> leftrow = res.getLeftMatchedRow();
			List<String> rightrow = res.getRightMatchedRow();
			float simMatchVal = res.getSimMatchVal();
			
			if (leftI != prevIndex ) { // starting of new set
				
				// Initialize for new set
				prevIndex = leftI;
				if (leftRightMap.containsValue(rightI) == true)
					continue; // already in HashMap
				simMatchIndex = simMatchVal;
				innerJoinHM.put(leftrow, rightrow);
				leftRightMap.put(leftI, rightI);
			} else {
				if (leftRightMap.containsValue(rightI) == true)
						continue; // already in HashMap
				// This right value
				if (simMatchVal > simMatchIndex ) {
					simMatchIndex = simMatchVal;
					innerJoinHM.put(leftrow, rightrow);
					leftRightMap.put(leftI, rightI);
				}
			}
			
		}
		return innerJoinHM;
		
	}
	
	
	
} // End of StringMergeUtil
