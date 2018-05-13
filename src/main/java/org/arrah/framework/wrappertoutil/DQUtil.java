package org.arrah.framework.wrappertoutil;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


/**
 * @author vivek singh
 *
 */

public class DQUtil {
	
	
	/**
	 * @param colName name of Columns in the grid
	 * @param gridVal the table in grid format
	 * it will drop null rows from Grid even if a single column is null, the row is considered null
	 * @return Grid after dropping null values
	 */
	public static ArrayList<ArrayList<Object> > dropNullFromGrid(ArrayList<String> colName, ArrayList<ArrayList<Object>> gridVal) {
		
		if(gridVal == null || gridVal.isEmpty() == true) return gridVal;
		
		int rowC = gridVal.get(0).size(); // row count using first column
		Object[] row = new Object[colName.size()]; // row placeholder
		
		// Start from bottom to avoid index count
		for (int j=rowC-1; j >= 0; j--) {
			
			for (int i=0; i < colName.size(); i++) {
				row[i] = gridVal.get(i).get(j);
				if (row[i] == null) {
					dropRowFromGrid(gridVal,j); 
					break;
				}
			}
		}
		
		return gridVal;
		
	}
	
	/**
	 * @param colName name of Columns in the grid
	 * @param gridVal the table in grid format
	 * it will replace null columns with randomly generated values
	 * it is will generate appropriate values based on datatype
	 * @return Grid after replacing null values
	 */
	public static ArrayList<ArrayList<Object> > replaceNullFromGrid(ArrayList<String> colName, ArrayList<ArrayList<Object>> gridVal) {
		
		if(gridVal == null || gridVal.isEmpty() == true) return gridVal;
		
		int rowC = gridVal.get(0).size(); // row count using first column
		Object[] row = new Object[colName.size()]; // row placeholder for not null value
		
		// Fill Object to get right dataType
		boolean isfilled = true;
		for (int j=0; j < rowC; j++) {
			for (int fillIndex = 0; fillIndex < colName.size(); fillIndex++) {
				if (row[fillIndex] == null)
					row[fillIndex] = gridVal.get(fillIndex).get(j);
			}
			for (Object o: row) {
				if (o == null)
					isfilled = false;
			}
			if (isfilled == true) break;
		}
		
		// Start from bottom to avoid index count
		for (int j=rowC-1; j >= 0; j--) {
			
			for (int i=0; i < colName.size(); i++) {
				Object o = gridVal.get(i).get(j);
				if (o == null) {
					o = generateRandom(row[i]);
					gridVal.get(i).set(j,o);
				}
			}
		}
		
		return gridVal;
		
	}
	
	/**
	 * @param colData data of column
	 * it will replace null columns with randomly generated values
	 * it is will generate appropriate values based on datatype
	 * @return Column after replacing null values
	 */
	public static ArrayList<Object>  replaceNullFromColumn(ArrayList<Object> colData) {
		
		if(colData == null || colData.isEmpty() == true) return colData;
		
		int rowC = colData.size(); // row count using first column
		Object colType = null; // row placeholder for not null value
		

		for (int j=0; j < rowC; j++) {
			if (colType != null) break;
			colType = colData.get(j);
		}

		for (int i=0; i < rowC; i++) {
			Object o = colData.get(i);
			if (o == null) {
				o = generateRandom(colType);
				colData.set(i, o);
			}
		}
		
		return colData;
	}
	
	/**
	 * @param colData data of column
	 * it will replace null columns with Average value
	 * it is only for Numberic datatype
	 * @return Column after replacing null values
	 */
	public static ArrayList<Object>  replaceNullwithAvg(ArrayList<Object> colData) {
		
		if(colData == null || colData.isEmpty() == true) return colData;
		
		int rowC = colData.size(); // row count using first column
		Object colType = null; // row placeholder for not null value
		double sum = 0.0D;
		Object classTypeInfo = null;

		for (int j=0; j < rowC; j++) {
			colType = colData.get(j);
			if (colType == null || colType instanceof Number == false )
				continue;
			classTypeInfo = colType;
			sum += ((Number)colType).doubleValue();
		}
		
		Double avg = sum/rowC;
		for (int i=0; i < rowC; i++) {
			Object o = colData.get(i);
			if (o == null) {
				if (classTypeInfo instanceof Short ) {
					colData.set(i,avg.shortValue()); 
					continue;
				}
				if (classTypeInfo  instanceof Integer) {
					colData.set(i,avg.intValue()); 
					continue;
				}
				if (classTypeInfo instanceof Long || classTypeInfo  instanceof BigInteger) {
					colData.set(i,avg.longValue()); 
					continue;
				}
				if (classTypeInfo instanceof Float ) {
					colData.set(i,avg.floatValue()); 
					continue;
				}
				if (classTypeInfo instanceof Double ) {
					colData.set(i,avg.doubleValue()); 
					continue;
				}
			}
		}
		
		return colData;
	}
	
