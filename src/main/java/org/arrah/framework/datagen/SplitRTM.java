package org.arrah.framework.datagen;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2016    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used to split RTM into
 * training data, validation data and test data
 * If timeseries data, it will put latest data
 * into test otherwise put random split
 * 
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.arrah.framework.ndtable.ReportTableModel;


public class SplitRTM {

	private static int maxIndex = -1;

	public SplitRTM() {
		
	} // Constructor
	
	// This function will assume duplicate records has been already removed
	// It will first shuffle the columns and get % accordingly.
	public ReportTableModel[] splitRandom(ReportTableModel rtm, int[] splitperc ) {
		int rowc= rtm.getModel().getRowCount();
		int colc = rtm.getModel().getColumnCount();
		int [] colIndex = new int[colc];
		for (int i = 0; i < colc; i++ ) {
			colIndex[i] = i;
		}
		rtm = ShuffleRTM.shuffleColumns(rtm, colIndex,0,rowc);
		
		// Randomization done, now sample it
		int sampleno = splitperc.length;
		int splitvalue[] = new int[sampleno];
		
		// may have rounding error because of floor 
		//but for large dataset it should not matter.
		for (int i=0; i < splitvalue.length; i++) {
			int buckno= (int) Math.floor(splitperc[i] * rowc / 100);
			if (i == 0)
				splitvalue[i] = buckno;
			else
				splitvalue[i] = splitvalue[i-1] + buckno; // Cumulative
		}
		
		// now create sample table
		// should we make it multi-threaded ???
		ReportTableModel[] rtmsplit = new ReportTableModel[sampleno];
		int rowindex=0;
		for (int i=0; i < sampleno; i++) {
			rtmsplit[i] = new ReportTableModel(rtm.getAllColName(),true,true);
			while (rowindex < rowc) {
				if (rowindex == splitvalue[i]) // now reached end of this loop or if there is 0
					break;
				rtmsplit[i].addFillRow(rtm.getRow(rowindex));
				rowindex++;
			}
			
		}
		return rtmsplit;
	}
	
