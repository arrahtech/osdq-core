package org.arrah.framework.analytics;

/**************************************************
*     Copyright to Vivek K Singh      2014        *
*                                                 *
* Any part of code or file can be changed,        *
* redistributed, modified with the copyright      *
* information intact                              *
*                                                 *
* Author$ : Vivek Singh                           *
*                                                 *
**************************************************/

/*
* This file will provide utility functions 
* to diff between two RTM (Report Table Model)
* classes.
*
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.arrah.framework.ndtable.ReportTableModel;

public class RTMDiffUtil {	
	
	private ReportTableModel leftRTM = null, rightRTM = null;
	private boolean allColMatch = false;
	private Vector<Integer> leftIndex = null, rightIndex = null;
	private ReportTableModel matchRTM = null, leftNoMatchRTM= null, rightNoMatchRTM = null, matchFailedRTM=null;
	private HashMap<Integer, Integer> matchedIndex;
	private HashMap<Integer, Integer> matchedDiffIndex;
	private int duplicateC = 0;
	
	public RTMDiffUtil() {
		// Default Constructor
	}
	public RTMDiffUtil(ReportTableModel left, ReportTableModel right) {
		leftRTM = left;
		rightRTM = right;
		allColMatch = true;
	}
	public RTMDiffUtil(ReportTableModel left, Vector<Integer> leftI, ReportTableModel right, Vector<Integer> rightI) {
		leftRTM = left;
		rightRTM = right;
		allColMatch = false;
		leftIndex = leftI;
		rightIndex = rightI;
	}
	
	// Create header with Index Info
	private void createTableHeader() {
		if (leftRTM == null || rightRTM == null)
			return;
		int leftColC = leftRTM.getModel().getColumnCount();
		int rightColC = rightRTM.getModel().getColumnCount();
		
		int rightC = (leftColC >= rightColC ) ? rightColC : leftColC; // common data set
		
		String[] matchedH = new String[rightC +2]; // left matched Index right matched Index
		matchedH[0] = "Left Index";matchedH[1] = "Right Index";
		String[] leftNoMatchH = new String[leftColC + 1]; // left Index
		leftNoMatchH[0]= "Index";
		String[] rightNoMatchH = new String[rightColC + 1]; // right Index
		rightNoMatchH[0] = "index";
		String[] nonmatchedH = new String[rightC +1]; // key match data no match
		nonmatchedH[0] = "Index";
		
		for (int i=0; i < leftColC ; i++)
			leftNoMatchH[i+1] = leftRTM.getModel().getColumnName(i);
		for (int i=0; i < rightColC ; i++)
			rightNoMatchH[i+1] = rightRTM.getModel().getColumnName(i);
		for (int i=0; i < rightC ; i++)
			matchedH[i+2] = leftRTM.getModel().getColumnName(i);
		for (int i=0; i < rightC ; i++)
			nonmatchedH[i+1] = leftRTM.getModel().getColumnName(i);
			
		matchRTM = new ReportTableModel(matchedH, true,true);
		leftNoMatchRTM = new ReportTableModel(leftNoMatchH, true,true);
		rightNoMatchRTM = new ReportTableModel(rightNoMatchH, true,true);
		matchFailedRTM = new ReportTableModel(nonmatchedH, true,true);
	}
		
	/*
	* Comparator for comparing rows from RTM
	* as string and whether to make cell listing or not
	*/	
	public boolean compare(boolean asString, boolean isKey)
	{
		// return true is comparison is successful
		if (leftRTM == null || rightRTM == null) {
			System.out.println("Can not Compare Null Table(s)");
			return false;
		}
		if ( (leftIndex != null && rightIndex == null) || (leftIndex == null && rightIndex != null) )
				allColMatch = true; //Default Behavior
				
		if (leftIndex != null && rightIndex != null) 
			if (leftIndex.size() != rightIndex.size() ) {
				System.out.println("Left and Right Columns are not Mapped");
				return false;
			}
		
		// now create holding RTM
		createTableHeader();
		matchedIndex = new HashMap<Integer,Integer>();
		int leftRowC = leftRTM.getModel().getRowCount();
		int rightRowC = rightRTM.getModel().getRowCount();
		
		for (int i=0 ; i < leftRowC; i++) {
			Object[] leftRow = leftRTM.getRow(i);
			if (leftRow == null ) continue;
			boolean ismatch = false;
			
			for (int j=0 ; j < rightRowC; j++) { // Iterate thru right RTM
				Object[] rightRow = rightRTM.getRow(j);
				if (rightRow == null ) continue;
				
				if ( allColMatch == true ) {
					ismatch =matchAllColumn(leftRow, rightRow, asString);
				}
				else {
					ismatch = matchIndexColumn(leftRow,leftIndex, rightRow,rightIndex, asString);
				}
				
				if (ismatch == true) {
					if(isKey == false) { // cell matching
					
						if (matchedIndex.containsKey(j))
							continue; // this record is already counted. Key is rightIndex
						
						int leftColC = leftRow.length;
						Object[] newRow = new Object[leftColC+2];
						newRow[0] = i; newRow[1] = j;  // Index
						for (int lc =0; lc  < leftColC ; lc++)
							newRow[lc+2] = leftRow[lc];
						matchRTM.addFillRow(newRow);
						matchedIndex.put(j, i);
						break; // break inner loop to maintain 1:1 relationship
					} else { //key matching
						
						if (matchedIndex.containsKey(j)) {
							duplicateC++;
							continue; // this record is already counted. Key is rightIndex
						}
						matchedIndex.put(j, i); // matched key
						
						// Keys have matched now match full record
						boolean allCmatch =matchAllColumn(leftRow, rightRow, asString);
						if (allCmatch == true) { // all cols Matched
							int leftColC = leftRow.length;
							Object[] newRow = new Object[leftColC+2];
							newRow[0] = i; newRow[1] = j;  // Index
							for (int lc =0; lc  < leftColC ; lc++)
								newRow[lc+2] = leftRow[lc];
							matchRTM.addFillRow(newRow);
							
						} else { // Key matched but all cols did not match
							int leftColC = leftRow.length;
							Object[] newRow = new Object[leftColC+1];
							newRow[0] = i; 
							for (int lc =0; lc  < leftColC ; lc++)
								newRow[lc+1] = leftRow[lc];
							matchFailedRTM.addFillRow(newRow);
							
							newRow[0] = j;  // Index
							for (int lc =0; lc  < leftColC ; lc++)
								newRow[lc+1] = rightRow[lc];
							matchFailedRTM.addFillRow(newRow);
							
							matchFailedRTM.addRow(); // a new row after i , j
							
						}
					} // Key Matching
				} //end of isMatch
				
			} // end of right iteration
		} // matched records have been filled by now
		
		fillNoMatchedRow();
		
		return true;

	}
	
	private void fillNoMatchedRow() {
		int leftRowC = leftRTM.getModel().getRowCount();
		int rightRowC = rightRTM.getModel().getRowCount();
		
		// Left (Primary)  is value
		for (int i=0 ; i < leftRowC; i++  ){
			if (matchedIndex.containsValue(i))
				continue;
			int leftColC = leftRTM.getModel().getColumnCount();
			Object[] leftRow = leftRTM.getRow(i);
			Object[] newRow = new Object[leftColC+1];
			newRow[0] = i; // Index
			for (int lc =0; lc  < leftColC ; lc++)
				newRow[lc+1] = leftRow[lc];
			leftNoMatchRTM.addFillRow(newRow);
		}
		
		// Right (Secondary) is key
		for (int i=0 ; i < rightRowC; i++  ){
			if (matchedIndex.containsKey(i))
				continue;
			int rightColC = rightRTM.getModel().getColumnCount();
			Object[] rightRow = rightRTM.getRow(i);
			Object[] newRow = new Object[rightColC+1];
			newRow[0] = i; // Index
			for (int lc =0; lc  < rightColC ; lc++)
				newRow[lc+1] = rightRow[lc];
			rightNoMatchRTM.addFillRow(newRow);
		}
	}
	// This function should be called after compare
	public ReportTableModel getMatchedRTM() {
		return matchRTM;
	}
	// This function should be called after compare
	public ReportTableModel leftNoMatchRTM() {
		return leftNoMatchRTM;
	}
	// This function should be called after compare
	public ReportTableModel rightNoMatchRTM() {
		return rightNoMatchRTM;
	}
	// This function should be called after compare
	public ReportTableModel getMatchFailedRTM() {
			return matchFailedRTM;
	}
	
	/* Utility functions */
	
	public static boolean matchAllColumn(Object[] leftRow,  Object[] rightRow , boolean asString) {
		int leftColC = leftRow.length;
		int rightColC = rightRow.length;
		
		int rightC = (leftColC >= rightColC ) ? rightColC : leftColC; // common data set
			for (int i=0; i < rightC; i++ ) {
				if ((asString == false) && (leftRow[i].equals( rightRow[i]) == false))
					return false;
				if (leftRow[i] == null && rightRow[i] == null) // can't compare Null
					continue;
				if (leftRow[i] == null && rightRow[i] != null) // can't compare Null
					return false;
				if (leftRow[i] != null && rightRow[i] == null) // can't compare Null
					return false;
				if ((asString == true) && (leftRow[i].toString().equalsIgnoreCase(rightRow[i].toString()) == false))
					return false;
			}
		return true;
	}
	
	public static boolean matchIndexColumn(Object[] leftRow, Vector<Integer> leftIndex, Object[] rightRow ,
			Vector<Integer> rightIndex,boolean asString) {
		
		int rightC = leftIndex.size();
		
			for (int i=0; i < rightC; i++ ) {
				
				if ((asString == false) && (leftRow[leftIndex.get(i)].equals(rightRow[rightIndex.get(i)]) == false))
					return false;
				if (leftRow[leftIndex.get(i)] == null && rightRow[rightIndex.get(i)] == null) // can't compare Null
					continue;
				if (leftRow[leftIndex.get(i)] == null && rightRow[rightIndex.get(i)] != null) // one null one not null
					return false;
				if (leftRow[leftIndex.get(i)] !=  null && rightRow[rightIndex.get(i)] == null) // one null one not null
					return false;
				if ((asString == true) && (leftRow[leftIndex.get(i)].toString().equalsIgnoreCase(rightRow[rightIndex.get(i)].toString())
						== false))
					return false;
			}
		return true;
	}
		
	/* To get cell level insight into different cell values
	 * we have to store all different cell values and track till
	 * end even if they fail early
	 */
	
	public static Vector<Integer > diffAllColumn(Object[] leftRow,  Object[] rightRow , boolean asString) {
		int leftColC = leftRow.length;
		int rightColC = rightRow.length;
		Vector<Integer>vc = new Vector<Integer> ();
		boolean markedfail = false; // marker for match or no match
		
		// Now it has loop thru all columns to find not matched columns
		int rightC = (leftColC >= rightColC ) ? rightColC : leftColC; // common data set
			for (int i=0; i < rightC; i++ ) {
				if ((asString == false) && (leftRow[i].equals( rightRow[i]) == false)) {
					markedfail = true;
					vc.add(i);
					continue;
					// return false;
				}
				if (leftRow[i] == null && rightRow[i] == null)  { // can't compare Null
					continue;
				}
				if (leftRow[i] == null && rightRow[i] != null) { // can't compare Null
					markedfail = true;
					vc.add(i);
					continue;
					// return false;
				}
				if (leftRow[i] != null && rightRow[i] == null) { // can't compare Null
					markedfail = true;
					vc.add(i);
					continue;
					// return false;
				}
				if ((asString == true) && (leftRow[i].toString().equalsIgnoreCase(rightRow[i].toString()) == false)) {
					markedfail = true;
					vc.add(i);
					continue;
					// return false;
				}
			}
			if (markedfail == true) //markedfail retained for future use of filtering
				return vc;
			else
				return vc;
	}
	
	public static Vector<Integer > diffIndexColumn(Object[] leftRow, Vector<Integer> leftIndex, Object[] rightRow ,
			Vector<Integer> rightIndex,boolean asString) {
		
		Vector<Integer > vc = new Vector<Integer> ();
		boolean markedfail = false; // marker for match or no match
		
		int rightC = leftIndex.size();
		
			for (int i=0; i < rightC; i++ ) {
				
				if ((asString == false) && (leftRow[leftIndex.get(i)].equals(rightRow[rightIndex.get(i)]) == false)) {
					markedfail = true;
					vc.add(leftIndex.get(i));
					continue;
				}
				if (leftRow[leftIndex.get(i)] == null && rightRow[rightIndex.get(i)] == null) // can't compare Null
					continue;
				if (leftRow[leftIndex.get(i)] == null && rightRow[rightIndex.get(i)] != null) { // one null one not null
					markedfail = true;
					vc.add(leftIndex.get(i));
					continue;
				}
				if (leftRow[leftIndex.get(i)] !=  null && rightRow[rightIndex.get(i)] == null) { // one null one not null
					markedfail = true;
					vc.add(leftIndex.get(i));
					continue;
				}
				if ((asString == true) && (leftRow[leftIndex.get(i)].toString().equalsIgnoreCase(rightRow[rightIndex.get(i)].toString())
						== false)) {
					markedfail = true;
					vc.add(leftIndex.get(i));
					continue;
				}
			}
			if (markedfail == true) //markedfail retained for future use of filtering
				return vc;
			else
				return vc;
	}
	
	/*
	* Comparator for comparing diff cells from RTM
	* as string 
	*/	
	public HashMap<Integer, Vector<Integer>> compareDiff(boolean asString)
	{
		HashMap<Integer, Vector<Integer>>  diffIndex = new HashMap<Integer,Vector<Integer>>();
		matchedDiffIndex = new HashMap<Integer,Integer>();
		// return true is comparison is successful
		if (leftRTM == null || rightRTM == null) {
			System.out.println("Can not Compare Null Table(s)");
			return diffIndex;
		}
		if ( (leftIndex != null && rightIndex == null) || (leftIndex == null && rightIndex != null) )
				allColMatch = true; //Default Behavior
				
		if (leftIndex != null && rightIndex != null) 
			if (leftIndex.size() != rightIndex.size() ) {
				System.out.println("Left and Right Columns are not Mapped");
				return diffIndex;
			}
		
		int leftRowC = leftRTM.getModel().getRowCount();
		int rightRowC = rightRTM.getModel().getRowCount();
		
		for (int i=0 ; i < leftRowC; i++) {
			Object[] leftRow = leftRTM.getRow(i);
			if (leftRow == null ) continue;
			
			Vector<Integer> prev_vc = new Vector<Integer>(); // to hold previous values
			
			for (int j=0 ; j < rightRowC; j++) { // Iterate thru right RTM
				Object[] rightRow = rightRTM.getRow(j);
				if (rightRow == null ) continue;
				
				Vector<Integer> vc = new Vector<Integer>();
				
				if ( allColMatch == true ) {
					vc =diffAllColumn(leftRow, rightRow, asString);
				}
				else {
					vc = diffIndexColumn(leftRow,leftIndex, rightRow,rightIndex, asString);
				}
				
				if (vc != null && vc.size() > 0) {
					
					// Check with prev value and if size is less than then current value is taken
					if ( prev_vc.size() == 0  || prev_vc.size() > vc.size()) {
						prev_vc = vc;
						diffIndex.put(i, prev_vc);
						matchedDiffIndex.put(i, j);
					}
				}
			} // end of right iteration
		} 
		return diffIndex;
	}
	
	public HashMap<Integer,Integer> getDiffMatchedIndex() {
		return matchedDiffIndex;
		
	}
	
	public int getDuplicateCount() {
		return duplicateC;
	}
	
	public Map<Integer, ArrayList<Integer>> reverseMap () {
		
		Map<Integer, ArrayList<Integer>> reverseMap = new HashMap<>();;
	
		for (Map.Entry<Integer,Integer> entry : matchedIndex.entrySet()) {
		    if (!reverseMap.containsKey(entry.getValue())) {
		        reverseMap.put(entry.getValue(), new ArrayList<>());
		    }
		    ArrayList<Integer> keys = reverseMap.get(entry.getValue());
		    keys.add(entry.getKey());
		    reverseMap.put(entry.getValue(), keys);
		}
		
		return reverseMap;
	}
	
} // end of class RTMDiffUtil
