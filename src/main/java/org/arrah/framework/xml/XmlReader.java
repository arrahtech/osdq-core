package org.arrah.framework.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.arrah.framework.ndtable.ReportTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Read an XML file and create a report table
 * 
 * @author jchamblee
 */
public class XmlReader {
	/**
	 * Read an XML file and create a report table
	 * 
	 * @param file
	 *            XML file to be read
	 * @return report table to be displayed
	 */
	public ReportTableModel read(final File file) {
		final DOMValidator domValidator = new DOMValidator();
		domValidator.setErrorFile(new File(DTD_ERRORS));
		domValidator.validate(file);
		if (domValidator.getErrorCount() > 0) {
			System.out.println("\n " + domValidator.getErrorCount()
					+ " DTD errors found, see " + DTD_ERRORS);
		}
		parseDocument(file);
		getHeadings();
		getRowData();
		return reportTable;
	}

	/**
	 * Parse the document
	 * 
	 * @param file
	 *            XML file to be read
	 */
	private void parseDocument(final File file) {
		try {
			final DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			final DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			document = docBuilder.parse(file);
			document.getDocumentElement().normalize();
		} catch (SAXException exc) {
			System.out.println("\n XmlReader error:" + exc.getMessage());
		} catch (ParserConfigurationException exc) {
			System.out.println("\n XmlReader error:" + exc.getMessage());
		} catch (IOException exc) {
			System.out.println("\n XmlReader error:" + exc.getMessage());
		}
	}

	/**
	 * Gets the column headings
	 */
	private void getHeadings() {
		if (document == null)
			return;
		final NodeList headerList = document.getElementsByTagName("header");
		final Node firstNode = headerList.item(0);
		if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
			final Element element = (Element) firstNode;
			final NodeList nodeList = element
					.getElementsByTagName("columnName");
			headings = new String[nodeList.getLength()];
			for (int index = 0; index < nodeList.getLength(); index++) {
				final Element columnName = (Element) nodeList.item(index);
				final NodeList columnNameNode = columnName.getChildNodes();
				final Node firstItem = (Node) columnNameNode.item(0);
				if (firstItem == null) {
					headings[index] = null;
				} else {
					headings[index] = firstItem.getNodeValue();
				}
			}
			reportTable = new ReportTableModel(headings, true, true);
		}
	}

	/**
	 * Gets the row data of the table
	 */
	private void getRowData() {
		if (document == null)
			return;
		final NodeList rowNodeList = document.getElementsByTagName("row");
		for (int rowIndex = 0; rowIndex < rowNodeList.getLength(); rowIndex++) {
			final Node node = rowNodeList.item(rowIndex);
			if (node.getNodeType() == Node.ELEMENT_NODE && headings != null) {
				String[] rowdata = new String[headings.length];
				for (int colIndex = 0; colIndex < headings.length; colIndex++) {
					final Element dataValueElement = (Element) node;
					final NodeList dataValueNodeList = dataValueElement
							.getElementsByTagName(headings[colIndex]);
					final Element firstElement = (Element) dataValueNodeList
							.item(0);
					final NodeList dataValueNode = firstElement.getChildNodes();
					final Node firstItem = (Node) dataValueNode.item(0);
					if (firstItem == null) {
						rowdata[colIndex] = null;
					} else {
						// System.out.println(headings[colIndex] + "\t" +
						// firstItem.getNodeValue());
						rowdata[colIndex] = firstItem.getNodeValue();
					}
				}
				reportTable.addFillRow(rowdata);
			}
		}
	}

	private static final String DTD_ERRORS = "DTD_Errors.txt";

	private String[] headings = null;
	private ReportTableModel reportTable = null;
	private Document document = null;
}
