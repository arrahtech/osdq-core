package org.arrah.framework.xls;

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

/* This files is used for reading,writing xls files
 *
 */

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.arrah.framework.ndtable.ReportTableModel;

public class XlsReader {
	private ReportTableModel _rt;
	private boolean FIRST_ROW_HEADER = true; // default yes

	public XlsReader() {
	};

	public ReportTableModel read(File file) {
		if (loadXlsFile(file) == true)
			return _rt;
		else {
			System.out.println("XLS File can not be loaded");
			return null;
		}
	}

	public void write(ReportTableModel rt, File file) {
		saveXlsFile(rt, file);
	}

	private boolean loadXlsFile(File file) {
		try {
			int index = 0;

			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);

			int col = sheet.getColumns();
			int row = sheet.getRows();

			String[] colName = new String[col];
			boolean sel = FIRST_ROW_HEADER; // later would be set

			if (sel != FIRST_ROW_HEADER) {
				for (int i = 0; i < colName.length; i++)
					colName[i] = "Column_" + (i + 1);
			}
			if (sel == FIRST_ROW_HEADER) {
				for (int i = 0; i < colName.length; i++) {
					Cell a1 = sheet.getCell(i, 0);
					if (a1 == null || a1.getType() == CellType.EMPTY
							|| a1.getType() == CellType.ERROR)
						colName[i] = "Column_" + (i + 1);
					else
						colName[i] = a1.getContents();
				}
				index = 1;
			}
			_rt = new ReportTableModel(colName, true, true);
			_rt.addRows(0, row - index);

			for (int i = index; i < row; i++)
				for (int j = 0; j < col; j++) {
					Cell a1 = sheet.getCell(j, i);
					if (a1 == null || a1.getType() == CellType.EMPTY
							|| a1.getType() == CellType.ERROR)
						continue;
					_rt.setValueAt(a1.getContents(), i - index, j);
				}
			// Finished - close the workbook and free up memory
			workbook.close();
		} catch (JXLException e) {
			System.out.println("\n Exception:" + e.getMessage());
			return false;
		} catch (IOException e) {
			System.out.println("\n Exception:" + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean saveXlsFile(ReportTableModel rt, File file) {
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("Arrah Sheet", 0);
			int row = rt.getModel().getRowCount();
			int col = rt.getModel().getColumnCount();

			// Get Column header
			for (int j = 0; j < col; j++) {
				Label label = new Label(j, 0, rt.getModel().getColumnName(j));
				sheet.addCell(label);
			}
			// Put the cell
			for (int i = 0; i < row; i++)
				for (int j = 0; j < col; j++) {
					Object obj = rt.getModel().getValueAt(i, j);
					if (obj == null)
						continue;
					Label label = new Label(j, i + 1, obj.toString());
					sheet.addCell(label);
				}
			// All sheets and cells added. Now write out the workbook
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			System.out.println("\n Save Exception:" + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean isFirstRowHeader() {
		return FIRST_ROW_HEADER;
	}

	public void setFirstRowHeader(boolean firstRowheader) {
		FIRST_ROW_HEADER = firstRowheader;
	}
}