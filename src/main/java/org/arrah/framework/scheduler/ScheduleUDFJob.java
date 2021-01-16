package org.arrah.framework.scheduler;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;

import org.arrah.framework.ndtable.DisplayFileAsTableCore;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.udf.UDFInterfaceToRTM;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class ScheduleUDFJob implements Job{
		
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	// Get the input Query from BusinessRules.xml to schedule the job	
	String ruleName = context.getJobDetail().getJobDataMap().getString("jcbRule");
	File file=new File("./udfscheduleoutput_"+ruleName+".csv");
	
    Date date = new Date();
    String DATE_FORMAT = "dd_MM_yyyy_HH_mm_ss";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    String formatDate=sdf.format(date);

		 
		if(file.exists()){
			// If the file already exists, append it with the current date and time
			file=new File(file + "_" + formatDate + ".csv");
		}  
        
		try {
			
		  //legacy code, converting hashmap into hashtable
		  Hashtable<String, String> hashtable = new Hashtable<>();
		  context.getJobDetail().getJobDataMap().entrySet().stream().forEach((e) -> {hashtable.put(e.getKey(), (String)e.getValue());});
		
		  
		  // Get the parameters from hashrule table
		  String selectedUDF = hashtable.get("rule_Type");
		  String fileLoc = hashtable.get("table_Names");
		  String inputCol = hashtable.get("column_Names");
		  
		  // read file in RTM
		  ReportTableModel rtm = new DisplayFileAsTableCore().loadFileIntoRTM(fileLoc);
				  
		  UDFInterfaceToRTM.evalUDF(selectedUDF, rtm, Arrays.asList(inputCol.split(",")) );
		  ReportTableModel rtmmodel = UDFInterfaceToRTM.metricrtm;
		  
		  // Save RTM to file
		  rtmmodel.saveAsOpenCSV(file.getAbsolutePath());
		
		  System.out.println("Job executed");	
		} 
		
		catch (Exception e) {
			System.out.println("UDF Scheduler Job failed with an exception" + e);
			e.printStackTrace();
		}
		
		finally{
			try {
				System.out.println("UDF Job Scheduler File Saved at:"+file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}  
		}		
}
	
}

