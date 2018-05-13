package org.arrah.framework.xls;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @author vivek singh
 *
 */

public class ReadXLSXFile {
	
	private ArrayList<String> columnName = new ArrayList<String> ();
	
	// Send Double, Boolean LocalDate not Date
	public  ArrayList<ArrayList<Object> > readXLSX(String fileName) {
		ArrayList<ArrayList<Object> > grid = new ArrayList<ArrayList<Object> >();
		int colI=0; // column Index
		int rowI=0; // row Index
		
		try {

			Workbook workbook = WorkbookFactory.create(new BufferedInputStream(new FileInputStream(new File(fileName))));
			
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
						ArrayList<Object> colData = new ArrayList<Object>();
						grid.add(colI++,colData);
					}
					continue;
				} else {
					colI =0; // Start of new Row
					while (cellIterator.hasNext() && colI < columnName.size()) { // will not take extra column if more than header
						Cell cell = cellIterator.next();
						CellType ct = cell.getCellTypeEnum();
						if (ct == CellType.STRING) {
							grid.get(colI).add(rowI,cell.getStringCellValue());
						} else if (ct == CellType.NUMERIC) {
							if (DateUtil.isCellDateFormatted(cell)) {
								grid.get(colI).add(rowI,cell.getDateCellValue());
							} else {
								grid.get(colI).add(rowI,cell.getNumericCellValue());
							}
						} else if (ct == CellType.BLANK) {
							grid.get(colI).add(rowI,"");
						} else if (ct == CellType.BOOLEAN) {
							grid.get(colI).add(rowI,cell.getBooleanCellValue());
						} else { // default Behavior
							grid.get(colI).add(rowI,cell.toString());
						}
						colI++; // Move to next column
					}
					while ( colI < columnName.size()) { // fill null values if iterator has less cells
						grid.get(colI++).add(rowI,null);
					}
					
				}
				rowI++; // Move to next Row
			} // End of while loop
		} catch ( FileNotFoundException e) {
			System.out.println("File Not found");
		} catch (Exception e) {
			System.out.println("Exception:"+e.getClass().getSimpleName()+ " Message: "+e.getLocalizedMessage());
		}
		return grid;
	}
	
	// Send Double, Boolean LocalDate not Date
	public  ArrayList<Map<String,Object> > readXLSXintoRow(String fileName) {
		ArrayList<Map<String,Object> > grid = new ArrayList<Map<String,Object> >();
		int colI=0; // column Index
		int rowI=0; // row Index
		
		try {

			Workbook workbook = WorkbookFactory.create(new BufferedInputStream(new FileInputStream(new File(fileName))));
			
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
					continue;
				} else {
					colI =0; // Start of new Row
					Map<String,Object> rowmap = new HashMap<String,Object>(); // to hold Row
					while (colI < columnName.size()) {
						Cell cell = row.getCell(colI);
						if (cell == null) {
							rowmap.put(columnName.get(colI),"");
							colI++;
							continue;
						}
						CellType ct = cell.getCellTypeEnum();
						
						if (ct == CellType.STRING) {
							rowmap.put(columnName.get(colI), cell.getStringCellValue());
						} else if (ct == CellType.NUMERIC) {
							if (DateUtil.isCellDateFormatted(cell)) {
								long epochtime = cell.getDateCellValue().getTime(); // in milli second
								long epochday = new Double(epochtime/(1000*60*60*24)).longValue(); // in days
								rowmap.put(columnName.get(colI),LocalDate.ofEpochDay(epochday));
							} else {
								rowmap.put(columnName.get(colI),cell.getNumericCellValue());
							}
						} else if (ct == CellType.BLANK) {
							rowmap.put(columnName.get(colI),"");
						} else if (ct == CellType.BOOLEAN) {
							rowmap.put(columnName.get(colI),cell.getBooleanCellValue());
						} else { // default Behavior
							rowmap.put(columnName.get(colI),cell.toString());
						}
						colI++; // Move to next column
					}
					
					grid.add(rowI,rowmap);
				}
				rowI++; // Move to next Row
			} // End of while loop
		} catch ( FileNotFoundException e) {
			System.out.println("File Not found");
		} catch (Exception e) {
			System.out.println("Exception:"+e.getClass().getSimpleName()+ " Message: "+e.getLocalizedMessage());
		}
		return grid;
	}
	
	public ArrayList<String> getColumns() {
		return columnName;
	}

	public static void main(String [] args) {
		
		
		ReadXLSXFile xlFile = new ReadXLSXFile();
		/***
		ArrayList<ArrayList<Object> > gridValue = new ArrayList<ArrayList<Object> > ();
		//gridValue = xlFile.readXLSX("/Users/vsingh007c/Downloads/ACTIVE_MARA_COLUMN_REPORT.xls");
		//gridValue = xlFile.readXLSX("/Users/vsingh007c/Downloads/problem - 2017 (1).xls");
		// gridValue = xlFile.readXLSX("/Users/vsingh007c/Downloads/textworkbook.xlsx");
		ArrayList<String> colName = xlFile.getColumns();
		

		int rowC  = gridValue.get(0).size();

		for (String col : colName) 
			System.out.print(col +"\t");
			
		System.out.print("\n");
		for (int i=0; i < rowC; i++) {
			for (int j=0; j < colName.size(); j++) {
					System.out.print(gridValue.get(j).get(i) +"\t");

			}
			System.out.print("\n");
		}

		*****/ // readXLSX end
		
		ArrayList<Map<String,Object> > gridValue = xlFile.readXLSXintoRow("/Users/vsingh007c/Downloads/problem - 2017 (1).xls");
		ArrayList<String> colName = xlFile.getColumns();
		for (String col : colName) 
			System.out.print(col +"\t");
		System.out.print("\n");
		
		for (int i=0; i < gridValue.size(); i++) {
			Map<String,Object> row = gridValue.get(i);
			String[] keyset = new String[row.size()];
			keyset = row.keySet().toArray(keyset);
			for (String key : keyset) {
				System.out.print(key +":" + row.get(key) + "\t");
			}
			System.out.print("\n");
		}
		
	}
}
