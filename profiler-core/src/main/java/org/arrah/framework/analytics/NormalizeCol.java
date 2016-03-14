package org.arrah.framework.analytics;

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
 * This file is used for creating normalized 
 * columns. It will take input columns and output
 * Normalized columns
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.profile.StatisticalAnalysis;


public class NormalizeCol {

	public NormalizeCol() {
		
	} // Constructor
	
	// This function will a inputList and return normalized list
	public static ArrayList<Double> zeroNormal(ArrayList<Number> inputCol) {
		int colLen = inputCol.size();
		Number[] intemediate = new Number[colLen];
		intemediate = 	inputCol.toArray(intemediate);
		Arrays.sort(intemediate); // Ascending order
		
		Double min = (Double) intemediate[0]; // min value in sorted array
		Double max = (Double) intemediate[colLen -1]; // max value in sorted array
		
		ArrayList<Double> outnormal = new ArrayList<Double>();
		for (Number i: inputCol) {
			Double factor = ((Double)i - min )/(max - min);
			outnormal.add(factor);
		}
		 
		 return outnormal;
	}
	
	// This function will return a 0 - 1 normalized form
	public static ReportTableModel zeroNormal(ReportTableModel rtm, int inputIndex, int outPutIndex) {
		Vector<Double> inputData = rtm.getColDataVD(inputIndex); // Null Skipped value
		int colLen = inputData.size();
		
		Number[] intemediate = new Number[colLen];
		intemediate = 	inputData.toArray(intemediate);
		Arrays.sort(intemediate); // Ascending order
		
		Double min = (Double) intemediate[0]; // min value in sorted array
		Double max = (Double) intemediate[colLen -1]; // max value in sorted array
		
		int rowC = rtm.getModel().getRowCount();
		
		for (int i=0; i < rowC; i++) {
			Object d = rtm.getModel().getValueAt(i, inputIndex);
			
			if (d == null ) {
				rtm.getModel().setValueAt(d,i, outPutIndex);
				continue;
			}
			Double factor = 0.0d;
			if (d instanceof Number) 
				factor = ((Double)d - min )/(max - min);
			else if (d instanceof String) {
				try {
						factor = Double.parseDouble(d.toString());
						factor = (factor - min )/(max - min);
					} catch (Exception e) {
						factor = null;
					}
				}
			rtm.getModel().setValueAt(factor,i, outPutIndex);
			}
		 
		 return rtm;
	}
	
	// This function will return zscore normalization
		public static ReportTableModel zscoreNormal(ReportTableModel rtm, int inputIndex, int outPutIndex) {
			
			Vector<Double> inputData = rtm.getColDataVD(inputIndex); // Null Skipped value
			StatisticalAnalysis sa = new StatisticalAnalysis(inputData.toArray());
			
			Double mean = sa.getMean(); // Mean value of Data
			Double sdev = sa.getSDev(); // Standard deviation
			
			int rowC = rtm.getModel().getRowCount();
			
			for (int i=0; i < rowC; i++) {
				Object d = rtm.getModel().getValueAt(i, inputIndex);
				
				if (d == null ) {
					rtm.getModel().setValueAt(d,i, outPutIndex);
					continue;
				}
				Double factor = 0.0d;
				if (d instanceof Number) 
					factor = ((Double)d - mean )/sdev;
				else if (d instanceof String) {
					try {
							factor = Double.parseDouble(d.toString());
							factor = (factor - mean )/sdev;
						} catch (Exception e) {
							factor = null;
						}
					}
				rtm.getModel().setValueAt(factor,i, outPutIndex);
				}
			 
			 return rtm;
		}
		
