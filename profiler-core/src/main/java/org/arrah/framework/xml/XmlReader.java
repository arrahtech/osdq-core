package org.arrah.framework.xml;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.arrah.framework.ndtable.ReportTableModel;

/**
 * Read an XML file and create a report table
 * 
 * @author jchamblee
 */
public class XmlReader {
	 
	
	 String colNames ="";
	/**
	 * Read an XML file and create a report table
	 * 
	 * @param file
	 *            XML file to be read
	 * @return report table to be displayed
	 */
    
        // getRulesName() method added by Dareppa B
    
        public String[] getRulesName( File file, String rule, String ruleName ) {
            if( file.exists() && file.length() > 0 ) {
                parseDocument(file);
                NodeList nList = document.getElementsByTagName(rule);
                nodeList = new String[nList.getLength()];
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        nodeList[temp] = eElement.getElementsByTagName(ruleName).item(0).getTextContent().trim();
                    }
                }
            }
            return nodeList;
        }
        
        public String[] getDBNames() {
            File file = new File(FilePaths.getConnFilePath());
            if( file.exists() && file.length() > 0 ) {
                parseDocument(file);
                NodeList nList = document.getElementsByTagName("entry");
                nodeList = new String[nList.getLength()];
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        nodeList[temp] = eElement.getElementsByTagName("database_ConnectionName").item(0).getTextContent().trim();
                    }
                }
            }
            return nodeList;
        }
        
        // getDatabaseDetails() method added by Dareppa B
        public Hashtable<String, String> getDatabaseDetails( File file, String fileEntry, String connName ) {
            if( file.exists() && file.length() > 0 ) {
                parseDocument(file);
                NodeList nList = document.getElementsByTagName(fileEntry);
                nodeList = new String[nList.getLength()];
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if( eElement.getElementsByTagName("database_ConnectionName").item(0).getTextContent().trim().equals(connName) ) {
                            hashTable = new Hashtable<String, String>();
                            hashTable.put("Database_Type", eElement.getElementsByTagName("database_Type").item(0).getTextContent().trim());
                            hashTable.put("Database_DSN", eElement.getElementsByTagName("database_DSN").item(0).getTextContent().trim());
                            hashTable.put("Database_Protocol", eElement.getElementsByTagName("database_Protocol").item(0).getTextContent().trim());
                            hashTable.put("Database_Driver", eElement.getElementsByTagName("database_Driver").item(0).getTextContent().trim());
                            hashTable.put("Database_User", eElement.getElementsByTagName("database_User").item(0).getTextContent().trim());
                            hashTable.put("Database_Passwd", eElement.getElementsByTagName("database_Passwd").item(0).getTextContent().trim());
                            hashTable.put("Database_JDBC", eElement.getElementsByTagName("database_JDBC").item(0).getTextContent().trim());
                            hashTable.put("Database_ConnName", eElement.getElementsByTagName("database_ConnectionName").item(0).getTextContent().trim());
                        }
                    }
                }
            }
            return hashTable;
        }
        
        public Hashtable<String, String> getRuleDetails( File file, String ruleEntry, String ruleName ) {
            if( file.exists() && file.length() > 0 ) {
                parseDocument(file);
                NodeList nList = document.getElementsByTagName(ruleEntry);
                nodeList = new String[nList.getLength()];
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if( eElement.getElementsByTagName("rule_Name").item(0).getTextContent().trim().equals(ruleName) ) {
                            hashRule = new Hashtable<String, String>();
                            hashRule.put("rule_Name", eElement.getElementsByTagName("rule_Name").item(0).getTextContent().trim());
                            hashRule.put("database_ConnectionName", eElement.getElementsByTagName("database_ConnectionName").item(0).getTextContent().trim());
                            hashRule.put("rule_Type", eElement.getElementsByTagName("rule_Type").item(0).getTextContent().trim());
                            hashRule.put("table_Names", eElement.getElementsByTagName("table_Names").item(0).getTextContent().trim());
                            hashRule.put("column_Names", eElement.getElementsByTagName("column_Names").item(0).getTextContent().trim());
                            hashRule.put("condition_Names", eElement.getElementsByTagName("condition_Names").item(0).getTextContent().trim());
                            hashRule.put("join_Name", eElement.getElementsByTagName("join_Name").item(0).getTextContent().trim());
                            //hashRule.put("rule_Description", eElement.getElementsByTagName("rule_Description").item(0).getTextContent().trim());
                        }
                    }
                }
            }
            return hashRule;
        }
     
        public String getColumnNames( File file, String ruleName) {
        	 parseDocument(file);
        	
        	 NodeList nodes = document.getElementsByTagName("rule");
        	
        	 for (int i = 0; i < nodes.getLength(); i++) {
        	 Node node = nodes.item(i);

        	// if (node.getNodeType() == Node.ELEMENT_NODE) {
        	 Element element = (Element) node;
        	 if(ruleName.equals(getValue("rule_Name", element))){
        	 colNames=getValue("column_Names", element);
        	 }
        	 else{
        		 
        	 }
        	
        	 }
        	// }
        	 return colNames;
        	 }
        
        	 private static String getValue(String tag, Element element) {
        		 NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        		 Node node = (Node) nodes.item(0);
        		 if (node == null) return "";
        		 return node.getNodeValue();
        		 }


        		
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
					if (firstElement == null )continue; // vivek
					final NodeList dataValueNode = firstElement.getChildNodes();
					if (dataValueNode == null )continue; // vivek
					
					final Node firstItem = (Node) dataValueNode.item(0);
					if (firstItem == null) {
						rowdata[colIndex] = null;
					} else {
						
						rowdata[colIndex] = firstItem.getNodeValue();
					}
				}
				reportTable.addFillRow(rowdata);
			}
		} // End of rowIndex
	}
	
	

	private static final String DTD_ERRORS = "resource/DTD_Errors.txt";

	private String[] headings = null;
    private String[] nodeList = null;
	private ReportTableModel reportTable = null;
	private Document document = null;
    private Hashtable<String, String> hashTable = null, hashRule = null;

}
