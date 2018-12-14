package org.arrah.framework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
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
	private Object _key;
	private Double _value;

	public ValueSorter (Object key, Double value) {
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


	public Object get_key() {
		return _key;
	}


	public void set_key(Object _key) {
		this._key = _key;
	}
	
	public static Object[] sortOnValue(Hashtable<Object, Double> map, boolean desc) {
		Enumeration <Object> key = map.keys();
		Object keyE = null;
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
			Object keyv = vs.get_key();
			obj[i] = keyv;
		}
		return obj;
	}
	
	// OTHER is default string value like "undefined" "NA"
	public static Object[] sortKey(Hashtable<Object, Double> map, String OTHER) {
		Object[] obj = null;
		if (map.containsKey(OTHER) == false) {
			obj = map.keySet().toArray();
			Arrays.sort(obj);
			return obj;
		} else {
			List<Object> key_l = Arrays.asList(map.keySet().toArray());
			List<Object> newkey_l = new ArrayList<Object>();
			int otherI = key_l.indexOf(OTHER);
			if (otherI != -1) {
				System.out.println("UNDEFINED found at:" +otherI);
				for (int i=0; i < key_l.size(); i++)
					if (i != otherI)
						newkey_l.add(key_l.get(i));
			}
			
			newkey_l.sort(null);
			newkey_l.add(0, OTHER);
			return newkey_l.toArray();
		}
	}
	

}
