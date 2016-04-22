echo ==== Starting Profiler =======
echo Need java 1.8.x or above to run
echo 
java -version 
java -Xmx4096M -Xms4096M -classpath "$CLASSPATH:utiljar/*:jdbcjar/*:hivejar/*:hive2jar/*:schedulejar/*:lib/profiler-6.1.6.jar" org/arrah/gui/swing/Profiler $1