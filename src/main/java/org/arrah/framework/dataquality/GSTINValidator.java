package org.arrah.framework.dataquality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2016    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used to validate GSTIN fields
 * 
 */


/**
 * GSTIN Validator as per
 * https://www.quora.com/How-do-they-calculate-the-checksum-in-the-GST-number-of-India
 * 
 * @author arun-
 *
 */
public class GSTINValidator   {

	private static Pattern stateCode = Pattern.compile("(0[1-9]|1[0-9]|2[0-9]|3[0-7])");

	private static Pattern char13And14 = Pattern.compile("(\\d{1})Z$");

	private PANValidator panValidator = new PANValidator();

	public HashMap<String, String> validate(String id) {

		HashMap<String, String> responseMap = new HashMap<>();
		responseMap.put("isValid", "false");
		if (id != null && !id.isEmpty() && id.length() == 15) {
			id = id.toUpperCase();
			Matcher stateCodeMatcher = stateCode.matcher(id.substring(0, 2));
			Matcher lastThreeCharsMatcher = char13And14.matcher(id.substring(12, 14));
			Map<String, String> panValidation = panValidator.validate(id.substring(2, 12));
			boolean isValidPAN = Boolean.parseBoolean(panValidation.get("isValid"));
			if (stateCodeMatcher.matches() && isValidPAN && lastThreeCharsMatcher.matches() && isCheckSumValid(id)) {
				// TODO: if entity name is known, we can also validate 5th char
				responseMap.put("isValid", "true");
				responseMap.put("entityType", panValidation.get("entityType"));
				responseMap.put("entityDesc", panValidation.get("entityDesc"));
			} else {
				responseMap.put("isValid", "false");
				responseMap.put("entityDesc", "GSTIN Format not valid");
			}
		} else {
			responseMap.put("entityDesc", "GSTIN Format is not 15 characters");
		}

		return responseMap;

	}

	/**
	 * 
	 * Step-1 Find “Place Value of Digit” and “Factor” of all Digits as follows:
	 * 
	 * See the following Image to find Place Value of Digit: Factor : Factor is “ 1
	 * ” for all Odd digits(i.e 1st, 3rd, … , 13th) and “ 2 “ for all even
	 * digits(2nd, 4th, …, 14th).
	 * 
	 * Step-2 Now Do the following steps individually for all the digits:
	 * 
	 * Multiply Place Value and Factor (Place Value x Factor). Lets call it “A”
	 * Divide “A” by 36 and Find Quotient(Absolute value lower rounded off) and
	 * Remainder. Add Quotient and Remainder and keep the value aside as Step-2.
	 * Repeat the Step-2 for all the 14 digits.
	 * 
	 * Step-3
	 * 
	 * Add all the fourteen values calculated in Step-2.(lets call it S) Divide S by
	 * 36 and see whats the Remainder (lets call it Z) Deduct Z from 36 and divide
	 * resulted value by 36. [ (36-Z)/36 ] Find Remainder in above step. Lookup the
	 * Character with Place Value as Remainder in above character array. And this
	 * Character will be your Checksum.
	 */

	private boolean isCheckSumValid(String id) {

		char checkSum = id.charAt(14);
		int s = 0;
		for (int i = 0; i < 14; i++) {
			int a = placeValue(id.charAt(i)) * factor(i + 1);
			int q = a / 36;
			int r = a % 36;
			s += (q + r);
		}
		int z = s % 36;
		z = (36 - z) % 36;
		return placeValue(checkSum) == z;

	}

	/**
	 * cardinal index of char in string starting with 1
	 * 
	 * @param cardinalIndex
	 * @return
	 */
	private int factor(int cardinalIndex) {
		return cardinalIndex % 2 == 0 ? 2 : 1;
	}

	/**
	 * place value 0-9 -> 0-9 place value A-Z -> 10-35
	 * 
	 * @param value
	 * @return
	 */
	private int placeValue(char value) {
		if (value >= 48 && value <= 57) {
			return value - 48;
		} else if (value >= 65 && value <= 90) {
			return value - 65 + 10;
		} else {
			return -1;
		}
	}

}
