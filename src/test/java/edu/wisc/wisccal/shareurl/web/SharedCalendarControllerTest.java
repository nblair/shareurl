/**
 * 
 */
package edu.wisc.wisccal.shareurl.web;

import java.util.Collections;

import junit.framework.Assert;
import net.fortuna.ical4j.model.Calendar;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import edu.wisc.wisccal.shareurl.domain.SharePreferences;

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
		
		SharePreferences share = new SharePreferences();
		
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		controller.prepareModel(share, requestDetails, calendar, account, model);
	
		Assert.assertEquals(calendar.toString(), model.get("ical"));
		Assert.assertEquals(2, StringUtils.countMatches(calendar.toString(), "BEGIN:VEVENT"));
		Assert.assertEquals(0, StringUtils.countMatches(calendar.toString(), "RECURRENCE-ID"));
	}
}
