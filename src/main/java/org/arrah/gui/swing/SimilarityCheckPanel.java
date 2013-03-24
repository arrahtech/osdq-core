package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2007      * 
 *     http://www.arrah.in                     * 
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          * 
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* 
 * This file is used to Similarity Check
 * for Lucene Index directory or RAM
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.arrah.framework.dataquality.SimilarityCheckLucene;
import org.arrah.framework.rdbms.JDBCRowset;
import org.arrah.framework.rdbms.QueryBuilder;
import org.arrah.framework.rdbms.Rdbms_conn;

public class SimilarityCheckPanel implements ActionListener, TableModelListener {
	private ReportTable _rt, outputRT;
	private String[] colName;
	private String[] colType;
	private int rowC;
	private Vector<Integer> skipVC, markDel;
	private JDialog d_m;
	private JFrame dg;
	private JCheckBox chk;
	private JComboBox[] sType, sImp;
	private JTextField[] skiptf;
	private boolean isChanged = false;
	private Hashtable<Integer, Integer> parentMap;
	boolean isRowSet = false;
	private JDBCRowset _rows;
	private SimilarityCheckLucene _simcheck;

	// For ReportTable Input
	public SimilarityCheckPanel(ReportTable rt) {
		_rt = rt;
		_simcheck = new SimilarityCheckLucene(_rt.getRTMModel());
		colName = getColName();
		rowC = _rt.table.getRowCount();
		_rt.getModel().addTableModelListener(this);
		mapDialog();
	}

	// For Rowset Table Input
	public SimilarityCheckPanel(JDBCRowset rowSet) {
		isRowSet = true;
		_rows = rowSet;
		_simcheck = new SimilarityCheckLucene(_rows);
		colName = _rows.getColName();
		colType = _rows.getColType();
		rowC = _rows.getRowCount();
		mapDialog();
	}
	// For independent table
	public SimilarityCheckPanel(String searchString, String tabName, Vector<String> colV) {
		
		_rt = createRT(tabName,colV);
		_simcheck = new SimilarityCheckLucene(_rt.getRTMModel());
		colName = getColName();
		rowC = _rt.table.getRowCount();
		_rt.getModel().addTableModelListener(this);
		
		_simcheck.makeIndex();
		searchTableIndex(searchString);
		
 
	}

	private String[] getColName() {
		int colC = _rt.table.getColumnCount();
		colName = new String[colC];
		colType = new String[colC];

		for (int i = 0; i < colC; i++) {
			colName[i] = _rt.table.getColumnName(i);
			colType[i] = _rt.table.getColumnClass(i).getName();
		}
		return colName;
	}

	private void searchTableIndex() {
		if (_simcheck.openIndex() == false)
			return;
		// Add a boolean column for delete
		String[] newColN = new String[colName.length + 1];
		newColN[0] = "Delete Editable"; // This column you can change
		for (int i = 0; i < colName.length; i++)
			newColN[i + 1] = colName[i];

		outputRT = new ReportTable(newColN, false, true);
		skipVC = new Vector<Integer>();
		parentMap = new Hashtable<Integer, Integer>();

		// Iterate over row
		for (int i = 0; i < rowC; i++) {
			if (isRowSet == false && skipVC.contains(i) == true)
				continue;
			if (isRowSet == true && skipVC.contains(i + 1) == true)
				continue;

			String queryString = getQString(i);
			if (queryString == null || queryString.equals("") == true)
				continue;
			Query qry = _simcheck.parseQuery(queryString);
			Hits hit = _simcheck.searchIndex(qry);
			if (hit == null || hit.length() <= 1)
				continue; // It will match self
			// Iterate over the Documents in the Hits object

			for (int j = 0; j < hit.length(); j++) {
				try {
					Document doc = hit.doc(j);
					String rowid = doc.get("at__rowid__");
					parentMap.put(outputRT.table.getRowCount(),
							Integer.parseInt(rowid));
					Object[] row = null;
					if (isRowSet == false)
						row = _rt.getRow(Integer.parseInt(rowid));
					else
						row = _rows.getRow(Integer.parseInt(rowid));
					Object[] newRow = new Object[row.length + 1];
					boolean del = false;
					newRow[0] = del;
					for (int k = 0; k < row.length; k++)
						newRow[k + 1] = row[k];
					outputRT.addFillRow(newRow);
					skipVC.add(Integer.parseInt(rowid));
				} catch (Exception e) {
					ConsoleFrame.addText("\n " + e.getMessage());
					ConsoleFrame.addText("\n Error: Can not open Document");
				}
			}
			outputRT.addNullRow();
		}
		_simcheck.closeSeachIndex();

		JPanel jp_p = new JPanel(new BorderLayout());
		jp_p.add(outputRT, BorderLayout.CENTER);
		jp_p.add(deletePanel(), BorderLayout.PAGE_END);
		// Show the table now
		dg = new JFrame("Similar Records Frame");
		dg.setLocation(250, 100);
		dg.getContentPane().add(jp_p);
		dg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dg.pack();
		QualityListener.bringToFront(dg);
	}

