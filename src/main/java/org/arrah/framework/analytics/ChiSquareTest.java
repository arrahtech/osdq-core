package org.arrah.framework.analytics;

/**************************************************
*     Copyright to Vivek Singh        2017        *
*                                                 *
* Any part of code or file can be changed,        *
* redistributed, modified with the copyright      *
* information intact                              *
*                                                 *
* Author$ : Vivek Singh                           *
*                                                 *
**************************************************/

/*
* This class provides chi square test for
* independence where input is ReportTableModel
*
*/

import java.util.Vector;

import org.arrah.framework.datagen.AggrCumRTM;
import org.arrah.framework.ndtable.ReportTableModel;

public class ChiSquareTest {
	ReportTableModel _rtm = null;
	private double _significanceLevel = 0.05D; // it can be customized. Most people this value 
	private int degreeOfFree = 0;
	
	
	public ChiSquareTest ( ReportTableModel rtm) {
		_rtm = rtm;
	}
	public ChiSquareTest ( ReportTableModel rtm, float sigl) {
		_rtm = rtm;
		set_significanceLevel(sigl);
	}
	public double getChiSquare() {
		int rowN = _rtm.getModel().getRowCount();
		int colN = _rtm.getModel().getColumnCount();
		
		setDegreeOfFreedom((rowN -1 )* (colN -3)); // there are two extra cols in rtm
		
		Vector<Double> populationData = AggrCumRTM.getColumnNumberData(_rtm, colN -1);
		double population = AggrCumRTM.getSum(populationData);
		double chisquare = 0.000D;
		
		for (int i=1; i < (colN -1) ; i++) { // last column is total count
			Vector<Double> colData = AggrCumRTM.getColumnNumberData(_rtm, i);
			double colSum = AggrCumRTM.getSum(colData);
			
			for (int j=0; j < rowN; j++ ) {
				double rowSum = new Double(_rtm.getModel().getValueAt(j, colN -1).toString()); //last column value
				double expectedFreq = colSum * rowSum / population;
				//System.out.println(rowSum + ":"+colSum +":" +population);
				double observedFreq = new Double(_rtm.getModel().getValueAt(j, i).toString());
				double singleChi = ((expectedFreq - observedFreq) * (expectedFreq - observedFreq))/expectedFreq;
				//System.out.println(expectedFreq + ":"+observedFreq );
				chisquare += singleChi;
			}
		}
		return chisquare;
	}
	public int getDegreeOfFreedom() {
		return degreeOfFree;
	}
	public void setDegreeOfFreedom(int degreeOfFree) {
		this.degreeOfFree = degreeOfFree;
	}
	public double get_significanceLevel() {
		return _significanceLevel;
	}
	public void set_significanceLevel(double _significanceLevel) {
		this._significanceLevel = _significanceLevel;
	}
	
	

} // end of ChiSquareTest
