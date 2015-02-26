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
import java.util.Arrays;
import java.util.Calendar;
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
	
	// This util function will return sorted RTM
	public static ReportTableModel sortRTM(ReportTableModel _rtm, final boolean asc) {
		
		
		 final class Row implements Comparable<Row> {
			 Object[] _row;
			 
			 public Row(Object[] row) {
				 _row = row;
			 }
			 
			 private Object[] getRow() {
				 return _row;
			 }
			 // CompareTo function Natural Ordering
			public int compareTo(Row r1) {
				Object[] row1 = this.getRow();
				Object[] row2 = r1.getRow();
				int comparison = 0;
				
				for (int i=0; i < row1.length; i++) {
					
					Object o1 = row1[i];
					Object o2 = row2[i];
					
					// Define null less than everything, except null.
					if (o1 == null && o2 == null) {
						continue;
					} else if (o1 == null) {
						comparison = -1; break;
					} else if (o2 == null) {
						comparison = 1; break;
					}
					// Now see the values
					
					if (o2 instanceof Number){
						if(  ((Number)o2).doubleValue() > ((Number)o1).doubleValue() ) {
							comparison = -1; break;
							
						}
						if(  ((Number)o2).doubleValue() < ((Number)o1).doubleValue() ) {
							comparison = 1; break;
							
						}
					} // Number
					else if (o2 instanceof java.util.Date){
						Calendar c1 = Calendar.getInstance();
						Calendar c2 = Calendar.getInstance();
						c1.setTime((Date)o1); c2.setTime((Date)o2);
						int cmp = c1.compareTo(c2);
						if( cmp != 0) {
							comparison = cmp; break;
						}
						
					} // Date
					else {
						String s1 = o1.toString();
						String s2 = o2.toString();
						int cmp = s1.compareTo(s2);
						if( cmp != 0) {
							comparison = cmp; break;
						}
					} // treat as String
					
				}
				if (comparison != 0) {
					return asc == false ? -comparison: comparison;
				}
				return comparison;
			} } // End of Row Class
	
	 int rowC = _rtm.getModel().getRowCount();
	 Row[] rows = new Row[rowC];
	 for (int i=0 ; i < rowC; i++) {
		 Object[] row = _rtm.getRow(i);
		 rows[i] = new Row(row);
	 }
	 Arrays.sort(rows); 

	 // Now Create new RreportTableModel and return it
	 
	 Object[] colName = _rtm.getAllColName();
	 
	 ReportTableModel newRTM = new ReportTableModel(colName, _rtm.isRTEditable(), true);
	 for (int i =0; i < rows.length; i++)
		 newRTM.addFillRow(rows[i].getRow());
	 
	 	return newRTM;
	}
	
	
} // End of Class RTMUtil