	private JDialog mapDialog() {

		int colC = colName.length;
		sType = new JComboBox[colC];
		sImp = new JComboBox[colC];
		JTextField[] tf1 = new JTextField[colC];
		skiptf = new JTextField[colC];

		JPanel jp = new JPanel();
		SpringLayout layout = new SpringLayout();
		jp.setLayout(layout);

		JLabel l1 = new JLabel("Field");
		l1.setForeground(Color.BLUE);
		JLabel l2 = new JLabel("Search Criterion");
		l2.setForeground(Color.BLUE);
		JLabel l3 = new JLabel("Importance");
		l3.setForeground(Color.BLUE);
		JLabel l4 = new JLabel("Skip Words");
		l4.setForeground(Color.BLUE);
		jp.add(l1);
		jp.add(l2);
		jp.add(l3);
		jp.add(l4);

		for (int i = 0; i < colC; i++) {
			tf1[i] = new JTextField(8);
			tf1[i].setText(colName[i]);
			tf1[i].setEditable(false);
			tf1[i].setToolTipText(colType[i]);
			jp.add(tf1[i]);

			sType[i] = new JComboBox(new String[] { "Don't Use", "Exact",
					"Similar-Any", "Similar-All", "Left Imp.", "Right Imp." });
			jp.add(sType[i]);

			sImp[i] = new JComboBox(new String[] { "Low", "Medium", "High" });
			jp.add(sImp[i]);

			skiptf[i] = new JTextField(8);
			skiptf[i].setText("And,Or,Not"); // Lucene core words
			jp.add(skiptf[i]);

		}
		SpringUtilities.makeCompactGrid(jp, colC + 1, 4, 3, 3, 3, 3); // +1 for
																		// header

		JScrollPane jscrollpane1 = new JScrollPane(jp);
		if (colC * 35 + 50 > 400)
			jscrollpane1.setPreferredSize(new Dimension(575, 400));
		else
			jscrollpane1.setPreferredSize(new Dimension(575, colC * 35 + 50));

		JPanel bp = new JPanel();
		JButton help = new JButton("Help");
		help.setActionCommand("help");
		help.addActionListener(this);
		help.addKeyListener(new KeyBoardListener());
		bp.add(help);
		JButton ok = new JButton("Search");
		ok.setActionCommand("simcheck");
		ok.addActionListener(this);
		ok.addKeyListener(new KeyBoardListener());
		bp.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.setActionCommand("mcancel");
		cancel.addActionListener(this);
		cancel.addKeyListener(new KeyBoardListener());
		bp.add(cancel);

		JPanel jp_p = new JPanel(new BorderLayout());
		jp_p.add(jscrollpane1, BorderLayout.CENTER);
		jp_p.add(bp, BorderLayout.PAGE_END);

		d_m = new JDialog();
		d_m.setTitle("Similarity Map Dialog");
		d_m.setLocation(150, 150);
		d_m.getContentPane().add(jp_p);
		d_m.setModal(true);
		d_m.pack();
		d_m.setVisible(true);

		return d_m;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals("simcheck")) {
			try {
				d_m.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				_simcheck.makeIndex();
				searchTableIndex();
			} finally {
				d_m.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d_m.dispose();
			}
		}
		if (command.equals("mcancel")) {
			if (isRowSet == true && _rows != null)
				_rows.close();
			d_m.dispose();
		}
		if (command.equals("cancel")) {
			if (isRowSet == true && _rows != null)
				_rows.close();
			dg.dispose();
		}
		if (command.equals("help")) {
			JOptionPane.showMessageDialog(null,
					"Open Similarity_check.[doc][pdf] to get more Infomation");
			return;
		}
		if (command.equals("delete")) {
			if (outputRT.isSorting()) {
				JOptionPane.showMessageDialog(null,
						"Table is in Sorting State.");
				return;
			}
			if (isChanged == true && chk.isSelected() == true) {
				JOptionPane
						.showMessageDialog(null,
								"Parent table has changed.\n Redo Similarity Check to get updated value.");
				ConsoleFrame
						.addText("\n Parent table has changed.\n Redo Similarity Check to get updated value.");
				return;
			}
			markDel = new Vector<Integer>();
			int rowC = outputRT.table.getRowCount();
			for (int i = 0; i < rowC; i++)
				if (outputRT.getValueAt(i, 0) != null
						&& ((Boolean) outputRT.getValueAt(i, 0)).booleanValue() == true)
					markDel.add(i);
			int size = markDel.size();
			if (size == 0) {
				JOptionPane.showMessageDialog(null, "Check Rows to Delete");
				return;
			}
			int n = JOptionPane.showConfirmDialog(null,
					"Do you want to delete " + size + " rows?",
					"Confirmation Type", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.NO_OPTION)
				return;

			int[] parentO = new int[size];
			for (int i = 0; i < size; i++) {
				if (chk.isSelected() == true) {
					if (parentMap.get(markDel.get(size - 1 - i)) == null)
						parentO[i] = -1;
					else
						parentO[i] = parentMap.get(markDel.get(size - 1 - i));
				}
				outputRT.removeRows(markDel.get(size - 1 - i), 1);
			}
			dg.repaint();
			if (chk.isSelected() == true) {
				if (isRowSet == false)
					_rt.cancelSorting();
				Arrays.sort(parentO);
				int indexL = parentO.length;
				for (int i = 0; i < indexL; i++) {
					if (parentO[indexL - 1 - i] < 0)
						continue;
					if (indexL - 1 - i > 0) {
						if (parentO[indexL - 1 - i] > parentO[indexL - 1
								- (i + 1)])
							if (isRowSet == false)
								_rt.removeRows(parentO[indexL - 1 - i], 1);
							else
								_rows.deleteRow(parentO[indexL - 1 - i]);
						else
							continue;
					} else {
						if (isRowSet == false)
							_rt.removeRows(parentO[indexL - 1 - i], 1);
						else
							_rows.deleteRow(parentO[indexL - 1 - i]);
					}
				}
				if (isRowSet == false)
					_rt.repaint();
				else if (_rows != null)
					_rows.close();
			}
			// Run delete with/without parent once. After that parent table and
			// mapping will change
			chk.setSelected(false);
			chk.setEnabled(false);
		}
	}

	private String getQString(int rowid) {
		String queryString = "";
		Object[] row = null;
		if (isRowSet == false)
			row = _rt.getRow(rowid);
		else {
			try {
				row = _rows.getRow(rowid + 1);
			} catch (Exception e) {
				ConsoleFrame.addText("\n Row Fetch Error:" + e.getMessage());
				e.printStackTrace();
				// TODO -- catch exception
			}
		}
		if (row == null)
			return "";

		for (int j = 0; j < row.length; j++) {
			int type = sType[j].getSelectedIndex();
			int imp = sImp[j].getSelectedIndex() + 1;
			String multiWordQuery = "";
			String skipText = skiptf[j].getText();
			boolean skip = true;
			String[] skiptoken = null;

			if (skipText != null && skipText.equals("") == false) {
				skip = false;
				skipText = skipText.trim().replaceAll(",", " ");
				skipText = skipText.trim().replaceAll("\\s+", " ");
				skiptoken = skipText.split(" ");
			}

			if (row[j] != null) {
				String term = row[j].toString();
				term.trim();
				switch (type) {
				case 0:
					continue; // do not use
				case 1: // Exact match
					boolean matchF = false;
					if (skip == false) {
						for (int k = 0; k < skiptoken.length; k++)
							if (skiptoken[k].compareToIgnoreCase(term) == 0) {
								matchF = true;
								break;
							}
					}
					if (matchF == true)
						continue;
					break;
				case 2:
				case 3: // It may have multi-words
					term = term.replaceAll(",", " ");
					term = term.replaceAll("\\s+", " ");
					String[] token = term.split(" ");
					String newTerm = "";
					for (int i = 0; i < token.length; i++) {
						if (token[i] == null || "".equals(token[i]))
							continue;
						matchF = false;
						if (skip == false) {
							for (int k = 0; k < skiptoken.length; k++)
								if (skiptoken[k].compareToIgnoreCase(token[i]) == 0) {
									matchF = true;
									break;
								}
						}
						if (matchF == true)
							continue;
						if (newTerm.equals("") == false && type == 3)
							newTerm += " AND ";
						if (newTerm.equals("") == false && type == 2)
							newTerm += " OR ";
						newTerm += colName[j] + ":"
								+ QueryParser.escape(token[i]) + "~^" + imp
								+ " "; // For Fuzzy Logic
					}
					multiWordQuery = newTerm;
					break;
				case 4: // Left Imp first 4 characters
					matchF = false;
					if (skip == false) {
						for (int k = 0; k < skiptoken.length; k++)
							if (skiptoken[k].compareToIgnoreCase(term) == 0) {
								matchF = true;
								break;
							}
					}
					if (matchF == true)
						continue;

					if (term.length() > 4) {
						term = term.substring(0, 4);
					}
					break;
				case 5: // Right Imp last 4 characters
					matchF = false;
					if (skip == false) {
						for (int k = 0; k < skiptoken.length; k++)
							if (skiptoken[k].compareToIgnoreCase(term) == 0) {
								matchF = true;
								break;
							}
					}
					if (matchF == true)
						continue;

					if (term.length() > 4) {
						term = term.substring(term.length() - 4, term.length());
					}
					break;

				default:
					break;

				}
				if (queryString.equals("") == false)
					queryString += " AND ";
				if (type == 2 || type == 3) // Single Word Match
					queryString += multiWordQuery;
				else if (type == 1) // Exact match
					queryString += colName[j] + ":\"" + term + "\"^" + imp;
				else if (type == 4) // Left Imp match
					queryString += colName[j] + ":"
							+ QueryParser.escape(term.trim()) + "*^" + imp;
				else if (type == 5) // Left Imp match
					queryString += colName[j] + ":*"
							+ QueryParser.escape(term.trim()) + "^" + imp;
			} else {
				if (type != 0 && imp > 1)
					return "";
			}
		}
		return queryString;
	}

	private JComponent deletePanel() {
		JPanel delP = new JPanel();
		if (isRowSet == false)
			chk = new JCheckBox("Delete from Parent Table");
		else
			chk = new JCheckBox("Delete from Database");

		JButton delB = new JButton("Delete");
		delB.setActionCommand("delete");
		delB.addActionListener(this);
		delB.addKeyListener(new KeyBoardListener());

		JButton canB = new JButton("Cancel");
		canB.setActionCommand("cancel");
		canB.addActionListener(this);
		canB.addKeyListener(new KeyBoardListener());

		delP.add(chk);
		delP.add(delB);
		delP.add(canB);
		return delP;
	}

	public void tableChanged(TableModelEvent e) {
		isChanged = true;
	}
	
	private ReportTable createRT (String tabName, Vector<String> colName) {
		ReportTable newRT = null;
		QueryBuilder qb = new QueryBuilder(
				Rdbms_conn.getHValue("Database_DSN"), tabName,
				Rdbms_conn.getDBType());
		String query = qb.get_selCol_query(colName.toArray(),"");
		
		try {
			Rdbms_conn.openConn();
			ResultSet rs = Rdbms_conn.runQuery(query);
			newRT = SqlTablePanel.getSQLValue(rs, true);
			Rdbms_conn.closeConn();
		} catch (SQLException ee) {
			ConsoleFrame.addText("\n SQL Exception:" + ee.getMessage());
			return newRT; // newRT can not be populated
		}
		return newRT;
		
	}
	
	private void searchTableIndex(String searchStr) {
		outputRT = new ReportTable(colName, false, true);
		
		if (_simcheck.openIndex() == false)
			return;

		String queryString = searchStr;
		if (queryString == null || queryString.equals("") == true)
			return;
		
		    queryString = prepareLQuery(queryString);
			Query qry = _simcheck.parseQuery(queryString);
			Hits hit = _simcheck.searchIndex(qry);
			if (hit == null || hit.length() <= 0) {
				JOptionPane.showMessageDialog(null,
						"No Matching Record Found", "No Record Found",
						JOptionPane.INFORMATION_MESSAGE);
				return; 
			}
			// Iterate over the Documents in the Hits object

			for (int j = 0; j < hit.length(); j++) {
				try {
					Document doc = hit.doc(j);
					String rowid = doc.get("at__rowid__");
					Object[] row = null;
						row = _rt.getRow(Integer.parseInt(rowid));

					outputRT.addFillRow(row);

				} catch (Exception e) {
					ConsoleFrame.addText("\n " + e.getMessage());
					ConsoleFrame.addText("\n Error: Can not open Document");
				}
			}

			
		_simcheck.closeSeachIndex();

		JPanel jp_p = new JPanel(new BorderLayout());
		jp_p.add(outputRT, BorderLayout.CENTER);
		// Show the table now
		dg = new JFrame("Similar Records Frame");
		dg.setLocation(250, 100);
		dg.getContentPane().add(jp_p);
		dg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dg.pack();
		QualityListener.bringToFront(dg);
	}
	
	public String prepareLQuery(String rawquery) {
		rawquery = rawquery.replaceAll(",", " ");
		rawquery = rawquery.replaceAll("\\s+", " ");
		String[] token = rawquery.split(" ");
		String newTerm = "";
		for (int j=0; j<colName.length; j++ ) {
			for (int i = 0; i < token.length; i++) {
				if (token[i] == null || "".equals(token[i]))
				continue;

				if (newTerm.equals("") == false )
				newTerm += " OR ";
				
				newTerm += colName[j] + ":"
					+ QueryParser.escape(token[i]) + "~0.3 "; // For Fuzzy Logic
		
			}
		}
		return newTerm;
	}

} // End of Similarity Check
