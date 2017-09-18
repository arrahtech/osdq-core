package org.arrah.framework.analytics;

import java.util.Vector;
import org.arrah.framework.analytics.FuzzyVector;

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
* This class provides set analysis
* that can be used in venn diagram etc
*
*/

public class SetAnalysis {
	private String errstr="";
	private Vector<Object> smallSet, bigSet;
	
	
	public SetAnalysis (Vector<Object> setA, Vector<Object> setB) throws NullPointerException { //Default Constructor
		if (setA == null || setB == null ) {
			NullPointerException e = new NullPointerException();
			throw e;
		}
		if ( setA.size() > setB.size() ){
			smallSet = setB;
			bigSet = setA;
		} else {
			smallSet = setA;
			bigSet = setB;
		}
		
	}
	//default constructor
	public SetAnalysis () throws NullPointerException { //Default Constructor
		
	}
	
	// This function will return intersection set
	public Vector<Object> getIntersection () {
		Vector<Object> resultSet = null;
		int ilen = smallSet.size();
		if (ilen == 0 || bigSet.size() == 0 ) { // no intersection
			errstr = "Not Valid sets for Intersection";
			return resultSet;
		}
		resultSet = new Vector<Object>();
		
		for (int i=0 ; i < ilen; i++ ) {
			Object o = smallSet.get(i);
			if (bigSet.indexOf(o) != -1 ) // it is  found in bigger set
				resultSet.add(o);
		}
		return resultSet;
	}
	
	// This function will return intersection set
	public Vector<Object> getIntersection (float distance) {
		Vector<Object> resultSet = null;
		int ilen = smallSet.size();
		if (ilen == 0 || bigSet.size() == 0 ) { // no intersection
			errstr = "Not Valid sets for Intersection";
			return resultSet;
		}
		resultSet = new Vector<Object>();
		FuzzyVector fz = new FuzzyVector(bigSet);
		Vector<Integer> mIFuzzySet = new Vector<Integer>();
		
		for (int i=0 ; i < ilen; i++ ) {
			Object o = smallSet.get(i);
			int matchedI = fz.indexOf(o,distance,0); // first satrt from  begining
			if (matchedI != -1  && mIFuzzySet.indexOf(matchedI) == -1) { // it is  found in bigger set and not already counted
				mIFuzzySet.add(matchedI);
				if (resultSet.indexOf(o) == -1 ) // if it not already added
					resultSet.add(o);
			} else {
				
			}
		}
		return resultSet;
	}
	
	// This function will return Union set
	public Vector<Object> getUnion () {
		Vector<Object> resultSet = null;
		int ilen = smallSet.size();
		if (ilen == 0 ) {
			errstr = "Not Valid sets for Union";
			return bigSet;
		}
		
		resultSet = new Vector<Object>();
		
		for (int i=0 ; i < ilen; i++ ) {
			Object o = smallSet.get(i);
			if (resultSet.indexOf(o) == -1 ) // it is not found in result set
				resultSet.add(o);
		}
		int ilenB = bigSet.size();
		for (int i=0 ; i < ilenB; i++ ) {
			Object o = bigSet.get(i);
			if (resultSet.indexOf(o) == -1 ) // it is not found in  result set
				resultSet.add(o);
		}
		
		return resultSet;
	}
	// We have to make fuzzy both sides and then take intersection
	public Vector<Object> getUnion (float distance) {
		Vector<Object> resultSetA = getUnion (smallSet, bigSet, distance);
		Vector<Object> resultSetB = getUnion (bigSet, smallSet, distance);
		if (resultSetA.size() == 0) return resultSetB;
		if (resultSetB.size() == 0) return resultSetA;
		if (resultSetA.size() >= resultSetB.size()) {
			bigSet = resultSetA;
			smallSet = resultSetB;
		} else {
			bigSet = resultSetB;
			smallSet = resultSetA;
		}
		
		return getIntersection();
	}
	
