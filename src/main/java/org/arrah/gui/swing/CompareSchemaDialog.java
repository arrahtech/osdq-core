package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2013      *
 *     http://www.arrah.in                     *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is used for getting info about 
 * comparing schemas across DB
 * 
 *
 */

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;


import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.profile.DBMetaInfo;
import org.arrah.framework.profile.NewConnTableMetaInfo;
import org.arrah.framework.profile.TableMetaInfo;
import org.arrah.framework.rdbms.Rdbms_NewConn;
import org.arrah.framework.rdbms.Rdbms_conn;

public class CompareSchemaDialog implements TreeSelectionListener {

	private JPanel topTablePane = new JPanel(),botTablePane = new JPanel();
	private DefaultMutableTreeNode priTop = new DefaultMutableTreeNode("Primary DB");
	private DefaultMutableTreeNode secBot = new DefaultMutableTreeNode("Secondary DB");
	private Rdbms_NewConn newConn = null;
	
	public CompareSchemaDialog() { // Default
		
	}
	
	public void createGUI () {
		
		if ( inputTable() == false) return;
		
		//Provide preferred  sizes for the two components in the split pane
		Dimension minSize = new Dimension(300,300);
		topTablePane.setMinimumSize(minSize);
		botTablePane.setMinimumSize(minSize);

		JTree toptree = new JTree(priTop);
		toptree.getSelectionModel().setSelectionMode(1);
		
		JTree bottree = new JTree(secBot);
		toptree.getSelectionModel().setSelectionMode(1);
		toptree.addTreeSelectionListener(this);
		bottree.addTreeSelectionListener(this);
		
		JScrollPane jscrollpane1 = new JScrollPane(toptree);
		jscrollpane1.setPreferredSize(new Dimension(150,300));
		
		JScrollPane jscrollpane2 = new JScrollPane(bottree);
		jscrollpane2.setPreferredSize(new Dimension(150,300));
		
		JPanel leftPanel = new JPanel();
		BoxLayout boxl = new BoxLayout(leftPanel,BoxLayout.Y_AXIS);
		leftPanel.setLayout(boxl);
		leftPanel.add(jscrollpane1); leftPanel.add(jscrollpane2);
		
		JScrollPane jscrollpane3 = new JScrollPane(topTablePane);
		jscrollpane3.setPreferredSize(new Dimension(600,300));
		
		JScrollPane jscrollpane4 = new JScrollPane(botTablePane);
		jscrollpane4.setPreferredSize(new Dimension(600,300));
		
		//Create a split pane with the two scroll panes in it.
		JSplitPane splitPaneR = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
						jscrollpane3, jscrollpane4);
		splitPaneR.setOneTouchExpandable(true);
		splitPaneR.setDividerLocation(300);
		
