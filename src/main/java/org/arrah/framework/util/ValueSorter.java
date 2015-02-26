package org.arrah.framework.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

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
 * This utility file sorts the key-value file 
 * and returns parameter in either as key sorted
 *  or value sorted. This is utility function used 
 *  by hashtable
 */



public class ValueSorter implements Comparable <Object> {
	private String _key;
	private Double _value;

	public ValueSorter (String key, Double value) {
		set_key(key);
		_value = value;
	}


	@Override
	public int compareTo(Object o) {
		 ValueSorter newobj = (ValueSorter)o;
		 if ( this._value > newobj._value) 
			 return 1;
		 if ( this._value < newobj._value) 
			 return  -1;
		return 0;
	}


	public String get_key() {
		return _key;
	}


	public void set_key(String _key) {
		this._key = _key;
	}
	
	public static Object[] sortOnValue(Hashtable<String, Double> map, boolean desc) {
		Enumeration <String> key = map.keys();
		String keyE = null;
		Vector <ValueSorter> vsv = new Vector <ValueSorter> ();
		while (key.hasMoreElements() == true ){
			keyE = key.nextElement();
			ValueSorter vs = new ValueSorter(keyE, map.get(keyE));
			vsv.add(vs);
		}
			Collections.sort(vsv); //ascending order
			
		if (desc == true) {
			Collections.reverse(vsv);
		}
		
		Object[] obj = new Object[vsv.size()];
		for ( int i=0; i <vsv.size(); i++ ) {
			ValueSorter vs = vsv.get(i);
			String keyv = vs.get_key();
			obj[i] = keyv;
		}
		return obj;
	}
	
	// OTHER is defualt strin value like "undefined" "NA"
	public static Object[] sortKey(Hashtable<String, Double> map, String OTHER) {
		Object[] obj = null;
		if (map.containsKey(OTHER) == false) {
			obj = map.keySet().toArray();
			Arrays.sort(obj);
			return obj;
		} else {
			Object[] key_s = map.keySet().toArray();
			Arrays.sort(key_s);
			obj = new Object[key_s.length];
			int j = 0; // New Object Index
			for (int i = 0; i < key_s.length; i++) {
				if (key_s[i].toString().compareToIgnoreCase(OTHER) == 0) {
					obj[key_s.length - 1] = OTHER;
					j--;
				} else
					obj[j] = key_s[i];
				j++;
			}
			return obj;
		}
	}
	


}
