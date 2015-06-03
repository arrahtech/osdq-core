package org.arrah.framework.util;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This class is used to implement
 * discreet range analysis.
 * It matches one vector set with another
 * vector set and utilities related to that
 *
 */

import java.util.Hashtable;
import java.util.Vector;


public class DiscreetRange {

	public static boolean isMatch(Vector<Object> disObj, Object obj) {
		if (obj == null || disObj == null || disObj.size() == 0)
			return false;
		return disObj.contains(obj);
	}

	public static Vector<Object> matchedSet(Vector<Object> disObj,
			Vector<Object> inputObj, boolean match) {
		Vector<Object> subSet = new Vector<Object>();
		int i = 0;
		while (i < inputObj.size()) {
			if (DiscreetRange.isMatch(disObj, inputObj.elementAt(i)) == true
					&& match == true) {
				subSet.add(inputObj.elementAt(i));
			}
			if (DiscreetRange.isMatch(disObj, inputObj.elementAt(i)) == false
					&& match == false) {
				subSet.add(inputObj.elementAt(i));
			}
		}
		return subSet;
	}

	/* For matching one column of a row */
	public static Vector<Object[]> matchedSetArray(Vector<Object> disObj,
			Vector<Object[]> inputObj, boolean match, int index) {
		Vector<Object[]> subSet = new Vector<Object[]>();
		int i = 0;
		while (i < inputObj.size()) {
			Object[] objArray = inputObj.elementAt(i);
			if (DiscreetRange.isMatch(disObj, objArray[index]) == true
					&& match == true) {
				subSet.add(objArray);
			}
			if (DiscreetRange.isMatch(disObj, objArray[index]) == false
					&& match == false) {
				subSet.add(objArray);
			}
		}
		return subSet;
	}

	public static Vector<String> tokenizeText(String text, String token) {
		if (token == null || text == null || "".equals(text) || "".equals(token))
			return (Vector<String>)null;
		String[] tokenA = text.trim().split(token);
		int i = 0;
		Vector<String> vec = new Vector<String>();
		while (i < tokenA.length)
			vec.add(tokenA[i++]);
		return vec;
	}
	
	public static Vector<Integer> mergeSet(Vector<Integer> leftSet,
			Vector<Integer> rightSet, String mergeType) {
		if (leftSet == null || rightSet == null)
			return null;

		if (mergeType.trim().compareToIgnoreCase("or") == 0) { // OR set
			Vector<Integer> orSet = new Vector<Integer>();
			orSet = leftSet;
			for (int i = 0; i < rightSet.size(); i++) {
				if (orSet.contains(rightSet.get(i)) == false)
					orSet.add(rightSet.get(i));
			}
			return orSet;
		} else if (mergeType.trim().compareToIgnoreCase("and") == 0) { // AND
																		// set
			Vector<Integer> andSet = new Vector<Integer>();
			if (leftSet.size() > rightSet.size()) {
				for (int i = 0; i < rightSet.size(); i++) {
					if (leftSet.contains(rightSet.get(i)) == true)
						andSet.add(rightSet.get(i));
				}
			} else {
				for (int i = 0; i < leftSet.size(); i++) {
					if (rightSet.contains(leftSet.get(i)) == true)
						andSet.add(leftSet.get(i));
				}
			}
			return andSet;
		} else if (mergeType.trim().compareToIgnoreCase("xor") == 0) { // XoR
																		// set
			// Left is Universal Set and right Set is getting Exclusive XoR
			Vector<Integer> xorSet = new Vector<Integer>();
			for (int i = 0; i < leftSet.size(); i++) {
				if (rightSet.contains(leftSet.get(i)) == false)
					xorSet.add(leftSet.get(i));
			}
			return xorSet;

		}
		return leftSet;
	}
	
	// This function will unique values assuming low cardinality
	public static Hashtable<Object,Integer> getUnique(Vector<Object> list){
		
		Hashtable <Object,Integer> newUniq = new Hashtable <Object,Integer>();
		for (Object o: list) {
			if (o == null) continue; // null not counted
			if (newUniq.containsKey(o) == true ) {
				int prev = newUniq.get(o);
				newUniq.put(o,++prev); // Increase counter by one
			} else {
				newUniq.put(o, 1);
			}
		}
		return newUniq;
	}

	

}
