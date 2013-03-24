package org.arrah.framework.xml;

import java.io.File;
import java.io.IOException;

import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The class used to write the row and column data of a JTable into an XML file
 * 
 * @author jchamblee
 */
public class XmlWriter {

	/**
	 * This method writes the JTable to an XML file.
	 * 
	 * @param jtable
	 *            a table of data
	 * @param fileName
	 *            filename
	 * @exception IOException
	 *                Description of the Exception
	 */
	public void writeXmlFile(final JTable jtable, final String fileName)
			throws IOException {
		final Document document = createDomDocument(jtable);
		assert document != null;
		writeDomDocument(document, fileName);
	}

	/**
	 * Create an XML document from a JTable
	 * 
	 * @param jtable
	 *            a table of data
	 * @return an XML document with the row and column data
	 */
	private Document createDomDocument(final JTable jtable) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		Document document = null;
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			final Element rootElement = document.createElement("table");
			rootElement.setAttribute("Name", "TableName");
			document.appendChild(rootElement);
			final Element columnNames = document.createElement("header");
			// Get column names
			for (int j = 0; j < jtable.getColumnCount(); j++) {
				final Element curElement = document.createElement("columnName");
				curElement.appendChild(document.createTextNode(jtable
						.getColumnName(j)));
				columnNames.appendChild(curElement);
			}
			rootElement.appendChild(columnNames);
			// Get row data
			for (int i = 0; i < jtable.getRowCount(); i++) {
				final Element rowElement = document.createElement("row");
				for (int j = 0; j < jtable.getColumnCount(); j++) {
					try {
						final Object cellValue = jtable.getValueAt(i, j);
						final String curValue = cellValue == null ? ""
								: cellValue.toString();
						final Element curElement = document
								.createElement(jtable.getColumnName(j));
						curElement.appendChild(document
								.createTextNode(curValue));
						rowElement.appendChild(curElement);
					} catch (DOMException exc) {
						if (exc.code == DOMException.INVALID_CHARACTER_ERR) {
							System.out.println("\nInvalid data: "
									+ jtable.getColumnName(j) + " " + " "
									+ exc.getMessage());
						} else {
							System.out
									.println("Unexpected error code of exception: "
											+ exc);
						}
					}
				}
				rootElement.appendChild(rowElement);
			}
			document.getDocumentElement().normalize();

		} catch (ParserConfigurationException exc) {
			System.out.println("\n XmlWriter error:" + exc.getMessage());
		}
		return document;
	}

	/**
	 * This method writes the document object to an xml file.
	 * 
	 * @param document
	 *            an XML document
	 * @param xmlFilename
	 *            XML file name
	 * @exception IOException
	 *                Description of the Exception
	 */
	private void writeDomDocument(final Document document,
			final String xmlFilename) throws IOException {
		try {
			final TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DTDGenerator dtdGenerator = new DTDGenerator();
			final File xmlFile = new File(xmlFilename);
			final String dtdFilename = dtdGenerator.getDtdFilename(xmlFile);
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "file:/"
					+ dtdFilename);
			final Source source = new DOMSource(document);
			final Result dest = new StreamResult(xmlFile);
			transformer.transform(source, dest);
		} catch (TransformerConfigurationException exp) {
			System.err.println(exp.toString());
		} catch (TransformerException exp) {
			System.err.println(exp.toString());
		}
	}
}
