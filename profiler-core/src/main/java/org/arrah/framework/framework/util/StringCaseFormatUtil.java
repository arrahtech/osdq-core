package org.arrah.framework.util;

import java.util.Vector;

/***********************************************
 *     Copyright to Arrah Technology 2013      *
 *     http://www.arrahtec.org                 *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used creating case format Uppercase, Lowercase, Titlecase and
 * Sentence case
 */

public class StringCaseFormatUtil {

	public static String toUpperCase(String s) {
		return s.toUpperCase();
	}

	public static String toLowerCase(String s) {
		return s.toLowerCase();
	}

	public static String toTitleCase(String s) {
		s = s.toLowerCase();
		int strl = s.length();
		char[] holder = new char[strl];
		boolean titleActive = true;

		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (titleActive == true || i == 0) {
				nextC = Character.toTitleCase(nextC);
				titleActive = false;
			}
			if (Character.isWhitespace(nextC) == true)
				titleActive = true;

			holder[i] = nextC;
			i++;
		}

		return new String(holder);

	}

	public static String toSentenceCase(String s, char endOfLineSym) {
		s = s.toLowerCase();

		int strl = s.length();
		char[] holder = new char[strl];
		boolean sentenceActive = true;

		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (sentenceActive == true || i == 0) {
				if (Character.isLetterOrDigit(nextC) == true) {
					nextC = Character.toUpperCase(nextC);
					sentenceActive = false;
				}
			}
			if (Character.getType(nextC) == Character.LINE_SEPARATOR
					|| Character.getType(nextC) == Character.PARAGRAPH_SEPARATOR
					|| nextC == endOfLineSym)
				sentenceActive = true;

			holder[i] = nextC;
			i++;
		}

		return new String(holder);
	}

	public static boolean isUpperCase(String s) {
		int strl = s.length();
		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (Character.isUpperCase(nextC) == false)
				return false;
			i++;
		}
		return true;
	}

	public static boolean isLowerCase(String s) {
		int strl = s.length();
		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (Character.isLowerCase(nextC) == false)
				return false;
			i++;
		}
		return true;
	}

	public static boolean isTitleCase(String s) {
		int strl = s.length();
		int i = 0;
		boolean titleActive = true;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (titleActive == false) {
				if (Character.isTitleCase(nextC) == true
						|| Character.isUpperCase(nextC) == true)
					return false;
			}
			if (titleActive == true || i == 0) {
				if (Character.isTitleCase(nextC) == false
						&& Character.isUpperCase(nextC) == false)
					return false;
				titleActive = false;
			}
			if (Character.isWhitespace(nextC) == true)
				titleActive = true;
			i++;
		}
		return true;
	}

	public static boolean isSentenceCase(String s, char endOfLineSym) {
		int strl = s.length();
		boolean sentenceActive = true;
		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (sentenceActive == false) {
				if (Character.isLetterOrDigit(nextC) == true)
					if (Character.isUpperCase(nextC) == true)
						return false;
			}

			if (sentenceActive == true || i == 0) {
				if (Character.isLetterOrDigit(nextC) == true) {
					if (Character.isUpperCase(nextC) == false)
						return false;
					sentenceActive = false;
				}
			}
			if (Character.getType(nextC) == Character.LINE_SEPARATOR
					|| Character.getType(nextC) == Character.PARAGRAPH_SEPARATOR
					|| nextC == endOfLineSym)
				sentenceActive = true;
			i++;
		}
		return true;
	}

	/* This is a utility function to split String in two parts only by width/length size */
	public static String[] splitColStringWidth(String value, int len) {
		String[] output= new String[2];
		output[0] = ""; output[1] = "";
		
		if (value == null || "".equals(value) || value.length()<=len )
				return output;
	
		output[0] = value.substring(0,len);output[1] = value.substring(len);
		
		return output ;
	}
	
	/* This is a utility function to split String in two parts only */
	public static String[] splitColString(String value, String regex) {
		String[] output= new String[2];
		output[0] = ""; output[1] = "";
		
		if (value == null || "".equals(value) )
				return output;
	
		return output = value.split(regex,2);
	}
	
	/* This is a utility function to split String in subString*/
	public static String[] splitColSubString(String value, String regex) {
		String[] output = null;
		
		if (value == null || "".equals(value) )
				return output;
	
		return output = value.split(regex);
	}
	
	/* This is a utility function to Remove MetaChar from String */
	public static String removeMetaCharString(String oldString, String skipString) {
		String newString="";
		for (int curIndex = 0; curIndex < oldString.length(); curIndex++ ) {
			 char c = oldString.charAt(curIndex);
			 Character ch = new Character(c);
			 if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
			 newString += ch.toString();
		 }
		return newString;
	}
	// From Start, end and inBetween
	public static String removeMetaCharString(String oldString, String skipString, boolean start,  boolean inBtw, boolean end) {
		String newString="";
		int len =  oldString.length();
		
		{ // Start Block
			char c = oldString.charAt(0); // First char
		 	Character ch = new Character(c);
		 	
			if (start == true) {
			 	if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
					 newString += ch.toString();
			} else 
				newString += ch.toString();
		}
		
		if (len > 1) { // inBet Block
			for (int curIndex = 1; curIndex < len - 1 ; curIndex++ ) {
				 char c = oldString.charAt(curIndex);
				 Character ch = new Character(c);
				 if (inBtw == true ) {	
					 if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
					 newString += ch.toString();
				 } else
					 newString += ch.toString();	 
			}
		}
		
		if ((len > 1)) { // end block
			char c = oldString.charAt(len -1); // last char
		 	Character ch = new Character(c);
			if ( end == true) {
			 	if (Character.isLetterOrDigit(c) || skipString.contains(ch.toString()))
					 newString += ch.toString();
			} else
				 newString += ch.toString();
		}
		
		return newString;
	}
	
	/* This is a utility function to Remove Character from String */
	public static String removeCharacterString(String oldString, String skipString) {
		String newString="";
		for (int curIndex = 0; curIndex < oldString.length(); curIndex++ ) {
			 char c = oldString.charAt(curIndex);
			 Character ch = new Character(c);
			 if ( skipString.contains(ch.toString()) == false)
			 newString += ch.toString();
		 }
		return newString;
	}
	// From Start, end and inBetween
	public static String removeCharacterString(String oldString, String skipString, boolean start,  boolean inBtw, boolean end) {
		String newString="";
		int len =  oldString.length();
		
		for (int curIndex = 0 ; curIndex < len ; curIndex++ ) {
			 char c = oldString.charAt(curIndex);
			 Character ch = new Character(c);
			 
			 if (curIndex == 0) { //Start block
				 if (start == true ) {
					 if ( skipString.contains(ch.toString()) == false)
						 newString += ch.toString();
				 } else 
					 newString += ch.toString();
				 
				 continue; // will work for single byte character
			 }
			 
			 if (curIndex < len -1 ) {
				 if (inBtw == true) { // middle block
					 if ( skipString.contains(ch.toString()) == false)
						 newString += ch.toString();
				 } else 
					 newString += ch.toString();
			 } else {
				 if (end == true) { // end block
					 if ( skipString.contains(ch.toString()) == false)
						 newString += ch.toString();
				 } else 
					 newString += ch.toString();
			 }
			 
		 } // For Loop
		
		return newString;
	}
	
	/* This is a utility function to Remove Character from String */
	public static String replaceString(String fullString, String matchString, String replaceString, boolean fromStart) {
		String newString ="";
		if ( fromStart == true) 
			newString = fullString.replaceFirst(matchString, replaceString);
		 else 
			newString = fullString.replaceAll(matchString, replaceString);
		
		return newString;
	}
	
	/* This is a utility function to send matching character */
	public static int matchString(String first, String second, boolean fromStart) {
		int matchc= 0;
		if (first == null || second == null) return matchc;
		int fc = first.length();
		int sc = second.length();
		
		int commonc = (fc > sc ) ? sc : fc;
		if (fromStart == false) {
			first = new StringBuffer(first).reverse().toString();
			second = new StringBuffer(second).reverse().toString();
		}
			
		for (int i=0; i < commonc; i++) {
			if (first.charAt(i) == second.charAt(i)) 
				matchc++;
			else
				break;
		}
		
		return matchc;
	}
	
	/* This is a utility function to send matching emails 0-1 */
	public static float matchEmail(String first, String second) {
		float matchc= 0.00f;
		if (first == null || second == null) return matchc;
		
		if (first.equalsIgnoreCase(second)) return 1.00f; // Exact match
		
		String firstName = first.split("@",2)[0].toLowerCase();
		String secondName = second.split("@",2)[0].toLowerCase();
			
		if (firstName.equals(secondName)) return 0.9f; // Exact match without domain
		
		int fc = firstName.length();
		int sc = secondName.length();
		
		int commonc = (fc > sc ) ? sc : fc;
		int[] matchedI = new int[commonc]; // keep the matched index
		
		String shortstr,longstr;
		
		if (fc > sc ) {
			longstr = firstName;
			shortstr = secondName;
		} else {
			longstr = secondName;
			shortstr = firstName;
		}
	
		for (int i=0; i < commonc; i++) {
			char c = shortstr.charAt(i);
			int j = shortstr.indexOf(c);
			int startI = 0; // start of indexOf for bigString
			
			while (j < i) { // duplicate record bring to current index
				startI = matchedI[j];
				j = shortstr.indexOf(c,j);
				
			}
			if ( j == i) { // Double match - while loop should create that
				matchedI[i] = longstr.indexOf(c,startI);
			} 
		} // matchedI should be filled by now. Calculate the value now
		matchc = StringCaseFormatUtil.showMatchval(matchedI);
		
		return matchc;
	}
	
	public static float showMatchval(int[] matchedA) {
		float matchval = 0.00f;
		
		// 0.5 weightage to match and 0.4 weightage to max weightage 0.9
		int matchlen = matchedA.length;
		int prevVal = -1;
		int matchC=0, seqC = 0;
		
		for (int i=0; i < matchlen; i++) {
			if (matchedA[i] == -1)  continue ;  // not matched
				matchC++;
				if (matchedA[i] == prevVal + 1  ) // In sequence, first value also matched
					seqC++;
		}
		matchval = ((float)matchC/(float)matchlen)*0.5f + ((float)seqC/(float)matchlen)*0.4f;
				
		return matchval;
	}
	
	// For tokenize text
	public static Vector<String> tokenizeText(String text, String token) {
		if (token == null || text == null || "".equals(text) || "".equals(token))
			return (Vector<String>)null;
		String[] tokenA = text.trim().split(token);
		int i = 0;
		Vector<String> vec = new Vector<String>();
		while (i < tokenA.length)
			vec.add(tokenA[i++]);
		return vec;
	}
	
	// removing extra characters and returning digit
	public static String digitString(String numString) {
		String onlyDigit="";
		if (numString == null  || "".equals(numString) )
			return onlyDigit;
		int strlen = numString.length();
		for (int i=0; i < strlen; i++) {
			char c = numString.charAt(i);
			if (Character.isDigit(c) == true )
				onlyDigit += c;
		}
		return onlyDigit;
	}
}
