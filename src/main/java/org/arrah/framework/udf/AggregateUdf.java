package org.arrah.framework.udf;

import java.util.List;

import org.arrah.framework.ndtable.ReportTableModel;

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
 * An abstract class for all aggregate type UDFs. For UDFs to be recognized
 * it must extend this class and override eval() function.
 *
 * Also the UDFs should be under org as root package
 */
public abstract class AggregateUdf<T> {

    /**
     * main UDF to be implemented by UDF author.
     *
     * @param ReportTableModel, ColumnName
     * @return Aggregred Value
     */
    public abstract T eval(ReportTableModel rtm, List<String> columnName);
    
    abstract  public  String describeFunction();

}
