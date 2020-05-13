package org.arrah.framework.dataquality;

/***************************************************
 *     Copyright to Vivek Kumar Singh 2016         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                           *
 *                                                 *
 **************************************************/

/*
 * This class is used finding duplicate rows from
 * RTM and show the duplicate values
 *
 * This file will also be used for completing 
 * some empty columns like address completion
 * if zip code is provided.
 */
import java.math.BigInteger;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.ndtable.ResultsetToRTM;

public class DeDuplicateRTM {
	private ReportTableModel duplicates = null;
	
	// Constructor
	public DeDuplicateRTM() {
		// Do nothing
	}

	public ReportTableModel removeDuplicate(ReportTableModel reportTableModel, int[] columnIndices) {
		return removeDuplicate(reportTableModel, columnIndices, false);
	}

	/* this function will take report table model and colI
	 * on which duplicate need to be found.
	 * It will take string values of those colI and will do
	 * MD5 and will do match.
	 * 1 in billion there is possibility that it will be a 
	 * false positive. 
	 * 
	 */
	public ReportTableModel removeDuplicate (ReportTableModel reportTableModel, int[] columnIndices, boolean ignoreCase) {
		if (reportTableModel == null) {
			return null;
		}

		int rowCount = reportTableModel.getModel().getRowCount();

		if (rowCount <= 0) {
			return reportTableModel;
		}

		Vector<Integer> deletionIndices = new Vector<>();
		Vector<BigInteger> md5array = new Vector<>();

		duplicates = new ReportTableModel(reportTableModel.getAllColName(),true,true);
				
		// Sanity check and Init over
		
		for (int i = 0; i < rowCount; i++) {
			Object[] rowObjectArray;

			if (columnIndices != null) {
				rowObjectArray = reportTableModel.getSelectedColRow(i, columnIndices);
			} else {
				rowObjectArray = reportTableModel.getRow(i);
			}

			StringBuilder row = new StringBuilder();
			
			// Create a string for row Values
			for (Object o : rowObjectArray) {
				if (o == null) {
					row.append("Null");
				} else {
					row.append(o.toString());
				}
			}

			BigInteger md5v = ResultsetToRTM.getMD5(row.toString());

			int md5RowIndex = md5array.indexOf(md5v);

			if (md5RowIndex == -1){ // not found so uniuqe row
				md5array.add(md5v);	
			} else { // not unique so mark for delete it
				deletionIndices.add(i);

				duplicates.addFillRow(reportTableModel.getRow(i));
			}
		}

		reportTableModel.removeMarkedRows(deletionIndices);
		
		return reportTableModel;
	}
	
	public ReportTableModel showDuplicateModel () {
		return duplicates;
	}
	
}