	// This function will return Union set with distance as input
	public Vector<Object> getUnion (Vector<Object> setA, Vector<Object> setB, float distance) {
		Vector<Object> resultSet = null;
		
		int ilen = setA.size();
		if (ilen == 0 ) {
			errstr = "Not Valid sets for Union";
			return setB;
		}
		
		int ilenB = setB.size();
		if (ilen == 0 ) {
			errstr = "Not Valid sets for Union";
			return setA;
		}
		
		resultSet = new Vector<Object>();
		
		for (int i=0 ; i < ilen; i++ ) {
			Object o = setA.get(i);
			if (resultSet.indexOf(o) == -1 ) // it is not found in result set
				resultSet.add(o);
		}
		
		FuzzyVector fz = new FuzzyVector(resultSet);
		
		for (int i=0 ; i < ilenB; i++ ) {
			Object o = setB.get(i);
			if (fz.indexOf(o,distance,0) == -1 ) // it is not found in  fuzzyvector set
				if (resultSet.indexOf(o) == -1 )  {// it is not found in  result set
					resultSet.add(o);
					fz.add(o);
				}
		}
		
		return resultSet;
	}
	// Order is important cartesian
	// This function will return cartesian set [first,second]
	public Vector<Object[]> getCartesian (Vector<Object> first, Vector<Object> second) {
		Vector<Object[]> resultSet = null;
		int ilen = first.size();
		int ilenB = second.size();
		if (ilen == 0 || ilenB == 0 ) {
			errstr = "Not Valid sets for Cartesian";
			return resultSet;
		}
		
		resultSet = new Vector<Object[]>();
		
		for (int i=0; i < ilen; i++ ) {
			for (int j=0; j < ilenB; j++ ) {
				Object[] cartS = new Object[2];
				cartS[0] = first.get(i);
				cartS[1] = second.get(j);
				resultSet.add(cartS);
			}
		}
		return resultSet;
	}
	
	// This function will return A- B set
	public Vector<Object> getDifference (Vector<Object> first, Vector<Object> second) {
		Vector<Object> resultSet = new Vector<Object>();
		int ilen = first.size();
		for (int i=0; i < ilen; i++ ) {
			Object o = first.get(i);
			if (second.indexOf(o) == -1 ) // it is not found in second
				resultSet.add(o);
		}
		
		errstr = "Difference Successful";
		return resultSet;
	}
	
	// This function will return A- B set with cosine distance
		public Vector<Object> getDifference (Vector<Object> first, Vector<Object> second, float distance) {
			Vector<Object> resultSet = new Vector<Object>();
			int ilen = first.size();
			FuzzyVector fz = new FuzzyVector(second);
			for (int i=0; i < ilen; i++ ) {
				Object o = first.get(i);
				if (fz.indexOf(o,distance,0) == -1 ) // it is not found in second
					resultSet.add(o);
			}
			
			errstr = "Difference Successful";
			return resultSet;
		}
	
	// This function will return true if A is subset of B
	public boolean isSubset(Vector<Object> first, Vector<Object> second) {
		int ilen = first.size();
		for (int i=0; i < ilen; i++ ) {
			Object o = first.get(i);
			if (second.indexOf(o) == -1 ) // it is not found in second
				return false;
		}
		
		errstr = "Subset Successful";
		return true;
	}
	
	public String getErrstr() {
		return errstr;
	}
	
	public void setSmallset(Vector<Object> setA) {
		smallSet = setA;
	}
	public void setBigset(Vector<Object> setA) {
		bigSet = setA;
	}
	
	// for testing
	public static void main(String[] args) {
		Vector<Object> first = new Vector<Object>();
		first.add("vivek");first.add("vivek16");first.add("abcd");first.add("vivek44");
		
		Vector<Object> second = new Vector<Object>();
		second.add("vivek36");second.add("vivekkk");second.add("abcd");second.add("vivek445");
		
		SetAnalysis seta = new SetAnalysis(first,second);
		//SetAnalysis seta = new SetAnalysis(second,first);
		//Vector<Object> finaltse  = seta.getIntersection(0.9f);
		Vector<Object> finaltse  = seta.getUnion();
		//Vector<Object> finaltse  = seta.getDifference(first, second,0.6f);

		for (Object a:finaltse)
			System.out.println(a.toString());
		
		
	}

	} // end of SetAnalysis
