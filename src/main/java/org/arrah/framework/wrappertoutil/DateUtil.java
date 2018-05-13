package org.arrah.framework.wrappertoutil;
/**
 * @author vivek singh
 *
 */

import java.util.TimeZone;

import org.arrah.framework.dataquality.FormatCheck;
import org.arrah.framework.util.TimeUtil;

public class DateUtil {
	
	/**
	 * @param Date to be converted into long
	 * @return long value of date since Epoch in default timezone
	 */
	public static long dateToEpoch (java.util.Date d) {
			return TimeUtil.dateIntoSecond(d);
	}
	
	/**
	 * @param Date to be converted into long
	 * @param timezone - String TimeZone 
	 * @return long value of date since Epoch
	 */
	public static long dateToEpoch (java.util.Date d, String timezone) {
		TimeZone tz = TimeZone.getTimeZone(timezone);
			return TimeUtil.dateIntoSecond(d,tz);
	}
	

	/**
	 * @param millsec - time since epoch
	 * @return Date of default timezone
	 */
	public static java.util.Date epochToDate (long millsec) {
		return TimeUtil.secondIntoDate(millsec);
	}
	
	/**
	 * @param millsec - time since epoch
	 * @param timezone - String TimeZone 
	 * @return Date of default timezone
	 */
	public static java.util.Date epochToDate (long millsec, String timezone) {
		return TimeUtil.secondIntoDate(millsec);
	}
	
	/**
	 * @param a Date
	 * @param b Date
	 * @return difference in milli seconds
	 */
	public static long diffInMilliSec (java.util.Date a, java.util.Date b) {
		return TimeUtil.diffIntoMilliSecond(a,b);
	}
	
	public static String convertToFormat (java.util.Date a, String format) {
		return FormatCheck.toFormatDate(a,format);
	}
	
	public static void main(String [] args) {
		
			System.out.println(convertToFormat(new java.util.Date(),"dd--MM--YYY"));
		
	}

}
