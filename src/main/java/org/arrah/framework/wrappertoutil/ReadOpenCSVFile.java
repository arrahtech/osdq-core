package org.arrah.framework.wrappertoutil;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;


/**
 * @author vivek singh
 *
 */

public class ReadOpenCSVFile {
	
	private ArrayList<String> columnName = new ArrayList<String> ();
	
	public  ArrayList<ArrayList<Object> > readOpenCSV(String fileName) {
		ArrayList<ArrayList<Object> > grid = new ArrayList<ArrayList<Object> >();
		int colI=0; // column Index
		int rowI=0; // row Index
		boolean isHeaderSet = false; // header first line
		
		try {

			CSVReader reader = new CSVReader(new BufferedReader(new FileReader(fileName)));
			String [] nextLine = null;
			
			while ((nextLine = reader.readNext())  != null) {
				if (isHeaderSet == false) { // this is header - default behaviour
					for (String s:nextLine) {
						columnName.add(s);
						ArrayList<Object> colData = new ArrayList<Object>();
						grid.add(colI++,colData);
					}
					isHeaderSet = true;
					continue;
				} else {
					colI =0; // Start of new Row
					while (nextLine.length <= columnName.size() && colI < columnName.size()) { // will not take extra column if more than header
						String s = nextLine[colI];
						if (s == null)
							grid.get(colI).add(rowI,"");
						else
							grid.get(colI).add(rowI,s);
						colI++; // Move to next column
					}
					while ( colI < columnName.size()) { // fill null values if iterator has less cells
						grid.get(colI++).add(rowI,"");
					}
				}
				rowI++; // Move to next Row
			} // End of while loop
			reader.close();
		} catch ( FileNotFoundException e) {
			System.out.println("File Not found");
		} catch (Exception e) {
			System.out.println("Exception:"+e.getClass().getSimpleName()+ " Message: "+e.getLocalizedMessage());
		}
		return grid;
	}
	
	public  ArrayList<Map<String,String> > readOpenCSVintoRow(String fileName) {
		ArrayList<Map<String,String> > grid = new ArrayList<Map<String,String> >();
		int colI=0; // column Index
		int rowI=0; // row Index
		boolean isHeaderSet = false; // header first line
		
		try {

			CSVReader reader = new CSVReader(new BufferedReader(new FileReader(fileName)));
			String [] nextLine = null;
			
			while ((nextLine = reader.readNext())  != null) {
				if (isHeaderSet == false) { // this is header - default behaviour
					for (String s:nextLine) {
						columnName.add(s);
					}
					isHeaderSet = true;
					continue;
				} else {
					colI =0; // Start of new Row
					Map<String,String> rowmap = new HashMap<String,String>(); // to hold Row
					while ( colI < columnName.size()) { // will not take extra column if more than header
						String s = nextLine[colI];
						if (s == null)
							rowmap.put(columnName.get(colI),"");
						else
							rowmap.put(columnName.get(colI),s);
						colI++; // Move to next column
					}
					grid.add(rowI,rowmap);
				}
				rowI++; // Move to next Row
			} // End of while loop
			reader.close();
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
		
		
		ReadOpenCSVFile xlFile = new ReadOpenCSVFile();
		
		/***
		ArrayList<ArrayList<Object> > gridValue = new ArrayList<ArrayList<Object> > ();
		gridValue = xlFile.readOpenCSV("/Users/vsingh007c/Downloads/result.csv");
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
	**/

		ArrayList<Map<String,String> > gridValue = xlFile.readOpenCSVintoRow("/Users/vsingh007c/Downloads/mysqldata.csv");
		ArrayList<String> colName = xlFile.getColumns();
		for (String col : colName) 
			System.out.print(col +"\t");
		System.out.print("\n");
		
		for (int i=0; i < gridValue.size(); i++) {
			Map<String,String> row = gridValue.get(i);
			String[] keyset = new String[row.size()];
			keyset = row.keySet().toArray(keyset);
			for (String key : keyset) {
				System.out.print(key +":" + row.get(key) + "\t");
			}
			System.out.print("\n");
		}
	}
}
