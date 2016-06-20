package org.arrah.framework.scheduler;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.arrah.framework.rdbms.Rdbms_NewConn;
import org.arrah.framework.xml.FilePaths;
import org.arrah.framework.xml.XmlReader;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class ScheduleJob implements Job{
		
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	  // Get the input Query from BusinessRules.xml to schedule the job	
	  File xmlFile=new File(FilePaths.getFilePathRules());
	  
		String[] splittedColumns=null;
		String finalCols = null;
		String actualCols=null;
		ArrayList<String> colNames=new ArrayList<String>();
	    ResultSet rs = null;
		File file=new File("./scheduleoutput.csv");
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
			
		  //legacy code, converting hashmap into hashtable
		  Hashtable<String, String> hashtable = new Hashtable<>();
		  context.getJobDetail().getJobDataMap().entrySet().stream().forEach((e) -> {hashtable.put(e.getKey(), (String)e.getValue());});
			Rdbms_NewConn dbConn=new Rdbms_NewConn(hashtable);
			dbConn.openConn();
			XmlReader xmlReader=new XmlReader();
			 String columnNames = null;

			columnNames=xmlReader.getColumnNames(xmlFile, context.getJobDetail().getJobDataMap().getString("jcbRule"));
			
			String query = context.getJobDetail().getJobDataMap().getString("query");
			 
			 if(columnNames != null && "".equals(columnNames) == false)
					rs = dbConn.execute(query);
					else {
						System.out.println("Column is Empty");
						throw new JobExecutionException("Column is Empty");
			}
			 
			// Check whether the input Query is null
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

