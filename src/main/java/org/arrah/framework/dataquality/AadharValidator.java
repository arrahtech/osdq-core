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
 * This file is used to validate AAdhar  numbers
 * 
 */



/**
 * https://www.quora.com/What-is-the-structure-of-ones-Aadhar-Card-UID-number
 *
 */
public class AadharValidator  {
	
	private static Pattern pattern = Pattern.compile("^([0-9]{12})"); // 12 digit
	
	public HashMap<String, String> validate(String id) {

		HashMap<String, String> responseMap = new HashMap<>();
		responseMap.put("isValid", "false");
		if (id != null && !id.isEmpty()) {
			id = id.toUpperCase();
			char firstc=id.charAt(0);
			
			if (firstc == '0' || firstc =='1') {
				responseMap.put("entityDesc", "AADHAR can not start with 0 or 1");
				return responseMap;
			}
			
			Matcher matcher = pattern.matcher(id);
			if (matcher.matches()) {
				//TODO: we may add nice looking number rejection rule
				if (Verhoeff.validateVerhoeff(id)) {
					responseMap.put("isValid", "true");
					responseMap.put("entityDesc", "Valid Aadhar number");
				} else{
					responseMap.put("isValid", "false");
					responseMap.put("entityDesc", "AADHAR Checksum failed.");
					}
			} else {
				responseMap.put("isValid", "false");
				responseMap.put("entityDesc", "AADHAR Format is not valid. 12 digits");
			}
		} else {
			responseMap.put("entityDesc", "AADHAR Number is Empty");
		}
		return responseMap;
		
	}
	
	public boolean isValidAadhar(String id) {

		HashMap<String, String> responseM = validate( id);
		String val = responseM.get("isValid");
		if (val == null || "".equals(val) || val.equalsIgnoreCase("false") )
			return false;
		return true;
		
	}



}
