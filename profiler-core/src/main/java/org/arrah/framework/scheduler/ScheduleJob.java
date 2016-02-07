package org.arrah.framework.scheduler;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import org.arrah.framework.rdbms.Rdbms_NewConn;
import org.arrah.framework.xml.FilePaths;
import org.arrah.framework.xml.XmlReader;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class ScheduleJob implements Job{
		
	static String query="";
	String columnNames ="";
	 ResultSetMetaData rsmd;
	 int count = 0;
	 Hashtable<String, String> hashTable, hashRule;
	 XmlReader xmlReader;
	 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	String dbConnName;
	private String jcbRule;
	
	// Get the input Query from BusinessRules.xml to schedule the job	
	File xmlFile=new File(FilePaths.getFilePathRules());
	
	
	public ScheduleJob(String text, Hashtable<String, String> hashValues, String jcbRule){
		query=text;
		this.hashTable = hashValues;
		this.jcbRule = jcbRule;
	}
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		String[] splittedColumns=null;
		String finalCols = null;
		String actualCols=null;
		ArrayList<String> colNames=new ArrayList<String>();
	    ResultSet rs = null;
		File file=new File(".\\scheduleoutput.csv");
		FileWriter fstream;
		 BufferedWriter out = null;
		    Date date = new Date();
		    String DATE_FORMAT = "dd_MM_yyyy_HH_mm_ss";
		    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		    String formatDate=sdf.format(date);

		 
		try {
			
			if(file.exists()){
				// If the file already exists, append it with the current date and time
				file=new File(file + "_" + formatDate + ".csv");
			}
			 fstream = new FileWriter(file);
			 out = new BufferedWriter(fstream);   
		} 
		catch ( IOException e1) {
			e1.printStackTrace();
		}  
        
		try {
			
			Rdbms_NewConn dbConn=new Rdbms_NewConn(hashTable);
			dbConn.openConn();
			XmlReader xmlReader=new XmlReader();
			columnNames=xmlReader.getColumnNames(xmlFile, jcbRule);
			
			 
			 if(columnNames != null && "".equals(columnNames) == false)
					rs = dbConn.execute(query);
					else {
						System.out.println("Column is Empty");
						throw new JobExecutionException("Column is Empty");
			}
			 
			// Check wether the input Query is null
			 if(query != null )
				rs = dbConn.execute(query);
				else {
					System.out.println("Query is null");
					throw new JobExecutionException("Query is null");
			}
								
			 if(columnNames.contains(",")){
				    splittedColumns=columnNames.split(",");
				if(splittedColumns.length>1)	
				for (int i=0; i< splittedColumns.length ; i++) {
					 actualCols=splittedColumns[i];
					if(actualCols.contains(".")){
						finalCols=actualCols.substring(actualCols.indexOf(".") + 1);
						colNames.add(finalCols.trim());
					}
					else
						colNames.add(splittedColumns[i].trim());			
				 }
				 
				}	
				else if(columnNames.contains(".")){
						finalCols=columnNames.substring(columnNames.indexOf(".") + 1);
						colNames.add(finalCols.trim());	
				}
				else
					colNames.add(columnNames.trim());	
				 	while (rs.next()) {
					for(int j=0;j<colNames.size();j++){
						//Get the output from ResultSet and write to a file
						out.append(rs.getString(colNames.get(j))).append(",");
					  					
					}
					out.write("\n");
					
		            }
			
				 	System.out.println("Job executed");	
				 		
			
		}  catch (SQLException e) {
			System.out.println("Job failed because of SQL Exception" + e);
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("Job failed with an exception" + e);
			e.printStackTrace();
		}
		finally{
			try {
				out.close();
				System.out.println("File Saved at:"+file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
	
		
}
	
}

