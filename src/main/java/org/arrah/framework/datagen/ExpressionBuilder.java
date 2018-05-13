package org.arrah.framework.datagen;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for creating Expression   
 * Builder on the Table columns.
 * 
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.arrah.framework.ndtable.ReportTableModel;

public class ExpressionBuilder {
	final static private String START_TOKEN = "#{";
	final static private String END_TOKEN = "}";

	public ExpressionBuilder() {
	};

	/* This function takes the expression string, parses it and substitute variables
	 * 
	 */
	public static String preparseJeval(String expression, ReportTableModel rpt,
								int selIndex, int beginIndex, int endIndex) {
		String jevalString = null;
		int startI = 0;
		int endI = 0;
		Hashtable<String, Integer> colTable = new Hashtable<String, Integer>();
		Hashtable<String, Vector<Double>> aggrColVal = new Hashtable<String, Vector<Double>>();
		boolean isNumber = false,isCondition = false;
		String condition = null, thenexpression= null;

		Evaluator eva = new Evaluator('"', true, true, true, true);
		while (true) {
			startI = expression.indexOf(START_TOKEN, endI);
			if (startI == -1)
				break;
			endI = expression.indexOf(END_TOKEN, startI);
			if (endI == -1)
				break;
			String colName = expression.substring(
					startI + START_TOKEN.length(), endI);
			int i = ReportTableModel.getColumnIndex(rpt, colName);
			
			// If there is aggregate strings like SUM_, CUMAVG_ it 
			// will not match. Before returning null we need to check
			// that also.
			
			if (i < 0) {
				int j = findColumn( rpt,  colName, aggrColVal);
			if (j < 0) {
				System.out.println("\n ERROR:Column Name Not Found in Table:"
						+ colName);
				return null;
			} }
			colTable.put(colName, i);  // i negative for aggr cum values
			
			try {
			// negative values for aggregate and cumulative values
			if (i >= 0 ) {
				Object obj = rpt.getModel().getValueAt(0, i); // get value from 1st row
				if (obj == null) {
					eva.putVariable(colName, "");
				} else
					eva.putVariable(colName, obj.toString());
			} else { // aggregate cumulative values
				Vector<Double> val = aggrColVal.get(colName);
				if (val != null)
					eva.putVariable(colName,val.get(0).toString()); // first member
			}
			} catch (Exception e) {
				System.out.println("\n Exception :"+e);
				return null;
			}
		} // End of while loop

		try {
			
			/* We need to parse IF(condition) THEN (expression) 
			 * loop. If IF ( true ) then only expressing should be 
			 * parsed and returned jevalString
			 */
			expression = expression.trim(); // remove leading trailing whitespaces
			if (expression.startsWith("IF") == true ) {
				int thenIndex = expression.indexOf(" THEN ",2); // it has to come after if
				if (thenIndex < 0) { // IF must have THEN clause
					System.out.println("Format error  IF (condition) THEN expression");
					return null;
				}
				// First check IF condition is OK
				condition = expression.substring(2, thenIndex);
				thenexpression = expression.substring(thenIndex+" THEN ".length()); // take the expression
				eva.parse(condition);
				String condOutput = eva.evaluate(false, false);
				isCondition = true;

				if (condOutput.startsWith("0") == false ) {// condition true
					eva.parse(thenexpression);
					 jevalString = eva.evaluate(false, false);
					
				} else
					jevalString = "IF condition not met";
				
			} else { // No IF condition
				eva.parse(expression);
				jevalString = eva.evaluate(false, false);
			}
			if (selIndex < 0)
				return jevalString;
		} catch (EvaluationException ee) {
			System.out.println("\n WARNING: Parsing Falied " + ee.getMessage());
			return jevalString;
		}

		if (rpt.getModel().getColumnClass(selIndex).getName().toUpperCase()
				.contains("DOUBLE"))
			isNumber = true;
		
		Hashtable<String, String> varT = new Hashtable<String, String>();
		
		for (int i = (beginIndex -1)  ; i < (endIndex -1) ; i++) { // get Index
			Enumeration<String> table_enum = colTable.keys();
			
			// Setting variables
			while (table_enum.hasMoreElements()) {
				String colKey = table_enum.nextElement();
				int j = colTable.get(colKey);
				if (j >= 0) {
					Object obj = rpt.getModel().getValueAt(i, j);
					if (obj == null) {
						if (obj instanceof Number)
							varT.put(colKey, "0");
						else
							varT.put(colKey, "");
					} else
						varT.put(colKey, obj.toString());
				} else {
					Vector<Double> val = aggrColVal.get(colKey);
					if (val == null) 
						varT.put(colKey, "0"); // Number 
					else if (val.size() == 1 )
						varT.put(colKey,val.get(0).toString()); // For SUM, AVG, MIN, MAX
					else 
						varT.put(colKey,val.get(i).toString()); // CUMSUM, CUMAVG, PREV, NEXT
				}
			} // end while loop
			eva.setVariables(varT);

			// now evaluate
			try {
				if (isCondition == true ) {
				eva.parse(condition);
				String condOutput = eva.evaluate(false, false);
				if (condOutput.startsWith("0") == false ) {// condition true
					eva.parse(thenexpression);
					jevalString = eva.evaluate(false, false);
				} else continue; // does not match IF condition
				
				} else 
				jevalString = eva.evaluate(false, false);
			} catch (EvaluationException ee) {
				System.out.println("\n Parse WARNING:  Row id: " + (i + 1)
						+ " :" + ee.getMessage());
				rpt.getModel().setValueAt(null, i, selIndex);
				continue;
			}
			if (isNumber)
				try {
					rpt.getModel().setValueAt(Double.parseDouble(jevalString),
							i, selIndex);
				} catch (NumberFormatException exp) {
					System.out.println("\n Format WARNING:  Row id: " + (i + 1)
							+ " :" + exp.getMessage());
					rpt.getModel().setValueAt(null, i, selIndex);

				}
			else
				rpt.getModel().setValueAt(jevalString, i, selIndex);

		} // end of for loop
		return jevalString;
	}
	
