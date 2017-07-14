package org.arrah.framework.analytics;

import java.util.ArrayList;
import java.util.Vector;

import org.arrah.framework.util.StringCaseFormatUtil;
import org.simmetrics.metrics.CosineSimilarity;

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

/* This  class is created to implement fuzzy indexOf function 
 * will matching 
 * using fuzzy logic using edit distance
 */

public class FuzzyVector extends java.util.Vector<Object> {

		/**
		 * @param <T>
		 * 
		 */
	<T> FuzzyVector (Vector<T> parentC) {
			super(parentC);
			
	}
		
	private static final long serialVersionUID = 1L;
	public int indexOf(Object obj, float distance) {
		int matchI = -1;
		if (obj == null)
			return matchI;
		matchI = super.indexOf(obj);
		if (matchI != -1 ) //exact match found
			return matchI;
		
		// Now loop and find the match which distance closer to given one
		// for timebeing we are using cosine distance
		ArrayList<Character> alist = StringCaseFormatUtil.toArrayListChar(obj.toString());
		java.util.Set<Character> aset = new java.util.HashSet<Character>(alist);
		CosineSimilarity<Character> fuzzyalgo = new CosineSimilarity<Character> ();
		for (int i=0; i < this.size(); i++) {
			Object b = this.get(i);
			if (b == null )
					continue;
			ArrayList<Character> blist = StringCaseFormatUtil.toArrayListChar(b.toString());
			java.util.Set<Character> bset = new java.util.HashSet<Character>(blist);
			float dis = fuzzyalgo.compare(aset, bset);
			if (dis>=distance)
				return i;
		}
		return matchI;
	}
	
} // end of FuzzyVector

