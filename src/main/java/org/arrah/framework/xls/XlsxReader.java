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

/* This files is used for reading,writing xlsx files
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


import org.arrah.framework.ndtable.ReportTableModel;

public class XlsxReader {
	private ReportTableModel _rt;
	private ArrayList<String> columnName = new ArrayList<String> ();

	public XlsxReader() {
	};

	public ReportTableModel read(File file) {
		if (loadXlsxFile(file) == true)
			return _rt;
		else {
			System.out.println("XLSX File can not be loaded");
			return null;
		}
	}

	// Send Double, Boolean LocalDate not Date
		private  boolean loadXlsxFile(File fileName) {
		int colI=0; // column Index
		
		try {

			Workbook workbook = WorkbookFactory.create(fileName);
			
			// It takes only first sheet
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				if (row.getRowNum() == 0) { // this is header - default behaviour
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnName.add(cell.getStringCellValue());
					}
					_rt = new ReportTableModel(columnName.toArray(), true, true);
					continue;
				} else {
					colI =0; // Start of new Row
					ArrayList<Object> rowmap = new ArrayList<Object>(); // to hold Row
					while (colI < columnName.size()) {
						Cell cell = row.getCell(colI);
						if (cell == null) {
							rowmap.add("");
							colI++;
							continue;
						}
						CellType ct = cell.getCellTypeEnum();
						
						if (ct == CellType.STRING) {
							rowmap.add(cell.getStringCellValue());
						} else if (ct == CellType.NUMERIC) {
							if (DateUtil.isCellDateFormatted(cell)) {
								long epochtime = cell.getDateCellValue().getTime(); // in milli second
								rowmap.add(new Date(epochtime));
							} else {
								rowmap.add(cell.getNumericCellValue());
							}
						} else if (ct == CellType.BLANK || ct == CellType.ERROR) {
							rowmap.add("");
						} else if (ct == CellType.BOOLEAN) {
							rowmap.add(cell.getBooleanCellValue());
						} else { // default Behavior
							rowmap.add(cell.toString());
						}
						colI++; // Move to next column
					}
					_rt.addFillRow(rowmap.toArray());
				}
			} // End of while loop
		} catch ( FileNotFoundException e) {
			System.out.println("File Not found");
			return false;
		} catch (Exception e) {
			System.out.println("Exception:"+e.getClass().getSimpleName()+ " Message: "+e.getLocalizedMessage());
			return false;
		}
		return true;
	}
}
