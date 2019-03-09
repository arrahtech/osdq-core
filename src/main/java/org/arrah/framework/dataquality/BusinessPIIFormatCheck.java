package org.arrah.framework.dataquality;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * like credit card, Aadhar, SSN , Phone number, email
 * DoB etc
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
	
	public ReportTableModel isEmailmatch (ReportTableModel rtm, int colI) {
		PIIValidator pii = new PIIValidator();
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsEmailValid") == -1 )
			rtm.addColumn("IsEmailValid"); 
		if ( rtm.getModel().findColumn("EmailValidDescription") == -1 )
			rtm.addColumn("EmailValidDescription");
		
		int validI = rtm.getModel().findColumn("IsEmailValid"); 
		int descI = rtm.getModel().findColumn("EmailValidDescription");
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString())) continue;
			Boolean isValid = pii.isEmail(o.toString().replaceAll("\\s+", "").trim());
			String des = pii.getErrdef();
			rtm.getModel().setValueAt(isValid.toString(), i, validI);
			rtm.getModel().setValueAt(des, i, descI);
		}
		
		return rtm;
		
	}
	
	public ReportTableModel isDoBmatch (ReportTableModel rtm, int colI) {
		PIIValidator pii = new PIIValidator();
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsDoBValid") == -1 )
			rtm.addColumn("IsDoBValid"); 
		if ( rtm.getModel().findColumn("DoBValidDescription") == -1 )
			rtm.addColumn("DoBValidDescription");
		
		int validI = rtm.getModel().findColumn("IsDoBValid"); 
		int descI = rtm.getModel().findColumn("DoBValidDescription");
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString()) ) continue;
			Boolean isValid = false; String des = " Not in Date Format";
			if (o instanceof Date) {
				isValid = pii.isDoB((Date)o);
				des = pii.getErrdef();
			}
			rtm.getModel().setValueAt(isValid.toString(), i, validI);
			rtm.getModel().setValueAt(des, i, descI);
		}
		
		return rtm;
		
	}
	public ReportTableModel isDoBmatch (ReportTableModel rtm, int colI, Date from, Date to) throws ParseException {
		PIIValidator pii = new PIIValidator();
		int rowc = rtm.getModel().getRowCount();
		if ( rtm.getModel().findColumn("IsDoBValid") == -1 )
			rtm.addColumn("IsDoBValid"); 
		if ( rtm.getModel().findColumn("DoBValidDescription") == -1 )
			rtm.addColumn("DoBValidDescription");
		
		int validI = rtm.getModel().findColumn("IsDoBValid"); 
		int descI = rtm.getModel().findColumn("DoBValidDescription");

		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, colI);
			if (o == null || "".equals(o.toString()) ) continue;
			Boolean isValid = false; String des = " Not in Date Format";
			if (o instanceof Date) {
				isValid = pii.isDoB((Date)o,from,to);
				des = pii.getErrdef();
			}
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
	
	public ReportTableModel isPANNamematch (ReportTableModel rtm, int colI, int col2) {
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
			Object n = rtm.getModel().getValueAt(i, col2);
			n = (n == null) ?n="":n;
			if (o == null || "".equals(o.toString())) continue;
			HashMap<String, String> responseMap = panV.validate(o.toString().replaceAll("\\s+", "").trim(),
					n.toString().trim());
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
	
	public ReportTableModel isGSTINNamematch (ReportTableModel rtm, int colI,int col2) {
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
			Object n = rtm.getModel().getValueAt(i, col2);
			n = (n == null) ?n="":n;
			if (o == null || "".equals(o.toString())) continue;
			HashMap<String, String> responseMap = gstV.validate(o.toString().replaceAll("\\s+", "").trim(),
					n.toString().trim());
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