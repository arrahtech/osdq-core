package org.arrah.framework.rdbms;
/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *              2013                           *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This class is used for creating data dictionary
 * in pdf format.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import org.arrah.framework.ndtable.RTMUtil;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.profile.DBMetaInfo;
import org.arrah.framework.profile.TableMetaInfo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class DataDictionaryPDF {
	private Vector<String> _tableN = new Vector<String>();
	
	public DataDictionaryPDF() {
		_tableN = Rdbms_NewConn.get().getTable();
	}
	
	public PdfPTable getTableMetaData(int tabIndex) {
		ReportTableModel reporttable = null; 
		reporttable = TableMetaInfo.populateTable(2, tabIndex, tabIndex+1, reporttable);
		PdfPTable pdfTable = RTMUtil.createPDFTable(reporttable);
		return pdfTable;
	}
	
	public PdfPTable getTableData(int tabIndex) {
		ReportTableModel reporttable = null; 
		reporttable = TableMetaInfo.populateTable(4, tabIndex, tabIndex+1, reporttable);
		PdfPTable pdfTable = RTMUtil.createPDFTable(reporttable);
		return pdfTable;
	}
	
	public PdfPTable getTableIndex(int tabIndex) {
		ReportTableModel reporttable = null; 
		reporttable = TableMetaInfo.populateTable(1, tabIndex, tabIndex+1, reporttable);
		PdfPTable pdfTable = RTMUtil.createPDFTable(reporttable);
		return pdfTable;
	}
	
	public PdfPTable getTableKey(String table) throws SQLException {
		ReportTableModel reporttable = null; 
		reporttable = TableMetaInfo.tableKeyInfo(table);
		PdfPTable pdfTable = RTMUtil.createPDFTable(reporttable);
		return pdfTable;
	}
	
	public PdfPTable getDBParameter() throws SQLException {
		ReportTableModel reporttable = null; 
		DBMetaInfo dbmeta = new DBMetaInfo();
		reporttable = dbmeta.getParameterInfo();
		PdfPTable pdfTable = RTMUtil.createPDFTable(reporttable);
		return pdfTable;
	}
	
	public PdfPTable getDBProcedure() throws SQLException {
		ReportTableModel reporttable = null; 
		DBMetaInfo dbmeta = new DBMetaInfo();
		reporttable = dbmeta.getProcedureInfo();
		PdfPTable pdfTable = RTMUtil.createPDFTable(reporttable);
		return pdfTable;
	}
	
	public static void createPDFfromRTM(File pdfFile, ReportTableModel rtm) throws FileNotFoundException, DocumentException {
		Document document=new Document();
		PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
		document.open();
		
		PdfPTable pdftable = RTMUtil.createPDFTable(rtm);
		document.add(pdftable);
		document.close();
	}
	
	public void createDDPDF(OutputStream output) throws FileNotFoundException,
			DocumentException, SQLException {
			Document document = new Document();
			PdfWriter.getInstance(document, output);
			document.open();
			addTitlePage(document);
			createPDFBody(document);
	}
	//Function to create PDF Body
		public void createPDFBody(Document document) throws DocumentException,
				SQLException {
			try {
			for (int i=0; i < _tableN.size(); i++) {
				Paragraph comment = new Paragraph((i+1)+"."+_tableN.get(i));
				comment.setAlignment(Element.ALIGN_LEFT);
				document.add(comment);
				addEmptyLine(document,1);
				
				comment = new Paragraph("Metadata Information");
				comment.setAlignment(Element.ALIGN_LEFT);
				document.add(comment);
				addEmptyLine(document,1);
				PdfPTable pdfTable = getTableMetaData(i);
				if (pdfTable == null ) continue;
				pdfTable.setWidthPercentage(100.00f);
				document.add(pdfTable);
				addEmptyLine(document,1);
			
				comment = new Paragraph("Data Information");
				comment.setAlignment(Element.ALIGN_LEFT);
				document.add(comment);
				addEmptyLine(document,1);
				pdfTable = getTableData(i);
				if (pdfTable == null ) continue;
				pdfTable.setWidthPercentage(100.00f);
				document.add(pdfTable);
				addEmptyLine(document,1);

				try {
					comment = new Paragraph("PK-FK Information");
					comment.setAlignment(Element.ALIGN_LEFT);
					document.add(comment);
					addEmptyLine(document,1);
					pdfTable = getTableKey(_tableN.get(i));
					pdfTable.setWidthPercentage(100.00f);
					document.add(pdfTable);
					addEmptyLine(document,1);
				} catch (SQLException sqlex) {
					System.out.println("\n Method getTableKey Not Supported.");
					// Do nothing
				}
				
				comment = new Paragraph("Index Information");
				comment.setAlignment(Element.ALIGN_LEFT);
				document.add(comment);
				addEmptyLine(document,1);
				pdfTable = getTableIndex(i);
				if (pdfTable == null ) continue;
				pdfTable.setWidthPercentage(100.00f);
				document.add(pdfTable);
				addEmptyLine(document,1);
			}
			Paragraph proc = new Paragraph("Procedure Information");
			proc.setAlignment(Element.ALIGN_LEFT);
		    document.add(proc);
		    addEmptyLine(document, 2);
		    PdfPTable pdfTable = null;
		    
		    try {
		    	pdfTable = getDBProcedure();
		    	if (pdfTable != null) {
		    		pdfTable.setWidthPercentage(100.00f);
		    		document.add(pdfTable);
		    		addEmptyLine(document,2);
		    	}
		    } catch (SQLException sqlexp) {
		    	System.out.println("\n Method getDBProcedure Not Supported.");
				// Do nothing
		    }

			Paragraph param = new Paragraph("Parameter Information");
			param.setAlignment(Element.ALIGN_LEFT);
		    document.add(param);
		    addEmptyLine(document, 2);
			try {		   
		    	pdfTable = getDBParameter();
		    	if (pdfTable != null ) {
		    		pdfTable.setWidthPercentage(100.00f);
		    		document.add(pdfTable);
		    		addEmptyLine(document,2);
		    	}
		    } catch (SQLException sqlexp) {
		    	System.out.println("\n Method getDBParameter Not Supported.");
				// Do nothing
		    }
			
			Paragraph eoDoc = new Paragraph("End of Document");
			eoDoc.setAlignment(Element.ALIGN_CENTER);
		    document.add(eoDoc);
			 
			document.close();
			System.out.println("\n Data Dictionary File saved successfully");
			} catch (Exception e) {
				document.close();
				System.out.println("\n Data Dictionary File closed Abnormally");
				try {
					throw e;
				} catch (Exception e1) {
					// do nothing
				}
			}
			finally {
				document.close();
			}
		}

	
	// Data Dictionary PDF
	public void createDDPDF(File pdfFile) throws FileNotFoundException, DocumentException, SQLException {

		
		Document document=new Document();
		PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
		document.open();
		addTitlePage(document);
		createPDFBody(document);

	} // End of createDDPDF
	
	 private void addEmptyLine(Document doc, int number) throws DocumentException {
		    for (int i = 0; i < number; i++) {
		      doc.add(new Paragraph(" "));
		    }
	 }
	 
	 private void addTitlePage(Document document) throws DocumentException {
		 
		 	addEmptyLine(document, 5);
		    
		    Paragraph title = new Paragraph("Data Dictionary by Arrah technology");
		    title.setAlignment(Element.ALIGN_CENTER);
		    document.add(title);
		    addEmptyLine(document, 1);
		    
		    Paragraph url = new Paragraph("http://sourceforge.net/projects/dataquality/");
		    url.setAlignment(Element.ALIGN_CENTER);
		    document.add(url);
		    addEmptyLine(document, 3);
		    
		    Paragraph rtime = new Paragraph("Report generated on: " +  new Date());
		    rtime.setAlignment(Element.ALIGN_CENTER);
		    document.add(rtime);

		    document.newPage();
		  }

	 public static Font getFont(final int size, final int style) {
		 
		 Font f = new Font() {
			 public float getSize(){
				 return size;
			 }
			 public int getStyle() {
				 return style;
			 }
		 } ;
		 
		 return f;
		 
	 }
}
