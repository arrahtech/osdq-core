package org.arrah.framework.profile;

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

/* This file is used for creating DB Metadata info 
 *
 */

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_NewConn;
import org.arrah.framework.rdbms.Rdbms_conn;
import org.arrah.framework.rdbms.SqlType;
import org.arrah.framework.rdbms.TableRelationInfo;

public class DBMetaInfo {
	private Hashtable<String, TableRelationInfo> hashtable = null,
			hashtable1 = null, hashtable2 = null;
	private DatabaseMetaData dbmd;
	private ReportTableModel rtm__;
	private Rdbms_NewConn _newConn = null;

	public DBMetaInfo() {
		rtm__ = null;
	}

	public DBMetaInfo(Rdbms_NewConn newConn) {
		_newConn = newConn;
		rtm__ = null;
	}
	
	public ReportTableModel getGeneralInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();

		rtm__ = new ReportTableModel(new String[] { "Property", "Value" });
		try {
			String s1 = dbmd.getDatabaseProductName();
			rtm__.addFillRow(new String[] { "Database Product", s1 });
		} catch (UnsupportedOperationException unsupportedoperationexception1) {
		}
		try {
			String s2 = dbmd.getDatabaseProductVersion();
			rtm__.addFillRow(new String[] { "Database Version", s2 });
		} catch (UnsupportedOperationException unsupportedoperationexception2) {
		}
		try {
			String s3 = dbmd.getURL();
			rtm__.addFillRow(new String[] { "URL for this DBMS", s3 });
		} catch (UnsupportedOperationException unsupportedoperationexception3) {
		}
		try {
			String s4 = dbmd.getDriverName();
			rtm__.addFillRow(new String[] { "JDBC Driver Name", s4 });
		} catch (UnsupportedOperationException unsupportedoperationexception4) {
		}
		try {
			String s5 = dbmd.getDriverVersion();
			rtm__.addFillRow(new String[] { "JDBC Driver Version", s5 });
		} catch (UnsupportedOperationException unsupportedoperationexception5) {
		}
		try {
			String s6 = dbmd.getExtraNameCharacters();
			rtm__.addFillRow(new String[] {
					"EXTRA characters used in unquoted identifier names", s6 });
		} catch (UnsupportedOperationException unsupportedoperationexception6) {
		}
		try {
			String s7 = dbmd.getIdentifierQuoteString();
			rtm__.addFillRow(new String[] {
					"String used to quote SQL identifiers", s7 });
		} catch (UnsupportedOperationException unsupportedoperationexception7) {
		}
		try {
			String s8 = dbmd.getCatalogSeparator();
			rtm__.addFillRow(new String[] {
					"Separator between a Catalog and Table name", s8 });
		} catch (UnsupportedOperationException unsupportedoperationexception8) {
		}
		try {
			String s9 = dbmd.getCatalogTerm();
			rtm__.addFillRow(new String[] {
					"Database vendor preferred term for CATALOG", s9 });
		} catch (UnsupportedOperationException unsupportedoperationexception9) {
		}
		try {
			boolean flag = dbmd.isCatalogAtStart();
			rtm__.addFillRow(new String[] {
					"Catalog appears at the start of a fully qualified table name",
					!flag ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception10) {
		}
		try {
			String s10 = dbmd.getSchemaTerm();
			rtm__.addFillRow(new String[] {
					"Database vendor preferred term for SCHEMA", s10 });
		} catch (UnsupportedOperationException unsupportedoperationexception11) {
		}
		try {
			String s11 = dbmd.getProcedureTerm();
			rtm__.addFillRow(new String[] {
					"Database vendor preferred term for PROCEDURE", s11 });
		} catch (UnsupportedOperationException unsupportedoperationexception12) {
		}
		try {
			String s12 = dbmd.getSearchStringEscape();
			rtm__.addFillRow(new String[] {
					"String that can be used to escape wildcard characters",
					s12 });
		} catch (UnsupportedOperationException unsupportedoperationexception13) {
		}
		try {
			boolean flag1 = dbmd.allProceduresAreCallable();
			rtm__.addFillRow(new String[] { "All Procedures are Callbale",
					!flag1 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception14) {
		}
		try {
			boolean flag2 = dbmd.allTablesAreSelectable();
			rtm__.addFillRow(new String[] { "All Table are Selectable ",
					!flag2 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception15) {
		}
		try {
			boolean flag3 = dbmd.isReadOnly();
			rtm__.addFillRow(new String[] { "Database is Read Only",
					!flag3 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception16) {
		}
		try {
			boolean flag4 = dbmd.locatorsUpdateCopy();
			rtm__.addFillRow(new String[] {
					"Updates made to a LOB  made on a copy or to LOB",
					!flag4 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception17) {
		}
		try {
			boolean flag5 = dbmd.nullPlusNonNullIsNull();
			rtm__.addFillRow(new String[] {
					"Concatenates NULL and non-NULL as NULL",
					!flag5 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception18) {
		}
		try {
			boolean flag6 = dbmd.nullsAreSortedAtEnd();
			rtm__.addFillRow(new String[] { "Nulls are Sorted at End ",
					!flag6 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception19) {
		}
		try {
			boolean flag7 = dbmd.nullsAreSortedHigh();
			rtm__.addFillRow(new String[] { "Nulls are Sorted High",
					!flag7 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception20) {
		}
		try {
			boolean flag8 = dbmd.storesLowerCaseIdentifiers();
			rtm__.addFillRow(new String[] {
					"Unquoted SQL identifiers stored in lower case",
					!flag8 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception21) {
		}
		try {
			boolean flag9 = dbmd.storesLowerCaseQuotedIdentifiers();
			rtm__.addFillRow(new String[] {
					"Quoted SQL identifiers stored in lower case",
					!flag9 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception22) {
		}
		try {
			boolean flag10 = dbmd.usesLocalFiles();
			rtm__.addFillRow(new String[] {
					"Database stores tables in a local file",
					!flag10 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception23) {
		}
		try {
			boolean flag11 = dbmd.usesLocalFilePerTable();
			rtm__.addFillRow(new String[] {
					"Database uses a file for each table",
					!flag11 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception24) {
		}
		try {
			boolean flag12 = dbmd.dataDefinitionIgnoredInTransactions();
			rtm__.addFillRow(new String[] {
					"Database ignores a data definition statement within a transaction",
					!flag12 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception25) {
		}
		try {
			boolean flag13 = dbmd.doesMaxRowSizeIncludeBlobs();
			rtm__.addFillRow(new String[] { "Max Row Size includes Blob",
					!flag13 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception26) {
		}
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getSupportInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		rtm__ = new ReportTableModel(new String[] { "Property", "Support" });
		try {
			boolean flag14 = dbmd.supportsAlterTableWithAddColumn();
			rtm__.addFillRow(new String[] {
					"Database supports ALTER TABLE with ADD column",
					!flag14 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception27) {
		}
		try {
			boolean flag15 = dbmd.supportsAlterTableWithDropColumn();
			rtm__.addFillRow(new String[] {
					"Database supports ALTER TABLE with DROP column",
					!flag15 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception28) {
		}
		try {
			boolean flag16 = dbmd.supportsANSI92EntryLevelSQL();
			rtm__.addFillRow(new String[] {
					"Databse supports ANSI192 Entry Level SQL",
					!flag16 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception29) {
		}
		try {
			boolean flag17 = dbmd.supportsANSI92IntermediateSQL();
			rtm__.addFillRow(new String[] {
					"Databse supports ANSI192 Intermediate  SQL",
					!flag17 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception30) {
		}
		try {
			boolean flag18 = dbmd.supportsANSI92FullSQL();
			rtm__.addFillRow(new String[] {
					"Database supports ANSI192 Full SQL",
					!flag18 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception31) {
		}
		try {
			boolean flag19 = dbmd.supportsBatchUpdates();
			rtm__.addFillRow(new String[] { "Database support Batch Update",
					!flag19 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception32) {
		}
		try {
			boolean flag20 = dbmd.supportsCatalogsInDataManipulation();
			rtm__.addFillRow(new String[] {
					"Database support catalog name in a data manipulation statement",
					!flag20 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception33) {
		}
		try {
			boolean flag21 = dbmd.supportsCatalogsInIndexDefinitions();
			rtm__.addFillRow(new String[] {
					"Database support catalog name in Index Definition statement",
					!flag21 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception34) {
		}
		try {
			boolean flag22 = dbmd.supportsCatalogsInPrivilegeDefinitions();
			rtm__.addFillRow(new String[] {
					"Database support catalog name in Privilege Definition statement",
					!flag22 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception35) {
		}
		try {
			boolean flag23 = dbmd.supportsCatalogsInProcedureCalls();
			rtm__.addFillRow(new String[] {
					"Database support catalog name in Procedure Call statement",
					!flag23 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception36) {
		}
		try {
			boolean flag24 = dbmd.supportsCatalogsInTableDefinitions();
			rtm__.addFillRow(new String[] {
					"Database support catalog name in Table Definition statement",
					!flag24 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception37) {
		}
		try {
			boolean flag25 = dbmd.supportsColumnAliasing();
			rtm__.addFillRow(new String[] {
					"Database supports Column Aliasing", !flag25 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception38) {
		}
		try {
			boolean flag26 = dbmd.supportsConvert();
			rtm__.addFillRow(new String[] {
					"Database supports the CONVERT for two given SQL types",
					!flag26 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception39) {
		}
		try {
			boolean flag27 = dbmd.supportsCoreSQLGrammar();
			rtm__.addFillRow(new String[] {
					"Database supports ODBC core SQL grammar",
					!flag27 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception40) {
		}
		try {
			boolean flag28 = dbmd.supportsCorrelatedSubqueries();
			rtm__.addFillRow(new String[] {
					"Database supports Correlated Subqueries",
					!flag28 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception41) {
		}
		try {
			boolean flag29 = dbmd
					.supportsDataDefinitionAndDataManipulationTransactions();
			rtm__.addFillRow(new String[] {
					"Database supports both data definition and data manipulation statements within a transaction",
					!flag29 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception42) {
		}
		try {
			boolean flag30 = dbmd.supportsDataManipulationTransactionsOnly();
			rtm__.addFillRow(new String[] {
					"Database supports data manipulation statements only within a transaction",
					!flag30 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception43) {
		}
		try {
			boolean flag31 = dbmd.supportsExpressionsInOrderBy();
			rtm__.addFillRow(new String[] {
					"Database supports Expressions in ORDER BY lists",
					!flag31 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception44) {
		}
		try {
			boolean flag32 = dbmd.supportsExtendedSQLGrammar();
			rtm__.addFillRow(new String[] {
					"Database supports ODBC Extended SQL Grammar",
					!flag32 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception45) {
		}
		try {
			boolean flag33 = dbmd.supportsFullOuterJoins();
			rtm__.addFillRow(new String[] {
					"Database supports Full nested Outer Join ",
					!flag33 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception46) {
		}
		try {
			boolean flag34 = dbmd.supportsGetGeneratedKeys();
			rtm__.addFillRow(new String[] {
					"Auto-generated keys can be retrieved after a statement has been executed",
					!flag34 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception47) {
		}
		try {
			boolean flag35 = dbmd.supportsGroupBy();
			rtm__.addFillRow(new String[] {
					"Database supports some form of GROUP BY",
					!flag35 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception48) {
		}
		try {
			boolean flag36 = dbmd.supportsGroupByUnrelated();
			rtm__.addFillRow(new String[] {
					"Database supports using a column that is not in the SELECT statement in a GROUP BY clause",
					!flag36 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception49) {
		}
		try {
			boolean flag37 = dbmd.supportsIntegrityEnhancementFacility();
			rtm__.addFillRow(new String[] {
					"Database supports the SQL Integrity Enhancement Facility",
					!flag37 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception50) {
		}
		try {
			boolean flag38 = dbmd.supportsLikeEscapeClause();
			rtm__.addFillRow(new String[] {
					"Database supports specifying a LIKE escape clause",
					!flag38 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception51) {
		}
		try {
			boolean flag39 = dbmd.supportsLimitedOuterJoins();
			rtm__.addFillRow(new String[] {
					"Database has limited support for outer joins",
					!flag39 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception52) {
		}
		try {
			boolean flag40 = dbmd.supportsMinimumSQLGrammar();
			rtm__.addFillRow(new String[] {
					"Database supports the ODBC Minimum SQL grammar",
					!flag40 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception53) {
		}
		try {
			boolean flag41 = dbmd.supportsNamedParameters();
			rtm__.addFillRow(new String[] {
					"Database supports named parameters to callable statements",
					!flag41 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception54) {
		}
		try {
			boolean flag42 = dbmd.supportsNonNullableColumns();
			rtm__.addFillRow(new String[] {
					"Columns in this database may be defined as non-nullable",
					!flag42 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception55) {
		}
		try {
			boolean flag43 = dbmd.supportsOrderByUnrelated();
			rtm__.addFillRow(new String[] {
					"Database supports using a column that is not in the SELECT statement in an ORDER BY clause",
					!flag43 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception56) {
		}
		try {
			boolean flag44 = dbmd.supportsOuterJoins();
			rtm__.addFillRow(new String[] {
					"Database supports some form of Outer Join",
					!flag44 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception57) {
		}
		try {
			boolean flag45 = dbmd.supportsPositionedUpdate();
			rtm__.addFillRow(new String[] {
					"Database supports positioned UPDATE statements",
					!flag45 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception58) {
		}
		try {
			boolean flag46 = dbmd.supportsPositionedDelete();
			rtm__.addFillRow(new String[] {
					"Database supports positioned DELETE statements",
					!flag46 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception59) {
		}
		try {
			boolean flag47 = dbmd.supportsSavepoints();
			rtm__.addFillRow(new String[] { "Database supports Save Points",
					!flag47 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception60) {
		}
		try {
			boolean flag48 = dbmd.supportsStatementPooling();
			rtm__.addFillRow(new String[] {
					"Database supports statement pooling",
					!flag48 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception61) {
		}
		try {
			boolean flag49 = dbmd.supportsSubqueriesInQuantifieds();
			rtm__.addFillRow(new String[] {
					"Database supports subqueries in quantified expressions",
					!flag49 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception62) {
		}
		try {
			boolean flag50 = dbmd.supportsSubqueriesInIns();
			rtm__.addFillRow(new String[] {
					"Database supports subqueries in IN statements",
					!flag50 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception63) {
		}
		try {
			boolean flag51 = dbmd.supportsSubqueriesInExists();
			rtm__.addFillRow(new String[] {
					"Database supports subqueries in EXISTS expressions",
					!flag51 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception64) {
		}
		try {
			boolean flag52 = dbmd.supportsTransactions();
			rtm__.addFillRow(new String[] { "Database supports transactions",
					!flag52 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception65) {
		}
		try {
			boolean flag53 = dbmd.supportsTableCorrelationNames();
			rtm__.addFillRow(new String[] {
					"Database supports table correlation names",
					!flag53 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception66) {
		}
		try {
			boolean flag54 = dbmd.supportsUnion();
			rtm__.addFillRow(new String[] { "Database supports SQL UNION",
					!flag54 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception67) {
		}
		try {
			boolean flag55 = dbmd.supportsUnionAll();
			rtm__.addFillRow(new String[] { "Database supports SQL UNION ALL",
					!flag55 ? "NO" : "YES" });
		} catch (UnsupportedOperationException unsupportedoperationexception68) {
		}
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getLimitationInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		rtm__ = new ReportTableModel(new String[] { "Property", "Value" });
		rtm__.addFillRow(new String[] { "",
				"Value of \"0\" means Undefined or Unlimited" });
		int i = dbmd.getMaxBinaryLiteralLength();
		rtm__.addFillRow(new String[] { "Maximum Binary Literal Length",
				String.valueOf(i) });
		int j = dbmd.getMaxCatalogNameLength();
		rtm__.addFillRow(new String[] { "Maximum Catalog Name Length",
				String.valueOf(j) });
		int i1 = dbmd.getMaxCharLiteralLength();
		rtm__.addFillRow(new String[] { "Maximum Character Literal Length",
				String.valueOf(i1) });
		int k1 = dbmd.getMaxColumnNameLength();
		rtm__.addFillRow(new String[] { "Maximum Column Name Length",
				String.valueOf(k1) });
		int i2 = dbmd.getMaxColumnsInGroupBy();
		rtm__.addFillRow(new String[] { "Maximum Column in GROUP BY clause",
				String.valueOf(i2) });
		int k2 = dbmd.getMaxColumnsInIndex();
		rtm__.addFillRow(new String[] { "Maximum Column in Index ",
				String.valueOf(k2) });
		int i3 = dbmd.getMaxColumnsInOrderBy();
		rtm__.addFillRow(new String[] { "Maximum Column in ORDER BY clause ",
				String.valueOf(i3) });
		int k3 = dbmd.getMaxColumnsInSelect();
		rtm__.addFillRow(new String[] { "Maximum Column in SELECT clause",
				String.valueOf(k3) });
		int i4 = dbmd.getMaxColumnsInTable();
		rtm__.addFillRow(new String[] { "Maximum Column in a TABLE ",
				String.valueOf(i4) });
		int j4 = dbmd.getMaxConnections();
		rtm__.addFillRow(new String[] { "Maximum Connections ",
				String.valueOf(j4) });
		int k4 = dbmd.getMaxCursorNameLength();
		rtm__.addFillRow(new String[] { "Maximum Cursor Name Length",
				String.valueOf(k4) });
		int l4 = dbmd.getMaxIndexLength();
		rtm__.addFillRow(new String[] { "Maximum Index Length",
				String.valueOf(l4) });
		int i5 = dbmd.getMaxProcedureNameLength();
		rtm__.addFillRow(new String[] { "Maximum  Procedure Name Length",
				String.valueOf(i5) });
		int k6 = dbmd.getMaxRowSize();
		rtm__.addFillRow(new String[] { "Maximum Row Size", String.valueOf(k6) });
		int l6 = dbmd.getMaxSchemaNameLength();
		rtm__.addFillRow(new String[] { "Maximum Binary Literal Length",
				String.valueOf(l6) });
		int i7 = dbmd.getMaxStatementLength();
		rtm__.addFillRow(new String[] { "Maximum Schema Name Length",
				String.valueOf(i7) });
		int j7 = dbmd.getMaxStatements();
		rtm__.addFillRow(new String[] { "Maximum Statements Count",
				String.valueOf(j7) });
		int k7 = dbmd.getMaxTableNameLength();
		rtm__.addFillRow(new String[] { "Maximum Table Name Length",
				String.valueOf(k7) });
		int l7 = dbmd.getMaxTablesInSelect();
		rtm__.addFillRow(new String[] { "Maximum Tables in SELECT clause ",
				String.valueOf(l7) });
		int i8 = dbmd.getMaxUserNameLength();
		rtm__.addFillRow(new String[] { "Maximum User Name Length",
				String.valueOf(i8) });
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getFunctionInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();

		String as[] = null;
		String as1[] = null;
		String as2[] = null;
		String as3[] = null;
		String as4[] = null;
		int j3 = 0;
		rtm__ = new ReportTableModel(new String[] { "String Fx", "Numeric Fx",
				"Date Fx", "System Fx", "SQL Keywords" });
		String s48 = dbmd.getStringFunctions();
		as = s48.split(",");
		String s54 = dbmd.getNumericFunctions();
		as1 = s54.split(",");
		String s61 = dbmd.getTimeDateFunctions();
		as2 = s61.split(",");
		String s70 = dbmd.getSystemFunctions();
		as3 = s70.split(",");
		String s77 = dbmd.getSQLKeywords();
		as4 = s77.split(",");
		for (int j5 = 0; j5 < as.length; j5++) {
			rtm__.addRow();
			j3 = j5;
			if (as[j5].compareTo("") != 0)
				rtm__.setValueAt(as[j5] + "( )", j5, 0);
		}

		for (int k5 = 0; k5 < as1.length; k5++) {
			if (k5 > j3) {
				rtm__.addRow();
				j3 = k5;
			}
			if (as1[k5].compareTo("") != 0)
				rtm__.setValueAt(as1[k5] + "( )", k5, 1);
		}

		for (int l5 = 0; l5 < as2.length; l5++) {
			if (l5 > j3) {
				rtm__.addRow();
				j3 = l5;
			}
			if (as2[l5].compareTo("") != 0)
				rtm__.setValueAt(as2[l5] + "( )", l5, 2);
		}

		for (int i6 = 0; i6 < as3.length; i6++) {
			if (i6 > j3) {
				rtm__.addRow();
				j3 = i6;
			}
			if (as3[i6].compareTo("") != 0)
				rtm__.setValueAt(as3[i6] + "( )", i6, 3);
		}

		for (int j6 = 0; j6 < as4.length; j6++) {
			if (j6 > j3) {
				rtm__.addRow();
				j3 = j6;
			}
			if (as4[j6].compareTo("") != 0)
				rtm__.setValueAt(as4[j6], j6, 4);
		}
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getCatalogInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		rtm__ = new ReportTableModel(new String[] { "Index", "Catalog" });
		ResultSet resultset = dbmd.getCatalogs();
		int k = 0;
		do {
			if (!resultset.next())
				break;
			String s21 = resultset.getString(1);
			if (s21.compareTo("") != 0)
				rtm__.addFillRow(new String[] { "" + ++k, s21 });
		} while (true);
		resultset.close();
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getStandardSQLInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		rtm__ = new ReportTableModel(
				new String[] { "Name", "Data_Type", "Precision", "Prefix",
						"Suffix", "Param", "Nullable", "Case Sensitive",
						"Searchable", "Unsigned", "Auto Increamental" });
		ResultSet resultset1;
		String as10[];
		for (resultset1 = dbmd.getTypeInfo(); resultset1.next(); rtm__
				.addFillRow(as10)) {
			String s15 = resultset1.getString(1);
			String s22 = SqlType.getTypeName(resultset1.getInt(2));
			String s27 = resultset1.getString(3);
			String s32 = resultset1.getString(4);
			String s36 = resultset1.getString(5);
			String s43 = resultset1.getString(6);
			short word2 = resultset1.getShort(7);
			String s55 = "";
			switch (word2) {
			case 0: // '\0'
				s55 = "No";
				break;

			case 1: // '\001'
				s55 = "Yes";
				break;

			case 2: // '\002'
				s55 = "Unknown";
				break;

			default:
				s55 = "UnSupported NULLable type";
				break;
			}
			String s62 = !resultset1.getBoolean(8) ? "False" : "True";
			short word4 = resultset1.getShort(9);
			String s78 = "";
			switch (word4) {
			case 0: // '\0'
				s78 = "No";
				break;

			case 1: // '\001'
				s78 = "Limited (Only LIKE Supported)";
				break;

			case 2: // '\002'
				s78 = "Limited (Except LIKE Supported)";
				break;

			case 3: // '\003'
				s78 = "Yes";
				break;

			default:
				s78 = "UnSupported NULLable type";
				break;
			}
			String s82 = !resultset1.getBoolean(10) ? "False" : "True";
			String s86 = !resultset1.getBoolean(12) ? "False" : "True";
			as10 = (new String[] { s15, s22, s27, s32, s36, s43, s55, s62, s78,
					s82, s86 });
		}
		resultset1.close();
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getUserSQLInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		rtm__ = new ReportTableModel(new String[] { "Name", "Class",
				"Data_Type", "Base_Type", "Remark", "Category", "Schema" });
		ResultSet resultset2;
		String as6[];
		for (resultset2 = dbmd.getUDTs(null, null, null, null); resultset2
				.next(); rtm__.addFillRow(as6)) {
			String s16 = resultset2.getString(1);
			String s23 = resultset2.getString(2);
			String s28 = resultset2.getString(3);
			String s33 = resultset2.getString(4);
			int l2 = resultset2.getInt(5);
			String s44 = "";
			switch (l2) {
			case 2000:
				s44 = "Java Object";
				break;

			case 2002:
				s44 = "Structure";
				break;

			case 2001:
				s44 = "Distinct";
				break;

			default:
				s44 = "UnSupported java type";
				break;
			}
			String s49 = resultset2.getString(6);
			String s56 = SqlType.getTypeName(resultset2.getShort(7));
			as6 = (new String[] { s28, s33, s44, s56, s49, s16, s23 });
		}
		resultset2.close();
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getSchemaInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		rtm__ = new ReportTableModel(new String[] { "Index", "Schema" });
		ResultSet resultset3 = dbmd.getSchemas();
		int l = 0;
		do {
			if (!resultset3.next())
				break;
			String s24 = resultset3.getString(1);
			if (s24.compareTo("") != 0)
				rtm__.addFillRow(new String[] { "" + ++l, s24 });
		} while (true);
		resultset3.close();
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getParameterInfo() throws SQLException {
		
		if (_newConn == null ) {
			Rdbms_conn.openConn();
			dbmd = Rdbms_conn.getMetaData();
		} else {
			_newConn.openConn();
			dbmd = _newConn.getMetaData();
		}
		
		ResultSet resultset5 = dbmd.getProcedureColumns(null, null, null, null);
		if (resultset5 != null)
			rtm__ = new ReportTableModel(new String[] { "Parameter", "Type",
					"Nullable", "Procedure", "Schema", "Category" });
		String as7[];
		for (; resultset5.next(); rtm__.addFillRow(as7)) {
			String s18 = resultset5.getString(1);
			String s26 = resultset5.getString(2);
			String s30 = resultset5.getString(3);
			String s35 = resultset5.getString(4);
			String s38 = "";
			short word1 = resultset5.getShort(5);
			switch (word1) {
			case 0: // '\0'
				s38 = "Unknown Parameter";
				break;

			case 1: // '\001'
				s38 = "IN Parameter";
				break;

			case 2: // '\002'
				s38 = "IN OUT Parameter";
				break;

			case 3: // '\003'
				s38 = "Result Column in ResultSet";
				break;

			case 4: // '\004'
				s38 = "OUT Parameter";
				break;

			case 5: // '\005'
				s38 = "Procedure return Value";
				break;

			default:
				s38 = "UnSupported Type";
				break;
			}
			String s50 = "";
			short word3 = resultset5.getShort(12);
			switch (word3) {
			case 0: // '\0'
				s50 = "No";
				break;

			case 1: // '\001'
				s50 = "Yes";
				break;

			case 2: // '\002'
				s50 = "Unknown";
				break;

			default:
				s50 = "UnSupported NULLable type";
				break;
			}
			as7 = (new String[] { s35, s38, s50, s30, s26, s18 });
		}

		if (resultset5 != null)
			resultset5.close();
		if (_newConn == null ) 
			Rdbms_conn.closeConn();
		else 
			_newConn.closeConn();
		return rtm__;
	}

	public ReportTableModel getProcedureInfo() throws SQLException {
		if (_newConn == null ) {
			Rdbms_conn.openConn();
			dbmd = Rdbms_conn.getMetaData();
		} else {
			_newConn.openConn();
			dbmd = _newConn.getMetaData();
		}
		
		ResultSet resultset4 = dbmd.getProcedures(null, null, null);
		if (resultset4 != null)
			rtm__ = new ReportTableModel(new String[] { "Procedure", "Remark",
					"Type", "Schema", "Category" });
		String as5[];
		for (; resultset4.next(); rtm__.addFillRow(as5)) {
			String s17 = resultset4.getString(1);
			String s25 = resultset4.getString(2);
			String s29 = resultset4.getString(3);
			String s34 = resultset4.getString(7);
			String s37 = "";
			short word0 = resultset4.getShort(8);
			switch (word0) {
			case 0: // '\0'
				s37 = "May/May Not return result";
				break;

			case 1: // '\001'
				s37 = "Does Not return result";
				break;

			case 2: // '\002'
				s37 = "Returns result";
				break;

			default:
				s37 = "Type not supported";
				break;
			}
			as5 = (new String[] { s29, s34, s37, s25, s17 });
		}
		resultset4.close();
		if (_newConn == null ) 
			Rdbms_conn.closeConn();
		else 
			_newConn.closeConn();
		return rtm__;
	}

	public ReportTableModel getTableModelInfo() throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();

		hashtable = new Hashtable<String, TableRelationInfo>();
		hashtable1 = new Hashtable<String, TableRelationInfo>();
		hashtable2 = new Hashtable<String, TableRelationInfo>();
		String s13 = Rdbms_conn.getHValue("Database_Catalog");
		s13 = "";
		String s19 = Rdbms_conn.getHValue("Database_SchemaPattern");
		s13 = s13.compareTo("") != 0 ? s13 : null;
		s19 = s19.compareTo("") != 0 ? s19 : null;
		Vector<String> vector = Rdbms_conn.getTable();
		int l1 = vector.size();
		for (int j2 = 0; j2 < l1; j2++) {
			String s39 = (String) vector.elementAt(j2);
			try {
				TableRelationInfo TableRelationInfo = new TableRelationInfo(s39);
				if (Rdbms_conn.getDBType().compareToIgnoreCase("ms_access") == 0) {
					ResultSet resultset9 = dbmd.getIndexInfo(s13, s19, s39,
							false, true);
					do {
						if (!resultset9.next())
							break;
						String s57 = resultset9.getString(9);
						String s63 = resultset9.getString(6);
						String s71 = resultset9.getString(3);
						if (s57 != null && s63 != null)
							if (s63.compareToIgnoreCase("primarykey") == 0) {
								TableRelationInfo.pk[TableRelationInfo.pk_c] = s57;
								TableRelationInfo.pk_index[TableRelationInfo.pk_c] = s63;
								TableRelationInfo.hasPKey = true;
								TableRelationInfo.pk_c++;
								TableRelationInfo.isRelated = true;
							} else if (s63.endsWith(s39)) {
								TableRelationInfo.fk[TableRelationInfo.fk_c] = s57;
								TableRelationInfo.fk_pKey[TableRelationInfo.fk_c] = null;
								TableRelationInfo.fk_pTable[TableRelationInfo.fk_c] = s63
										.substring(0, s63.lastIndexOf(s39));
								TableRelationInfo.hasFKey = true;
								TableRelationInfo.fk_c++;
								TableRelationInfo.isRelated = true;
							}
					} while (true);
					resultset9.close();
				} else {
					int l3 = 0;
					ResultSet resultset10 = dbmd.getPrimaryKeys(s13, s19, s39);
					do {
						if (!resultset10.next())
							break;
						String s64 = resultset10.getString(4);
						String s72 = resultset10.getString(6);
						if (s64 != null && s72 != null) {
							TableRelationInfo.pk[l3] = s64;
							TableRelationInfo.pk_index[l3] = s72;
							TableRelationInfo.hasPKey = true;
							l3++;
							TableRelationInfo.pk_c++;
						}
					} while (true);
					resultset10.close();
					l3 = 0;
					for (resultset10 = dbmd.getImportedKeys(s13, s19, s39); resultset10
							.next();) {
						String s65 = resultset10.getString(3);
						String s73 = resultset10.getString(4);
						String s79 = resultset10.getString(7);
						String s83 = resultset10.getString(8);
						TableRelationInfo.fk[l3] = s83;
						TableRelationInfo.fk_pKey[l3] = s73;
						TableRelationInfo.fk_pTable[l3] = s65;
						TableRelationInfo.hasFKey = true;
						TableRelationInfo.fk_c++;
						TableRelationInfo.isRelated = true;
						l3++;
					}
					resultset10.close();
					l3 = 0;
					for (resultset10 = dbmd.getExportedKeys(s13, s19, s39); resultset10
							.next();) {
						String s66 = resultset10.getString(3);
						String s74 = resultset10.getString(4);
						String s80 = resultset10.getString(7);
						String s84 = resultset10.getString(8);
						TableRelationInfo.pk_ex[l3] = s74;
						TableRelationInfo.pk_exKey[l3] = s84;
						TableRelationInfo.pk_exTable[l3] = s80;
						TableRelationInfo.hasExpKey = true;
						TableRelationInfo.exp_c++;
						TableRelationInfo.isRelated = true;
						l3++;
					}

					resultset10.close();
				}

				if (TableRelationInfo.isRelated)
					hashtable2.put(TableRelationInfo.tableName,
							TableRelationInfo);
				else if (TableRelationInfo.hasPKey)
					hashtable.put(TableRelationInfo.tableName,
							TableRelationInfo);
				else
					hashtable1.put(TableRelationInfo.tableName,
							TableRelationInfo);

			} catch (Exception exception) {
				System.out
						.println("\n WARNING: Unknown Exception Happened for Table:"
								+ s39);
				System.out.println("\n Message: " + exception.getMessage());
				exception.printStackTrace();
				continue;
			}
		}
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getTableMetaData(String tb_pattern)
			throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		String s14 = Rdbms_conn.getHValue("Database_SchemaPattern");
		String s20 = Rdbms_conn.getHValue("Database_Catalog");
		s20 = "";
		int j1 = 0;
		String s31 = tb_pattern;

		rtm__ = new ReportTableModel(new String[] { "Table", "Column", "DBType", "SQLType",
				"Size", "Precision", "Radix", "Remark", "Default", "Bytes",
				"Ordinal Pos", "Nullable" });
		if (s31 == null || s31.compareTo("") == 0)
			return rtm__;

		ResultSet resultset6;
		String as11[];
		for (resultset6 = dbmd.getColumns(s20.equals("") ? (s20 = null) : s20,
				s14.equals("") ? (s14 = null) : s14, s31, null); resultset6
				.next(); rtm__.addFillRow(as11)) {
			j1++;
			String s40 = resultset6.getString(3);
			String s45 = resultset6.getString(4);
			int i52 = resultset6.getInt(5);
			String s52 = SqlType.getTypeName(i52);
			String s51 = resultset6.getString(6);
			String s58 = resultset6.getString(7);
			String s67 = resultset6.getString(9);
			String s75 = resultset6.getString(10);
			String s81 = resultset6.getString(12);
			String s85 = resultset6.getString(13);
			String s87 = resultset6.getString(16);
			String s88 = resultset6.getString(17);
			String s89 = resultset6.getString(18);
			as11 = (new String[] { s40, s45, s51, s52, s58, s67, s75, s81, s85, s87,
					s88, s89 });
		}

		resultset6.close();
		if (j1 == 0) {
			System.out
					.println("Table does not Exist \n Or You might not have permission to run query on this table");
			return rtm__;
		}
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getTablePrivilege(String tb_pattern)
			throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();

		String s14 = Rdbms_conn.getHValue("Database_SchemaPattern");
		String s20 = Rdbms_conn.getHValue("Database_Catalog");
		s20 = "";
		int j1 = 0;
		String s31 = tb_pattern;

		ResultSet resultset7 = dbmd.getTablePrivileges(
				s20.compareTo("") != 0 ? s20 : (s20 = null),
				s14.compareTo("") != 0 ? s14 : (s14 = null),
				s31.compareTo("") != 0 ? s31 : (s31 = null));
		rtm__ = new ReportTableModel(new String[] { "Table", "Grantor",
				"Grantee", "Privileges", "Grantable" });
		if (s31 == null || s31.compareTo("") == 0)
			return rtm__;

		String as8[];
		for (; resultset7.next(); rtm__.addFillRow(as8)) {
			j1++;
			String s41 = resultset7.getString(3);
			String s46 = resultset7.getString(4);
			String s52 = resultset7.getString(5);
			String s59 = resultset7.getString(6);
			String s68 = resultset7.getString(7);
			as8 = (new String[] { s41, s46, s52, s59, s68 });
		}

		resultset7.close();
		if (j1 == 0) {
			System.out
					.println("Table does not Exist \n Or You might not have permisson to run query on this table");
			return rtm__;
		}
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public ReportTableModel getColumnPrivilege(String tb_pattern)
			throws SQLException {
		Rdbms_conn.openConn();
		dbmd = Rdbms_conn.getMetaData();
		String s14 = Rdbms_conn.getHValue("Database_SchemaPattern");
		String s20 = Rdbms_conn.getHValue("Database_Catalog");
		s20 = "";
		int j1 = 0;
		rtm__ = new ReportTableModel(new String[] { "Table", "Column",
				"Grantor", "Grantee", "Privileges", "Grantable" });
		String s31 = tb_pattern;
		if (s31 == null || s31.compareTo("") == 0)
			return rtm__;
		ResultSet resultset8;
		String as9[];
		for (resultset8 = dbmd.getColumnPrivileges(s20.compareTo("") != 0 ? s20
				: (s20 = null), s14.compareTo("") != 0 ? s14 : (s14 = null),
				s31, null); resultset8.next(); rtm__.addFillRow(as9)) {
			j1++;
			String s42 = resultset8.getString(3);
			String s47 = resultset8.getString(4);
			String s53 = resultset8.getString(5);
			String s60 = resultset8.getString(6);
			String s69 = resultset8.getString(7);
			String s76 = resultset8.getString(8);
			as9 = (new String[] { s42, s47, s53, s60, s69, s76 });
		}

		resultset8.close();
		if (j1 == 0) {
			System.out
					.println("Table does not Exist \n Or You might not have permission to run query on this Table's column");
			return rtm__;
		}
		Rdbms_conn.closeConn();
		return rtm__;
	}

	public Hashtable<String, TableRelationInfo> getNoPKTable() {
		return hashtable1;
	}

	public Hashtable<String, TableRelationInfo> getOnlyPKTable() {
		return hashtable;
	}

	public Hashtable<String, TableRelationInfo> getRelatedTable() {
		return hashtable2;
	}

	synchronized public static ReportTableModel queryDB(final String query) {
		
		final Vector<String> table = Rdbms_conn.getTable();
		
		final int[] vc = new int[table.size()];
		ReportTableModel _rt = new ReportTableModel(new String[] { "Count",
				"Table" });
		for (int i = 0; i < table.size(); i++) {
			final int index = i;
			QueryBuilder qb = new QueryBuilder(
					Rdbms_conn.getHValue("Database_DSN"), table.get(index)
							.toString(), Rdbms_conn.getDBType());
			final String toQuery = qb.get_like_table(query, index, true);
			if (toQuery == null || "".equals(toQuery)) continue;
			
			try {	
				synchronized (Rdbms_conn.class) {
				Rdbms_conn.openConn();
				ResultSet rs = Rdbms_conn.runQuery(toQuery);
				
				while (rs.next()) {
					int c = rs.getInt(1);
					vc[index] = c;
				}
				 rs.close();
				 Rdbms_conn.closeConn();  //  Other thread might have closed it
				}
			} catch (SQLException ee) {
				System.out.println("\n ResultSet SQL Error :" + ee.getMessage());
			}	finally {
			}
		}
		try {
			Rdbms_conn.closeConn();
		} catch (SQLException ee) {
			System.out.println("\n SQL Error:" + ee.getMessage());
		}
		for (int i = 0; i < table.size(); i++) {
			Object[] obj = new Object[2];
			obj[0] = vc[i];
			obj[1] = table.get(i);
			_rt.addFillRow(obj);
		}
		return _rt;
	}
	public static Vector<String> getDataTypeInfo() throws SQLException {
		Rdbms_conn.openConn();
		DatabaseMetaData dbmd = Rdbms_conn.getMetaData();
		
		Vector<String> dataTypeName = new Vector<String>();
		ResultSet resultset1;
		for (resultset1 = dbmd.getTypeInfo(); resultset1.next(); ) {
			String s15 = resultset1.getString(1);
			dataTypeName.add(s15);
		}
		resultset1.close();
		Rdbms_conn.closeConn();
		return dataTypeName;
	}
}
