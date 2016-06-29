package org.arrah.framework.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Description of the Class
 * 
 * @author jchamblee
 */
public class DOMValidator {
	/**
	 * The main program for the DOMValidator class
	 * 
	 * @param args
	 *            The command line arguments
	 */
	public static void main(final String[] args) {
		final DOMValidator domValidator = new DOMValidator();
		final File file = new File(args[0]);
		domValidator.validate(file);
	}

	/**
	 * Description of the Method
	 * 
	 * @param file
	 *            Description of the Parameter
	 */
	public void validate(final File file) {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(true);
			// Default is false
			final DocumentBuilder builder = factory.newDocumentBuilder();
			// final ErrorHandler handler = new
			// org.xml.sax.helpers.DefaultHandler();
			final DomValidatorErrorHandler handler = new DomValidatorErrorHandler();
			handler.setErrorFile(errorFile);
			builder.setErrorHandler(handler);
			final Document document = builder.parse(file);
			setErrorCount(handler.getErrorCount());
		} catch (ParserConfigurationException e) {
			System.out.println(e.toString());
		} catch (SAXException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * Sets the errorCount attribute of the DOMValidator object
	 * 
	 * @param count
	 *            The new errorCount value
	 */
	void setErrorCount(final int count) {
		this.errorCount = count;
	}

	/**
	 * Sets the errorFile attribute of the DOMValidator object
	 * 
	 * @param errorFile
	 *            The new errorFile value
	 */
	void setErrorFile(final File errorFile) {
		this.errorFile = errorFile;
	}

	/**
	 * Gets the errorCount attribute of the DOMValidator object
	 * 
	 * @return The errorCount value
	 */
	int getErrorCount() {
		return errorCount;
	}

	/**
	 * Description of the Class
	 * 
	 * @author jchamblee
	 */
	static class DomValidatorErrorHandler implements ErrorHandler {

		/**
		 * Description of the Method
		 * 
		 * @param exc
		 *            Description of the Parameter
		 * @exception SAXException
		 *                Description of the Exception
		 */
		public void error(final SAXParseException exc) throws SAXException {
			printStream.println("Error: ");
			printInfo(exc);
		}

		/**
		 * Description of the Method
		 * 
		 * @param exc
		 *            Description of the Parameter
		 * @exception SAXException
		 *                Description of the Exception
		 */
		public void fatalError(final SAXParseException exc) throws SAXException {
			printStream.println("Error: ");
			printInfo(exc);
		}

		/**
		 * Description of the Method
		 * 
		 * @param exc
		 *            Description of the Parameter
		 * @exception SAXException
		 *                Description of the Exception
		 */
		public void warning(final SAXParseException exc) throws SAXException {
			printStream.println("Warning: ");
			printInfo(exc);
		}

		/**
		 * Description of the Method
		 * 
		 * @param exc
		 *            Description of the Parameter
		 */
		private void printInfo(final SAXParseException exc) {
			printStream.println("   Public ID: " + exc.getPublicId());
			printStream.println("   System ID: " + exc.getSystemId());
			printStream.println("   Line number: " + exc.getLineNumber());
			printStream.println("   Column number: " + exc.getColumnNumber());
			printStream.println("   Message: " + exc.getMessage());
			errorCount++;
		}

		/**
		 * Sets the errorFile attribute of the DomValidatorErrorHandler object
		 * 
		 * @param errorFile
		 *            The new errorFile value
		 */
		void setErrorFile(final File errorFile) {
			if (errorFile != null) {
				try {
					printStream = new PrintStream(new FileOutputStream(
							errorFile));
				} catch (FileNotFoundException exc) {
					System.out.println(exc);
				}

			}
		}

		/**
		 * Gets the errorCount attribute of the DomValidatorErrorHandler object
		 * 
		 * @return The errorCount value
		 */
		int getErrorCount() {
			return errorCount;
		}

		private int errorCount = 0;
		private PrintStream printStream = System.out;
	}

	private int errorCount = 0;
	private File errorFile = null;
}
