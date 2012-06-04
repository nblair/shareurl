/**
 *
 */

package edu.wisc.wisccal.shareurl.web;

import java.util.Date;

import junit.framework.Assert;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Nicholas Blair
 */
public class SharedCalendarControllerTest {

	/**
	 * Verify no exceptions thrown for empty calendar
	 */
	@Test
	public void filterEmpty() {
		Calendar calendar = new Calendar();
		SharedCalendarController controller = new SharedCalendarController();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/share/u/abcdefg");
		request.setContextPath("/share");
		request.setServletPath("/u");
		ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		controller.filterAgendaForDateRange(calendar, requestDetails);
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
		SharedCalendarController controller = new SharedCalendarController();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/share/u/abcdefg");
		request.setContextPath("/share");
		request.setServletPath("/u");
		ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		controller.filterAgendaForDateRange(calendar, requestDetails);
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
		SharedCalendarController controller = new SharedCalendarController();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/share/u/abcdefg");
		request.setContextPath("/share");
		request.setServletPath("/u");
		ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		controller.filterAgendaForDateRange(calendar, requestDetails);
		Assert.assertEquals(1, calendar.getComponents(VEvent.VEVENT).size());
	}
}
