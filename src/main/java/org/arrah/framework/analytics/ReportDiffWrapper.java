package org.arrah.framework.analytics;

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
* classes which will be made out of BI reports.
* It can do Metric diff based on dimension and
* diff with Agrregate (SUM,COUNT,MIN, MAX,AVG)
* based on group by
*
*/




import java.util.ArrayList;
import java.util.Vector;

import org.arrah.framework.ndtable.RTMDiffWithData;
import org.arrah.framework.ndtable.ReportTableModel;

public class ReportDiffWrapper {	
	
	private ReportTableModel leftRTM = null, rightRTM = null;
	private Vector<Integer> _leftDimensionI,_rightDimensionI,_metricToMatchLI,_metricToMatchRI;
	
	private String[][] matchedKeyData;
	private ArrayList<String[]> nomatchKeyData;


	
	public ReportDiffWrapper() {
		// Default Constructor
	}
	public ReportDiffWrapper(ReportTableModel left, ReportTableModel right) {
		leftRTM = left;
		rightRTM = right;
	}
	
	// This function will take key, columns to diff and their data type
	public ReportDiffWrapper(ReportTableModel left, String[] dimL, String[] aggrTypeL,String[] metricL,
							ReportTableModel right, String[] dimR, String[] metricR) {
		
		leftRTM = TabularReport.showReport(left, dimL, metricL, aggrTypeL);
		rightRTM = TabularReport.showReport(right, dimR, metricR, aggrTypeL);
		
		_leftDimensionI = new Vector<Integer>(); _rightDimensionI = new Vector<Integer>();
		_metricToMatchLI = new Vector<Integer>(); _metricToMatchRI =  new Vector<Integer>();
		
		// Get index to pull in values
		// get the index of key from columNames
		for (String key:dimL)
			_leftDimensionI.add(leftRTM.getColumnIndex(key));
		for (String key:dimR)
			_rightDimensionI.add(rightRTM.getColumnIndex(key));
		
		// get the index of columns to match from columNames
		for (String key:metricL)
			_metricToMatchLI.add(leftRTM.getColumnIndex(key));
		for (String key:metricR)
			_metricToMatchRI.add(rightRTM.getColumnIndex(key));
		
		// Validation
		if ( (_leftDimensionI.indexOf(-1) != -1) || (_rightDimensionI.indexOf(-1) != -1) || 
			 (_metricToMatchLI.indexOf(-1) != -1) || (_metricToMatchRI.indexOf(-1) != -1 ) ) {
			System.out.println("Error: Column Name could not be found in table");
			return;
		}
		
	}
	
	public Object[][] showMetricDiff() {
		
		RTMDiffWithData  rtmdiff = new RTMDiffWithData(leftRTM, _leftDimensionI,_metricToMatchLI,
				 rightRTM, _rightDimensionI, _metricToMatchRI);
		
		Object[][] metricData = rtmdiff.compareData();
		matchedKeyData = rtmdiff.getmatchedKeyData();
		nomatchKeyData = rtmdiff.getNomatchKeyData();
		
		return metricData;
		
	}
	
	public String[][] getmatchedKeyData() {
		return matchedKeyData;
	}
	public ArrayList<String[]> getNomatchKeyData() {
		return nomatchKeyData;
	}
	
} // end of class ReportDiffWrapper
