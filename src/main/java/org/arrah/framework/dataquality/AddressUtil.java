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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.arrah.framework.ndtable.RTMUtil;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.util.DiscreetRange;
import org.arrah.framework.util.KeyValueParser;
import org.arrah.framework.util.StringCaseFormatUtil;
import org.arrah.framework.wrappertoutil.DQUtil;

public class AddressUtil {
	static Hashtable<String,String> htSec = getSecondaryTypeStd();
	static Hashtable<String,String> htStr = null;
	
	
	// Constructor
	public AddressUtil() {
		// Do nothing for time being
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
	private static void completeColumnsRowIndex (ReportTableModel rtm,int rowI, Integer[] indexToComplete, Object[] valuesToFill) {
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
	// Not in use for now
	@SuppressWarnings("unused")
	private static Object[]  completeColumns (Object[] row, Integer[] indexToComplete, Object[] valuesToFill) {
		for (int i=0; i <indexToComplete.length; i++) {
			Object o = row[indexToComplete[i]];
			if (o == null || o.toString().equals("")) // override
				row[indexToComplete[i]] = valuesToFill[i];
		}
		return row;
	}
	
	// This function will be used for fetch zip/pin or first index who may be empty in toFill table
	// Skip the first index assuming it is empty
	private static Object[] getMatchedFirstIndex(Object[] recordToMatch,ReportTableModel toFillFrom, Integer[] indexToFetch ) {
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
		
		String[] stdAddressArray = new String[6];
		if (addressString == null || "".equals(addressString))
			return stdAddressArray;
		
		
		String[] rawSplitToken = addressString.trim().split("\\s+|,|#");
		
		boolean isDirectional = false;
		boolean isforthDirectional = false;
		List<String> validSplitToken = new ArrayList<String>();
		
		// remove meta character from start and end and find valid token
		for (int i=0; i < rawSplitToken.length; i++) {
			
			if (rawSplitToken[i] == null || rawSplitToken[i].isEmpty())
				continue;
			
			String afterMetaCharCleaning = StringCaseFormatUtil.removeMetaCharString(rawSplitToken[i], "", true, false, true);
			
			if (afterMetaCharCleaning == null || "".equals(afterMetaCharCleaning))
				continue;
			
			validSplitToken.add(afterMetaCharCleaning);
		}
		
		String[] validToken = new String[validSplitToken.size()];
		validToken = validSplitToken.toArray(validToken);
		
		//System.out.println("Valid Token"+Arrays.toString(validToken));
		
		
		
		if (validToken.length == 3) { // Standard Address
			stdAddressArray[0] = validToken[0]; stdAddressArray[2] = validToken[1]; stdAddressArray[3] = validToken[2];
			return stdAddressArray;
		} 
		
		if (validToken.length > 3) { // it may be directional or secondary unit type and lot in one
			
			stdAddressArray[0] = validToken[0];
			
			String directionStr= getdirectionalVal(validToken[1]); 
			
			if (directionStr== null|| "".equals(directionStr)) { 
				// 2nd value is not directornal 
				stdAddressArray[2] = validToken[1]; stdAddressArray[3] = validToken[2];
				
			} else { // 2nd value is  directornal like 1200 N Main st
				
				stdAddressArray[1] = directionStr; stdAddressArray[2] = validToken[2]; stdAddressArray[3] = validToken[3];
				if (validToken.length == 4) // Standard directional address
						return stdAddressArray;
				isDirectional = true;
			}
			
			directionStr= getdirectionalVal(validToken[3]); // Now check 4th value
			
			if (directionStr== null|| "".equals(directionStr)) { // 4th value is not directornal . 
				
				stdAddressArray[2] = validToken[1]; stdAddressArray[3] = validToken[2]; stdAddressArray[4] = validToken[3];
				
			} else { // 4th value is  directional like 1200  Main st South
				
				stdAddressArray[1] = directionStr; stdAddressArray[2] = validToken[1]; stdAddressArray[3] = validToken[2];
				
				isforthDirectional= true;
			}
			
			// 4 token are valid only if if one value is directional
			// Some street might have two to more words
			
			if (validToken.length == 4 ) {
				
				if (isDirectional || isforthDirectional) { // 1200 N Main St or 1200 Main st North
				
					return stdAddressArray;
				
				} else {
					
				// like 1200 Main St STE110 or 12 George Washinton Ave
				// last token may be secondary unit type and secondary lot type in one
				// so try to split it
					
					// check if last is street type
					String streetType = streetSuffixStd(validToken[3]);
					//System.out.println(streetType);
					
					if (streetType != null && streetType.isEmpty() == false) {
						
						// 12 George Washinton Ave
						
						stdAddressArray[2] = validToken[1]+" "+ validToken[2]; stdAddressArray[3] = validToken[3];
						stdAddressArray[4] = ""; //reset previously set value
						
						return stdAddressArray;
						
					} else { // 4th token is not street type
						
						//1200 Main St STE110  Might be secondary unit type and lot in one
						String[] secondaryAddr = StringCaseFormatUtil.splitDigitLetter(validToken[3]);
						//System.out.println(secondaryAddr.toString());
						
						stdAddressArray[2] = validToken[1]; stdAddressArray[3] = validToken[2]; 
						stdAddressArray[4] = secondaryAddr[1];stdAddressArray[5] = secondaryAddr[0];
						
						return stdAddressArray;
					}
				
				}
			} // end of validToken.length == 4
			
			
			// Now check for full address
			if (validToken.length > 4) {
				
				int streetsufixIndex = 2; // 12 main st
				
				if (isDirectional)
					streetsufixIndex = 3; // 12 north main st
				
				//System.out.println("Initial "+streetsufixIndex + "isDirectional: " + isDirectional );
				
				for (int i=streetsufixIndex; i < validToken.length; i++) {
					
					//System.out.println("Token "+ validToken[i]);
					
					String streetType = streetSuffixStd(validToken[i]);
					
					if (streetType != null && streetType.isEmpty() == false) {
						streetsufixIndex = i; // got street sufiix id
						
//						System.out.println("Got "+ streetType+":"+streetsufixIndex);
//						System.out.println(Arrays.toString(validToken));
						break;
					}
					
				}
				
				// Append street Name and street suffix	
				int streetNameStartIndex = (isDirectional) ? 2 : 1;
				
				stdAddressArray[2]=""; // reset
				
				for (int i = streetNameStartIndex; i < streetsufixIndex; i++) {
					stdAddressArray[2] = stdAddressArray[2]+ " "+ validToken[i];
				}
				stdAddressArray[2] = stdAddressArray[2].trim();
				stdAddressArray[3] = validToken[streetsufixIndex];
					
				
				// If street suffix is last index
				if (streetsufixIndex + 1 == validToken.length) { // 12 George W washinton ave
					return stdAddressArray;
				}
				
				// Check if after suffix it is directional
				if (isDirectional == false  && validToken.length > streetsufixIndex+1) {
					
					directionStr= getdirectionalVal(validToken[streetsufixIndex+1]); // Now check next value of street suffix
					
					if (directionStr != null && "".equals(directionStr) == false) { // it is directional .
						isDirectional= true;
						
						stdAddressArray[1] = directionStr; 
						
						// If direction is last index
						if (streetsufixIndex + 2 == validToken.length) { // 12 George W washinton ave
							return stdAddressArray;
						}
					
					}
				}
				
				// Now secondary type and lot. It can be one string or two strings
				int secondaryStartIndex = (isDirectional) ? streetsufixIndex+2 : streetsufixIndex+1;
				//System.out.println("Street Suffix:" + streetsufixIndex);
				
				if (validToken.length >= secondaryStartIndex + 2) {
					
					stdAddressArray[4] = validToken[secondaryStartIndex];
					
					stdAddressArray[5]="";
					for (int i=secondaryStartIndex + 1; i < validToken.length; i++)
						stdAddressArray[5] = stdAddressArray[5] +" "+validToken[i];
					
					stdAddressArray[5]= stdAddressArray[5].trim();
					
					return stdAddressArray;
					
				} else {
					
					String[] secondaryAddr = StringCaseFormatUtil.splitDigitLetter(validToken[secondaryStartIndex]);
					stdAddressArray[5] = secondaryAddr[0];
					stdAddressArray[4] = secondaryAddr[1];
					
					return stdAddressArray;
				}

			} // end of > 4

			
		} // end of non-standard loop
		
		return stdAddressArray;
		
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
	
	public static Hashtable<String,String> getSecondaryTypeStd() {
		Hashtable<String,String> ht = new Hashtable<String,String>();
		ht.put("APARTMENT","APT");
		ht.put("APT","APT");
		ht.put("STOP","STOP");
		ht.put("PIER","PIER");
		ht.put("PENTHOUSE","PH");
		ht.put("PH","PH");
		ht.put("LOT","LOT");
		ht.put("ROOM","RM");
		ht.put("RM","RM");
		ht.put("TRAILER","TRLR");
		ht.put("TRLR","TRLR");
		ht.put("UPPER","UPPR");
		ht.put("UPPR","UPPR");
		ht.put("FLOOR","FL");
		ht.put("FL","FL");
		ht.put("SUITE","STE");
		ht.put("STE","STE");
		ht.put("SPACE","SPC");
		ht.put("SPC","SPC");
		ht.put("SIDE","SIDE");
		ht.put("LOWER","LOWR");
		ht.put("LOWR","LOWR");
		ht.put("SLIP","SLIP");
		ht.put("HANGAR","HNGR");
		ht.put("HNGR","HNGR");
		ht.put("BUILDING","BLDG");
		ht.put("BLDG","BLDG");
		ht.put("LOBBY","LBBY");
		ht.put("LBBY","LBBY");
		ht.put("REAR","REAR");
		ht.put("BASEMENT","BSMT");
		ht.put("BSMT","BSMT");
		ht.put("UNIT","UNIT");
		ht.put("FRONT","FRNT");
		ht.put("FRNT","FRNT");
		ht.put("OFFICE","OFC");
		ht.put("OFC","OFC");
		ht.put("DEPARTMENT","DEPT");
		ht.put("DEPT","DEPT");
		
		return ht;
	}
	
	public static String[]  usaAddressSecondLineStd (String addressString) {
		// [Lot] [Directional] [Street] [StreetSuffix]  [Secondary Unit Type] [Secondary Unit lot]
		try {
			String[]  s = splitUSAddressSecondLine ( addressString);
			
			//System.out.println("After splitUS" + Arrays.toString(s));
		
			if (s[3] != null && "".equals(s[3]) == false) {
				
				s[3] = streetSuffixStd(s[3]);
			}
			
			if (s[4] != null && "".equals(s[4]) == false) {
				
				s[4] = secondaryTypeStd(s[4]);
			}
			
			//System.out.println("After Std" + Arrays.toString(s));
			return s;
			
		} catch (Exception e) {
			System.out.println("Exception:"+ e.getLocalizedMessage());
			System.out.println("Could not parse:"+addressString);
			return new String[6] ;
		}
		
		
	}
	
    public static ReportTableModel addrStandardRTM(ReportTableModel rtm, int addrcolindex, String referenceFile) {
    		
		// Open keyval pair reference file
    	if (htStr != null)
    		htStr.clear();
    	
		htStr = KeyValueParser.parseFile(referenceFile);
		
		ArrayList<String> keyList = new ArrayList<String>();
		
		for(Enumeration<String> s =htStr.keys(); s.hasMoreElements();)
			keyList.add(s.nextElement());
		
		//for debug
		//System.out.println( referenceKeyVal.toString() );
	
		// Below is the format for second Line split
		// [Lot] [Directional] [Street] [StreetSuffix]  [Secondary Unit Type] [Secondary Unit lot]
		int rowc = rtm.getModel().getRowCount();
		String[] lotAddr = new String[rowc]; String[] stName = new String[rowc];String[] secondType = new String[rowc];
		String[] dirValue = new String[rowc]; String[] stType = new String[rowc];String[] secondUnit = new String[rowc];

		for (int i =0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, addrcolindex);
			if (o != null ) {
			 String s[] = AddressUtil.usaAddressSecondLineStd(o.toString().toUpperCase());

//			 System.out.println(o.toString().toUpperCase());
//			 System.out.println(Arrays.toString(s));
			 
			 // Now do a fuzzy match for StreetSuffix or StreetType
//			 ArrayList<String> matchedString = DQUtil.matchFuzzyString(s[3], keyList, 0.8);
//			 if(matchedString != null && matchedString.isEmpty() == false) {
//				 if (matchedString.size() == 1) {
//					 s[3] = htStr.get(matchedString.get(0));
//				 }
//				 else {
//					 for(String sp:matchedString)
//					 	System.out.println("Conflicting Fuzzy matches:" +sp + " for:" + s[3]);
//				 }
//			 }

			 // empty string not null value
			 for (int j=0; j < 6; j++ )
				 if ( s[j]  == null) s[j] = "";
				 
			 lotAddr[i] = s[0];dirValue[i] = s[1];stName[i] = s[2];stType[i] = s[3];secondType[i] = s[4];secondUnit[i] = s[5];
			 
//				System.out.println("[Lot -"+s[0]+
//						"] [Directional -" +s[1] +
//						"] [Street -" +s[2] +
//						"] [StreetSuffix -" +s[3] +
//						"] [Secondary Unit Type -" +s[4] +
//						"] [Secondary Unit lot -" +s[5] +"]"
//						);
			}
			
		}
		
		rtm.getModel().addColumn("LotAddress", lotAddr);
		rtm.getModel().addColumn("StreetName", stName);
		rtm.getModel().addColumn("StreetType", stType);
		rtm.getModel().addColumn("DirectionalAddress", dirValue);
		rtm.getModel().addColumn("SecondaryType", secondType);
		rtm.getModel().addColumn("SecondaryUnit", secondUnit);
		
		
		return rtm;
    }
    
    private static String streetSuffixStd(String streetsuffix) {
    		String streetstdval = htStr.get(streetsuffix);
    		
//    		System.out.println("Going to match:" + streetsuffix);
//    		System.out.println("val:" + streetstdval);
//    		
    		if (streetstdval != null) 
    			return streetstdval;
    		
    		ArrayList<String> keyList = new ArrayList<String>();
    		
    		for(Enumeration<String> s =htStr.keys(); s.hasMoreElements();)
    			keyList.add(s.nextElement());
    		
    		// Now do a fuzzy match for StreetSuffix or StreetType
			 ArrayList<String> matchedString = DQUtil.matchFuzzyString(streetsuffix, keyList, 0.8);
			 
			 if(matchedString != null  && matchedString.isEmpty() == false) {
				 if (matchedString.size() > 1)
					 for(String sp:matchedString)
						 	System.out.println("Conflicting Fuzzy matches:" +sp + " for:" +streetsuffix);
			 
				 return htStr.get(matchedString.get(0));
			 } else {
				 return null;
			 }
    	
    }
    
    private static String secondaryTypeStd(String secondaryType) {
		String secondarystdval = htSec.get(secondaryType);
		
		if (secondarystdval != null) 
			return secondarystdval;
		
		ArrayList<String> keyList = new ArrayList<String>();
		
		for(Enumeration<String> s =htSec.keys(); s.hasMoreElements();)
			keyList.add(s.nextElement());
		
		// Now do a fuzzy match for StreetSuffix or StreetType
		 ArrayList<String> matchedString = DQUtil.matchFuzzyString(secondaryType, keyList, 0.8);
		 
		 if(matchedString != null  && matchedString.isEmpty() == false) {
			 if (matchedString.size() > 1)
				 for(String sp:matchedString)
					 	System.out.println("Conflicting Fuzzy matches:" +sp + " for:" +secondaryType);
		 
			 return htSec.get(matchedString.get(0));
		 } else {
			 return secondaryType;
		 }
		 
	
}
	
} // end of AddressUtilclass
