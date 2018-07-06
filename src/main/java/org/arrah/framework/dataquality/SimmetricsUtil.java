package org.arrah.framework.dataquality;

import java.util.List;

import org.simmetrics.StringMetric;
import org.simmetrics.StringMetricBuilder;


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
	
	public static class Soundex {
		private StringMetric soundex;
		public Soundex() {
			soundex = org.simmetrics.StringMetrics.soundex();
		}
		
		public float compare(String a, String b) {
			return soundex.compare(a, b);
		}
	}
	
	public static class qGramDistance {
		private StringMetric qGram;
		public qGramDistance() {
			qGram = org.simmetrics.StringMetrics.qGramsDistance();
		}
		
		public float compare(String a, String b) {
			return qGram.compare(a, b);
		}
	}
	
	public static class DoubleMetaPhone {
		private StringMetric dmetaPhone;
		public DoubleMetaPhone() {
			dmetaPhone = StringMetricBuilder.with(org.simmetrics.StringMetrics.jaroWinkler()).
					simplify(new org.simmetrics.simplifiers.DoubleMetaphone()).build();
		}
		
		public float compare(String a, String b) {
			return dmetaPhone.compare(a, b);
		}
	}
	
	
	
} //SimmetricsUtil
