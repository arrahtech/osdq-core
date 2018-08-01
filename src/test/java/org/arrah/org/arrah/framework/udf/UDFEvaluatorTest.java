package org.arrah.org.arrah.framework.udf;


import org.arrah.framework.udf.UDFEvaluator;
import org.junit.Test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class UDFEvaluatorTest {

    @Test
    public void test1() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<Object> al = new ArrayList<>();
        al.add(1);
        al.add(2);
        al.add(3);
        assertEquals(6L, (long)UDFEvaluator.aggregate(
                "org.arrah.framework.udf.sample.SUM", al));
    }

    @Test
    public void test2() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<Object> al = new ArrayList<>();
        String[] testInput = new String[] {"Long", "Live", "Java"};
        al.add(testInput[0]);
        al.add(testInput[1]);
        al.add(testInput[2]);
        List<String> result = UDFEvaluator.map("org.arrah.framework.udf.sample.UPPER", al);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(testInput[i].toUpperCase(), result.get(i));
        }
    }
}