	/**
	 * @param colName name the the columns from Gid
	 * @param gridVal the input grid
	 * @return Grid with completeness information
	 */
	public static HashMap<String,ArrayList<ArrayList<Object> >> countNullColumns(ArrayList<String> colName,
			ArrayList<ArrayList<Object>> gridVal) {
		
		HashMap<String,ArrayList<ArrayList<Object> >> colGrid = new HashMap<String,ArrayList<ArrayList<Object> >> ();
		if(gridVal == null || gridVal.isEmpty() == true) return colGrid;
		
		int rowC = gridVal.get(0).size(); // row count using first column
		Hashtable<Integer,Integer> nullCountH = new Hashtable<Integer,Integer>();
		
		for (int i=0; i <= colName.size();i++ )
			nullCountH.put(i, 0); // initialise it
		
		// Start from bottom to avoid index count
		for (int j=rowC-1; j >= 0; j--) {
			int nullColcount =0;
			for (int i=0; i < colName.size(); i++) {
				Object o = gridVal.get(i).get(j);
				if (o == null) {
					nullColcount++;
				}
			}
			int newC = nullCountH.get(nullColcount);
			nullCountH.put(nullColcount,++newC);
		}
		
		ArrayList<Object> metricName = new ArrayList<Object>();
		ArrayList<Object> metricValue = new ArrayList<Object>();
		int metricI=0;
		
		for (Iterator<Integer> a = nullCountH.keySet().iterator() ; a.hasNext(); ) {
			int nullValue = a.next();
			metricName.add(metricI,nullValue +" Null Column(s)");metricValue.add(metricI++,nullCountH.get(nullValue));
		}
		ArrayList<ArrayList<Object> > profileData = new ArrayList<ArrayList<Object> >();
		profileData.add(metricName);profileData.add(metricValue);
		colGrid.put("Grid Completeness Metric", profileData);
		return colGrid;
		
	}
	
	private static ArrayList<ArrayList<Object> > dropRowFromGrid(ArrayList<ArrayList<Object>> gridVal, int j) {
		for (int i=0; i < gridVal.size(); i++) {
			gridVal.get(i).remove(j);
		}
		
		return gridVal;
	}
	
