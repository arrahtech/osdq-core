package org.arrah.framework.scheduler;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.arrah.framework.rdbms.Rdbms_NewConn;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class ScheduleSQLJob implements Job{
		
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	// Get the input Query from BusinessRules.xml to schedule the job	
	// add rulename to file to make
	
	String ruleName = context.getJobDetail().getJobDataMap().getString("jcbRule");
    ResultSet rs = null;
	File file=new File("./sqlscheduleoutput_"+ruleName+".csv");
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
			
			String query = context.getJobDetail().getJobDataMap().getString("query");
			
			// Check whether the input Query is null
			 if(query != null )
				rs = dbConn.execute(query);
				else {
					System.out.println("Query is null");
					throw new JobExecutionException("Query is null");
				}
			 
			 ResultSetMetaData rsmd = rs.getMetaData();
			 int numberOfColumns = rsmd.getColumnCount();
			
			 while (rs.next()) {
				for(int j=0;j<numberOfColumns;j++){
					//Get the output from ResultSet and write to a file
					out.append(rs.getString(rsmd.getColumnName(j+1))).append(","); // Column name does not start with 0				
				}
				out.write("\n");
	         }
		
			 System.out.println("SQL Scheduler Job executed");	
		}  catch (SQLException e) {
			System.out.println("Job failed because of SQL Exception" + e);
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("SQL Scheduler Job failed with an exception" + e);
			e.printStackTrace();
		}
		finally{
			try {
				out.close();
				System.out.println("SQL Scheduler File Saved at:"+file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}		
}
	
}

