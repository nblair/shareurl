/**
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
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
/**
 * 
 */
package edu.wisc.wisccal.shareurl.domain.simple;

import java.io.IOException;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import edu.wisc.wisccal.shareurl.Tests;

/**
 * @author Nicholas Blair
 *
 */
public class SimpleCalendarsImplTest {

	@Test
	public void testControl() throws IOException, ParserException {
		ClassPathResource resource = new ClassPathResource("/example-data/control.ics");
		CalendarBuilder builder = new CalendarBuilder();
		net.fortuna.ical4j.model.Calendar calendar = builder.build(resource.getInputStream());
		
		SimpleCalendarsImpl simpleCalendars = new SimpleCalendarsImpl();
		Calendar simplified = simpleCalendars.simplify(calendar, false);
		Assert.assertNotNull(simplified);
		List<CalendarEntry> entries = simplified.getEntries();
		Assert.assertEquals(1, entries.size());
		CalendarEntry entry = entries.get(0);
		Assert.assertTrue(entry instanceof Event);
		Event event = (Event) entry;
		Assert.assertEquals("234ea713-7fc9-4860-80fe-8b6140271af6", entry.getUid());
		Assert.assertEquals("individual", event.getSummary());
		Assert.assertEquals(FreeBusyStatus.BUSY, event.getShowTimeAs());
		Assert.assertEquals(Tests.makeDateTime("20120629-1415"), event.getStartTime());
		Assert.assertEquals(Tests.makeDateTime("20120629-1515"), event.getEndTime());
		Assert.assertNull(event.getEventStatus());
	}
	
	@Test
	public void testTransparent() throws IOException, ParserException {
		ClassPathResource resource = new ClassPathResource("/example-data/control.ics");
		CalendarBuilder builder = new CalendarBuilder();
		net.fortuna.ical4j.model.Calendar calendar = builder.build(resource.getInputStream());
		VEvent vevent = (VEvent) calendar.getComponent(VEvent.VEVENT);
		vevent.getProperties().remove(vevent.getTransparency());
		vevent.getProperties().add(Transp.TRANSPARENT);
		
		SimpleCalendarsImpl simpleCalendars = new SimpleCalendarsImpl();
		Calendar simplified = simpleCalendars.simplify(calendar, false);
		Assert.assertNotNull(simplified);
		List<CalendarEntry> entries = simplified.getEntries();
		Assert.assertEquals(1, entries.size());
		CalendarEntry entry = entries.get(0);
		Assert.assertTrue(entry instanceof Event);
		Event event = (Event) entry;
		Assert.assertEquals("234ea713-7fc9-4860-80fe-8b6140271af6", entry.getUid());
		Assert.assertEquals("individual", event.getSummary());
		Assert.assertEquals(FreeBusyStatus.FREE, event.getShowTimeAs());
		Assert.assertEquals(Tests.makeDateTime("20120629-1415"), event.getStartTime());
		Assert.assertEquals(Tests.makeDateTime("20120629-1515"), event.getEndTime());
	}
	
	@Test
	public void testCancelled() throws IOException, ParserException {
		ClassPathResource resource = new ClassPathResource("/example-data/control.ics");
		CalendarBuilder builder = new CalendarBuilder();
		net.fortuna.ical4j.model.Calendar calendar = builder.build(resource.getInputStream());
		((VEvent) calendar.getComponent(VEvent.VEVENT)).getProperties().add(Status.VEVENT_CANCELLED);
		SimpleCalendarsImpl simpleCalendars = new SimpleCalendarsImpl();
		Calendar simplified = simpleCalendars.simplify(calendar, false);
		Assert.assertNotNull(simplified);
		List<CalendarEntry> entries = simplified.getEntries();
		Assert.assertEquals(1, entries.size());
		CalendarEntry entry = entries.get(0);
		Assert.assertTrue(entry instanceof Event);
		Event event = (Event) entry;
		Assert.assertEquals("234ea713-7fc9-4860-80fe-8b6140271af6", entry.getUid());
		Assert.assertEquals("individual", event.getSummary());
		Assert.assertEquals(FreeBusyStatus.BUSY, event.getShowTimeAs());
		Assert.assertEquals(Tests.makeDateTime("20120629-1415"), event.getStartTime());
		Assert.assertEquals(Tests.makeDateTime("20120629-1515"), event.getEndTime());
		Assert.assertEquals("CANCELLED", event.getEventStatus());
	}
}
