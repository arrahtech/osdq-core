package org.arrah.framework.ndtable;

/***********************************************
 * Copyright to Vivek Kumar Singh * * Any part of code or file can be changed, *
 * redistributed, modified with the copyright * information intact * * Author$ :
 * Vivek Singh * *
 ***********************************************/

/*
 * This class defines the get/set for Attributes of Columns that is used for
 * displaying the columns for importing files
 */

public class ColumnAttr {
	int colId;
	String colName;
	String colSep;
	int colType;
	int colWidth;

	// Constructor
	public ColumnAttr() {
	};

	public ColumnAttr(int id) {
		colId = id;
	};

	public int getType() {
		return colType;
	}

	public void setType(int type) {
		colType = type;
	}

	public String getName() {
		return colName;
	}

	public void setName(String name) {
		colName = name;
	}

	public int getWidth() {
		return colWidth;
	}

	public void setWidth(int width) {
		colWidth = width;
	}

	public String getSep() {
		return colSep;
	}

	public void setSep(String sep) {
		colSep = sep;
	}

	public int getId() {
		return colId;
	}

	public void setId(int id) {
		colId = id;
	}
}
