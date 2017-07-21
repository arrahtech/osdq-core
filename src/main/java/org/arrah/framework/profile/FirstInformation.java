package org.arrah.framework.profile;

/***************************************************
 *     Copyright to Vivek Kumar Singh 2013         *
 *                                                 *
 * Any part of code or file can be changed,        *
 * redistributed, modified with the copyright      *
 * information intact                              *
 *                                                 *
 * Author$ : Vivek Singh                           *
 *                                                 *
 **************************************************/

/* This file is used for doing profiling like pattern
 * analysis, null value, unique value and distribution.
 * This is very common profiling and typically this
 * is the profile, that is done first.
 * 
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_NewConn;

public class FirstInformation {
	private static String minVal = "";
	private static String maxVal = "";

	public static double[] getProfileValues(QueryBuilder querybuilder) {
		String s = querybuilder.count_query_w(false, "row_count");
		String s1 = querybuilder.count_query_w(true, "row_count");
		String s2 = querybuilder.get_nullCount_query_w("Null");
		String s3 = querybuilder.get_zeroCount_query_w("0");
		String s4 = querybuilder.get_zeroCount_query_w("''");
		String s5 = querybuilder.get_pattern_query();
		double d = 0.0D;
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 0.0D;
		double d4 = 0.0D;
		double d5 = 0.0D;
		Object obj = null;
		try {
			Rdbms_NewConn.get().openConn();
		} catch (SQLException sqlexception) {
			System.out.println("\n Error: Could not open Connection");
			return null;
		}
		try {
			ResultSet resultset;
			for (resultset = Rdbms_NewConn.get().runQuery(s); resultset.next();)
				d = resultset.getDouble("row_count");

			resultset.close();
		} catch (SQLException sqlexception1) {
			d = -1D;
		}
		try {
			ResultSet resultset1;
			for (resultset1 = Rdbms_NewConn.get().runQuery(s1); resultset1.next();)
				d1 = resultset1.getDouble("row_count");

			resultset1.close();
		} catch (SQLException sqlexception2) {
			d1 = -1D;
		}
		try {
			ResultSet resultset2;
			for (resultset2 = Rdbms_NewConn.get().runQuery(s2); resultset2.next();)
				d2 = resultset2.getDouble("equal_count");

			resultset2.close();
		} catch (SQLException sqlexception3) {
			d2 = -1D;
		}
		try {
			ResultSet resultset3;
			for (resultset3 = Rdbms_NewConn.get().runQuery(s5); resultset3.next();)
				d5 = resultset3.getDouble("row_count");

			resultset3.close();
		} catch (SQLException sqlexception4) {
			d5 = -1D;
		}
		try {
			Rdbms_NewConn.get().closeConn();
		} catch (SQLException sqlexception5) {
			System.out.println("\n Warning: Could not close Connection");
		}
		return  (new double[] { d, d1, d1 != -1D ? d - d1 : -1D, d5, d2 });
	}

	public static Vector[] getPatternValues(QueryBuilder querybuilder) throws Exception {
		String s = querybuilder.get_freq_query();
		int i = 0;
		Vector avector[] = new Vector[2];
		avector[0] = new Vector<String>();
		avector[1] = new Vector<Double>();
		try {
			Rdbms_NewConn.get().openConn();
			ResultSet resultset;
			resultset = Rdbms_NewConn.get().runQuery(s);
			if (resultset == null)
				return null;
			while (resultset.next()) {
				double d = resultset.getDouble("row_count");
				if (d < 1.0D)
					break;
				String s1 = resultset.getString("like_wise");
				avector[0].add(i, s1);
				avector[1].add(i, new Double(d));
				i++;
			}
			if (resultset != null)
				resultset.close();
			Rdbms_NewConn.get().closeConn();
		} catch (SQLException sqlexception) {
		  System.out.println("Warning:"+sqlexception.getMessage());
			throw new Exception("\n Warning: Could not Get Pattern Information");
		}
		return avector;
	}

	public static Vector[] getDistributionValues(QueryBuilder querybuilder) {
		minVal="" ; maxVal = "";
		String s;
		String s1;
		int i =0;
		Vector avector[];
		s = querybuilder.get_freq_query_wnull();
		s1 = querybuilder.get_pattern_all_query();
		avector = new Vector[2];
		avector[0] = new Vector<String>();
		avector[1] = new Vector<Double>();
		double d;
		d = 0.0D;
		try {
			Rdbms_NewConn.get().openConn();
			ResultSet resultset;
			for (resultset = Rdbms_NewConn.get().runQuery(s1); resultset.next();)
				d = resultset.getDouble("row_count");

			resultset.close();
			if (d <= 0.0D)
				return null;

			double d1 = d / 6D; // making into 6 parts
			double d2 = 0.0D;
			double d3 = 0.0D;
			ResultSet resultset1;
			for (resultset1 = Rdbms_NewConn.get().runQuery(s); resultset1.next();) {
				d--;
				if (d1 < 1.0D) {
					double d4 = resultset1.getDouble("row_count");
					String s2 = resultset1.getString("like_wise");
					avector[0].add(i, s2);
					avector[1].add(i, new Double(d4));
					if (i == 0)
						setMinVal(s2);
					if (d == 0.0D)
						setMaxVal(s2);
					i++;
				} else {
					double d5 = resultset1.getDouble("row_count");
					d2 += d5;
					if (i == 0 && d3 == 0.0D)
						setMinVal(resultset1.getString("like_wise"));
					if (d3 < d1 && d != 0.0D) {
						d3++; // iterate till d3 hits the group count
					} else {
						String s3 = resultset1.getString("like_wise");
						avector[0].add(i, s3);
						avector[1].add(i, new Double(d2));
						if (d == 0.0D)
							setMaxVal(s3);
						d2 = 0.0D;
						d3 = 0.0D;
						i++;
					}
				}
			}

			resultset1.close();
			Rdbms_NewConn.get().closeConn();
		} catch (SQLException sqlexception) {
			System.out
					.println("\n Error: Could not Get Distribution Information");
			System.out.println("\n " + sqlexception.getMessage());
			return null;
		}
		return avector;
	}

	public static void setMaxVal(String maxVal) {
		FirstInformation.maxVal = maxVal;
	}

	public static String getMaxVal() {
		return maxVal;
	}

	public static void setMinVal(String minVal) {
		FirstInformation.minVal = minVal;
	}

	public static String getMinVal() {
		return minVal;
	}

}
