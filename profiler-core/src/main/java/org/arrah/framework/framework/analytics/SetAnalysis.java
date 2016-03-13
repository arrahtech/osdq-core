package org.arrah.framework.analytics;

import java.util.Vector;

/**************************************************
*     Copyright to Vivek Singh        2016        *
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
			if (bigSet.indexOf(o) != -1 ) // it is not found in bigger set
				resultSet.add(o);
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
			if (resultSet.indexOf(o) != -1 ) // it is not found in small result set
				resultSet.add(o);
		}
		int ilenB = bigSet.size();
		for (int i=0 ; i < ilenB; i++ ) {
			Object o = bigSet.get(i);
			if (resultSet.indexOf(o) != -1 ) // it is not found in big result set
				resultSet.add(o);
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

	} // end of SetAnalysis
