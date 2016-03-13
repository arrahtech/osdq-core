package org.arrah.framework.scheduler;


import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


import org.arrah.gui.swing.JobScheduler;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;

import org.quartz.JobDetail;
import org.quartz.JobKey;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

public class QuartzScheduler{

String quer;
int hours, minutes, seconds;
JobKey jk;
Date d1=new Date(); 
int dayofWeek, dayOfMonth;
CronTrigger trigger1; 
SimpleTrigger trigger;
Date date=new Date();

	public QuartzScheduler(String text, int hour, int minute, int second) throws SchedulerException, InterruptedException{
		
		hours=hour;
		minutes=minute;
		seconds=second;
		quer=text;
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
			
			
			new ScheduleJob(quer);
			String key="ExecuteJob1", value="Report Generation1";
			JobKey jobKey = new JobKey(key, value);
							
			JobDetail job = newJob(ScheduleJob.class)
			    .withIdentity(jobKey)
			    .build();
			
			// This logic applies if the user want to schedule on a one-time basis
			if(JobScheduler.jcbFrequency.getSelectedItem().toString().equals("One Time")){
			      
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
			if(JobScheduler.jcbFrequency.getSelectedItem().toString().equals("Daily")){
			 trigger1 = newTrigger()
				    .withIdentity("trigger1", "Report Generation3")
				    .withSchedule(dailyAtHourAndMinute(hours, minutes)) 
				    .build();
			}
			
			// This logic applies if the user want to schedule on a weekly basis
			else if(JobScheduler.jcbFrequency.getSelectedItem().toString().equals("Weekly")){
				dayoftheWeek();
				trigger1 = newTrigger()
					    .withIdentity("trigger1", "Report Generation3")
					    .withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(dayofWeek, hours, minutes))
					    .build();
			}
			
			// This logic applies if the user want to schedule on a Monthly basis
			else if(JobScheduler.jcbFrequency.getSelectedItem().toString().equals("Monthly")){
				trigger1 = newTrigger()
					    .withIdentity("trigger1", "Report Generation3")
					    .withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(JobScheduler.startdayofMonth, hours, minutes))
					    .endAt(JobScheduler.jdcEdate.getDate())
					    .build();
				}
			         if (trigger1 !=null)
				     sched.scheduleJob(job, trigger1);
			 
			    	 
			//Start the scheduler
			sched.start();
				

	}

	
private int dayoftheWeek(){
	if(JobScheduler.jcbSfrequency.getSelectedItem().toString().equals("SUNDAY") )
		dayofWeek = 1;
	if(JobScheduler.jcbSfrequency.getSelectedItem().toString().equals("MONDAY") )
		dayofWeek = 2;
	if(JobScheduler.jcbSfrequency.getSelectedItem().toString().equals("TUESDAY") )
		dayofWeek = 3;
	if(JobScheduler.jcbSfrequency.getSelectedItem().toString().equals("WEDNESDAY") )
		dayofWeek = 4;
	if(JobScheduler.jcbSfrequency.getSelectedItem().toString().equals("THURSDAY") )
		dayofWeek = 5;
	if(JobScheduler.jcbSfrequency.getSelectedItem().toString().equals("FRIDAY") )
		dayofWeek = 6;
	if(JobScheduler.jcbSfrequency.getSelectedItem().toString().equals("SATURDAY") )
		dayofWeek = 7;
	return dayofWeek;
}

	 
	}
