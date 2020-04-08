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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.table.DefaultTableModel;

import org.arrah.framework.rdbms.SqlType;

import com.opencsv.CSVWriter;

public class ReportTableModel implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Vector<Vector<?>> rowVector = new Vector<>();
	private Vector<Object> columnVector = new Vector<>();
	private int columnSize = 0;
	private DefaultTableModel defaultTableModel;
	private boolean isEditable = false;
	private boolean showClass = false;
	private int[] classType = null;
	private Object item;

	public ReportTableModel(String[] column) {
		addColumns(column);

		createTable(false);
	}

	public ReportTableModel(Object[] column) {
		addColumns(column);

		createTable(false);
	}

	public ReportTableModel(Object[] column, boolean isEditable) {
		addColumns(column);

		createTable(isEditable);
	}

	public ReportTableModel(String[] column, boolean isEditable) {
		addColumns(column);

		createTable(isEditable);
	}

	public ReportTableModel(String[] column, boolean isEditable, boolean columnClass) {
		addColumns(column);

		createTable(isEditable);

		showClass = columnClass;
	}

	public ReportTableModel(String[] column, int[] sqlType, boolean isEditable,
			boolean colClass) {

		addColumns(column);

		createTable(isEditable);

		classType = sqlType;
		showClass = colClass;
	}

	public ReportTableModel(Object[] column, boolean isEditable, boolean columnClass) {
		addColumns(column);

		createTable(isEditable);

		showClass = columnClass;
	}

	public ReportTableModel(
			String less, String more, String bLess1,
			String bMore1, String bLess2, String bMore2) {

		String[] columnNames = {
				"<html><b><i>Values</i></b></html>",
				"<html><b>Aggregate</i></b></html>",
				"<html><b> &lt;  <i>" + less + "</i></b></html>",
				"<html><b> &gt;  <i>" + more + "</i></b></html>",
				"<html><b><i>" + bLess1 + "</i>&lt;&gt;<i>" + bMore1
						+ "</i></b></html>",
				"<html><b><i>" + bLess2 + "</i>&lt;&gt;<i>" + bMore2
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

	public static ReportTableModel copyTable(
			ReportTableModel reportTableModel,
			boolean editable, boolean showClass) {

		if (reportTableModel == null) {
			return null;
		}

		int columnCount = reportTableModel.defaultTableModel.getColumnCount();
		int rowCount = reportTableModel.defaultTableModel.getRowCount();

		String[] columnName = new String[columnCount];

		for (int i = 0; i < columnCount; i++) {
			columnName[i] = reportTableModel.defaultTableModel.getColumnName(i);
		}

		ReportTableModel newReportTableModel = new ReportTableModel(columnName, editable, showClass);

		for (int i = 0; i < rowCount; i++) {
			newReportTableModel.addRow();

			for (int j = 0; j < columnCount; j++) {
				newReportTableModel.defaultTableModel.setValueAt(
						reportTableModel.defaultTableModel.getValueAt(i, j), i, j);
			}
		}

		return newReportTableModel;
	}

	// Static utility method
	public static int getColumnIndex(ReportTableModel reportTableModel, String columnName) {
		int columnCount = reportTableModel.getModel().getColumnCount();

		for (int i = 0; i < columnCount; i++) {
			if (columnName.equals(reportTableModel.getModel().getColumnName(i))) {
				return i;
			}
		}

		return -1;
	}

	private void createTable(final boolean isEditable) {
		this.isEditable = isEditable;

		defaultTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				String columnName = this.getColumnName(column);

				int[] columnIndex = new int[1];

				columnIndex[0] = column;

				if (isEditable == true) {
					return true;
				}

				else { // isEditable False
					if (columnName.endsWith("Editable") == true ) {
						return true;
					} else  { // colN.endsWith("Editable") not true
						return false;
					}
				}
			} // end of isCellEditable

			public Class<?> getColumnClass(int columnIndex) {
				if (showClass == true)
					if (classType != null) {
						return SqlType.getClass(classType[columnIndex]);
					} else { // class type is null
						for (int i = 0; i < this.getRowCount(); i++)
							try{
								if (getValueAt(i, columnIndex) != null)
									return getValueAt(i, columnIndex).getClass();
							} catch(Exception e) {
								return (new Object()).getClass();
							}
						return (new Object()).getClass();
					}
				return (new Object()).getClass();
			}
		};

		defaultTableModel.setDataVector(rowVector, columnVector);
	}

	public void setValueAt(String value, int row, int column) {
		if (row < 0 || column < 0) {
			return;
		}

		defaultTableModel.setValueAt(value, row, column);
	}

	public void setValueAt(Object value, int row, int column) {
		item = value;

		if (row < 0 || column < 0) {
			return;
		}

		defaultTableModel.setValueAt(value, row, column);
	}

	private void addColumns(String[] columns) {
		int i;

		for (i = 0; i < columns.length; i++) {
			columnVector.addElement((String) columns[i]);
		}

		columnSize = i;
	}

	private void addColumns(Object[] columns) {
		int i;

		for (i = 0; i < columns.length; i++){
			columnVector.addElement((String) columns[i].toString());
		}

		columnSize = i;
	}

	private void addRows(String[][] table) {
		for (int i = 0; i < table.length; i++) {
			Vector<String> newRow = new Vector<>();

			for (int j = 0; j < table[i].length; j++) {
				newRow.addElement((String) table[i][j]);
			}

			rowVector.addElement(newRow);
		}
	}

	public void addFillRow(String[] stringRow) {
		Vector<String> newRow = new Vector<>();

		for (int j = 0; j < stringRow.length; j++) {
			newRow.addElement((String) stringRow[j]);
		}

		rowVector.addElement(newRow);

		defaultTableModel.fireTableRowsInserted(defaultTableModel.getRowCount(),1);
	}

	/* Add row from one object sequence*/
	public void addFillRow(Object[] objectRow) {
		Vector<Object> newRow = new Vector<Object>();

		for (int j = 0; j < objectRow.length; j++) {
			newRow.addElement(objectRow[j]);
		}

		rowVector.addElement(newRow);

		defaultTableModel.fireTableRowsInserted(defaultTableModel.getRowCount(),1);
	}

	public void addFillRow(Object[] objectRow1, Object[] objectRow2) {
		Vector<Object> newRow = new Vector<>();

		for (int j = 0; j < objectRow1.length; j++) {
			newRow.addElement(objectRow1[j]);
		}

		for (int j = 0; j < objectRow2.length; j++) { // append in the last
			newRow.addElement(objectRow2[j]);
		}

		rowVector.addElement(newRow);

		defaultTableModel.fireTableRowsInserted(defaultTableModel.getRowCount(),1);
	}

	public void addFillRow(Vector<Object> objectVectorRow) {
		rowVector.addElement(objectVectorRow);

		defaultTableModel.fireTableRowsInserted(defaultTableModel.getRowCount(),1);
	}

	// Add Column and Remove Column

	public void addRow() {
		Vector<String> newRow = new Vector<>();

		for (int j = 0; j < columnSize; j++) {
			newRow.addElement((String) "");
		}

		rowVector.addElement(newRow);

		defaultTableModel.fireTableRowsInserted(defaultTableModel.getRowCount(),1);
	}

	public void addNullRow() {
		Vector<String> newRow = new Vector<String>();

		for (int j = 0; j < columnSize; j++)
			newRow.addElement(null);

		rowVector.addElement(newRow);

		defaultTableModel.fireTableRowsInserted(defaultTableModel.getRowCount(),1);
	}

	public void addColumn(final String name) {
		defaultTableModel.addColumn(name);

		defaultTableModel.fireTableStructureChanged();

		columnSize = defaultTableModel.getColumnCount();// Increase the col size
	}
	
	public void addRows(int startRow, int noOfRows) {
		int columnCount = defaultTableModel.getColumnCount();

		Object[] row = new Object[columnCount];

		for (int i = 0; i < noOfRows; i++) {
			defaultTableModel.insertRow(startRow, row);
		}

		defaultTableModel.fireTableRowsInserted(startRow, noOfRows);
	}

	public void removeRows(int startRow, int noOfRows) {
		for (int i = 0; i < noOfRows; i++) {
			defaultTableModel.removeRow(startRow);
		}

		defaultTableModel.fireTableRowsDeleted(startRow, noOfRows);
	}

	public void removeMarkedRows(Vector<Integer> marked) {
		Integer[] a = new Integer[marked.size()];

		a = marked.toArray(a);

		Arrays.sort(a);

		int length = a.length;

		for (int i = 0; i < length; i++) {
			defaultTableModel.removeRow(a[length - 1 - i]);

			defaultTableModel.fireTableRowsDeleted(a[length - 1 - i], 1);
		}
	}

	public Object[] copyRow(int startRow) {
		int columnCount = defaultTableModel.getColumnCount();

		Object[] row = new Object[columnCount];

		for (int i = 0; i < columnCount; i++) {
			row[i] = defaultTableModel.getValueAt(startRow, i);
		}

		return row;
	}

	public void pasteRow(int startRow, Vector<Object[]> row) {
		int rowCount = defaultTableModel.getRowCount();

		int columnCount = defaultTableModel.getColumnCount();

		int vci = 0;

		int saveRow = rowCount - (startRow + row.size());

		if (saveRow < 0) {
			System.out.println("Not Enough Rows left to paste " + row.size()
					+ " Rows \n Use \'Insert Clip\' instead");

			return;
		}

		for (int i = row.size() - 1; i >= 0; i--) {
			Object[] a = row.elementAt(vci++);

			columnCount = (columnCount > a.length) ? a.length : columnCount;

			for (int j = 0; j < columnCount; j++)
				defaultTableModel.setValueAt(a[j], startRow + i, j);
		}
	}

	public void pasteRow(int startRow, Object[] row) {
		int rowCount = defaultTableModel.getRowCount();
		int columnCount = defaultTableModel.getColumnCount();

		int saveRow = rowCount - (startRow + 1);

		if (saveRow < 0) {
			System.out.println("Not Enough Rows left to paste " + 1
					+ " Row \n Use \'Insert Clip\' instead");
			return;
		}

		Object[] rowReference = row;

		columnCount = (columnCount > rowReference.length) ? rowReference.length : columnCount;

		for (int j = 0; j < columnCount; j++) {
			defaultTableModel.setValueAt(rowReference[j], startRow, j);
		}
	}

	public DefaultTableModel getModel() {
		return defaultTableModel;
	}

	public boolean isRTMEditable() {
		return isEditable;
	}

	public boolean isRTMShowClass() {
		return showClass;
	}
	
	public Object[] getRow(int rowIndex) {
		int columnCount = defaultTableModel.getColumnCount();

		Object[] objectArray = new Object[columnCount];

		if (rowIndex < 0 || rowIndex >= defaultTableModel.getRowCount()){
			return objectArray;
		}

		for (int i = 0; i < columnCount; i++) {
			objectArray[i] = defaultTableModel.getValueAt(rowIndex, i);
		}

		return objectArray;
	}

	// To get only indexed or selected cols
	public Object[] getSelectedColRow(int rowIndex, int[] columnIndex) {
		int columnIndexLength = columnIndex.length;

		Object[] objectArray = new Object[columnIndexLength];

		if (rowIndex < 0 || rowIndex >= defaultTableModel.getRowCount()){
			return objectArray;
		}

		for (int i = 0; i < columnIndexLength; i++) {
			try {
				objectArray[i] = defaultTableModel.getValueAt(rowIndex, columnIndex[i]);
			} catch (Exception e) { // in case array out-of-bound
				objectArray[i] = null;

				continue;
			}
		}
		return objectArray;
	}

	// To get only indexed or selected cols
	public Object[] getSelectedColRow(int rowIndex, Integer[] columnIndex) {
		int[] leftIndex = Arrays.stream(columnIndex).mapToInt(Integer::intValue).toArray();

		return getSelectedColRow( rowIndex, leftIndex);
	}
	
	public void cleanallRow() {
		int rowCount = defaultTableModel.getRowCount();

		removeRows(0, rowCount);
	}
	
	public boolean isEmptyRow (int rowIndex) {
		Object[] rowObjectArray = this.getRow(rowIndex);

		for (int i=0 ; i < rowObjectArray.length ; i++) {
			if (!(rowObjectArray[i] == null || "".equals(rowObjectArray[i].toString()))) {
				return false;
			}
		}

		return true;
	}

	public boolean isEmptyExceptCols (int rowIndex, int[] columnIndex) {
		boolean isColumnMatched = false;

		Object[] rowObjectArray = this.getRow(rowIndex);

		for (int i=0 ; i < rowObjectArray.length ; i++) {
			isColumnMatched = false;

			for (int j=0; j < columnIndex.length; j++){
				if ( i == columnIndex[j]) isColumnMatched = true;
			}

			if (isColumnMatched == false && (!(rowObjectArray[i] == null || "".equals(rowObjectArray[i].toString())))) {
				return false;
			}
		}

		return true;
	}
	
	// public util methods
	public  int getColumnIndex(String columnName) {
		int columnCount = this.getModel().getColumnCount();

		for (int i = 0; i < columnCount; i++) {
			if (columnName.equals(this.getModel().getColumnName(i))) {
				return i;
			}
		}

		return -1;
	}
	
	public  Object[] getAllColName() {
		int columnCount = this.getModel().getColumnCount();

		Object[] columnNamesObjectArray = new Object[columnCount];

		for (int i = 0; i < columnCount; i++) {
			columnNamesObjectArray[i] = this.getModel().getColumnName(i);
		}

		return columnNamesObjectArray;
	}
	
	public  String[] getAllColNameStr() {
		int columnCount = this.getModel().getColumnCount();

		String[] columns = new String[columnCount];

		for (int i = 0; i < columnCount; i++) {
			columns[i] = this.getModel().getColumnName(i);
		}

		return columns;
	}
	
	public  Object[] getColData(int index) {
		int rowCount = this.getModel().getRowCount();

		Object[] columnNameObjectArray = new Object[rowCount];

		for (int i = 0; i < rowCount; i++) {
			columnNameObjectArray[i] = this.getModel().getValueAt(i, index);
		}

		return columnNameObjectArray;
	}
	
	public  Object[] getColDataRandom(int index,int count) {
		Object[] columnNameObjectArray = new Object[count];

		Vector<Object> objectVector = new Vector<>();

		int rowCount = this.getModel().getRowCount();

		int rowcount=0;

		while(rowcount < count) {
			int newIndex = new Random().nextInt(rowCount);

		 	Object value = this.getModel().getValueAt(newIndex, index);
		 	// if (objectVector.indexOf(value) != -1) continue; // it may create loop

		 	objectVector.add(value);

		 	rowcount++;
		}

		return objectVector.toArray(columnNameObjectArray);
	}
	
	public  Object[] getColDataRandom(String columnName,int count) {
		int index = getColumnIndex( columnName);

		if ( index  < 0 ) {
			return null;
		}

		return getColDataRandom( index,count);
	}
	
	
	public  Vector<Object> getColDataV(int index) {
		int rowCount = this.getModel().getRowCount();

		Vector<Object> objectVector = new Vector<>();

		for (int i = 0; i < rowCount; i++) {
			objectVector.add(this.getModel().getValueAt(i, index));
		}

		return objectVector;
	}
	
	public  Vector<Double> getColDataVD(int index) {
		int rowCount = this.getModel().getRowCount();

		Vector<Double> doubleVector = new Vector<>();

		for (int i = 0; i < rowCount; i++) {
			Object columnValue = this.getModel().getValueAt(i, index);

			if (columnValue == null || "".equals(columnValue.toString())) {
				continue; // Null, empty skipped
			}

			if (columnValue instanceof Number){
				//doubleVector.add(((Double) columnValue).doubleValue());
				doubleVector.add(((Number)columnValue).doubleValue());
			}
			else if (columnValue instanceof String) {
				try {
					double newColumnValue = Double.parseDouble(columnValue.toString());

					doubleVector.add(newColumnValue);
				} catch (Exception e) {
					// Do nothing TODO log this..
				}
			}
		}

		return doubleVector;
	}
	
	public  Object[] getColData(String columnName) {
		int columnIndex = getColumnIndex( columnName);

		if ( columnIndex  < 0 ) {
			return null;
		}

		return getColData(columnIndex);
	}
	
	public int[] getClassType() {
		return classType;
	}
	
	public Object clone() throws CloneNotSupportedException {
        return super.clone();  
    }

    public List<List<Object>> toNativeObjectListList() {
		return rowVector
			.stream()
			.map(vector -> {

				List<Object> list = new ArrayList<>(vector);

				return list;
			})
			.collect(Collectors.toList());
	}
	
	public void toPrint() {
		String[] columnNames = this.getAllColNameStr();

		for(String columnName: columnNames)
			if (columnName != null) {
				System.out.print(columnName+" ");
			}
			else {
				System.out.print("EMPTYCOLNAME"+" ");
			}

			System.out.println();

		for (int i=0 ; i <this.getModel().getRowCount(); i++) {
			for (int j=0 ; j <this.getModel().getColumnCount(); j++) {
				Object value = this.getModel().getValueAt(i, j);

				if (value != null) {
					System.out.print(value.toString()+" ");
				}
				else {
					System.out.print("null"+" ");
				}
			}

			System.out.println();
		}
	}
	
	/**
	 * save the table as comma separated values (OpenCSV format)
	 */
	public void saveAsOpenCSV(String fileLocation) {
		
		File fileName = null;
		if (fileLocation.toLowerCase().endsWith(".csv") == false) {
			fileName = new File(fileLocation + ".csv");
		} else {
			fileName = new File(fileLocation);
		}
		
		// Get Row and Column count
		int rowCount = this.getModel().getRowCount();

		int columnCount = this.getModel().getColumnCount();

		String[] columnData = new String[columnCount];

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(fileName));
			
			// Get Column header
			for (int j = 0; j < columnCount; j++) {
				columnData[j] = this.getModel().getColumnName(j);
			}

			writer.writeNext(columnData,true);
			
			// Get Column data
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < columnCount; j++) {
					Object object = this.getModel().getValueAt(i, j);

					if (object == null) {
						columnData[j] = "null";
					}
					else {
						columnData[j] = object.toString();
					}
				}

				writer.writeNext(columnData,true);
			}

			writer.close();
		} catch (IOException exp) {
			System.out.println( exp.getMessage());

		}
	}
	
} // End of ReportTableModel class