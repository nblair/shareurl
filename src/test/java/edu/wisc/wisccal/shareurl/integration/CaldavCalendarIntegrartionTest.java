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

public class CaldavCalendarIntegrartionTest extends AbstractCalendarIntegrationTest {

	@Autowired
	IShareCalendarDataDao caldavDataDao;
	
	@Autowired
	CacheManager cacheManager;
	
	/**
	 * Simple integration test to see if {@link ICalendarDataDao#getCalendar(org.jasig.schedassist.model.ICalendarownerCalendarAccount1, Date, Date)}
	 * returns data.
	 * 
	 * Before you run this test for the first time, import the event below into ownerCalendarownerCalendarAccount11's personal calendar:
	 * <pre>
	 * src/test/resources/vevent-examples/example-non-scheduling-assistant-conflict.ics
	 * </pre>
	 * 
	 * This test will fail until the aforementioned event is imported into the ownerCalendarownerCalendarAccount11 personal calendar.
	 * 
	 * @throws InputFormatException 
	 */
	@Test
	public void testGetCalendar() throws InputFormatException {
		
		Date start = CommonDateOperations.parseDateTimePhrase("20130507-1300");
		Date end = DateUtils.addHours(start, 1);
		log.info("getCalendar test, " + start + " to " + end + " for ownerCalendarAccount1 " + ownerCalendarAccount1);
		Calendar calendar = caldavDataDao.getCalDavCalendars(ownerCalendarAccount1, start, end, null);
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
		
		Map<String, String> caldavNames = caldavDataDao.listCalendars(ownerCalendarAccount1);
		for (int i = 0; i < 10; i++) {
			caldavNames = caldavDataDao.listCalendars(ownerCalendarAccount1);
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
		
		Map<String, String> caldavNames = caldavDataDao.listCalendars(ownerCalendarAccount1);
		assertNotNull(caldavNames);
		
        Iterator<Entry<String, String>> it = caldavNames.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
            log.debug( pairs.getValue() + " = " + pairs.getKey());
            it.remove(); // avoids a ConcurrentModificationException
        }
	}
	
}
