/**
 * 
 */
/**
 * @author Vivek singh
 *
 */
package com.arrah.namestandard;


import org.arrah.framework.dataquality.AddressUtil;
import org.arrah.framework.ndtable.*;
import org.arrah.framework.util.KeyValueParser;
import org.arrah.framework.wrappertoutil.DQUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;


public class AddrStandardisation {
	
	public AddrStandardisation() {
		// Default
	}
   
   
    public static void main(String[] args) {
    	
    	
    	// Validate the Input String
    	// InputFile, ReferenceFile, ColumnIndex,OutputLocation 
    	
    	if ((args.length < 4 ) || ( args[0] == null || "".equals(args[0]))
    			|| ( args[1] == null || "".equals(args[1] ) )
    			|| ( args[3] == null || "".equals(args[3]) ) ) 
    	System.out.println("Usage:InputFileLcoation,ReferenceFileLocation,ColumnIndex,OutputFIlelcoation" ) ;
    						
    	ReportTableModel rtm = null;	
    	// Open Input CSV file
		if (  !"\r".equals(args[0]) && !"\n".equals(args[0])) { // open the inputFile of CSV format
				
			// mac os append ctrl-M to file name - Strip if required
			for (int i=0; i < args.length; i++) {
				char last = args[i].charAt(args[i].length() -1);
				if (Character.isISOControl(last))
					args[i] = args[i].substring(0, args[i].length() - 1);
			}

			
			try {
				rtm = new CSVtoReportTableModel(new File(args[0])).loadOpenCSVIntoTable();
			} catch(Exception e) {
				System.out.println(e.getLocalizedMessage());
			}
		} else {
			System.out.println("Invalid input file location");
			return;
		}
		// for debug
		//rtm.toPrint();
		
		// Open keyval pair reference file
		Hashtable<String, String> referenceKeyVal = KeyValueParser.parseFile(args[1]);
		ArrayList<String> keyList = new ArrayList<String>();
		
		for(Enumeration<String> s =referenceKeyVal.keys(); s.hasMoreElements();)
			keyList.add(s.nextElement());
		
		//for debug
		//System.out.println( referenceKeyVal.toString() );
	
		// Below is the format for second Line split
		// [Lot] [Directional] [Street] [StreetSuffix]  [Secondary Unit Type] [Secondary Unit lot]
		int rowc = rtm.getModel().getRowCount();
		String[] lotAddr = new String[rowc]; String[] stName = new String[rowc];String[] secondType = new String[rowc];
		String[] dirValue = new String[rowc]; String[] stType = new String[rowc];String[] secondUnit = new String[rowc];

		for (int i =0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, Integer.parseInt(args[2]));
			if (o != null ) {
			 String s[] = AddressUtil.usaAddressSecondLineStd(o.toString());

			 // Now do a fuzzy match for StreetSuffix or StreetType
			 ArrayList<String> matchedString = DQUtil.matchFuzzyString(s[3], keyList, 0.8);
			 if(matchedString != null && matchedString.isEmpty() == false) {
				 if (matchedString.size() == 1) {
					 s[3] = referenceKeyVal.get(matchedString.get(0));
				 }
				 else {
					 for(String sp:matchedString)
					 	System.out.println("Conflicting Fuzzy matches:" +sp + " for:" + s[3]);
				 }
			 }

			 // empty string not null value
			 for (int j=0; j < 6; j++ )
				 if ( s[j]  == null) s[j] = "";
				 
			 lotAddr[i] = s[0];dirValue[i] = s[1];stName[i] = s[2];stType[i] = s[3];secondType[i] = s[4];secondUnit[i] = s[5];
			 
				System.out.println("[Lot -"+s[0]+
						"] [Directional -" +s[1] +
						"] [Street -" +s[2] +
						"] [StreetSuffix -" +s[3] +
						"] [Secondary Unit Type -" +s[4] +
						"] [Secondary Unit lot -" +s[5] +"]"
						);
			}
			
		}
		
		rtm.getModel().addColumn("LotAddress", lotAddr);
		rtm.getModel().addColumn("DirectionalAddress", dirValue);
		rtm.getModel().addColumn("StreetName", stName);
		rtm.getModel().addColumn("StreetType", stType);
		rtm.getModel().addColumn("SecondaryType", secondType);
		rtm.getModel().addColumn("SecondaryUnit", secondUnit);
		
		
		// for debug
		//rtm.toPrint();
				
		// Save the file
		System.out.println( args[3] );
		rtm.saveAsOpenCSV(args[3]);  	
    } // end of main
	    
    	
}
    	