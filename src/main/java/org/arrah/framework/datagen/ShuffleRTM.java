package org.arrah.framework.datagen;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2013    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for shuffle data
 * and Mask data in  RTM columns.
 * 
 */

import java.util.ArrayList;
import java.util.Collections;

import org.arrah.framework.ndtable.ReportTableModel;


public class ShuffleRTM {


	public ShuffleRTM() {
		
	} // Constructor
	
	public static String shuffleString(String str) {
		if ( str == null || "".equals(str)) return str;
		
		ArrayList<Character> charList = new ArrayList<Character>();
		for (int i=0; i < str.length(); i++) {
			charList.add(str.charAt(i));
		}
		Collections.shuffle(charList);
		
		String newVal = new String();
		for (int i=0; i < charList.size(); i++) {
			newVal = newVal.concat(charList.get(i).toString());
		}
		return newVal;
		
	}
	
	public static ArrayList<Integer> shuffleIntegerList(ArrayList<Integer> rowNumber) {
		if ( rowNumber == null || rowNumber.size() ==0 ) return rowNumber;
		
		Collections.shuffle(rowNumber);
		return rowNumber;
		
	}
	/* It will modify individual record */
	
	public static ReportTableModel shuffleRecord(ReportTableModel rtm, int colIndex, int beginRow, int endRow) {
		if (rtm == null || rtm.getModel().getRowCount() == 0) return rtm;
		for (int i = beginRow; i < endRow; i++ ) {
			try {
				String colVal = (rtm.getModel().getValueAt(i, colIndex)).toString();
				String newColVal = shuffleString(colVal);
				rtm.setValueAt(newColVal,i, colIndex);
			} catch (Exception e) {
				rtm.setValueAt(rtm.getModel().getValueAt(i, colIndex),i, colIndex);
			}
		}
		return rtm;
		
	}
	
	/* It will shuffle across table but will not modify record */
	public static ReportTableModel shuffleColumns(ReportTableModel rtm, int[] colIndex, int beginRow, int endRow) {
		if (rtm == null || rtm.getModel().getRowCount() == 0) return rtm;
		ArrayList<Integer> rowList = new ArrayList<Integer>();
		for (int i = beginRow; i < endRow; i++ ) {
			rowList.add(i);
		}
		rowList = shuffleIntegerList(rowList);
		
		for (int i = beginRow; i < endRow; i++ ) {
			for (int j =0; j<colIndex.length; j++) {
				Object colVal = (rtm.getModel().getValueAt(i, colIndex[j]));
				Object newcolVal = (rtm.getModel().getValueAt(rowList.get(i), colIndex[j]));
				rtm.setValueAt(colVal,rowList.get(i),colIndex[j]);
				rtm.setValueAt(newcolVal,i,colIndex[j]);
			}
			
		} // Shuffling done
		
		return rtm;
	}

	// It will mask columns with given maskChar **** /
	public static ReportTableModel maskColumn(ReportTableModel rtm, int colIndex, 
				int beginRow, int endRow, String maskChar, int maskPosition) {
		
		if (rtm == null || rtm.getModel().getRowCount() == 0) return rtm;
		
		for (int i = beginRow; i < endRow; i++ ) {
			Object o = rtm.getModel().getValueAt(i, colIndex);
			if ( o == null) {
				rtm.setValueAt(o,i,colIndex);
				continue;
			}
			String colVal = o.toString();
			String newcolVal = null;
			if ( maskChar.length() > colVal.length() )
				newcolVal = maskChar;
			else {
				switch (maskPosition) {
				case 0: // start from begining
					newcolVal = maskChar +colVal.substring(maskChar.length());
					break;
				case -1 : // start from end
					newcolVal = colVal.substring(0,colVal.length() -maskChar.length()) + maskChar;
					break;
				default: // start from pos of colIndex
					newcolVal = colVal.substring(0,maskPosition) + maskChar;
					break;
			}
			} // end of else

			rtm.setValueAt(newcolVal,i,colIndex);
			
		} // Masking done
		
		return rtm;
	}
	
} // End of ShuffleRTM
