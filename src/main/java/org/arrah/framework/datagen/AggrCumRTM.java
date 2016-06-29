package org.arrah.framework.datagen;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2014    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for create aggregate data
 * and cumulative data - aggregate data sum,
 * count, avg, min and max
 * Cumulative data sum, count and avg
 * Utilitiies functions are also supported here
 */

import java.util.Arrays;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.profile.StatisticalAnalysis;

public class AggrCumRTM {

	public AggrCumRTM() {
		
	} // Constructor
	
	/* Sum for column Index which will come in Vector */
	public static double getSum(Vector<Double> colList) {
		double sum=0.00D;
		
		if (colList == null || colList.size() == 0)
			return sum;
		
		for (double colVal:colList) {
			sum +=colVal;
		}
		return sum;
	}
	
	/* Avg for column Index which will come in Vector */
	public static double getAverage(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return 0.00D;
		
		long count = colList.size();
		double sum = getSum(colList);
		
		if (sum != 0.00D && count != 0)
			return  (sum/count);
		else
			return sum;
	}
	
	/* get Max and Min value for ColIndex which will come in Vector */
	public static double[] getMinMax(Vector<Double> colList) {
		double[] minMax = new double[2];
		minMax[0] = 0.00D; minMax[1] = 0.00D;
		
		if (colList == null || colList.size() == 0)
			return minMax;
		
		double sum = getSum(colList);
		long count = colList.size();
		
		if (sum == 0.00D || count == 0) {
			return minMax;
		}
		
		Double[] newList = new Double[colList.size()];
		newList = colList.toArray(newList);
		Arrays.sort(newList); // Ascending order
		minMax[0] = newList[0];
		minMax[1] = newList[newList.length - 1];
		
		return minMax;
		
	}
	
	/* Cumulative Sum for column Index which will come in Vector */
	public static Vector<Double> getCumSum(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		Vector<Double> cumSum = new Vector<Double>();
		double csum = 0.00D;
		
		for (double colVal:colList) {
			csum +=colVal;
			cumSum.add(csum);
		}
		return cumSum;
	}
	
	/* Cumulative Avg for column Index which will come in Vector */
	public static Vector<Double> getCumAvg(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		long count = colList.size();
		Vector<Double> cumAvg = new Vector<Double>();
		double csum = 0.00D;
		
		for (int i = 1; i <= count; i++) { // dividing by zero will NaN
			csum +=colList.get(i-1);
			cumAvg.add(csum/i);
		}
		
		return cumAvg;
	}
	
	/* Get next value value in vector Vector */
	public static Vector<Double> putNextVal(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		long count = colList.size();
		Vector<Double> nextVal = new Vector<Double>();
		
		for (int i = 0; i < (count -1) ; i++) { // Last value will be 0
			nextVal.add(colList.get(i+1));
		}
		nextVal.add(0.0D); // no next so put zero
		
		return nextVal;
	}
	
	/* Get prev value value in vector Vector */
	public static Vector<Double> putPrevVal(Vector<Double> colList) {
		
		if (colList == null || colList.size() == 0)
			return colList;
		
		long count = colList.size();
		Vector<Double> prevVal = new Vector<Double>();
		prevVal.add(0.0D); // no prev so put zero
		
		for (int i = 1; i < count  ; i++) { // First value will be 0
			prevVal.add(colList.get(i-1));
		}
		
		return prevVal;
	}
	
	/* Get Pearson correlation  */
	public static Double getPCorrelation(Vector<Double> one, Vector<Double> two) {
		
		StatisticalAnalysis sa_1 = new StatisticalAnalysis(one.toArray());
		Double mean_1 = sa_1.getMean(); // Mean value of Data
		
		StatisticalAnalysis sa_2 = new StatisticalAnalysis(two.toArray());
		Double mean_2 = sa_2.getMean(); // Mean value of Data
		
		double num=0.0D,denum_a=0.0D, denum_b=0.0D;
		
		int rowC_one = one.size(); // select the lowest number
		int rowC_two = two.size();
		int rowC = (rowC_one > rowC_two ) ? rowC_two: rowC_one;
		
		for (int i=0; i < rowC; i++) {
			double a = one.get(i) - mean_1;
			double b = two.get(i) - mean_2;
			num = num + a*b;
			denum_a = denum_a + a*a;
			denum_b = denum_b + b*b;
		}
		return num/(Math.sqrt(denum_a) * Math.sqrt(denum_b));
	}
	
	/* This is utility function to get ReportTableModel data into vector */
	public static Vector<Double> getColumnNumberData(ReportTableModel rpt, int columnIndex) {
		Vector<Double> columnDataVector = new Vector<Double>();
		int rowCount = rpt.getModel().getRowCount();
		
		for (int i=0; i < rowCount; i++ ){
			Object obj = rpt.getModel().getValueAt(i, columnIndex);
			if (obj == null) columnDataVector.add(0D);
			try {
				Double d = Double.parseDouble(obj.toString());
				columnDataVector.add(d);
			} catch (Exception e) {
				columnDataVector.add(0D);
			}
		}
		
		return columnDataVector;	
	}
	

	
} // End of AggrCumRTM
