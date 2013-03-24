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


}
