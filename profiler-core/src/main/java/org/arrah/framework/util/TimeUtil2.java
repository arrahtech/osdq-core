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
			int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			int ampm = cal.get(Calendar.AM_PM);
					
			datev.put("year",new Integer(yr).toString());
			datev.put("month",getCanonicalMonth(month,showNumber));
			datev.put("day",getCanonicalDay(day,showNumber));
			datev.put("date",new Integer(dayofmonth).toString());
			datev.put("hour",new Integer(hour).toString());
			datev.put("minute",new Integer(min).toString());
			datev.put("second",new Integer(sec).toString());
			if (ampm == Calendar.AM)
				datev.put("ampm","am");
			else
				datev.put("ampm","pm");
			
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
	
	// This function will provide a boolean output to a date/long column
	// and say if it belongs to that group or not
	public static boolean isInGroup (Hashtable<String,String> start,Hashtable<String,String> end, Hashtable<String,String> tobeValid) {
		
		if (tobeValid == null || start == null || end == null ) return false; // nothing to match
		
		 // if year matches
		String startv = start.get("year");
		String endv = start.get("year");
		String validv = tobeValid.get("year");
		if (validv != null) {
			if (startv == null || endv == null)
				return false;
			try {
				if ( (Integer.parseInt(validv) < Integer.parseInt(startv)) ||
					(Integer.parseInt(validv) > Integer.parseInt(endv)) )
					return false;
			} catch (Exception r) {
				return false;
			}
		}
		
		// if month matches no canonical month
		startv = start.get("month");
		endv = start.get("month");
		validv = tobeValid.get("month");
		if (validv != null) {
			if (startv == null || endv == null)
				return false;
			try {
				if ( (Integer.parseInt(validv) < Integer.parseInt(startv)) ||
					(Integer.parseInt(validv) > Integer.parseInt(endv)) )
					return false;
			} catch (Exception r) {
				return false;
			}
		}
		
		// if day matches no canonical day
		startv = start.get("day");
		endv = start.get("day");
		validv = tobeValid.get("day");
		if (validv != null) {
			if (startv == null || endv == null)
				return false;
			try {
				if ( (Integer.parseInt(validv) < Integer.parseInt(startv)) ||
					(Integer.parseInt(validv) > Integer.parseInt(endv)) )
					return false;
			} catch (Exception r) {
				return false;
			}
		}
		
		// if date matches
		startv = start.get("date");
		endv = start.get("date");
		validv = tobeValid.get("date");
		if (validv != null) {
			if (startv == null || endv == null)
				return false;
			try {
				if ( (Integer.parseInt(validv) < Integer.parseInt(startv)) ||
					(Integer.parseInt(validv) > Integer.parseInt(endv)) )
					return false;
			} catch (Exception r) {
				return false;
			}
		}
		
		// if hour matches
		startv = start.get("hour");
		endv = start.get("hour");
		validv = tobeValid.get("hour");
		if (validv != null) {
			if (startv == null || endv == null)
				return false;
			try {
				if ( (Integer.parseInt(validv) < Integer.parseInt(startv)) ||
					(Integer.parseInt(validv) > Integer.parseInt(endv)) )
					return false;
			} catch (Exception r) {
				return false;
			}
		}
		
		// if minute matches
		startv = start.get("minute");
		endv = start.get("minute");
		validv = tobeValid.get("minute");
		if (validv != null) {
			if (startv == null || endv == null)
				return false;
			try {
				if ( (Integer.parseInt(validv) < Integer.parseInt(startv)) ||
					(Integer.parseInt(validv) > Integer.parseInt(endv)) )
					return false;
			} catch (Exception r) {
				return false;
			}
		}
		
		// if seconds matches
		startv = start.get("second");
		endv = start.get("second");
		validv = tobeValid.get("second");
		if (validv != null) {
			if (startv == null || endv == null)
				return false;
			try {
				if ( (Integer.parseInt(validv) < Integer.parseInt(startv)) ||
					(Integer.parseInt(validv) > Integer.parseInt(endv)) )
					return false;
			} catch (Exception r) {
				return false;
			}
		}
		
		return true;
		
	}
	
} // End of TimeUtil2
