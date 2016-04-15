package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2016      *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is used for grouping time based
 * on hour, day of week and date of month
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
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;


public class TimeGroupingPanel implements ActionListener, ItemListener {
	private ReportTable _rt;
	private int _rowC = 0;
	private int _colIndex = 0;
	private JDialog d_f;
	private JFormattedTextField jrn_low, jrn_high;
	private JComboBox<String> colSel;
	private Border line_b;
	private int beginIndex, endIndex;
	private JLabel colType;
	private Vector<JTextField> grpName_v,otherValue_v,grpName_vS;
	private Vector<JFormattedTextField>jft_low_v, jft_high_v;
	private Vector<JSpinner>jft_low_vS, jft_high_vS;
	private JPanel cp,cps; // for adding more row for number grouping
	private Vector<Boolean> validDate_bvS;

	
	private int capacity, capacityS;
	
	public TimeGroupingPanel(ReportTable rt, int colIndex) {
		_rt = rt;
		_colIndex = colIndex;
		_rowC = rt.table.getRowCount();
		createDialog();
	}; // Constructor
	

	private void createDialog() {
		JPanel jp = new JPanel(new BorderLayout());
		line_b = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

		JPanel header = new JPanel(new GridLayout(1,1));
		header.add(createSelectionPanel());
		jp.add(header,BorderLayout.NORTH);

		JPanel body = new JPanel(new GridLayout(1,1));
		body.add(createSeasonPanel());
		jp.add(body,BorderLayout.CENTER);

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
		d_f.setTitle("Grouping  Dialog");
		d_f.setLocation(100, 100);
		d_f.setPreferredSize(new Dimension(950,850));
		d_f.getContentPane().add(jp);
		d_f.pack();
		d_f.setVisible(true);

	}

