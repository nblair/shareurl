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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.FreeBusy;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;

import edu.wisc.wisccal.shareurl.Tests;
import edu.wisc.wisccal.shareurl.web.ShareRequestDetails;

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
		VEvent event = new VEvent(new DateTime(Tests.makeDateTime("20100210-0900")), new DateTime(Tests.makeDateTime("20100210-1000")), "a");
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
		VEvent privateEvent = new VEvent(new DateTime(Tests.makeDateTime("20100210-0900")), new DateTime(Tests.makeDateTime("20100210-1000")), "private event");
		privateEvent.getProperties().add(Clazz.PRIVATE);
		components.add(privateEvent);
		
		VEvent confidentialEvent = new VEvent(new DateTime(Tests.makeDateTime("20100210-0900")), new DateTime(Tests.makeDateTime("20100210-1000")), "confidential event");
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
		VEvent privateEvent = new VEvent(new DateTime(Tests.makeDateTime("20100210-0900")), new DateTime(Tests.makeDateTime("20100210-1000")), "event");
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
		VEvent privateEvent = new VEvent(new DateTime(Tests.makeDateTime("20100210-0900")), new DateTime(Tests.makeDateTime("20100210-1000")), "event");
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
		
		Assert.assertEquals(EventParticipation.ATTENDEE_NEEDSACTION, utils.getEventParticipation(event, calendarAccount));
		
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
		ICalendarAccount calendarAccount = mock(ICalendarAccount.class);
		when(calendarAccount.getEmailAddress()).thenReturn("somebody@wisc.edu");
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test stripEventDetails", false);
		event.getProperties().add(new Location("somewhere"));
		Calendar calendar = CalendarDataUtils.wrapEvent(event);
		
		CalendarDataUtils utils = new CalendarDataUtils();
		utils.stripEventDetails(calendar, calendarAccount);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
		VEvent after = (VEvent) calendar.getComponent(VEvent.VEVENT);
		Assert.assertNotNull(after.getUid());
		Assert.assertEquals("Busy", after.getSummary().getValue());
		Assert.assertNull(after.getLocation());
	}
	
	@Test
	public void testStripEventDetailsFree() {
		ICalendarAccount calendarAccount = mock(ICalendarAccount.class);
		when(calendarAccount.getEmailAddress()).thenReturn("somebody@wisc.edu");
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test stripEventDetailsFree", false);
		event.getProperties().add(Transp.TRANSPARENT);
		Calendar calendar = CalendarDataUtils.wrapEvent(event);
		
		CalendarDataUtils utils = new CalendarDataUtils();
		utils.stripEventDetails(calendar, calendarAccount);
		Assert.assertEquals(0, calendar.getComponents(VEvent.VEVENT).size());
	}
	
	@Test
	public void testCheapRecurrenceCopy() throws ParseException {
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test cheapRecurrenceCopy", false);
		event.getProperties().add(new Location("somewhere"));
		CalendarDataUtils utils = new CalendarDataUtils();
		Period period = new Period(new DateTime("20120814-0900"), new DateTime("20120814-1000"));
		VEvent recurrenceCopy = utils.cheapRecurrenceCopy(event, period, false, false);
		Assert.assertEquals(period.getStart(), recurrenceCopy.getStartDate().getDate());
		Assert.assertEquals(period.getEnd(), recurrenceCopy.getEndDate().getDate());
		Assert.assertEquals("somewhere", recurrenceCopy.getLocation().getValue());
		Assert.assertEquals("test cheapRecurrenceCopy", recurrenceCopy.getSummary().getValue());
		Assert.assertNotSame(event.getUid(), recurrenceCopy.getUid());
		Assert.assertTrue(recurrenceCopy.getUid().getValue().startsWith(event.getUid().getValue()));
		Assert.assertNull(recurrenceCopy.getTransparency());
	}
	
	@Test
	public void testTruncate() {
		DateTime dateTime = new DateTime(Tests.makeDateTime("20120813-0900"));
		CalendarDataUtils utils = new CalendarDataUtils();
		Date date = utils.truncate(dateTime);
		Assert.assertNotSame(dateTime, date);
	}
	@Test
	public void testCheapRecurrenceCopyDayEvent() throws ParseException {
		VEvent event = Tests.mockAllDayEvent("20120813", "20120814", "test cheapRecurrenceCopyDayEvent");
		event.getProperties().add(new Location("somewhere"));
		CalendarDataUtils utils = new CalendarDataUtils();
		Period period = new Period(new DateTime("20120815"), new DateTime("20120816"));
		VEvent recurrenceCopy = utils.cheapRecurrenceCopy(event, period, false, false);
		Assert.assertEquals(period.getStart(), recurrenceCopy.getStartDate().getDate());
		Assert.assertEquals(period.getEnd(), recurrenceCopy.getEndDate().getDate());
		Assert.assertEquals(Value.DATE, recurrenceCopy.getStartDate().getParameter(Value.VALUE));
		Assert.assertEquals(Value.DATE, recurrenceCopy.getEndDate().getParameter(Value.VALUE));
		Assert.assertEquals("20120815", recurrenceCopy.getStartDate().getValue());
		Assert.assertEquals("20120816", recurrenceCopy.getEndDate().getValue());
		Assert.assertEquals("somewhere", recurrenceCopy.getLocation().getValue());
		Assert.assertEquals("test cheapRecurrenceCopyDayEvent", recurrenceCopy.getSummary().getValue());
		Assert.assertNotSame(event.getUid(), recurrenceCopy.getUid());
		Assert.assertTrue(recurrenceCopy.getUid().getValue().startsWith(event.getUid().getValue()));
		Assert.assertNull(recurrenceCopy.getTransparency());
	}
	
	@Test
	public void testCheapRecurrenceCopyTransparency() throws ParseException {
		VEvent event = Tests.mockEvent("20120813-0900", "20120813-1000", "test cheapRecurrenceCopy", false);
		event.getProperties().add(new Location("somewhere"));
		event.getProperties().add(Transp.TRANSPARENT);
		CalendarDataUtils utils = new CalendarDataUtils();
		Period period = new Period(new DateTime("20120814-0900"), new DateTime("20120814-1000"));
		VEvent recurrenceCopy = utils.cheapRecurrenceCopy(event, period, false, false);
		Assert.assertEquals(period.getStart(), recurrenceCopy.getStartDate().getDate());
		Assert.assertEquals(period.getEnd(), recurrenceCopy.getEndDate().getDate());
		Assert.assertEquals("somewhere", recurrenceCopy.getLocation().getValue());
		Assert.assertEquals("test cheapRecurrenceCopy", recurrenceCopy.getSummary().getValue());
		Assert.assertNotSame(event.getUid(), recurrenceCopy.getUid());
		Assert.assertTrue(recurrenceCopy.getUid().getValue().startsWith(event.getUid().getValue()));
		Assert.assertEquals(Transp.TRANSPARENT, recurrenceCopy.getTransparency());
	}
	
	/**
	 * Verify no exceptions thrown for empty calendar
	 */
	@Test
	public void filterEmpty() {
		Calendar calendar = new Calendar();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/share/u/abcdefg");
		request.setContextPath("/share");
		request.setServletPath("/u");
		ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		CalendarDataUtils utils = new CalendarDataUtils();
		utils.filterAgendaForDateRange(calendar, requestDetails);
	}
	
	/**
	 * Verify event within date range is retained.
	 */
	@Test
	public void filterControl() {
		Calendar calendar = new Calendar();
		Date now = new Date();
		VEvent event = new VEvent(new DateTime(now), new DateTime(DateUtils.addMinutes(now, 30)), "filterControl");
		calendar.getComponents().add(event);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/share/u/abcdefg");
		request.setContextPath("/share");
		request.setServletPath("/u");
		ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		CalendarDataUtils utils = new CalendarDataUtils();
		utils.filterAgendaForDateRange(calendar, requestDetails);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
	}
	
	/**
	 * Verify event within date range is retained, and event outside of date range
	 * is removed.
	 * 
	 */
	@Test
	public void filterRemove() {
		Calendar calendar = new Calendar();
		Date now = new Date();
		VEvent event = new VEvent(new DateTime(now), new DateTime(DateUtils.addMinutes(now, 30)), "filterRemove-keep");
		
		VEvent willDrop = new VEvent(new DateTime(DateUtils.addDays(now, 1)), new DateTime(DateUtils.addMinutes(DateUtils.addDays(now, 1), 30)), "filterRemove-remove");
		calendar.getComponents().add(event);
		calendar.getComponents().add(willDrop);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/share/u/abcdefg");
		request.setContextPath("/share");
		request.setServletPath("/u");
		ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		CalendarDataUtils utils = new CalendarDataUtils();
		utils.filterAgendaForDateRange(calendar, requestDetails);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
	}
	
	@Test
	public void testIsAllDayPeriod() {
		Period negative = new Period(new DateTime(Tests.makeDateTime("20120820-1200")), new DateTime(Tests.makeDateTime("20120820-1300")));
		Assert.assertFalse(CalendarDataUtils.isAllDayPeriod(negative));
		
		Period positive = new Period(new DateTime(Tests.makeDate("20120820")), new DateTime(Tests.makeDate("20120821")));
		Assert.assertTrue(CalendarDataUtils.isAllDayPeriod(positive));
	}
	
	@Test
	public void testExpandRecurrenceOnRecurringAllDayEvent() throws IOException, ParserException {
		ClassPathResource resource = new ClassPathResource("example-data/recurring-day-event-1.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		Assert.assertEquals(2, calendar.getComponents().size());
		
		CalendarDataUtils utils = new CalendarDataUtils();	
		utils.expandRecurrence(calendar, Tests.makeDate("20120820"), Tests.makeDate("20120825"), false);	
		Assert.assertEquals(5, calendar.getComponents().size());
		for(Object o: calendar.getComponents()) {
			VEvent event = (VEvent) o;
			if("20120822".equals(event.getStartDate().getValue())) {
				Assert.assertEquals(Transp.TRANSPARENT, event.getTransparency());
			} else {
				Assert.assertEquals(Transp.OPAQUE, event.getTransparency());
			}
		}
	}
	@Test
	public void testNoRecurrenceOnRecurringAllDayEvent() throws IOException, ParserException {
		ClassPathResource resource = new ClassPathResource("example-data/recurring-day-event-1.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		Assert.assertEquals(2, calendar.getComponents().size());
		
		CalendarDataUtils utils = new CalendarDataUtils();	
		utils.noRecurrence(calendar, Tests.makeDate("20120820"), Tests.makeDate("20120825"), false);	
		Assert.assertEquals(5, calendar.getComponents().size());
		for(Object o: calendar.getComponents()) {
			VEvent event = (VEvent) o;
			if("20120822".equals(event.getStartDate().getValue())) {
				Assert.assertEquals(Transp.TRANSPARENT, event.getTransparency());
			} else {
				Assert.assertEquals(Transp.OPAQUE, event.getTransparency());
			}
		}
	}
	
	@Test
	public void testConvertToFreeBusyExampleRecurringDayEvent1() throws IOException, ParserException {
		ClassPathResource resource = new ClassPathResource("example-data/recurring-day-event-1.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		Assert.assertEquals(2, calendar.getComponents().size());
		
		CalendarDataUtils utils = new CalendarDataUtils();	
		utils.noRecurrence(calendar, Tests.makeDate("20120820"), Tests.makeDate("20120825"), false);	
		Assert.assertEquals(5, calendar.getComponents().size());
		Calendar fbCalendar = utils.convertToFreeBusy(calendar, Tests.makeDate("20120820"), Tests.makeDate("20120825"));
		
		Assert.assertEquals(1, fbCalendar.getComponents().size());
		VFreeBusy freeBusy = (VFreeBusy) fbCalendar.getComponent(VFreeBusy.VFREEBUSY);
		Period expectedSkipped = new Period(new DateTime(Tests.makeDate("20120822")), new DateTime(Tests.makeDate("20120823")));
		Assert.assertNotNull(freeBusy);
		for(Object o : freeBusy.getProperties(FreeBusy.FREEBUSY)) {
			FreeBusy fb = (FreeBusy) o;
			PeriodList periodList = fb.getPeriods();
			for(Object p: periodList) {
				Period period = (Period) p;
				Assert.assertNotSame(expectedSkipped, period);
			}
			
		}
	}
}
