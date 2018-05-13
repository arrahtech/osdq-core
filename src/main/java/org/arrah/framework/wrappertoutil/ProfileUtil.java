package org.arrah.framework.wrappertoutil;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author vivek singh
 *
 */

public class ProfileUtil {
	
	 enum classType {BOOLEAN,LOCALDATE,LONG,INTEGER,DOUBLE,FLOAT,SHORT,BIGINTEGER,STRING,CHARACTER,OTHERS};
	 private static Character charToreplace ='?'; // default
	 
	/**
	 * @param colName array of Column Name in the same order as gridVal
	 * @param gridVal gridvalue in the format of
	 * List<ColumnData<Object>>
	 * example Grid
	 * ------
	 * ColA		ColB
	 * 2		Tom
	 * 4		Right
	 * ------
	 * colName [ColA,ColB]
	 * gridVal [ [2,4],[Tom,Right] ]
	 * @return profiled grid value
	 * ColA ,[ [Count,2] [Sum,6]
	 * ColB, [ [Count,2] [Pattern,2]
	 * 
	 */
	
	public static HashMap<String,ArrayList<ArrayList<Object> >> profileGrid(ArrayList<String> colName,
			ArrayList<ArrayList<Object>> gridVal) {
		
		HashMap<String,ArrayList<ArrayList<Object> >> colGrid = new HashMap<String,ArrayList<ArrayList<Object> >> ();
		if(gridVal == null || gridVal.isEmpty() == true) return colGrid;
		
		for (int i=0; i < colName.size(); i++) {
			try{
				ArrayList<Object> colData = gridVal.get(i);
				ArrayList<ArrayList<Object> > profileData = analyseValue(colData);
				colGrid.put(colName.get(i), profileData);
			} catch (Exception e) {
				System.out.println("Exception for:"+colName.get(i) + " Message:"+e.getLocalizedMessage());
				continue;
			}
		}
		
		return colGrid;
		
	}
	
	
	private static ArrayList<ArrayList<Object>> analyseValue(ArrayList<Object> colData) {
		ArrayList<ArrayList<Object>> profileData = new ArrayList<ArrayList<Object>>();
		
		double count, sum=0, avg=0, uniqCount =0;
		double variance = 0, aad = 0, skew = 0, kurt = 0;
		double[] perv_a = new double[21]; // To store value
		long[] perc_a = new long[21];
		boolean isNumber = true;
		classType classT = classType.OTHERS;
		
		for (Object o:colData) {
			if (o == null) continue;
			else { 
				if (o instanceof Boolean) 
					classT = classType.BOOLEAN;
				else if (o instanceof LocalDate)
					classT = classType.LOCALDATE;
				else if (o instanceof Long)
					classT = classType.LONG;
				else if (o instanceof Short)
					classT = classType.SHORT;
				else if (o instanceof Integer)
					classT = classType.INTEGER;
				else if (o instanceof Double)
					classT = classType.DOUBLE;
				else if (o instanceof Float)
					classT = classType.FLOAT;
				else if (o instanceof BigInteger)
					classT = classType.BIGINTEGER;
				else if (o instanceof Character)
					classT = classType.CHARACTER;
				else if (o instanceof String)
					classT = classType.STRING;
				else
					classT = classType.OTHERS;
				break;
			}
		}
		
		colData = treatNull(colData,classT);
		
		if (classT == classType.LOCALDATE)
			return profileData = analyseDate(colData);
			
		// find count now
		colData.sort(null); // natural order
		count = colData.size();

		
		ArrayList<Object> metricName = new ArrayList<Object>();
		ArrayList<Object> metricValue = new ArrayList<Object>();
		int metricI=0;
		
		metricName.add(metricI,"Count");metricValue.add(metricI++,count);
		if (colData == null || count <= 0) {
			profileData.add(metricName);profileData.add(metricValue);
			return profileData;
		}
		
		for (Object o:colData) {
			if (o instanceof Number) {
				sum += ((Number) o).doubleValue();
			}
			else
				isNumber = false;
		}
		
		avg = sum / count;
		int freq_c = 1;
		Object prev_obj = null, curr_obj = null;

		// For Number Analysis
		int arr_i = 0;
		int dataset_c = 1;

		perc_a[0] = Math.round(count / 100);
		if (perc_a[0] == 0) {
			arr_i = 1;
			perv_a[0] = 0;
		}

		for (int i = 1; i < 20; i++) {
			perc_a[i] = Math.round(5 * i * count / 100);
			if (perc_a[i] == 0) {
				arr_i++;
				perv_a[i] = 0;
			}
		}
		perc_a[20] = Math.round(99 * count / 100);
		if (perc_a[20] == 0) {
			arr_i = 21;
			perv_a[20] = 0;
		}

		/* Start the loop */
		for (int c = 0; c < count; c++) {
			curr_obj = colData.get(c);
			if (curr_obj.equals(prev_obj))
				freq_c++;
			else {
				// Frequency  Analysis addition
				if (prev_obj != null && isNumber == false) {
					metricName.add(metricI,prev_obj);metricValue.add(metricI++,freq_c);
				}
				freq_c = 1;
				prev_obj = curr_obj;
				uniqCount++;
			}
			// Advance Analysis goes here if is a Number
			if (isNumber == true) {

				double d = ((Number) curr_obj).doubleValue();
				if ((arr_i < 21) == true && dataset_c == perc_a[arr_i]) {
					while (arr_i < 20 && perc_a[arr_i + 1] == perc_a[arr_i]) {
						perv_a[arr_i] = d;
						arr_i++;
					}
					perv_a[arr_i] = d;
					arr_i++;
				}

				aad += Math.abs(d - avg) / count;
				variance += Math.pow(d - avg, 2) / (count - 1);
				skew += Math.pow(d - avg, 3);
				kurt += Math.pow(d - avg, 4);

				dataset_c++;
			} // end of Number Analysis
		} 
		// Insert last value if String type
		if (prev_obj != null && isNumber == false) {
			metricName.add(metricI,prev_obj);metricValue.add(metricI++,freq_c);
		}
			
		if (isNumber == true) {
			metricName.add(metricI,"Sum");metricValue.add(metricI++,sum);
			metricName.add(metricI,"Average");metricValue.add(metricI++,avg);
			metricName.add(metricI,"Unique Count");metricValue.add(metricI++,uniqCount);
			metricName.add(metricI,"Min");metricValue.add(metricI++,colData.get(0));
			metricName.add(metricI,"Max");metricValue.add(metricI++,colData.get(colData.size() -1));
			metricName.add(metricI,"Variance");metricValue.add(metricI++,variance);
			metricName.add(metricI,"Std. Dev.(SD)");metricValue.add(metricI++,Math.sqrt(variance));
			metricName.add(metricI,"Std. Error of Mean(SE)");metricValue.add(metricI++,Math.sqrt(variance) / Math.sqrt(count));
			metricName.add(metricI,"AAD");metricValue.add(metricI++,aad);
			metricName.add(metricI,"Skewness");metricValue.add(metricI++,skew);
			metricName.add(metricI,"Kurtosis");metricValue.add(metricI++,kurt);
		}
		
		profileData.add(metricName);profileData.add(metricValue);
		return profileData;
		
	}
	
