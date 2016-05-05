package org.arrah.framework.analytics;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/


/*
 * This file will be used to create tabular, pivot
 *  and OLAP cubes.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;

public class TabularReport  {
	
	
	// Utility function to create tabular report from master
	// _reportColV will have index of column name for new Table
	// _reportFieldV will the type like Sum, Min, Group By
	public static ReportTableModel showReport (ReportTableModel _rt, Vector<Integer> _reportColV,
			Vector<Integer> _reportFieldV ) {
		
		int newColC = _reportColV.size();
		String[] newColN = new String[newColC];
		int[] newColT = new int[newColC];
				
		for (int i = 0; i < newColC; i++) {
			newColN[i] = _rt.getModel().getColumnName(_reportColV.get(i));
			newColT[i] = _reportFieldV.get(i);
		}
		Vector<Integer> dimV = new Vector<Integer>();
		Vector<Integer> measureV = new Vector<Integer>();
		
		for (int i = 0; i < newColC; i++) {
			int fieldVal = _reportFieldV.get(i);
			if ( fieldVal == 0 ) { // Dimension // Group By
				dimV.add(_reportColV.get(i));
			} else {
				measureV.add(_reportColV.get(i));
			}
		}
		
		ReportTableModel newRT = new ReportTableModel(newColN,false,true);
		int dimSize = dimV.size();
		Object[] dimObj = new Object[dimSize];
		int measureSize = measureV.size();
		Object[] measureObj = new Object[measureSize];
		
		// A Hashtable to contain group by value and rowid
		Hashtable<String,Integer> rptContent = new Hashtable<String, Integer>();
		int newRowIndex = 0;
		
		// A Hashtable to contain group by value and count
		Hashtable<String,Integer> dimCount = new Hashtable<String, Integer>();
		Integer existingCount = null;
		int _rowC = _rt.getModel().getRowCount();
		for (int i=0; i< _rowC; i++) { //scan the table and create new tables
			
			boolean newrecord = true;
			Object[] row = new Object[newColC];
			String dimensionId ="";
			
			for ( int j=0; j<dimSize ; j++) {
				dimObj[j] = _rt.getModel().getValueAt(i,dimV.get(j));
				if (dimObj[j] == null) 
					dimensionId += "Undefined";
				else
					dimensionId += dimObj[j].toString();
			}
			Integer existingrowid = rptContent.get(dimensionId);
			existingCount = dimCount.get(dimensionId);
			
			if (existingrowid == null) {
				rptContent.put(dimensionId,newRowIndex++);
				existingCount = 1;
				dimCount.put(dimensionId, existingCount); // First Instance
			} else {
				newrecord = false;
				dimCount.put(dimensionId,++existingCount);
			}
			
			
			for ( int j=0; j<measureSize ; j++) {
				measureObj[j] = _rt.getModel().getValueAt(i,measureV.get(j));
			}
			// prepare new Record
			for ( int j=0; j<newColC ; j++) {
				int fieldVal = newColT[j];
				if ( fieldVal == 2) {  // Absolute Sum
					try {
					row[j] = Math.abs((Double)(_rt.getModel().getValueAt(i,_reportColV.get(j))));
					} catch (Exception e) {
						System.out.println("\n Can not cast table value as Number");
					}
				} else if ( fieldVal == 3) { // Count
					row[j] = (Double)existingCount.doubleValue();
				} else {
					row[j] = _rt.getModel().getValueAt(i,_reportColV.get(j));
				}
			}
			
			if (newrecord == true) {
				newRT.addFillRow(row);
			} else { // add sum, abs sum, count, avg
				Object[] existMeasureObj = new Object[measureSize];
				int k=0;
				for (int j = 0; j < newColC; j++) {
					int fieldVal = newColT[j];
					if (fieldVal == 1 || fieldVal == 2 // Sum // Absolute Sum
							||  fieldVal == 3 ||  fieldVal == 4 ) {  // Count // Avg
						existMeasureObj[k] = newRT.getModel().getValueAt(existingrowid.intValue(),j);
						Double newVal =0D;
						if (newRT.getModel().getColumnClass(j).getName().toString().toUpperCase().contains("DOUBLE")) {
							if (fieldVal == 1) {
								 newVal = (Double)existMeasureObj[k] + (Double)measureObj[k];
							} else if (fieldVal == 2) {
								 newVal = Math.abs((Double)existMeasureObj[k]) + Math.abs((Double)measureObj[k]);
							} else if (fieldVal == 3) {
								newVal = existingCount.doubleValue();
							} else if (fieldVal == 4) {
								newVal = (((Double)(existMeasureObj[k])*(existingCount-1)) + (Double)measureObj[k])/existingCount;
							}
							
							newRT.getModel().setValueAt(newVal, existingrowid.intValue(), j);
							k++;
						} else {
							System.out.println("\n Value is not Number");
						}
					}		
						
				}
				
				
			} // End of updating existing row

		} // End of For loop
		
		return newRT;
	}
	
	// This utility fuction will take a tabular report RTM and
	// convert into crossTab RTM. it may have 0 or more row dimension
	// and 0 and more column dimension.
	
	// this _rtm is changed RTM which has only info about the selected cols
	public static ReportTableModel tabToCrossTab(ReportTableModel _rtm, Vector<Integer> _reportColV,
			Vector<Integer> _reportFieldV ) {
		
		// rowdim1 , rowdim2, coldim1v1, coldim1v2, coldim1,coldim2v1,coldim2v2,coldim2
		//                     metric1, metric1,    aggr,   metric 2, metric 2, aggr
		
		// get count & index of row dimension and column dimension
		ArrayList<Integer> rowdimI = new ArrayList<Integer>();
		ArrayList<Integer> coldimI = new ArrayList<Integer>();
		
		class keyVal {
			int _key;
			int _val;
			public keyVal(int key, int val) {
				_key = key;
				_val = val;
				
			}
		}
		ArrayList<keyVal> merticI =new  ArrayList<keyVal>(); // holding metric index
		
		for (int i=0; i < _reportFieldV.size(); i++ ) {
			if (_reportFieldV.get(i) == 0) // rowDim
				rowdimI.add(i);
			else if (_reportFieldV.get(i) == 1) // colDim
				coldimI.add(i);
			else
				merticI.add(new keyVal(i, _reportFieldV.get(i)));
		}
		
		@SuppressWarnings("unchecked")
		List<String> [] coldimN = new List[coldimI.size()];
		
		for (int i=0; i< coldimI.size(); i++) 
			coldimN[i] = new ArrayList<String>();
		
		ArrayList<Object[]> lineRowItem = new ArrayList<Object[]>(); // to hold Row Dimension
		
		// Get cardinality of column dimensions
		// Add column if is not there in the list
		for (int i=0; i < _rtm.getModel().getRowCount(); i++) {
			for (int j=0; j< coldimI.size(); j++ ) {
				Object obj = _rtm.getModel().getValueAt(i, coldimI.get(j));
				if (obj == null) obj = ""; // replace Null by empty string
						
				if ( coldimN[j].contains(obj.toString()) == false) // new listing for col Dim
					coldimN[j].add(obj.toString());
			}
		} // end of first pass
		
		// Let's sort the values
		for (int i=0; i < coldimN.length; i++)
			Arrays.sort(coldimN[i].toArray());
		
		// Get loop to create row dimension metric
		List<Object[][][]> listlineMetricItem = new ArrayList<Object[][][]>();
		
		// Create Storage to hold data
		// May have more than one metric to hold data
		Object[][][] lineMetricItem = new Object [merticI.size()][coldimI.size()][];
		for (int meti=0; meti<merticI.size(); meti++) 
			for (int j=0; j<coldimI.size(); j++)
				lineMetricItem[meti][j] = new Object[coldimN[j].size()];
		

		int rowCount = _rtm.getModel().getRowCount();
		Object[] prevRow = new Object[rowdimI.size()];
		for (int j=0; j< rowdimI.size(); j++ )
			prevRow[j] = _rtm.getModel().getValueAt(0, rowdimI.get(j)); // fill first row
		boolean isNewRow=false;
		
		// Start the loop
		for (int i=0; i < rowCount ; i++) {
			Object[] newRow = new Object[rowdimI.size()];
			isNewRow=false;
			// Doing Group by for Row Dimension
			for (int j=0; j< rowdimI.size(); j++ ) {
				newRow[j] = _rtm.getModel().getValueAt(i, rowdimI.get(j));
				// Compare as string even other datatype would be fine
				//if (newRow[j] == prevRow[j]) continue; //already there
				if (newRow[j].toString().compareToIgnoreCase(prevRow[j].toString()) == 0) continue; //already there
				else  {
					isNewRow = true;
				}
			}
			
			if (isNewRow == true ) { // new line for crossTab
				lineRowItem.add(prevRow);
				prevRow = new Object[rowdimI.size()];
				for (int j=0; j< rowdimI.size(); j++ )
					prevRow[j] = newRow[j];
				listlineMetricItem.add(lineMetricItem);
				// now create new object
				lineMetricItem = new Object [merticI.size()][coldimI.size()][];
				for (int meti=0; meti<merticI.size(); meti++) 
					for (int j=0; j<coldimI.size(); j++)
						lineMetricItem[meti][j] = new Object[coldimN[j].size()];
				
			}	
			
			// n X m - colDim X metric val
			// RTM has been adjusted to take row dimension first
			for (int j=0; j< coldimI.size(); j++ ) {
				Object colobj = _rtm.getModel().getValueAt(i, coldimI.get(j));
				int index = coldimN[j].indexOf(colobj.toString());
				
				for (int metV=0; metV < merticI.size(); metV++) { // loop for each metric
					Object metobj = _rtm.getModel().getValueAt(i, merticI.get(metV)._key);
					int aggrT = merticI.get(metV)._val;
					
					// except Avg everything will be sum
					if (lineMetricItem[metV][j][index] != null) {
						if (aggrT == 5) //avg
							lineMetricItem[metV][j][index] = ((Double) lineMetricItem[metV][j][index] + (Double)metobj)/2;
						else
							lineMetricItem[metV][j][index] = (Double) lineMetricItem[metV][j][index] + (Double)metobj;
					}
					else	
						lineMetricItem[metV][j][index] =(Double)metobj;
				}
			}
			if (i == (rowCount -1) ) { // lastline line for crossTab
				lineRowItem.add(newRow);
				listlineMetricItem.add(lineMetricItem);
			}	
					
			
		} // end of Second pass
		
		// Create columns for new RTM as crossTab
		// Create Column Header
		int size = 0;
		for (int i=0 ; i < coldimN.length ; i++ ) {
			 size += coldimN[i].size();
		}
		// One for each Metric
		String[] colName = new String[rowdimI.size() + (size + coldimN.length) * merticI.size() ]; // Column dimension Name
		
		for (int i=0; i <rowdimI.size() ; i++ ) {
			colName[i] = _rtm.getModel().getColumnName(rowdimI.get(i));
		}
		
		int inc=0;
		for (int metricC =0; metricC < merticI.size(); metricC++) {
			for (int i=0 ; i < coldimN.length ; i++ ) {
				for (int j=0 ; j < coldimN[i].size() ; j++ ) {
					colName[rowdimI.size() + inc++] = coldimN[i].get(j);
				}
				int metVal = merticI.get(metricC)._val;
				String appned="";
				switch(metVal) {
				case 2: 
					appned = "(Sum)";
					break;
				case 3: 
					appned = "(Abs. Sum)";
					break;
				case 4: 
					appned = "(Count)";
					break;
				case 5: 
					appned = "(Avg)";
					break;
				default:
					break;
				}
				
				colName[rowdimI.size() + inc++] = _rtm.getModel().getColumnName(coldimI.get(i)) +appned;
			}
		}
		
		ReportTableModel rtm = new ReportTableModel(colName);
		// Fill the data
		// Flatten the data nXm array and put into Single Dimension
		
		int rowC = lineRowItem.size();
		for (int i=0; i <rowC; i++  ) {
			Object[] crossRow = new Object[colName.length];
			
			// Get RowDimension First
			int rowdimS = lineRowItem.get(i).length;
			for (int j=0; j<rowdimS; j++ )
				crossRow[j] = lineRowItem.get(i)[j]; // row dimension filled
			
			// Get Metric and fill
			Object[][][] metrticV = listlineMetricItem.get(i);
			
			for (int metricC =0; metricC < merticI.size(); metricC++)
				for (int coldimName =0; coldimName < coldimN.length; coldimName++) {
					double aggr = 0D;
					for (int index =0; index < coldimN[coldimName].size(); index++) {
						
						if ( null == metrticV[metricC][coldimName][index]) 
							metrticV[metricC][coldimName][index] = 0D;
						
						int aggrT = merticI.get(metricC)._val;
						if (aggrT ==5 ) //Avg
							aggr = (aggr + (Double)metrticV[metricC][coldimName][index] )/2;
						else
							aggr += (Double)metrticV[metricC][coldimName][index];
						crossRow[rowdimS++] = metrticV[metricC][coldimName][index];
					}
					// fill aggregate value
					crossRow[rowdimS++] = aggr;
				}
			
			rtm.addFillRow(crossRow);
		}
		
		return rtm;
		
	}
	
	
	
	
	// This function will create crossTab for the given Report Table Model
	// This function is not used for now
	@SuppressWarnings("unchecked")
	public static ArrayList<Number[]> createBitMap (ReportTableModel _rt, Vector<Integer> _reportColV,
			Vector<Integer> _reportFieldV, ArrayList<Object> [] cardinal  ) {
		
		/* This function will first group the Row Dimension by the order that have been received
		 * Then it will group by Column Dimension When the Row and Column matrix is created
		 * it will fill matrix with values in cross tab.
		 * it may have zero or more  row dim and  zero or more column dimensions
		 */
		
		// Create a bitmap kind of structure to hold values in single pass itself
				
		int arrSize = _reportFieldV.size();
		cardinal = new ArrayList[arrSize]; // to hold cardinal value
		ArrayList<Number[]> bitArray = new ArrayList<Number[]>(); // to hold bit values nXm dimension
		
		for (int i=0; i < arrSize; i++) 
			cardinal[i] = new ArrayList<Object>();
		
		int rowCount = _rt.getModel().getRowCount();
		
		for (int i=0; i < rowCount; i++ ) { // loop into  row data
			Number[] newRow = new Number[arrSize];
			
			for (int j=0; j < arrSize; j++ ) {  // loop into  column data
				Object col = _rt.getModel().getValueAt(i, _reportColV.get(j));
				
				int fieldVal = _reportFieldV.get(i);
				if ( fieldVal == 0  || fieldVal == 1) { // Row Dimension Col Dimension
					// if dimension values check if exist 
					int index = cardinal[j].indexOf(col);
					if ( index < 0 ) // could not find value
						cardinal[j].add(col);
					else
						newRow[j] = cardinal[j].indexOf(col);
				} else { // measure
					newRow[j] = Double.parseDouble(col.toString());
				}
				
				// Measure add only
						
			}
			bitArray.add(i,newRow);
		}
		
		return bitArray;
	}
	
	
}
