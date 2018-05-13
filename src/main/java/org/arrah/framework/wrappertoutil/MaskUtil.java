package org.arrah.framework.wrappertoutil;
/**
 * @author vivek singh
 *
 */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.time.LocalDate;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author vivek singh
 *
 */
public class MaskUtil {
	// Initializing vector
	static byte iv[] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		
	
	/**
	 * @param a
	 * This function will retrun MD5 hashcode of the List
	 * @return ArrayList
	 */
	public static ArrayList<Object> toHashValue (ArrayList<Object> a) throws RuntimeException{
		ArrayList<Object> md5Val = new ArrayList<Object>();
		
		for (Object o:a) {
			if (o == null) md5Val.add("d41d8cd98f00b204e9800998ecf8427e"); //null MD5 value
			else
				md5Val.add(getMD5(o.toString()).toString(16));
		}
		return md5Val;
	}
	
	/**
	 * @param a Object to be hashed
	 * @return hashed string
	 * @throws RuntimeException
	 */
	public static Object toHashValue (Object a) throws RuntimeException{
		
			if (a == null) return "d41d8cd98f00b204e9800998ecf8427e"; //null MD5 value
			return getMD5(a.toString()).toString(16);
	}
	
	
	/**
	 * @param input String
	 * This static function will take a string and return an MD5 value of it
	 * @return
	 */
	private static BigInteger getMD5(String input) {
		BigInteger number = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");            
			byte[] messageDigest = md.digest(input.getBytes());            
			 number = new BigInteger(1, messageDigest);   // -1 for negative, 0 for zero, or 1 for positive                        
			} catch (NoSuchAlgorithmException e) {
		         throw new RuntimeException(e);        
			} catch (Exception e) {
				return new BigInteger("0");
			}
		// System.out.println("Big Integer is:"+number);
		return number;
	}
	
	/**
	 * @param val Object to be enctypted
	 * @param encrptkey key
	 * This function will take an object and return encryption
	 * @return enctypted string
	 */
	
	public  static Object toEncryptedVal(Object vals, String encrptkey) {
		if (vals == null || vals.toString().length() ==0 )
			return "";
	
		try{
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			// Make sure we have 16 byte string
			byte[] keyval = padkey(encrptkey);
			// initialize the cipher for encrypt mode
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKey keyValue = new SecretKeySpec(keyval,"AES");
			cipher.init(Cipher.ENCRYPT_MODE, keyValue, ivspec);
				
			byte[] encryptedByte = cipher.doFinal(vals.toString().getBytes("UTF-8"));
			Base64.Encoder encoder = Base64.getEncoder();
			return encoder.encodeToString(encryptedByte);
			// Encryption done
				
		} catch (Exception e) {
			System.out.println("Exception:" +e.getLocalizedMessage());
			return "";
		} finally {
			//TODO
		}
	}
	
	/**
	 * @param vals Arraylist of values to be encrypted
	 * @param encrptkey 
	 * this function takes an ArrayList of objects and key and encrypts the Arraylist
	 * @return
	 */
	public static ArrayList<Object> toEncryptedVal(ArrayList<Object> vals, String encrptkey) {
		ArrayList<Object> encyptVal = new ArrayList<Object>();
		
		for (Object o:vals) 
			encyptVal.add(toEncryptedVal(o,encrptkey));
		
		return encyptVal;
		
	}
	
	/**
	 * @param vals to be decypted
	 * @param decryptkey
	 * This function will take a encrypted value and return the decrypted value
	 * @return decrypted value
	 */
	public  static Object toDecryptedVal(Object vals, String decryptkey) {
		if (vals == null || vals.toString().length() ==0 )
			return "";
	
		try{
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			// Make sure we have 16 byte string
			byte[] keyval = padkey(decryptkey);

			// initialize the cipher for decrytion mode
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKey keyValue = new SecretKeySpec(keyval,"AES");
			cipher.init(Cipher.DECRYPT_MODE, keyValue, ivspec);
				
			//Decode
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] encryptedTextByte = decoder.decode(vals.toString());
			
			byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
			return new String(decryptedByte);
			// Decryption done
				
		} catch (Exception e) {
			System.out.println("Exception:" +e.getLocalizedMessage());
			return "";
		} finally {
			//TODO
		}
	}
	
	/**
	 * @param vals Arraylist to be decrypted
	 * @param decryptkey key for decryption
	 * This function takes an arrylist of encrypted value and decrypts it using the key.
	 * @return decrypted Arraylist
	 */
	public static ArrayList<Object> toDecryptedVal(ArrayList<Object> vals, String decryptkey) {
		ArrayList<Object> decyptVal = new ArrayList<Object>();
		
		for (Object o:vals) 
			decyptVal.add(toDecryptedVal(o,decryptkey));
		
		return decyptVal;
		
	}
	
	/**
	 * @param key
	 * this function will return padding key
	 * @return
	 */
	private static byte[] padkey (String key) {
		byte padding[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		if (key == null || "".equals(key)) return padding;
		try {
			byte[] keybtye = key.getBytes("UTF-8");
			int len = keybtye.length;
			if ( len == 16) { // 16 bytes is hard coded 
				return keybtye;
			} else {
				for (int i=0; i < 16 && i < len ; i++ )
					padding[i] = keybtye[i];
				return padding;
			}
		} catch(Exception e) {
			System.out.println("Exception:" +e.getLocalizedMessage());
			return padding;
		} finally {
			
		}
		
	}
	
	/**
	 * @param vals to be shuffled
	 * This will will return the shuffled value of input object
	 * @return String Shuffled value
	 */
	public  static Object toShuffleVal(Object vals) {
		if ( vals == null || "".equals(vals.toString())) return "";
		String str = vals.toString();
		
		ArrayList<Character> charList = new ArrayList<Character>();
		for (int i=0; i < str.length(); i++) {
			charList.add(str.charAt(i));
		}
		
		Collections.shuffle(charList);
		String newVal = new String();
		for (int i=0; i < charList.size(); i++) {
			newVal = newVal.concat(charList.get(i).toString());
		}
		return newVal;
		
	}
	
	/**
	 * @param vals list of objects to be shuffled
	 * This will will return the shuffled value of input object list
	 * @return shuffled arrayList
	 */
	public  static ArrayList<Object> toShuffleVal(ArrayList<Object> vals) {
		ArrayList<Object> shuffleVal = new ArrayList<Object>();
		
		for (Object o:vals) 
			shuffleVal.add(toShuffleVal(o));
		
		return shuffleVal;
	}
	
	/**
	 * @param vals to be shuffled should be Number
	 * This will will return the shuffled value of input object
	 * @return Integer Shuffled value
	 */
	public  static Object toShuffleVal(Number vals) {
		if ( vals == null ) return 0; // zero for null
		String str = vals.toString();
		
		ArrayList<Character> charList = new ArrayList<Character>();
		for (int i=0; i < str.length(); i++) {
			charList.add(str.charAt(i));
		}
		
		Collections.shuffle(charList);
		String newVal = new String();
		for (int i=0; i < charList.size(); i++) {
			newVal = newVal.concat(charList.get(i).toString());
		}
		try {
			return new Double(newVal).intValue(); // get the biggest byte then make it Integer
		} catch  (Exception e){
			return 0;
		}
		
	}
	
	/**
	 * @param vals the object to be masked
	 * @param maskChar - the masking character
	 * @param maskPosition - start index of masking 
	 * @return
	 */
	public static Object toMaskVal(Object vals, String maskChar, int maskPosition) {
	
	if (vals == null || "".equals(vals.toString())) return "";
		String colVal = vals.toString();
		String newcolVal = null;
		
		if ( maskChar.length() > colVal.length() )
			newcolVal = maskChar;
		else {
			switch (maskPosition) {
			case 0: // start from begining
				newcolVal = maskChar +colVal.substring(maskChar.length());
				break;
			case -1 : // start from end
				newcolVal = colVal.substring(0,colVal.length() -maskChar.length()) + maskChar;
				break;
			default: // start from pos of colIndex
				newcolVal = colVal.substring(0,maskPosition) + maskChar;
				break;
			}
		} // end of else
		
		return newcolVal;
	}
	
	/**
	 * @param vals arrayList to be masked
	 * @param maskChar - masking character
	 * @param maskPosition - from where to mask
	 * @return masked ArrayList
	 */
	public static ArrayList<Object> toMaskVal(ArrayList<Object> vals, String maskChar, int maskPosition) {
		
		ArrayList<Object> maskVal = new ArrayList<Object>();
		
		for (Object o:vals) 
			maskVal.add(toMaskVal(o,maskChar,maskPosition));
		
		return maskVal;
	}
	
	/**
	 * @param vals the object to be masked Number
	 * @param maskChar - the masking character [0-9] 0 defualt
	 * @param maskPosition - start index of masking 
	 * @return masked Integer value of Number
	 */
	public static Object toMaskVal(Number vals, char maskChar) {
	
	if (vals == null ) return 0;
		String colVal = vals.toString();
		int strlen = colVal.length();
		
		StringBuffer newcolVal = new StringBuffer();
		
		if (Character.isDigit(maskChar) == false)
			maskChar = '0';
		for (int i=0; i <strlen; i++ )
			newcolVal.append(maskChar);
		
		try {
			return new Double(newcolVal.toString()).intValue(); // get the biggest byte then make it Integer
		} catch  (Exception e){
			return 0;
		}
	}
	
	/**
	 * @param seedDate on which random value will be added
	 * This function will a take a seed value and create 
	 * randomDate adding that seed value valid values -999999999 - 999999999
	 * @return Date it may be future date
	 */
	public static Object toDateVal(long seedDate) {
		Random rd = new Random();
		long rdGen = rd.nextLong();
		long res = seedDate+rdGen;
		try {
			return LocalDate.ofEpochDay(res);
		} catch (Exception e) {
			return LocalDate.ofEpochDay(000000000L);
		}
	}
	
	/**
	 * @param max long value for random value since epoch
	 * @param min long value for random value since epoch
	 * valid values -999999999 - 999999999
	 * @return Date random but values within min and max
	 */
	public static Object toDateVal(long max, long min) {
		Random rd = new Random();
		double rdGen = rd.nextDouble();
		double res = min + ((max - min) * ((1 - rdGen) / 1));
		try {
			return  LocalDate.ofEpochDay(Math.round(res));
		} catch (Exception e) {
			return LocalDate.ofEpochDay(000000000L);
		}

	}
	
	public static void main(String [] args) {
		
		boolean ab = true;
		
		ArrayList<Object> testA = new ArrayList<Object>();
		testA.add("Testing");
		testA.add(null);
		testA.add(new Double(1111100.00D));
		testA.add("0");
		testA.add("slfjajsdlflsdnflsdfsdjfjdsajfasdjf;sad;fjsa;djf;asdj;fjsad;fj;sdajf;jsad;fjd;sjf;sdj");
		//testA.add(new Boolean(true));
		testA.add(ab);
		
		
//		ArrayList<Object> testOut = toHashValue(testA);
//		for (Object a: testOut)
//			System.out.println(a);
//		
		
//		 Object testOut = toHashValue(testA.get(5));
//		 System.out.println(testOut);
//		
//		 Object testOut = toEncryptedVal(testA.get(4),"key");
//		 System.out.println(testOut);
//		
//		 ArrayList<Object> testOutA = toEncryptedVal(testA,"key");
//			for (Object a: testOutA)
//				System.out.println(a);
//		
//		Object testOut = toDecryptedVal("kYvhXshspq+yvhgB0QYoecuGe64+Wg21sHYhQfYdoVxHHeVUerC31PeewMXHaLRgr8zwFF1w226ZhFV4GHEQQHFJ43Et4P3QfoOWDCCPPXKPXGOGR15lM2TK+O6QQASu","key");
//			System.out.println(testOut);
//			
//		ArrayList<Object> encryptedtestA = new ArrayList<Object>();
//		encryptedtestA.add("maR9EN1FAAm1OAd2igLB6g==");
//		encryptedtestA.add("");
//		encryptedtestA.add("D1kxE28DkK7a+b/9p9oSUw==");
//		encryptedtestA.add("LyPAQCxhld3oGUPTu5olvQ==");
//		
//		ArrayList<Object> testOutA = toDecryptedVal(encryptedtestA,"key");
//		for (Object a: testOutA)
//			System.out.println(a);
		
//		Object testOut = toShuffleVal(testA.get(4));
//			System.out.println(testOut);
//		
//		Object testOutI = toShuffleVal(12345.8444);
//		System.out.println(testOutI);
//			
//		ArrayList<Object> testOutA = toShuffleVal(testA);
//			for (Object a: testOutA)
//				System.out.println(a);
		
//		Object testOut = toMaskVal(testA.get(4),"----",4);
//			System.out.println(testOut);
//		
//		Object testOutI = toMaskVal(34443.44,'7');
//		System.out.println(testOutI);
//		
//		ArrayList<Object> testOutA = toMaskVal(testA,"----",4);
//		for (Object a: testOutA)
//			System.out.println(a);
		
		Object testOut = toDateVal(00000L);
		System.out.println(testOut);
		
		Object testOutD = toDateVal(100000000L,900000000000000L);
		System.out.println(testOutD);
		
		
	}

}
