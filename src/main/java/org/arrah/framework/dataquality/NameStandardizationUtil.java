package org.arrah.framework.dataquality;

/***************************************************
 *     Copyright to Vivek Kumar Singh 2020         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                           *
 *                                                 *
 **************************************************/

/*
 * This file will also be used creating utilities for
 * name standardization.
 * Like remove s/o, c/o and meta characters.
 */


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.util.AsciiParser;
import org.arrah.framework.util.StringCaseFormatUtil;

public class NameStandardizationUtil {
	static Hashtable<String,String> removaprefix = null;
	
	
	// Constructor
	public NameStandardizationUtil() {
		// Do nothing for time being
	}
	
	// This function will be used for spliting name into std format
	// Title, First Name, Middle Name , Last Name, PostFix


	
	public static Hashtable<String,String> getPrefixToRemove() {
		Hashtable<String,String> ht = new Hashtable<String,String>();
		ht.put("SO","Son Of");
		ht.put("DO","Daughther Of");
		ht.put("CO","Care Of");
		ht.put("ATTN","Attention");
		
		return ht;
	}
	
	public static String[]  nameStd (String fullnamestring) {
		// [Title] [First] [Middle] [Last]  [suffix]
		
		if (fullnamestring == null || "".equals(fullnamestring))
			return new String[5];
		
		
		if (removaprefix != null) 
			removaprefix.clear();
		
		
		removaprefix = getPrefixToRemove();
			
		try {
			
			String[] originalName = fullnamestring.trim().split(" |,");
			//System.out.println("Before parsing:"+ Arrays.toString(originalName));
			
			for (int i=0; i < originalName.length; i++) {
				
				originalName[i] = StringCaseFormatUtil.letterString(originalName[i]);
				
				if (i==0) { // first object
					
					String removeValueFor =  removaprefix.get(originalName[i] );
					
					if (removeValueFor != null)
						originalName[i] = ""; // make it empty
				}
				
			}
			
			//System.out.println(Arrays.toString(originalName));
			return removeemptystring(originalName) ;
			
		} catch (Exception e) {
			System.out.println("Exception:"+ e.getLocalizedMessage());
			System.out.println("Could not parse:"+fullnamestring);
			return new String[5] ;
		}
		
		
	}
	
    public static ReportTableModel nameStandardRTM(ReportTableModel rtm, int addrcolindex, String prefixFile, String postfixFile) {
    	
		
		List<String> prefixkey = AsciiParser.pullKeysFromFile(prefixFile);
		List<String> postfixkey = AsciiParser.pullKeysFromFile(postfixFile);
		
//		System.out.println(prefixkey);
//		System.out.println(postfixkey);
	
		int rowc = rtm.getModel().getRowCount();
		
		String[] title = new String[rowc]; String[] fName = new String[rowc];String[] mName = new String[rowc];
		String[] lName = new String[rowc]; String[] nameSuffix = new String[rowc];

		for (int i =0; i < rowc; i++) {
			
			Object o = rtm.getModel().getValueAt(i, addrcolindex);
			if (o != null ) {
				
			 String s[] = NameStandardizationUtil.nameStd(o.toString().toUpperCase());
			 
			 if (s == null || s.length < 1)  // nothing to add
				continue; 
			 
			 int splitlength = s.length;
			 
			 int middlestartIndex=0;
			 int middleendIndex=0;
			 
			 if ( splitlength == 1)  { // only one word to default first name
				 
				 fName[i] = s[0];
				 continue;
			 }
				 
			 if (s[0] != null && "".equals(s[0]) == false) {
				 
//				 System.out.println(prefixkey);
//				 System.out.println(s[0]);
				 
				 if	(prefixkey.contains(s[0]) ) {
					 
					 title[i] = s[0];
					 
					 fName[i] = s[1];
					 
					 middlestartIndex = 2;
				 }
				 else {
					 
					 fName[i] = s[0];
					 
					 middlestartIndex = 1;
				 }
					 
				 
			 }
			 
			 if (splitlength > middlestartIndex)
				 
			 if (s[splitlength -1] != null && "".equals(s[splitlength -1]) == false) {
				 
				 if	(postfixkey.contains(s[splitlength -1]) ) {
					 
					 nameSuffix[i] = s[splitlength -1];
					 
					 middleendIndex = splitlength - 2;
					 
					 if (splitlength > middlestartIndex+1) {
						 
						 lName[i] = s[splitlength -2];
						 
						 middleendIndex = splitlength - 3;
					 }

				 }
				 else {
					 
					 lName[i] = s[splitlength -1];
					 
					 middleendIndex = splitlength - 2;
				 }
				 
			 }
			 
			 for ( int j= middlestartIndex; j <= middleendIndex; j++ ) {
				 if (mName[i] == null)
					 mName[i] = "";
				 
				 mName[i] = mName[i] + " " + s[j];
			 }
			 
			 if (mName[i] != null)
				 mName[i] = mName[i].trim();
			 
//				System.out.println("[Title -"+s[0]+
//						"] [First Name -" +s[1] +
//						"] [Middle Name -" +s[2] +
//						"] [Last Name -" +s[3] +
//						"] [Name Suffix -" +s[4] +
//						);
			}
			
		}
		
		rtm.getModel().addColumn("Title", title);
		rtm.getModel().addColumn("FirstName", fName);
		rtm.getModel().addColumn("MiddleName", mName);
		rtm.getModel().addColumn("LastName", lName);
		rtm.getModel().addColumn("NameSuffix", nameSuffix);
		
		
		return rtm;
    }
    
    private static String[] removeemptystring(String[] stringwithempty ) {
    	
    	ArrayList<String> afterEmpty = new ArrayList<String>();
    	
    	for (String s:stringwithempty) {
    		if (s != null && "".equals(s) == false)
    			afterEmpty.add(s);
    	}
    	String []  newArray =  new String[afterEmpty.size()];
    	return afterEmpty.toArray(newArray);
    	
    }
    
	
} // end of AddressUtilclass
