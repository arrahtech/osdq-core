package org.arrah.framework.dataquality;

import java.util.List;


/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for creating utility 
 * functions will be used by string similarity
 * based on open source simmetric function
 */


public class SimmetricsUtil {

	
	public SimmetricsUtil() {
		
	} // Constructor
	
	public static class MongeElkan {
		// Default Constructor
		private org.simmetrics.metrics.MongeElkan modMongeElkan;
		
		public MongeElkan() {
			modMongeElkan =  new org.simmetrics.metrics.MongeElkan(new org.simmetrics.metrics.JaroWinkler());
		}
		
		public float compare(List<String> a, List<String> b) {
			return modMongeElkan.compare(a, b);
		}
		
	}
		
	
	
	
} //SimmetricsUtil
