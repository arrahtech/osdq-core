package org.arrah.framework.profile;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import org.arrah.framework.dataquality.AadharValidator;
import org.arrah.framework.dataquality.GSTINValidator;
import org.arrah.framework.dataquality.MNValidator;
import org.arrah.framework.dataquality.PANValidator;
import org.arrah.framework.dataquality.PIIValidator;

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
	private int empty_count =0 ;
	private boolean hasNull = false;
	
	public FileProfile () {
		
	}
	
	private void profileHashtable(Hashtable <Object, Integer> hashtable) {
		Object o = new String("Null-Arrah");
		
		if (hashtable.containsKey(o ) == true ) {
			setHasNull(true);
			null_count = hashtable.get("Null-Arrah");
		}
		
		if (hashtable.containsKey("") == true ) {
			empty_count = hashtable.get("");
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
			Integer[] value = new Integer[5];
			value[0] = total_count;value[1] = unique_count;value[2] = pattern_count;value[3] = null_count;value[4] = empty_count;
			return value;
	}
	
	public Integer[] getStrProfiledValue (Hashtable <Object, Integer> hashtable) {
		Integer[] value = new Integer[2];
		int spaceC=0;
		int controlC=0;
		
		Enumeration<Object> keys =   hashtable.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (key.toString().matches(".*\\s+.*") == true) {
				spaceC = spaceC+hashtable.get(key);
			}
			if (key.toString().matches("^(?=.*[\\w])(?=.*[\\W])[\\w|\\W]+$") == true) {
				controlC = controlC+hashtable.get(key);
			}
		}
		value[0] = spaceC;value[1] = controlC;
		return value;
	}
	
	public Integer getAadhardValue (Hashtable <Object, Integer> hashtable) {
		Integer value = 0;
		Enumeration<Object> keys =   hashtable.keys();
		AadharValidator av = new AadharValidator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (av.isValidAadhar(key.toString()) == true) {
				value = value+hashtable.get(key);
			}
		}
		return value;
	}
	
	public Integer getPANValue (Hashtable <Object, Integer> hashtable) {
		Integer value = 0;
		Enumeration<Object> keys =   hashtable.keys();
		PANValidator av = new PANValidator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (av.isValidPAN(key.toString()) == true) {
				value = value+hashtable.get(key);
			}
		}
		return value;
	}
	
	public Integer getGSTValue (Hashtable <Object, Integer> hashtable) {
		Integer value = 0;
		Enumeration<Object> keys =   hashtable.keys();
		GSTINValidator av = new GSTINValidator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (av.isValidGST(key.toString()) == true) {
				value = value+hashtable.get(key);
			}
		}
		return value;
	}
	
	public Integer getCreditCardValue (Hashtable <Object, Integer> hashtable) {
		Integer value = 0;
		Enumeration<Object> keys =   hashtable.keys();
		PIIValidator pii = new PIIValidator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (pii.isCreditCard(key.toString()) == true) {
				value = value+hashtable.get(key);
			}
		}
		return value;
	}
	
	public Integer getEmailValue (Hashtable <Object, Integer> hashtable) {
		Integer value = 0;
		Enumeration<Object> keys =   hashtable.keys();
		PIIValidator pii = new PIIValidator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (pii.isEmail(key.toString()) == true) {
				value = value+hashtable.get(key);
			}
		}
		return value;
	}
	
	public Integer getMobileNValue (Hashtable <Object, Integer> hashtable) {
		Integer value = 0;
		Enumeration<Object> keys =   hashtable.keys();
		MNValidator  mnvalid = new MNValidator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (mnvalid.isValidMN(key.toString()) == true) {
				value = value+hashtable.get(key);
			}
		}
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

	public boolean isHasNull() {
		return hasNull;
	}

	public void setHasNull(boolean hasNull) {
		this.hasNull = hasNull;
	}
	
	public Double[] getNumberProfiledValue (Object[]  val) {
		for (int i=0; i < val.length; i++)
			val[i] = Double.parseDouble(val[i].toString());
		StatisticalAnalysis sa = new StatisticalAnalysis(val);
		Double[] value = new Double[5];
		value[0] = sa.getSum();value[1] = sa.getMean();
		value[2] = Double.parseDouble(sa.getMinObject().toString()); value[3] = Double.parseDouble(sa.getMaxObject().toString()); 
		value[4] = sa.getSDev();
		return value;
	}
	
	public Double[] getStrLengthProfiledValue (Object[]  val) {
		for (int i=0; i < val.length; i++)
			val[i] = val[i].toString().length();
		StatisticalAnalysis sa = new StatisticalAnalysis(val);
		Double[] value = new Double[5];
		value[0] = sa.getSum();value[1] = sa.getMean();
		value[2] = Double.parseDouble(sa.getMinObject().toString()); value[3] = Double.parseDouble(sa.getMaxObject().toString()); 
		value[4] = sa.getSDev();
		return value;
	}
}
