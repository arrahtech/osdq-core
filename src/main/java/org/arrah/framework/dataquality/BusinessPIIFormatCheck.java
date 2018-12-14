package org.arrah.framework.dataquality;


import java.util.HashMap;

import org.arrah.framework.ndtable.ReportTableModel;


/***************************************************
 *     Copyright to Vivek Kumar Singh 2018         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                       	   *
 *                                                 *
 **************************************************/

/*
 * This file is used for validation business objects
 * like credit card, Aashar, SSN , Phone number etc
 *
 */

public class BusinessPIIFormatCheck {
	
	// Default Constructor
	public BusinessPIIFormatCheck() {
		
	}
	
	
	public ReportTableModel isCCmatch (ReportTableModel rtm, int colI) {
		PIIValidator pii = new PIIValidator();
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsCreditCardValid") == -1 )
			rtm.addColumn("IsCreditCardValid"); 
		if ( rtm.getModel().findColumn("CreditCardValidDescription") == -1 )
			rtm.addColumn("CreditCardValidDescription");
		
		int validI = rtm.getModel().findColumn("IsCreditCardValid"); 
		int descI = rtm.getModel().findColumn("CreditCardValidDescription");
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString())) continue;
			Boolean isValid = pii.isCreditCard(o.toString().replaceAll("\\s+", "").trim());
			String des = pii.getErrdef();
			rtm.getModel().setValueAt(isValid.toString(), i, validI);
			rtm.getModel().setValueAt(des, i, descI);
		}
		
		return rtm;
		
	}
	
	public ReportTableModel isPANmatch (ReportTableModel rtm, int colI) {
		PANValidator panV = new PANValidator();
		
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsPANCardValid") == -1 )
			rtm.addColumn("IsPANCardValid"); 
		if ( rtm.getModel().findColumn("PANCardValidDescription") == -1 )
			rtm.addColumn("PANCardValidDescription");
		
		int validI = rtm.getModel().findColumn("IsPANCardValid"); 
		int descI = rtm.getModel().findColumn("PANCardValidDescription");
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString())) continue;
			HashMap<String, String> responseMap = panV.validate(o.toString().replaceAll("\\s+", "").trim());
			rtm.getModel().setValueAt(responseMap.get("isValid"), i, validI);
			rtm.getModel().setValueAt(responseMap.get("entityDesc"), i, descI);
		}
		
		return rtm;
		
	}
	
	public ReportTableModel isGSTINmatch (ReportTableModel rtm, int colI) {
		GSTINValidator gstV = new GSTINValidator();
		
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsGSTINValid") == -1 )
			rtm.addColumn("IsGSTINValid"); 
		if ( rtm.getModel().findColumn("GSTINValidDescription") == -1 )
			rtm.addColumn("GSTINValidDescription");
		
		int validI = rtm.getModel().findColumn("IsGSTINValid"); 
		int descI = rtm.getModel().findColumn("GSTINValidDescription");
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString())) continue;
			HashMap<String, String> responseMap = gstV.validate(o.toString().replaceAll("\\s+", "").trim());
			rtm.getModel().setValueAt(responseMap.get("isValid"), i, validI);
			rtm.getModel().setValueAt(responseMap.get("entityDesc"), i, descI);
		}
		
		return rtm;
		
	}
	
	public ReportTableModel isAADHARmatch (ReportTableModel rtm, int colI) {
		AadharValidator gstV = new AadharValidator();
		
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsAADHARValid") == -1 )
			rtm.addColumn("IsAADHARValid"); 
		if ( rtm.getModel().findColumn("AADHARValidDescription") == -1 )
			rtm.addColumn("AADHARValidDescription");
		
		int validI = rtm.getModel().findColumn("IsAADHARValid"); 
		int descI = rtm.getModel().findColumn("AADHARValidDescription");
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString())) continue;
			HashMap<String, String> responseMap = gstV.validate(o.toString().replaceAll("\\s+", "").trim());
			rtm.getModel().setValueAt(responseMap.get("isValid"), i, validI);
			rtm.getModel().setValueAt(responseMap.get("entityDesc"), i, descI);
		}
		
		return rtm;
		
	}
	
	public ReportTableModel isMobiematch (ReportTableModel rtm, int colI) {
		MNValidator mobileV = new MNValidator();
		
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsMobileValid") == -1 )
			rtm.addColumn("IsMobileValid"); 
		if ( rtm.getModel().findColumn("MobileValidDescription") == -1 )
			rtm.addColumn("MobileValidDescription");
		
		int validI = rtm.getModel().findColumn("IsMobileValid"); 
		int descI = rtm.getModel().findColumn("MobileValidDescription");
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString())) continue;
			HashMap<String, String> responseMap = mobileV.validate(o.toString().replaceAll("\\s+", "").trim());
			rtm.getModel().setValueAt(responseMap.get("isValid"), i, validI);
			rtm.getModel().setValueAt(responseMap.get("entityDesc"), i, descI);
		}
		
		return rtm;
		
	}
	
	
}