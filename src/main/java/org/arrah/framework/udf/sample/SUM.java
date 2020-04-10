package org.arrah.framework.udf.sample;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.udf.AggregateUdf;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

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
 * A sample UDF to show how to implement as aggregate function.
 * It takes list of Long object as input and returns a scalar, which is a sum of all input Long objects
 */
public class SUM extends AggregateUdf<Number> {

    @Override
    public Number eval(ReportTableModel rtm, List<String> columnName) {
    	
    	List<Object> input = Arrays.asList(rtm.getColData(columnName.get(0)) );
    	
        final DoubleAdder sum = new DoubleAdder();
        
        input.stream().forEach(e -> {
            sum.add((double) e);
        });
        
        return sum.doubleValue();
    }

    @Override
    public String describeFunction() {
		return "It returns SUM total of all rows of selected cell";
    	
    }
}
