/*******************************************************************************
*  Copyright 2007-2010 The Board of Regents of the University of Wisconsin System.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*******************************************************************************/
package edu.wisc.wisccal.shareurl.ical;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Assert;
import org.junit.Test;

import edu.wisc.wisccal.shareurl.Tests;

/**
 * Test harness for {@link CalendarDataUtils}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarDataUtilsTest.java 1722 2010-02-15 22:01:26Z npblair $
 */
public class CalendarDataUtilsTest {

	/**
	 * Verify the {@link CalendarDataUtils#constructProperty(String, String)} properly creates
	 * a {@link Summary} property.
	 * @throws Exception
	 */
	@Test
	public void testConstructPropertySummary() throws Exception {
		Property prop = CalendarDataUtils.constructProperty(Summary.SUMMARY, "event summary");
		Assert.assertNotNull(prop);
		Assert.assertEquals(Summary.SUMMARY, prop.getName());
		Assert.assertEquals("event summary", prop.getValue());
	}
	
	/**
	 * Assert calendar with events already containing CLASS:PUBLIC are unchanged.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvertClassPublicControl() throws Exception {
		ComponentList components = new ComponentList();
		VEvent event = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "a");
		event.getProperties().add(Clazz.PUBLIC);
		
		VEvent clone = (VEvent) event.copy();
		
		components.add(event);
		
		Calendar original = new Calendar(components);
		original.getProperties().add(Version.VERSION_2_0);
		original.getProperties().add(new ProdId(CalendarDataUtils.SHAREURL_PROD_ID));
		CalendarDataUtils instance = new CalendarDataUtils();
		instance.convertClassPublic(original);
		Assert.assertEquals(clone, original.getComponent(VEvent.VEVENT));
	}
	
	/**
	 * Assert calendar with events with CLASS properties not equal to PUBLIC are appropriately
	 * modified by {@link CalendarDataUtils#convertClassPublic(Calendar)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvertClassPublic1() throws Exception {
		ComponentList components = new ComponentList();
		VEvent privateEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "private event");
		privateEvent.getProperties().add(Clazz.PRIVATE);
		components.add(privateEvent);
		
		VEvent confidentialEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "confidential event");
		confidentialEvent.getProperties().add(Clazz.CONFIDENTIAL);
		components.add(confidentialEvent);
		
		Calendar original = new Calendar(components);
		CalendarDataUtils instance = new CalendarDataUtils();
		instance.convertClassPublic(original);
		
		for (Iterator<?> i = original.getComponents().iterator(); i.hasNext();) {
			VEvent event = (VEvent) i.next();  
			Assert.assertEquals(Clazz.PUBLIC, event.getProperty(Clazz.CLASS));
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSingleEventNull() throws Exception {
		Assert.assertNull(CalendarDataUtils.getSingleEvent(null, null));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSingleEventControl() throws Exception {
		ComponentList components = new ComponentList();
		VEvent privateEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "event");
		privateEvent.getProperties().add(new Uid("12345"));
		components.add(privateEvent);
		Calendar calendar = new Calendar(components);
		
		VEvent result = CalendarDataUtils.getSingleEvent(calendar, "12345");
		Assert.assertNotNull(result);
		Assert.assertEquals(privateEvent, result);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSingleEventNotFound() throws Exception {
		ComponentList components = new ComponentList();
		VEvent privateEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "event");
		privateEvent.getProperties().add(new Uid("123456"));
		components.add(privateEvent);
		Calendar calendar = new Calendar(components);
		
		VEvent result = CalendarDataUtils.getSingleEvent(calendar, "12345");
		Assert.assertNull(result);
	}
	
	@Test
	public void testGetEventParticipation() throws URISyntaxException {
		CalendarDataUtils utils = new CalendarDataUtils();
		ICalendarAccount calendarAccount = mock(ICalendarAccount.class);
		when(calendarAccount.getEmailAddress()).thenReturn("somebody@wisc.edu");
		
		VEvent event = new VEvent();
		Assert.assertEquals(EventParticipation.PERSONAL_EVENT, utils.getEventParticipation(event, calendarAccount));
		
		Organizer organizer = new Organizer("mailto:somebody@wisc.edu");
		event.getProperties().add(organizer);
		
		// not a group event until another ATTENDEE shows up
		Assert.assertEquals(EventParticipation.PERSONAL_EVENT, utils.getEventParticipation(event, calendarAccount));
		
		Attendee attendee = new Attendee("mailto:somebodyelse@wisc.edu");
		event.getProperties().add(attendee);
		
		Assert.assertEquals(EventParticipation.ORGANIZER, utils.getEventParticipation(event, calendarAccount));
		
		event.getProperties().remove(organizer);
		event.getProperties().remove(attendee);
		
		event.getProperties().add(new Organizer("mailto:somebodyelse@wisc.edu"));
		Attendee selfAttendee = new Attendee("mailto:somebody@wisc.edu");
		event.getProperties().add(selfAttendee);
		
		Assert.assertEquals(EventParticipation.ATTENDEE, utils.getEventParticipation(event, calendarAccount));
		
		event.getProperties().remove(selfAttendee);
		
		event.getProperties().add(new Attendee("mailto:another@wisc.edu"));
		Assert.assertEquals(EventParticipation.NOT_INVOLVED, utils.getEventParticipation(event, calendarAccount));
	}
	
	@Test
	public void testPersonalOnly() throws URISyntaxException {
		CalendarDataUtils utils = new CalendarDataUtils();
		ICalendarAccount calendarAccount = mock(ICalendarAccount.class);
		when(calendarAccount.getEmailAddress()).thenReturn("somebody@wisc.edu");
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test personalOnly", false);
		
		Calendar calendar = CalendarDataUtils.wrapEvent(event);
		utils.personalOnly(calendar, calendarAccount);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
		Assert.assertEquals(event, calendar.getComponents(VEvent.VEVENT).get(0));
		
		// add organizer and attendee
		event.getProperties().add(new Organizer("mailto:somebodyelse@wisc.edu"));
		event.getProperties().add(new Attendee("mailto:somebody@wisc.edu"));
		utils.personalOnly(calendar, calendarAccount);
		Assert.assertEquals(0, calendar.getComponents(VEvent.VEVENT).size());
	}
	@Test
	public void testOrganizingOnly() throws URISyntaxException {
		CalendarDataUtils utils = new CalendarDataUtils();
		ICalendarAccount calendarAccount = mock(ICalendarAccount.class);
		when(calendarAccount.getEmailAddress()).thenReturn("somebody@wisc.edu");
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test personalOnly", false);
		// add organizer and attendee
		Organizer o = new Organizer("mailto:somebody@wisc.edu");
		event.getProperties().add(o);
		Attendee a = new Attendee("mailto:somebodyelse@wisc.edu");
		event.getProperties().add(a);
		Calendar calendar = CalendarDataUtils.wrapEvent(event);
		utils.organizerOnly(calendar, calendarAccount);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
		Assert.assertEquals(event, calendar.getComponents(VEvent.VEVENT).get(0));
		
		// remove organizer and attendee
		event.getProperties().remove(o);
		event.getProperties().remove(a);
		utils.organizerOnly(calendar, calendarAccount);
		Assert.assertEquals(0, calendar.getComponents(VEvent.VEVENT).size());
	}
	@Test
	public void testAttendingOnly() throws URISyntaxException {
		CalendarDataUtils utils = new CalendarDataUtils();
		ICalendarAccount calendarAccount = mock(ICalendarAccount.class);
		when(calendarAccount.getEmailAddress()).thenReturn("somebody@wisc.edu");
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test personalOnly", false);
		// add organizer and attendee
		Organizer o = new Organizer("mailto:somebodyelse@wisc.edu");
		event.getProperties().add(o);
		Attendee a = new Attendee("mailto:somebody@wisc.edu");
		event.getProperties().add(a);
		Calendar calendar = CalendarDataUtils.wrapEvent(event);
		utils.attendeeOnly(calendar, calendarAccount);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
		Assert.assertEquals(event, calendar.getComponents(VEvent.VEVENT).get(0));
		
		// remove organizer and attendee
		event.getProperties().remove(o);
		event.getProperties().remove(a);
		utils.attendeeOnly(calendar, calendarAccount);
		Assert.assertEquals(0, calendar.getComponents(VEvent.VEVENT).size());
	}
	
	@Test
	public void testStripEventDetails() {
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test stripEventDetails", false);
		event.getProperties().add(new Location("somewhere"));
		Calendar calendar = CalendarDataUtils.wrapEvent(event);
		
		CalendarDataUtils utils = new CalendarDataUtils();
		utils.stripEventDetails(calendar);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
		VEvent after = (VEvent) calendar.getComponent(VEvent.VEVENT);
		Assert.assertNotNull(after.getUid());
		Assert.assertEquals("Busy", after.getSummary().getValue());
		Assert.assertNull(after.getLocation());
	}
	
	@Test
	public void testStripEventDetailsFree() {
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test stripEventDetails", false);
		event.getProperties().add(Transp.TRANSPARENT);
		Calendar calendar = CalendarDataUtils.wrapEvent(event);
		
		CalendarDataUtils utils = new CalendarDataUtils();
		utils.stripEventDetails(calendar);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
		VEvent after = (VEvent) calendar.getComponent(VEvent.VEVENT);
		Assert.assertNotNull(after.getUid());
		Assert.assertEquals("Free", after.getSummary().getValue());
		Assert.assertNull(after.getLocation());
	}
	/**
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	private java.util.Date makeDateTime(String value) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		return DateUtils.truncate(df.parse(value), java.util.Calendar.MINUTE);
	}
}
