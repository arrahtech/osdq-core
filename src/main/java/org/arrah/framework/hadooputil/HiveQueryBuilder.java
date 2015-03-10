package org.arrah.framework.hadooputil;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* 
 * This is utility class for building query
 * mostly for Hive and Impala ( in future). It
 * may have some rdbms related query also.
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_conn;


public class HiveQueryBuilder extends QueryBuilder {
	
	public HiveQueryBuilder(String Dsn, String Table, String Column, String DBType) {
			super(Dsn,Table,Column,DBType);
	}

	public HiveQueryBuilder(String Dsn, String Table, String DBType) {
		super(Dsn,Table,DBType);
	}

	public HiveQueryBuilder(String Dsn, String DBType) {
		super(Dsn,DBType);
	}
	
	/* This method will append a file into Hive table. It will not overwrite existing data*/
	public String appendHiveTable (String filePath, String tableName, boolean isLocal, boolean isOverWrite, String partition) {
		String local = "";
		if (isLocal == true) local = "LOCAL";
		
		String overWrite ="";
		if (isOverWrite == true) overWrite=" OVERWRITE ";
		
		
		String query = "LOAD DATA "+local+" INPATH "+filePath+overWrite+"INTO TABLE " +super._table ;
		
		if (!(partition == null || "".equals(partition)))
			query = query+" PARTITION "+partition;
		
		return query;
	}
	
	
	/* This method will give detailed info about Hive table. */
	
	public String descHiveTable () {
		String query = "DESCRIBE FORMATTED "+super._table;
		return query;
	}
	
	/* This method will create rdbms table with Col description and constraint description. 
	 * This function takes values in two strings. One for Column parameters and other
	 * for CONSTRAINT parameter. The format for both is described below.
	 * */
	
	public String createRDBMSTable(String colDesc,String tbName,String constraintDesc ){
		
		String strrC=null,s1C=null;
		ArrayList<String> constraints;
		String[] constraintDescArray;
		int constraintCount=0;
		
		String createQuery="";
		boolean isConstraint = false;
		
		/**
		 * Column Params should given in  the format - column name,datatype, NOT NULL:column name1,datatype1:....
		 */
		if (colDesc != null && colDesc.length() > 1 ) {
			colDesc = colDesc.replace(","," ");
			colDesc = colDesc.replace(":",",");
		}
		
		
		/***Constraints**/
			/***This constraint is given in the form as below****/

		/***constraint name,CONSTRAINT,(column1:column2) Example -- id_pkey,PRIMARY KEY,(id1:1d2)
		* Multiple constraints can be given in the same way separated by ":"
		* constraint_name,CONSTRAINT,(column): constraint_name1,CONSTRAINT1,(column1)
		* For DB2, when creating a PRIMARY KEY constraint explicitly identify as
		* NOT NULL for example "column NOT NULL" in the columnDesc
		****/

		if (constraintDesc != null && constraintDesc.length() > 0) {
		constraintDescArray = constraintDesc.split(":");
		constraintCount = constraintDescArray.length;
		
		constraints = new ArrayList<String>();

		for (int i = 0; i < constraintCount; i++) {
			constraints.add(constraintDescArray[i]);
		}

		int noc1 = constraints.size();
		isConstraint=true;
		
			for (int i = 0; i < noc1; i++) {
				s1C = constraints.get(i).replace(","," ");
				s1C = s1C.replace(";",","); // (Col1;Col2) (Col1,Col2)
				// for Informix CONSTRAINT KEY WORD IS not needed for primary key and the 
				// options in GUI
				if(Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("Informix") == 0 ){
					if (strrC == null || "".equals(strrC)) 
						strrC = s1C;
					else 
						strrC = strrC + ", " + s1C + " ";
				} // Informix
				else { // Not Informix
				if (strrC == null || "".equals(strrC)) {
					strrC = "CONSTRAINT "+s1C;
				} else {
					strrC = strrC + " ," + " CONSTRAINT "+s1C + " ";
				}
				} // Not Informix
			}
		}
		/****constraints ***/
		if(isConstraint==true){
			createQuery = "CREATE TABLE " + tbName + "(" + colDesc +" ," + strrC + ")";
		}
		else {
			strrC=" ";
			createQuery = "CREATE TABLE " + tbName + "(" + colDesc + ")";
		}
		
		System.out.println("Create Table Query is:"+createQuery);
		return createQuery;
	}
	
	/* This method will create Hive table with Col description. 
	 * This function will also take info for partitioned column, row/field format
	 * 
	 * */
	
	public String createHiveTable(String tbName, String colDesc, String partColDesc, String fieldedelim){
		
		String createQuery= "CREATE TABLE "+tbName;
		/**
		 * Column Params should given in  the format - column name,datatype, COMMENT:column name1,COMMENT:....
		 * Strip the "," first then split on ":"
		 */
		if (colDesc != null && colDesc.length() > 1 ) {
			colDesc = colDesc.replace(","," ");
			colDesc = colDesc.replace(":",",");
			createQuery += " ("+ colDesc+")";
		}
		if (partColDesc != null && partColDesc.length() > 1 ) {
			partColDesc = partColDesc.replace(","," ");
			partColDesc = partColDesc.replace(":",",");
			createQuery += " PARTITIONED BY ("+partColDesc+")";
		}
		
		if (fieldedelim != null  ) // Not null
		createQuery += "ROW FORMAT DELIMITED FIELDS TERMINATED BY \'"+ fieldedelim +"\'";
		
		System.out.println("Hive Table Create Query:"+createQuery);
		return createQuery;
	}
	
	/* Hive does not support setObject method yet. So based on data type the parameters need to 
	 * changed to support the query. Like if it is String ( varchar ) type it should be within 
	 * single or double quote.
	 */
	
	public static String prepareHiveParam(int type, Object param) {
		String formatParam="";
		
		if (param instanceof Number) {
			return param.toString();
		} else if ( param instanceof java.util.Date) {
			// need to format
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Default format for Hive UnixTimeCall
			return "unix_timestamp(\'"+sd.format(param) +"\')";
			
		} else if (param instanceof String || param instanceof Character) {
			return "\'"+param+"\'";
		} else if (param instanceof Boolean) {
			if (((Boolean) param).booleanValue() == true) 
				return "TRUE";
			else 
				return "FALSE";
			
		} else if (param instanceof Byte) { // For Binary
			return param.toString();
		}
		
		return formatParam;
	
	}
	
	/* For hive equal to query 
	 * Hive does not support setObject so we have to prepare query with right format 
	 * vc_t will take data type and vc_v will carry object
	 * 
	 */
	public String get_hiveequal_query(Vector<?> col_vc, Vector<Integer> vc_t ,Vector<Object> vc_v, String condition) {
		String equal_query = "";
		String columns = "";
		String column = "";
		Enumeration<?> cols = col_vc.elements();
		int i=0;
		while (cols.hasMoreElements()) {
			column = cols.nextElement().toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0 
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_conn.getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) 
			{
				if (!column.startsWith("\""))
					column = "\"" + column + "\"";
			}
			String param = prepareHiveParam(vc_t.get(i),vc_v.get(i));
			if (cols.hasMoreElements())
				column += "="+param+" AND ";
			else
				column += "="+param;
			columns += column;
			i++;
		}
		equal_query = "SELECT * from " + _table + " WHERE " + columns;
		if (condition != null && "".equals(condition) == false)
			equal_query = equal_query + " AND (" + condition + ")";

		return equal_query;
		
	}

}
