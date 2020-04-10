package org.arrah.framework.udf.sample;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.udf.MapUdf;

import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * A sample UDF to show how to implement custom to-upper function.
 * It takes maps of String as input and calls String::toUpperCase on each element
 * and returns the List on String with upper case text
 */
public class CONCATUPPER extends MapUdf<String> {


    @Override
    public List<String> eval(ReportTableModel rtm, List<String> columnName) {
    	
    	if (columnName == null) return null;
    	
    	int parameterLength = columnName.size();
    	if (parameterLength <= 0) return null;
    	
    	List<Object> input[] = new List[parameterLength];
    	
    	for (int i=0; i<parameterLength; i++) { // fill the data
    		input[i] = Arrays.asList(rtm.getColData(columnName.get(i))) ;
    	}
    	
    	List<String> output = new ArrayList<String>();
    	
    	for (int i=0; i < input[0].size(); i++) {
    		
    		String newCellValue="";
    		
    		for (int j=0; j<parameterLength; j++) {
    			newCellValue += input[j].get(i);
    		}
    		output.add(newCellValue.toUpperCase());
    	}
    	
    	return output;
    }
    
    @Override
    public String describeFunction() {
		return "<HTML><BODY>CONCATUPPER concatenates the input cells "
				+ "<BR> and then converts cancatenated value to UPPER Case"
				+ "<BR> Input:  Comma separate column names i.e col1,col2"
				+ "<BR> "
				+ "<BR> input - First,Second   Output - FIRSTSECOND"
				+"</HTML></BODY>";
    	
    }
}
