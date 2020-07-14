package org.arrah.framework.udf;

import java.util.List;
import java.util.Map;

import org.arrah.framework.ndtable.ReportTableModel;

/***********************************************
 *     Copyright to Arrah Technology 2020      *
 *     http://www.arrahtech.com                *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/**
 * An abstract class for all metric type UDFs.
 *
 * For each element in input, it maps to an output
 *
 * For UDFs to be recognized
 * it must extend this class and override eval() function.
 *
 * Also the UDFs should be under org.arrah.framework.udf as  package
 *
 */
public abstract class MetricUdf<K, V > {

    abstract public Map<K,V> eval(ReportTableModel rtm, List<String> columnName);
    
    abstract  public  String describeFunction();
    
    abstract  public  String[] getMetricName();

}
