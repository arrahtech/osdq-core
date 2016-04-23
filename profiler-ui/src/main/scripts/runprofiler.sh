echo ==== Starting Profiler =======
echo Need java 1.8.x or above to run
echo 
java -version 
java -Xmx4096M -Xms4096M -jar profiler-ui-6.1.6-SNAPSHOT.jar $1
