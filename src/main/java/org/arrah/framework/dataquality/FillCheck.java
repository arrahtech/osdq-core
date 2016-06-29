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
 * This class is used finding fill ratio of a dataset
 * it will tell how many columns are null or empty
 *
 */

import java.util.Arrays;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;


public class FillCheck {
	
	// Constructor
	public FillCheck() {
		// Do nothing

	}
	
	// Count null or empty object
	public static int getEmptyNo(Object[] row) {
		int emptyNo=0;
		if ( row == null ) return emptyNo;
		
 		for ( int i=0; i < row.length; i++)
			if ( row[i] == null || "".equals(row[i].toString()) )
				emptyNo++;
		
		return emptyNo;
	}

	public static int getEmptyNo(Vector<Object> row) {
		int emptyNo=0;
		if ( row == null ) return emptyNo;
		emptyNo = getEmptyNo(row.toArray());
		return emptyNo;
	}
	
	public static int[] getEmptyCount(Object[][] dataset) {
		if (dataset == null) return null;
		int[] emptyCount = new int[dataset[0].length +1 ]; // one extra for all columns null
		Arrays.fill(emptyCount, 0);
		
		for (int i=0; i < dataset.length; i++) {
			int emptyNo = getEmptyNo(dataset[i]);
			emptyCount[emptyNo] = emptyCount[emptyNo] + 1;
		}
		
		return emptyCount;
	}
	
	public static int[] getEmptyCount(ReportTableModel rtm) {
		if (rtm== null) return null;
		int rowc = rtm.getModel().getRowCount();
		int colc = rtm.getModel().getColumnCount();
		
		int[] emptyCount = new int[colc +1]; // one extra for all columns null
		Arrays.fill(emptyCount, 0);
		
		for (int i=0; i <rowc; i++ ) {
			Object[] row = rtm.getRow(i);
			int emptyNo = getEmptyNo(row);
			emptyCount[emptyNo] = emptyCount[emptyNo] + 1;
		}
		
		return emptyCount;
	}
	
	// This function will return rows matching no of empty columns
	public static ReportTableModel getEmptyCount(ReportTableModel rtm, int emptyCount) {
		if (rtm == null || emptyCount >= rtm.getModel().getColumnCount())
			return rtm;
		
		ReportTableModel newRTM = new ReportTableModel(rtm.getAllColName(),true,true);
		int rowc = rtm.getModel().getRowCount();
		
		for (int i=0; i <rowc; i++ ) {
			Object[] row = rtm.getRow(i);
			int emptyNo = getEmptyNo(row);
			if (emptyNo == emptyCount )
				newRTM.addFillRow(row);
		}
		
		return newRTM;
	}
	
	
}
