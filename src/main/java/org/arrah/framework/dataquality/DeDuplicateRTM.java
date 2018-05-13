package org.arrah.framework.dataquality;

/***************************************************
 *     Copyright to Vivek Kumar Singh 2016         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                           *
 *                                                 *
 **************************************************/

/*
 * This class is used finding duplicate rows from
 * RTM and show the duplicate values
 *
 * This file will also be used for completing 
 * some empty columns like address completion
 * if zip code is provided.
 */
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.ndtable.ResultsetToRTM;

public class DeDuplicateRTM {
	private ReportTableModel duplicate = null;
	
	// Constructor
	public DeDuplicateRTM() {
		// Do nothing
	}
	
	/* this function will take report table model and colI
	 * on which duplicate need to be found.
	 * It will take string values of those colI and will do
	 * MD5 and will do match.
	 * 1 in billion there is possibility that it will be a 
	 * false positive. 
	 * 
	 */
	public ReportTableModel removeDuplicate (ReportTableModel rtm, int[] colI) {
		if (rtm == null) return rtm;
		int rowC = rtm.getModel().getRowCount();
		if (rowC <= 0) return rtm;
		Vector<BigInteger> md5array = new Vector<BigInteger>();
		Vector<Integer> markDel = new Vector<Integer>();
		duplicate = new ReportTableModel(rtm.getAllColName(),true,true);
				
		// Sanity check and Init over
		
		for (int i=0; i < rowC; i++) {
			Object[] rowv = null;
			if (colI != null)
				rowv = rtm.getSelectedColRow(i,colI);
			else
				rowv = rtm.getRow(i);
			
			String rowStr="";
			
			// Create a string for row Values
			for (int j=0;j< rowv.length; j++) {
				if (rowv[j] == null)
					rowStr += "Null";
				else
					rowStr += rowv[j].toString();
			}
			BigInteger md5v = ResultsetToRTM.getMD5(rowStr);
			int indexf = md5array.indexOf(md5v);
			if (indexf == -1){ // not found so uniuqe row
				md5array.add(md5v);	
			} else { // not unique so mark for delete it
				markDel.add(i);
				duplicate.addFillRow(rtm.getRow(i));
			}
		}
		rtm.removeMarkedRows(markDel);
		
		return rtm;
	}
	
	public ReportTableModel showDuplicateModel () {
		
		return duplicate;
	}
	
	// This function will be used for address completion wrapper function
	public static ReportTableModel  completeRTMCOls (ReportTableModel rtm, Integer[] indexToComplete,int matchIndex,
				Hashtable<Object,Object> keyRowMap,ReportTableModel toFillFrom, int[] indexToFetch ) {
		if (indexToComplete.length != indexToFetch.length) {
			System.out.println("To Fill and From Fill columns does not match");
			return rtm;
		}

		int rowC = rtm.getModel().getRowCount();
		for (int i=0; i < rowC; i++ ) {
			Object matchO = rtm.getModel().getValueAt(i, matchIndex);
			int rowMatched = (Integer)keyRowMap.get(matchO);
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
	
	}
