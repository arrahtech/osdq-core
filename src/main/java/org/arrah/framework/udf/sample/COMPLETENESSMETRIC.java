package org.arrah.framework.udf.sample;

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

/**
 * A sample UDF to show how to implement business rule
 * based metrics. This metric will show the incomplete
 * value of each column
 */


import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.udf.MetricUdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class COMPLETENESSMETRIC extends MetricUdf<String,List<Integer> > {

    @Override
    public Map<String,List<Integer>> eval(ReportTableModel rtm, List<String> columnName) {
    	
    	if (columnName == null) return null;
    	if (columnName.size() <= 0) return null;
    	
    	Map<String,List<Integer>> metricMap = new HashMap<String,List<Integer>>();
    	
    	for (int i=0; i < columnName.size(); i++) { // fill the data
    		
    		List<Object> input = new ArrayList<Object>();
    		input = Arrays.asList(rtm.getColData(columnName.get(i))) ;
    	
	    	List<Integer> output = new ArrayList<Integer>();
	    	
	    	int nullValue =0;
	    	int emptyValue=0;
	    	
	    	for (int rowIndex =0; rowIndex < input.size(); rowIndex++) {
	    		
	    		Object cellObj = input.get(rowIndex);
	    		
	    		if (cellObj == null) {
	    			nullValue++;
	    			continue;
	    		}
	    		
	    		if (cellObj.toString().equals("") ) {
	    			emptyValue++;
	    		}
	    	}
	    	output.add(nullValue);
	    	output.add(emptyValue);
	    	
	    	metricMap.put(columnName.get(i),output);
    	}

    	return metricMap;
    }
    
    @Override
    public String describeFunction() {
		return "<HTML><BODY>COMPLETENESS_METRIC will run completeness "
				+ "<BR> check on all the columns that is given "
				+ "<BR> Input:  Comma separate column names i.e col1,col2"
				+ "<BR> "
				+ "<BR> input - First,Second "
				+ "<BR> Output - Metric Name1, Metric Name2 "
				+ "<BR> First - 22, 44 "
				+"</HTML></BODY>";
    	
    }

	@Override
	public String[] getMetricName() {
		String[] metricName = new String[]{"Column","NullValue","EmptyValue"};
		return metricName;
	}
}
