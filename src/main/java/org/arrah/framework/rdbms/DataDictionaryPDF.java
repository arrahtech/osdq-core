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
		_tableN = Rdbms_conn.getTable();
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
	
	// Data Dictionary PDF
	public void createDDPDF(File pdfFile) throws FileNotFoundException, DocumentException, SQLException {

		
		Document document=new Document();
		PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
		document.open();
		addTitlePage(document);

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
			pdfTable.setWidthPercentage(100.00f);
			document.add(pdfTable);
			addEmptyLine(document,1);
		
			comment = new Paragraph("Data Information");
			comment.setAlignment(Element.ALIGN_LEFT);
			document.add(comment);
			addEmptyLine(document,1);
			pdfTable = getTableData(i);
			pdfTable.setWidthPercentage(100.00f);
			document.add(pdfTable);
			addEmptyLine(document,1);

			comment = new Paragraph("PK-FK Information");
			comment.setAlignment(Element.ALIGN_LEFT);
			document.add(comment);
			addEmptyLine(document,1);
			pdfTable = getTableKey(_tableN.get(i));
			pdfTable.setWidthPercentage(100.00f);
			document.add(pdfTable);
			addEmptyLine(document,1);
			
			comment = new Paragraph("Index Information");
			comment.setAlignment(Element.ALIGN_LEFT);
			document.add(comment);
			addEmptyLine(document,1);
			pdfTable = getTableIndex(i);
			pdfTable.setWidthPercentage(100.00f);
			document.add(pdfTable);
			addEmptyLine(document,1);
		}
		Paragraph proc = new Paragraph("Procedure Information");
		proc.setAlignment(Element.ALIGN_LEFT);
	    document.add(proc);
	    addEmptyLine(document, 2);
	    
	    PdfPTable pdfTable = getDBProcedure();
	    pdfTable.setWidthPercentage(100.00f);
	    document.add(pdfTable);
		addEmptyLine(document,2);
		
		Paragraph param = new Paragraph("Parameter Information");
		param.setAlignment(Element.ALIGN_LEFT);
		pdfTable.setWidthPercentage(100.00f);
	    document.add(param);
	    addEmptyLine(document, 2);
	    
	    pdfTable = getDBParameter();
	    document.add(pdfTable);
		addEmptyLine(document,2);
		
		Paragraph eoDoc = new Paragraph("End of Document");
		eoDoc.setAlignment(Element.ALIGN_CENTER);
	    document.add(eoDoc);
		 
		document.close();
		System.out.println("\n Data Dictionary File saved successfully");
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
