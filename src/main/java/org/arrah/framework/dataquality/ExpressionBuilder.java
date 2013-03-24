package org.arrah.framework.dataquality;

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

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.arrah.framework.ndtable.ReportTableModel;

public class ExpressionBuilder {
	final static private String START_TOKEN = "#{";
	final static private String END_TOKEN = "}";

	public ExpressionBuilder() {
	};

	public static String preparseJeval(String expression, ReportTableModel rpt,
			int selIndex) {
		String jevalString = null;
		int startI = 0;
		int endI = 0;
		Hashtable<String, Integer> colTable = new Hashtable<String, Integer>();
		boolean isNumber = false;

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
			int i = getColumnIndex(rpt, colName);
			if (i < 0) {
				System.out.println("\n ERROR:Column Name Not Found in Table:"
						+ colName);
				return null;
			}
			colTable.put(colName, i);
			Object obj = rpt.getModel().getValueAt(0, i);
			if (obj == null) {
				eva.putVariable(colName, "");
			} else
				eva.putVariable(colName, obj.toString());
		}

		try {
			eva.parse(expression);
			if (selIndex < 0)
				return jevalString = eva.evaluate(false, false);
		} catch (EvaluationException ee) {
			System.out.println("\n WARNING: Parsing Falied " + ee.getMessage());
			return jevalString;
		}

		if (rpt.getModel().getColumnClass(selIndex).getName().toUpperCase()
				.contains("DOUBLE"))
			isNumber = true;
		int row_c = rpt.getModel().getRowCount();
		Hashtable<String, String> varT = new Hashtable<String, String>();
		for (int i = 0; i < row_c; i++) {
			Enumeration<String> table_enum = colTable.keys();
			while (table_enum.hasMoreElements()) {
				String colKey = table_enum.nextElement();
				Object obj = rpt.getModel().getValueAt(i, colTable.get(colKey));
				if (obj == null) {
					if (obj instanceof Number)
						varT.put(colKey, "0");
					else
						varT.put(colKey, "");
				} else
					varT.put(colKey, obj.toString());
			}
			eva.setVariables(varT);

			try {
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

		}
		return jevalString;
	}

	public static int getColumnIndex(ReportTableModel rpt, String colName) {
		int row_c = rpt.getModel().getColumnCount();
		for (int i = 0; i < row_c; i++) {
			if (colName.equals(rpt.getModel().getColumnName(i)))
				return i;
		}
		return -1;
	}
} // End of Expression Builder
