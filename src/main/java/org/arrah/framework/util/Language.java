package org.arrah.framework.util;

/***********************************************
 * Copyright to Vivek Kumar Singh * * Any part of code or file can be changed, *
 * redistributed, modified with the copyright * information intact * * Author$ :
 * Vivek Singh * *
 ***********************************************/

/*
 * This file is used to fetch language types name from unicode and codepoint
 * information.
 */

public class Language {
	public static final int LATIN_BASIC = 0;
	public static final int GREEK = 1;
	public static final int HEBREW = 2;
	public static final int ARABIC = 3;
	public static final int DEVANAGIRI = 4;
	public static final int TAMIL = 5;
	public static final int KANNADA = 6;
	public static final int THAI = 7;
	public static final int HANGUL = 8;
	public static final int HIRAGANA = 9;
	public static final int KATAKANA = 10;
	public static final int BOPOMOFO = 11;
	public static final int KANBUN = 12;

	public static final int LATIN_BASIC_MIN = 0x0000;
	public static final int LATIN_BASIC_MAX = 0x007F;
	public static final int GREEK_MIN = 0x0370;
	public static final int GREEK_MAX = 0x03FF;
	public static final int HEBREW_MIN = 0x0590;
	public static final int HEBREW_MAX = 0x05FF;
	public static final int ARABIC_MIN = 0x0600;
	public static final int ARABIC_MAX = 0x06FF;
	public static final int DEVANAGIRI_MIN = 0x0900;
	public static final int DEVANAGIRI_MAX = 0x097F;
	public static final int TAMIL_MIN = 0x0B80;
	public static final int TAMIL_MAX = 0x0BFF;
	public static final int KANNADA_MIN = 0x0C80;
	public static final int KANNADA_MAX = 0x0CFF;
	public static final int THAI_MIN = 0x0E00;
	public static final int THAI_MAX = 0x0e7F;
	public static final int HANGUL_MIN = 0x1100;
	public static final int HANGUL_MAX = 0x11FF;
	public static final int HIRAGANA_MIN = 0x3040;
	public static final int HIRAGANA_MAX = 0x309F;
	public static final int KATAKANA_MIN = 0x30A0;
	public static final int KATAKANA_MAX = 0x30FF;
	public static final int BOPOMOFO_MIN = 0x3100;
	public static final int BOPOMOFO_MAX = 0x312F;
	public static final int KANBUN_MIN = 0x3190;
	public static final int KANBUN_MAX = 0x319F;

}
