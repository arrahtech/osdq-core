package org.arrah.framework.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.arrah.framework.util.DBConnectionConfiguration.Builder;

/**
 * Factory class to return database specific {@link DBConnectionUriParser}
 * 
 * @author aruny
 *
 */
public class DBConnectionUriParser {

  private static HashMap<String, String[]> uriScheme2ProtocolDriverMapping = new HashMap<>(
      1);

  /**
   * Add all the mapping from scheme to jdbc-scheme
   */
  static {
    uriScheme2ProtocolDriverMapping.put("oracle",
        new String[] { "jdbc:oracle:native", "oracle.jdbc.OracleDriver" });
    uriScheme2ProtocolDriverMapping.put("derby",
        new String[] { "jdbc:derby", "org.apache.derby.jdbc.ClientDriver" });
    uriScheme2ProtocolDriverMapping.put("mysql",
        new String[] { "jdbc:mysql", "com.mysql.jdbc.Driver" });
    uriScheme2ProtocolDriverMapping.put("informix",
        new String[] { "jdbc:informix-sqli", "com.informix.jdbc.IfxDriver" });
    uriScheme2ProtocolDriverMapping.put("hive2",
        new String[] { "jdbc:hive2", "org.apache.hive.jdbc.HiveDriver" });
    uriScheme2ProtocolDriverMapping.put("hive",
        new String[] { "jdbc:hive", "org.apache.hadoop.hive.jdbc.HiveDriver" });
    uriScheme2ProtocolDriverMapping.put("db2",
        new String[] { "jdbc:db2", "com.ibm.db2.jcc.DB2Driver" });
    uriScheme2ProtocolDriverMapping.put("postgresql",
        new String[] { "jdbc:postgresql", "org.postgresql.Driver" });
    uriScheme2ProtocolDriverMapping.put("ucanaccess", new String[] {
        "jdbc:ucanaccess", "net.ucanaccess.jdbc.UcanaccessDriver" });
    uriScheme2ProtocolDriverMapping.put("sqlserver", new String[] {
        "jdbc:sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver" });

    /*
     * In server mode, do we need odbc? I think, No!!
     * 
     * uriScheme2ProtocolDriverMapping.put("mysql-odbc", new String[]
     * {"jdbc:odbc", "sun.jdbc.odbc.JdbcOdbcDriver"});
     * uriScheme2ProtocolDriverMapping.put("msaccess-odbc", new String[]
     * {"jdbc:odbc", "sun.jdbc.odbc.JdbcOdbcDriver"});
     * uriScheme2ProtocolDriverMapping.put("sqlserver-odbc", new String[]
     * {"jdbc:odbc", "sun.jdbc.odbc.JdbcOdbcDriver"});
     * uriScheme2ProtocolDriverMapping.put("oracle-odbc", new String[]
     * {"jdbc:odbc", "sun.jdbc.odbc.JdbcOdbcDriver"});
     */
  }

