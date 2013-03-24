package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2013      *
 *     http://www.arrah.in                     *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* 
 * This file is used to analyze data from File
 */

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

public class StatisticalAnalysisPanel {
	private Object[] _colObj;
	private boolean isNumber = true;
	private ReportTable freq_t = new ReportTable(new String[] { "Record Value",
			"Frequency", "% Freq." }, false, true);
	private ReportTable range_t = new ReportTable(new String[] {
			"Range Metric", "Metric Value" });
	private ReportTable perc_t = null;

	private double count, sum, min, max, avg;
	private double variance = 0, aad = 0, skew = 0, kurt = 0;
	private double[] perv_a = new double[21]; // To store value
	private long[] perc_a = new long[21];

	public StatisticalAnalysisPanel(Object[] colValue) {
		int colC = colValue.length;
		Vector<Object> vc = new Vector<Object>();
		int index = 0;
		while (index < colC) {
			if (colValue[index] != null) {
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
				// Freq Analysis addition
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

	private class FileScatter extends ScatterPlotterPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FileScatter() {
		};

		public Vector<Double> fillValues() {

			final int gc = getGC();
			if (gc == 0)
				return null;

			int counter = 0;
			double d = 0;
			double sum = 0;
			Vector<Double> vc = new Vector<Double>(20, 5);
			int i = 0;
			int colC = _colObj.length;
			try {
				for (int c = 0; c < colC; c++) {

					String colV_s = _colObj[c].toString();
					if (colV_s.equals(""))
						continue;
					d = Double.valueOf(colV_s).doubleValue();
					counter++;
					if (counter <= gc) {
						sum += d;
						if (counter != gc)
							continue;
					}
					double avg = sum / counter;
					sum = 0;
					counter = 0;
					vc.add(i++, new Double(avg));
				}
			} catch (NumberFormatException e) {
				counter = 0;
				ConsoleFrame
						.addText("\n ERROR: Could not fill data into Cluster Chart");
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Error Message", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			// After loop breaks
			// Rounding off the values
			if (counter != 0 && Math.round((float) counter / gc) > 0) {
				double avg = sum / counter;
				vc.add(i, new Double(avg));
			}

			return vc;
		}
	}

	public ReportTable getFrequencyTable() {
		return freq_t;
	}

	public ReportTable getRangeTable() {
		return range_t;
	}

	public ReportTable getPercTable() {
		return perc_t;
	}

	private void fillDataIntoTable() {

		range_t.addFillRow(new String[] { "Sample Size", String.valueOf(count) });
		range_t.addFillRow(new String[] { "Maximum ",
				String.valueOf(_colObj[_colObj.length - 1]) });
		range_t.addFillRow(new String[] { "Minimum ",
				String.valueOf(_colObj[0]) });

		if (isNumber == true) {
			range_t.addFillRow(new String[] {
					"Range",
					String.valueOf(((Number) _colObj[_colObj.length - 1])
							.doubleValue()
							- ((Number) _colObj[0]).doubleValue()) });
			range_t.addFillRow(new String[] { "Summation", String.valueOf(sum) });
			range_t.addRow();
			range_t.addFillRow(new String[] { "Mean", String.valueOf(avg) });
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
					"Mid Range",
					String.valueOf((((Number) _colObj[_colObj.length - 1])
							.doubleValue() + ((Number) _colObj[0])
							.doubleValue()) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(1%-99%)",
					Double.toString((perv_a[0] + perv_a[20]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(5%-95%)",
					Double.toString((perv_a[1] + perv_a[19]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(10%-90%)",
					Double.toString((perv_a[2] + perv_a[18]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(15%-85%)",
					Double.toString((perv_a[3] + perv_a[17]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(20%-80%)",
					Double.toString((perv_a[4] + perv_a[16]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(25%-75%)",
					Double.toString((perv_a[5] + perv_a[15]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(30%-70%)",
					Double.toString((perv_a[6] + perv_a[14]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(35%-65%)",
					Double.toString((perv_a[7] + perv_a[13]) / 2) });
			range_t.addFillRow(new String[] { "Mid Range(40%-60%)",
					Double.toString((perv_a[8] + perv_a[12]) / 2) });

			// Add Percentile Analysis
			perc_t = new ReportTable(new String[] { "Percentile %",
					"Record Upper Value", "Samples Below" });
			perc_t.addFillRow(new String[] { "1", Double.toString(perv_a[0]),
					Long.toString(perc_a[0]) });
			for (int i = 1; i < 20; i++)
				perc_t.addFillRow(new String[] { Integer.toString(i * 5),
						Double.toString(perv_a[i]), Long.toString(perc_a[i]) });
			perc_t.addFillRow(new String[] { "99", Double.toString(perv_a[20]),
					Long.toString(perc_a[20]) });
		}
	}

	public void createAndShowGUI() {

		final FileScatter spp = new FileScatter();
		final JTabbedPane _ta_p = new JTabbedPane() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				super.paint(g);
				if (this.getSelectedIndex() != 3)
					return;
				spp.setSlideBar();
			}
		};
		_ta_p.addTab("Frequency Analysis", null, freq_t, "Frequency Analyis");
		_ta_p.addTab("Variation Analysis", null, range_t, "Variation Analysis");

		if (isNumber == true) {
			_ta_p.addTab("Percentile Analysis", null, perc_t,"Percentile Analysis");
			_ta_p.addTab("Cluster Analysis", null, spp, "Cluster Analysis");
		}

		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		final JFrame frame = new JFrame("Advance Number Analyis") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				super.paint(g);
				if (_ta_p.getSelectedIndex() != 3)
					return;
				spp.g_p.showBubbleChart();
				spp.setSlideBar();
			}
		};
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create and set up the content pane.
		frame.getContentPane().add(_ta_p, BorderLayout.CENTER);

		// Display the window.
		frame.setLocation(125, 75);
		frame.pack();
		frame.setVisible(true);

	}
}
