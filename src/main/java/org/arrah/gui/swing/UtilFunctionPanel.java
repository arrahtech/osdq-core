package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2014      *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is uses to integrated utility functions
 * of expression builder
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.arrah.framework.datagen.AggrCumRTM;


public class UtilFunctionPanel implements ActionListener, ItemListener {
	private ReportTable _rt;
	private int _rowC = 0;
	private int _colIndex = 0;
	private JDialog d_f;
	private JFormattedTextField jrn_low, jrn_high;
	private JRadioButton rd1, rd2, rd3, rd4, leftrd, rightrd;
	private JComboBox<String> colSel;
	private Border line_b;
	private int beginIndex, endIndex;
	private JLabel colType;
	private JTextField splitString;

	
	public UtilFunctionPanel(ReportTable rt, int colIndex) {
		_rt = rt;
		_colIndex = colIndex;
		_rowC = rt.table.getRowCount();
		createDialog();
	}; // Constructor
	

	private void createDialog() {
		JPanel jp = new JPanel(new BorderLayout());
		line_b = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

		rd1 = new JRadioButton("String Split");
		rd4 = new JRadioButton("Reversse String");
		rd2 = new JRadioButton("Epoch MilliSecond to Date ");
		rd3 = new JRadioButton("Date to Epoch MilliSecond");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rd1);bg.add(rd2);bg.add(rd3);bg.add(rd4);
		rd1.setSelected(true);

		jp.add(createSelectionPanel(),BorderLayout.NORTH);
		
		JPanel header = new JPanel(new GridLayout(4,1));
		header.add(createSplitPanel()); 
		header.add(createReversePanel()); 
		header.add(createDateToSecondPanel() );
		header.add(createSecondToDatePanel());
		jp.add(header,BorderLayout.CENTER);

		JPanel bp = new JPanel();
		JButton ok = new JButton("OK");
		ok.addKeyListener(new KeyBoardListener());
		ok.setActionCommand("ok");
		ok.addActionListener(this);
		bp.add(ok);
		JButton can = new JButton("Cancel");
		can.addKeyListener(new KeyBoardListener());
		can.setActionCommand("cancel");
		can.addActionListener(this);
		bp.add(can);

		JPanel bottom = new JPanel(new GridLayout(2,1));
		bottom.add(createRowNumPanel());bottom.add(bp);
		
		jp.add(bottom,BorderLayout.SOUTH);

