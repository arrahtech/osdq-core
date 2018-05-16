package org.arrah.framework.udf;

import java.util.ArrayList;

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
     * @param input
     * @return
     */
    public abstract T eval(ArrayList<Object> input);

}
