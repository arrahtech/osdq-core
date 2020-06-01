package org.arrah.framework.util;

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
 * This utility file parses the ascii value from
 * a file and shows in the UI
 * This file will also parse multiline file based
 * on record Separator and field separator
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.arrah.framework.ndtable.ReportTableModel;


public class AsciiParser {

		public static ArrayList<String> delims = new ArrayList<String>();
		
		/* This function will check ./ascii.txt file in same directory
		 * and parse it. If file not found it will set default limit value
		 */
	public void init(String fileName) throws IOException {
			
		//BufferedReader br = new BufferedReader(new InputStreamReader(
		// AsciiParser.class.getClassLoader().getResourceAsStream(fileName)));
		
		BufferedReader br = new BufferedReader(new  FileReader(fileName));
		 
		int ctr = 1; // Counter
		String sCurrentLine = null;
		String delimadd ="";
		
		while ((sCurrentLine = br.readLine()) != null) {
			
			if(ctr%5==4)
			{	
				delimadd = sCurrentLine + " : "; 
			}
			else if(ctr%5==0){
				delimadd = delimadd + sCurrentLine;
				delims.add(delimadd);
				delimadd = "";
			}
			ctr++;
		}
		br.close();
	}
		
		/* This function will read file and 
		 * return reporttablemodel
		 */
		public static ReportTableModel loadRecord ( File f, String rsp, String fsp, int noCol, boolean isEditable) {
			String [] colName = new String[noCol];
			
			for (int i=0 ; i < noCol; i++ )
				colName[i] = "ColName_"+i;
			
			ReportTableModel rtm = new ReportTableModel(colName,isEditable );
			String[] col  = new String[noCol];
			int validLine = 0;
			
			try {
				// record separator can be null or empty
				// but field separator can not be
				Scanner s = null;
				if (rsp == null || rsp.equals(""))
					 s = new Scanner(f).useDelimiter(fsp);
				else 
					 s = new Scanner(f).useDelimiter(rsp);
				
				if (rsp == null || rsp.equals("") ||fsp.equals(rsp) == true) { // Field Sep and Record Sep are same
					int index =0;
					while (s.hasNext()) {
						if (validLine > 100 && isEditable == false) // preview 100 clumns
							break;
						
						String record = s.next(); // Got record here
						if (index < (noCol -1) ) {
							col[index] = record;
							index++;
							continue;
						} else if (index == (noCol -1)) {
							col[index] = record;
							index = 0;
							rtm.addFillRow(col);;
							validLine++;
						}
					}
				} else {
				     while (s.hasNext()) {
				    	 if (validLine > 100 && isEditable == false) // preview 100 clumns
								break;
				          String record = s.next(); // Got record here
				          String[] newCol = record.split(fsp);
				          if (newCol.length == colName.length) {
				        	  rtm.addFillRow(newCol);
				        	  validLine++;
				          } else if (newCol.length > colName.length )  {
				        	  for (int i=0; i < col.length; i++ )
				        		  col[i] = newCol[i];
				        	  rtm.addFillRow(col);
				        	  validLine++;
				          } else {
				        	  for (int i=0; i < newCol.length; i++ )
				        		  col[i] = newCol[i];
				        	  for (int i=newCol.length; i < colName.length; i++ )
				        		  col[i] =""; // empty value if no of cols are less
				        	  rtm.addFillRow(col);
				        	  validLine++;
				          }  
				     }	
				} // Field sep and Record Sep are different
				
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found:" + e);
			}
			
			return rtm;
		}
		
	public static List<String> pullKeysFromFile(String filePath) {
			try {
				File keyfile = new File(filePath);
				Path path = Paths.get(keyfile.getPath());
				return  Files.readAllLines(path,StandardCharsets.ISO_8859_1);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("File Path:" +filePath );
				return null;
			}
	}
		
 } // End of AsciiParser
