package org.arrah.framework.udf;

import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A convenience class for UDF calling program.
 * This class hides the underlying java reflection
 * to invoke UDFs at runtime
 */
public class UDFEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UDFEvaluator.class);
    /*
    A local cache of all UDFs
     */
    private static ConcurrentHashMap<String, Method> aggregateUdfMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Method> mapUdfMap = new ConcurrentHashMap<>();

    private static Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("org"))
            .setScanners(new SubTypesScanner(), new MethodParameterScanner()));


    static {
        try {
            LOGGER.info("Indexing all UDFs");
            indexAggregateUdfs();
            indexMapUdfs();
            LOGGER.info("Indexing all UDFs - [OK]");

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static <T> T aggregate(final String udfName, ArrayList<Object> udfArgs) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, InvocationTargetException {
        return (T) aggregateUdfMap.get(udfName).invoke(Class.forName(udfName).newInstance(), udfArgs);
    }

    public static <T> List<T> map(final String udfName, ArrayList<Object> udfArgs) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, InvocationTargetException {
        return (List<T>) mapUdfMap.get(udfName).invoke(Class.forName(udfName).newInstance(), udfArgs);
    }

    private static <T> void indexAggregateUdfs() throws NoSuchMethodException {
        Set<Class<? extends AggregateUdf>> subTypes = reflections.getSubTypesOf(AggregateUdf.class);

        subTypes.forEach(subType -> {
            try {
                Method method = subType.getDeclaredMethod("eval", ArrayList.class);
                aggregateUdfMap.put(subType.getName(), method);
            } catch (NoSuchMethodException exc) {
                LOGGER.error("Error indexing UDFs", exc);
            }
        });

        LOGGER.info(aggregateUdfMap.toString());
    }

    private static <T> void indexMapUdfs() throws NoSuchMethodException {
        Set<Class<? extends MapUdf>> subTypes = reflections.getSubTypesOf(MapUdf.class);

        subTypes.forEach(subType -> {
            try {
                Method method = subType.getDeclaredMethod("eval", ArrayList.class);
                mapUdfMap.put(subType.getName(), method);
            } catch (NoSuchMethodException exc) {
                LOGGER.error("Error indexing UDFs", exc);
            }
        });

        LOGGER.info(mapUdfMap.toString());
    }
}