		d_f = new JDialog();
		d_f.setModal(true);
		d_f.setTitle("Utility Function Dialog");
		d_f.setLocation(300, 250);
		d_f.setPreferredSize(new Dimension(600,275));
		d_f.getContentPane().add(jp);
		d_f.pack();
		d_f.setVisible(true);

	}

	/* User can choose multiple options to group */
	private JPanel createSplitPanel() {
		
		JPanel splitjp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		splitjp.add(rd1);
		
		splitjp.setBorder(line_b);
		splitString = new JTextField();
		splitString.setText("");
		splitString.setColumns(10);
		splitjp.add(splitString);
		
		leftrd = new JRadioButton("Left Value");
		rightrd = new JRadioButton("Right Value");
		leftrd.setSelected(true);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(leftrd); bg.add(rightrd);
		splitjp.add(leftrd) ;splitjp.add(rightrd) ;
		return splitjp;
	}
	private JPanel createRowNumPanel() {
		JPanel rownnumjp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel lrange = new JLabel("  Row Numbers to Populate : From (Inclusive)", JLabel.LEADING);
		jrn_low = new JFormattedTextField();
		jrn_low.setValue(new Long(1));
		jrn_low.setColumns(8);
		JLabel torange = new JLabel("  To(Exclusive):", JLabel.LEADING);
		jrn_high = new JFormattedTextField();
		jrn_high.setValue(new Long(_rowC+1));
		jrn_high.setColumns(8);
		
		rownnumjp.add(lrange);
		rownnumjp.add(jrn_low);
		rownnumjp.add(torange);
		rownnumjp.add(jrn_high);
		rownnumjp.setBorder(line_b);
		return rownnumjp;
	}
	
	private JPanel createReversePanel() {
		JPanel reversejp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		reversejp.add(rd4);
		reversejp.setBorder(line_b);
		return reversejp;
	}
	
	private JPanel createSecondToDatePanel() {
		JPanel datejp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		datejp.add(rd2);
		datejp.setBorder(line_b);
		return datejp;
	}

	private JPanel createDateToSecondPanel() {
		JPanel datejp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		datejp.add(rd3);
		datejp.setBorder(line_b);
		return datejp;
	}

	private JPanel createSelectionPanel() {
		JPanel selectionjp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel selL = new JLabel("Choose Column to Apply Function on:     ");
		int colC = _rt.getModel().getColumnCount();
		String[] colName = new String[colC];
		
		for (int i = 0; i < colC; i++) 
			colName[i] = _rt.getModel().getColumnName(i);
		
		selectionjp.add(selL);
		colSel = new JComboBox<String>(colName);
		colSel.addItemListener(this);
		selectionjp.add(colSel);
		colType = new JLabel("        "+_rt.getModel().getColumnClass(0).getName());
		selectionjp.add(colType);
		return selectionjp;
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("cancel")) {
			d_f.dispose();
			return;
		}
		if (action.equals("ok")) {
			// d_f.dispose(); do not dispose now
			beginIndex = ((Long) jrn_low.getValue()).intValue();
			endIndex = ((Long) jrn_high.getValue()).intValue();
			if (beginIndex <= 0 || beginIndex > _rowC)
				beginIndex = 1;
			if (endIndex <= 0 || endIndex > (_rowC + 1))
				endIndex = _rowC +1;
			
			int numGenerate = endIndex - beginIndex;
			if ( numGenerate <= 0 || numGenerate > _rowC) {
				numGenerate = _rowC; // default behavior for Invalid number
				beginIndex = 1;endIndex = _rowC+1;
			}
			
			if (rd4.isSelected() == true) {
				int selColIndex = colSel.getSelectedIndex(); // Take value from  col on which grouping will be done
				
				for (int i = (beginIndex -1) ; i < ( endIndex -1 ); i++) {
					Object colObject = _rt.getValueAt(i, selColIndex);
					 if (colObject == null) continue;
					 String revString="";
					 for (int curIndex = colObject.toString().length()-1; curIndex >= 0; curIndex-- ) 
						 revString += colObject.toString().charAt(curIndex);
					 _rt.setTableValueAt(revString, i, _colIndex);
				}
				d_f.dispose(); // in case it is not disposed yet if all the filed null condition
				return;
			} // end of Reverse
			if (rd1.isSelected() == true) {
				int selColIndex = colSel.getSelectedIndex(); // Take value from  col on which grouping will be done
				String regexStr = splitString.getText();
				if (regexStr == null || "".equals(regexStr)) {
					JOptionPane.showMessageDialog(null, "Split value is not valid ", 
						"Split Type Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				for (int i = (beginIndex -1) ; i < ( endIndex -1 ); i++) {
					Object colObject = _rt.getValueAt(i, selColIndex);
					 if (colObject == null) continue;
					 String[] colVal = AggrCumRTM.splitColString (colObject.toString(),regexStr) ;
					 if (leftrd.isSelected() == true)
						 _rt.setTableValueAt(colVal[0], i, _colIndex);
					 else if (colVal.length > 1 && colVal[1] != null )
						 _rt.setTableValueAt(colVal[1], i, _colIndex);
				}
				d_f.dispose(); // in case it is not disposed yet if all the filed null condition
				return;
			} // end of Split
			if (rd2.isSelected() == true) { //Epoch Millisecond to Date
				int selColIndex = colSel.getSelectedIndex(); // Take value from  col on which grouping will be done
				
				for (int i = (beginIndex -1) ; i < ( endIndex -1 ); i++) {
					Object colObject = _rt.getValueAt(i, selColIndex);
					 if (colObject == null) continue;
						 if (colObject instanceof Long) {
								d_f.dispose(); // now dispose
						} else {
							try {
								colObject = Long.parseLong(colObject.toString());
							} catch(Exception forexp) {
								ConsoleFrame.addText("\n Input Value is not in Number format");
								continue;
							}
						}
					 
					Date colVal = AggrCumRTM.secondIntoDate((Long)colObject);
					_rt.setTableValueAt(colVal, i, _colIndex);
					
				}
				d_f.dispose(); // in case it is not disposed yet if all the filed null condition
				return;
			} // end of date to Epoch
			
			if (rd3.isSelected() == true) { // Date to Epoch Millisecond
				int selColIndex = colSel.getSelectedIndex(); // Take value from  col on which grouping will be done
				
				// Check if it date type
				boolean dateValidated = false;
				
				for (int i = (beginIndex -1) ; i < ( endIndex -1 ); i++) {
					Object colObject = _rt.getValueAt(i, selColIndex);
					 if (colObject == null) continue;
					 if (dateValidated == false) {
						 if (colObject instanceof java.util.Date) {
								d_f.dispose(); // now dispose
								dateValidated = true;
						} else {
							ConsoleFrame.addText("\n Input String is not in Date format");
								JOptionPane.showMessageDialog(null, "Input is not of Date Type\n Please format to date type ", 
										"Date Type Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					 }
					 
					long colVal = AggrCumRTM.dateIntoSecond((Date)colObject); 
					_rt.setTableValueAt(colVal, i, _colIndex);
					
				}
				d_f.dispose(); // in case it is not disposed yet if all the filed null condition
				return;
			} // end of date to Epoch
			
			return;
		} // Ok action
	} // End of actionPerformed
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED ) {
			if (e.getSource() == colSel) {
			int index = colSel.getSelectedIndex();
			colType.setText("        "+_rt.getModel().getColumnClass(index).getName());
			} 
		}
	} // End of ItemStateChange
}
