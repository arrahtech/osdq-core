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
 * This file is used to map java classed to
 * sqlType 
 * 
 */
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.sql.Struct;
import java.util.Date;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

public class SqlType {

	public SqlType() {
	}

	public static Class<?> getClass(int i) {
		switch (i) {
		case java.sql.Types.ARRAY:
			return Array.class;

		case java.sql.Types.INTEGER:
		case java.sql.Types.TINYINT:
		case java.sql.Types.SMALLINT:
			return Integer.class;

		case java.sql.Types.DOUBLE:
		case java.sql.Types.REAL:
		case java.sql.Types.DECIMAL:
		case java.sql.Types.NUMERIC:
		case java.sql.Types.BIGINT:
			return Double.class;

		case java.sql.Types.FLOAT:
			return Float.class;

		case java.sql.Types.CLOB:
			return SerialClob.class;

		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
			return String.class;

		case java.sql.Types.BLOB:
			return SerialBlob.class;

		case java.sql.Types.BINARY:
			return byte.class;

		case java.sql.Types.LONGVARBINARY:
		case java.sql.Types.VARBINARY:
			return byte[].class;

		case java.sql.Types.BOOLEAN:
		case java.sql.Types.BIT:
			return Boolean.class;

		case java.sql.Types.DATE:
		case java.sql.Types.TIME:
		case java.sql.Types.TIMESTAMP:
			return Date.class;

		case java.sql.Types.REF:
			return Reference.class;

		case java.sql.Types.STRUCT:
			return Struct.class;

		default:
			return new Object().getClass();
		}
	}

	public static String getTypeName(int i) {
		String s = "";
		
		switch (i) {
		case java.sql.Types.ARRAY:
			return s = "Array";

		case java.sql.Types.BIGINT:
			return s = "Big Integer";

		case java.sql.Types.BINARY:
			return s = "Binary";

		case java.sql.Types.BIT:
			return s = "Bit";

		case java.sql.Types.BLOB:
			return s = "Blob";

		case java.sql.Types.BOOLEAN:
			return s = "Boolean";

		case java.sql.Types.CHAR:
			return s = "Char";

		case java.sql.Types.CLOB:
			return s = "Clob";

		case java.sql.Types.DATALINK:
			return s = "DataLink";

		case java.sql.Types.DATE:
			return s = "Date";

		case java.sql.Types.DECIMAL:
			return s = "Decimal";

		case java.sql.Types.DISTINCT:
			return s = "Distinct";

		case java.sql.Types.DOUBLE:
			return s = "Double";

		case java.sql.Types.FLOAT:
			return s = "Float";

		case java.sql.Types.INTEGER:
			return s = "Integer";

		case java.sql.Types.JAVA_OBJECT:
			return s = "Java Object";

		case java.sql.Types.LONGVARBINARY:
			return s = "Long VarBinary";

		case java.sql.Types.LONGVARCHAR:
			return s = "Long VarChar";

		case java.sql.Types.NULL:
			return s = "Null";

		case java.sql.Types.NUMERIC:
			return s = "Numeric";

		case java.sql.Types.OTHER:
			return s = "DB Specific";

		case java.sql.Types.REAL:
			return s = "Real";

		case java.sql.Types.REF:
			return s = "Ref";

		case java.sql.Types.SMALLINT:
			return s = "Small Integer";

		case java.sql.Types.STRUCT:
			return s = "Structure";

		case java.sql.Types.TIME:
			return s = "Time";

		case java.sql.Types.TIMESTAMP:
			return s = "TimeStamp";

		case java.sql.Types.TINYINT:
			return s = "Tiny Integer";

		case java.sql.Types.VARBINARY:
			return s = "VarBinary";

		case java.sql.Types.VARCHAR:
			return s = "VarChar";
		}
		s = "Undefined";
		return s;
	}

	public static String getMetaTypeName(String type) {
		String s = "String";
		if (type.compareToIgnoreCase("Big Integer") == 0
				|| type.compareToIgnoreCase("Decimal") == 0
				|| type.compareToIgnoreCase("Double") == 0
				|| type.compareToIgnoreCase("Float") == 0
				|| type.compareToIgnoreCase("Integer") == 0
				|| type.compareToIgnoreCase("Numeric") == 0
				|| type.compareToIgnoreCase("Real") == 0
				|| type.compareToIgnoreCase("Small Integer") == 0
				|| type.compareToIgnoreCase("Tiny Integer") == 0)
			s = "Number";

		if (type.compareToIgnoreCase("Date") == 0
				|| type.compareToIgnoreCase("Time") == 0
				|| type.compareToIgnoreCase("TimeStamp") == 0)
			s = "Date";

		return s;
	}
}