	private static ArrayList<ArrayList<Object>> analyseDate(ArrayList<Object> colData) {
		ArrayList<ArrayList<Object>> profileData = new ArrayList<ArrayList<Object>>();
		boolean isDate = true;
		int metricI=0;
		double count =0, sum=0 , avg=0;
		double variance = 0, aad = 0;
		
		ArrayList<Object> metricName = new ArrayList<Object>();
		ArrayList<Object> metricValue = new ArrayList<Object>();
		
		ArrayList<Long> colLongD = new ArrayList<Long>();
		for (Object o:colData ) {
			if (o instanceof LocalDate) {
				LocalDate d = (LocalDate)o;
				Long lv = d.toEpochDay();
				sum = sum+lv;
				colLongD.add(d.toEpochDay());
			}	else {
				isDate = false;
			}
		} 
				
		// find count now
		colLongD.sort(null); // natural order
		
		count = colLongD.size();
		metricName.add(metricI,"Count");metricValue.add(metricI++,count);
		
		avg = sum /count;
		
		for(Long d:colLongD ) {
			aad += Math.abs((d - avg)) / count;
			variance += Math.pow((d - avg), 2) / (count - 1); 
		}
		
		if (isDate == true) {
			LocalDate localD;
			
			
			localD = LocalDate.ofEpochDay(colLongD.get(colLongD.size() - 1));
			metricName.add(metricI,"Maximum Date");metricValue.add(metricI++,localD);

			localD = LocalDate.ofEpochDay(colLongD.get(0));
			metricName.add(metricI,"Minimum Date");metricValue.add(metricI++,localD);
			
			double dayval = colLongD.get(colLongD.size() - 1) -colLongD.get(0);
			metricName.add(metricI,"Range(Max-Min) in Days");metricValue.add(metricI++,dayval);
			
			localD = LocalDate.ofEpochDay((long)avg);
			metricName.add(metricI,"Mean Value");metricValue.add(metricI++,localD);
			
			metricName.add(metricI,"Avg. Absolute Dev.(AAD) in Days");metricValue.add(metricI++,aad);
			metricName.add(metricI,"Variance in Days");metricValue.add(metricI++,variance);
			metricName.add(metricI,"Std. Dev.(SD) in Days");metricValue.add(metricI++,Math.sqrt(variance));

		}
		profileData.add(metricName);profileData.add(metricValue);
		return profileData;
	}
	
