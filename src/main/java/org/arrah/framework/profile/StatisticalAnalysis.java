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
 * This file is used to analyze data from array
 * and put the data into reporttableModel
 */


import java.util.Arrays;
import java.util.Vector;


import org.arrah.framework.ndtable.ReportTableModel;

public class StatisticalAnalysis {
	private Object[] _colObj;
	private boolean isNumber = true;
	private ReportTableModel freq_t = new ReportTableModel(new String[] { "Record Value",
			"Frequency", "% Freq." }, false, true);
	private ReportTableModel range_t = new ReportTableModel(new String[] {
			"Range Metric", "Metric Value" });
	private ReportTableModel perc_t = new ReportTableModel(new String[] { "Percentile %",
			"Record Upper Value", "Samples Below" });;

	private double count, sum, avg;
	private double variance = 0, aad = 0, skew = 0, kurt = 0;
	private double[] perv_a = new double[21]; // To store value
	private long[] perc_a = new long[21];

	public StatisticalAnalysis(Object[] colValue) {
		int colC = colValue.length;
		if (colC <= 0) {
			System.out.println("No Value for Analysis. Empty Column Data");
			return;
		}
		Vector<Object> vc = new Vector<Object>();
		int index = 0;
		while (index < colC) {
			if (colValue[index] != null && colValue[index].toString().isEmpty() == false) {
				vc.add(colValue[index]);
				if (isNumber == true && colValue[index] instanceof Number)
					sum += ((Number) colValue[index]).doubleValue();
				else
					isNumber = false;
			}
			index++;

		}
		_colObj = vc.toArray();
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
					freq_t.addFillRow(new Object[] { prev_obj,
							new Integer(freq_c),
							new Double(freq_c / count * 100) });
				freq_c = 1;
				prev_obj = curr_obj;
			}
			// Advance Analysis goes here if is a Number
			if (isNumber == true) {

				double d = ((Number) curr_obj).doubleValue();
				if ((arr_i < 21) == true && dataset_c == perc_a[arr_i]) {
					while (arr_i < 20 && perc_a[arr_i + 1] == perc_a[arr_i]) {
						perv_a[arr_i] = d;
						arr_i++;
					}
					perv_a[arr_i] = d;
					arr_i++;
				}

				aad += Math.abs(d - avg) / count;
				variance += Math.pow(d - avg, 2) / (count - 1);
				skew += Math.pow(d - avg, 3);
				kurt += Math.pow(d - avg, 4);

				dataset_c++;
			} // end of Advance analysis
		} // Insert last value
		if (prev_obj != null)
			freq_t.addFillRow(new Object[] { prev_obj, new Integer(freq_c),
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
	public boolean isObjNumber() {
		return isNumber;
	}
	public double getMean() {
		return avg;
	}
	public double getVariance() {
		return variance;
	}
	public double getSDev() {
		return Math.sqrt(variance);
	}
	public Object getMinObject() {
		return _colObj[0];
	}
	public Object getMaxObject() {
		return _colObj[_colObj.length -1 ]; //max object
	}
	public double rangeObject () {
		if (_colObj[0] instanceof Number && _colObj[_colObj.length -1 ] instanceof Number) {
			return ((Number)(_colObj[_colObj.length -1 ])).doubleValue() - ((Number)(_colObj[0])).doubleValue();
		}
		return 0D;
		
	}

	private void fillDataIntoTable() {

		range_t.addFillRow(new String[] { "Total Record Count", String.valueOf(count) });
		range_t.addFillRow(new String[] { "Maximum Value ",
				String.valueOf(_colObj[_colObj.length - 1]) });
		range_t.addFillRow(new String[] { "Minimum Value ",
				String.valueOf(_colObj[0]) });

		if (isNumber == true) {
			range_t.addFillRow(new String[] {
					"Range(Max-Min)",
					String.valueOf(((Number) _colObj[_colObj.length - 1])
							.doubleValue()
							- ((Number) _colObj[0]).doubleValue()) });
			range_t.addFillRow(new String[] { "Total Record Sum", String.valueOf(sum) });
			range_t.addRow();
			range_t.addFillRow(new String[] { "Mean Value", String.valueOf(avg) });
			range_t.addFillRow(new String[] { "Avg. Absolute Dev.(AAD)",
					Double.toString(aad) });
			range_t.addFillRow(new String[] { "Variance",
					Double.toString(variance) });
			range_t.addFillRow(new String[] { "Std. Dev.(SD)",
					Double.toString(Math.sqrt(variance)) });
			range_t.addFillRow(new String[] { "Std. Error of Mean(SE)",
					Double.toString(Math.sqrt(variance) / Math.sqrt(count)) });
			range_t.addFillRow(new String[] {
					"Skewness",
					Double.toString(skew
							/ ((count - 1) * Math.pow(variance, 1.5))) });
			range_t.addFillRow(new String[] {
					"Kurtosis",
					Double.toString(kurt
							/ ((count - 1) * Math.pow(variance, 2))) });

			range_t.addRow();
			range_t.addFillRow(new String[] {
					"Mid Range Value",
					String.valueOf((((Number) _colObj[_colObj.length - 1])
							.doubleValue() + ((Number) _colObj[0])
							.doubleValue()) / 2) });
			
			if (perc_a[0] > 0)
			range_t.addFillRow(new String[] { "Mid Range(1%-99%)",
					Double.toString((perv_a[0] + perv_a[20]) / 2) });
			if (perc_a[1] > 0)
			range_t.addFillRow(new String[] { "Mid Range(5%-95%)",
					Double.toString((perv_a[1] + perv_a[19]) / 2) });
			if (perc_a[2] > 0)
			range_t.addFillRow(new String[] { "Mid Range(10%-90%)",
					Double.toString((perv_a[2] + perv_a[18]) / 2) });
			if (perc_a[3] > 0)
			range_t.addFillRow(new String[] { "Mid Range(15%-85%)",
					Double.toString((perv_a[3] + perv_a[17]) / 2) });
			if (perc_a[4] > 0)
			range_t.addFillRow(new String[] { "Mid Range(20%-80%)",
					Double.toString((perv_a[4] + perv_a[16]) / 2) });
			if (perc_a[5] > 0)
			range_t.addFillRow(new String[] { "Mid Range(25%-75%)",
					Double.toString((perv_a[5] + perv_a[15]) / 2) });
			if (perc_a[6] > 0)
			range_t.addFillRow(new String[] { "Mid Range(30%-70%)",
					Double.toString((perv_a[6] + perv_a[14]) / 2) });
			if (perc_a[7] > 0)
			range_t.addFillRow(new String[] { "Mid Range(35%-65%)",
					Double.toString((perv_a[7] + perv_a[13]) / 2) });
			if (perc_a[8] > 0)
			range_t.addFillRow(new String[] { "Mid Range(40%-60%)",
					Double.toString((perv_a[8] + perv_a[12]) / 2) });

			// Add Percentile Analysis
			perc_t.addFillRow(new String[] { "1", Double.toString(perv_a[0]),
					Long.toString(perc_a[0]) });
			for (int i = 1; i < 20; i++)
				perc_t.addFillRow(new String[] { Integer.toString(i * 5),
						Double.toString(perv_a[i]), Long.toString(perc_a[i]) });
			perc_t.addFillRow(new String[] { "99", Double.toString(perv_a[20]),
					Long.toString(perc_a[20]) });
		}
	} // End of fillDataIntoTable

} // End of class
