package org.arrah.framework.wrappertoutil;
/**
 * @author vivek singh
 *
 */
import java.util.Arrays;
import java.util.Vector;

import org.arrah.framework.analytics.SetAnalysis;

/**
 * @author viveksingh
 *
 */
public class SetUtil {
	
	/**
	 * @param seta
	 * the input set A where memebers can be duplicate
	 * @param setb
	 * the input set B where memebers can be duplicate
	 * @return
	 * Union of Set A and set B where memebers can not be duplicate
	 */
	Object[]  unionSet(Object[] seta, Object[] setb) {
		if (seta == null )  return setb;
		if (setb == null ) return setb;
		SetAnalysis sa = new SetAnalysis(new Vector<Object>(Arrays.asList(seta)),
				new Vector<Object>(Arrays.asList(setb)));
		return sa.getUnion().toArray();
		
	}
	
	/**
	 * @param seta
	 * the input set A where memebers can be duplicate
	 * @param setb
	 * the input set B where memebers can be duplicate
	 * @param fuzzyIndex
	 * this is the fuzzy distance which will match similar words
	 * 0.0 - will take all ( no match) and > 1.0 will match exact
	 * @return
	 * Union of Set A and set B where memebers can not be duplicate
	 */
	Object[]  unionFuzzy(Object[] seta, Object[] setb, float fuzzyIndex) {
		if (seta == null )  return setb;
		if (setb == null ) return setb;
		SetAnalysis sa = new SetAnalysis(new Vector<Object>(Arrays.asList(seta)),
				new Vector<Object>(Arrays.asList(setb)));
		return sa.getUnion(fuzzyIndex).toArray();
		
	}

	/**
	 * @param seta
	 * the input set A where memebers can be duplicate
	 * @param setb
	 * the input set B where memebers can be duplicate
	 * @return
	 * Intersection of Set A and set B where memebers can not be duplicate
	 */
	Object[]  intersectionSet(Object[] seta, Object[] setb) {
		if (seta == null || setb == null)  return null;
		SetAnalysis sa = new SetAnalysis(new Vector<Object>(Arrays.asList(seta)),
				new Vector<Object>(Arrays.asList(setb)));
		return sa.getIntersection().toArray();
	}
	
	/**
	 * @param seta
	 * the input set A where memebers can be duplicate
	 * @param setb
	 * the input set B where memebers can be duplicate
	 * @param fuzzyIndex
	 * this is the fuzzy distance which will match similar words
	 * 0.0 - will take all ( no match) and > 1.0 will match exact
	 * @return
	 * Intersection of Set A and set B where memebers can not be duplicate
	 */
	
	Object[]  intersectionFuzzy(Object[] seta, Object[] setb, float fuzzyIndex) {
		if (seta == null || setb == null)  return null;
		SetAnalysis sa = new SetAnalysis(new Vector<Object>(Arrays.asList(seta)),
				new Vector<Object>(Arrays.asList(setb)));
		return sa.getIntersection(fuzzyIndex).toArray();
	}
	
	/**
	 * @param seta
	 * the input set A where memebers can be duplicate
	 * @param setb
	 * the input set B where memebers can be duplicate
	 * @return
	 * Difference of Set A and set B where memebers can not be duplicate
	 * Elements which are there in set A but not in set B
	 */

	Object[]  minusSet(Object[] seta, Object[] setb) {
		if (seta == null )  return null;
		if (setb == null ) return seta;
		SetAnalysis sa = new SetAnalysis();
		return sa.getDifference(new Vector<Object>(Arrays.asList(seta)), 
				new Vector<Object>(Arrays.asList(setb))).toArray();
		
	}
	
	/**
	 * @param seta
	 * the input set A where memebers can be duplicate
	 * @param setb
	 * the input set B where memebers can be duplicate
	 * @param fuzzyIndex
	 * this is the fuzzy distance which will match similar words
	 * 0.0 - will take all ( no match) and > 1.0 will match exact
	 * @return
	 * Difference of Set A and set B where memebers can not be duplicate
	 * Elements which are there in set A but not in set B
	 */
	
	Object[]  minusFuzzy(Object[] seta, Object[] setb, float fuzzyIndex) {
		if (seta == null )  return null;
		if (setb == null ) return seta;
		SetAnalysis sa = new SetAnalysis();
		return sa.getDifference(new Vector<Object>(Arrays.asList(seta)), 
				new Vector<Object>(Arrays.asList(setb)),fuzzyIndex).toArray();
	}
	
	// for testing
	public static void main(String[] args) {
		//Object[] first = new Object[]{"vivek","vivek16","abcd","vivek44"};
		
		//Object[] second = new Object[]{"vivek36","vivekkk","abcd","vivek445"};
		
		String[] first = new String[]{"vivek16","abcd","vivek44", "x", "xx"};
		String[] second = new String[]{"vivek36","vivekkk","abcd","vivek445", "x"};
		
		SetUtil setu = new SetUtil();
		//Object[] finaltse  = setu.unionSet(first, second);
		//Object[] finaltse  = setu.unionFuzzy(first, second, 1.1f);
		//Object[] finaltse  = setu.intersectionSet(first, second);
		//Object[] finaltse  = setu.intersectionFuzzy(first, second,1.0f);
		//Object[] finaltse2  = setu.intersectionFuzzy(second, first,1.0f);
		//Object[] finaltse  = setu.minusSet(first, second);
		//Object[] finaltse  = setu.minusFuzzy(first, second,1.1f);
		
		/***
		Object[] finaltse  = setu.intersectionFuzzy(first, second,0.0f);
		Object[] finaltse2  = setu.intersectionFuzzy(second, first,0.0f);

		for (Object a:finaltse)
			System.out.println(a.toString());
		
		System.out.println("----");
		
		for (Object a:finaltse2)
			System.out.println(a.toString());
		**/
		
		for (float f : Arrays.asList(0.0f, 1f, 1.1f)) {
            System.out.println(Arrays.toString(setu.intersectionFuzzy(first,second,f)));
            System.out.println(Arrays.toString(setu.intersectionFuzzy(second,first,f)));
            System.out.println(Arrays.toString(setu.minusFuzzy(first,second,f)));
            System.out.println(Arrays.toString(setu.minusFuzzy(second,first,f)));
            System.out.println(Arrays.toString(setu.unionFuzzy(first,second,f)));
            System.out.println(Arrays.toString(setu.unionFuzzy(second,first,f)));
      }
	}
}

