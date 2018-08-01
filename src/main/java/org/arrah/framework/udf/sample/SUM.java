package org.arrah.framework.udf.sample;

import org.arrah.framework.udf.AggregateUdf;

import java.util.ArrayList;
import java.util.concurrent.atomic.LongAdder;

/**
 * A sample UDF to show how to implement as aggregate function.
 * It takes list of Long object as input and returns a scalar, which is a sum of all input Long objects
 */
public class SUM extends AggregateUdf<Long> {

    @Override
    public Long eval(ArrayList<Object> input) {
        final LongAdder sum = new LongAdder();
        input.stream().forEach(e -> {
            sum.add((int)e);
        });
        return sum.longValue();
    }

}
