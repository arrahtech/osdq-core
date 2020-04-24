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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.arrah.framework.ndtable.RTMUtil;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.util.DiscreetRange;
import org.arrah.framework.util.StringCaseFormatUtil;

public class AddressUtil {
	static Hashtable<String,String> htSec = getSecondaryTypeStd();
	static Hashtable<String,String> htStr = getSreetTypeStd();
	
	
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
		System.out.println(Arrays.toString(validToken));
		
		
		
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
				
				isDirectional= true;
			}
			
			// 4 token are valid only if if one value is directional
			if (validToken.length == 4 && isDirectional) { // 1200 N Main St or 1200 Main st North
				
				return stdAddressArray;
				
			} else { // like 1200 Main St STE110 or
				// last token may be secondary unit type and secondary lot type in one
				// so try to split it
				
			}
			
			if (validToken.length > 4) {
				if (isDirectional == false)  { // 1200 Main St STE 110
					stdAddressArray[5] = validToken[4];
				} else { // 1200 Main St North STE110
					stdAddressArray[4] = validToken[4];
				}
			}
			
			if (validToken.length == 5) //  like 1200 Main St STE 110 or 1200 N Main St STE#110
				return stdAddressArray;
			
			stdAddressArray[5] = validToken[5]; // 1200 N Main St STE 110
			
		} // end of non standard loop
		
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
		//ht.put("STOP","STOP");
		ht.put("PIER","PIER");
		ht.put("PENTHOUSE","PH");
		//ht.put("LOT","LOT");
		ht.put("ROOM","RM");
		ht.put("TRAILER","TRLR");
		ht.put("UPPER","UPPR");
		ht.put("FLOOR","FL");
		ht.put("SUITE","STE");
		ht.put("SPACE","SPC");
		//ht.put("SIDE","SIDE");
		ht.put("LOWER","LOWR");
		//ht.put("SLIP","SLIP");
		ht.put("HANGAR","HNGR");
		ht.put("BUILDING","BLDG");
		ht.put("LOBBY","LBBY");
		//ht.put("REAR","REAR");
		ht.put("BASEMENT","BSMT");
		//ht.put("UNIT","UNIT");
		ht.put("FRONT","FRNT");
		ht.put("OFFICE","OFC");
		ht.put("DEPARTMENT","DEPT");
		
		return ht;
	}
	
	public static Hashtable<String,String> getSreetTypeStd() {
		Hashtable<String,String> ht = new Hashtable<String,String>();
		
		ht.put("SHOAR","SHR");
		//ht.put("LCKS","LCKS");
		ht.put("SPRNG","SPG");
		ht.put("BOT","BTM");
		ht.put("SHOAL","SHL");
		ht.put("UNDERPASS","UPAS");
		ht.put("DRIV","DR");
		ht.put("COURT","CT");
		//ht.put("HBR","HBR");
		ht.put("PINE","PNE");
		ht.put("JUNCTON","JCT");
		ht.put("EXPRESSWAY","EXPY");
		//ht.put("CRES","CRES");
		//ht.put("KNLS","KNLS");
		ht.put("THROUGHWAY","TRWY");
		ht.put("MOUNTAIN","MTN");
		ht.put("MNTN","MTN");
		//ht.put("ORCH","ORCH");
		ht.put("STRAVEN","STRA");
		//ht.put("MTN","MTN");
		ht.put("FORGE","FRG");
		//ht.put("CSWY","CSWY");
		ht.put("GATWAY","GTWY");
		ht.put("LIGHTS","LGTS");
		ht.put("JCTNS","JCTS");
		ht.put("ISLES","ISLE");
		ht.put("VILL","VLG");
		ht.put("HLLW","HOLW");
		ht.put("GRDEN","GDN");
		//ht.put("FLDS","FLDS");
		//ht.put("VLY","VLY");
		//ht.put("FT","FT");
		//ht.put("BND","BND");
		ht.put("SPURS","SPUR");
		ht.put("PLAIN","PLN");
		ht.put("HARBORS","HBRS");
		ht.put("KNOLL","KNL");
		ht.put("DAM","DM");
		//ht.put("ISS","ISS");
		ht.put("CREEK","CRK");
		ht.put("CLIFF","CLF");
		ht.put("HRBOR","HBR");
		ht.put("VILLAGE","VLG");
		//ht.put("VLG","VLG");
		//ht.put("EST","EST");
		//ht.put("OVAL","OVAL");
		ht.put("STREME","STRM");
		ht.put("CRESCENT","CRES");
		//ht.put("MNRS","MNRS");
		ht.put("TUNEL","TUNL");
		//ht.put("PNES","PNES");
		ht.put("FALLS","FLS");
		ht.put("STATION","STA");
		ht.put("BLUFF","BLF");
		ht.put("COVE","CV");
		ht.put("CRCL","CIR");
		//ht.put("FLT","FLT");
		//ht.put("FLS","FLS");
		ht.put("CENTR","CTR");
		//ht.put("PKWY","PKWY");
		//ht.put("KEY","KY");
		ht.put("TURNPIKE","TPKE");
		ht.put("REST","RST");
		ht.put("PARKWAYS","PKWY");
		ht.put("FORDS","FRDS");
		//ht.put("FLD","FLD");
		//ht.put("PIKE","PIKE");
		//ht.put("DV","DV");
		ht.put("RANCH","RNCH");
		//ht.put("BLF","BLF");
		//ht.put("DR","DR");
		ht.put("STRAV","STRA");
		ht.put("NECK","NCK");
		//ht.put("DM","DM");
		//ht.put("DL","DL");
		ht.put("LODGE","LDG");
		//ht.put("HWY","HWY");
		//ht.put("ARC","ARC");
		ht.put("CRSNT","CRES");
		ht.put("GROVES","GRVS");
		ht.put("FIELD","FLD");
		ht.put("CEN","CTR");
		//ht.put("MEWS","MEWS");
		ht.put("BEACH","BCH");
		ht.put("HIGHWY","HWY");
		ht.put("FLAT","FLT");
		ht.put("TRNPK","TPKE");
		ht.put("GLEN","GLN");
		//ht.put("CV","CV");
		ht.put("VILLAG","VLG");
		//ht.put("CT","CT");
		ht.put("CROSSROAD","XRD");
		//ht.put("CP","CP");
		//ht.put("VIS","VIS");
		ht.put("TRACES","TRCE");
		ht.put("VISTA","VIS");
		ht.put("CLIFFS","CLFS");
		//ht.put("RIV","RIV");
		ht.put("UNIONS","UNS");
		ht.put("VSTA","VIS");
		ht.put("BROOK","BRK");
		//ht.put("HVN","HVN");
		//ht.put("VIA","VIA");
		ht.put("DALE","DL");
		//ht.put("EXTS","EXTS");
		ht.put("WELL","WL");
		ht.put("EXTENSION","EXT");
		ht.put("PIKES","PIKE");
		ht.put("LAKES","LKS");
		ht.put("CROSSING","XING");
		ht.put("EXTN","EXT");
		//ht.put("BR","BR");
		//ht.put("WALL","WALL");
		//ht.put("WALK","WALK");
		ht.put("LOCKS","LCKS");
		ht.put("CNYN","CYN");
		ht.put("HILLS","HLS");
		ht.put("SHOARS","SHRS");
		ht.put("DVD","DV");
		//ht.put("PLNS","PLNS");
		ht.put("BURGS","BGS");
		ht.put("BLUFFS","BLFS");
		ht.put("GROV","GRV");
		ht.put("SUMIT","SMT");
		ht.put("SQUARES","SQS");
		//ht.put("WAY","WAY");
		ht.put("CORS","CORS");
		//ht.put("FRKS","FRKS");
		ht.put("AV","AVE");
		//ht.put("PTS","PTS");
		ht.put("MNT","MT");
		ht.put("BOTTM","BTM");
		//ht.put("MNR","MNR");
		ht.put("LODG","LDG");
		ht.put("VILLIAGE","VLG");
		ht.put("VLLY","VLY");
		ht.put("ORCHARD","ORCH");
		ht.put("FREEWY","FWY");
		ht.put("FORESTS","FRST");
		ht.put("VILLG","VLG");
		ht.put("TUNNEL","TUNL");
		ht.put("CANYN","CYN");
		ht.put("SPNGS","SPGS");
		ht.put("ANNEX","ANX");
		ht.put("VILLE","VL");
		//ht.put("HTS","HTS");
		//ht.put("HOLW","HOLW");
		ht.put("COVES","CVS");
		//ht.put("XING","XING");
		ht.put("EXTNSN","EXT");
		ht.put("SQRS","SQS");
		//ht.put("ANX","ANX");
		ht.put("BYPASS","BYP");
		ht.put("PLAINS","PLNS");
		ht.put("COURTS","CTS");
		ht.put("RIVER","RIV");
		ht.put("CRSENT","CRES");
		ht.put("MOUNTAINS","MTNS");
		ht.put("LOCK","LCK");
		ht.put("HIGHWAY","HWY");
		ht.put("CRCLE","CIR");
		ht.put("STRVN","STRA");
		ht.put("FLATS","FLTS");
		ht.put("SQRE","SQ");
		ht.put("POINT","PT");
		ht.put("VIEW","VW");
		ht.put("SPRING","SPG");
		ht.put("DRIVE","DR");
		ht.put("SPRNGS","SPGS");
		ht.put("PORT","PRT");
		ht.put("JCTS","JCTS");
		ht.put("BOULV","BLVD");
		ht.put("BRDGE","BRG");
		//ht.put("KYS","KYS");
		ht.put("HARBOR","HBR");
		ht.put("JCTN","JCT");
		ht.put("SHORE","SHR");
		ht.put("TRL","TRL");
		ht.put("TRK","TRAK");
		//ht.put("PRT","PRT");
		ht.put("ANNX","ANX");
		ht.put("LANDING","LNDG");
		ht.put("PRR","PR");
		ht.put("SUMMIT","SMT");
		ht.put("ROAD","RD");
		ht.put("STREETS","STS");
		ht.put("PRK","PARK");
		ht.put("COURSE","CRSE");
		ht.put("HIWY","HWY");
		ht.put("VIADCT","VIA");
		ht.put("WY","WAY");
		ht.put("EXPY","EXPY");
		ht.put("CIRCLE","CIR");
		ht.put("STRAVENUE","STRA");
		ht.put("EXPW","EXPY");
		ht.put("MEADOWS","MDWS");
		//ht.put("ALY","ALY");
		ht.put("GARDEN","GDN");
		ht.put("PASSAGE","PSGE");
		ht.put("DRV","DR");
		ht.put("EXPR","EXPY");
		ht.put("LIGHT","LGT");
		ht.put("RAMP","RAMP");
		ht.put("LOAF","LF");
		ht.put("ESTATES","ESTS");
		ht.put("TRAILS","TRL");
		ht.put("STREET","ST");
		ht.put("PLAZA","PLZ");
		ht.put("RAPID","RPD");
		ht.put("BROOKS","BRKS");
		//ht.put("RDS","RDS");
		ht.put("LANE","LN");
		ht.put("LAND","LAND");
		ht.put("BRIDGE","BRG");
		ht.put("FLTS","FLTS");
		//ht.put("VW","VW");
		ht.put("GTWY","GTWY");
		ht.put("KNOLLS","KNLS");
		//ht.put("RDG","RDG");
		//ht.put("MDWS","MDWS");
		//ht.put("VL","VL");
		//ht.put("SPUR","SPUR");
		ht.put("MEADOW","MDW");
		ht.put("CNTR","CTR");
		ht.put("CIRCL","CIR");
		ht.put("CORNER","COR");
		ht.put("TRLS","TRL");
		//ht.put("TRLR","TRLR");
		ht.put("OVL","OVAL");
		ht.put("ORCHRD","ORCH");
		//ht.put("ISLE","ISLE");
		ht.put("PINES","PNES");
		ht.put("FRWAY","FWY");
		ht.put("VALLEYS","VLYS");
		ht.put("SHORES","SHRS");
		//ht.put("UN","UN");
		ht.put("OVERPASS","OPAS");
		//ht.put("NCK","NCK");
		ht.put("MOUNTIN","MTN");
		//ht.put("JCT","JCT");
		ht.put("CENTRE","CTR");
		ht.put("GLENS","GLNS");
		ht.put("ALLEY","ALY");
		ht.put("GATEWAY","GTWY");
		//ht.put("CRSE","CRSE");
		ht.put("AVENU","AVE");
		ht.put("TURNPK","TPKE");
		ht.put("ARCADE","ARC");
		ht.put("SHOALS","SHLS");
		//ht.put("VLGS","VLGS");
		//ht.put("ESTS","ESTS");
		ht.put("TRKS","TRAK");
		//ht.put("PRTS","PRTS");
		ht.put("MOUNT","MT");
		ht.put("ISLANDS","ISS");
		ht.put("CROSSROADS","XRDS");
		ht.put("GARDENS","GDNS");
		ht.put("TERR","TER");
		ht.put("ALLEE","ALY");
		//ht.put("CLFS","CLFS");
		ht.put("COMMON","CMN");
		ht.put("CREST","CRST");
		ht.put("FREEWAY","FWY");
		ht.put("STR","ST");
		//ht.put("BCH","BCH");
		ht.put("GREENS","GRNS");
		ht.put("STN","STA");
		ht.put("RNCH","RNCH");
		//ht.put("LNDG","LNDG");
		ht.put("MOTORWAY","MTWY");
		//ht.put("FALL","FALL");
		//ht.put("STA","STA");
		ht.put("LAKE","LK");
		ht.put("STRT","ST");
		ht.put("PRAIRIE","PR");
		//ht.put("ST","ST");
		ht.put("VILLAGES","VLGS");
		//ht.put("SQ","SQ");
		ht.put("STRM","STRM");
		ht.put("AVENUE","AVE");
		ht.put("RAD","RADL");
		ht.put("PATHS","PATH");
		ht.put("VALLEY","VLY");
		//ht.put("CTS","CTS");
		//ht.put("CTR","CTR");
		//ht.put("STRA","STRA");
		ht.put("WELLS","WLS");
		ht.put("RAPIDS","RPDS");
		ht.put("STREAM","STRM");
		ht.put("RIDGES","RDGS");
		ht.put("SHRS","SHRS");
		//ht.put("PLZ","PLZ");
		ht.put("MNTNS","MTNS");
		ht.put("CURVE","CURV");
		ht.put("SQUARE","SQ");
		ht.put("CANYON","CYN");
		ht.put("GROVE","GRV");
		ht.put("VIEWS","VWS");
		ht.put("PLN","PLN");
		ht.put("BURG","BG");
		ht.put("TRLRS","TRLR");
		ht.put("CENTERS","CTRS");
		//ht.put("RD","RD");
		ht.put("ISLAND","IS");
		ht.put("ROADS","RDS");
		ht.put("BYP","BYP");
		ht.put("BOULEVARD","BLVD");
		ht.put("HLS","HLS");
		ht.put("TUNL","TUNL");
		ht.put("HARB","HBR");
		ht.put("MILL","ML");
		ht.put("GRV","GRV");
		ht.put("PKY","PKWY");
		ht.put("SQU","SQ");
		ht.put("RADIEL","RADL");
		ht.put("VWS","VWS");
		ht.put("SQR","SQ");
		ht.put("BYPAS","BYP");
		ht.put("GRN","GRN");
		ht.put("DRIVES","DRS");
		ht.put("CNTER","CTR");
		ht.put("MALL","MALL");
		ht.put("LKS","LKS");
		ht.put("CRK","CRK");
		ht.put("BYPS","BYP");
		ht.put("VIADUCT","VIA");
		ht.put("PKWAY","PKWY");
		ht.put("PT","PT");
		ht.put("PR","PR");
		ht.put("VDCT","VIA");
		ht.put("FWY","FWY");
		ht.put("MDW","MDWS");
		ht.put("PL","PL");
		ht.put("DIVIDE","DV");
		ht.put("BYPA","BYP");
		ht.put("RVR","RIV");
		ht.put("GRDNS","GDNS");
		//ht.put("SPG","SPG");
		ht.put("STATN","STA");
		//ht.put("PKWYS","PKWY");
		ht.put("WAYS","WAYS");
		ht.put("ISLND","IS");
		ht.put("GATEWY","GTWY");
		ht.put("AVEN","AVE");
		ht.put("ISLNDS","ISS");
		ht.put("INLT","INLT");
		ht.put("GRDN","GDN");
		ht.put("HWAY","HWY");
		ht.put("ANEX","ANX");
		ht.put("GTWAY","GTWY");
		ht.put("BLVD","BLVD");
		ht.put("LNDNG","LNDG");
		ht.put("FOREST","FRST");
		ht.put("HAVEN","HVN");
		ht.put("SPNG","SPG");
		//ht.put("RUN","RUN");
		ht.put("STRAVN","STRA");
		ht.put("FORT","FT");
		ht.put("CPE","CPE");
		ht.put("SKYWAY","SKWY");
		ht.put("RUE","RUE");
		ht.put("TUNNL","TUNL");
		ht.put("DIV","DV");
		ht.put("FORK","FRK");
		ht.put("HOLWS","HOLW");
		ht.put("FORG","FRG");
		ht.put("TRAIL","TRL");
		ht.put("BLUF","BLF");
		ht.put("FORD","FRD");
		ht.put("VIST","VIS");
		ht.put("VLYS","VLYS");
		ht.put("STRVNUE","STRA");
		//ht.put("COR","COR");
		//ht.put("RADL","RADL");
		ht.put("JCTION","JCT");
		ht.put("COMMONS","CMNS");
		ht.put("FRWY","FWY");
		ht.put("BRANCH","BR");
		ht.put("KEYS","KYS");
		ht.put("UNION","UN");
		ht.put("RNCHS","RNCH");
		ht.put("CRSSNG","XING");
		//ht.put("MT","MT");
		//ht.put("KNL","KNL");
		ht.put("PLZA","PLZ");
		ht.put("FORGES","FRGS");
		ht.put("VST","VIS");
		ht.put("RADIAL","RADL");
		//ht.put("SMT","SMT");
		ht.put("RIDGE","RDG");
		ht.put("CIRC","CIR");
		ht.put("PORTS","PRTS");
		ht.put("GREEN","GRN");
		ht.put("RST","RST");
		//ht.put("PATH","PATH");
		ht.put("MSSN","MSN");
		ht.put("LGT","LGT");
		ht.put("CIRCLES","CIRS");
		ht.put("SPRINGS","SPGS");
		ht.put("MEDOWS","MDWS");
		ht.put("BEND","BND");
		ht.put("TUNLS","TUNL");
		ht.put("LOOP","LOOP");
		ht.put("SHLS","SHLS");
		ht.put("BTM","BTM");
		ht.put("WLS","WLS");
		ht.put("HILL","HL");
		//ht.put("PASS","PASS");
		ht.put("PARKWAY","PKWY");
		ht.put("TRCE","TRCE");
		//ht.put("LN","LN");
		//ht.put("LK","LK");
		ht.put("TUNNELS","TUNL");
		ht.put("GDNS","GDNS");
		//ht.put("LF","LF");
		ht.put("CMP","CP");
		ht.put("MNTAIN","MTN");
		ht.put("MISSN","MSN");
		ht.put("GARDN","GDN");
		ht.put("HARBR","HBR");
		ht.put("FORKS","FRKS");
		//ht.put("KY","KY");
		ht.put("POINTS","PTS");
		ht.put("JUNCTIONS","JCTS");
		ht.put("TER","TER");
		ht.put("TERRACE","TER");
		ht.put("FRY","FRY");
		ht.put("RDGS","RDGS");
		ht.put("GLN","GLN");
		ht.put("HOLLOW","HOLW");
		ht.put("FRT","FT");
		ht.put("MANORS","MNRS");
		ht.put("PARK","PARK");
		ht.put("TRAILER","TRLR");
		ht.put("JUNCTN","JCT");
		ht.put("CAUSEWAY","CSWY");
		ht.put("EXT","EXT");
		ht.put("PARKWY","PKWY");
		//ht.put("FRK","FRK");
		ht.put("RDGE","RDG");
		ht.put("HOLLOWS","HOLW");
		ht.put("EXP","EXPY");
		ht.put("MILLS","MLS");
		ht.put("HIWAY","HWY");
		//ht.put("FRG","FRG");
		ht.put("EXPRESS","EXPY");
		//ht.put("CLF","CLF");
		//ht.put("FRD","FRD");
		ht.put("CAPE","CPE");
		//ht.put("BRK","BRK");
		ht.put("ESTATE","EST");
		//ht.put("TRAK","TRAK");
		ht.put("CLB","CLB");
		ht.put("JUNCTION","JCT");
		ht.put("BRG","BRG");
		ht.put("FIELDS","FLDS");
		ht.put("KNOL","KNL");
		ht.put("SUMITT","SMT");
		ht.put("CAUSWA","CSWY");
		ht.put("CLUB","CLB");
		ht.put("ROUTE","RTE");
		ht.put("TRAFFICWAY","TRFY");
		ht.put("ALLY","ALY");
		ht.put("LDGE","LDG");
		//ht.put("FRST","FRST");
		ht.put("PARKS","PARK");
		ht.put("AVNUE","AVE");
		//ht.put("LDG","LDG");
		ht.put("RPD","RPD");
		//ht.put("IS","IS");
		ht.put("CORNERS","CORS");
		ht.put("CENT","CTR");
		ht.put("MTIN","MTN");
		ht.put("TRACKS","TRAK");
		ht.put("RPDS","RPDS");
		ht.put("BRNCH","BR");
		ht.put("ROW","ROW");
		ht.put("RANCHES","RNCH");
		ht.put("FRRY","FRY");
		ht.put("BAYOU","BYU");
		ht.put("WALKS","WALK");
		ht.put("FERRY","FRY");
		//ht.put("LCK","LCK");
		ht.put("BAYOO","BYU");
		ht.put("CENTER","CTR");
		ht.put("HT","HTS");
		ht.put("AVN","AVE");
		ht.put("BOUL","BLVD");
		ht.put("BOTTOM","BTM");
		ht.put("VALLY","VLY");
		//ht.put("SPGS","SPGS");
		ht.put("SHR","SHR");
		ht.put("MANOR","MNR");
		ht.put("HL","HL");
		ht.put("LOOPS","LOOP");
		ht.put("TRACK","TRAK");
		//ht.put("AVE","AVE");
		ht.put("RIVR","RIV");
		ht.put("CIR","CIR");
		ht.put("SHL","SHL");
		//ht.put("TRACE","TRCE");
		ht.put("CAMP","CP");
		
		return ht;
	}
	
	public static String[]  usaAddressSecondLineStd (String addressString) {
		// [Lot] [Directional] [Street] [StreetSuffix]  [Secondary Unit Type] [Secondary Unit lot]
		String[]  s = splitUSAddressSecondLine ( addressString);
		
		if (s[3] != null && "".equals(s[3]) == false) {
			s[3] = s[3].toUpperCase();
			Object val= htStr.get(s[3]);
			if (val != null) 
				s[3] = val.toString();
		}
		
		if (s[4] != null && "".equals(s[4]) == false) {
			s[4] = s[4].toUpperCase();
			Object val= htSec.get(s[4]);
			if (val != null) 
				s[4] = val.toString();
		}
		
		return s;
		
	}
	
} // end of class