	/* User can choose multiple options to group date so that */
	private JPanel createSeasonPanel() {
		
		grpName_vS = new Vector<JTextField>();
		jft_low_vS = new Vector<JSpinner>();
		jft_high_vS = new Vector<JSpinner>();
				
		JPanel numjp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		numjp.setBorder(line_b);
		
		JButton addcol = new JButton("Add Row");
		addcol.setActionCommand("addcolseason");
		addcol.addActionListener(this);
		addcol.addKeyListener(new KeyBoardListener());
		cps = new JPanel(new SpringLayout());
		for (int i = 0; i < 8; i++) // create default 8 col Name
			addSeasonRow();

		SpringUtilities.makeCompactGrid(cps, capacityS, 6, 3, 3, 3, 3);
		JScrollPane jscrollpane1 = new JScrollPane(cps);
		jscrollpane1.setPreferredSize(new Dimension(850, 250));
		
		numjp.add(addcol);
		numjp.add(jscrollpane1);
		
		return numjp;
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


	private JPanel createSelectionPanel() {
		JPanel selectionjp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel selL = new JLabel("Choose Column to Group From:     ");
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
		if (action.equals("addcol")) {
			addRow();
			cp.setLayout(new SpringLayout());
			SpringUtilities.makeCompactGrid(cp, capacity, 8, 3, 3, 3, 3);
			cp.revalidate();
			return;
		}
		if (action.equals("addcolseason")) {
			addSeasonRow();
			cps.setLayout(new SpringLayout());
			SpringUtilities.makeCompactGrid(cps, capacityS, 6, 3, 3, 3, 3);
			cps.revalidate();
			return;
		}
		if (action.equals("ok")) {
			// d_f.dispose(); do not dispose now
			if (_rt.isSorting() || _rt.table.isEditing()) {
				JOptionPane.showMessageDialog(null, "Please Cancel Editing or Sorting. Then proceed");
				return;
			}
			
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
			
			int selColIndex = colSel.getSelectedIndex(); // Take value from  col on which grouping will be done
			// Check if it date type
			boolean dateValidated = false;
			
			validateDateInput();
			
			for (int i = (beginIndex -1) ; i < ( endIndex -1 ); i++) {
				Object colObject = _rt.getModel().getValueAt(i, selColIndex);
				 if (colObject == null) continue;
				 if (dateValidated == false) {
					 if (colObject instanceof java.util.Date) {
							d_f.dispose(); // now dispose
							dateValidated = true;
					} else {
						ConsoleFrame.addText("\n Input String is not in Date format");
							JOptionPane.showMessageDialog(null, "Input is not of Date Type\n Please format to Date type ", 
									"Date Type Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				 }
				 String colVal = "";
				 for (int j=0; j < capacityS; j++) {
						if (validDate_bvS.get(j) == true) {
							Date mindate = (Date) jft_low_vS.get(j).getValue();
							Date maxdate = (Date) jft_high_vS.get(j).getValue();
							if ( ((Date)colObject).compareTo(mindate) >= 0 && ((Date)colObject).before(maxdate)) {
								colVal = grpName_vS.get(j).getText();
								break; // break the loop and setValue then get next value
							}
							
						} else continue; // not valid value	
				}
				_rt.getModel().setValueAt(colVal, i, _colIndex);
			}
			d_f.dispose(); // in case it is not disposed yet if all the filed null condition


			
			return;
		} // Ok action
	} // End of actionPerformed
	
	public void itemStateChanged(ItemEvent e) {
		
	} // End of ItemStateChange
	
	
	/* This function will be used to add row to number grouping feature */
	private void addRow() {
		
		JLabel grpNameL = new JLabel("Group ID:");
		JTextField grpName = new JTextField(10);
		grpName.setText("Group_"+ (capacity+1));
		grpName.setToolTipText("Enter Group Name");
		
		JLabel lrange = new JLabel("  Min:", JLabel.LEADING);
		JFormattedTextField jft_low = new JFormattedTextField();
		jft_low.setValue(new Double(capacity * 100.00D));
		jft_low.setColumns(10);
		JLabel hrange = new JLabel("  Max:", JLabel.LEADING);
		JFormattedTextField jft_high = new JFormattedTextField();
		jft_high.setValue(new Double((capacity+1) * 100.00D)); // default Min
		jft_high.setColumns(10);
		
		JLabel otherValueL = new JLabel("Others:", JLabel.LEADING);
		otherValueL.setToolTipText("Comma Separated List of Numbers");
		JTextField otherValue = new JTextField(20);
		otherValue.setToolTipText("Comma Separated List of Numbers");
		
		grpName_v.add(capacity,grpName);
		jft_low_v.add(capacity,jft_low);
		jft_high_v.add(capacity,jft_high);
		otherValue_v.add(capacity,otherValue);
		
		/* Add value to Panel */
		cp.add(grpNameL);
		cp.add(grpName);
		cp.add(lrange);
		cp.add(jft_low);
		cp.add(hrange);
		cp.add(jft_high);
		cp.add(otherValueL);
		cp.add(otherValue);
		capacity++;
	}

	/* This function will be used to add row to season grouping feature */
	private void addSeasonRow() {
		
		JLabel grpNameL = new JLabel("Season ID:");
		JTextField grpName = new JTextField(10);
		grpName.setText("Season_"+ (capacityS+1));
		grpName.setToolTipText("Enter Season Name");
		
		JSpinner jsp_low = new JSpinner(new SpinnerDateModel());
		JSpinner jsp_high = new JSpinner(new SpinnerDateModel());
		jsp_low.setEditor(new JSpinner.DateEditor(jsp_low, "HH:mm:ss"));
		jsp_high.setEditor(new JSpinner.DateEditor(jsp_high, "HH:mm:ss"));
		
		JLabel lrange = new JLabel("Start Time:", JLabel.LEADING);
		JLabel hrange = new JLabel("End Time:", JLabel.LEADING);
		
		grpName_vS.add(capacityS,grpName);
		jft_low_vS.add(capacityS,jsp_low);
		jft_high_vS.add(capacityS,jsp_high);

		
		/* Add value to Panel */
		cps.add(grpNameL);
		cps.add(grpName);
		cps.add(lrange);
		cps.add(jsp_low);
		cps.add(hrange);
		cps.add(jsp_high);
		capacityS++;
	}
	
	// Validate the date
	private void validateDateInput() {
		validDate_bvS = new Vector<Boolean>();
		
		for (int i=0; i < capacityS; i++) {
			String grpName = grpName_vS.get(i).getText();
			if (grpName == null || "".equals(grpName) ){
				validDate_bvS.add(i, false);
				continue;
			}
			Date mindate = (Date) jft_low_vS.get(i).getValue();
			Date maxdate = (Date) jft_high_vS.get(i).getValue();
			if ( mindate == null || maxdate == null || maxdate.before(mindate) == true ) {
				validDate_bvS.add(i, false);
				continue;
			}
			validDate_bvS.add(i, true); // All valid
		}
	}

}
