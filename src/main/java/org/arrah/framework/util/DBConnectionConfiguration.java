package org.arrah.framework.util;

import java.util.HashMap;

/**
 * Represents a database connection configuration.
 * @author aruny
 *
 */
public class DBConnectionConfiguration {
  private String databaseType;
  private String databaseProtocol;
  private String datasourceName;


  private String host;
  private int port;
  private String database;
  private String jdbcDriver;
  private String username;
  private String password;
  private HashMap<String, String> parameters;
  private String jdbcURL;

  public static class Builder {
    private String databaseType;
    private String databaseProtocol;
    private String datasourceName;
    private String host;
    private int port;
    private String database;
    private String jdbcDriver;
    private String username;
    private String password;
    private HashMap<String, String> parameters = new HashMap<>();
    private String jdbcURL;
    
    public Builder(final String databaseType) {
      this.databaseType = databaseType;
    }
    
    public Builder setDatabaseProtocol(final String databaseProtocol) {
      this.databaseProtocol = databaseProtocol;
      return this;
    }
    
    public Builder setDataSourceName(final String datasourceName) {
      this.datasourceName = datasourceName;
      return this;
    }
    
    public Builder setHost(final String host) {
      this.host = host;
      return this;
    }
    
    public Builder setPort(final int port) {
      this.port = port;
      return this;
    }
    
    public Builder setDatabase(final String database) {
      this.database = database;
      return this;
    }
    
    public Builder setJDBCDriver(final String jdbcDriver) {
      this.jdbcDriver = jdbcDriver;
      return this;
    }
    
    public Builder setUsername(final String username) {
      this.username = username;
      return this;
    }
    
    public Builder setPassword(final String password) {
      this.password = password;
      return this;
    }
    
    public Builder setParam(final String key, final String value) {
      this.parameters.put(key, value);
      return this;
    }
    
    public Builder setJdbcURL(final String jdbcURL) {
      this.jdbcURL = jdbcURL;
      return this;
    }
    
    public DBConnectionConfiguration build() {
      DBConnectionConfiguration configuration = new DBConnectionConfiguration();
      configuration.databaseType = this.databaseType;
      configuration.databaseProtocol = this.databaseProtocol; 
      configuration.datasourceName = this.datasourceName;
      configuration.host = this.host;
      configuration.port = this.port;
      configuration.database = this.database;
      configuration.jdbcDriver = this.jdbcDriver;
      configuration.username = this.username;
      configuration.password = this.password;
      configuration.parameters = this.parameters;
      configuration.jdbcURL = this.jdbcURL;
      return configuration;
    }
  }
  
  public String getDatabaseType() {
    return databaseType;
  }
  
  public String getDatabaseProtocol() {
    return databaseProtocol;
  }
  
  public String getDatasourceName() {
    return datasourceName;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getDatabase() {
    return database;
  }

  public String getJdbcDriver() {
    return jdbcDriver;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public HashMap<String, String> getParameters() {
    return parameters;
  }
  
  public String getJdbcURL() {
    return jdbcURL;
  }
}