	/* This function will check if colname has any aggregate string tagged into it
	 * This function will return array - first member will be columnIndex
	 * second member will be cum / aggr type
	 */

	private static int findColumn(ReportTableModel rpt, String colName,
									Hashtable<String, Vector<Double>> aggrColVal) {
		
		if (colName == null || "".equals(colName)) 
			return -1;
		Vector<Double> colData = new Vector<Double>();
		
		if (colName.startsWith("SUM_") == true ) { // 1 for SUM
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("SUM_".length()));
			if (i < 0) return i; 
			else {
				//colData = AggrCumRTM.getColumnNumberData(rpt,i);
				colData = rpt.getColDataVD(i);
				Double sum = AggrCumColumnUtil.getSum(colData);
				Vector<Double> sum_v = new Vector<Double>();
				sum_v.add(sum);
				aggrColVal.put(colName,sum_v);
				return 1;
			}
		}
		if (colName.startsWith("AVG_") == true ) { // 2 for AVG
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("AVG_".length()));
			if (i < 0) return i; 
			else {
				colData = rpt.getColDataVD(i);
				Double avg = AggrCumColumnUtil.getAverage(colData);
				Vector<Double> avg_v = new Vector<Double>();
				avg_v.add(avg);
				aggrColVal.put(colName,avg_v);
				return 2;
			}
		}
		if (colName.startsWith("MIN_") == true ) { // 3 for MIN
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("MIN_".length()));
			if (i < 0) return i; 
			else { 
				colData = rpt.getColDataVD(i);
				double[] minmax = AggrCumColumnUtil.getMinMax(colData);
				Vector<Double> min_v = new Vector<Double>();
				min_v.add(minmax[0]);
				aggrColVal.put(colName,min_v);
				return 3;
			
			}
		}
		if (colName.startsWith("MAX_") == true ) { // 4 for MIN
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("MAX_".length()));
			if (i < 0) return i; 
			else {
				colData = rpt.getColDataVD(i);
				double[] minmax = AggrCumColumnUtil.getMinMax(colData);
				Vector<Double> max_v = new Vector<Double>();
				max_v.add(minmax[1]);
				aggrColVal.put(colName,max_v);
				return 4;
			}
		}
		if (colName.startsWith("CUMSUM_") == true ) { // 6 for CUMSUM
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("CUMSUM_".length()));
			if (i < 0) return i; 
			else { 
				colData = rpt.getColDataVD(i);
				Vector<Double> cumsum = AggrCumColumnUtil.getCumSum(colData);
				aggrColVal.put(colName,cumsum);
				return 6;
			}
		}
		if (colName.startsWith("CUMAVG_") == true ) { // 7 for CUMAVG
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("CUMAVG_".length()));
			if (i < 0) return i;
			else {
				colData = rpt.getColDataVD(i);
				Vector<Double> cumavg = AggrCumColumnUtil.getCumAvg(colData);
				aggrColVal.put(colName,cumavg);
				return 7;
			}
		}
		if (colName.startsWith("PREV_") == true ) { // 8 for PREV
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("PREV_".length()));
			if (i < 0) return i;
			else {
				colData = rpt.getColDataVD(i);
				Vector<Double> prevavg = AggrCumColumnUtil.putPrevVal(colData);
				aggrColVal.put(colName,prevavg);
				return 8;
			}
		}
		if (colName.startsWith("NEXT_") == true ) { // 9 for NEXT
			int i = ReportTableModel.getColumnIndex(rpt, colName.substring("NEXT_".length()));
			if (i < 0) return i;
			else {
				colData = rpt.getColDataVD(i);
				Vector<Double> nextavg = AggrCumColumnUtil.putNextVal(colData);
				aggrColVal.put(colName,nextavg);
				return 9;
			}
		}
		
		return -1;
	}

} // End of Expression Builder
