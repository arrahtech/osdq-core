package org.arrah.framework.util;

/***********************************************
 * Copyright to Vivek Kumar Singh * * Any part of code or file can be changed, *
 * redistributed, modified with the copyright * information intact * * Author$ :
 * Vivek Singh * *
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

}
