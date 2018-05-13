package org.arrah.framework.util;

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

/* 
 * This file contains logic for license validation
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JOptionPane;

public class LicenseManager {

	private Date oldDate = null;
	private Hashtable<String,String> _table = new Hashtable<String,String>();

	public boolean isEval = true;
	public int days_remaining = -1;
	public String c_name = null;

	@SuppressWarnings("unchecked")
	public boolean isValid() {
		try {

			// Open the file and read Company name
			FileInputStream fileIn = new FileInputStream("profiler.lic");
			ObjectInputStream in = new ObjectInputStream(fileIn);

			// If eval copy gets the timestamp
			_table = (Hashtable<String,String>) in.readObject();
			c_name = (String) _table.get("Company_name");
			if (c_name.equalsIgnoreCase("evaluation copy") == true) {
				System.out.println("\nEvaluation Copy");
				oldDate = (Date) in.readObject();
				if (oldDate == null) {

					System.out.println("Date Null");
					// If time stamp null close the read and open the file
					// in write mode
					in.close();
					fileIn.close();
					Calendar old_d = Calendar.getInstance();
					oldDate = old_d.getTime();
					days_remaining = 60;
					// days_remaining = 15 ; // 15 days license
					FileOutputStream fileOut = new FileOutputStream(
							"profiler.lic");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject((Hashtable<String,String>) _table);
					out.writeObject((Date) oldDate);
					out.close();
					fileOut.close();
					return true;
				} else {
					in.close();
					return isTimeV();
				}
			}
			isEval = false;
			in.close();
			if (c_name == null) {
				return false;
			}

		} catch (FileNotFoundException file_exp) {
			System.err.println("License File not found.");
			System.err.println("Exiting ........");
			System.exit(0);
		} catch (IOException exp) {
			JOptionPane.showMessageDialog(null, exp.getMessage(),
					"Error Message", JOptionPane.ERROR_MESSAGE);
			exp.printStackTrace();
		} catch (ClassNotFoundException cl_exp) {
			JOptionPane.showMessageDialog(null, cl_exp.getMessage(),
					"Error Message", JOptionPane.ERROR_MESSAGE);
			cl_exp.printStackTrace();
		}

		System.out.println("\nProduct Licensed to: " + c_name);
		return true;

	}

	private boolean isTimeV() {

		Calendar old = Calendar.getInstance();
		old.setTime(oldDate);
		old.setLenient(true);
		Calendar now = Calendar.getInstance();
		now.setLenient(true);
		old.add(Calendar.DAY_OF_YEAR, 60); 
		if (now.after(old)) {
			JOptionPane.showMessageDialog(null, "License has expired.....",
					"Error Message", JOptionPane.ERROR_MESSAGE);
			System.err.println("License has expired....");
			return false;
		}
		int oy = old.get(Calendar.YEAR);
		int ny = now.get(Calendar.YEAR);
		int doy = old.get(Calendar.DAY_OF_YEAR);
		int dny = now.get(Calendar.DAY_OF_YEAR);
		if (oy == ny)
			days_remaining = doy - dny;
		if (oy > ny)
			days_remaining = (365 - dny) + doy;
		System.err.println("Days remaining: " + days_remaining);
		return true;

	}

}
