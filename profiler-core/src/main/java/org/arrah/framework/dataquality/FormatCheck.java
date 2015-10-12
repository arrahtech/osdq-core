package org.arrah.framework.dataquality;

/***************************************************
 *     Copyright to Vivek Kumar Singh 2013         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                       	   *
 *                                                 *
 **************************************************/

/*
 * This class is used for creating and validating
 * number, date, phone and string formats
 *
 */

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.MaskFormatter;

public class FormatCheck {
	// Constructor
	public FormatCheck() {
		// Do nothing

	}

	public static Double parseNumber(Object value, Object[] pattern) {
		Double d = null;
		if (value instanceof Double)
			return d = (Double) value;
		DecimalFormat format = new DecimalFormat();

		for (int i = 0; i < pattern.length; i++) {
			try {
				format.applyPattern(pattern[i].toString());
				d = new Double(format.parse(value.toString()).doubleValue());
				if (d != null)
					break;
			} catch (Exception e) {
				continue;
			}
		} // End of for loop
		return d;
	}

	public static Date parseDate(Object value, Object[] pattern) {
		Date d = null;
		if (value instanceof Date)
			return d = (Date) value;

		SimpleDateFormat format = new SimpleDateFormat();

		for (int i = 0; i < pattern.length; i++) {
			try {
				format.applyPattern(pattern[i].toString());
				d = format.parse(value.toString(), new ParsePosition(0));
				if (d != null)
					break;
			} catch (Exception e) {
				continue;
			}
		} // End of for loop
		return d;
	}

	public static Object parseString(String value, Object[] pattern) {
		MaskFormatter format = new MaskFormatter();
		format.setValueContainsLiteralCharacters(false);
		Object d = null;

		for (int i = 0; i < pattern.length; i++) {
			try {
				format.setMask(pattern[i].toString());
				d = format.stringToValue(value);
				if (d != null)
					break;
			} catch (Exception e) {
				continue;
			}
		} // End of for loop
		return d;
	}

	/* This function will return a mask formatted string */
	public static StringBuffer toFormat(String value, String mask) {
		if (value == null)
			return null;

		StringBuffer output = new StringBuffer();
		int maskIndex = 0; // for mask indexing
		int i = 0; // for value Indexing

		while ((maskIndex < mask.length()) && (i < value.length())) {
			char c_m = mask.charAt(maskIndex);
			if (c_m == '#' && Character.isDigit(value.charAt(i)))
				output.append(value.charAt(i));
			else if (c_m == 'U' && Character.isLetter(value.charAt(i)))
				output.append(Character.toUpperCase(value.charAt(i)));
			else if (c_m == 'L' && Character.isLetter(value.charAt(i)))
				output.append(Character.toLowerCase(value.charAt(i)));
			else if (c_m == '?' && Character.isLetter(value.charAt(i)))
				output.append(value.charAt(i));
			else if (c_m == 'A'
					&& (Character.isLetter(value.charAt(i)) || Character
							.isDigit(value.charAt(i))))
				output.append(value.charAt(i));
			else if (c_m == '*')
				output.append(value.charAt(i));
			else if (c_m == 'H'
					&& (Character.isDigit(value.charAt(i))
							|| value.charAt(i) == 'a' || value.charAt(i) == 'A'
							|| value.charAt(i) == 'b' || value.charAt(i) == 'B'
							|| value.charAt(i) == 'c' || value.charAt(i) == 'C'
							|| value.charAt(i) == 'd' || value.charAt(i) == 'D'
							|| value.charAt(i) == 'e' || value.charAt(i) == 'E'
							|| value.charAt(i) == 'f' || value.charAt(i) == 'F'))
				output.append(value.charAt(i));
			else if (c_m == '\'' && (value.length() > (i + 1))) {
				i++;
				maskIndex++;
				output.append(value.charAt(i));
			} else {
				output.append(mask.charAt(maskIndex));
				i--; // reduce the value index so that it does not add it
			}
			maskIndex++;
			i++;
		}
		return output;
	}

