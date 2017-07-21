package org.arrah.framework.rdbms;

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
 * depending upon table, column, Data source and 
 * database type
 *
 */

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arrah.framework.profile.TableMetaInfo;

public class QueryBuilder {
	protected String _dsn, _table, _column, _dtype;
	protected String _table1, _column1; // for table comparison

	private static boolean isCond = false;
	private static String _cond_q = "";
	private static Vector<?>[] dateVar;
	
	public QueryBuilder() {
		
	}

	public QueryBuilder(String Dsn, String Table, String Column, String DBType) {
		set_dsn(Dsn);
		_table = Table;
		_column = Column;
		_dtype = DBType;
		if (_dtype.compareToIgnoreCase("mysql") != 0 
				&& _dtype.compareToIgnoreCase("hive") != 0 
				&& _dtype.compareToIgnoreCase("informix") != 0 
				&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
				&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
						Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ) )
		{
			if (!_table.startsWith("\""))
				_table = "\"" + _table + "\"";
			if (!_column.startsWith("\""))
				_column = "\"" + _column + "\"";
		}
		String cat = Rdbms_NewConn.get().getHValue("Database_Catalog");
		if (!(cat == null || "".equals(cat)))
			_table = cat + "." + _table;
	}

	/* Use for Table query */
	public QueryBuilder(String Dsn, String Table, String DBType) {
		set_dsn(Dsn);
		_table = Table;
		_column = "";
		_dtype = DBType;
		if (_dtype.compareToIgnoreCase("mysql") != 0 
			    && _dtype.compareToIgnoreCase("hive") != 0 
			    && _dtype.compareToIgnoreCase("Informix") != 0 
				&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
			    && !(_dtype.compareToIgnoreCase("Others") == 0 && 
						Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ) )
		{
			if (!_table.startsWith("\""))
				_table = "\"" + _table + "\"";
		}
		String cat = Rdbms_NewConn.get().getHValue("Database_Catalog");
		if (!(cat == null || "".equals(cat)))
			_table = cat + "." + _table;
	}

	/* Use for ETL query */
	public QueryBuilder(String Dsn, String DBType) {
		set_dsn(Dsn);
		_dtype = DBType;
	}

	/* Setting Comparison Table - set column dynamically */
	public void setCTableCol(String Table, String Column) {
		_table1 = Table;
		_column1 = Column;
		if (_dtype.compareToIgnoreCase("mysql") != 0 
				&& _dtype.compareToIgnoreCase("hive") != 0
				&& _dtype.compareToIgnoreCase("informix") != 0
				&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
				&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
						Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ) ) 
		{
			if (!_table1.startsWith("\""))
				_table1 = "\"" + _table1 + "\"";
			if (!_column1.startsWith("\""))
				_column1 = "\"" + _column1 + "\"";
		}
		String cat = Rdbms_NewConn.get().getHValue("Database_Catalog");
		if (!(cat == null || "".equals(cat)))
			_table1 = cat + "." + _table1;
	}

	/* Get all table values */
	public String get_tableAll_query() {
		String allTable = "SELECT * FROM " + _table;
		return allTable;
	}

	/* Get all table values Count */
	public String get_tableCount_query() {
		String allCount = "SELECT count(*) as row_count FROM " + _table;
		return allCount;
	}

	/* Get the value of count */
	public String count_query(boolean distinct, String col_name) {

		// Count Query
		String count_query = "";

		// MS Access does not support DISTINCT on count
		if (distinct == false) {
			count_query = " SELECT count(" + _column + ") as " + col_name
					+ " FROM " + _table;
			if (isCond)
				count_query = count_query + " WHERE " + _cond_q;
		} else {
			count_query = "SELECT count(*) as " + col_name
					+ " FROM ( SELECT DISTINCT " + _column + " FROM " + _table
					+ " WHERE " + _column + " IS NOT NULL ";

			if (isCond)
				count_query = count_query + " AND " + _cond_q;

			if (_dtype.compareToIgnoreCase("sql_server") == 0
					|| _dtype.compareToIgnoreCase("mysql") == 0
					|| _dtype.compareToIgnoreCase("postgres") == 0
					|| _dtype.compareToIgnoreCase("teiid") == 0
					|| _dtype.compareToIgnoreCase("splice") == 0)
				count_query += " ) as AS1";
			else if (_dtype.compareToIgnoreCase("hive") == 0)
				count_query += " ) t1";
			else
				count_query += " )";

		}

		return count_query;
	}

	/* Get the value of count without condition */
	public String count_query_w(boolean distinct, String col_name) {
		String count_query = "";

		if (distinct == false)
			count_query = " SELECT count(" + _column + ") as " + col_name
					+ " FROM " + _table;
		else {
			count_query = "SELECT count(*) as " + col_name
					+ " FROM ( SELECT DISTINCT " + _column + " FROM " + _table
					+ " WHERE " + _column + " IS NOT NULL ";

			if (_dtype.compareToIgnoreCase("sql_server") == 0
					|| _dtype.compareToIgnoreCase("mysql") == 0
					|| _dtype.compareToIgnoreCase("postgres") == 0
					|| _dtype.compareToIgnoreCase("teiid") == 0
					|| _dtype.compareToIgnoreCase("splice") == 0)
				count_query += " ) as AS1";
			else if (_dtype.compareToIgnoreCase("hive") == 0)
				count_query += " ) t1";
			else
				count_query += " )";
		}
		return count_query;
	}

	/* Get the value of Bottom X */
	public String bottom_query(boolean distinct, String col_name, String num) {
		String distinct_str = "";
		String bottom_sel_query = "";
		if (distinct == true)
			distinct_str = " DISTINCT ";

		// Bottom X Value
		if ((_dtype.compareToIgnoreCase("oracle_native") == 0)
				|| (_dtype.compareToIgnoreCase("oracle_odbc") == 0))

		{
			bottom_sel_query = " SELECT " + _column + " as " + col_name
					+ " FROM " + " (SELECT " + distinct_str + _column
					+ " FROM " + _table;

			if (isCond)
				bottom_sel_query = bottom_sel_query + " WHERE " + _cond_q;

			bottom_sel_query += " order by " + _column + ") WHERE rownum <= "
					+ num;
		} else if (_dtype.compareToIgnoreCase("mysql") == 0
				|| _dtype.compareToIgnoreCase("postgres") == 0
				|| _dtype.compareToIgnoreCase("ms_access_jdbc") == 0
				|| _dtype.compareToIgnoreCase("teiid") == 0
				|| _dtype.compareToIgnoreCase("db2") == 0 ) {

			bottom_sel_query = " SELECT " + distinct_str + " " + _column
					+ " as " + col_name + " FROM " + _table;

			if (isCond)
				bottom_sel_query = bottom_sel_query + " WHERE " + _cond_q;
			
			if ( _dtype.compareToIgnoreCase("teiid") == 0)
				bottom_sel_query += " order by " + _column + " LIMIT "+num;
			else
				bottom_sel_query += " order by " + _column + " LIMIT "+num+" OFFSET 0";
			
		} else if(_dtype.compareToIgnoreCase("hive") == 0){   // Hive does not support offset
			
			
			  bottom_sel_query = " SELECT " + _column + " as " + col_name + " FROM ( ";  
			 	
			 	bottom_sel_query += " SELECT " + distinct_str + " " + _column +  
			 	" FROM " + _table;  
			 	
			 	if (isCond)  
			 	bottom_sel_query = bottom_sel_query + " WHERE " + _cond_q;  
			 	
			 	bottom_sel_query += " order by " + _column + " LIMIT "+num +" ) t1 ";  

		} else if(_dtype.compareToIgnoreCase("Informix") ==0){ // Informix Syntax does not take TOP
			// Informix does not take the keyword TOP. So updated with the keyword FIRST

			bottom_sel_query = " SELECT " + " FIRST " + num + " "
			+ _column + " as " + col_name + " FROM " + _table;

			if (isCond)
			bottom_sel_query = bottom_sel_query + " WHERE " + _cond_q;

			bottom_sel_query += " order by " + _column;

		} else if(_dtype.compareToIgnoreCase("Splice") ==0){  // Splice Syntax does not take TOP
			
            bottom_sel_query = " SELECT " + _column  + " as " + col_name + " FROM " + _table   ;              
            if (isCond)
            	bottom_sel_query =  bottom_sel_query + " WHERE " + _cond_q;
                  
            bottom_sel_query += " order by " + _column + "FETCH FIRST " + " " + num + " " + " ROW ONLY";
            
        }  else {
			bottom_sel_query = " SELECT " + distinct_str + " TOP " + num + " "
					+ _column + " as " + col_name + " FROM " + _table;

			if (isCond)
				bottom_sel_query = bottom_sel_query + " WHERE " + _cond_q;

			bottom_sel_query += " order by " + _column;
		}

		return bottom_sel_query;
	}

	/* Get the value of Top X */
	public String top_query(boolean distinct, String col_name, String num) {
		String distinct_str = "";
		String top_sel_query = "";
		if (distinct == true)
			distinct_str = " DISTINCT ";

		// Top X value
		if ((_dtype.compareToIgnoreCase("oracle_native") == 0)
				|| (_dtype.compareToIgnoreCase("oracle_odbc") == 0))

		{
			top_sel_query = " SELECT " + _column + " as " + col_name + " FROM "
					+ " (SELECT " + distinct_str + _column + " FROM " + _table;

			if (isCond)
				top_sel_query = top_sel_query + " WHERE " + _cond_q;

			top_sel_query += " order by " + _column
					+ " desc ) WHERE rownum <= " + num;
		} else if (_dtype.compareToIgnoreCase("mysql") == 0
				 || _dtype.compareToIgnoreCase("postgres") == 0
				 || _dtype.compareToIgnoreCase("ms_access_jdbc") == 0
				 || _dtype.compareToIgnoreCase("teiid") == 0
				 || _dtype.compareToIgnoreCase("db2") == 0) {
			
			top_sel_query = " SELECT " + distinct_str + " " + _column + " as "
					+ col_name + " FROM " + _table;

			if (isCond)
				top_sel_query = top_sel_query + " WHERE " + _cond_q;
			if (_dtype.compareToIgnoreCase("teiid") == 0)
				top_sel_query += " order by " + _column + " desc LIMIT "+num;
			else
				top_sel_query += " order by " + _column + " desc LIMIT "+num+" OFFSET 0";
			
		} else if(_dtype.compareToIgnoreCase("hive") ==0){  // Hive does not support offset
		 	
		 	top_sel_query = " SELECT " + _column + " as " + col_name + " FROM ( ";  
		 	
		 	top_sel_query +=  " SELECT " + distinct_str + " " + _column +  " FROM " + _table;  
		 	
		 	if (isCond)  
		 	top_sel_query = top_sel_query + " WHERE " + _cond_q;  
		 	
		 	top_sel_query += " order by " + _column + " desc LIMIT "+num +" ) t1 ";  
		 	}  
		else if(_dtype.compareToIgnoreCase("Informix") ==0){ // Informix Syntax does not take TOP
			top_sel_query = " SELECT " + " FIRST " + num + " "
			+ _column + " as " + col_name + " FROM " + _table;

			if (isCond)
			top_sel_query = top_sel_query + " WHERE " + _cond_q;

			top_sel_query += " order by " + _column + " desc ";

		} else if(_dtype.compareToIgnoreCase("Splice") ==0){  // Splice does not support Top    
            top_sel_query = " SELECT " + _column + " as " +  col_name + " FROM " + _table ;
                                    
            if (isCond)
                   top_sel_query = top_sel_query + " WHERE " + _cond_q;
            top_sel_query += " order by " + _column + " " + " desc fetch first" + " " +  num + " " + "row Only ";

        } else {
			top_sel_query = " SELECT " + distinct_str + " TOP " + num + " "
					+ _column + " as " + col_name + " FROM " + _table;

			if (isCond)
				top_sel_query = top_sel_query + " WHERE " + _cond_q;

			top_sel_query += " order by " + _column + " desc ";

		}
		return top_sel_query;
	}

	/* Get the Aggregate Values */
	public String aggr_query(String status, int index, String min_value,
			String max_value) {
		String count = "", avg = "", max = "", min = "", sum = "";
		String aggr_query = "";
		String total_count = status.substring(0, 1);
		int total_sel = new Integer(total_count).intValue();

		if (total_sel == 0)
			return aggr_query; // Nothing Selected

		// How to find out where to put Separator
		// Get the total # of selected check box ,SEP should be n-1

		if (status.charAt(1) == 'Y') {
			count = "count(" + _column + ") as row_count ";
			if (total_sel > 1) {
				total_sel -= 1;
				count += ",";
			}
		}

		if (status.charAt(2) == 'Y') {
			avg = "avg(" + _column + ") as avg_count ";
			if (total_sel > 1) {
				total_sel -= 1;
				avg += ",";
			}
		}

		if (status.charAt(3) == 'Y') {
			max = "max(" + _column + ") as max_count ";
			if (total_sel > 1) {
				total_sel -= 1;
				max += ",";
			}
		}

		if (status.charAt(4) == 'Y') {
			min = "min(" + _column + ") as min_count ";
			if (total_sel > 1) {
				total_sel -= 1;
				min += ",";
			}
		}

		if (status.charAt(5) == 'Y')
			sum = " sum(" + _column + ") as sum_count ";

		// for Aggregate value

		if (index == 0) {
			aggr_query = "SELECT " + count + avg + max + min + sum + " FROM "
					+ _table;
			if (isCond)
				aggr_query = aggr_query + " WHERE " + _cond_q;

		}
		// for Less than Value
		if (index == 1) {
			aggr_query = "SELECT " + count + avg + max + min + sum + " FROM "
					+ _table + " WHERE " + _column + " < " + min_value;
			if (isCond)
				aggr_query = aggr_query + " and " + _cond_q;
		}
		// for More than Value
		if (index == 2) {
			aggr_query = "SELECT " + count + avg + max + min + sum + " FROM "
					+ _table + " WHERE " + _column + " > " + max_value;
			if (isCond)
				aggr_query = aggr_query + " and " + _cond_q;
		}
		// for In Between Values (Range)
		if (index == 3) {
			aggr_query = "SELECT " + count + avg + max + min + sum + " FROM "
					+ _table + " WHERE " + _column + " > " + min_value
					+ " and " + _column + " < " + max_value;
			if (isCond)
				aggr_query = aggr_query + " and " + _cond_q;
		}
		return aggr_query;
	}

	/* Get the duplicate value */
	public String dist_count_query(int index, String min_value, String max_value) {
		String dist_count_query;

		dist_count_query = "SELECT count(*) as dist_count FROM ( SELECT DISTINCT "
				+ _column + " FROM " + _table;

		if (isCond)
			dist_count_query = dist_count_query + " WHERE " + _cond_q;

		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0
				|| _dtype.compareToIgnoreCase("postgres") == 0 
				|| _dtype.compareToIgnoreCase("teiid") == 0
				|| _dtype.compareToIgnoreCase("splice") == 0)
			dist_count_query += " ) as AS1";
		else if (_dtype.compareToIgnoreCase("hive") == 0)
			dist_count_query += " ) as t1";
		else
			dist_count_query += " )";

		if (index == 0) {
		}
		if (index == 1) {
			dist_count_query += " WHERE " + _column + " < " + min_value;
		}
		if (index == 2) {
			dist_count_query += " WHERE " + _column + " > " + max_value;
		}
		if (index == 3) {
			dist_count_query += " WHERE " + _column + " > " + min_value
					+ " and " + _column + " < " + max_value;
		}

		return dist_count_query;

	}

	/* Get the Like String */
	public String get_like_query(String like_str, boolean like) {
		String like_query = "";
		if (like == true) {
			
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				like_query = "SELECT " + _column + " as like_wise FROM " + _table
				+ " WHERE " + _column + "::text ILIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("informix") == 0 )
				like_query = "SELECT " + _column + " as like_wise FROM " + _table + 
				" WHERE " + _column + "::VARCHAR(255) LIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("splice") == 0 )
                like_query = "SELECT " + _column + " as like_wise FROM " + _table
                + " WHERE " + "CAST(" + _column + " AS CHAR(250)) LIKE "  + "'" + like_str + "'";

			else
				like_query = "SELECT " + _column + " as like_wise FROM " + _table
					+ " WHERE " + _column + " LIKE " + "'" + like_str + "'";
		} else {
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				like_query = "SELECT " + _column + " as like_wise FROM " + _table
				+ " WHERE " + _column + "::text NOT ILIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("informix") == 0 )
				like_query = "SELECT " + _column + " as like_wise FROM " + _table +
				" WHERE " + _column + "::VARCHAR(255) NOT LIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("splice") == 0 )
                like_query = "SELECT " + _column + " as like_wise FROM " + _table
                + " WHERE " + "CAST(" + _column + " AS CHAR(250)) NOT LIKE "  + "'" + like_str + "'";

			else
				like_query = "SELECT " + _column + " as like_wise FROM " + _table
					+ " WHERE " + _column + " NOT LIKE " + "'" + like_str + "'";
		}

		if (isCond)
			like_query = like_query + " and " + _cond_q;

		// Order by creates problem with multi-line data field
		// like_query += " order by " +_column;

		return like_query;

	}

	/* Get the All String  for a column*/
	public String get_all_query() {
		
		String all_query; 
		
		if(_dtype.compareToIgnoreCase("hive") == 0){ 
			all_query = "SELECT " + _column + " as like_wise FROM "; 
			             
		 	all_query += " ( SELECT * FROM " + _table + " order by " + _column + " ) t1";  
		 	
		 	if (isCond)  
		 	all_query = all_query + " WHERE " + _cond_q;  
		 	
		 }  else {  
		 	all_query = "SELECT " + _column + " as like_wise FROM " + _table;  
		 	
		 	
		 	if (isCond)  
		 	all_query = all_query + " WHERE " + _cond_q;  
		 	
		 	all_query += " order by " + _column; 
		}
	
		return all_query;
	}

	/* Get the All String */
	public String get_all_query_wcond_wnull() {
		String all_query="";
		
		if(_dtype.compareToIgnoreCase("hive") == 0){ 
			all_query = "SELECT " + _column + " as like_wise FROM "; 
            
		 	all_query += " ( SELECT * FROM " + _table + 
		 			" WHERE " + _column + " IS NOT NULL"+" order by " + _column + " ) t1";
		}
		else {
			all_query = "SELECT " + _column + " as like_wise FROM " + _table
				+ " WHERE " + _column + " IS NOT NULL";
			all_query += " order by " + _column;
		}

		return all_query;
	}

	/* Get ALL Frequency Query without Null */
	public String get_freq_query_wnull() {
		
		String freq_query = "SELECT count( " + _column + " ) as row_count," 
				+ _column + " as like_wise FROM " + _table + " WHERE " 
				+ _column + " IS NOT NULL";  
			freq_query += " group by " + _column + " having count(" + _column  
					+ ") > 0 order by "; 

		if(_dtype.compareToIgnoreCase("hive") == 0){ 
				freq_query = freq_query +"like_wise";  
		 } else {  
		 		freq_query = freq_query +_column;  
		 }  

		
		return freq_query;
	}

	/* Get the All String without Order */
	public String get_all_worder_query() {
		String all_query = "SELECT " + _column + " as like_wise FROM " + _table;
		if (isCond)
			all_query = all_query + " WHERE " + _cond_q;

		// Order by creates problem with multi-line data field
		// all_query += " order by "+_column;

		return all_query;

	}

	/* Get Null to Query */
	public String get_nullCount_query(String equalTo) {
		String equal_query = "SELECT count(*) as equal_count FROM " + _table
				+ " WHERE " + _column + " Is " + equalTo;
		if (isCond)
			equal_query = equal_query + " and " + _cond_q;

		return equal_query;
	}

	/* Get Null to Query without condition */
	public String get_nullCount_query_w(String equalTo) {
		String equal_query = "SELECT count(*) as equal_count FROM " + _table
				+ " WHERE " + _column + " Is " + equalTo;
		return equal_query;
	}

	/* Get zero count query */
	public String get_zeroCount_query(String equalTo) {
		String equal_query = "SELECT count( " + _column
				+ " ) as equal_count FROM " + _table + " WHERE " + _column
				+ " = " + equalTo;
		if (isCond)
			equal_query = equal_query + " and " + _cond_q;

		return equal_query;
	}

	/* Get zero count query without condition */
	public String get_zeroCount_query_w(String equalTo) {
		String equal_query = "SELECT count( " + _column
				+ " ) as equal_count FROM " + _table + " WHERE " + _column
				+ " = " + equalTo;
		return equal_query;
	}

	/* Get Prepared query */
	public String get_prep_query() {
		String prep_query = "SELECT count( " + _column
				+ " ) as row_count FROM " + _table + " WHERE " + _column
				+ " >= ? and " + _column + " < ?";
		if (isCond)
			prep_query = prep_query + " and " + _cond_q;
		return prep_query;
	}

	/* Get Duplicate Frequency Query */
	public String get_freq_query() {
		String freq_query = "SELECT count( " + _column + " ) as row_count, "
				+ _column + " as like_wise FROM " + _table;
		freq_query += " group by " + _column + " having count(" + _column
				+ ") > 1 order by ";

		
		if(_dtype.compareToIgnoreCase("hive") == 0){ 
			freq_query = freq_query +"row_count desc";  
		} else {  
	 		freq_query = freq_query +"1 desc";  
		}  
		
		return freq_query;
	}

	/* Get Pattern for Column without condition Query */
	public String get_pattern_query() {
		String pattern_query = "SELECT count(*) as row_count FROM ( ";
		pattern_query += "SELECT " + _column + " FROM " + _table;
		pattern_query += " group by " + _column + " having count(" + _column
				+ ") > 1 ";
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0
				|| _dtype.compareToIgnoreCase("postgres") == 0
				|| _dtype.compareToIgnoreCase("teiid") == 0
				|| _dtype.compareToIgnoreCase("splice") == 0)
			pattern_query += " ) as AS1";
		else if (_dtype.compareToIgnoreCase("hive") == 0)
			pattern_query += " ) t1";
		else
			pattern_query += " )";
		
		return pattern_query;
	}

	/* Get Pattern for Column without condition without Null Query */
	public String get_pattern_all_query() {
		String pattern_query = "SELECT count(*) as row_count FROM ( ";
		pattern_query += "SELECT " + _column + " FROM " + _table;
		pattern_query += " WHERE " + _column + " IS NOT NULL ";
		pattern_query += " group by " + _column + " having count(" + _column
				+ ") > 0 ";
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0
				||  _dtype.compareToIgnoreCase("postgres") == 0
				|| _dtype.compareToIgnoreCase("teiid") == 0
				|| _dtype.compareToIgnoreCase("splice") == 0)
			pattern_query += " ) as AS1";
		else if (_dtype.compareToIgnoreCase("hive") == 0)
			pattern_query += " ) t1";
		else
			pattern_query += " )";
		
		return pattern_query;
	}

	/* Get Frequency All Query */
	public String get_freq_all_query() {
		String freq_all_query = "SELECT " + _column + " as like_wise, count( "
				+ _column + " ) as row_count FROM " + _table;
		if (isCond)
			freq_all_query = freq_all_query + " WHERE " + _cond_q;
		
		freq_all_query += " group by " + _column + " having count(" + _column
				+ ") > 0 order by";

		if(_dtype.compareToIgnoreCase("hive") == 0) 
		 		freq_all_query += " row_count desc";  
		 else  
		 	freq_all_query += " 2 desc ";  
		return freq_all_query;
	}

	/* Get Frequency Like Query */
	public String get_freq_like_query(String like_str, boolean like) {
		String freq_like_query = "";
		if (like == true) {
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				freq_like_query = "SELECT " + _column + " as like_wise, count( "
					+ _column + " ) as row_count FROM " + _table + " WHERE "
					+ _column + "::text ILIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("informix") == 0)
				freq_like_query = "SELECT " + _column + " as like_wise, count( "
					+ _column + " ) as row_count FROM " + _table + " WHERE "
					+ _column + "::VARCHAR(255) LIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("splice") == 0 )
                freq_like_query = "SELECT " + _column + " as like_wise, count( "
                             + _column + " ) as row_count FROM " + _table + " WHERE "
                             + "CAST("  + _column + "AS CHAR(250)) LIKE " + "'" + like_str + "'";
			else
				freq_like_query = "SELECT " + _column + " as like_wise, count( "
						+ _column + " ) as row_count FROM " + _table + " WHERE "
						+ _column + " LIKE " + "'" + like_str + "'";
		} else
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				freq_like_query = "SELECT " + _column + " as like_wise, count( "
					+ _column + " ) as row_count FROM " + _table + " WHERE "
					+ _column + "::text NOT ILIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("informix") == 0)
				freq_like_query = "SELECT " + _column + " as like_wise, count( "
					+ _column + " ) as row_count FROM " + _table + " WHERE "
					+ _column + "::VARCHAR(255) NOT LIKE " + "'" + like_str + "'";
			else if (_dtype.compareToIgnoreCase("splice") == 0 )
                freq_like_query = "SELECT " + _column + " as like_wise, count( "
                             + _column + " ) as row_count FROM " + _table + " WHERE "
                             + "CAST("  + _column + "AS CHAR(250)) NOT LIKE " + "'" + like_str + "'";
			else
				freq_like_query = "SELECT " + _column + " as like_wise, count( "
						+ _column + " ) as row_count FROM " + _table + " WHERE "
						+ _column + " NOT LIKE " + "'" + like_str + "'";

		if (isCond)
			freq_like_query = freq_like_query + " and " + _cond_q;
		
		if(_dtype.compareToIgnoreCase("hive") == 0)  
		 	freq_like_query += " group by " + _column + " having count(" + _column  
		 	+ ") > 0 order by row_count desc";  
		 	else  
		 	freq_like_query += " group by " + _column + " having count(" + _column  
		        + ") > 0 order by 2 desc";
		

		return freq_like_query;
	}

	/* Get Matching compare count */
	public String get_match_count(byte multiple, int mX) {
		String m_count_query;
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| (_dtype.compareToIgnoreCase("mysql") == 0)
				|| _dtype.compareToIgnoreCase("ms_access") == 0
				|| _dtype.compareToIgnoreCase("teiid") == 0
				|| _dtype.compareToIgnoreCase("postgres") == 0 )
			
			m_count_query = " SELECT count(*) as row_count,sum(AS1.row_count) as row_sum FROM ( SELECT count("
					+ _table + "." + _column + ") as row_count FROM " + _table;
		
		else if (_dtype.compareToIgnoreCase("splice") == 0)
            m_count_query = " SELECT count(*) as row_count,sum(row_count) as row_sum FROM ( SELECT count("
                         + _table + "." + _column + ") as row_count FROM " + _table + "as VirtualTable";
		else
			m_count_query = " SELECT count(*) as row_count,sum(row_count) as row_sum FROM ( SELECT count("
					+ _table + "." + _column + ") as row_count FROM " + _table;

		if (_table.equals(_table1) == false) {
			if (_dtype.compareToIgnoreCase("hive") == 0 ){ // hive to have explicit Inner join - JOIN
				m_count_query += " LEFT SEMI JOIN " + _table1 + " ON ( " + _table + "." + _column  
					+ " = " + _table1 + "." + _column1 + ") AND ";  
				
			} else {
				m_count_query += "," + _table1 + " WHERE " + _table + "." + _column
					+ " = " + _table1 + "." + _column1 + " AND ";
			}
		} else
			m_count_query += " WHERE ";

		m_count_query += _table + "." + _column + " IS NOT NULL GROUP BY "
				+ _table + "." + _column + " HAVING " + " count(" + _table
				+ "." + _column + ") ";
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| (_dtype.compareToIgnoreCase("ms_access") == 0)
				|| _dtype.compareToIgnoreCase("mysql") == 0
				||  _dtype.compareToIgnoreCase("postgres") == 0
				|| _dtype.compareToIgnoreCase("teiid") == 0
				|| _dtype.compareToIgnoreCase("splice") == 0) {
			// Splice remove alias 
			if (_dtype.compareToIgnoreCase("Splice") == 0) 
                m_count_query = m_count_query.replaceAll("as VirtualTable", "");

			if (multiple == 0)
				m_count_query += "= 1 ) as AS1";
			else if (multiple == 1)
				m_count_query += ">= 1 ) as AS1";
			else if (multiple == 2)
				m_count_query += "> 1 ) as AS1";
			else if (multiple == 3)
				m_count_query += "= " + mX + " ) as AS1";
		} else if (_dtype.compareToIgnoreCase("hive") == 0) {
			if (multiple == 0)
				m_count_query += "= 1 )  t1";
			else if (multiple == 1)
				m_count_query += ">= 1 )  t1";
			else if (multiple == 2)
				m_count_query += "> 1 )  t1";
			else if (multiple == 3)
				m_count_query += "= " + mX + " )  t1";
		}
		else {
			if (multiple == 0)
				m_count_query += "= 1 ) ";
			else if (multiple == 1)
				m_count_query += ">= 1 ) ";
			else if (multiple == 2)
				m_count_query += "> 1 ) ";
			else if (multiple == 3)
				m_count_query += "= " + mX + " ) ";
		}
		
		return m_count_query;
	}

	/* Get Matching compare row value */
	public String get_match_value(byte multiple, int mX, boolean match,
			boolean isLeft) {
		String m_match_query = "";
		String match_str = "";
		String hive_innerQuery =""; // Hive does not support IN or NOT
		if (match)
			match_str = " IN";
		else
			match_str = " NOT IN";

		
		if (_dtype.compareToIgnoreCase("hive") == 0) {

				if (_table.equals(_table1) == false )
					hive_innerQuery += " ( SELECT "+_table+"."+_column+" as match_value FROM "
						+ _table + " JOIN " + _table1 + " ON (" + _table + "."
						+ _column + " = " + _table1 + "." + _column1 + ")  AND "
						+ _table + "." + _column + " IS NOT NULL GROUP BY "
						+ _table + "." + _column + " HAVING " + " count(" + _table
						+ "." + _column + ")  ";
				else 
					hive_innerQuery += " ( SELECT "+_table+"."+_column+" as match_value FROM "
							+ _table + " WHERE " + _table + "." + _column
							+ " IS NOT NULL GROUP BY " + _table + "." + _column
							+ " HAVING " + " count(" + _table + "." + _column + ")  ";
			
				if (match) {
				if (multiple == 0)
					hive_innerQuery += "= 1 ) t1";
				else if (multiple == 1)
					hive_innerQuery += ">= 1 ) t1";
				else if (multiple == 2)
					hive_innerQuery += "> 1 ) t1";
				else if (multiple == 3)
					hive_innerQuery += "= " + mX + ") t1";
				} else {
					if (multiple == 0)
						hive_innerQuery += "!= 1 ) t1";
					else if (multiple == 1)
						hive_innerQuery += "< 1 ) t1";
					else if (multiple == 2)
						hive_innerQuery += "<= 1 ) t1";
					else if (multiple == 3)
						hive_innerQuery += "!= " + mX + ") t1";
				}
				
			
			if (_table.equals(_table1) == false && isLeft == false) {
				m_match_query = " SELECT * FROM "+_table1 +
						 " LEFT SEMI JOIN "+hive_innerQuery +" ON  " +
						 _table1+"."+_column1+" = t1.match_value";
				m_match_query += " ORDER BY "+_table1+"."+_column1;
			} else if(_table.equals(_table1) == true && isLeft == false) {
				m_match_query = " SELECT * FROM "+_table +
						 " LEFT SEMI JOIN "+hive_innerQuery +" ON  " +
						 _table+"."+_column+" = t1.match_value";
				m_match_query += " ORDER BY "+_table+"."+_column1;
			} else {
				m_match_query = " SELECT *  FROM "+_table+ " LEFT SEMI JOIN "+ hive_innerQuery+" ON " + 
						_table+"."+_column+" = t1.match_value";
				m_match_query += " ORDER BY "+ _table+"."+_column;
			}
			
		} else { // RDBMS Code
			if (_table.equals(_table1) == false && isLeft == false)
				m_match_query = " SELECT *  FROM " + _table1 + " WHERE " + _column1
					+ match_str;
			else
				m_match_query = " SELECT *  FROM " + _table + " WHERE " + _column
					+ match_str;
		
			
			if (_table.equals(_table1) == false)
				m_match_query += " ( SELECT " + _table + "." + _column + " FROM "
					+ _table + "," + _table1 + " WHERE " + _table + "."
					+ _column + " = " + _table1 + "." + _column1 + "  AND "
					+ _table + "." + _column + " IS NOT NULL GROUP BY "
					+ _table + "." + _column + " HAVING " + " count(" + _table
					+ "." + _column + ") ";
			else
				m_match_query += " ( SELECT " + _table + "." + _column + " FROM "
					+ _table + " WHERE " + _table + "." + _column
					+ " IS NOT NULL GROUP BY " + _table + "." + _column
					+ " HAVING " + " count(" + _table + "." + _column + ") ";

			if (multiple == 0)
				m_match_query += "= 1 )";
			else if (multiple == 1)
				m_match_query += ">= 1 )";
			else if (multiple == 2)
				m_match_query += "> 1 )";
			else if (multiple == 3)
				m_match_query += "= " + mX + ")";
		
			if (isLeft)
				m_match_query += " ORDER BY " + _column;
			else
				m_match_query += " ORDER BY " + _column1;
		}

		return m_match_query;
	}

	/* Get All Duplicate Frequency Query */
	/** Not in use for Now ***/
	public String get_all_freq_query(boolean isDup) {

		String freq_query = "SELECT * FROM " + _table + " WHERE " + _column
				+ " IN ";
		freq_query += "( SELECT " + _column + " FROM (";
		freq_query += " SELECT " + _column + " FROM " + _table;
		if (isCond)
			freq_query += " WHERE " + _cond_q;
		freq_query += " group by " + _column + " having count(" + _column + ")";
		if (isDup)
			freq_query += " > 1 ) ) ";
		else
			freq_query += " = 1 ) )";

		return freq_query;
	}

	/* Get like for SearchDB Query */
	public String get_like_table(String searchS, int index, boolean isCount) {
		Vector<?> avector[] = null;
		avector = TableMetaInfo.populateTable(5, index, index + 1, avector);
		String columns = "";
		if (avector == null ) return null;
		for (int j = 0; j < avector[0].size() - 1; j++) {
			
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				columns += "\"" + avector[0].elementAt(j) + "\"::text ILIKE \'%"
						+ searchS + "%\' OR ";
			else if (_dtype.compareToIgnoreCase("informix") == 0 )
				columns += avector[0].elementAt(j) + "::VARCHAR(255) LIKE \'%" + searchS
				+ "%\' OR ";
			else if (_dtype.compareToIgnoreCase("splice") == 0 )
                columns +=  "CAST(" + avector[0].elementAt(j) + " AS CHAR(250)) LIKE \'%" + searchS + "%\' OR ";
			else if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
				columns += "\"" + avector[0].elementAt(j) + "\" LIKE \'%"
						+ searchS + "%\' OR ";
			else
				columns += avector[0].elementAt(j) + " LIKE \'%" + searchS
						+ "%\' OR ";
		}
		if (avector[0].size() != 0)
			
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				columns += "\"" + avector[0].elementAt(avector[0].size() - 1) + "\"::text ILIKE \'%"
						+ searchS + "%\'";
			else if (_dtype.compareToIgnoreCase("informix") == 0 )
				columns += avector[0].elementAt(avector[0].size() - 1) + "::VARCHAR(255) LIKE \'%" + searchS
				+ "%\'";
			else if (_dtype.compareToIgnoreCase("splice") == 0 )
				columns += " CAST(" + " " + avector[0].elementAt(avector[0].size() - 1) + " AS CHAR(250)) LIKE \'%" + searchS + "%\' ";
			
			else if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
				columns += "\"" + avector[0].elementAt(avector[0].size() - 1)
						+ "\" LIKE \'%" + searchS + "%\'";
			else
				columns += avector[0].elementAt(avector[0].size() - 1)
						+ " LIKE \'%" + searchS + "%\'";

		String tb_like_query = "";
		if (isCount)
			tb_like_query = "SELECT count(*) as COUNT FROM " + _table
					+ " WHERE " + columns;
		else
			tb_like_query = "SELECT *  FROM " + _table + " WHERE " + columns;

		return tb_like_query;
	}

	/* Get like for SearchDB Query for specified columns */
	public String get_like_table_cols(String searchS, Vector<String> avector, boolean isCount) {

		String columns = "";
		if (avector == null ) return null;
		
		for (int j = 0; j < avector.size() - 1; j++) {
			
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				columns += "\"" + avector.elementAt(j) + "\"::text ILIKE \'%"
						+ searchS + "%\' OR ";
			else if (_dtype.compareToIgnoreCase("informix") == 0)
				columns += avector.elementAt(j) + "::VARCHAR(255) LIKE \'%"
				+ searchS + "%\' OR ";
			else if (_dtype.compareToIgnoreCase("splice") == 0 )
                columns +=  "CAST(" + avector.elementAt(j) + " AS CHAR(250)) LIKE \'%" + searchS + "%\' OR ";

			else if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
				columns += "\"" + avector.elementAt(j) + "\" LIKE \'%"
						+ searchS + "%\' OR ";
			else
				columns += avector.elementAt(j) + " LIKE \'%" + searchS
						+ "%\' OR ";
		}
		if (avector.size() != 0)
			
			if (_dtype.compareToIgnoreCase("postgres") == 0)
				columns += "\"" + avector.elementAt(avector.size() - 1) + "\"::text ILIKE \'%"
						+ searchS + "%\'";
			else if (_dtype.compareToIgnoreCase("informix") == 0)
				columns += avector.elementAt(avector.size() - 1)
				+ " ::VARCHAR(255) LIKE \'%" + searchS + "%\'";
			else if (_dtype.compareToIgnoreCase("Splice") == 0 )
				columns +=  "CAST(" + avector.elementAt(avector.size() - 1) + " AS CHAR(250)) LIKE \'%" + searchS + "%\' ";

			else if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
				columns += "\"" + avector.elementAt(avector.size() - 1)
						+ "\" LIKE \'%" + searchS + "%\'";
			else
				columns += avector.elementAt(avector.size() - 1)
						+ " LIKE \'%" + searchS + "%\'";

		String tb_like_query = "";
		if (isCount)
			tb_like_query = "SELECT count(*) as COUNT FROM " + _table
					+ " WHERE " + columns;
		else
			tb_like_query = "SELECT *  FROM " + _table + " WHERE " + columns;
		
		return tb_like_query;
	}

	/* Get All Ordered Query */
	public String get_tb_value(boolean isOrd) {
		String table = _table.charAt(0) == '"' ? _table.replaceAll("\"", "")
				: _table;
		String cat = Rdbms_NewConn.get().getHValue("Database_Catalog");
					
		if (!(cat == null || "".equals(cat)) && cat.charAt(0)=='"') {
				table = table.substring(cat.length()-1); //strip cat
			} else if (!(cat == null || "".equals(cat))) {
				table = table.substring(cat.length()+1); //strip cat
				table = table.charAt(0) == '"' ? table.replaceAll("\"", "") : table;
		}
		
		Vector<String> vector = Rdbms_NewConn.get().getTable(); // this will be without catalog append.
		int i = vector.indexOf(table);

		Vector<?> avector[] = null;
		avector = TableMetaInfo.populateTable(5, i, i + 1, avector);
		String columns = "";
		if (avector == null )return null;
		
		for (int j = 0; j < avector[0].size() - 1; j++) {
			if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
				columns += "\"" + avector[0].elementAt(j) + "\"" + ",";
			else
				columns += avector[0].elementAt(j) + ",";
		}
		if (avector[0].size() != 0)
			if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
				columns += "\"" + avector[0].elementAt(avector[0].size() - 1)
						+ "\"";
			else
				columns += avector[0].elementAt(avector[0].size() - 1);

		String tb_query = "SELECT " + columns + " FROM " + _table;
		if (isCond)
			tb_query = tb_query + " WHERE " + _cond_q;
		if (isOrd)
			tb_query += " order by " + _column;

		return tb_query;
	}

	/* For mapping to DB */
	public String[] get_mapping_query(Hashtable<String, Vector<String>> tb,
			Vector<String> tableV) {
		String[] map_query = new String[tb.size()];
		String cat = Rdbms_NewConn.get().getHValue("Database_Catalog");
		int index = 0;

		for (Enumeration<String> e = tb.keys(); e.hasMoreElements(); index++) {
			String cols = "";
			String table = e.nextElement();
			tableV.add(table);

			Vector<String> vc = tb.get(table);
			if (vc == null)
				System.out.println("\n ERROR:Could not Find:" + table);

			if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
				if (!table.startsWith("\""))
					table = "\"" + table + "\"";
			}
			if (!(cat == null || "".equals(cat)))
				table = cat + "." + table;

			for (int i = 0; i < vc.size(); i++) {
				String col = vc.elementAt(i);
				if (_dtype.compareToIgnoreCase("mysql") != 0 
						&& _dtype.compareToIgnoreCase("hive") != 0
						&& _dtype.compareToIgnoreCase("informix") != 0
						&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
						&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
								Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
					if (!col.startsWith("\""))
						col = "\"" + col + "\"";
				}
				if ("".equals(cols))
					cols = table + "." + col;
				else
					cols = cols + "," + table + "." + col;
			}
			map_query[index] = "SELECT " + cols + " FROM " + table;
		}
		return map_query;
	}

	/* For Synch mapping to DB */
	public Vector<String> get_synch_mapping_query(Vector<String> table_s,
			Vector<String> column_s) {
		Vector<String> synch_map_query = new Vector<String>();
		String cat = Rdbms_NewConn.get().getHValue("Database_Catalog");

		for (int index = 0; index < table_s.size(); index++) {
			String table = table_s.get(index);
			String col = column_s.get(index);

			if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
				if (!table.startsWith("\""))
					table = "\"" + table + "\"";
			}
			if (!(cat == null || "".equals(cat)))
				table = cat + "." + table;

			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
				if (!col.startsWith("\""))
					col = "\"" + col + "\"";
			}
			String query = "SELECT " + table + "." + col + " FROM " + table;
			synch_map_query.add(query);
		}
		return synch_map_query;
	}

	/* For Duplicate rows */
	public String get_table_duprow_query(Vector<?> col_vc, String cond) {
		String dup_row_query = "";
		String columns = "";
		String column = "";
		Enumeration<?> cols = col_vc.elements();
		while (cols.hasMoreElements()) {
			column = cols.nextElement().toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
				if (!column.startsWith("\""))
					column = "\"" + column + "\"";
			}
			if ("".equals(columns))
				columns = columns + column;
			else
				columns = columns + "," + column;
		}
		dup_row_query = "SELECT count(" + column + ") as count" + "," + columns
				+ " from " + _table;
		if (!"".equals(cond))
			dup_row_query += " WHERE " + cond;
		dup_row_query += " GROUP BY " + columns + " HAVING COUNT(*) > 1 ";

		return dup_row_query;
	}

	/* For Prepared equal to query */
	public String get_equal_query(Vector<?> col_vc, String condition) {
		String equal_query = "";
		String columns = "";
		String column = "";
		Enumeration<?> cols = col_vc.elements();
		while (cols.hasMoreElements()) {
			column = cols.nextElement().toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
				if (!column.startsWith("\""))
					column = "\"" + column + "\"";
			}
			if (cols.hasMoreElements())
				column += "= ? AND ";
			else
				column += "= ? ";
			columns += column;
		}
		equal_query = "SELECT * from " + _table + " WHERE " + columns;
		if (condition != null && "".equals(condition) == false)
			equal_query = equal_query + " AND (" + condition + ")";

		return equal_query;
	}

	/* For Completeness Inclusive/Exclusive query */
	public String get_inclusive_query(Vector<?> col_vc, boolean isInclusive) {
		String inclusive_query = "";
		String columns = "";
		String column = "";
		String inclusive = "";

		if (isInclusive == true)
			inclusive = " AND ";
		else
			inclusive = " OR ";

		Enumeration<?> cols = col_vc.elements();
		while (cols.hasMoreElements()) {
			column = cols.nextElement().toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
				if (!column.startsWith("\""))
					column = "\"" + column + "\"";
			}
			if (cols.hasMoreElements())
				column += " IS NULL" + inclusive;
			else
				column += " IS NULL";
			columns += column;
		}
		inclusive_query = "SELECT * from " + _table + " WHERE " + columns;
		return inclusive_query;
	}

	/* For getting selected Columns */
	public String get_selCol_query(Object[] col, String cond) {
		String selColQuery = "";
		String column = "";
		for (int i = 0; i < col.length; i++) {
			String colN = col[i].toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0 
					&& _dtype.compareToIgnoreCase("hive") != 0
					&& _dtype.compareToIgnoreCase("informix") != 0
					&& _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
					&& !(_dtype.compareToIgnoreCase("Others") == 0 && 
							Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 )) {
				if (!colN.startsWith("\""))
					colN = "\"" + colN + "\"";
			}
			if ("".equals(column) == false)
				column += ",";
			column += colN;
		}
		selColQuery = "SELECT " + column + " FROM " + _table;
		if (cond != null && "".equals(cond) == false)
			selColQuery += " WHERE " + cond;
		return selColQuery;

	}
	
	/**For Oracle-Compare query added to include cols since it was not working with oracle**/
    public String get_col_match_value(byte multiple, int mX, boolean match,
                  boolean isLeft) {
           
           String table =" ";
           String cat = Rdbms_NewConn.get().getHValue("Database_Catalog");
           
           if (_table.equals(_table1) == false && isLeft == false){
           table = _table1.charAt(0) == '"' ? _table1.replaceAll("\"", "")
                        : _table1;
           }
           else {
                  table = _table.charAt(0) == '"' ? _table.replaceAll("\"", "")
                               : _table;
           }
           
                               
           if (!(cat == null || "".equals(cat)) && cat.charAt(0)=='"') {
                        table = table.substring(cat.length()-1); //strip cat
                  } else if (!(cat == null || "".equals(cat))) {
                        table = table.substring(cat.length()+1); //strip cat
                        table = table.charAt(0) == '"' ? table.replaceAll("\"", "") : table;
           }
           
           Vector<String> vector = Rdbms_NewConn.get().getTable(); // this will be without catalog append.
           int i = vector.indexOf(table);

           Vector<?> avector[] = null;
           avector = TableMetaInfo.populateTable(5, i, i + 1, avector);
           String columns = "";
           if (avector == null )return null;
           
           for (int j = 0; j < avector[0].size() - 1; j++) {
                  if (_dtype.compareToIgnoreCase("mysql") != 0 
                		  && _dtype.compareToIgnoreCase("hive") != 0
                		  && _dtype.compareToIgnoreCase("informix") != 0
                		  && _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
                		  && !(_dtype.compareToIgnoreCase("Others") == 0 && 
          						Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
                        columns += "\"" + avector[0].elementAt(j) + "\"" + ",";
                  else
                        columns += avector[0].elementAt(j) + ",";
           }
           if (avector[0].size() != 0)
                  if (_dtype.compareToIgnoreCase("mysql") != 0 
                		  && _dtype.compareToIgnoreCase("hive") != 0
                		  && _dtype.compareToIgnoreCase("informix") != 0
                		  && _dtype.compareToIgnoreCase("ms_access_jdbc") != 0
                		  && !(_dtype.compareToIgnoreCase("Others") == 0 && 
          						Rdbms_NewConn.get().getHValue("Database_SupportQuote").compareToIgnoreCase("NO") ==0 ))
                        columns += "\"" + avector[0].elementAt(avector[0].size() - 1)
                                      + "\"";
                  else
                        columns += avector[0].elementAt(avector[0].size() - 1);
           
           String m_match_query = "";
           String match_str = "";
           if (match)
                  match_str = " IN (";
           else
                  match_str = " NOT IN (";

           if (_table.equals(_table1) == false && isLeft == false)
                  m_match_query = " SELECT  "+columns+" FROM " + _table1 + " WHERE " + _column1
                               + match_str;
           else
                  m_match_query = " SELECT  "+columns+" FROM " + _table + " WHERE " + _column
                               + match_str;

           if (_table.equals(_table1) == false)
                  m_match_query += " SELECT " + _table + "." + _column + " FROM "
                               + _table + "," + _table1 + " WHERE " + _table + "."
                               + _column + " = " + _table1 + "." + _column1 + "  AND "
                               + _table + "." + _column + " IS NOT NULL GROUP BY "
                               + _table + "." + _column + " HAVING " + " count(" + _table
                               + "." + _column + ") ";
           else
                  m_match_query += " SELECT " + _table + "." + _column + " FROM "
                               + _table + " WHERE " + _table + "." + _column
                               + " IS NOT NULL GROUP BY " + _table + "." + _column
                               + " HAVING " + " count(" + _table + "." + _column + ") ";

           if (multiple == 0)
                  m_match_query += "= 1 )";
           else if (multiple == 1)
                  m_match_query += ">= 1 )";
           else if (multiple == 2)
                  m_match_query += "> 1 )";
           else if (multiple == 3)
                  m_match_query += "= " + mX + ")";

           if (isLeft)
                  m_match_query += " ORDER BY " + _column;
           else
                  m_match_query += " ORDER BY " + _column1;

           return m_match_query;
    }
    
    /* This function is to support count for rowset in hive as hive does not support last(), next() etc
     * Making it static to use as utility */
    
    public static String hive_count_query(String orgquery) {
    	String query = "SELECT COUNT(*) as total_count FROM ("+orgquery+") t1";
    	return query;
    	
    }
    // new code Join Query  Multi join query No Join query
    public String getJoinQuery(Hashtable<String, String> ruleDetails) {
        String j_query = null;
        String[] cond = ruleDetails.get("table_Names").split(" ");
        String dbtype = ruleDetails.get("Database_Type").toUpperCase();
        switch (dbtype) {
            case "ORACLE_NATIVE":
            case "ORACLE_ODBC":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " ON " + ruleDetails.get("condition_Names");
                }
                break;
            case "MYSQL":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " ON " + ruleDetails.get("condition_Names");
                }
            case "SQL_SERVER":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " ON " + ruleDetails.get("condition_Names");
                }
                break;
            case "INFORMIX":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "SPLICE":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " ON " + ruleDetails.get("condition_Names");
                }
                break;
            case "MS_ACCESS":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "POSTGRES":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " ON " + ruleDetails.get("condition_Names");
                }
                break;
            case "DB2":
            case "HIVE":
                if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " ON (" + ruleDetails.get("condition_Names") + ")";
                }
                break;
            default:
            	if( cond.length > 3 ) {
                    j_query = getMultiJoinQuery(ruleDetails, ruleDetails.get("Database_Type"));
                } else {
                    String temp;
                    temp = ruleDetails.get("table_Names");
                    temp = temp.replaceAll("-", " ");
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " ON (" + ruleDetails.get("condition_Names") + ")";
                }
                break;
        }
        return j_query;
    }
    
    private String getMultiJoinQuery(Hashtable<String, String> ruleDetails, String dbType) {
        String[] tables = ruleDetails.get("table_Names").split(" ");
        String[] condition = ruleDetails.get("condition_Names").split(" ");

        String query = "";
        
        if (tables.length == 5) {
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].contains("-")) {
                    tables[i] = tables[i].replaceAll("-", " ");
                }
            }
            switch( dbType ) {
                case "ORACLE_NATIVE":
                case "ORACLE_ODBC":
                case "MYSQL":
                case "SQL_SERVER":
                case "SPLICE":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + condition[2];
                    break;
                case "MS_ACCESS":
                case "INFORMIX":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " WHERE " + condition[0] + " " + tables[3] + " " + tables[4] + " WHERE " + condition[2];
                    break;
                case "POSTGRES":
                case "DB2":
                case "HIVE":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + "(" + condition[2] + ")";
                    break;
                default:
                	query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + "(" + condition[2] + ")";
                    break;
            }
        }
        
        if( tables.length == 7 ) {
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].contains("-")) {
                    tables[i] = tables[i].replaceAll("-", " ");
                }
            }
            switch( dbType ) {
            	case "ORACLE_NATIVE":
            	case "ORACLE_ODBC":
            	case "MYSQL":
            	case "SQL_SERVER":
            	case "SPLICE":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + condition[2] + " " + tables[5] + " " + tables[6] + " ON " + condition[4];
                    break;
                case "MS_ACCESS":
                case "INFORMIX":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " WHERE " + condition[0] + " " + tables[3] + " " + tables[4] + " WHERE " + condition[2] + " " + tables[5] + " " + tables[6] + " WHERE " + condition[4];
                    break;
                case "POSTGRES":
                case "DB2":
                case "HIVE":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + "(" + condition[2] + ") " + tables[5] + " " + tables[6] + " ON " + "(" + condition[4] + ")";
                    break;
                default:
                	query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + "(" + condition[2] + ") " + tables[5] + " " + tables[6] + " ON " + "(" + condition[4] + ")";
                    break;
            }
        }
        
        if( tables.length == 9 ) {
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].contains("-")) {
                    tables[i] = tables[i].replaceAll("-", " ");
                }
            }
            switch( dbType ) {
        		case "ORACLE_NATIVE":
        		case "ORACLE_ODBC":
        		case "MYSQL":
        		case "SQL_SERVER":
        		case "SPLICE":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + condition[2] + " " + tables[5] + " " + tables[6] + " ON " + condition[4] + " " + tables[7] + tables[8] + " ON " + condition[6];
                    break;
                case "MS_ACCESS":
                case "INFORMIX":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " WHERE " + condition[0] + " " + tables[3] + " " + tables[4] + " WHERE " + condition[2] + " " + tables[5] + " " + tables[6] + " WHERE " + condition[4] + " " + tables[7] + tables[8] + " WHERE " + condition[6];
                    break;
                case "POSTGRES":
                case "DB2":
                case "HIVE":
                    query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + "(" + condition[2] + ")" + tables[5] + " " + tables[6] + " ON " + "(" + condition[4] + ") " + tables[7] + tables[8] + " ON " + "(" + condition[6] + ")";
                    break;
                default:
                	query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + tables[0] + " " + tables[1] + " " + tables[2] + " ON " + condition[0] + " " + tables[3] + " " + tables[4] + " ON " + "(" + condition[2] + ")" + tables[5] + " " + tables[6] + " ON " + "(" + condition[4] + ") " + tables[7] + tables[8] + " ON " + "(" + condition[6] + ")";
                    break;
            }
        }
        return query;
    }
    
    public String getNonJoinQuery(Hashtable<String, String> ruleDetails) {
        String j_query = null;
        switch (ruleDetails.get("Database_Type")) {
            case "ORACLE_NATIVE":
            case "ORACLE_ODBC":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "MYSQL":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
            case "SQL_SERVER":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "INFORMIX":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "SPLICE":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "MS_ACCESS":
                break;
            case "POSTGRES":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "DB2":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
            case "HIVE":
                if (ruleDetails.get("condition_Names").equals("")) {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                } else {
                    j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                }
                break;
             default:
            	 if (ruleDetails.get("condition_Names").equals("")) {
                     j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names");
                 } else {
                     j_query = "SELECT " + ruleDetails.get("column_Names") + " FROM " + ruleDetails.get("table_Names") + " WHERE " + ruleDetails.get("condition_Names");
                 }
                break;
        }
        return j_query;
    }

    
    public List<String> get_all_tables(Hashtable<String, String> dbDetails) {
    	ArrayList<String >db_tables = new ArrayList<String>();
    	Rdbms_NewConn dbmsConn = null;
    	ResultSet rs = null;
    	
        try {
        	  Rdbms_NewConn.init(dbDetails);
        	  Rdbms_NewConn.get().openConn();
            System.out.println("Connected to " + dbDetails.get("Database_ConnName"));
            DatabaseMetaData md = dbmsConn.getMetaData();
            rs = md.getTables(null, null, "%", null);

            while (rs.next()) {
                db_tables.add(rs.getString("TABLE_NAME"));
            }
            dbmsConn.closeConn();
            rs.close();
           
            return db_tables;
        } catch (SQLException ex) {
            Logger.getLogger(QueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return db_tables;
    }
    public List<String> get_all_cols(Hashtable<String, String> dbDetails, String t_name) {
    	ArrayList<String >db_cols = new ArrayList<String>();
    	Rdbms_NewConn dbmsConn = null;
    	ResultSet rsCol = null;
    	
        try {
        	  Rdbms_NewConn.init(dbDetails);
        	  dbmsConn = Rdbms_NewConn.get();

            dbmsConn.openConn();

            rsCol = dbmsConn.getMetaData().getColumns(null, null, t_name, "%");

            while (rsCol.next()) {
                db_cols.add(rsCol.getString("COLUMN_NAME"));
            }

            dbmsConn.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(QueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return db_cols;

    }
    
    // End of new code
    
	public static void setCond(String query) {
		isCond = true;
		_cond_q = "(" + query + ")";
	}

	public static void unsetCond() {
		isCond = false;
		_cond_q = "";
	}

	public static String getCond() {
		if (isCond)
			return _cond_q;
		else
			return "";

	}

	public static Vector<?>[] getDateCondition() {
		if (dateVar == null)
			return dateVar = new Vector[2];
		return dateVar;
	}

	public static void setDateCondition(Vector<?>[] vc) {
		dateVar = vc;
	}

	public String get_dsn() {
		return _dsn;
	}

	public void set_dsn(String _dsn) {
		this._dsn = _dsn;
	}

}
