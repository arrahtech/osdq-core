package org.arrah.framework.profile;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

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
 * This is File profiler which profile data
 * for file which be passed into hashtable
 *
 */

public class FileProfile {
	private  int total_count =0;
	private int unique_count = 0;
	private int pattern_count =0;
	private int null_count =0 ;
	private boolean hasNull = false;
	
	public FileProfile () {
		
	}
	
	private void profileHashtable(Hashtable <Object, Integer> hashtable) {
		Object o = new String("Null-Arrah");
		
		if (hashtable.containsKey(o )== true ) {
			hasNull = true;
			null_count = hashtable.get("Null-Arrah");
		}
		
		Collection<Integer> values =   hashtable.values();
		int size = values.size();
		Integer[] intval = values.toArray(new Integer[0]);
		for (int i=0; i < size;  i++) {
			int cur_val = intval[i];
			total_count = total_count + cur_val;
			unique_count++; // Total unique value
			if (cur_val > 1)
				pattern_count++; // Total duplicate values
		}
	}
	
	public Integer[] getProfiledValue (Hashtable <Object, Integer> hashtable) {
			profileHashtable(hashtable);
			Integer[] value = new Integer[4];
			value[0] = total_count;value[1] = unique_count;value[2] = pattern_count;value[3] = null_count;
			return value;
	}
	
	public Hashtable <Object, Integer>  showPattern(Hashtable <Object, Integer> hashtable) {
		Hashtable <Object, Integer> pattern = new Hashtable <Object, Integer>();
		for (Enumeration<Object> e = hashtable.keys(); e.hasMoreElements();) {
			Object key = e.nextElement();
			int val = hashtable.get(key);
			if (val > 1)
				pattern.put(key, val);
		}
		return pattern;
	}
}
