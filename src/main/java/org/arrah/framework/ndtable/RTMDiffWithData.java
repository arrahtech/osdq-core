package org.arrah.framework.ndtable;

/**************************************************
*     Copyright to Vivek K Singh      2019        *
*                                                 *
* Any part of code or file can be changed,        *
* redistributed, modified with the copyright      *
* information intact                              *
*                                                 *
* Author$ : Vivek Singh                           *
*                                                 *
**************************************************/

/*
* This file will provide utility functions 
* to diff between two RTM (Report Table Model)
* classes.
*
*/


import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class RTMDiffWithData {	
	
	private ReportTableModel leftRTM = null, rightRTM = null;
	private String[] _dataTypeL ;// same data type will match _dataTypeR;
	private Vector<Integer> _leftKeyI,_rightKeyI,_colsToMatchLI,_colsToMatchRI;
	private Hashtable<String,Object[]> keywithDataL,keywithDataR;
	private String[][] keydata;
	private ArrayList<String[]> nomatchKeyData;

	
	public RTMDiffWithData() {
		// Default Constructor
	}
	public RTMDiffWithData(ReportTableModel left, ReportTableModel right) {
		leftRTM = left;
		rightRTM = right;
	}
	
	// This function will take key, columns to diff and their data type
	public RTMDiffWithData(ReportTableModel left, String[] keyL, String[] datatypeL,String[] colsToMatchL,
							ReportTableModel right, String[] keyR, String[] datatypeR, String[] colsToMatchR) {
		leftRTM = left; rightRTM = right;
		
		_leftKeyI = new Vector<Integer>(); _rightKeyI = new Vector<Integer>();
		_colsToMatchLI = new Vector<Integer>(); _colsToMatchRI =  new Vector<Integer>();
		
		// Get index to pull in values
		// get the index of key from columNames
		for (String key:keyL)
			_leftKeyI.add(left.getColumnIndex(key));
		for (String key:keyR)
			_rightKeyI.add(right.getColumnIndex(key));
		
		// get the index of columns to match from columNames
		for (String key:colsToMatchL)
			_colsToMatchLI.add(left.getColumnIndex(key));
		for (String key:colsToMatchR)
			_colsToMatchRI.add(right.getColumnIndex(key));
		
		// Validation
		if ( (_leftKeyI.indexOf(-1) != -1) || (_rightKeyI.indexOf(-1) != -1) || 
			 (_colsToMatchLI.indexOf(-1) != -1) || (_colsToMatchRI.indexOf(-1) != -1 ) ) {
			System.out.println("Error: Column Name could not be found in table");
			return;
		}
		
		_dataTypeL = datatypeL;
		prepareData();// Now prepare
		
	}
	
	// This funtion will put the data in key , value in hashtable that will be 
	// easier to compare
	private void prepareData() {
		
		int leftRowC = leftRTM.getModel().getRowCount();
		int rightRowC = rightRTM.getModel().getRowCount();
		
		keywithDataL = new Hashtable<String,Object[]> ();
		keywithDataR = new Hashtable<String,Object[]> ();
		
		// Validation
		if ( _leftKeyI == null || _leftKeyI.size() <= 0 ||
			_rightKeyI  == null || _rightKeyI.size() <= 0 ||
			_colsToMatchLI == null || _colsToMatchLI.size() <=0 ||
			_colsToMatchRI == null || _colsToMatchRI.size() <=0 )
		{
			System.out.println("Error:" + " Key or Columns to compare is empty");
			return;
		}
		
		// Prepare Left or First table
		for (int i=0 ; i < leftRowC; i++) {
			Object[] leftRow = leftRTM.getRow(i);
			if (leftRow == null || leftRow.length == 0) continue;
			
			//Initialize ( appends all the keys together and all matching data in array
			String key ="" ;
			Object[] data = new Object[_colsToMatchLI.size()];
			
			for (int j=0; j< _leftKeyI.size(); j++)
				key = key+  leftRow[_leftKeyI.get(j)] +"@@@"; // @@@ used as column separator
			
			for (int j=0; j< _colsToMatchLI.size(); j++)
				data[j] = leftRow[_colsToMatchLI.get(j)];
			
			keywithDataL.put(key, data); // populate hashtable
		}
		
		// Prepare Right or Second table
		for (int i=0 ; i < rightRowC; i++) {
			Object[] rightRow = rightRTM.getRow(i);
			if (rightRow == null || rightRow.length == 0) continue;
			
			//Initialize ( appends all the keys together and all matching data in array
			String key ="" ;
			Object[] data = new Object[_colsToMatchRI.size()];
			
//			System.out.println("Size:" + _colsToMatchRI.size());
//			System.out.println("Array" + _colsToMatchRI.toString());
//			System.out.println("Key" + _rightKeyI.toString());
			
			for (int j=0; j< _rightKeyI.size(); j++)
				key = key+  rightRow[_rightKeyI.get(j)] +"@@@"; // @@@ used as column separator;
			
			for (int j=0; j< _colsToMatchRI.size(); j++)
				data[j] = rightRow[_colsToMatchRI.get(j)];
			
			keywithDataR.put(key, data); // populate hashtable
		}
		
	}
	
	// this is public function to compare two datasets  default Inner Join
	// there should be an API wrapper around it
	// it will return the data , keys already should be with API calling function
	public Object[][] compareData() {
		
		// Validation
		if ( _leftKeyI == null || _leftKeyI.size() <= 0 ||
			_rightKeyI  == null || _rightKeyI.size() <= 0 ||
			_colsToMatchLI == null || _colsToMatchLI.size() <=0 ||
			_colsToMatchRI == null || _colsToMatchRI.size() <=0 )
		{
			System.out.println("Error:" + " Key or Columns to compare is empty");
			return new Object[1][]; // placeholder
		}
				
		Object[][] newdata = new Object[leftRTM.getModel().getRowCount()][_colsToMatchLI.size()*3]; // 3 values for each column - left,right and diff
		keydata = new String[leftRTM.getModel().getRowCount()][];
		nomatchKeyData = new ArrayList<String[]>();
		Set<String> keyset = keywithDataL.keySet();
		int index=0;
		
		for (String key:keyset ) {
			Object[] rightD = keywithDataR.get(key); // inner join keys should be in both
			Object[] leftD = keywithDataL.get(key);
			
			if ( leftD == null || leftD.length ==0 )
				continue; // inner join condition failed
			
			if (rightD == null || rightD.length ==0 )  { // Left outer join condition
				String [] nomatchKey = key.split("@@@");
				nomatchKeyData.add(nomatchKey);
				continue; // inner join condition failed
			}
			
			for (int i=0; i <rightD.length; i++ ) {
				
				keydata[index] = key.split("@@@");
//				System.out.println("index:"+index + "i:" +i + "key:" + Arrays.toString(keydata[index]));
//				System.out.println(Arrays.toString(rightD));
//				System.out.println(Arrays.toString(leftD));
//				
				newdata[index][i*3]=leftD[i];newdata[index][(i*3) + 1]=rightD[i];
				if (leftD[i] == null || rightD[i] == null) {
					newdata[index][(i*3) + 2] = null;
					continue;
				}
				// newdata[index][(i*3) + 2] = leftD[i] -  rightD[i] ; // depending on datatype implement -
				if (_dataTypeL[i].equals("Number")) {
					newdata[index][(i*3) + 2] =  Double.parseDouble(leftD[i].toString()) - Double.parseDouble(rightD[i].toString());
				} else if (_dataTypeL[i].equals("Date")) { // find date format
					// might need diff from epoch date
					try {
						newdata[index][(i*3) + 2] =  DateFormat.getDateInstance().parse(leftD[i].toString()).compareTo(
								DateFormat.getDateInstance().parse(rightD[i].toString()) );
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					newdata[index][(i*3) + 2] =  leftD[i].toString().compareTo(rightD[i].toString());
				}
			}
			
			index++;
		}
		return newdata;
	}	
	
	public String[][] getkeydata() {
		return keydata;
	}
	public ArrayList<String[]> getNomatchKeyData() {
		return nomatchKeyData;
	}
	
} // end of class RTMDiffUtil
