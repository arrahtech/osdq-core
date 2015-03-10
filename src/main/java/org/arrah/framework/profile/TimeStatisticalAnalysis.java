package org.arrah.framework.profile;

/***********************************************
 *     Copyright to Arrah Technology 2013      *
 *     http://www.arrahtec.org                 *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* 
 * This file is used to analyze date from array
 * and put the data into reporttableModel
 * The date value is taken in long - number
 * of milliseconds since January 1, 1970, 00:00:00 
 * GMT represented by this Date object.
 * 
 */


import java.util.Arrays;
import java.util.Date;

import org.arrah.framework.ndtable.ReportTableModel;


public class TimeStatisticalAnalysis {
	private Long[] _colObj;
	private ReportTableModel freq_t = new ReportTableModel(new String[] { "Date Value",
			"Frequency", "% Freq." }, false, true);
	private ReportTableModel range_t = new ReportTableModel(new String[] {
			"Date Range Metric", "Metric Value" });
	private ReportTableModel perc_t = new ReportTableModel(new String[] { "Percentile %",
			"Date Upper Value", "Records Below" });

	private double count, sum , avg;
	private double variance = 0, aad = 0;
	
	private double[] perv_a = new double[21]; // To store value
	private long[] perc_a = new long[21];

	public TimeStatisticalAnalysis(Long[] colValue) {
		if ( colValue.length <= 0) return;
		
		_colObj = colValue;
		for (int i=0; i < colValue.length ; i++)
			sum += _colObj[i];
		Arrays.sort(_colObj);
		analyseValue();
	}

	public void analyseValue() {
		count = _colObj.length;
		avg = sum / count;
		int freq_c = 1, c = 0;
		Object prev_obj = null, curr_obj = null;

		// For Number Analysis
		int arr_i = 0;
		int dataset_c = 1;

		perc_a[0] = Math.round(count / 100);
		if (perc_a[0] == 0) {
			arr_i = 1;
			perv_a[0] = 0;
		}

		for (int i = 1; i < 20; i++) {
			perc_a[i] = Math.round(5 * i * count / 100);
			if (perc_a[i] == 0) {
				arr_i++;
				perv_a[i] = 0;
			}
		}
		perc_a[20] = Math.round(99 * count / 100);
		if (perc_a[20] == 0) {
			arr_i = 21;
			perv_a[20] = 0;
		}

		/* Start the loop */
		for (c = 0; c < count; c++) {
			curr_obj = _colObj[c];
			if (curr_obj.equals(prev_obj))
				freq_c++;
			else {
				// Frequency  Analysis addition
				if (prev_obj != null)
					freq_t.addFillRow(new Object[] { new Date((Long)prev_obj),
							new Integer(freq_c),
							new Double(freq_c / count * 100) });
				freq_c = 1;
				prev_obj = curr_obj;
			}
			// Advance Analysis goes here if is a Number


				double d = ((Number) curr_obj).doubleValue();
				if ((arr_i < 21) == true && dataset_c == perc_a[arr_i]) {
					while (arr_i < 20 && perc_a[arr_i + 1] == perc_a[arr_i]) {
						perv_a[arr_i] = d;
						arr_i++;
					}
					perv_a[arr_i] = d;
					arr_i++;
				}

				aad += Math.abs(getDayVal((long)(d - avg))) / count;
				variance += Math.pow(getDayVal((long)(d - avg)), 2) / (count - 1); 


				dataset_c++;
			// end of Advance analysis
		} // Insert last value
		if (prev_obj != null)
			freq_t.addFillRow(new Object[] { new Date((Long)prev_obj), new Integer(freq_c),
					new Double(freq_c / count * 100) });

		fillDataIntoTable();
	}

	
	public ReportTableModel getFrequencyTable() {
		return freq_t;
	}

	public ReportTableModel getRangeTable() {
		return range_t;
	}

	public ReportTableModel getPercTable() {
		return perc_t;
	}
	public Object[] getColObject() {
		return _colObj;
	}


	private void fillDataIntoTable() {
		Date localdate = new Date();

		range_t.addFillRow(new String[] { "Total Record Count", String.valueOf(count) });
		
		localdate.setTime(_colObj[_colObj.length - 1]);
		range_t.addFillRow(new String[] { "Maximum Date ",localdate.toString()});
		
		localdate.setTime(_colObj[0]);
		range_t.addFillRow(new String[] { "Minimum Date ",localdate.toString()});

		double dayval = getDayVal (_colObj[_colObj.length - 1] -_colObj[0] );
		range_t.addFillRow(new String[] {
					"Range(Max-Min) in Days", String.valueOf(dayval)});

		range_t.addRow();
		localdate.setTime((long)avg);
		range_t.addFillRow(new String[] { "Mean Value",localdate.toString()});
		
			range_t.addFillRow(new String[] { "Avg. Absolute Dev.(AAD) in Days",
					Double.toString(aad) });
			range_t.addFillRow(new String[] { "Variance in Days",
					Double.toString(variance) });
			range_t.addFillRow(new String[] { "Std. Dev.(SD) in Days",
					Double.toString(Math.sqrt(variance)) });
			range_t.addFillRow(new String[] { "Std. Error of Mean(SE) in Days",
					Double.toString(Math.sqrt(variance) / Math.sqrt(count)) });

			range_t.addRow();
			localdate.setTime((_colObj[_colObj.length - 1] + _colObj[0])/2);
			range_t.addFillRow(new String[] {
					"Mid Range Date Value",localdate.toString()});
			
			localdate.setTime((long)(perv_a[0] + perv_a[20]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(1%-99%)",localdate.toString()});
			localdate.setTime((long)(perv_a[1] + perv_a[19]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(5%-95%)",localdate.toString()});
			localdate.setTime((long)(perv_a[2] + perv_a[18]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(10%-90%)",localdate.toString()});
			localdate.setTime((long)(perv_a[3] + perv_a[17]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(15%-85%)",localdate.toString()});
			localdate.setTime((long)(perv_a[4] + perv_a[16]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(20%-80%)",localdate.toString()});
			localdate.setTime((long)(perv_a[5] + perv_a[15]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(25%-75%)",localdate.toString()});
			localdate.setTime((long)(perv_a[6] + perv_a[14]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(30%-70%)",localdate.toString()});
			localdate.setTime((long)(perv_a[7] + perv_a[13]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(35%-65%)",localdate.toString()});
			localdate.setTime((long)(perv_a[8] + perv_a[12]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(40%-60%)",localdate.toString()});
			localdate.setTime((long)(perv_a[9] + perv_a[11]) / 2);
			range_t.addFillRow(new String[] { "Mid Range(45%-55%)",localdate.toString()});
					
			// Add Percentile Analysis
			localdate.setTime((long)perv_a[0]);
			perc_t.addFillRow(new String[] { "1", localdate.toString(),
					Long.toString(perc_a[0]) });
			
			for (int i = 1; i < 20; i++) {
				localdate.setTime((long)perv_a[i]);
				perc_t.addFillRow(new String[] { Integer.toString(i * 5),
						localdate.toString(), Long.toString(perc_a[i]) });
			}
			localdate.setTime((long)perv_a[20]);
			perc_t.addFillRow(new String[] { "99", localdate.toString(),
					Long.toString(perc_a[20]) });
	} // End of fillValues
	
	public static double getDayVal ( long milliSec) {
		return milliSec/(1000*60*60*24); // day count
		
	}

} // End of class
