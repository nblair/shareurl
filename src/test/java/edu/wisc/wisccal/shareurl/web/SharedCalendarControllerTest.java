/**
 * 
 */
package edu.wisc.wisccal.shareurl.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.web.ShareRequestDetails.Client;
import edu.wisc.wisccal.shareurl.web.ShareRequestDetails.PathData;

/**
 * @author Nicholas Blair
 *
 */
public class SharedCalendarControllerTest {

	/**
	 * All defaults, empty calendar.
	 * Model should be prepped for default HTML view.
	 */
	@Test
	public void testPrepareModelControl() {
		SharedCalendarController controller = new SharedCalendarController();
		
		ModelMap model = new ModelMap();
		Calendar emptyCalendar = new Calendar();
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest("GET", "/12345abcde");
		ShareRequestDetails requestDetails = new ShareRequestDetails(httpRequest);
		
		SharePreferences share = new SharePreferences();
		
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(share, requestDetails, emptyCalendar, account, model);
	
		Assert.assertEquals("12345abcde", model.get("shareId"));
		Assert.assertTrue((Boolean) model.get("empty"));
		Assert.assertEquals(Collections.emptyList(), model.get("allEvents"));
		Assert.assertEquals(requestDetails.getStartDate(), model.get("startDate"));
		Assert.assertEquals(requestDetails.getEndDate(), model.get("endDate"));
		Assert.assertEquals(requestDetails.getDatePhrase(), model.get("datePhrase"));
	}	
	
	/**
	 * All defaults, empty calendar.
	 * Model should be prepped for default HTML view.
	 */
	@Test
	public void testPrepareModelHTMLViewWithRecurringEvent() {
		SharedCalendarController controller = new SharedCalendarController();
		
		ModelMap model = new ModelMap();
		Calendar calendar = new Calendar();
		String uidValue = "xyz123";
		
		java.util.Date today = new java.util.Date();
		VEvent event = new VEvent(new Date(today), new Date(DateUtils.addDays(today, 1)), "test day event");
		event.getProperties().add(new RRule(new Recur(Recur.DAILY, 1)));
		event.getProperties().add(new Uid(uidValue));
		calendar.getComponents().add(event);
		
		java.util.Date twoDaysAhead = DateUtils.addDays(new java.util.Date(), 2);
		VEvent event2 = new VEvent(new Date(twoDaysAhead), new Date(DateUtils.addDays(today, 1)), "test day event diff title");
		event2.getProperties().add(new RecurrenceId(new Date(twoDaysAhead)));
		event2.getProperties().add(new Uid(uidValue));
		calendar.getComponents().add(event2);
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest("GET", "/12345abcde");
		ShareRequestDetails requestDetails = new ShareRequestDetails(httpRequest);
		
		SharePreferences share = new SharePreferences();
		
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(share, requestDetails, calendar, account, model);
	
		Assert.assertEquals("12345abcde", model.get("shareId"));
		Assert.assertFalse((Boolean) model.get("empty"));
		Assert.assertEquals(calendar.getComponents(), model.get("allEvents"));
		Assert.assertEquals(1, StringUtils.countMatches(calendar.toString(), "UID:xyz123_UW_"));
	}
	
	/**
	 * All defaults, empty calendar. ical parameter present.
	 * Model should be prepped for iCalendar view.
	 */
	@Test
	public void testPrepareModelICalendar() {
		SharedCalendarController controller = new SharedCalendarController();
		
		ModelMap model = new ModelMap();
		Calendar emptyCalendar = new Calendar();
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest("GET", "/12345abcde");
		httpRequest.addParameter("ical", "");
		ShareRequestDetails requestDetails = new ShareRequestDetails(httpRequest);
		
		SharePreferences share = new SharePreferences();
		
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(share, requestDetails, emptyCalendar, account, model);
	
		Assert.assertEquals(emptyCalendar.toString(), model.get("ical"));
	}	
	
	/**
	 * 
	 */
	@Test
	public void testPrepareModelICalendarTriggerBreakRecurrence() {
		SharedCalendarController controller = new SharedCalendarController();
		
		ModelMap model = new ModelMap();
		Calendar calendar = new Calendar();
		String uidValue = "xyz123";
		java.util.Date today = new java.util.Date();
		VEvent event = new VEvent(new Date(today), new Date(DateUtils.addDays(today, 1)), "test day event");
		event.getProperties().add(new RRule(new Recur(Recur.DAILY, 1)));
		event.getProperties().add(new Uid(uidValue));
		calendar.getComponents().add(event);
		
		java.util.Date twoDaysAhead = DateUtils.addDays(new java.util.Date(), 2);
		VEvent event2 = new VEvent(new Date(twoDaysAhead), "test day event diff title");
		event2.getProperties().add(new RecurrenceId(new Date(twoDaysAhead)));
		event2.getProperties().add(new Uid(uidValue));
		calendar.getComponents().add(event2);
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest("GET", "/12345abcde/dr(0,1)");
		httpRequest.addParameter("ical", "");
		httpRequest.addParameter("compat", "br");
		ShareRequestDetails requestDetails = new ShareRequestDetails(httpRequest);
		
		SharePreferences sharePreferences = new SharePreferences();
		
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(sharePreferences, requestDetails, calendar, account, model);
	
		Assert.assertEquals(calendar.toString(), model.get("ical"));
		Assert.assertEquals(2, StringUtils.countMatches(calendar.toString(), "BEGIN:VEVENT"));
		Assert.assertEquals(0, StringUtils.countMatches(calendar.toString(), "RECURRENCE-ID"));
	}
	
