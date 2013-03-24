package org.arrah.framework.ndtable;

/***********************************************
 *     Copyright to Vivek Kumar Singh	       *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This is a util for Report Table Model class
 * it will define join, sort or match condition
 * for RTM class
 *
 */

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.arrah.framework.rdbms.DataDictionaryPDF;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class RTMUtil {
	/*
	 * It will join two tables based on two column Indexes
	 */
	public static ReportTableModel joinTables(ReportTableModel leftT,
			int indexL, ReportTableModel rightT, int indexR, int joinType) {

		Vector<Object> lvc = new Vector<Object>();
		Vector<Object> rvc = new Vector<Object>();
		int lrow_c = leftT.getModel().getRowCount();
		int rrow_c = rightT.getModel().getRowCount();
		for (int i = 0; i < lrow_c; i++) {
			lvc.addElement(leftT.getModel().getValueAt(i, indexL));
		}
		for (int i = 0; i < rrow_c; i++) {
			rvc.addElement(rightT.getModel().getValueAt(i, indexR));
		}
		int rcolc = rightT.getModel().getColumnCount();
		int lcolc = leftT.getModel().getColumnCount();
		for (int i = 0; (i < rcolc); i++) {
			if (i == indexR)
				continue;
			leftT.addColumn(rightT.getModel().getColumnName(i));
		}
		switch (joinType) {
		case 0: // Left Outer Join with Cardinality 1:1 is default
			for (int i = 0; i < lrow_c; i++) {
				int i_find = rvc.indexOf(lvc.get(i));
				if (i_find != -1) {
					int curC = lcolc;
					for (int j = 0; (j < rcolc); j++) {
						if (j == indexR)
							continue;
						leftT.getModel().setValueAt(
								rightT.getModel().getValueAt(i_find, j), i,
								curC++);
					}
				}
			}
			break;
		}
		return leftT;
	}

	/*
	 * It will look for the conditions in table based on column Index. Int will
	 * tell what types of condition (<, >, = , Like% etc) it is looking for.
	 * condV will have the value of the condition if any.
	 */
	public static Vector<Integer> matchCondition(ReportTableModel _rt,
			int colI, int cond, String condV) {
		if (_rt == null) {
			System.out.println("\n ERROR:Table not Set for Filtering");
			return null;
		}
		int rowC = _rt.getModel().getRowCount();
		if (colI < 0)
			return null; // Column not found
		if (cond < 2)
			return null; // No condition is chosen
		Vector<Integer> result_v = new Vector<Integer>();

		for (int i = 0; i < rowC; i++) {
			switch (cond) {
			case 2:
				Object obj = _rt.getModel().getValueAt(i, colI);
				if (obj == null)
					result_v.add(i);
				break;
			case 3:
				obj = _rt.getModel().getValueAt(i, colI);
				if (obj != null)
					result_v.add(i);
				break;
			case 4:
			case 5:
				boolean found = false;
				obj = _rt.getModel().getValueAt(i, colI);
				if (obj == null || condV == null || "".equals(condV))
					break;

				if ((condV.startsWith("\'")) && condV.endsWith("\'")) {
					if ((condV.length() - 1) > 0)
						condV = condV.substring(1, condV.length() - 1);

				}
				if ((condV.startsWith("%")) && condV.endsWith("%")) {
					if (condV.length() == 1) // show all if only %
						found = true;
					else {
						String condV_s = condV.substring(1, condV.length() - 1);
						found = obj.toString().contains(condV_s);
					}
				} else if (condV.startsWith("%")) {
					String condV_s = condV.substring(1, condV.length());
					found = obj.toString().endsWith(condV_s);
				} else if (condV.endsWith("%")) {
					String condV_s = condV.substring(0, condV.length() - 1);
					found = obj.toString().startsWith(condV_s);
				} else
					found = obj.toString().contains(condV);

				if (cond == 4 && found == true)
					result_v.add(i);
				if (cond == 5 && found == false)
					result_v.add(i);
				break;
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				found = false;
				obj = _rt.getModel().getValueAt(i, colI);
				if (obj == null || condV == null || "".equals(condV))
					break;

				if ((condV.startsWith("\'")) && condV.endsWith("\'")) {
					if ((condV.length() - 1) > 0)
						condV = condV.substring(1, condV.length() - 1);

				}
				if (obj instanceof Date) {
					SimpleDateFormat simpledateformat = new SimpleDateFormat(
							"dd/MM/yyyy hh:mm:ss");
					simpledateformat.setLenient(true);
					Date date = simpledateformat.parse(condV,
							new ParsePosition(0));
					if (date == null) {
						System.out.println("\n ERROR:Could not Parse " + condV
								+ " for Date object");
						System.out
								.println("\n Date Format is dd/MM/yyyy hh:mm:ss");
						return null;
					}
					if (cond == 6)
						found = (date.compareTo((Date) obj) == 0) ? true
								: false;
					else if (cond == 7)
						found = (date.compareTo((Date) obj) != 0) ? true
								: false;
					else if (cond == 8)
						found = (date.compareTo((Date) obj) > 0) ? true : false;
					else if (cond == 9)
						found = (date.compareTo((Date) obj) >= 0) ? true
								: false;
					else if (cond == 10)
						found = (date.compareTo((Date) obj) < 0) ? true : false;
					else if (cond == 10)
						found = (date.compareTo((Date) obj) <= 0) ? true
								: false;

				} else if (obj instanceof Number) {
					try {
						Double num = Double.parseDouble(condV);
						if (cond == 6)
							found = (((Double) obj).doubleValue() == num
									.doubleValue()) ? true : false;
						else if (cond == 7)
							found = (((Double) obj).doubleValue() != num
									.doubleValue()) ? true : false;
						else if (cond == 8)
							found = (((Double) obj).doubleValue() < num
									.doubleValue()) ? true : false;
						else if (cond == 9)
							found = (((Double) obj).doubleValue() <= num
									.doubleValue()) ? true : false;
						else if (cond == 10)
							found = (((Double) obj).doubleValue() > num
									.doubleValue()) ? true : false;
						else if (cond == 10)
							found = (((Double) obj).doubleValue() >= num
									.doubleValue()) ? true : false;
					} catch (NumberFormatException nexp) {
						System.out.println("\n ERROR:Could not Parse " + condV
								+ " for Number object");
						return null;
					}

				} else {
					if (cond == 6)
						found = (condV.compareTo(obj.toString()) == 0) ? true
								: false;
					else if (cond == 7)
						found = (condV.compareTo(obj.toString()) != 0) ? true
								: false;
					else if (cond == 8)
						found = (condV.compareTo(obj.toString()) > 0) ? true
								: false;
					else if (cond == 9)
						found = (condV.compareTo(obj.toString()) >= 0) ? true
								: false;
					else if (cond == 10)
						found = (condV.compareTo(obj.toString()) < 0) ? true
								: false;
					else if (cond == 10)
						found = (condV.compareTo(obj.toString()) <= 0) ? true
								: false;
				}
				if (found == true)
					result_v.add(i);
				break;

			default:
				break;
			}
		}
		return result_v;
	}

	public static Vector<Integer> mergeSet(Vector<Integer> leftSet,
			Vector<Integer> rightSet, String mergeType) {
		if (leftSet == null || rightSet == null)
			return null;

		if (mergeType.trim().compareToIgnoreCase("or") == 0) { // OR set
			Vector<Integer> orSet = new Vector<Integer>();
			orSet = leftSet;
			for (int i = 0; i < rightSet.size(); i++) {
				if (orSet.contains(rightSet.get(i)) == false)
					orSet.add(rightSet.get(i));
			}
			return orSet;
		} else if (mergeType.trim().compareToIgnoreCase("and") == 0) { // AND
																		// set
			Vector<Integer> andSet = new Vector<Integer>();
			if (leftSet.size() > rightSet.size()) {
				for (int i = 0; i < rightSet.size(); i++) {
					if (leftSet.contains(rightSet.get(i)) == true)
						andSet.add(rightSet.get(i));
				}
			} else {
				for (int i = 0; i < leftSet.size(); i++) {
					if (rightSet.contains(leftSet.get(i)) == true)
						andSet.add(leftSet.get(i));
				}
			}
			return andSet;
		} else if (mergeType.trim().compareToIgnoreCase("xor") == 0) { // XoR
																		// set
			// Left is Universal Set and right Set is getting Exclusive XoR
			Vector<Integer> xorSet = new Vector<Integer>();
			for (int i = 0; i < leftSet.size(); i++) {
				if (rightSet.contains(leftSet.get(i)) == false)
					xorSet.add(leftSet.get(i));
			}
			return xorSet;

		}
		return leftSet;
	}
	
	public static PdfPTable createPDFTable (ReportTableModel rtm) {
		if (rtm == null ) return null;
		int colC = rtm.getModel().getColumnCount();
		PdfPTable pdfTable = new PdfPTable (colC);
		for (int i=0; i <colC; i++ ) {
			PdfPCell c1 = new PdfPCell(new Phrase(rtm.getModel().getColumnName(i),
					DataDictionaryPDF.getFont(10, Font.BOLD)));
			c1.setBackgroundColor(BaseColor.GRAY);
		    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		    pdfTable.addCell(c1);
		}
		pdfTable.setHeaderRows(1); // First row is header

		int rowC =  rtm.getModel().getRowCount();
		for (int i=0; i < rowC; i++) {
			for (int j=0; j < colC; j++) {
				PdfPCell c1 = new PdfPCell();
				String valS="";
				
				if (rtm.getModel().getValueAt(i,j) != null)
					valS= rtm.getModel().getValueAt(i,j).toString();
				
				if(i % 2 == 0 ) c1.setBackgroundColor(new BaseColor(150, 255, 150, 255));
				c1.setPhrase(new Phrase(valS, DataDictionaryPDF.getFont(9, Font.NORMAL)));
				pdfTable.addCell(c1);
			}
		}
		return pdfTable;
	}
}
