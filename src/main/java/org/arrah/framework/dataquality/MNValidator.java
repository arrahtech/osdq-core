package org.arrah.framework.dataquality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;


/***********************************************
 *     Copyright to Vivek Kumar Singh  2018    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used to validate Mobile Number fields
 * 
 */



public class MNValidator {

	private static Pattern pattern = Pattern.compile("^([6-9]{1})([0-9]{9})");
	
	public HashMap<String, String> validate( String id) {
		
		HashMap<String, String> responseMap = new HashMap<>();
		responseMap.put("isValid", "false");
		if (id != null && !id.isEmpty()) {
			Matcher matcher = pattern.matcher(id);
			if (matcher.matches()) {
				responseMap.put("isValid", "true");
				responseMap.put("entityDesc", "Mobile Number is  valid");
			} else {
				responseMap.put("isValid", "false");
				responseMap.put("entityDesc", "Mobile Number format is wrong");
			}
		} else {
			responseMap.put("isValid", "false");
			responseMap.put("entityDesc", "Mobile Number is  empty");
		}
		return responseMap;
	}
	
	public boolean isValidMN(String id) {

		HashMap<String, String> responseM = validate( id);
		String val = responseM.get("isValid");
		if (val == null || "".equals(val) || val.equalsIgnoreCase("false") )
			return false;
		return true;
		
	}

}