	/* This function will return a Phone formatted string */
	public static StringBuffer phoneFormat(String value, String mask) {
		if (value == null)
			return null;

		StringBuffer output = new StringBuffer();
		int maskIndex = mask.length() - 1; // for mask indexing
		int i = value.length() - 1; // for value Indexing

		// Code is added for converting letter to number as per phone format protocol
		while ((maskIndex >= 0) && (i >= 0)) {
			char c_m = mask.charAt(maskIndex);
			
			if (c_m == '#' && Character.isDigit(value.charAt(i)))
				output.append(value.charAt(i));
			
			else if (c_m == '#' && Character.isLetter(value.charAt(i))) // Phone Letter to number
				output.append(charIntPhoneMap(value.charAt(i)));
			
			else if (c_m == 'U' && Character.isLetter(value.charAt(i)))
				output.append(Character.toUpperCase(value.charAt(i)));
			
			else if (c_m == 'U' && Character.isDigit(value.charAt(i))) // Phone Number to upper case
				output.append(intUpperCharPhoneMap(value.charAt(i)));
			
			else if (c_m == 'L' && Character.isLetter(value.charAt(i)))
				output.append(Character.toLowerCase(value.charAt(i)));
			
			else if (c_m == 'L' && Character.isDigit(value.charAt(i)))  // Phone Number to lower case
				output.append(intLowerCharPhoneMap(value.charAt(i)));
			
			else if (c_m == '?' && Character.isLetter(value.charAt(i)))
				output.append(value.charAt(i));
			
			else if (c_m == 'A' && (Character.isLetter(value.charAt(i)) || Character.isDigit(value.charAt(i))))
				output.append(value.charAt(i));
			
			else if (c_m == '*')
				output.append(value.charAt(i));
			else if (c_m == 'H'
					&& (Character.isDigit(value.charAt(i))
							|| value.charAt(i) == 'a' || value.charAt(i) == 'A'
							|| value.charAt(i) == 'b' || value.charAt(i) == 'B'
							|| value.charAt(i) == 'c' || value.charAt(i) == 'C'
							|| value.charAt(i) == 'd' || value.charAt(i) == 'D'
							|| value.charAt(i) == 'e' || value.charAt(i) == 'E'
							|| value.charAt(i) == 'f' || value.charAt(i) == 'F'))
				output.append(value.charAt(i));
			else {
				output.append(mask.charAt(maskIndex));
				i++; // increase value index so that it does not add it
			}
			maskIndex--;
			i--;
		}
		if (maskIndex == -1)
			return output.reverse();

		// Do default padding if not enough value - 0 for phone
		while (maskIndex >= 0) {
			char c_m = mask.charAt(maskIndex);
			if (c_m == '#' || c_m == 'U' || c_m == 'L' || c_m == '?'
					|| c_m == 'A' || c_m == '*' || c_m == 'H')
				output.append('0');
			else
				output.append(c_m);
			maskIndex--;
		}

		return output.reverse();
	}

	public static Number validateNumber(String format, String number) {
		DecimalFormat form = null;
		Number v = null;
		try {
			form = new DecimalFormat(format.trim());
			v = form.parse(number.trim());
		} catch (Exception p_e) {
			System.out.println("Format Error:" + p_e.getMessage());
			return (Number) null;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse Number");
			return v;
		}
		return v;
	}

	public static Date validateDate(String format, String date) {
		SimpleDateFormat form = null;
		Date v = null;
		try {
			form = new SimpleDateFormat(format.trim());
			v = form.parse(date.trim());
		} catch (Exception p_e) {
			System.out.println("Format Error:" + p_e.getMessage());
			return (Date) null;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse Date");
			return v;
		}
		return v;
	}

	public static Object validateString(String format, String str) {
		MaskFormatter form = null;
		Object v = null;
		try {
			form = new MaskFormatter(format.trim());
			v = form.stringToValue(str.trim());
		} catch (Exception p_e) {
			System.out.println("Format Error:" + p_e.getMessage());
			return v;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse String");
			return v;
		}
		return v;
	}

	public static Object validatePhone(String format, String phone) {
		MaskFormatter form = null;
		Object v = null;
		try {
			form = new MaskFormatter(format.trim());
			v = form.stringToValue(phone.trim());
		} catch (Exception p_e) {
			System.out.println("Format Error:" + p_e.getMessage());
			return v;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse Phone");
			return v;
		}
		return v;
	}
	
	public static char charIntPhoneMap (char letter) {
		char uplet = Character.toUpperCase(letter);
		switch (uplet) {
		
		case 'A': case 'B': case 'C':
			return '2';
		case 'D':case 'E':case 'F':
			return '3'; 
		case 'G': case 'H': case 'I':
			return '4'; 
		case 'J': case 'K': case 'L':
			return '5';
		case 'M': case 'N': case 'O':
			return '6';
		case 'P': case 'Q': case 'R': case 'S':
			return '7';
		case 'T': case 'U': case 'V':
			return '8';
		case 'W': case 'X': case 'Y': case 'Z':
			return '9';	
		default: 
			return '0';  // default value
		}	
	}
	
	public static char intUpperCharPhoneMap (char number) {
		switch (number) {
		case '2':
			return 'A';
		case '3':
			return 'D';
		case '4':
			return 'G';
		case '5':
			return 'J';
		case '6':
			return 'M';
		case '7':
			return 'P';
		case '8':
			return 'T';
		case '9':
			return 'W';
		default:
			return '0';  // default value
		}
		
	}
	public static char intLowerCharPhoneMap (char number) {
		switch (number) {
		case '2':
			return 'a';
		case '3':
			return 'd';
		case '4':
			return 'g';
		case '5':
			return 'j';
		case '6':
			return 'm';
		case '7':
			return 'p';
		case '8':
			return 't';
		case '9':
			return 'w';
		default:
			return '0'; // default value
		}
		
	}

	
	
}
