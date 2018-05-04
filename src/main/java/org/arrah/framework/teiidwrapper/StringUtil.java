package org.arrah.framework.teiidwrapper;
/**
 * @author vivek singh
 *
 */
import java.util.ArrayList;

import org.arrah.framework.analytics.PIIValidator;
import org.arrah.framework.datagen.ShuffleRTM;
import org.arrah.framework.ndtable.ResultsetToRTM;
import org.arrah.framework.util.StringCaseFormatUtil;
import org.arrah.framework.datagen.EncryptRTM;
import org.simmetrics.metrics.*;

/**
 * @author vsingh007c
 *
 */
public class StringUtil {
	
	/**
	 * @param a
	 * The string that need to randomize
	 * 'vivek singh' will become 'ihg vkeivh'
	 * @return String
	 */
	public static String toRandomValue (String a) {
		
		return ShuffleRTM.shuffleString(a);
	}
	
	/**
	 * @param a
	 * This function will retrun MD5 hashcode of the string
	 * @return String
	 */
	public static String toHashValue (String a) {
		if (a==null) return "d41d8cd98f00b204e9800998ecf8427e"; //null MD5 value
		
		return ResultsetToRTM.getMD5(a).toString();
	}
	
	/**
	 * @param a
	 * This function will retun digit characters of the string
	 * @return
	 */
	public static String toDigitValue (String a) {
		
		return StringCaseFormatUtil.digitString(a);
	}
	
	/**
	 * @param val 
	 * String array that need to be encrpyted
	 * @param key
	 * key given to encrypt
	 * @return 
	 * String array after encryption
	 */
	public static String[] encryptStrArray(String[]val, String key) {
		return new EncryptRTM().encryptStrArray(val,key);
		
	}
	/**
	 * @param val 
	 * String array that need to be decrpyted
	 * @param key
	 * key given to decrypt
	 * @return 
	 * String array after decryption
	 */
	public static String[] decryptStrArray(String[]val, String key) {
		return new EncryptRTM().decryptStrArray(val,key);
		
	}
	/**
	 * @param val
	 * @return
	 * -1 of no match otherwise index of the first match
	 */
	public static int whitespaceIndex(String val) {
		return StringCaseFormatUtil.whitespaceIndex(val);
	}
	
	/**
	 * @param Credit Card number
	 * @return boolean if matches credit card logic and checksum
	 */
	public static boolean isValidCreditCard(String cc) {
		return new PIIValidator().isCreditCard(cc);
	}
	
	/**
	 * @param ssn number
	 * @return boolean if matches ssn logic
	 */
	public static boolean isValidSSN(String ssn) {
		return new PIIValidator().isSSN(ssn);
	}
	
	/**
	 * @param phone number
	 * @return boolean if matches phone  logic more than 8 character less than 12 character
	 * can't start with 000
	 */
	public static boolean isValidPhone(String phone) {
		return new PIIValidator().isPhone(phone);
	}
	/**
	 * @param email
	 * @return boolean if valid email
	 */
	public static boolean isValidEmail(String email) {
		return new PIIValidator().isEmail(email);
	}
	
	/**
	 * @param String a
	 * @param String b
	 * @return float distance
	 */
	public static float cosineDistance(String a, String b) {
		ArrayList<Character> alist = StringCaseFormatUtil.toArrayListChar(a);
		ArrayList<Character> blist = StringCaseFormatUtil.toArrayListChar(b);
		java.util.Set<Character> aset = new java.util.HashSet<Character>(alist);
		java.util.Set<Character> bset = new java.util.HashSet<Character>(blist);
		return new CosineSimilarity<Character>().compare(aset, bset);
	}
	
	/**
	 * @param String a
	 * @param String b
	 * @return float distance
	 */
	public static float jaccardDistance(String a, String b) {
		ArrayList<Character> alist = StringCaseFormatUtil.toArrayListChar(a);
		ArrayList<Character> blist = StringCaseFormatUtil.toArrayListChar(b);
		java.util.Set<Character> aset = new java.util.HashSet<Character>(alist);
		java.util.Set<Character> bset = new java.util.HashSet<Character>(blist);
		return new JaccardSimilarity<Character>().compare(aset, bset);
	}
	
	/**
	 * @param String a
	 * @param String b
	 * @return float distance
	 */
	public static float jaroWinklerDistance(String a, String b) {
		return new JaroWinkler().compare(a, b);
	}
	
	/**
	 * @param String a
	 * @param String b
	 * @return float distance
	 */
	public static float levenshteinDistance(String a, String b) {
		return new Levenshtein().compare(a, b);
	}
	
	
	public static void main(String [] args) {
		float i  = jaccardDistance("sdfdsfvivek","sdfdsfdsfds");
		
			System.out.println(i);
		
		/***
			String[] deenc = decryptStrArray(new String[] {"yxCn2esYX+XIds8ND4lDYA==","b6K4piJVEj2x7J15ATFuZw==","KNOE0LloX7rxtKsekOadGg=="}, "key");
			for(String a: deenc)
				System.out.println(a);
		***/
	}

}