	private static Object  generateRandom(Object o) {
		Random rd = new Random();
		if (o instanceof Integer)
			return rd.nextInt();
		else if (o instanceof Long)
			return rd.nextLong();
		else if (o instanceof Double)
			return rd.nextDouble();
		else if (o instanceof Boolean)
			return rd.nextBoolean();
		else if (o instanceof LocalDate)
			return LocalDate.now();
		else if (o instanceof java.util.Date)
			return new Date();
		else 
			return generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 8);
	}
	
	private static String generateRandomChars(String candidateChars, int length) {
	    StringBuilder sb = new StringBuilder();
	    Random random = new Random();
	    for (int i = 0; i < length; i++) {
	        sb.append(candidateChars.charAt(random.nextInt(candidateChars
	                .length())));
	    }

	    return sb.toString();
	}

	/**
	 * @param searchStr string to string
	 * @param searchFrom array of strings to search from
	 * @param similarity similarity index if distance is less than this then it is match between 0 and 1
	 * it uses jaccard similarity algo
	 * @return matched string whose distance is closer than given index
	 */
	public static ArrayList<String> matchFuzzyString(String searchStr,ArrayList<String> searchFrom,double similarity) {
		
		ArrayList<String> matchedString= new ArrayList<String> ();
		
        ArrayList<Character> searchList = new ArrayList<Character>();
        for (int i=0; i < searchStr.length(); i++  ) {
			char c = searchStr.charAt(i);
			searchList.add(i, c);
		}
        ArrayList<Character> searchFromList = new ArrayList<Character>();
        
        Set<Character> union = new HashSet<Character>();
        for (String str:searchFrom ) {
        	union.clear();
        	searchFromList.clear();
	        union.addAll(searchList);
	        for (int i=0; i < str.length(); i++  ) {
				char c = str.charAt(i);
				searchFromList.add(i, c);
	        }
	        union.addAll(searchFromList);
	        
	        int inter = searchStr.length() + str.length() - union.size();
	        double similarityD = ((double)inter / union.size());
	        if (similarityD >= similarity)
	        	matchedString.add(str);
        }
        
        return matchedString;
	}
	
	public static void main(String [] args) {
		
		int index =0;
		ArrayList<String> colName = new ArrayList<String>();
		colName.add(index++,"ColA");colName.add(index++,"ColB"); colName.add(index++,"ColC");
		index =0;
		ArrayList<Object> column1= new ArrayList<Object>();
		column1.add(index++,null);column1.add(index++,null);column1.add(index++,null);
		index =0;
		ArrayList<Object> column2= new ArrayList<Object>();
		column2.add(index++,null);column2.add(index++,"Tom");column2.add(index++,"Peter");
		index =0;
		ArrayList<Object> column3= new ArrayList<Object>();
		column3.add(index++,LocalDate.MAX);column3.add(index++,LocalDate.MIN);column3.add(index++,null);
		index =0;
		ArrayList<ArrayList<Object> > gridValue = new ArrayList<ArrayList<Object> > ();
		gridValue.add(index++,column1);gridValue.add(index++,column2);gridValue.add(index++,column3);
		
		
		System.out.println("Originial Grid");
		ArrayList<Object> colData1 = gridValue.get(0);
		ArrayList<Object> colData2 = gridValue.get(1);
		ArrayList<Object> colData3 = gridValue.get(2);
		
		for (int i=0; i < colData1.size(); i++) {
				System.out.print(colData1.get(i)+"\t");
				System.out.print(colData2.get(i)+"\t");
				System.out.println(colData3.get(i));
		}
		
		
		
		//gridValue = dropNullFromGrid(colName,gridValue);
		//gridValue = replaceNullFromGrid(colName,gridValue);
		//column2 = replaceNullFromColumn(column2);
		column1 = replaceNullwithAvg(column1);
		
		/***
		HashMap<String,ArrayList<ArrayList<Object> >> profileColData =  countNullColumns(colName,gridValue);
		
		for (Iterator<String> a = profileColData.keySet().iterator() ; a.hasNext(); ) {
			String key = a.next();
			System.out.println("\nColumn:" + key);
			
			ArrayList<ArrayList<Object> > colProfData = profileColData.get(key);
			ArrayList<Object> metricN = colProfData.get(0);
			ArrayList<Object> metricV = colProfData.get(1);
			
			for (int i =0 ; i <metricN.size();i++) {
				System.out.print("[" + metricN.get(i).toString());
				System.out.println("," + metricV.get(i).toString() +"]");
			}
		}
		***/
		
		
		// For dropNullFromGrid(colName,gridValue) replaceNullFromGrid(colName,gridValue)
		System.out.println("\nAfter Replace");
		colData1 = gridValue.get(0);
		colData2 = gridValue.get(1);
		colData3 = gridValue.get(2);
		
		for (int i=0; i < colData1.size(); i++) {
				System.out.print(colData1.get(i) +"\t");
				System.out.print(colData2.get(i) +"\t");
				System.out.println(colData3.get(i));
		}
		
		/***
		
		
		ArrayList<String> searchFrom = new ArrayList<String>();
		searchFrom.add("Tom");searchFrom.add("Right");searchFrom.add("Pearson");
		ArrayList<String> s = matchFuzzyString("Tom",searchFrom,1);
		for (String st:s)
			System.out.println(st);
			
		****/
		
		/**
		System.out.println("\nColumn 3");
		for (Object o :column3)
			System.out.println(o);
		**/
	}
}