		// This function will return mean and std normalization
		public static ReportTableModel meanStdNormal(ReportTableModel rtm, int inputIndex, int outPutIndex, int std) {
			
			Vector<Double> inputData = rtm.getColDataVD(inputIndex); // Null Skipped value
			StatisticalAnalysis sa = new StatisticalAnalysis(inputData.toArray());
			
			Double mean = sa.getMean(); // Mean value of Data
			Double sdev = sa.getSDev(); // Standard deviation
			
			int rowC = rtm.getModel().getRowCount();
			
			for (int i=0; i < rowC; i++) {
				Object d = rtm.getModel().getValueAt(i, inputIndex);
				
				if (d == null ) {
					rtm.getModel().setValueAt(d,i, outPutIndex);
					continue;
				}
				Double factor = 0.0d;
				if (d instanceof Number) {
					if (std == 0 ) // 0 for Std; 1 for mean
						factor = ((Double)d)/sdev;
					else if (std == 1 )
						factor = ((Double)d)/mean;
					else 
						factor = ((Double)d) - mean;
				}
				else if (d instanceof String) {
					try {
							factor = Double.parseDouble(d.toString());
							if (std == 0 ) // 0 for Std; 1 for mean
								factor = factor/sdev;
							else if (std == 1)
								factor = factor/mean;
							else 
								factor = factor - mean;
						} catch (Exception e) {
							factor = null;
						}
					}
				rtm.getModel().setValueAt(factor,i, outPutIndex);
				}
			 
			 return rtm;
		}
		
	// This function will return distance from mean in terms of std deviation
	// value - mean / std
	public static ReportTableModel distStdNormal(ReportTableModel rtm, int inputIndex, int outPutIndex) {
		
		Vector<Double> inputData = rtm.getColDataVD(inputIndex); // Null Skipped value
		StatisticalAnalysis sa = new StatisticalAnalysis(inputData.toArray());
		
		Double mean = sa.getMean(); // Mean value of Data
		Double sdev = sa.getSDev(); // Standard deviation
		
		int rowC = rtm.getModel().getRowCount();
		
		for (int i=0; i < rowC; i++) {
			Object d = rtm.getModel().getValueAt(i, inputIndex);
			
			if (d == null ) {
				rtm.getModel().setValueAt(d,i, outPutIndex);
				continue;
			}
			Double factor = 0.0d;
			if (d instanceof Number) {
					factor = (((Double)d) - mean )/ sdev;
			}
			else if (d instanceof String) {
				try {
						factor = Double.parseDouble(d.toString());
							factor = (factor - mean) /sdev;
					} catch (Exception e) {
						factor = null;
					}
				}
			rtm.getModel().setValueAt(factor,i, outPutIndex);
			}
		 
		 return rtm;
	}
	
 // Following functions will be used for Rounding, Ceiling and Roofing
	
	// This function will return rounding of input number
	public static ReportTableModel roundingIndex(ReportTableModel rtm, int inputIndex, int outPutIndex, int roundtingType) {
		int rowC = rtm.getModel().getRowCount();
		
		for (int i=0; i < rowC; i++) {
			Object d = rtm.getModel().getValueAt(i, inputIndex);
			
			if (d == null ) {
				rtm.getModel().setValueAt(d,i, outPutIndex);
				continue;
			}
			
			Double factor = 0.0d;
			if (d instanceof Number) {
				switch(roundtingType) {
				case 1: // Rounding
					factor = (double) Math.round((Double)d);
					break;
				case 2: // Ceiling
					factor = Math.ceil((Double)d);
					break;
				case 3: // Flooring
					factor = Math.floor((Double)d);
					break;
				case 4: // Nearest 0
					factor = (double) (Math.round((Double)(d)/10) * 10);
					break;
					
				default:
					break;
				}
			}
			else if (d instanceof String) {
				try {
						d = Double.parseDouble(d.toString());
						switch(roundtingType) {
						case 1: // Rounding
							factor = (double) Math.round((Double)d);
							break;
						case 2: // Ceiling
							factor = Math.ceil((Double)d);
							break;
						case 3: // Flooring
							factor = Math.floor((Double)d);
							break;
						case 4: // Nearest 0
							factor = (double) (Math.round((Double)(d)/10) * 10);
							break;
							
						default:
							break;
						}
						
					} catch (Exception e) {
						System.out.println("Exception:"+ e.getLocalizedMessage());
						factor = null;
					}
			}
			rtm.getModel().setValueAt(factor,i, outPutIndex);
		}
		 
		 return rtm;
	}
	
	
} // End of NormalizeCol