	// This function will split RTM by date
	// epoch to first in date[0] next set in date[1]
	// date[last] - most recent data total split would be n+1
	// It will assume date is in sorted order
	public ReportTableModel[] splitByDate(ReportTableModel rtm, int dateCol, java.util.Date[] splitdate ) {
		
		// if will scan data in  chronological order
		int rowc = rtm.getModel().getRowCount();
		int splitc = splitdate.length;
		
		// now create sample table
		// should we make it multi-threaded ???
		
		ReportTableModel[] rtmsplit = new ReportTableModel[splitc+1]; // n+1 split
		for (int i=0; i < splitc+1; i++) {
			rtmsplit[i] = new ReportTableModel(rtm.getAllColName(),true,true);
		}
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, dateCol);
			boolean outbound = true;
			if (o == null) continue;
			try {
				
				for (int j=0; j < splitc; j++) {
					if (((Date)o).before(splitdate[j]) == true) {
						rtmsplit[j].addFillRow(rtm.getRow(i));
						outbound = false;
						break;
					}
				}
				// did not fit in any bucket as it is after most recent
				if (outbound == true) {
					rtmsplit[splitc].addFillRow(rtm.getRow(i));
				}
				
				
			} catch (Exception e) {
				System.out.println("Exception:" + e.getLocalizedMessage());
			}
		}
		
		return rtmsplit;
	}
	
	   // This util function will split the rtm in multiple rtm
    public static ReportTableModel[] splitRTM(ReportTableModel rtm, int count) {
        ReportTableModel[] newrtm = null;
        int rowc = rtm.getModel().getRowCount();

        if (count < 0  || rtm == null || rowc <= 0 || rtm.getModel().getColumnCount() <= 0)  {
            newrtm= new ReportTableModel[1];
            newrtm[0] = rtm; // no split required
            return newrtm;
        }

        int grp1 = Math.floorDiv(rowc,count);
        newrtm= new ReportTableModel[count];

        String[] colName = rtm.getAllColNameStr();

        for (int i=0; i < count; i++ ) {
            ReportTableModel newRTM = new ReportTableModel(colName,true,true);
            newrtm[i] = newRTM;
            for (int j=i*grp1; j < i*grp1 + grp1; j++) {
                newrtm[i].addFillRow(rtm.getRow(j));
            }
        }
        for (int j=count*grp1; j <rowc; j++) {
            newrtm[count-1].addFillRow(rtm.getRow(j));
        }

        return newrtm;
    }
    

    // This util function will create a random sample RTM
    public static ReportTableModel sampleRTM(ReportTableModel rtm, int count) {
        if (count < 0  || rtm == null || rtm.getModel().getRowCount() <= 0
                || rtm.getModel().getColumnCount() <= 0) return rtm; // no sampling needed

        String[] colName = rtm.getAllColNameStr();
        ReportTableModel newRTM = new ReportTableModel(colName,true,true);

        for (int i=0; i < count; i++ ) {
            int randInt = new Random().nextInt(count);
            Object[] row = rtm.getRow(randInt);
            newRTM.addFillRow(row);
        }

        return newRTM;
    }
    
    public static ReportTableModel explodeRTM(ReportTableModel tableModel, LinkedHashMap<String,List<Integer>> colToRow) {
    	String [] colName = tableModel.getAllColNameStr();
    	List<Integer> deleteIndex = new ArrayList<Integer>();
    	
    	for (String newCol: colToRow.keySet()) {
    		List<Integer> deleteSet = colToRow.get(newCol);
    		// take only odd values
    		List<Integer> newR = new ArrayList<Integer>();
    		for (int i=0; i < deleteSet.size(); i=i+2){
    			newR.add(deleteSet.get(i));
    		}
    		deleteSet = newR;
    		int firstIndex = deleteSet.get(0);
    		colName[firstIndex] = newCol;
    		
    		for (int i=1; i<deleteSet.size(); i++)
    			deleteIndex.add(deleteSet.get(i));
    		
    	}
    	deleteIndex.sort(null);
    	
    	String [] newcolName = new String[colName.length - deleteIndex.size()];
    	int index = 0;
    	
    	for (int i=0; i <colName.length; i++) {
    		if (deleteIndex.indexOf(i) == -1)
    			newcolName[index++] = colName[i];
    	}
    	ReportTableModel rtm = new ReportTableModel(newcolName,true,true);
    	
    	
    	List<List<Object>> finalTable = new ArrayList<List<Object>> ();
    	for (int i=0; i < tableModel.getModel().getRowCount(); i++) {
    		List<List<Object>> explodeRow = explodeRow(tableModel.getRow(i),colToRow);
    		
    		/***
    		for (List<Object> row:explodeRow)
    			System.out.println(row);
    		***/
    		
    		for (List<Object> row:explodeRow) {
    			List<Object> newRow= new ArrayList<Object>();
    			for (Object o: row)
    				newRow.add(o);
    			
    			// System.out.println("next:" + newRow + " Size:"+deleteIndex.size()+ " DeleteI"+deleteIndex);
	    		for (int j = deleteIndex.size()-1; j>=0; j-- ) {
	    			//System.out.println("J:" +j +"  -- "+(deleteIndex.size()-1));
	    			int indexToD = deleteIndex.get(j);
	    			//System.out.println(" DeleteIndex"+indexToD + "J:" +j);
	    			newRow.remove(indexToD);
	    		}
	    		finalTable.add(newRow);
    		}
    	}
    	for (List<Object> row:finalTable)
    		rtm.addFillRow(row.toArray());
    	
    	return rtm;
    }
    
    // This function will take a row and explode into multiple rows but putting colArray into row of a single column
    // Like A[0].name,A[1].name,age
    //	vivek,singh,20
    // After conversion will come
    //A.name,age
    //vivek,20
    //singh,20

    public static List<List<Object>> explodeRow(Object[] row, LinkedHashMap<String,List<Integer>> colToRow) {
    	
    	List<List<Object>> explodeL = new ArrayList<List<Object>>();
    	final List<Object> origR = new ArrayList<Object>();
    	for (Object o:row) {
    		origR.add(o);
    		// System.out.println(o);
    	}
    	
    	if (row.length == 0 || colToRow.size() == 0 ) {
    		explodeL.add(origR);
    		return explodeL;
    	}
    	
    	/**
    	for (String s:colToRow.keySet()) {
    		System.out.println(s+":"+colToRow.get(s));
    	}**/
    	
    	// Add enough rows half of the total count
		for (int i=0; i<= maxIndex; i++){
			List<Object> newR = new ArrayList<Object>();
			for (Object o:origR)
				newR.add(o);
			explodeL.add(newR);
		}
		
		// System.out.println("Max Index:"+maxIndex);
		
    	for(List<Integer> value:colToRow.values()) {
    		int firstI = value.get(0);
    		// take only odd value
    		List<Integer> newR = new ArrayList<Integer>();
    		for (int i=0; i < value.size(); i=i+2){
    			newR.add(value.get(i));
    		}
    		// take only even value
    		List<Integer> newER = new ArrayList<Integer>();
    		for (int i=1; i < value.size(); i=i+2){
    			newER.add(value.get(i));
    		}
    		value = newR; // assign new value
    		
    		// make all empty
    		for (int i=0; i < explodeL.size(); i++) {
    			List<Object> rowToChange = explodeL.get(i);
    			rowToChange.remove(firstI);
    			rowToChange.add(firstI,"");
    			explodeL.remove(i);
    			explodeL.add(i,rowToChange);
    		}
    		
    		
    		//System.out.println("Value:"+value);
    		//System.out.println("NewER:"+newER);
    		//System.out.println("FirstI:"+firstI);
    		
    		for (int i=0; i < value.size(); i++) { // start from first
    			List<Object> rowToChange = explodeL.get(newER.get(i));
    			rowToChange.remove(firstI);
    			rowToChange.add(firstI,origR.get(value.get(i)));
    			explodeL.remove((int)newER.get(i));
    			//System.out.println("AddI:"+newER.get(i) + " - "+rowToChange);
    			explodeL.add(newER.get(i),rowToChange);
    		}	
    	}
    	
    	return explodeL;
    }
    
    // This function will take columns Names and show columns which has indexes at at
    // input --  Like A[0].name,A[1].name,age,A[0].id[0],A[0].id[1]
    // output -- A[] , A.id[]


    public static List<String> getFlattableColumns(String[] colNames) {
    	List<String> flatC = new ArrayList<String>();
    	for(String s: colNames) {
    		int searchFrom = 0;
    		int indexVal = -1;
    		while( ( indexVal = s.indexOf("[",searchFrom)) != -1) { // found flattening information
    			searchFrom = indexVal+1; // search from next character
    			String value = s.substring(0, indexVal).replaceAll("\\[\\d\\]", ""); // replace [number]
    			value = value.concat("[]");
    			if (flatC.indexOf(value) == -1)
    				flatC.add(value);
    		}
    	}
    	return flatC;
    }
    
    // This function will a flattable column and search from all column where it matches
    // input --  Like A[]
    // output -- A[0].id , A[0].id[1],A[1], A[1].id[2]
    
    public static LinkedHashMap<String,List<Integer>> getMatchingColumns(String flatColumn,String[] colNames) {
    	LinkedHashMap<String,List<Integer>> flatCI = new LinkedHashMap<String,List<Integer>> ();
    	flatColumn = flatColumn.substring(0, flatColumn.length() - 2); // take without end "[]"
    	
    	int i=0;
    	maxIndex = -1;
    	for(String s: colNames) {
    		String[] origN = s.split("\\.");
    		s = s.replaceAll("\\[\\d\\]", ""); // replace [number]
    		if (s.indexOf(flatColumn,0) == 0) { // match from start
    			String col = s.substring(0,flatColumn.length());
    			String[] afterS = col.split("\\.");
    			int len = afterS.length -1;
    			col="";
    			int indexmatched = -1;
    			for (int j=0; j <origN.length; j++) {
    				if (j==len) {
    					if ("".equals(col) == false && col.endsWith(".") == false)
        					col = col+".";
    					col = col+afterS[j];
    					// It is right place to grab index then pass it on
    					String stripIndex = origN[len];
    					String indexStr = stripIndex.substring(stripIndex.indexOf("[") + 1, stripIndex.indexOf("]"));
    					//System.out.println(indexStr);
    					try{
    						indexmatched = Integer.parseInt(indexStr);
    					} catch(Exception e) {
    						indexmatched = -1;
    					}
    					if (indexmatched > maxIndex) maxIndex =indexmatched;
    					continue;
    				}
    				if ("".equals(col) == false && col.endsWith(".") == false)
    					col = col+".";
    				col = col+origN[j];
    					
    			}
    			List<Integer> colI = flatCI.get(col);
    			if (colI == null) {
    				colI = new ArrayList<Integer>();
    				colI.add(i);
    				colI.add(indexmatched); // tuple of column and index matched 2,0 ( column 2 index 0)
    				flatCI.put(col,colI);
    			} else {
    				colI.add(i);
    				colI.add(indexmatched);
    				flatCI.put(col,colI);
    			}
    		}
    		i++;
    	}
    	return flatCI;
    }
	
} // End of SplitRTM
