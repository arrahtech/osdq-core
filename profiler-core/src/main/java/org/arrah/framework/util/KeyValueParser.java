package org.arrah.framework.util;

import java.io.BufferedReader;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This utility file parses the key-value file 
 * and returns parameter in hashtable. Value must be
 * within quotes. ("Value")
 * Key="Value"
 */

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.Enumeration;
import java.util.Hashtable;

public class KeyValueParser {
	private static int token;
	private static int key = 0; // for toggling between Key-Val Pair
	private static String str_key = "", str_value = "";

	public static Hashtable<String, String> parseFile(String filename) {
		Hashtable<String, String> _hash = new Hashtable<String, String>();
		try {

			// Create the tokenizer to read from a file
		  InputStreamReader inputStreamReader = new InputStreamReader(KeyValueParser.class.getResourceAsStream(filename));
		  BufferedReader rd = new BufferedReader(inputStreamReader);
		  //FileReader rd = new FileReader(filename);
			StreamTokenizer st = new StreamTokenizer(rd);

			// Prepare the tokenizer for Java-style tokenizing rules
			st.wordChars('_', '_');
			// Treat numbers as char
			st.ordinaryChars('0', '9');
			st.wordChars('0', '9');

			// These calls caused comments to be discarded
			st.slashSlashComments(true);
			st.slashStarComments(true);
			st.commentChar('#');

			// Parse the file
			while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
				switch (token) {

				case StreamTokenizer.TT_NUMBER:
					// A number was found; the value is in nval
					@SuppressWarnings("unused")
					double num = st.nval;
					break;

				case StreamTokenizer.TT_WORD:
					// A word was found; the value is in sval - Key may have
					// space
					if (key == 0) {
						if (str_key.compareTo("") == 0)
							str_key = st.sval;
						else
							str_key += " " + st.sval;
					}
					break;

				case '"':
					// A double-quoted string was found; sval contains the
					// contents
					if (key == 1) {
						str_value = st.sval;
						key--;
					}
					break;
				case '\'':
					// A single-quoted string was found; sval contains the
					// contents
					@SuppressWarnings("unused")
					String squoteVal = st.sval;
					break;
				case StreamTokenizer.TT_EOL:
					// End of line character found
					break;
				case StreamTokenizer.TT_EOF:
					// End of file has been reached
					break;
				default:
					// A regular character was found; the value is the token
					// itself
					char ch = (char) st.ttype;
					if (key == 0) {
						if (ch == '=')
							key = 1;
						else
							str_key += ch;
					} else
						str_value += ch;

					break;
				}
				// Fill value in Hashtable
				if (str_key != "" && str_value != "") {
					_hash.put(str_key, str_value);
					str_key = "";
					str_value = "";
				}
			}
			rd.close();
			return _hash;
		} catch (IOException e) {
			System.out.println("\n IO Exception happened:" + filename);
			System.out.println(e.getMessage());

		}
		return _hash;
	}
	public static  boolean saveTextFile(String filename,Hashtable<String, String> __h ) {
		try {

			// Create the tokenizer to read from a file
			BufferedWriter wt = new BufferedWriter( new FileWriter(filename) );
			Enumeration<String> enum1 = __h.keys();
			while (enum1.hasMoreElements()) {
				String key_n = (String) enum1.nextElement();
				String val_n = __h.get(key_n);
				wt.write(key_n+"="+"\""+val_n+"\"");
				wt.newLine();
			}
			wt.flush();
			wt.close();
		} catch  (IOException e) {
			System.out.println("\n IO Exception happened:" + filename);
			System.out.println(e.getMessage());

		}
		
		return true;
	}

	public static void main(String[] args) {
		Hashtable<String, String> key_val = parseFile(args[0]);
		Enumeration<String> e = key_val.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			System.out.println(key + " = " + key_val.get(key));
		}
	}
}
