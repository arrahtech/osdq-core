package org.arrah.framework.datagen;

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
 * This file is used for adding functions related to 
 * time. It will be used for time grouping.
 * 
 */

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;


public class TimeUtil {

	private static Calendar cal = Calendar.getInstance();
	private static String OTHER = "UNDEFINED"; // Null or Undefined Key
	private static Vector<Integer> monthV = new Vector<Integer>();
	
	
	public TimeUtil() {
		
	} // Constructor
	
	/* This is older function which is used in file report */
	public static String timeKey(Date date, int timeH) {
		if (date == null || (date instanceof Date) == false)
			return null;

		cal.setTime(date);
		cal.setLenient(true);
		int yr = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_WEEK);

		switch (timeH) {
		case 0:
			return Integer.toString(yr);
		case 1:
			return Integer.toString(yr) + getQuarter(month);
		case 2:
			return getQuarter(month);
		case 3:
			return Integer.toString(yr) + getMonthCode(month);
		case 4:
			return getMonthCode(month);
		case 5:
			return Integer.toString(day);
		default:
			return date.toString();
		}
	}
	/* This function will be used by grouping algo to 
	 * group date/ time
	 */
	public static String timeValue(Date date, int timeH, int anchorTime) {
		if (date == null || (date instanceof Date) == false)
			return null;

		cal.setTime(date);
		cal.setLenient(true);
		int yr = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		int hour = cal.get(Calendar.HOUR_OF_DAY);

		switch (timeH) {
		case 0: // Year
			return Integer.toString(yr);
		case 1: // Year - Qrt
			return Integer.toString(yr) +" - "+ getQuarter(month,anchorTime); // Starting month
		case 2: // Qrt
			return getQuarter(month,anchorTime);
		case 3: // Year - Month
			return Integer.toString(yr) +" - "+ getMonthName(getMonthCode(month));
		case 4: // Month
			return getMonthName(getMonthCode(month));
		case 5: // Day
			return getDayName(Integer.toString(day));
		case 6: // Hour
			return ampmDateForm(hour,anchorTime); // will use anchor Time for am pm or 24 hrs format
		case 7: // Week of Year
			cal.setFirstDayOfWeek(anchorTime); // Starting day
			int weekY = cal.get(Calendar.WEEK_OF_YEAR);
			return Integer.toString(weekY);
		default:
			return date.toString();
		}
	}

	public static String getMonthCode(int month) { // Good for Sorting
		switch (month) {
		case Calendar.JANUARY:
			return "A";
		case Calendar.FEBRUARY:
			return "B";
		case Calendar.MARCH:
			return "C";
		case Calendar.APRIL:
			return "D";
		case Calendar.MAY:
			return "E";
		case Calendar.JUNE:
			return "F";
		case Calendar.JULY:
			return "G";
		case Calendar.AUGUST:
			return "H";
		case Calendar.SEPTEMBER:
			return "I";
		case Calendar.OCTOBER:
			return "J";
		case Calendar.NOVEMBER:
			return "K";
		case Calendar.DECEMBER:
			return "L";
		case Calendar.UNDECIMBER:
		default:
			return OTHER;
		}
	}

	public static String getMonthName(String code) { // Good for Sorting
		if (code.equals("A"))
			return "Jan";
		if (code.equals("B"))
			return "Feb";
		if (code.equals("C"))
			return "Mar";
		if (code.equals("D"))
			return "Apr";
		if (code.equals("E"))
			return "May";
		if (code.equals("F"))
			return "Jun";
		if (code.equals("G"))
			return "Jul";
		if (code.equals("H"))
			return "Aug";
		if (code.equals("I"))
			return "Sep";
		if (code.equals("J"))
			return "Oct";
		if (code.equals("K"))
			return "Nov";
		if (code.equals("L"))
			return "Dec";
		return OTHER;
	}

	public static String getDayName(String code) { // Good for Sorting
		int day = Integer.parseInt(code);
		switch (day) {
		case Calendar.SUNDAY:
			return "Sun";
		case Calendar.MONDAY:
			return "Mon";
		case Calendar.TUESDAY:
			return "Tue";
		case Calendar.WEDNESDAY:
			return "Wed";
		case Calendar.THURSDAY:
			return "Thu";
		case Calendar.FRIDAY:
			return "Fri";
		case Calendar.SATURDAY:
			return "Sat";
		default:
			return OTHER;
		}
	}

	/* This is standard qrt method starting from Jan
	 * 
	 */
	public static String getQuarter(int month) {
		switch (month) {
		case Calendar.JANUARY:
		case Calendar.FEBRUARY:
		case Calendar.MARCH:
			return "Q1";
		case Calendar.APRIL:
		case Calendar.MAY:
		case Calendar.JUNE:
			return "Q2";
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.SEPTEMBER:
			return "Q3";
		case Calendar.OCTOBER:
		case Calendar.NOVEMBER:
		case Calendar.DECEMBER:
			return "Q4";
		case Calendar.UNDECIMBER:
		default:
			return OTHER;
		}
	}
	
	/* This function will take qrts start month then loop 
	 * 3 months from there
	 */
	public static String getQuarter(int month, int startmonth ) {
		fillMonthVec();
		int smonthI = monthV.indexOf(startmonth);
		
		if ( smonthI < 0 || smonthI > 11 )
			return OTHER;
		
		int monthI = monthV.indexOf(month);
		
		if ( monthI < 0 || monthI > 11 )
			return OTHER;
		
		int diff = circularDiff(monthI,smonthI,12); // For month
		
		switch (diff) {
		case 0:case 1:case 2:
			return "Q1";
		case 3:case 4:case 5:
			return "Q2";
		case 6:case 7:case 8:
			return "Q3";
		case 9:case 10:case 11:
			return "Q4";
		default:
			return OTHER;
		}

	}
	
	/* Call Once to fill monthVector */
	private static void fillMonthVec() {
		if (monthV.size() < 12) { // if it is not filled
		monthV.add(Calendar.JANUARY, 0);monthV.add(Calendar.FEBRUARY, 1);monthV.add(Calendar.MARCH, 2);
		monthV.add(Calendar.APRIL, 3);monthV.add(Calendar.MAY, 4);monthV.add(Calendar.JUNE, 5);
		monthV.add(Calendar.JULY, 6);monthV.add(Calendar.AUGUST, 7);monthV.add(Calendar.SEPTEMBER, 8);
		monthV.add(Calendar.OCTOBER, 9);monthV.add(Calendar.NOVEMBER, 10);monthV.add(Calendar.DECEMBER, 11);
		}
		
	}
	
	/* Finding circular difference  - for month diagonalLen will be 12
	 * week it will be 7*/
	
	public static int circularDiff(int index, int anchor, int diagonalLen) {
		int diff = index - anchor;
		if ( diff >= 0 && diff <= diagonalLen)
			return diff;
		else // it is negative value
			return diagonalLen+diff;
	}
	
	public static String ampmDateForm(int hour, int ampmformat) {
		if (ampmformat < 1 ) { // select AM PM format
			if (hour < 12 )
				return Integer.toString(hour)+ " AM";
			else if (hour == 12 || hour < 24)
				return Integer.toString(hour)+ " PM"; // mid day 12 pm
			else if ( hour < 24)
				return Integer.toString(hour -12)+ " PM";
			else if ( hour == 24)
				return Integer.toString(hour -12)+ " AM"; // mid nigh 12 am
			else
				return Integer.toString(hour);
		} else // 24 hrs format
			return Integer.toString(hour);
	}
	
	/* This function will return java.Calendar values of month  name */
	public static int monthCalValue(String monthName) {
		if (monthName.compareToIgnoreCase("Jan") == 0 || monthName.compareToIgnoreCase("January") ==0)
			return Calendar.JANUARY;
		if (monthName.compareToIgnoreCase("Feb") == 0 || monthName.compareToIgnoreCase("February") ==0)
			return Calendar.FEBRUARY;
		if (monthName.compareToIgnoreCase("Mar") == 0 || monthName.compareToIgnoreCase("March") ==0)
			return Calendar.MARCH;
		if (monthName.compareToIgnoreCase("Apr") == 0 || monthName.compareToIgnoreCase("April") ==0)
			return Calendar.APRIL;
		if (monthName.compareToIgnoreCase("May") == 0 || monthName.compareToIgnoreCase("May") ==0)
			return Calendar.MAY;
		if (monthName.compareToIgnoreCase("Jun") == 0 || monthName.compareToIgnoreCase("June") ==0)
			return Calendar.JUNE;
		if (monthName.compareToIgnoreCase("Jul") == 0 || monthName.compareToIgnoreCase("July") ==0)
			return Calendar.JULY;
		if (monthName.compareToIgnoreCase("Aug") == 0 || monthName.compareToIgnoreCase("August") ==0)
			return Calendar.AUGUST;
		if (monthName.compareToIgnoreCase("Sep") == 0 || monthName.compareToIgnoreCase("September") ==0)
			return Calendar.SEPTEMBER;
		if (monthName.compareToIgnoreCase("Oct") == 0 || monthName.compareToIgnoreCase("October") ==0)
			return Calendar.OCTOBER;
		if (monthName.compareToIgnoreCase("Nov") == 0 || monthName.compareToIgnoreCase("November") ==0)
			return Calendar.NOVEMBER;
		if (monthName.compareToIgnoreCase("Dec") == 0 || monthName.compareToIgnoreCase("December") ==0)
			return Calendar.DECEMBER;
				
				return Calendar.UNDECIMBER;
	}
	
	/* This function will return java.Calendar values of weekday  name */
	public static int weekCalValue(String weekName) {
		if (weekName.compareToIgnoreCase("Sun") == 0 || weekName.compareToIgnoreCase("Sunday") ==0)
			return Calendar.SUNDAY;
		if (weekName.compareToIgnoreCase("Mon") == 0 || weekName.compareToIgnoreCase("Monday") ==0)
			return Calendar.MONDAY;
		if (weekName.compareToIgnoreCase("Tue") == 0 || weekName.compareToIgnoreCase("Tuesday") ==0)
			return Calendar.TUESDAY;
		if (weekName.compareToIgnoreCase("Wed") == 0 || weekName.compareToIgnoreCase("Wednesday") ==0)	
			return Calendar.WEDNESDAY;
		if (weekName.compareToIgnoreCase("Thu") == 0 || weekName.compareToIgnoreCase("Thursday") ==0)
			return Calendar.THURSDAY;
		if (weekName.compareToIgnoreCase("Fri") == 0 || weekName.compareToIgnoreCase("Friday") ==0)
			return Calendar.FRIDAY;
		if (weekName.compareToIgnoreCase("Sat") == 0 || weekName.compareToIgnoreCase("Saturday") ==0)
			return Calendar.SATURDAY;
		
		return -1;
		
	}
	
	public static TimeSeries getForecastData(TimeSeries observationData, int forcastperc) {
		TimeSeries fcdataset = new TimeSeries("Time Series Forecast");
		if (observationData.isEmpty() == true)
			return fcdataset;
		int size = observationData.getItemCount();
		long fcsize = size + Math.round( (double)size * ((double)forcastperc/100)); // new data set
		
		try {
			fcdataset = observationData.createCopy(0,size-1);
			for (int i=size; i < fcsize; i++) {
				RegularTimePeriod nexttp = fcdataset.getNextTimePeriod();
				fcdataset.add(nexttp,0.00D);
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.getLocalizedMessage());
			System.out.println("Could not create forecasted data sample");
			return fcdataset;
		}
		
		return fcdataset;
		
	}
	
} // End of TimeUtil
