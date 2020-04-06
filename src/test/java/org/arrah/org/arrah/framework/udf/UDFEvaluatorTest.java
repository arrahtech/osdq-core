package org.arrah.org.arrah.framework.udf;


import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.udf.UDFEvaluator;
import org.junit.Test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UDFEvaluatorTest {

    @Test
    public void test1() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<Object> al = new ArrayList<>();
        al.add(1.00D);
        al.add(2.00D);
        al.add(3.00D);
        
        String[] colName = new String[] {"col1"};
        ReportTableModel rtm = new ReportTableModel(colName);
        for (Object d : al)
        	rtm.addFillRow(new Object[]{d});
        //rtm.toPrint();
        
        assertEquals(6D, (double)UDFEvaluator.aggregate(
                "org.arrah.framework.udf.sample.SUM", rtm, Arrays.asList(colName)),0.00D);
    }

    @Test
    public void test2() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<Object> al = new ArrayList<>();
        String[] testInput = new String[] {"Long", "Live", "Java"};
        al.add(testInput[0]);
        al.add(testInput[1]);
        al.add(testInput[2]);
        
        String[] colName = new String[] {"col1"};
        ReportTableModel rtm = new ReportTableModel(colName);
        for (Object d : al)
        	rtm.addFillRow(new Object[]{d});
        //rtm.toPrint();
        
        List<String> result = UDFEvaluator.map("org.arrah.framework.udf.sample.UPPER", rtm, Arrays.asList(colName));
        for (int i = 0; i < result.size(); i++) {
            assertEquals(testInput[i].toUpperCase(), result.get(i));
        }
    }
}
