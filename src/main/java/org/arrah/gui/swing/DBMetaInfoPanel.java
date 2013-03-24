package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2006      *
 *     http://www.arrah.in                     *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is used for creating Metadata info 
 *
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.profile.DBMetaInfo;
import org.arrah.framework.rdbms.DataDictionaryPDF;
import org.arrah.framework.rdbms.Rdbms_conn;
import org.arrah.framework.rdbms.TableRelationInfo;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class DBMetaInfoPanel implements ActionListener {
	private boolean isFrame;
	private boolean summary_info;
	private boolean variableQ;
	private TableMetaInfoPanel vp;
	private ReportTableModel rtm__;
	private ReportTable rt__;
	private String f_title;
	private JComponent src_;

	public DBMetaInfoPanel() {
		isFrame = false;
		summary_info = false;
		variableQ = false;
		vp = null;
		rt__ = null;
		f_title = "DB Meta Information";
	}

	public DBMetaInfoPanel(JComponent src) {
		src_ = src;
		isFrame = false;
		summary_info = false;
		variableQ = false;
		vp = null;
		rt__ = null;
		f_title = "DB Meta Information";
	}

	public void actionPerformed(ActionEvent actionevent) {
		Hashtable<String, TableRelationInfo> hashtable = null, hashtable1 = null, hashtable2 = null;
		String s;
		String s31;

		try {

			if (src_ != null)
				src_.getTopLevelAncestor()
						.setCursor(
								java.awt.Cursor
										.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

			s = ((JMenuItem) actionevent.getSource()).getText();

			if (s.equals("Data Dictionary")) {
				File pdfFile = FileSelectionUtil.promptForFilename("Data Dictionary PDF File");
				if (pdfFile == null) {
					return;
				}
				if (pdfFile.getName().toLowerCase().endsWith(".pdf") == false) {
					File renameF = new File(pdfFile.getAbsolutePath() + ".pdf");
					pdfFile = renameF;
				}
				DataDictionaryPDF datad = new DataDictionaryPDF();
				datad.createDDPDF(pdfFile);
				ConsoleFrame.addText("\nData Dictionary File Saved at:"+pdfFile.getAbsolutePath());
				return;
			}
			if (s.equals("General Info")) {
				f_title = "General Information";
				rtm__ = new DBMetaInfo().getGeneralInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("Support Info")) {
				f_title = "Support Information";
				rtm__ = new DBMetaInfo().getSupportInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("Limitation Info")) {
				f_title = "Limitation Information";
				rtm__ = new DBMetaInfo().getLimitationInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("Functions Info")) {
				f_title = "Functions Information";
				rtm__ = new DBMetaInfo().getFunctionInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("Catalog Info")) {
				f_title = "Catalog Information";
				rtm__ = new DBMetaInfo().getCatalogInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("Standard SQL Type Info")) {
				f_title = "Standard SQL Type Information";
				rtm__ = new DBMetaInfo().getStandardSQLInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("User Defined Type Info")) {
				f_title = "User Defined Type Information";
				rtm__ = new DBMetaInfo().getUserSQLInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("Schema Info")) {
				f_title = "Schema Information";
				rtm__ = new DBMetaInfo().getSchemaInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;
			} else if (s.equals("Procedure Info")) {
				f_title = "Procedure Information";
				rtm__ = new DBMetaInfo().getProcedureInfo();
				rt__ = new ReportTable(rtm__);

				summary_info = true;
				isFrame = true;
			} else if (s.equals("Index Info")) {
				f_title = "Index Information";
				vp = new TableMetaInfoPanel(1);
				variableQ = true;
				isFrame = true;
			} else if (s.equals("Parameter Info")) {
				f_title = "Parameter Information";
				rtm__ = new DBMetaInfo().getParameterInfo();
				rt__ = new ReportTable(rtm__);

				isFrame = true;
				summary_info = true;

			} else if (s.equals("Table Model Info")) {
				f_title = "Table Model Information";

				hashtable = new Hashtable<String, TableRelationInfo>();
				hashtable1 = new Hashtable<String, TableRelationInfo>();
				hashtable2 = new Hashtable<String, TableRelationInfo>();
				DBMetaInfo dbMetaInfo = new DBMetaInfo();
				rtm__ = dbMetaInfo.getTableModelInfo();

				// write code to populate hashtable
				hashtable = dbMetaInfo.getOnlyPKTable();
				hashtable1 = dbMetaInfo.getNoPKTable();
				hashtable2 = dbMetaInfo.getRelatedTable();

			} else if (s.equals("DB MetaData Info")) {
				f_title = "DB MetaData Information";
				vp = new TableMetaInfoPanel(2);
				variableQ = true;
				isFrame = true;
			} else if (s.equals("Table MetaData Info")) {
				f_title = "Table MetaData Information";
				s31 = JOptionPane.showInputDialog(null,
						"Enter MetaData Table Pattern:", "Table Input Dialog",
						-1);
				if (s31 == null || s31.compareTo("") == 0)
					return;

				rtm__ = new DBMetaInfo().getTableMetaData(s31);
				rt__ = new ReportTable(rtm__);

				summary_info = true;
				isFrame = true;

			} else if (s.equals("All Tables Info")) {
				f_title = "All Table Privilege Information";
				vp = new TableMetaInfoPanel(3);
				variableQ = true;
				isFrame = true;
			} else if (s.equals("Table Info")) {
				f_title = " Table Privilege Information";
				s31 = JOptionPane.showInputDialog(null, "Enter Table Pattern:",
						"Table Input Dialog", -1);
				if (s31 == null || s31.compareTo("") == 0)
					return;

				rtm__ = new DBMetaInfo().getTablePrivilege(s31);
				rt__ = new ReportTable(rtm__);

				summary_info = true;
				isFrame = true;

			} else if (s.equals("Column Info")) {
				f_title = "Column Privilege Information";
				s31 = JOptionPane.showInputDialog(null, "Enter Table Pattern:",
						"Table Input Dialog", -1);
				if (s31 == null || s31.compareTo("") == 0)
					return;
				rtm__ = new DBMetaInfo().getColumnPrivilege(s31);
				rt__ = new ReportTable(rtm__);

				summary_info = true;
				isFrame = true;
			} else if (s.equals("Data Info")) {
				f_title = "Data Summary Information";
				vp = new TableMetaInfoPanel(4);
				variableQ = true;
				isFrame = true;
			} else {
				return;
			}
			Rdbms_conn.closeConn();
			RelationPanel relationpanel;
			if (isFrame) {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JFrame jframe = new JFrame(f_title);
				jframe.setDefaultCloseOperation(2);
				if (summary_info)
					jframe.setContentPane(rt__);
				else if (variableQ)
					jframe.setContentPane(vp);
				jframe.setLocation(100, 100);
				jframe.pack();
				jframe.setVisible(true);
			} else {
				relationpanel = new RelationPanel(hashtable1, hashtable,
						hashtable2);
			}
		} catch (SQLException sqlexception) {
			ConsoleFrame
					.addText("\n WARNING: SQL Exception in DBInfo Menu call ");
			JOptionPane.showMessageDialog(null, sqlexception.getMessage(),
					"Error Message", 0);
		} catch (NullPointerException nullpointerexception) {
			ConsoleFrame
					.addText("\n WARNING: Null Pointer Exception in DBInfo Menu call ");
			ConsoleFrame.addText("\n Message: "
					+ nullpointerexception.getMessage());
		} catch (UnsupportedOperationException unsupportedoperationexception) {
			ConsoleFrame
					.addText("\n WARNING: This operation is not supported on this database");
		} catch (Exception exception) {
			ConsoleFrame.addText("\n WARNING: Unknown Exception Happened");
			ConsoleFrame.addText("\n Message: " + exception.getMessage());
			exception.printStackTrace();
			JOptionPane.showMessageDialog(null, exception.getMessage(),
					"Error Message", 0);
		} finally {
			if (src_ != null)
				src_.getTopLevelAncestor()
						.setCursor(
								java.awt.Cursor
										.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
		}
		return;
	}

}
