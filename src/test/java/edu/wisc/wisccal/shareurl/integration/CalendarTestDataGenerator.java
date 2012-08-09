/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.wisc.wisccal.shareurl.integration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.wisc.wisccal.shareurl.Tests;
import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;

/**
 * Generator for a set of iCalendar examples.
 * 
 * @author Nicholas Blair
 */
public class CalendarTestDataGenerator {

	private final String targetMonth = "201208";
	private final String outputDirectoryBase = System.getProperty(
			"edu.wisc.wisccal.shareurl.integration.CalendarTestDataGenerator.outputDirectory", 
			SystemUtils.getJavaIoTmpDir().getAbsolutePath());
	private final File _outputDirectory;
	
	private final Date runtime = new Date();
	private final String runtimeFormatted;
	
	/**
	 * Initializes the outputDirectory using the base directory and the current systemtime.
	 */
	public CalendarTestDataGenerator() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		runtimeFormatted = df.format(runtime);
		_outputDirectory = new File(outputDirectoryBase + File.separator + "calendarTestData_" + runtimeFormatted);
		if(!_outputDirectory.exists()) {
			_outputDirectory.mkdirs();
		}
	}
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateControl() throws IOException {
		VEvent event = controlEvent(targetMonth + "01-0800", targetMonth + "01-0900", "control", true);
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("control", calendar);
	}
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateControlWithoutTimezone() throws IOException {
		VEvent event = controlEvent(targetMonth + "01-0900", targetMonth + "01-1000", "control without timezone", false);
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("controlWithoutTimezone", calendar);
	}
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateControlWithLocation() throws IOException {
		VEvent event = controlEvent(targetMonth + "01-1000", targetMonth + "01-1100", "control with location", true);
		event.getProperties().add(new Location("some office"));
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("controlWithLocation", calendar);
	}
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateControlWithLocationAndDescription() throws IOException {
		VEvent event = controlEvent(targetMonth + "01-1100", targetMonth + "01-1200", "control with location and description", true);
		event.getProperties().add(new Location("some office"));
		event.getProperties().add(new Description("This is the description."));
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("controlWithLocationAndDescription", calendar);
	}
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateControlConfidential() throws IOException {
		VEvent event = controlEvent(targetMonth + "02-0800", targetMonth + "02-0900", "control confidential", true);
		event.getProperties().add(Clazz.CONFIDENTIAL);
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("control-confidential", calendar);
	}
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateControlPrivate() throws IOException {
		VEvent event = controlEvent(targetMonth + "02-0900", targetMonth + "02-1000", "control private", true);
		event.getProperties().add(Clazz.PRIVATE);
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("control-private", calendar);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateRecurringRRule() throws IOException {
		VEvent event = controlEvent(targetMonth + "06-0900", targetMonth + "06-1000", "recurring using rrule", true);
		Recur recur = new Recur(Recur.DAILY, 5);
		event.getProperties().add(new RRule(recur));
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("recur-daily-rrule", calendar);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateRecurringRDateAsOnePropertyPeriod() throws IOException {
		VEvent event = controlEvent(targetMonth + "06-1200", targetMonth + "06-1300", "recurring using 1 RDATE property with a PERIOD value", true);
		DtStart start = event.getStartDate();
		DtEnd end = event.getEndDate();
		PeriodList periods = new PeriodList();
		for(int i = 1; i <= 5; i++) {
			DateTime recurStartTime = new DateTime(DateUtils.addDays(start.getDate(), i));
			DateTime recurEndTime = new DateTime(DateUtils.addDays(end.getDate(), i));
			Period period = new Period(recurStartTime, recurEndTime);
			periods.add(period);
		}
		ParameterList params = new ParameterList();
		params.add(net.fortuna.ical4j.model.parameter.Value.PERIOD);
		RDate rdate = new RDate(params, periods);
		
		event.getProperties().add(rdate);
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("recur-daily-rdate-onepropperiod", calendar);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateRecurringRDateAsMultiplePeriodProperties() throws IOException {
		VEvent event = controlEvent(targetMonth + "06-1200", targetMonth + "06-1300", "recurring using multiple RDATE properties with PERIOD values", true);
		DtStart start = event.getStartDate();
		DtEnd end = event.getEndDate();
		
		for(int i = 1; i <= 5; i++) {
			PeriodList periods = new PeriodList();
			DateTime recurStartTime = new DateTime(DateUtils.addDays(start.getDate(), i));
			DateTime recurEndTime = new DateTime(DateUtils.addDays(end.getDate(), i));
			Period period = new Period(recurStartTime, recurEndTime);
			periods.add(period);
			ParameterList params = new ParameterList();
			params.add(net.fortuna.ical4j.model.parameter.Value.PERIOD);
			RDate rdate = new RDate(params, periods);
			
			event.getProperties().add(rdate);
		}
		
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("recur-daily-rdate-multipleperiodprop", calendar);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGenerateRecurringRDateAsMultipleDateProperties() throws IOException {
		VEvent event = controlEvent(targetMonth + "06-1400", targetMonth + "06-1500", "recurring using multiple RDATE properties with DATETIME values", true);
		DtStart start = event.getStartDate();
		
		for(int i = 1; i <= 5; i++) {
			DateTime recurStartTime = new DateTime(DateUtils.addDays(start.getDate(), i));
			
			DateList dates = new DateList();
			dates.add(recurStartTime);
			RDate rdate = new RDate(dates);
			rdate.getParameters().add(new TzId("America/Chicago"));
			event.getProperties().add(rdate);
		}
		
		Calendar calendar = CalendarDataUtils.wrapEvent(event, "-//ShareURL CalendarTestDataGenerator " + runtimeFormatted + "//WiscCal//EN");
		writeCalendar("recur-daily-rdate-multipledateprop", calendar);
	}

	/**
	 * Write the {@link Calendar} to the output directory using the specified name.
	 * Avoid special characters in the name!
	 * 
	 * @param name
	 * @param calendar
	 * @throws IOException 
	 */
	protected void writeCalendar(String name, Calendar calendar) throws IOException {
		String filename = name + ".ics";
		FileWriter writer = new FileWriter(new File(_outputDirectory, filename));
		
		CalendarOutputter output = new CalendarOutputter(true);
		try {
			output.output(calendar, writer);
		} catch (ValidationException e) {
			throw new IllegalStateException("failed to write calendar with name " + name, e);
		}
	}
	/**
	 * The "control" event has a UID, DTSTART, DTEND, and SUMMARY.
	 * 
	 * @param start the start time
	 * @param end the end time
	 * @param the summary
	 * @param whether or not the times should include "America/Chicago" timezone reference
	 * @return the control event
	 */
	protected VEvent controlEvent(String start, String end, String summary, boolean includeTimezone) {
		VEvent event = new VEvent();
		event.getProperties().add(new Uid(UUID.randomUUID().toString()));
		DtStart dtStart = new DtStart(new DateTime(Tests.makeDateTime(start)));
		if(includeTimezone) {
			dtStart.getParameters().add(new TzId("America/Chicago"));
		} else {
			dtStart.setUtc(true);
		}
		event.getProperties().add(dtStart);
		DtEnd dtEnd = new DtEnd(new DateTime(Tests.makeDateTime(end)));
		if(includeTimezone) {
			dtEnd.getParameters().add(new TzId("America/Chicago"));
		} else {
			dtEnd.setUtc(true);
		}
		event.getProperties().add(dtEnd);
		event.getProperties().add(new Summary(summary));
		return event;
	}
}
