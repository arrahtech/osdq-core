package org.arrah.framework.ndtable;

/***********************************************
 *     Copyright to vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This files is used for creating Report Table model.
 * We are using swing class but no UI.
 * This can have non editiable/editable values.
 *
 */

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.arrah.framework.rdbms.SqlType;

public class ReportTableModel implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Vector<Object> row_v = new Vector<Object>();
	private Vector<Object> column_v = new Vector<Object>();
	private int col_size = 0;
	private DefaultTableModel tabModel;
	private boolean _isEditable = false;
	private boolean showClass = false;
	private int[] classType = null;

	public ReportTableModel(String[] col) {
		addColumns(col);
		createTable(false);

	}

	public ReportTableModel(Object[] col) {
		addColumns(col);
		createTable(false);

	}

	public ReportTableModel(Object[] col, boolean isEditable) {
		addColumns(col);
		createTable(isEditable);

	}

	public ReportTableModel(String[] col, boolean isEditable) {
		addColumns(col);
		createTable(isEditable);

	}

	public ReportTableModel(String[] col, boolean isEditable, boolean colClass) {
		addColumns(col);
		createTable(isEditable);
		showClass = colClass;

	}

	public ReportTableModel(String[] col, int[] sqlType, boolean isEditable,
			boolean colClass) {
		addColumns(col);
		createTable(isEditable);
		showClass = colClass;
		classType = sqlType;

	}

	public ReportTableModel(Object[] col, boolean isEditable, boolean colClass) {
		addColumns(col);
		createTable(isEditable);
		showClass = colClass;

	}

	public ReportTableModel(String less, String more, String b_less1,
			String b_more1, String b_less2, String b_more2) {

		String[] columnNames = {
				"<html><b><i>Values</i></b></html>",
				"<html><b>Aggregate</i></b></html>",
				"<html><b> &lt;  <i>" + less + "</i></b></html>",
				"<html><b> &gt;  <i>" + more + "</i></b></html>",
				"<html><b><i>" + b_less1 + "</i>&lt;&gt;<i>" + b_more1
						+ "</i></b></html>",
				"<html><b><i>" + b_less2 + "</i>&lt;&gt;<i>" + b_more2
						+ "</i></b></html>" };

		String[][] data = {
				{ "<html><b>COUNT</b></html>", "", "", "", "", "" },
				{ "<html><b>AVG</b></html>", "", "", "", "", "" },
				{ "<html><b>MAX</b></html>", "", "", "", "", "" },
				{ "<html><b>MIN</b></html>", "", "", "", "", "" },
				{ "<html><b>SUM</b></html>", "", "", "", "", "" },
				{ "<html><b>DUPLICATE</b></html>", "", "", "", "", "" }, };

		addColumns(columnNames);
		addRows(data);

		createTable(false);
	};

	private void createTable(final boolean isEditable) {
		_isEditable = isEditable;
		tabModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				String colN = this.getColumnName(col);
				int[] colIndex = new int[1];
				colIndex[0] = col;
				
				if (isEditable == true) {
					return true;
				}
				else { // isEditable False
					if (colN.endsWith("Editable") == true ) {
						return true;
					} else  { // colN.endsWith("Editable") not true
						return false;
					}
				}
			} // end of isCellEditable

			public Class<?> getColumnClass(int col) {
				if (showClass == true)
					if (classType != null) {
						return SqlType.getClass(classType[col]);
					} else { // class type is null
						for (int i = 0; i < this.getRowCount(); i++)
							if (getValueAt(i, col) != null)
								return getValueAt(i, col).getClass();
						return (new Object()).getClass();
					}
				return (new Object()).getClass();
			}

		};
		tabModel.setDataVector(row_v, column_v);
	}

	public void setValueAt(String s, int row, int col) {
		if (row < 0 || col < 0)
			return;
		tabModel.setValueAt(s, row, col);
	}

	public void setValueAt(Object s, int row, int col) {
		if (row < 0 || col < 0)
			return;
		tabModel.setValueAt(s, row, col);
	}

	private void addColumns(String[] colName) {
		int i;
		for (i = 0; i < colName.length; i++)
			column_v.addElement((String) colName[i]);
		col_size = i;
	}

	private void addColumns(Object[] colName) {
		int i;
		for (i = 0; i < colName.length; i++)
			column_v.addElement((String) colName[i].toString());
		col_size = i;
	}

	private void addRows(String[][] rowData) {
		for (int i = 0; i < rowData.length; i++) {
			Vector<String> newRow = new Vector<String>();
			for (int j = 0; j < rowData[i].length; j++)
				newRow.addElement((String) rowData[i][j]);
			row_v.addElement(newRow);
		}
	}

	public void addFillRow(String[] rowset) {
		Vector<String> newRow = new Vector<String>();
		for (int j = 0; j < rowset.length; j++)
			newRow.addElement((String) rowset[j]);

		row_v.addElement(newRow);
		tabModel.fireTableRowsInserted(tabModel.getRowCount(),1);

	}

	/* Add row from one object sequence*/
	public void addFillRow(Object[] rowset) {
		Vector<Object> newRow = new Vector<Object>();
		for (int j = 0; j < rowset.length; j++)
			newRow.addElement(rowset[j]);

		row_v.addElement(newRow);
		tabModel.fireTableRowsInserted(tabModel.getRowCount(),1);

	}
	public void addFillRow(Object[] rowset, Object[] rowset1) {
		Vector<Object> newRow = new Vector<Object>();
		for (int j = 0; j < rowset.length; j++)
			newRow.addElement(rowset[j]);
		for (int j = 0; j < rowset1.length; j++) // append in the last
			newRow.addElement(rowset1[j]);

		row_v.addElement(newRow);
		tabModel.fireTableRowsInserted(tabModel.getRowCount(),1);

	}

	public void addFillRow(Vector<?> rowset) {
		row_v.addElement(rowset);
		
		tabModel.fireTableRowsInserted(tabModel.getRowCount(),1);

	}

	public void addRow() {
		Vector<String> newRow = new Vector<String>();
		for (int j = 0; j < col_size; j++)
			newRow.addElement((String) "");

		row_v.addElement(newRow);
		tabModel.fireTableRowsInserted(tabModel.getRowCount(),1);

	}

	public void addNullRow() {
		Vector<String> newRow = new Vector<String>();
		for (int j = 0; j < col_size; j++)
			newRow.addElement(null);
		row_v.addElement(newRow);
		tabModel.fireTableRowsInserted(tabModel.getRowCount(),1);

	}

	// Add Column and Remove Column

	public void addColumn(final String name) {
		tabModel.addColumn(name);
		tabModel.fireTableStructureChanged();
		col_size = tabModel.getColumnCount();// Increase the col size
	}

	public void addRows(int startRow, int noOfRows) {
		int col_c = tabModel.getColumnCount();
		Object[] row = new Object[col_c];
		for (int i = 0; i < noOfRows; i++)
			tabModel.insertRow(startRow, row);
		tabModel.fireTableRowsInserted(startRow, noOfRows);
	}

	public void removeRows(int startRow, int noOfRows) {
		for (int i = 0; i < noOfRows; i++)
			tabModel.removeRow(startRow);
		tabModel.fireTableRowsDeleted(startRow, noOfRows);
	}
	
	public void removeMarkedRows(Vector<Integer> marked) {
		Integer[] a = new Integer[marked.size()];
		a = marked.toArray(a);
		Arrays.sort(a);
		int len = a.length;
		for (int i = 0; i < len; i++) {
			tabModel.removeRow(a[len - 1 - i]);
			tabModel.fireTableRowsDeleted(a[len - 1 - i], 1);
		}
	}

	public Object[] copyRow(int startRow) {
		int col_c = tabModel.getColumnCount();
		Object[] row = new Object[col_c];
		for (int i = 0; i < col_c; i++)
			row[i] = tabModel.getValueAt(startRow, i);
		return row;
	}

	public void pasteRow(int startRow, Vector<Object[]> row) {
		int row_c = tabModel.getRowCount();
		int col_c = tabModel.getColumnCount();
		int vci = 0;
		int saveR = row_c - (startRow + row.size());
		if (saveR < 0) {
			System.out.println("Not Enough Rows left to paste " + row.size()
					+ " Rows \n Use \'Insert Clip\' instead");
			return;
		}

		for (int i = row.size() - 1; i >= 0; i--) {
			Object[] a = row.elementAt(vci++);
			col_c = (col_c > a.length) ? a.length : col_c;
			for (int j = 0; j < col_c; j++)
				tabModel.setValueAt(a[j], startRow + i, j);
		}
	}

	public void pasteRow(int startRow, Object[] row) {
		int row_c = tabModel.getRowCount();
		int col_c = tabModel.getColumnCount();

		int saveR = row_c - (startRow + 1);
		if (saveR < 0) {
			System.out.println("Not Enough Rows left to paste " + 1
					+ " Row \n Use \'Insert Clip\' instead");
			return;
		}

		Object[] a = row;
		col_c = (col_c > a.length) ? a.length : col_c;
		for (int j = 0; j < col_c; j++)
			tabModel.setValueAt(a[j], startRow, j);
	}

	public DefaultTableModel getModel() {
		return tabModel;
	}

	public boolean isRTEditable() {
		return _isEditable;
	}
	public boolean isRTShowClass() {
		return showClass;
	}

	public static ReportTableModel copyTable(ReportTableModel rpt,
			boolean editable, boolean showClass) {
		if (rpt == null)
			return null;
		int colC = rpt.tabModel.getColumnCount();
		int rowC = rpt.tabModel.getRowCount();
		String[] colName = new String[colC];

		for (int i = 0; i < colC; i++)
			colName[i] = rpt.tabModel.getColumnName(i);

		ReportTableModel newRT = new ReportTableModel(colName, editable,
				showClass);
		for (int i = 0; i < rowC; i++) {
			newRT.addRow();
			for (int j = 0; j < colC; j++)
				newRT.tabModel.setValueAt(rpt.tabModel.getValueAt(i, j), i, j);
		}
		return newRT;

	}

	public Object[] getRow(int rowIndex) {
		int colC = tabModel.getColumnCount();
		Object[] obj = new Object[colC];
		if (rowIndex < 0 || rowIndex >= tabModel.getRowCount())
			return obj;
		for (int i = 0; i < colC; i++) {
			obj[i] = tabModel.getValueAt(rowIndex, i);
		}
		return obj;
	}
	
	// To get only indexed or selected cols
	public Object[] getSelectedColRow(int rowIndex,int[] colI) {
		int colC = colI.length;
		Object[] obj = new Object[colC];
		if (rowIndex < 0 || rowIndex >= tabModel.getRowCount())
			return obj;
		for (int i = 0; i < colC; i++) {
			try {
				obj[i] = tabModel.getValueAt(rowIndex, colI[i]);
			} catch (Exception e) { // in case array out of bound
				obj[i] = null;
				continue;
			}
		}
		return obj;
	}

	public void cleanallRow() {
		int i = tabModel.getRowCount();
		removeRows(0, i);
	}
	
	public boolean isEmptyRow (int rowid) {
		Object[] rowObj = this.getRow(rowid);
		for (int i=0 ; i < rowObj.length ; i++) {
			if (!(rowObj[i] == null || "".equals(rowObj[i].toString()))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmptyExceptCols (int rowid, int[] colIndex) {
		boolean isColMatch = false;
		Object[] rowObj = this.getRow(rowid);
		for (int i=0 ; i < rowObj.length ; i++) {
			isColMatch = false;
			for (int j=0; j < colIndex.length; j++){
				if ( i == colIndex[j]) isColMatch = true;
			}
			if (isColMatch == false && (!(rowObj[i] == null || "".equals(rowObj[i].toString())))) {
				return false;
			}
		}
		return true;
	}
	// Static utility method
	public static int getColumnIndex(ReportTableModel rpt, String colName) {
		int row_c = rpt.getModel().getColumnCount();
		for (int i = 0; i < row_c; i++) {
			if (colName.equals(rpt.getModel().getColumnName(i)))
				return i;
		}
		return -1;
	}
	
	// public util methods
	public  int getColumnIndex(String colName) {
		int row_c = this.getModel().getColumnCount();
		for (int i = 0; i < row_c; i++) {
			if (colName.equals(this.getModel().getColumnName(i)))
				return i;
		}
		return -1;
	}
	
	public  Object[] getAllColName() {
		int colC = this.getModel().getColumnCount();
		Object[] colN = new Object[colC];
		for (int i = 0; i < colC; i++)
		 colN[i] = this.getModel().getColumnName(i);	
		return colN;
	}
	
	public  String[] getAllColNameStr() {
		int colC = this.getModel().getColumnCount();
		String[] colN = new String[colC];
		for (int i = 0; i < colC; i++)
		 colN[i] = this.getModel().getColumnName(i);	
		return colN;
	}
	
	public  Object[] getColData(int index) {
		int row_c = this.getModel().getRowCount();
		Object[] colN = new Object[row_c];
		for (int i = 0; i < row_c; i++)
		 colN[i] = this.getModel().getValueAt(i, index);
		return colN;
	}
	
	public  Object[] getColDataRandom(int index,int count) {
		Object[] colN = new Object[count];
		Vector<Object> vc = new Vector<Object>();
		int row_c = this.getModel().getRowCount();
		int rowcount=0;
		while(rowcount < count) {
			int newc = new Random().nextInt(row_c);
		 	Object o = this.getModel().getValueAt(newc, index);
		 	// if (vc.indexOf(o) != -1) continue; // it may create loop
		 	vc.add(o);
		 	rowcount++;
		}
		return vc.toArray(colN);
	}
	
	public  Object[] getColDataRandom(String colName,int count) {
		int index = getColumnIndex( colName);
		if ( index  < 0 ) return null;
		return getColDataRandom( index,count);

	}
	
	
	public  Vector<Object> getColDataV(int index) {
		int row_c = this.getModel().getRowCount();
		Vector<Object> vc = new Vector<Object>();
		for (int i = 0; i < row_c; i++)
			vc.add(this.getModel().getValueAt(i, index));
		return vc;
	}
	
	public  Vector<Double> getColDataVD(int index) {
		int row_c = this.getModel().getRowCount();
		Vector<Double> vc = new Vector<Double>();
		for (int i = 0; i < row_c; i++) {
			Object colv = this.getModel().getValueAt(i, index);
			if (colv == null || "".equals(colv.toString())) continue; // Null, empty skipped
			if (colv instanceof Number)
				vc.add(((Double) colv).doubleValue());
			else if (colv instanceof String) {
				try {
					double newv = Double.parseDouble(colv.toString());
					vc.add(newv);
				} catch (Exception e) {
					// Do nothing
				}
			}
		}
		return vc;
	}
	
	public  Object[] getColData(String colName) {
		int colI = getColumnIndex( colName);
		if ( colI  < 0 ) return null;
		return getColData(colI);
	}
	
	public int[] getClassType() {
		return classType;
	}
	
} // End of ReportTableModel class