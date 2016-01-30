package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2015      *
 *     http://www.arrahtec.org                 *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is used for taking input and create
 * a new reportTable with less columns.
 * 
 * ReportTable does not allow deleting the column
 * so this is required.
 *
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.arrah.framework.ndtable.ReportTableModel;

public class NewTableDialog implements ActionListener {
	
	private JDialog jd, jd1;
	private JCheckBox selBox[];
	private JLabel lvalue[];
	private ReportTableModel _rtm;
	private ReportTable _rt = null;
	private boolean isExit = true;
	
	public NewTableDialog (ReportTableModel rtm) {
		_rtm = rtm;
		int colcount = rtm.getModel().getColumnCount();
		String[] colName = new String[colcount];
		for (int i=0; i < colcount; i++)
			colName[i] = rtm.getModel().getColumnName(i);
		tableRenameDialog(colName);
	}
	
	private void tableRenameDialog(String[] colName) {
		JPanel dp = new JPanel();
		dp.setLayout(new BorderLayout());
		
		
		//Create and populate the panel for table rename       
		JPanel p = new JPanel(new SpringLayout());
		int numPairs = colName.length;
		selBox = new JCheckBox[numPairs];
		lvalue = new JLabel[numPairs];
		
		for (int i = 0; i < numPairs; i++) {
			lvalue[i] = new JLabel(colName[i],JLabel.TRAILING);
			selBox[i] = new JCheckBox();
			p.add(selBox[i]);
			p.add(lvalue[i]);
		}
		
		//Lay out the panel.        
		SpringUtilities.makeCompactGrid(p,                                        
				numPairs, 2, //rows, cols                                        
				6, 6,        //initX, initY                                        
				6, 6);       //xPad, yPad          
		

		JPanel bp = new JPanel();

		JButton tstc = new JButton("Create");
		tstc.setActionCommand("save");
		tstc.addKeyListener(new KeyBoardListener());
		tstc.addActionListener(this);
		bp.add(tstc);
		
		JButton cn_b = new JButton("Exit");
		cn_b.setActionCommand("exit");
		cn_b.addKeyListener(new KeyBoardListener());
		cn_b.addActionListener(this);
		bp.add(cn_b);
		
		dp.add(p, BorderLayout.CENTER);
		dp.add(bp, BorderLayout.PAGE_END);
		
		jd = new JDialog ();
		jd.setTitle("Table Creation Dialog");
		jd.setModal(true);
		jd.setLocation(200, 200);
		jd.getContentPane().add(dp);
		jd.pack();
		jd.setVisible(true);

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String action_c = e.getActionCommand();
		try {
			if (action_c.compareToIgnoreCase("exit") == 0) {
				isExit = true;
				return;
			}
			if (action_c.compareToIgnoreCase("save") == 0) {
				isExit = false;
				int colC = lvalue.length;
				Vector<String>  colName = new Vector<String>();
				for (int i = 0; i < colC; i++) {
					if (selBox[i].isSelected() == true) // only selected one
					colName.add(lvalue[i].getText());
				}
				String[]  newCol = new String[colName.size()];
				int[] newColI = new int[colName.size()];
				ReportTableModel newRTM = new ReportTableModel(colName.toArray(newCol));
				
				for (int i=0; i < newColI.length; i++)
					newColI[i] = _rtm.getColumnIndex(newCol[i]);
				
				int rowC = _rtm.getModel().getRowCount();
						
				for (int i=0; i < rowC; i++) 
					newRTM.addFillRow(_rtm.getSelectedColRow(i, newColI));
				
				_rt = new ReportTable(newRTM); // assign new value
				return;
			}
		} catch (Exception e1) {
			
		} finally {
			jd.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
			jd.dispose();
		}
		
	}
	public void displayGUI () {
		if (isExit == true ) return;
		jd1 = new JDialog ();
		jd1.setTitle("Table Creation Dialog");
		jd1.setModal(true);
		jd1.setLocation(250,250);
		jd1.getContentPane().add(_rt);
		jd1.pack();
		jd1.setVisible(true);
		
	}
}