  public static DBConnectionConfiguration parse(final String dbConnectionURI)
      throws URISyntaxException {
    DBConnectionConfiguration dbConnectionConfiguration = null;

    URI uri = new URI(dbConnectionURI);
    String scheme = uri.getScheme();
    String userinfo = uri.getUserInfo();
    String host = uri.getHost();
    int port = uri.getPort();
    String path = uri.getPath();
    String query = uri.getQuery();

    Builder dbConnectionConfigurationBuilder = new DBConnectionConfiguration.Builder(
        scheme);
    StringBuilder jdbcURL;
    switch (scheme) {
    case "mysql":
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to mysql jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append("://").append(host).append(":").append(port).append(path);
      if (query != null) {
        jdbcURL.append(";");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append("&");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    case "derby":
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to derby jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append("://").append(host).append(":").append(port).append(path);
      if (query != null) {
        jdbcURL.append(";");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append(";");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    case "oracle-thin":
      //jdbc:oracle:<drivertype>:<user>/<password>@<database>
      //jdbc:oracle:thin:scott/tiger@myhost:1521:orcl
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to derby jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append(":@").append(host).append(":").append(port).append(":").append(path);
      if (query != null) {
        jdbcURL.append(";");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append("&");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    case "sqlserver":
      //jdbc:sqlserver://localhost;user=MyUserName;password=*****;
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to derby jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append("://").append(host).append(":").append(port);
      if (path != null) {
        //this will be database name in case of sqlserver
        jdbcURL.append(";databaseName=").append(path);
      }
      if (query != null) {
        jdbcURL.append(";");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append(";");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    case "db2":
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to mysql jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append("://").append(host).append(":").append(port).append(path);
      if (query != null) {
        jdbcURL.append(":");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append(";");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    case "ucanaccess":
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to mysql jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append("://").append(path);
      if (query != null) {
        jdbcURL.append(";");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append("&");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    case "postgres":
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to mysql jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append("://").append(host).append(":").append(port).append(path);
      if (query != null) {
        jdbcURL.append("?");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append("&");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    case "informix":
      dbConnectionConfigurationBuilder.setHost(host).setPort(port)
          .setDatabase(path);
      if (userinfo != null) {
        String[] usernamePassword = userinfo.split(":");
        dbConnectionConfigurationBuilder.setUsername(usernamePassword[0])
            .setPassword(usernamePassword[1]);
      }
      dbConnectionConfigurationBuilder
          .setDatabaseProtocol(uriScheme2ProtocolDriverMapping.get(scheme)[0])
          .setJDBCDriver(uriScheme2ProtocolDriverMapping.get(scheme)[1]);
      // Prepare JDBC URL specific to mysql jdbc Driver
      jdbcURL = new StringBuilder(
          uriScheme2ProtocolDriverMapping.get(scheme)[0]);
      jdbcURL.append("://").append(host).append(":").append(port).append(path);
      if (query != null) {
        jdbcURL.append(":");
        String[] queryParams = query.split("&");
        for (int i = 0; i < queryParams.length; i++) {
          String[] keyValue = queryParams[i].split("=");
          dbConnectionConfigurationBuilder.setParam(keyValue[0], keyValue[1]);
          jdbcURL.append(keyValue[0]).append("=").append(keyValue[1]);
          if (i < queryParams.length - 1) {
            jdbcURL.append(";");
          }
        }
      }
      dbConnectionConfigurationBuilder.setJdbcURL(jdbcURL.toString());
      dbConnectionConfiguration = dbConnectionConfigurationBuilder.build();
      break;
    default:

      /*
       * switch (dbIndex) { case 0 : _dbparam.put("Database_Type",
       * "ORACLE_NATIVE"); driver.setText("oracle.jdbc.OracleDriver");
       * protocol.setText("jdbc:oracle:thin"); _dbparam.put("Database_Driver",
       * "oracle.jdbc.OracleDriver"); _dbparam.put("Database_Protocol",
       * "jdbc:oracle:thin"); driver.setEditable(false);
       * protocol.setEditable(false); jdbc_cs.setEditable(false);
       * dsn.setText("//hostname/SID"); infoStatus =
       * "INFORMATION: \n$ORACLE_HOME/lib should be in LIBPATH (for AIX), \n" +
       * "LD_LIBRARY_PATH (for Solaris) or \nSHLIBPATH (for HP) for UNIX user. \n\n"
       * + "For Windows user PATH variable should be set." +
       * "\n\n $ORACLE_HOME/jdbc/lib/ojdbc*.jar should be in CLASSPATH";
       * info.setText(infoStatus); disableResInput(); break; case 1 :
       * _dbparam.put("Database_Type", "ORACLE_ODBC");
       * driver.setText("sun.jdbc.odbc.JdbcOdbcDriver");
       * protocol.setText("jdbc:odbc"); _dbparam.put("Database_Driver",
       * "sun.jdbc.odbc.JdbcOdbcDriver"); _dbparam.put("Database_Protocol",
       * "jdbc:odbc"); driver.setEditable(false); protocol.setEditable(false);
       * jdbc_cs.setEditable(false); disableResInput(); break; case 2 :
       * _dbparam.put("Database_Type", "MYSQL");
       * driver.setText("com.mysql.jdbc.Driver");
       * protocol.setText("jdbc:mysql"); _dbparam.put("Database_Driver",
       * "com.mysql.jdbc.Driver"); _dbparam.put("Database_Protocol",
       * "jdbc:mysql"); driver.setEditable(false); protocol.setEditable(false);
       * jdbc_cs.setEditable(false); dsn.setText("//hostname/db");
       * disableResInput(); break; case 3 : _dbparam.put("Database_Type",
       * "MYSQL"); driver.setText("sun.jdbc.odbc.JdbcOdbcDriver");
       * protocol.setText("jdbc:odbc"); _dbparam.put("Database_Driver",
       * "sun.jdbc.odbc.JdbcOdbcDriver"); _dbparam.put("Database_Protocol",
       * "jdbc:odbc"); driver.setEditable(false); protocol.setEditable(false);
       * jdbc_cs.setEditable(false); disableResInput(); break; case 4 :
       * _dbparam.put("Database_Type", "SQL_SERVER");
       * driver.setText("com.microsoft.sqlserver.jdbc.SQLServerDriver");
       * protocol.setText("jdbc:sqlserver"); _dbparam.put("Database_Driver",
       * "com.microsoft.sqlserver.jdbc.SQLServerDriver");
       * _dbparam.put("Database_Protocol", "jdbc:sqlserver");
       * driver.setEditable(false); protocol.setEditable(false);
       * jdbc_cs.setEditable(false); dsn.setText("//hostname;databaseName=db");
       * disableResInput(); break; case 5 : _dbparam.put("Database_Type",
       * "SQL_SERVER"); driver.setText("sun.jdbc.odbc.JdbcOdbcDriver");
       * protocol.setText("jdbc:odbc"); _dbparam.put("Database_Driver",
       * "sun.jdbc.odbc.JdbcOdbcDriver"); _dbparam.put("Database_Protocol",
       * "jdbc:odbc"); driver.setEditable(false); protocol.setEditable(false);
       * jdbc_cs.setEditable(false); disableResInput(); break;
       * 
       * case 6 : // Enable Input as it is not tested fully
       * _dbparam.put("Database_Type", "MS_ACCESS_JDBC");
       * driver.setText("net.ucanaccess.jdbc.UcanaccessDriver");
       * protocol.setText("jdbc:ucanaccess"); _dbparam.put("Database_Driver",
       * "net.ucanaccess.jdbc.UcanaccessDriver");
       * _dbparam.put("Database_Protocol", "jdbc:ucanaccess");
       * driver.setEditable(false); protocol.setEditable(false);
       * jdbc_cs.setEditable(false); dsn.setText("//<mdb or accdb file path>");
       * disableResInput(); break;
       * 
       * case 7 : _dbparam.put("Database_Type", "MS_ACCESS");
       * driver.setText("sun.jdbc.odbc.JdbcOdbcDriver");
       * protocol.setText("jdbc:odbc"); _dbparam.put("Database_Driver",
       * "sun.jdbc.odbc.JdbcOdbcDriver"); _dbparam.put("Database_Protocol",
       * "jdbc:odbc"); driver.setEditable(false); protocol.setEditable(false);
       * jdbc_cs.setEditable(false); disableResInput(); break; case 8 :
       * _dbparam.put("Database_Type", "POSTGRES");
       * driver.setText("org.postgresql.Driver");
       * protocol.setText("jdbc:postgresql"); driver.setEditable(false);
       * protocol.setEditable(false); jdbc_cs.setEditable(false);
       * dsn.setText("//hostname/db"); disableResInput(); break; case 9 :
       * _dbparam.put("Database_Type", "DB2");
       * driver.setText("com.ibm.db2.jcc.DB2Driver");
       * protocol.setText("jdbc:db2"); driver.setEditable(false);
       * protocol.setEditable(false); jdbc_cs.setEditable(false);
       * dsn.setText("//hostname/db"); disableResInput(); break;
       * 
       * case 10 : _dbparam.put("Database_Type", "HIVE");
       * driver.setText("org.apache.hadoop.hive.jdbc.HiveDriver");
       * protocol.setText("jdbc:hive"); driver.setEditable(false);
       * protocol.setEditable(false); jdbc_cs.setEditable(false);
       * dsn.setText("//hostname/db"); disableResInput(); break; case 11:
       * _dbparam.put("Database_Type", "HIVE");
       * driver.setText("org.apache.hive.jdbc.HiveDriver");
       * protocol.setText("jdbc:hive2"); driver.setEditable(false);
       * protocol.setEditable(false); jdbc_cs.setEditable(false);
       * dsn.setText("//hostname/db"); disableResInput(); break; case 12 :
       * _dbparam.put("Database_Type", "INFORMIX");
       * driver.setText("com.informix.jdbc.IfxDriver");
       * protocol.setText("jdbc:informix-sqli"); driver.setEditable(false);
       * protocol.setEditable(false); jdbc_cs.setEditable(false);
       * dsn.setText("//hostname/db"); disableResInput(); break;
       * 
       * case 13 : _dbparam.put("Database_Type", "SPLICE");
       * driver.setText("org.apache.derby.jdbc.ClientDriver");
       * protocol.setText("jdbc:derby"); driver.setEditable(false);
       * protocol.setEditable(false); jdbc_cs.setEditable(false);
       * dsn.setText("//hostname/db"); disableResInput(); break;
       * 
       * case 14 : _dbparam.put("Database_Type", "Others");
       * driver.setText("jdbc.DbNameDriver"); protocol.setText("jdbc:dbname");
       * driver.setEditable(true); protocol.setEditable(true);
       * jdbc_cs.setEditable(true); dsn.setText("Enter DB Name");
       * jdbc_cs.setText("Enter JDBC Connect String"); info.setText(
       * "Enter JDBC connect string if using JDBC driver.\n" +
       * "Make sure JDBC driver is in CLASSPATH"); enableResInput();
       * 
       * break; default: _dbparam.put("Database_Type", "Others");
       * driver.setText("sun.jdbc.odbc.JdbcOdbcDriver");
       * protocol.setText("jdbc:odbc"); driver.setEditable(true);
       * protocol.setEditable(true); jdbc_cs.setEditable(false); dsn.setText(
       * "Enter DSN "); info.setText("Enter DSN if using Window odbc Driver \n"
       * +
       * "Start --> Control Panel-->Administrative Tools-->Data Sources-->System DSN"
       * ); enableResInput();
       * 
       * 
       * }
       */
    }
    return dbConnectionConfiguration;
  }
}