	public static void setcharToreplace(Character c) {
		charToreplace = c;
	}
	
	private static ArrayList<Object> treatNull(ArrayList<Object> columnData,  classType classTypeInfo) {
		for (int i=0; i < columnData.size(); i++ ) {
			Object o = columnData.get(i);
			if ( o != null) continue;
			
			// Now treat null according to classType
			if (classTypeInfo == classType.BOOLEAN || classTypeInfo == classType.LOCALDATE || classTypeInfo == classType.OTHERS) {
				columnData.remove(i--); // size will reduce in next iteration
				continue;
			}
			if (classTypeInfo == classType.STRING  ) {
				columnData.set(i,"NULL"); 
				continue;
			}
			if (classTypeInfo == classType.CHARACTER) {
				columnData.set(i,charToreplace); 
				continue;
			}
			if (classTypeInfo == classType.INTEGER || classTypeInfo == classType.SHORT) {
				columnData.set(i,0); 
				continue;
			}
			if (classTypeInfo == classType.LONG || classTypeInfo == classType.BIGINTEGER) {
				columnData.set(i,0L); 
				continue;
			}
			if (classTypeInfo == classType.DOUBLE ) {
				columnData.set(i,0D); 
				continue;
			}
			if (classTypeInfo == classType.FLOAT ) {
				columnData.set(i,0F); 
				continue;
			}
		}
		
		return columnData;
	}
	
	
	public static void main(String [] args) {
		
		ArrayList<String> colName = new ArrayList<String>();
		colName.add("ColA");colName.add("ColB"); colName.add("ColC");
		
		ArrayList<Object> column1= new ArrayList<Object>();
		column1.add(null);column1.add(4.00F);column1.add(7.00F);
		
		ArrayList<Object> column2= new ArrayList<Object>();
		column2.add("Tom");column2.add("Right");column2.add(null);
		
		ArrayList<Object> column3= new ArrayList<Object>();
		column3.add(null);column3.add(LocalDate.MAX);column3.add(LocalDate.MIN);
		// column3.add(null);column3.add(null);column3.add(null);
		
		ArrayList<ArrayList<Object> > gridValue = new ArrayList<ArrayList<Object> > ();
		gridValue.add(column1);gridValue.add(column2);gridValue.add(column3);
		
		HashMap<String,ArrayList<ArrayList<Object> >> profileColData =  profileGrid(colName,gridValue);
		
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
		
	}
}
