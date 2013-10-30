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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Version;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import edu.wisc.wisccal.shareurl.domain.IncludeParticipantsPreference;
import edu.wisc.wisccal.shareurl.domain.PropertyMatchPreference;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EventFilterImplTest.java 1718 2010-02-15 21:53:22Z npblair $
 */
public class EventFilterImplTest {

	@Test
	public void testEmptyCalendar() throws Exception {
		Calendar calendar = new Calendar();
		SharePreferences preferences = new SharePreferences();
		
		EventFilterImpl filter = new EventFilterImpl();
		Calendar result = filter.filterEvents(calendar, preferences);
		Assert.assertNotNull(result);
		Assert.assertEquals(calendar, result);
	}
	
	@Test
	public void testNoFiltering() throws Exception {
		VEvent basicEvent = new VEvent(new Date("20091125"), new Date("20091126"), "some event");
		ComponentList components = new ComponentList();
		components.add(basicEvent);
		Calendar calendar = new Calendar(components);
		
		SharePreferences preferences = new SharePreferences();
		
		EventFilterImpl filter = new EventFilterImpl();
		Calendar result = filter.filterEvents(calendar, preferences);
		Assert.assertNotNull(result);
		Assert.assertEquals(calendar, result);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSummaryFilter() throws Exception {
		VEvent soccerEvent = new VEvent(new Date("20091125"), new Date("20091126"), "soccer against team A");
		VEvent footballEvent = new VEvent(new Date("20091125"), new Date("20091126"), "football against team A");
		ComponentList components = new ComponentList();
		components.add(soccerEvent);
		components.add(footballEvent);
		Calendar calendar = new Calendar(components);
		
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new PropertyMatchPreference(Summary.SUMMARY, "soccer"));
		
		EventFilterImpl filter = new EventFilterImpl();
		CalendarDataProcessor p = Mockito.mock(CalendarDataProcessor.class);
		filter.setCalendarDataProcessor(p);
		
		Calendar result = filter.filterEvents(calendar, preferences);
		Assert.assertNotNull(result);
		Assert.assertNotSame(calendar, result);
		Assert.assertEquals(1, result.getComponents().size());
		Assert.assertEquals(Version.VERSION_2_0, result.getVersion());
		Assert.assertEquals(new ProdId(CalendarDataUtils.SHAREURL_PROD_ID), result.getProductId());
	}
	
	/**
	 * Make sure an IncludeParticipantsPreference doesn't affect the result.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIncludeParticipantsFilter() throws Exception {
		VEvent soccerEvent = new VEvent(new Date("20091125"), new Date("20091126"), "soccer against team A");
		VEvent footballEvent = new VEvent(new Date("20091125"), new Date("20091126"), "football against team A");
		ComponentList components = new ComponentList();
		components.add(soccerEvent);
		components.add(footballEvent);
		Calendar calendar = new Calendar(components);
		
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new PropertyMatchPreference(Summary.SUMMARY, "soccer"));
		preferences.addPreference(new IncludeParticipantsPreference(true));
		
		EventFilterImpl filter = new EventFilterImpl();
		CalendarDataProcessor p = Mockito.mock(CalendarDataProcessor.class);
		filter.setCalendarDataProcessor(p);
		
		Calendar result = filter.filterEvents(calendar, preferences);
		Assert.assertNotNull(result);
		Assert.assertNotSame(calendar, result);
		Assert.assertEquals(1, result.getComponents().size());
		Assert.assertEquals(Version.VERSION_2_0, result.getVersion());
		Assert.assertEquals(new ProdId(CalendarDataUtils.SHAREURL_PROD_ID), result.getProductId());
	}
	
	/**
	 * Verify that VTIMEZONEs are kept when the filter is triggered by preferences.
	 * @throws Exception
	 */
	@Test
	public void testTimezonesRetained() throws Exception {
		ClassPathResource resource = new ClassPathResource("example-data/events-with-timezone.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		Assert.assertEquals(2, calendar.getComponents(VEvent.VEVENT).size());
		Assert.assertEquals(1, calendar.getComponents(VTimeZone.VTIMEZONE).size());
		
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new PropertyMatchPreference(Summary.SUMMARY, "individual"));
		
		EventFilterImpl filter = new EventFilterImpl();
		CalendarDataProcessor p = Mockito.mock(CalendarDataProcessor.class);
		filter.setCalendarDataProcessor(p);
		
		Calendar result = filter.filterEvents(calendar, preferences);
		Assert.assertNotNull(result);
		
		Assert.assertEquals(1, result.getComponents(VEvent.VEVENT).size());
		Assert.assertEquals(1, result.getComponents(VTimeZone.VTIMEZONE).size());
		
	}
}
