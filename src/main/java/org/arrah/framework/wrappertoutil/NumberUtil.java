package org.arrah.framework.wrappertoutil;
/**
 * @author vivek singh
 *
 */

import org.arrah.framework.profile.StatisticalAnalysis;

public class NumberUtil {
	
	/**
	 * @param numseries - series of numbers
	 * @return standard deviation of the series
	 */
	public static double stdDev (Number[] numseries) {
			return new StatisticalAnalysis(numseries).getSDev() ;
	}
	
	/**
	 * @param numseries - series of numbers
	 * @return range maxiumn - minimum
	 */
	public static double range (Number[] numseries) {
			return new StatisticalAnalysis(numseries).rangeObject() ;
	}
	
	/**
	 * @param numseries
	 * @return median or avg of the number series
	 */
	public static double median (Number[] numseries) {
		return new StatisticalAnalysis(numseries).getMean() ;
	}
	
	/**
	 * @param numseries
	 * @return variance of the number series
	 */
	public static double variance (Number[] numseries) {
		return new StatisticalAnalysis(numseries).getVariance() ;
	}
	
	
	public static void main(String [] args) {
			Number[] nums = new Number[] {1,2,3,4,5,6};
		
			System.out.println(variance(nums));
		
	}

}
