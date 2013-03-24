package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2007      *
 *     http://www.arrah.in                     *
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
 * that needs to be run to get next/previous value 
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.arrah.framework.dataquality.ExpressionBuilder;

public class ExpressionBuilderPanel implements ActionListener {
	private ReportTable _rt;
	private JTextArea _expane;
	private int _selColIndex;
	private JDialog _dg;
	private JLabel statusL;
	final static private String START_TOKEN = "#{";
	final static private String END_TOKEN = "}";
	final static private String STATUS_STR = "Value:";

	public ExpressionBuilderPanel(ReportTable rt, int selColIndex) {
		_rt = rt;
		_selColIndex = selColIndex;
		createGUI();
	}

	public ExpressionBuilderPanel(ReportTable rt) {
		_rt = rt;
		createGUI();
	}

	private void createGUI() {

		statusL = new JLabel("Value:", JLabel.TRAILING);

		JButton preview = new JButton("Parse");
		preview.addActionListener(this);
		preview.addKeyListener(new KeyBoardListener());
		preview.setActionCommand("preview");

		JButton ok = new JButton("OK");
		ok.addActionListener(this);
		ok.addKeyListener(new KeyBoardListener());
		ok.setActionCommand("ok");

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.addKeyListener(new KeyBoardListener());
		cancel.setActionCommand("cancel");

		JButton clear = new JButton("Clear");
		clear.addActionListener(this);
		clear.addKeyListener(new KeyBoardListener());
		clear.setActionCommand("clear");

		_expane = new JTextArea(6, 30);
		_expane.setWrapStyleWord(true);
		_expane.setLineWrap(true);

		JLabel exml = new JLabel();
		Border line_b = BorderFactory.createEtchedBorder(EtchedBorder.RAISED,
				Color.YELLOW, Color.RED);
		exml.setBorder(BorderFactory.createTitledBorder(line_b, "Example"));
		String exp_text = "<html><body><pre>\"#{COLUMN_NAME1}\"+\"#{COLUMN_NAME2}\"</pre> for String Value.<br>";
		exp_text += "<pre>#{COLUMN_NAME1}+#{COLUMN_NAME2}</pre> for Number Value.</body><html>";
		exml.setText(exp_text);

		int colC = _rt.table.getColumnCount();
		String[] colName = new String[colC];
		for (int i = 0; i < colC; i++) {
			colName[i] = _rt.table.getColumnName(i);
		}

		final JList<String> list = new JList<String>(colName);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setCellRenderer(new MyListRenderer());

		// Add listener to it
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String insS = START_TOKEN
							+ list.getSelectedValue().toString() + END_TOKEN;
					_expane.insert(insS, _expane.getCaretPosition());
				}
			}
		};
		list.addMouseListener(mouseListener);

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 300));

		JLabel ll = new JLabel("Double Click to Insert");

		JPanel panel = new JPanel();
		panel.add(preview);
		panel.add(ok);
		panel.add(cancel);
		panel.add(clear);
		panel.add(_expane);
		panel.add(listScroller);
		panel.add(statusL);
		panel.add(ll);
		panel.add(exml);

		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setPreferredSize(new Dimension(600, 400));

		layout.putConstraint(SpringLayout.NORTH, statusL, 2,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, statusL, 2, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.NORTH, _expane, 2,
				SpringLayout.SOUTH, statusL);
		layout.putConstraint(SpringLayout.WEST, _expane, 2, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.NORTH, clear, 5, SpringLayout.SOUTH,
				_expane);
		layout.putConstraint(SpringLayout.WEST, clear, 20, SpringLayout.WEST,
				_expane);
		layout.putConstraint(SpringLayout.NORTH, preview, 5,
				SpringLayout.SOUTH, _expane);
		layout.putConstraint(SpringLayout.WEST, preview, 20, SpringLayout.EAST,
				clear);
		layout.putConstraint(SpringLayout.NORTH, exml, 5, SpringLayout.SOUTH,
				preview);
		layout.putConstraint(SpringLayout.WEST, exml, 2, SpringLayout.WEST,
				panel);

		layout.putConstraint(SpringLayout.NORTH, ll, 5, SpringLayout.NORTH,
				panel);
		layout.putConstraint(SpringLayout.WEST, ll, 10, SpringLayout.EAST,
				_expane);
		layout.putConstraint(SpringLayout.NORTH, listScroller, 2,
				SpringLayout.SOUTH, ll);
		layout.putConstraint(SpringLayout.WEST, listScroller, 10,
				SpringLayout.EAST, _expane);
		layout.putConstraint(SpringLayout.SOUTH, ok, -5, SpringLayout.SOUTH,
				panel);
		layout.putConstraint(SpringLayout.WEST, ok, -200, SpringLayout.EAST,
				panel);
		layout.putConstraint(SpringLayout.SOUTH, cancel, 0, SpringLayout.SOUTH,
				ok);
		layout.putConstraint(SpringLayout.WEST, cancel, 5, SpringLayout.EAST,
				ok);

		_dg = new JDialog();
		_dg.setTitle("Expression Builder Dialog");
		_dg.setLocation(200, 100);
		_dg.getContentPane().add(panel);

		_dg.pack();
		_dg.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("clear")) {
			_expane.setText("");
			return;
		}
		if (command.equals("cancel")) {
			_dg.dispose();
			return;
		}
		if (command.equals("preview")) {
			String exp = _expane.getText();
			String res = preparseJeval(exp, _rt, -1); // -1 for preview do not
														// update column
			if (res != null) {
				JOptionPane.showMessageDialog(null, "Parsing OK");
				statusL.setText(STATUS_STR + " " + res);
			} else {
				JOptionPane.showMessageDialog(null, "Parsing Failed");
				statusL.setText(STATUS_STR + " Parsing Failed");
			}
			return;
		}
		if (command.equals("ok")) {
			String exp = _expane.getText();
			_dg.dispose();
			preparseJeval(exp, _rt, _selColIndex);

			return;
		}

	}

	public static String preparseJeval(String expression, ReportTable rpt,
			int selIndex) {
		String jevalString = ExpressionBuilder.preparseJeval(expression,
				rpt.getRTMModel(), selIndex);

		return jevalString;
	}

	public static int getColumnIndex(ReportTable rpt, String colName) {
		int row_c = rpt.table.getColumnCount();
		for (int i = 0; i < row_c; i++) {
			if (colName.equals(rpt.table.getColumnName(i)))
				return i;
		}
		return -1;
	}

	public class MyListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -347683601167694906L;

		public Component getListCellRendererComponent(JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value,
					index, isSelected, cellHasFocus);
			int index_i = getColumnIndex(_rt, value.toString());
			if (index_i < 0)
				((JLabel) c).setToolTipText(null);
			else
				((JLabel) c).setToolTipText(_rt.table.getColumnClass(index_i)
						.getName());
			return c;
		}
	}
} // End of Expression Builder
