package org.arrah.framework.udf;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class for all map type UDFs.
 *
 * For each element in input, it maps to an output
 *
 * For UDFs to be recognized
 * it must extend this class and override eval() function.
 *
 * Also the UDFs should be under org as root package
 *
 */
public abstract class MapUdf<T> {

    abstract public List<T> eval(ArrayList<Object> input);

}
