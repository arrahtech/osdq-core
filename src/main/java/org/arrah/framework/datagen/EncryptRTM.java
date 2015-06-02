package org.arrah.framework.datagen;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2015    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for encrypt 
 * and decrypt RTM and string
 * 
 */


import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.arrah.framework.ndtable.ReportTableModel;


public class EncryptRTM {
	
	// Initializing vector
	byte iv[] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

	public EncryptRTM() {
		
	} // Constructor
	

	public  ReportTableModel encryptColumn(ReportTableModel rtm, int colIndex, 
				int beginRow, int endRow, String encrptkey) {
		
		try{
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			// Make sure we have 16 byte string
			byte[] keyval = padkey(encrptkey);
			// initialize the cipher for encrypt mode
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKey keyValue = new SecretKeySpec(keyval,"AES");
			cipher.init(Cipher.ENCRYPT_MODE, keyValue, ivspec);
      
			if (rtm == null || rtm.getModel().getRowCount() == 0) return rtm;
		
			for (int i = beginRow; i < endRow; i++ ) {
				String colVal = (rtm.getModel().getValueAt(i, colIndex)).toString();
				String newcolVal = null;
				byte[] encryptedByte = cipher.doFinal(colVal.getBytes("UTF-8"));
				Base64.Encoder encoder = Base64.getEncoder();
				newcolVal = encoder.encodeToString(encryptedByte);
				rtm.setValueAt(newcolVal,i,colIndex);
			
			} // Encryption done
		} catch (Exception e) {
			System.out.println("Exception:" +e.getLocalizedMessage());
		} finally {
			
		}
		return rtm;
		
	}
	
	public  ReportTableModel decryptColumn(ReportTableModel rtm, int colIndex, 
			int beginRow, int endRow, String encrptkey) {
	
	try{
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		// Make sure we have 16 byte string
		byte[] keyval = padkey(encrptkey);

		// initialize the cipher for encrypt mode
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKey keyValue = new SecretKeySpec(keyval,"AES");
		cipher.init(Cipher.DECRYPT_MODE, keyValue, ivspec);
  
		if (rtm == null || rtm.getModel().getRowCount() == 0) return rtm;
	
		for (int i = beginRow; i < endRow; i++ ) {
			String colVal = (rtm.getModel().getValueAt(i, colIndex)).toString();
			String newcolVal = null;
			//Decode
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] encryptedTextByte = decoder.decode(colVal);
			
			byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
			newcolVal = new String(decryptedByte);
			rtm.setValueAt(newcolVal,i,colIndex);
		
		} // Encryption done
	} catch (Exception e) {
		System.out.println("Exception:" +e.getLocalizedMessage());
	} finally {
		
	}
	return rtm;
	
}
	private byte[] padkey (String key) {
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

	
} // End of EncryptRTM
