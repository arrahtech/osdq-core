package org.arrah.framework.util;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for adding functions 
 * related to additional time features. 
 * 
 */

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;


public class TimeUtil2 {
	
	
	public TimeUtil2() {
		
	} // Constructor
	

	
	/* This is a utility function which will take millisecond
	 * and return hashtable with key year,month,date,hour
	 * it will call another function with same signature and 
	 * date as inupt
	 */
	public static Hashtable<String,String> getDateAttributes(long millisec, TimeZone tz, boolean showNumber) {
		Calendar cal = null;
		if (tz == null)
			cal = Calendar.getInstance();
		else
			cal = Calendar.getInstance(tz);
		cal.setLenient(true);
		cal.setTimeInMillis(millisec);
		java.util.Date d= cal.getTime();
		return getDateAttributes(d,tz,showNumber);
	}
	
	/* This is a utility function which will take date
	 * and return hashtable with key year,month,date,hour
	 */
	public static Hashtable<String,String> getDateAttributes(java.util.Date date, TimeZone tz,boolean showNumber) {
		Hashtable<String,String> datev = new Hashtable<String,String> ();
		Calendar cal = null;
		if (date == null || (date instanceof Date) == false)
			return datev;
		if (tz == null)
			cal = Calendar.getInstance();
		else
			cal = Calendar.getInstance(tz);
		
		cal.setLenient(true);
		cal.setTime(date);
		
		try {
			int yr = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			datev.put("year",new Integer(yr).toString());
			datev.put("month",getCanonicalMonth(month,showNumber));
			datev.put("day",getCanonicalDay(day,showNumber));
		} catch (Exception e) {
			datev.put("exception",e.getLocalizedMessage());
			return datev;
		}
		
		return datev;
	}
	
	// This function will return canonical representation of Month
	public static String getCanonicalMonth(int month, boolean isNumber) { 
		switch (month) {
		case Calendar.JANUARY:
			return (isNumber == true)?"1":"January";
		case Calendar.FEBRUARY:
			return  (isNumber == true)?"2":"February";
		case Calendar.MARCH:
			return  (isNumber == true)?"3":"March";
		case Calendar.APRIL:
			return  (isNumber == true)?"4":"April";
		case Calendar.MAY:
			return  (isNumber == true)?"5":"May";
		case Calendar.JUNE:
			return  (isNumber == true)?"6":"June";
		case Calendar.JULY:
			return  (isNumber == true)?"7":"July";
		case Calendar.AUGUST:
			return  (isNumber == true)?"8":"August";
		case Calendar.SEPTEMBER:
			return  (isNumber == true)?"9":"September";
		case Calendar.OCTOBER:
			return  (isNumber == true)?"10":"October";
		case Calendar.NOVEMBER:
			return  (isNumber == true)?"11":"November";
		case Calendar.DECEMBER:
			return  (isNumber == true)?"12":"December";
		case Calendar.UNDECIMBER:
			return  (isNumber == true)?"13":"13Month";
		default:
			return (isNumber == true)?"0":"Undefined";
		}
	}
	
	// This function will return canonical representation of Day
	public static String getCanonicalDay(int day, boolean isNumber) {
		switch (day) {
		case Calendar.SUNDAY:
			return (isNumber == true)?"1":"Sunday";
		case Calendar.MONDAY:
			return (isNumber == true)?"2":"Monday";
		case Calendar.TUESDAY:
			return (isNumber == true)?"3":"Tuesday";
		case Calendar.WEDNESDAY:
			return (isNumber == true)?"4":"Wednesday";
		case Calendar.THURSDAY:
			return (isNumber == true)?"5":"Thursday";
		case Calendar.FRIDAY:
			return (isNumber == true)?"6":"Friday";
		case Calendar.SATURDAY:
			return (isNumber == true)?"7":"Saturday";
		default:
			return (isNumber == true)?"0":"Undefined";
		}
	}
	
	
} // End of TimeUtil2
