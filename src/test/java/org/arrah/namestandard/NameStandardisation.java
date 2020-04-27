/**
 * 
 */
/**
 * @author Vivek singh
 *
 */
package org.arrah.namestandard;


import org.arrah.framework.ndtable.*;
import org.arrah.framework.util.KeyValueParser;

import java.io.File;
import java.util.Hashtable;



public class NameStandardisation {
	
	public NameStandardisation() {
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
		//for debug
		//System.out.println( referenceKeyVal.toString() );
				
		// Run Standardisation
		//Hashtable<String,String> referencehash, ReportTableModel rtm, String options, int index)
		// options  - CaseInSensitive 1, Multiword 1, lietrals 1, fullmatch 1
		// 0101 - means CaseSensitive,Multiword true,noliterals (regex enabled), fullmatch true
		// index - column index for standardization
		 new DisplayFileAsTableCore().standardisationRegex(referenceKeyVal, rtm, "0101", Integer.parseInt(args[2]));
		 
		 
		// if you want to use fuzzy need to add lucene libraries
		// new DisplayFileAsTableCore().standardisationFuzzy(referenceKeyVal, rtm, Integer.parseInt(args[2]));
		 
		 rtm.toPrint();
				
		// Save the file
		System.out.println( args[3] );
		 rtm.saveAsOpenCSV(args[3]);
    	
		  }
	    
    	
}
    	