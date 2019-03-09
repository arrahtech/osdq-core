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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;




import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.arrah.framework.ndtable.ReportTableModel;

public class XlsxReader {
	private ReportTableModel _rt = null;
	private ArrayList<String> columnName = new ArrayList<String> ();
	private boolean allSheet = true;
	private Workbook workbook = null;

	public XlsxReader() {
	};
	
	public void readAllSheets(boolean readAll) {
		allSheet = readAll;
	}

	public ReportTableModel read(List<String> sheetName) {
		if (loadXlsxFile(sheetName) == true)
			return _rt;
		else {
			System.out.println("XLSX File can not be loaded");
			return null;
		}
	}

	public List<String> showSheets(File file) {
		List<String> sheetName = new ArrayList<String>();
		try {
			workbook = WorkbookFactory.create(file);
			int noOfSheet = workbook.getNumberOfSheets();
			// System.out.println("No of Sheets in Xlsx:"+noOfSheet);
			for (int i=0; i < noOfSheet ; i++) {
				sheetName.add(workbook.getSheetName(i));
				//System.out.println("Name:"+workbook.getSheetName(i));
			}
			
		} catch (EncryptedDocumentException e) {
			System.out.println("EncryptedDocumentException:"+e.getLocalizedMessage());
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			System.out.println("InvalidFormatException:"+e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException:"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return sheetName;
		
	}
	
	// Send Double, Boolean LocalDate not Date
		private  boolean loadXlsxFile(List<String> sheetName) {
		int colI=0; // column Index
		boolean headerSet= false; // for multiple sheets
		
		try {

			// It takes all the sheets
			java.util.Iterator<Sheet> shiter = workbook.sheetIterator();
			
			while (shiter.hasNext()) {
				Sheet sheet = shiter.next();
				//System.out.println(sheetName + ":" + sheet.getSheetName());
				
				if (sheetName.indexOf(sheet.getSheetName()) == -1 ) // skip this
					continue;
			
			// Fill the mergered Cell value
			Hashtable<String,String> mergeV = fillMergedVal( sheet);
			
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				if (row.getRowNum() == 0) { // this is header - default behaviour
					if (headerSet == true)
						continue;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						columnName.add(cell.getStringCellValue());
					}
					_rt = new ReportTableModel(columnName.toArray(), true, true);
					headerSet = true;
					continue;
				} else {
					colI =0; // Start of new Row
					ArrayList<Object> rowmap = new ArrayList<Object>(); // to hold Row
					while (colI < columnName.size()) {
						Cell cell = row.getCell(colI);
						if (cell == null) {
							String checkM = mergeV.get(row.getRowNum()+":"+colI);
							if (checkM == null)
								rowmap.add("");
							else
								rowmap.add(checkM);
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
							String checkM = mergeV.get(row.getRowNum()+":"+colI);
							if (checkM == null)
								rowmap.add("");
							else
								rowmap.add(checkM);
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
			if (allSheet == false)
				break; // no need for next iteration
		} // end of iterator
		}  catch (Exception e) {
			System.out.println("Exception:"+e.getClass().getSimpleName()+ " Message: "+e.getLocalizedMessage());
			return false;
		}
		return true;
	}
		
	private Hashtable<String,String> fillMergedVal(Sheet sheet) {
		Hashtable<String,String> mergerVal = new Hashtable<String,String>();
		//will iterate over the Merged cells
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i); //Region of merged cells

            int colIndex = region.getFirstColumn(); //number of columns merged
            int rowNum = region.getFirstRow();      //number of rows merged
            Cell cell = sheet.getRow(rowNum).getCell(colIndex);
            String cellV= "";
            if (cell == null)
            	mergerVal.put(rowNum+":"+colIndex, cellV);
            else
            	mergerVal.put(rowNum+":"+colIndex, cellV = cell.getStringCellValue());
            
            //fill merged value for region
            for (rowNum = region.getFirstRow(); rowNum <= region.getLastRow(); rowNum++)
            	for (colIndex = region.getFirstColumn(); colIndex <= region.getLastColumn(); colIndex++)
            		mergerVal.put(rowNum+":"+colIndex, cellV);
            
        }
		return mergerVal;
		
	}
	
	public boolean saveXlsxFile(ReportTableModel rt, File file) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Arrah Sheet");
			int row = rt.getModel().getRowCount();
			int col = rt.getModel().getColumnCount();
			
			POIXMLProperties.ExtendedProperties ext =  workbook.getProperties().getExtendedProperties();
	        ext.getUnderlyingProperties().setAppVersion("Microsoft Excel");
			// Get Column header
			// Create a Row
			XSSFRow headerRow = sheet.createRow(0);
			for (int j = 0; j < col; j++) {
				XSSFCell cell = headerRow.createCell(j);
	            cell.setCellValue((String)rt.getModel().getColumnName(j));
			}
			
			System.out.println(headerRow);
			
			// Put the cell & values
			for (int i = 0; i < row; i++) {
				XSSFRow newRow = sheet.createRow(i+1);
				for (int j = 0; j < col; j++) {
					XSSFCell cell = newRow.createCell(j);
					Object obj = rt.getModel().getValueAt(i, j);
//					if (obj == null)
//						continue;
					//cell.setCellValue((String)obj.toString());
					cell.setCellValue("test" + i);
					System.out.println(cell);
				}
			}
			
			// All sheets and cells added. Now write out the workbook
	        FileOutputStream fileOut = new FileOutputStream(file);
	        workbook.write(fileOut);
	        fileOut.flush();
	        fileOut.close();
			workbook.close();
			System.out.println("File:" + file);
		} catch (Exception e) {
			System.out.println("\n XLSX Save Exception:" + e.getMessage());
			return false;
		}
		return true;
	}
}
