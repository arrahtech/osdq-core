package org.arrah.framework.udf;

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
 * A convenience class for UDF calling program.
 * This class hides the underlying java reflection
 * to invoke UDFs at runtime
 * 
 
 */

import org.arrah.framework.ndtable.ReportTableModel;

import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class UDFEvaluator {

    private static final Logger LOGGER = Logger.getLogger(UDFEvaluator.class.getName());

    /*
    A local cache of all UDFs
     */
    private static ConcurrentHashMap<String, Method> aggregateUdfMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Method> mapUdfMap = new ConcurrentHashMap<>();
    
    public static ConcurrentHashMap<String, Method> getAggregateUdf () {
    	return aggregateUdfMap;
    }
    
    public static ConcurrentHashMap<String, Method> getMapUdf () {
    	return mapUdfMap;
    }

    private static Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("org.arrah.framework.udf"))
            .setScanners(new SubTypesScanner(), new MethodParameterScanner()));


    static {
        try {
        	LOGGER.setLevel(Level.SEVERE);
        	
            LOGGER.info("Indexing all UDFs");
            indexAggregateUdfs();
            indexMapUdfs();
            LOGGER.info("Indexing all UDFs - [OK]");

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static <T> T aggregate(final String udfName, ReportTableModel rtm, List<String> colNames) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, InvocationTargetException {
        return (T) aggregateUdfMap.get(udfName).invoke(Class.forName(udfName).newInstance(), rtm, colNames);
    }

    public static <T> List<T> map(final String udfName, ReportTableModel rtm, List<String> colNames) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, InvocationTargetException {
        return (List<T>) mapUdfMap.get(udfName).invoke(Class.forName(udfName).newInstance(), rtm, colNames);
    }

    private static <T> void indexAggregateUdfs() throws NoSuchMethodException {
        Set<Class<? extends AggregateUdf >> subTypes = reflections.getSubTypesOf(AggregateUdf.class);

        subTypes.forEach(subType -> {
            try {
                Method method = subType.getDeclaredMethod("eval", ReportTableModel.class, List.class);
                aggregateUdfMap.put(subType.getName(), method);
            } catch (NoSuchMethodException exc) {
                LOGGER.severe("Error indexing Aggregate UDFs");
            }
        });

        LOGGER.info(aggregateUdfMap.toString());
    }

    private static <T> void indexMapUdfs() throws NoSuchMethodException {
        Set<Class<? extends MapUdf >> subTypes = reflections.getSubTypesOf(MapUdf.class);

        subTypes.forEach(subType -> {
            try {
                Method method = subType.getDeclaredMethod("eval", ReportTableModel.class, List.class);
                mapUdfMap.put(subType.getName(), method);
            } catch (NoSuchMethodException exc) {
                LOGGER.severe("Error indexing Mapper UDFs");
            }
        });

        LOGGER.info(mapUdfMap.toString());
    }
}