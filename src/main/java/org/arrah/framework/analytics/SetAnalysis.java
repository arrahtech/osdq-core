package org.arrah.framework.analytics;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
		setA = new Vector<Object>(new HashSet<Object>(setA));// remove duplicates
		setB = new Vector<Object>(new HashSet<Object>(setB));
		
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
		Collections.sort(resultSet,COMPARABLE_COMAPRATOR);
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
		
		FuzzyVector fzb = new FuzzyVector(bigSet);
		fzb = deDupFuzzy(fzb,distance); // self dedup
		FuzzyVector fzs = new FuzzyVector(smallSet);
		fzs = deDupFuzzy(fzs,distance); // self dedup
		
		ilen = fzs.size();
		resultSet = new Vector<Object>();
		
		for (int i=0 ; i < ilen; i++ ) {
			Object o = fzs.get(i);
			int matchi = 0; // start from begining
			while ( (matchi = fzb.indexOf(o,distance,matchi))  != -1 ) {
				resultSet.add(o); // add from small set
				resultSet.add(fzb.get(matchi)); // add from large set
				matchi++;// from next index
				//System.out.println("Match:" + i + ":"+matchi);
			}
		}
		resultSet = deDupFuzzy(new FuzzyVector(resultSet),distance);
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
		
		Collections.sort(resultSet,COMPARABLE_COMAPRATOR);
		return resultSet;
	}
	
	// We have to make fuzzy both sides and then take intersection
	public Vector<Object> getUnion (float distance) {
		Vector<Object> resultSetA = getUnion (smallSet, bigSet, distance);
		return resultSetA;
		
	}
	
	// This function will return Union set with distance as input
	protected Vector<Object> getUnion (Vector<Object> setA, Vector<Object> setB, float distance) {
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
		// Add both set then weed out matching data
		for (int i=0 ; i < ilenB; i++ ) {
			Object o = setB.get(i);
			if (resultSet.indexOf(o) == -1 ) // it is not found in result set
				resultSet.add(o);
		}
		// Now create fuzzy
		
		FuzzyVector fz = new FuzzyVector(resultSet);
		fz = deDupFuzzy(fz,distance);	
		return fz;
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
		first = new Vector<Object>(new HashSet<Object>(first));
		second = new Vector<Object>(new HashSet<Object>(second));
		Vector<Object> resultSet = new Vector<Object>();
		int ilen = first.size();
		for (int i=0; i < ilen; i++ ) {
			Object o = first.get(i);
			if (second.indexOf(o) == -1 ) // it is not found in second
				resultSet.add(o);
		}
		
		errstr = "Difference Successful";
		Collections.sort(resultSet,COMPARABLE_COMAPRATOR);
		return resultSet;
	}
	
	// This function will return A- B set with cosine distance
		public Vector<Object> getDifference (Vector<Object> first, Vector<Object> second, float distance) {
			first = new Vector<Object>(new HashSet<Object>(first));
			second = new Vector<Object>(new HashSet<Object>(second));
			Vector<Object> resultSet = new Vector<Object>();
			
			FuzzyVector fzs = new FuzzyVector(second);
			FuzzyVector fzf = new FuzzyVector(first);
			fzs = deDupFuzzy(fzs,distance);
			fzf = deDupFuzzy(fzf,distance);
			
			int ilen = fzf.size();
			for (int i=0; i < ilen; i++ ) {
				Object o = fzf.get(i);
				if (fzs.indexOf(o,distance,0) == -1 ) // it is not found in second
					resultSet.add(o);
			}
			
			errstr = "Difference Successful";
			return resultSet;
		}
	
	// This function will return true if A is subset of B
	public boolean isSubset(Vector<Object> first, Vector<Object> second) {
		first = new Vector<Object>(new HashSet<Object>(first));
		second = new Vector<Object>(new HashSet<Object>(second));
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
	
	public FuzzyVector deDupFuzzy(FuzzyVector fz, float distance) {
		
		Collections.sort(fz,COMPARABLE_COMAPRATOR);
				
		for (int i=0 ; i < fz.size(); i++ ) {
			Object o = fz.get(i);
			int matchi = i+1; // Start from one index ahead
			
			while ( (matchi = fz.indexOf(o,distance,matchi))  != -1 ) {
				fz.remove(matchi);
				
				// matched indexed may be last after removing it would be out of index
				// but if it is positive it will handle it
			}
		}
		return fz;
	}
	
	public static final Comparator<Object> COMPARABLE_COMAPRATOR = new Comparator<Object>() {
		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			return ((Comparable<Object>) o1).compareTo(o2);
		}
	};
	
	// for testing
	public static void main(String[] args) {
		Vector<Object> first = new Vector<Object>();
		//first.add("vivek");first.add("vivek16");first.add("abcd");first.add("vivek44");
		first.add("vivek6");
		
		Vector<Object> second = new Vector<Object>();
		//second.add("vivek36");second.add("vivekkk");second.add("abcd");second.add("vivek445");
		second.add("vivek36");second.add("vivek16");
		
		//SetAnalysis seta = new SetAnalysis(first,second);
		SetAnalysis seta = new SetAnalysis(second,first);
		//Vector<Object> finaltse  = seta.getIntersection(0.9f);
		//Vector<Object> finaltse  = seta.getUnion();
		//Vector<Object> finaltse  = seta.getDifference(first, second,0.6f);
		Vector<Object> finaltse  = seta.getUnion(0.6f);

		for (Object a:finaltse)
			System.out.println(a.toString());
		
	}

	} // end of SetAnalysis
