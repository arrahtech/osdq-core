package org.arrah.framework.xml;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePaths {
  static Path pathtoDB, pathtoRules, pathtoOutputfile, pathtoUDF;
  private static String fileName = "./OutputFile.csv";

  private static String rFile = "configuration/BusinessRules.xml";
  private static String cFile = "configuration/DBConnections.xml";
  private static String uFile = "configuration/UDFRules.xml";

  public static String getRuleFilePath() {
    return rFile;
  }

  public static String getConnFilePath() {
    return cFile;
  }

  public static String getUDFFilePath() {
	    return uFile;
	  }

  public static String getFileName() {
    return fileName;
  }

  public static URI getFilePathDB() {
    pathtoDB = Paths.get("configuration/", "DBConnections.xml");
    return pathtoDB.toUri();
  }

  public static URI getFilePathRules() {
    pathtoRules = Paths.get("configuration/", "BusinessRules.xml");
    return pathtoRules.toUri();
  }
  
  public static URI getFilePathUDFRules() {
	  pathtoUDF = Paths.get("configuration/", "UDFRules.xml");
	    return pathtoUDF.toUri();
	  }


}
