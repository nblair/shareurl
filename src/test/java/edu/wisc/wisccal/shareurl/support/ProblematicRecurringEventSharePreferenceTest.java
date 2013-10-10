package edu.wisc.wisccal.shareurl.support;


import java.io.IOException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests for {@link ProblematicRecurringEventSharePreference}.
 * 
 * @author Nicholas Blair
 */
public class ProblematicRecurringEventSharePreferenceTest {

	/**
	 * 
	 */
	@Test
	public void testEmptyEvent() {
		ProblematicRecurringEventSharePreference preference = new ProblematicRecurringEventSharePreference();
		VEvent event = new VEvent();
		Assert.assertFalse(preference.matches(event));
		preference.dispose();
	}
	/**
	 * 
	 */
	@Test
	public void testControl() {
		ProblematicRecurringEventSharePreference preference = new ProblematicRecurringEventSharePreference();
		java.util.Date now = new java.util.Date();
		VEvent event = new VEvent(new DateTime(now), new DateTime(DateUtils.addHours(now, 1)), "testControl");
		Assert.assertFalse(preference.matches(event));
		preference.dispose();
	}
	/**
	 * 
	 * @throws IOException
	 * @throws ParserException
	 */
	@Test
	public void testRRule() throws IOException, ParserException {
		ProblematicRecurringEventSharePreference preference = new ProblematicRecurringEventSharePreference();
		ClassPathResource resource = new ClassPathResource("example-data/recurring-example-1.ics");
		
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		VEvent event = (VEvent) calendar.getComponent(VEvent.VEVENT);
		Assert.assertFalse(preference.matches(event));
		preference.dispose();
	}
	
	/**
	 * 
	 * @throws IOException
	 * @throws ParserException
	 */
	@Test
	public void testRDateExample1() throws IOException, ParserException {
		ProblematicRecurringEventSharePreference preference = new ProblematicRecurringEventSharePreference();
		ClassPathResource resource = new ClassPathResource("example-data/rdate-example-1.ics");
		
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		VEvent event = (VEvent) calendar.getComponent(VEvent.VEVENT);
		Assert.assertTrue(preference.matches(event));
		preference.dispose();
	}
	
	/**
	 * rdate-example-2.ics is a calendar file that contains 2 vevents. Both have the same
	 * UID, the latter in the list has a RECURRENCE-ID.
	 * 
	 * {@link ProblematicRecurringEventSharePreference#matches(VEvent)} implementation is order
	 * dependent.
	 * 
	 * @throws IOException
	 * @throws ParserException
	 */
	@Test
	public void testRDateExample2() throws IOException, ParserException {
		ProblematicRecurringEventSharePreference preference = new ProblematicRecurringEventSharePreference();
		ClassPathResource resource = new ClassPathResource("example-data/rdate-example-2.ics");
		
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		ComponentList events = calendar.getComponents(VEvent.VEVENT);
		Assert.assertEquals(2, events.size());
		for(Object o: events) {
			VEvent event = (VEvent) o;
			Assert.assertTrue(preference.matches(event));
		}
		
		preference.dispose();
	}
	
}
