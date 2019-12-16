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
import org.arrah.framework.util.StringCaseFormatUtil;

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
	
	// This function will be used for spliting freeflow address
	// like 12 Pleason drive will be [12][Pleason][drive]
	// More information can be found at https://pe.usps.com/text/pub28/28aph.htm
	// [Lot] [Directional] [Street] [StreetSuffix]  [Secondary Unit Type] [Secondary Unit lot]
	// 12/1 SW Lanlon st STE 201
	// Lot can be fractional or alpha numberic
	
	public static String[]  splitUSAddressSecondLine (String addressString) {
		String[] parseV = new String[6];
		String[] splitV = addressString.split(" .*");
		boolean isDirectional = false;
		
		// remove meta character from start and end 
		for (String s:splitV)
			StringCaseFormatUtil.removeMetaCharString(s, "", true, false, true);
		
		if (splitV.length == 3) { // Standard Address
			parseV[0] = splitV[0]; parseV[2] = splitV[1]; parseV[3] = splitV[2];
			return parseV;
		} 
		if (splitV.length > 3) { // it may be directional or secondary unit type and lot in one
			
			parseV[0] = splitV[0];
			String directionStr= getdirectionalVal(splitV[1]); 
			
			if (directionStr== null|| "".equals(directionStr)) { // 2nd value is not directornal 
				parseV[2] = splitV[1]; parseV[3] = splitV[2];
			} else { // 2nd value is  directornal like 1200 N Main st
				parseV[1] = directionStr; parseV[2] = splitV[2]; parseV[3] = splitV[3];
				if (splitV.length == 4) // Standard directional address
						return parseV;
				isDirectional = true;
			}
			
			directionStr= getdirectionalVal(splitV[3]); // Now check 4th value
			if (directionStr== null|| "".equals(directionStr)) { // 4th value is not directornal . 
				parseV[2] = splitV[1]; parseV[3] = splitV[2]; parseV[4] = splitV[3];
			} else { // 4th value is  directional like 1200  Main st South
				parseV[1] = directionStr; parseV[2] = splitV[1]; parseV[3] = splitV[2];
				isDirectional= true;
			}
			if (splitV.length == 4) //  like 1200 Main St STE110 or 1200 N Main St or 1200 Main st North
				return parseV;
			
			if (splitV.length > 4) {
				if (isDirectional == false)  { // 1200 Main St STE 110
					parseV[5] = splitV[4];
				} else { // 1200 Main St North STE110
					parseV[4] = splitV[4];
				}
			}
			
			if (splitV.length == 5) //  like 1200 Main St STE 110 or 1200 N Main St STE#110
				return parseV;
			
			parseV[5] = splitV[5]; // 1200 N Main St STE 110
			
			// Now secondary unit
			
		} // end of non standard loop
		
		
		return parseV;
		
	}

	private static String getdirectionalVal(String input) {
		String directionVal=null;
		String inputVal = input.toUpperCase();
		switch(inputVal) {
			case "N": case "NORTH":
				return "N";
			case "S": case "SOUTH":
				return "S";
			case "W": case "WEST":
				return "W";
			case "E": case "EAST":
				return "E";
			case "SW": case "SOUTHWEST":
				return "SW";
			case "SE": case "SOUTHEAST":
				return "SE";
			case "NW": case "NORTHWEST":
				return "NW";
			case "NE": case "NORTHEAST":
				return "NE";
				default:
		
		}
		
		return directionVal;
	}
	
} // end of class
