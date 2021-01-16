package org.arrah.framework.scheduler;


import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzScheduler{

String quer;
int hours, minutes, seconds;
JobKey jk;
Date d1=new Date(); 
int dayofWeek, dayOfMonth;
CronTrigger trigger1; 
SimpleTrigger trigger;
Date date=new Date();

private final String jcbSfrequency;
private final String jcbFrequency;
private final Date jdcEdate;
private final int startdayofMonth;
private final Hashtable<String, String> hashtable;
private final Hashtable<String, String> hashrule;
private final String jcbRule;

	public QuartzScheduler(String text, int hour, int minute, int second, 
	    String jcbSfrequency, 
	    String jcbFrequency, 
	    Date jdcEdate, 
	    int startdayofMonth, 
	    Hashtable<String, String> hashtableDBInfo, 
	    Hashtable<String, String> hashtableRuleInfo,
	    String jcbRule) throws SchedulerException, InterruptedException{
		
		hours=hour;
		minutes=minute;
		seconds=second;
		quer=text;
		this.jcbSfrequency = jcbSfrequency;
		this.jcbFrequency = jcbFrequency;
		this.jdcEdate = jdcEdate;
		this.startdayofMonth = startdayofMonth;
		this.hashtable = hashtableDBInfo;
		this.hashrule = hashtableRuleInfo;
		this.jcbRule = jcbRule;
		try {
			task();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param args
	 * @throws SchedulerException 
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws ClassNotFoundException 
	 */
	 public void task() throws SchedulerException, InterruptedException, ParseException{
			
			SchedulerFactory sf = new StdSchedulerFactory(); 
		
			Scheduler sched = sf.getScheduler();
			
			
		
			// Tell quartz to schedule the job using our trigger
			
			String key="ExecuteJob", value="Report Generation" +jcbRule ;
			JobKey jobKey = new JobKey(key, value);
			
			JobDataMap jobDataMap = null;
			JobDetail job = null;
			
			if (hashtable != null || hashtable.isEmpty() == false) {
			
				jobDataMap = new JobDataMap(hashtable);
							
				// here it get the job that needs Scheduling
				job = newJob(ScheduleSQLJob.class)
						.withIdentity(jobKey).usingJobData(jobDataMap).usingJobData("query", quer).usingJobData("jcbRule", jcbRule)
						.build();
			} else {
				
				jobDataMap = new JobDataMap(hashrule);
				job = newJob(ScheduleUDFJob.class)
						.withIdentity(jobKey).usingJobData(jobDataMap).usingJobData("jcbRule", jcbRule)
						.build();
			}
			
			// This logic applies if the user want to schedule on a one-time basis
			if(jcbFrequency.equals("One Time")){
			      
				  java.util.Calendar cal = new java.util.GregorianCalendar();
				  cal.set(Calendar.HOUR_OF_DAY, hours);
				  cal.set(Calendar.MINUTE, minutes);
				  cal.set(Calendar.SECOND, seconds);
				  
				 				  
				  Date startTime = cal.getTime();
				 
				  
				  trigger = (SimpleTrigger) newTrigger() 
						    .withIdentity("trigger", value)
						    .startAt(startTime) 
						    .build();
                 
				  sched.scheduleJob(job, trigger);
					
			}
						
			// This logic applies if the user want to schedule on a daily basis
			if(jcbFrequency.equals("Daily")){
			 trigger1 = newTrigger()
				    .withIdentity("trigger1", "Report Generation3")
				    .withSchedule(dailyAtHourAndMinute(hours, minutes)) 
				    .build();
			}
			
			// This logic applies if the user want to schedule on a weekly basis
			else if(jcbFrequency.equals("Weekly")){
				dayoftheWeek();
				trigger1 = newTrigger()
					    .withIdentity("trigger1", "Report Generation3")
					    .withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(dayofWeek, hours, minutes))
					    .build();
			}
			
			// This logic applies if the user want to schedule on a Monthly basis
			else if(jcbFrequency.equals("Monthly")){
				trigger1 = newTrigger()
					    .withIdentity("trigger1", "Report Generation3")
					    .withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(startdayofMonth, hours, minutes))
					    .endAt(jdcEdate)
					    .build();
				}
			         if (trigger1 !=null)
				     sched.scheduleJob(job, trigger1);
			 
			    	 
			//Start the scheduler
			sched.start();
				

	}

	
private int dayoftheWeek(){
	if(jcbSfrequency.equals("SUNDAY") )
		dayofWeek = 1;
	if(jcbSfrequency.equals("MONDAY") )
		dayofWeek = 2;
	if(jcbSfrequency.equals("TUESDAY") )
		dayofWeek = 3;
	if(jcbSfrequency.equals("WEDNESDAY") )
		dayofWeek = 4;
	if(jcbSfrequency.equals("THURSDAY") )
		dayofWeek = 5;
	if(jcbSfrequency.equals("FRIDAY") )
		dayofWeek = 6;
	if(jcbSfrequency.equals("SATURDAY") )
		dayofWeek = 7;
	return dayofWeek;
}

	 
	}