		// Final Window
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, splitPaneR);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);
		
		JDialog jd = new JDialog();
		jd.setTitle("Schema Comparison Dialog");
		jd.setLocation(75, 75);
		jd.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				try {
					newConn.exitConn();
				} catch (SQLException e) {
					// Do nothing
				}
				}
			});
		jd.getContentPane().add(splitPane);
		jd.pack();
		jd.setVisible(true);

	}
	
	private boolean inputTable() {
		
	TestConnectionDialog tcd = new TestConnectionDialog(1); // new Connection
	JOptionPane.showMessageDialog(null, "Choose another Data Source to compare Schema",
			"Table Comparison Dialog",
			JOptionPane.INFORMATION_MESSAGE);
	tcd.createGUI();

	Hashtable <String,String> _fileParse = tcd.getDBParam();
	if (_fileParse == null ) { 
			JOptionPane.showMessageDialog(null, "Parameters not selected for new Connection",
					"Schema Comparison Dialog",JOptionPane.ERROR_MESSAGE);
			return false; 
	} // do not show dialog

	
	try {
		newConn = new Rdbms_NewConn(_fileParse);
		if ( newConn.openConn() == false) {
			JOptionPane.showMessageDialog(null, "Can Not Create new Connection",
					"Schema Comparison Dialog",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		createNode(priTop,false);
		createNode(secBot,true);
		return true;
		
	} catch (Exception e1) {
		System.out.println(e1.getMessage());
		e1.printStackTrace();
		return false;
	} finally {
		
	}
	
	}
	
	private void getPanel(TreePath absPath) throws Exception {
		
		if (absPath == null) return ;
		int pathC = absPath.getPathCount();
		boolean isnewConn = false;
		int funcNo = 0;
		
		if ("Primary DB".equalsIgnoreCase(absPath.getPathComponent(0).toString()) == false)
			isnewConn = true;
				
		if (pathC == 2)  { // it may be parameter or Procedure
			String selStr = absPath.getPathComponent(1).toString();
			if ("Parameters".equalsIgnoreCase(selStr) == true ) funcNo = 1;
			if ("Procedures".equalsIgnoreCase(selStr) == true ) funcNo = 2;
			if ( funcNo == 0) return ;
		}
		if (pathC == 4)  { // it may be meta data profile data, index or key Info
			String selStr = absPath.getPathComponent(3).toString();
			if ("MetaData".equalsIgnoreCase(selStr) == true ) funcNo = 4;
			if ("Profile Data".equalsIgnoreCase(selStr) == true ) funcNo = 5;
			if ("Index".equalsIgnoreCase(selStr) == true ) funcNo = 3;
			if ("Key Info".equalsIgnoreCase(selStr) == true ) funcNo = 6;
			if ( funcNo == 0) return ;
		}
		if (isnewConn == true) {
			ReportTableModel rtm = null, rtm1 = null , rtm2 = null, rtm3 = null,  rtm4 = null, rtm5 = null;
			ReportTable rt = null;
			int i=0;
			
			Vector<String> table_v = newConn.getTable();
			if (funcNo >= 3 ) 
				i = table_v.indexOf(absPath.getPathComponent(2).toString());
			switch(funcNo) {
				case 1:
					DBMetaInfo dbmeta = new DBMetaInfo(newConn);
					rtm4 = dbmeta.getParameterInfo();
					rt = new ReportTable(rtm4);
					createPanel(rt, false);
					break;
				case 2 :
					DBMetaInfo dbmeta1 = new DBMetaInfo(newConn);
					rtm5 = dbmeta1.getProcedureInfo();
					rt = new ReportTable(rtm5);
					createPanel(rt, false);
					break;
				case 3 : // index
					NewConnTableMetaInfo newTableInfo = new NewConnTableMetaInfo(newConn);
					rtm = newTableInfo.populateTable(1, i, i+1, rtm);
					rt = new ReportTable(rtm);
					createPanel(rt, false);
					break;
				case 4 : // Metadata
					NewConnTableMetaInfo newTableInfo1 = new NewConnTableMetaInfo(newConn);
					rtm1 = newTableInfo1.populateTable(2, i, i+1, rtm1);
					rt = new ReportTable(rtm1);
					createPanel(rt, false);
					break;
				case 5 : // Profiler data
					NewConnTableMetaInfo newTableInfo2 = new NewConnTableMetaInfo(newConn);
					rtm2 = newTableInfo2.populateTable(4, i, i+1, rtm2);
					rt = new ReportTable(rtm2);
					createPanel(rt, false);
					break;
				case 6 :
					NewConnTableMetaInfo newTableInfo3 = new NewConnTableMetaInfo(newConn);
					rtm3 = newTableInfo3.tableKeyInfo(table_v.get(i));
					rt = new ReportTable(rtm3);
					createPanel(rt, false);
					break;
				default :
				
		} } else {
			ReportTableModel rtm = null, rtm1 = null , rtm2 = null, rtm3 = null,  rtm4 = null, rtm5 = null;
			ReportTable rt = null;
			int i=0;
			
			Vector<String> table_v = Rdbms_conn.getTable();
			if (funcNo >= 3 ) 
				i = table_v.indexOf(absPath.getPathComponent(2).toString());
			switch(funcNo) {
				case 1:
					DBMetaInfo dbmeta = new DBMetaInfo();
					rtm4 = dbmeta.getParameterInfo();
					rt = new ReportTable(rtm4);
					createPanel(rt, true);
					break;
				case 2 :
					DBMetaInfo dbmeta1 = new DBMetaInfo();
					rtm5 = dbmeta1.getProcedureInfo();
					rt = new ReportTable(rtm5);
					createPanel(rt, true);
					break;
				case 3 :
					rtm = TableMetaInfo.populateTable(1, i, i+1, rtm);
					rt = new ReportTable(rtm);
					createPanel(rt, true);
					break;
				case 4 :
					rtm1 = TableMetaInfo.populateTable(2, i, i+1, rtm1);
					rt = new ReportTable(rtm1);
					createPanel(rt, true);
					break;
				case 5 :
					rtm2 = TableMetaInfo.populateTable(4, i, i+1, rtm2);
					rt = new ReportTable(rtm2);
					createPanel(rt, true);
					break;
				case 6 :
					rtm3 = TableMetaInfo.tableKeyInfo(table_v.get(i));
					rt = new ReportTable(rtm3);
					createPanel(rt, true);
					break;
				default :
			
		} }
	}
	
	private void createPanel(JComponent jp, boolean top) {
		if (top== true) {
			topTablePane.removeAll();
			topTablePane.add(jp);
			topTablePane.revalidate();
			topTablePane.repaint();
		} else {
			botTablePane.removeAll();
			botTablePane.add(jp);
			botTablePane.revalidate();
			botTablePane.repaint();
		}
		
	}

	private void createNode (DefaultMutableTreeNode node, boolean newConnection) throws SQLException {
		Vector<String> table_v = new Vector<String>();
		if (newConnection == false ) {
			table_v = Rdbms_conn.getTable();
			
		} else { // new connection
			newConn.populateTable();
			table_v = newConn.getTable();
		}
		int tabC = table_v.size();
		
		DefaultMutableTreeNode tablen = new DefaultMutableTreeNode("Tables("+tabC+")");
		node.add(tablen);
		DefaultMutableTreeNode proce = new DefaultMutableTreeNode("Procedures");
		node.add(proce);
		DefaultMutableTreeNode param = new DefaultMutableTreeNode("Parameters");
		node.add(param);
		
		for (int i=0; i < tabC; i++) {
			DefaultMutableTreeNode tabName =  new DefaultMutableTreeNode(table_v.get(i));
			tablen.add(tabName);
			
			DefaultMutableTreeNode tabMData =  new DefaultMutableTreeNode("MetaData");
			tabName.add(tabMData);
			DefaultMutableTreeNode tabData =  new DefaultMutableTreeNode("Profile Data");
			tabName.add(tabData);
			DefaultMutableTreeNode tabindex =  new DefaultMutableTreeNode("Index");
			tabName.add(tabindex);
			DefaultMutableTreeNode tabPK =  new DefaultMutableTreeNode("Key Info");
			tabName.add(tabPK);
		}
	}
	
	public void valueChanged(TreeSelectionEvent treeselectionevent) {
		JTree tree = (JTree)treeselectionevent.getSource();
		if ( tree == null) return;
		try {
			tree.getTopLevelAncestor().setCursor(
					java.awt.Cursor
							.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

			TreePath treepath = tree.getSelectionPath();
			int pathC = treepath.getPathCount();
			
			if (pathC == 2)  { // it may be parameter or Procedure
				String selStr = treepath.getPathComponent(1).toString();
				if ("Parameters".equalsIgnoreCase(selStr) == true ||
						"Procedures".equalsIgnoreCase(selStr) == true ) {
					getPanel(treepath);
				}
			}
			if (pathC == 4)  { 
				getPanel(treepath);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tree.getTopLevelAncestor()
					.setCursor(
							java.awt.Cursor
									.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
		}

	}
}