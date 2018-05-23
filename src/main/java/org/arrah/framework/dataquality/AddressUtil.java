package org.arrah.framework.dataquality;

/***************************************************
 *     Copyright to Vivek Kumar Singh 2018         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                           *
 *                                                 *
 **************************************************/

/*
 * This file will also be used for completing 
 * some empty columns like address completion
 * if zip code is provided.
 */

import java.util.Hashtable;
import java.util.Vector;

import org.arrah.framework.ndtable.RTMUtil;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.util.DiscreetRange;

public class AddressUtil {
	
	
	// Constructor
	public AddressUtil() {
		// Do nothing
	}
	
	// This function will be used for address completion wrapper function
	public static ReportTableModel  completeRTMCOls (ReportTableModel rtm, Integer[] indexToComplete,int zipIndex,
				Hashtable<Object,Object> keyRowMap,ReportTableModel toFillFrom, Integer[] indexToFetch ) {
		if (indexToComplete.length != indexToFetch.length) {
			System.out.println("To Fill and From Fill columns does not match");
			return rtm;
		}

		int rowC = rtm.getModel().getRowCount();
		for (int i=0; i < rowC; i++ ) {
			Object matchO = rtm.getModel().getValueAt(i, zipIndex);
			if (matchO == null || "".equals(matchO.toString())) { // Zip is missing so populate  Zip
				Object[] fetchI = rtm.getSelectedColRow(i, indexToComplete);
				Object[] matchedOB =  getMatchedFirstIndex(fetchI,toFillFrom, indexToFetch );
				if (matchedOB.length > 0)
					matchO = matchedOB[0]; // take fot first probable zip
			}
			if (matchO == null || "".equals(matchO.toString())) continue; // Still empty
			Object o = keyRowMap.get(matchO);
			if (o == null) continue;
			int rowMatched = (Integer)o;
			Object[] valuesToFill = toFillFrom.getSelectedColRow(rowMatched, indexToFetch);
			completeColumnsRowIndex(rtm,i,indexToComplete,valuesToFill);
		}
		return rtm;
	}
	
	// This function will be used for address completion
	public static void completeColumnsRowIndex (ReportTableModel rtm,int rowI, Integer[] indexToComplete, Object[] valuesToFill) {
		if (indexToComplete.length != valuesToFill.length) {
			System.out.println("To Fill and From Fill columns does not match");
			return;
		}
			
		for (int i=0; i <indexToComplete.length; i++) {
			Object o = rtm.getModel().getValueAt(rowI, indexToComplete[i]);
			if (o == null || o.toString().equals("")) // override
				rtm.getModel().setValueAt(valuesToFill[i], rowI, indexToComplete[i]);
		}
	}
	
	// This function will be used for address completion
	public static Object[]  completeColumns (Object[] row, Integer[] indexToComplete, Object[] valuesToFill) {
		for (int i=0; i <indexToComplete.length; i++) {
			Object o = row[indexToComplete[i]];
			if (o == null || o.toString().equals("")) // override
				row[indexToComplete[i]] = valuesToFill[i];
		}
		return row;
	}
	
	// This function will be used for fetch zip/pin or first index who may be empty in toFill table
	// Skip the first index assuming it is empty
	public static Object[] getMatchedFirstIndex(Object[] recordToMatch,ReportTableModel toFillFrom, Integer[] indexToFetch ) {
		Object[] matchedObject = null;
		if (recordToMatch.length != indexToFetch.length) return matchedObject;
		
		Vector<Integer> mergeSetIndex = new Vector<Integer> ();
		boolean isFirstTime = true;

		for (int i=1; i <indexToFetch.length; i++ ) {
			String s = recordToMatch[i].toString();
			if (s == null || "".equals(s)) continue;
			Vector<Integer> matchedIndex = RTMUtil.matchCondition(toFillFrom,indexToFetch[i], 6, recordToMatch[i].toString()) ; //6 is equal to
			if (isFirstTime == true) {
				mergeSetIndex = matchedIndex;
				isFirstTime = false;
			}
			mergeSetIndex =  DiscreetRange.mergeSet(mergeSetIndex,matchedIndex,"and");
		}
		
		matchedObject = new Object[mergeSetIndex.size()];
		for (int i=0; i < matchedObject.length; i++ ) {
			matchedObject[i] = toFillFrom.getModel().getValueAt(mergeSetIndex.get(i),indexToFetch[0]); // 0 index is zip
		}
		
		return matchedObject;
	}
	
	}