	@Test
	public void testPrepareModelRecurrenceExample1() throws IOException, ParserException, ParseException {
		SharedCalendarController controller = new SharedCalendarController();
		
		ModelMap model = new ModelMap();
		
		ClassPathResource calendarData = new ClassPathResource("example-data/recurring-example-1.ics");
		
		Calendar calendar = new CalendarBuilder().build(calendarData.getInputStream());
		
		PathData pathData = new PathData();
		pathData.setShareKey("12345abcde");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date startDate = df.parse("2012-06-15");
		java.util.Date endDate = df.parse("2012-06-30");
		pathData.setStartDate(startDate);
		pathData.setEndDate(endDate);
		pathData.setStartDate(startDate);
		ShareRequestDetails requestDetails = new ShareRequestDetails(pathData, Client.OTHER, ShareDisplayFormat.HTML, false, false);
		
		SharePreferences sharePreferences = new SharePreferences();
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(sharePreferences, requestDetails, calendar, account, model);
		
		Assert.assertEquals(5, calendar.getComponents(VEvent.VEVENT).size());
		// inspect the calendar, look for the following events:
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				Assert.assertTrue(event.getUid().getValue().contains("_UW_"));
			}
		}
	}
	
	@Test
	public void testPrepareModelRecurrenceExample2() throws IOException, ParserException, ParseException {
		SharedCalendarController controller = new SharedCalendarController();
		
		ModelMap model = new ModelMap();
		
		ClassPathResource calendarData = new ClassPathResource("example-data/recurring-example-2.ics");
		
		Calendar calendar = new CalendarBuilder().build(calendarData.getInputStream());
		
		PathData pathData = new PathData();
		pathData.setShareKey("12345abcde");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date startDate = df.parse("2012-06-15");
		java.util.Date endDate = df.parse("2012-06-30");
		pathData.setStartDate(startDate);
		pathData.setEndDate(endDate);
		pathData.setStartDate(startDate);
		ShareRequestDetails requestDetails = new ShareRequestDetails(pathData, Client.OTHER, ShareDisplayFormat.HTML, false, false);
		
		SharePreferences sharePreferences = new SharePreferences();
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(sharePreferences, requestDetails, calendar, account, model);
		
		Assert.assertEquals(3, calendar.getComponents(VEvent.VEVENT).size());
		// inspect the calendar, look for the following events:
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				Uid uid = event.getUid();
				Assert.assertTrue(uid.getValue().contains("_UW_"));
				
				String [] uidSplit = uid.getValue().split("_UW_");
				if(uidSplit[1].startsWith("20120620")) {
					Assert.assertTrue(event.getStartDate().getValue().endsWith("103000"));
					Assert.assertTrue(event.getEndDate().getValue().endsWith("113000"));
				} else {
					Assert.assertTrue(event.getStartDate().getValue().endsWith("100000"));
					Assert.assertTrue(event.getEndDate().getValue().endsWith("110000"));
				}
			}
		}
	}
	
	@Test
	public void testPrepareModelRecurrenceExample3() throws IOException, ParserException, ParseException {
		SharedCalendarController controller = new SharedCalendarController();
		
		ModelMap model = new ModelMap();
		
		ClassPathResource calendarData = new ClassPathResource("example-data/recurring-example-3.ics");
		
		Calendar calendar = new CalendarBuilder().build(calendarData.getInputStream());
		
		PathData pathData = new PathData();
		pathData.setShareKey("12345abcde");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date startDate = df.parse("2012-06-15");
		java.util.Date endDate = df.parse("2012-06-30");
		pathData.setStartDate(startDate);
		pathData.setEndDate(endDate);
		pathData.setStartDate(startDate);
		ShareRequestDetails requestDetails = new ShareRequestDetails(pathData, Client.OTHER, ShareDisplayFormat.HTML, false, false);
		
		SharePreferences sharePreferences = new SharePreferences();
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(sharePreferences, requestDetails, calendar, account, model);
		
		Assert.assertEquals(4, calendar.getComponents(VEvent.VEVENT).size());
		// inspect the calendar, look for the following events:
		
		List<String> expectedStarts = Arrays.asList(new String[] {"20120618T120000", "20120620T120000", "20120625T120000", "20120628T120000"} );
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				Uid uid = event.getUid();
				Assert.assertTrue(uid.getValue().contains("_UW_"));
				
				Assert.assertTrue(expectedStarts.contains(event.getStartDate().getValue()));
			}
		}
	}
}

