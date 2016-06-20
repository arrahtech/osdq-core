package org.arrah.framework.analytics;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * This file is used to validate PII fields
 * like creditcard, SSN, DoB etc
 */

public class PIIValidator {
	
	private String errdef =""; 

	public PIIValidator() {
		
	} // Constructor
	
	// Validating a credit card
	public boolean isCreditCard(String ccnumber) {
		errdef = "";
		int strlen = ccnumber.length();
		// length
		if (strlen > 19 || strlen < 12) {
			errdef = "No of digits not matching";
			return false;
		}
		
		String lastdc = ccnumber.substring(strlen-1, strlen); // last digit
		int lastd = -1; // last digit holder
		
		// last digit fetch
		try {
			lastd = Integer.parseInt(lastdc);
			int checkd = luhn_checkSum(ccnumber);
			if (lastd != checkd ) {
				errdef = "Check Sum failed";
				return false;
			}
			
		} catch (Exception e) {
			errdef = "Non Digit Values for Credit Card";
			return false;
		}
		
		errdef = "Credit Card successful";
		return true;
	}

	//Algo to validate checksum
	private  int luhn_checkSum(String numberString) {
		int sum = 0, checkDigit = 0;
		boolean isDouble = true; // toggle
		
		for (int i = numberString.length() - 2; i >= 0; i--) { // leave last digit
			int k = Integer.parseInt(String.valueOf(numberString.charAt(i)));
			sum += sumToSingleDigit((k * (isDouble ? 2 : 1)));
			isDouble = !isDouble;
		}

		if ((sum % 10) > 0)
			checkDigit = (10 - (sum % 10));

		return checkDigit;
	}

	private  int sumToSingleDigit(int k) {
		if (k < 10)
			return k;
		return sumToSingleDigit(k / 10) + (k % 10);
	}
	
	
	// Validating SSN
	public boolean isSSN(String ssnnumber) {
		int strlen = ssnnumber.length();
		
		// length
		if (strlen != 9) { // ssn 9 digit
			errdef = "No of digits not matching for SSN";
			return false;
		}
		
		// Invalid numbers
		if (ssnnumber.startsWith("000") || ssnnumber.startsWith("666") ||
				ssnnumber.startsWith("9") || ssnnumber.endsWith("0000") ||
				ssnnumber.startsWith("00",3)) {
			errdef = "Invalid digits for SSN";
			return false;
		}
		
		// Is all digit ?
		try {
			for (int i=0; i < strlen; i++) 
				 Integer.parseInt(String.valueOf(ssnnumber.charAt(i)));
		} catch (Exception e) {
			errdef = "Non Digit Values for SSN";
			return false;
		}
		errdef = "SSN Validation Successful";
		return true;
		
	}
	// Validating SSN
	public boolean isPhone(String phonenum) {
		int strlen = phonenum.length();
		
		if (strlen > 12 || strlen < 8) { // phone number can vary from 8 digit to upto 12 digits
			errdef = "No of digits not matching";
			return false;
		}
		errdef = "Phone Validation Successful";
		return true;
		
	}
	
	// Validating Emails
	public boolean isEmails(String[] emailStr) {
		boolean allEmail = false;
		for(String email : emailStr) {
			allEmail = isEmail(email);
			if (allEmail == false)
				return false;
		}
		errdef = "Email Validation successful";
		return true;
		
	}
	// Validating Email
	public boolean isEmail(String emailStr) {
		//  Valid Email="^.+@[^\.].*\.[a-zA-Z0-9]{2,}$"
		// String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

		final String EMAIL_PATTERN =  "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			 
		  Matcher matcher = pattern.matcher(emailStr);
		  if (matcher.matches() == false) {
			  errdef = "Not well formed email";
			  return false;
		  }
			  
		errdef = "Email Validation successful";
		return true;
		
	}
	// Validating Date of Birth
	public boolean isDoB(Date dob) {
		
		long dobmilliSec = dob.getTime();
		long nowmilliSec = System.currentTimeMillis();
		
		// date of birth can not be negative
		if (dobmilliSec > nowmilliSec ) {
			errdef = "Dob is after today's date";
			return false;
		}

		errdef = "Date of Birth Validation successful";
		return true;
		
	}
	// Validating IP v4 and v6
	public boolean isIp(String ipaddress) {
		
		final Pattern IPV4_PATTERN =
				Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
		final Pattern IPV6_STD_PATTERN =
				Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
		final Pattern IPV6_HEX_COMPRESSED_PATTERN =
				Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

		  if (IPV4_PATTERN.matcher(ipaddress).matches() == true ||
				  IPV6_STD_PATTERN.matcher(ipaddress).matches() == true ||
				  IPV6_HEX_COMPRESSED_PATTERN.matcher(ipaddress).matches() == true ) {
			  errdef = "IP Validation successful";
				return true;
		  } else {
			  errdef = "Not well formed IP";
			  return false;
		  }
	}
	
	// Get error message
	public String getErrdef() {
		return errdef;
	}

	
	
} // End of PIIValidator
