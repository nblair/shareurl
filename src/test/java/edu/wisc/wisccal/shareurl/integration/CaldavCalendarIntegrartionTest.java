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
package edu.wisc.wisccal.shareurl.integration;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.InputFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.wisc.wisccal.shareurl.IShareCalendarDataDao;
import edu.wisc.wisccal.shareurl.support.Calkey115CalendarDataDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:contexts/caldavContext-test.xml")
public class CaldavCalendarIntegrartionTest extends AbstractCalendarIntegrationTest {

	@Autowired
	IShareCalendarDataDao caldavDataDao;
	
	@Autowired
	CacheManager cacheManager;
	
	/**
	 * Simple integration test to see if {@link ICalendarDataDao#getCalendar(org.jasig.schedassist.model.ICalendarAccount, Date, Date)}
	 * returns data.
	 * 
	 * Before you run this test for the first time, import the event below into ownerCalendarAccount1's personal calendar:
	 * <pre>
	 * src/test/resources/vevent-examples/example-non-scheduling-assistant-conflict.ics
	 * </pre>
	 * 
	 * This test will fail until the aforementioned event is imported into the ownerCalendarAccount1 personal calendar.
	 * 
	 * @throws InputFormatException 
	 */
	@Test
	public void testGetCalendar() throws InputFormatException {
		//this test only works if the test hsql db has been started?
		
		Date start = CommonDateOperations.parseDateTimePhrase("20130507-1300");
		Date end = DateUtils.addHours(start, 1);
		log.info("getCalendar test, " + start + " to " + end + " for account " + account);
		Calendar calendar = caldavDataDao.getCalDavCalendars(account, start, end, null);
		Assert.assertNotNull(calendar);
		ComponentList events = calendar.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, events.size());
		VEvent event = (VEvent) events.get(0);
		Summary summary = event.getSummary();
		Assert.assertNotNull(summary);
		Assert.assertEquals("dentist appointment", summary.getValue());
	}
	
	

	
	@Test
	public void listCalDavCalendarsCacheTest(){
		assertNotNull(caldavDataDao);
		assertNotNull(cacheManager);
		
		Map<String, String> caldavNames = caldavDataDao.listCalendars(account);
		for (int i = 0; i < 10; i++) {
			caldavNames = caldavDataDao.listCalendars(account);
		}
		assertNotNull(caldavNames);
		
		List cacheKeys = cacheManager.getCache("calendarListCache").getKeys();
		for(Object key: cacheKeys){
			log.debug("calendarListCache contains Key="+key);
		}
		
		log.debug("CacheStatistics: "+cacheManager.getCache("calendarListCache").getStatistics());
	}
	
	@Test
	public void listCalDavCalendarsTest(){
		assertNotNull(caldavDataDao);
		
		Map<String, String> caldavNames = caldavDataDao.listCalendars(account);
		assertNotNull(caldavNames);
		
        Iterator<Entry<String, String>> it = caldavNames.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
            log.debug( pairs.getValue() + " = " + pairs.getKey());
            it.remove(); // avoids a ConcurrentModificationException
        }
	}
	
}
