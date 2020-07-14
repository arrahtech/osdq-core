package org.arrah.framework.udf;

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
 * This file is used as interface between RTM
 * (ReportTableModel) and UDF. This file will 
 * call UDF and update n return RTM
 * 
 */

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


import java.util.Map;

import org.arrah.framework.ndtable.ReportTableModel;

public class UDFInterfaceToRTM {
	
	public static ReportTableModel metricrtm = null;

	public UDFInterfaceToRTM() {
	};

	/* This function takes the expression string, parses it and substitute variables
	 * 
	 */
	public static int evalUDF(String udfName, ReportTableModel rtm,
								int selIndex, int beginIndex, int endIndex , List<String> colNames) {

//		colNames.forEach(e -> {
//			int i = ReportTableModel.getColumnIndex(rtm, colNames.get(0));
//			if (i < 0) {
//				return ;
//			}
//		});


		try {

			if (selIndex < 0)
				return -1;

			List<String> result = UDFEvaluator.map(udfName, rtm, colNames);
			if (result == null || result.size() == 0) {
				System.out.println(udfName + " returned empty List");
				return -1;
			}
		
			for (int i = (beginIndex -1)  ; i < (endIndex -1) ; i++) { 
					rtm.getModel().setValueAt(result.get(i), i, selIndex);
	
			} // end of for loop
		
			return 1;
		
		} catch (Exception e) {
			System.out.println("Could not Invoke:"+udfName);
			System.out.println("Exception:" + e.getLocalizedMessage());
			// e.printStackTrace();
			return -1;
			
		}
	}
	
	public static int evalUDF(String udfName, ReportTableModel rtm, List<String> colNames) {

		try {
		
			Class<?> obj = Class.forName(udfName);
			Method mapUDF = obj.getMethod("getMetricName");
			String[] metricName = (String[]) mapUDF.invoke(obj.newInstance());
			
			metricrtm = new ReportTableModel(metricName,true,true);
			
			Map<Object,List<Object>> result = UDFEvaluator.metric(udfName, rtm, colNames);
			
			if (result == null || result.size() == 0) {
				System.out.println(udfName + " returned empty Metric List");
				return -1;
			}
			
			// System.out.println("Result:" +result.toString() );
			
			for (Object s : result.keySet()) {
				List<Object> metricvalue = result.get(s.toString());
				
				metricvalue.add(0, s.toString());
				
				metricrtm.addFillRow(metricvalue.toArray());
			}
			

			return 1;
			
		} catch (Exception e) {
				
			System.out.println("Could not Invoke:"+udfName);
			System.out.println("Exception:" + e.getLocalizedMessage());
			// e.printStackTrace();
			return -1;
			
		}
	}
	
	
	
} // End of UDFInterfaceToRTM
