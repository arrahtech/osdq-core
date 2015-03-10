package org.arrah.framework.ndtable;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is used for creating 
 * random numbers, string and date
 * to  populate a column.
 * 
 */

import java.util.Date;
import java.util.Random;
import java.util.Vector;

import org.arrah.framework.util.Language;

public class RandomColGen {
	private int _rowC = 0;

	public RandomColGen(int rowc) {
		_rowC = rowc;
	}; // Constructor

	public int getColCount() {
		return _rowC;
	}

	public void setColCount(int rowc) {
		_rowC = rowc;

	}

	public Vector<Long> updateColumnRandomInt(long max, long min) {
		Random rd = new Random();
		Vector<Long> _vc = new Vector<Long>();
		for (int i = 0; i < _rowC; i++) {
			double rdGen = rd.nextDouble();
			double res = min + ((max - min) * ((1 - rdGen) / 1));
			_vc.add(Long.valueOf(Math.round(res)));
		}
		return _vc;
	}

	public Vector<Double> updateColumnRandomDouble(long max, long min) {
		Random rd = new Random();
		Vector<Double> _vc = new Vector<Double>();
		for (int i = 0; i < _rowC; i++) {
			double rdGen = rd.nextDouble();
			double res = min + ((max - min) * ((1 - rdGen) / 1));
			_vc.add(Double.valueOf(res));
		}
		return _vc;
	}

	public Vector<Date> updateColumnRandomDate(long max, long min) {
		Random rd = new Random();
		Vector<Date> _vc = new Vector<Date>();
		int i = 0;
		while (i < _rowC) {
			double rdGen = rd.nextDouble();
			double res = min + ((max - min) * ((1 - rdGen) / 1));
			_vc.add(new Date(Math.round(res)));
			i++;
		}
		return _vc;
	}

	public Vector<String> updateColumnRandomString(long maxL, long minL,
			int lan, int type) {
		int minCodePoint = getRangeOfUnicode(lan, true);
		int maxCodePoint = getRangeOfUnicode(lan, false);

		System.out
				.println("\n Information: Make sure fonts are available for selected language");
		System.out.println("\n or Character may not appear.");
		Vector<String> _vc = new Vector<String>();

		Random rd = new Random();
		for (int i = 0; i < _rowC; i++) {
			String repS = "";
			int currCount = 0;
			int degCount = (int) minL + rd.nextInt((int) maxL - (int) minL);

			while (currCount < degCount) {
				int codeP = minCodePoint
						+ new Integer(rd.nextInt(maxCodePoint - minCodePoint));

				if (Character.isISOControl(codeP) == true)
					continue;
				if (type == 1 && Character.isLetterOrDigit(codeP) == false)
					continue;
				if (type == 2 && Character.isLetter(codeP) == false)
					continue;
				if (type == 3 && Character.isDigit(codeP) == false)
					continue;

				if (repS.equals("")
						&& Character.isJavaIdentifierStart(codeP) == true) {
					char[] rep = Character.toChars(codeP);
					repS += new String(rep);
					currCount++;
				} else if (Character.isJavaIdentifierPart(codeP) == true) {
					char[] rep = Character.toChars(codeP);
					repS += new String(rep);
					currCount++;
				}
			}

			_vc.add(new String(repS));
		}
		return _vc;
	}

	private int getRangeOfUnicode(int lan, boolean lower) {

		switch (lan) {

		case Language.LATIN_BASIC:
			if (lower == true)
				return Language.LATIN_BASIC_MIN;
			else
				return Language.LATIN_BASIC_MAX;

		case Language.GREEK:
			if (lower == true)
				return Language.GREEK_MIN;
			else
				return Language.GREEK_MAX;

		case Language.HEBREW:
			if (lower == true)
				return Language.HEBREW_MIN;
			else
				return Language.HEBREW_MAX;

		case Language.ARABIC:
			if (lower == true)
				return Language.ARABIC_MIN;
			else
				return Language.ARABIC_MAX;

		case Language.DEVANAGIRI:
			if (lower == true)
				return Language.DEVANAGIRI_MIN;
			else
				return Language.DEVANAGIRI_MAX;

		case Language.TAMIL:
			if (lower == true)
				return Language.TAMIL_MIN;
			else
				return Language.TAMIL_MAX;

		case Language.KANNADA:
			if (lower == true)
				return Language.KANNADA_MIN;
			else
				return Language.KANNADA_MAX;

		case Language.THAI:
			if (lower == true)
				return Language.THAI_MIN;
			else
				return Language.THAI_MAX;

		case Language.HANGUL:
			if (lower == true)
				return Language.HANGUL_MIN;
			else
				return Language.HANGUL_MAX;

		case Language.HIRAGANA:
			if (lower == true)
				return Language.HIRAGANA_MIN;
			else
				return Language.HIRAGANA_MAX;

		case Language.KATAKANA:
			if (lower == true)
				return Language.KATAKANA_MIN;
			else
				return Language.KATAKANA_MAX;

		case Language.BOPOMOFO:
			if (lower == true)
				return Language.BOPOMOFO_MIN;
			else
				return Language.BOPOMOFO_MAX;

		case Language.KANBUN:
			if (lower == true)
				return Language.KANBUN_MIN;
			else
				return Language.KANBUN_MAX;

		default:
			if (lower == true)
				return 0x0000;
			else
				return 0xFFFF;
		}
	}

}
