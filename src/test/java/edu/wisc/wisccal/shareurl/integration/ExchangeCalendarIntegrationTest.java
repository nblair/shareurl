package edu.wisc.wisccal.shareurl.integration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.sf.ehcache.CacheManager;

import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.time.StopWatch;

import edu.wisc.wisccal.shareurl.domain.CalendarMatchPreference;

public class ExchangeCalendarIntegrationTest extends AbstractCalendarIntegrationTest {

	@Autowired
	ExchangeCalendarDataDao exchangeCalendarDataDao;

	@Autowired
	CacheManager cacheManager;
	
	/**
	 * Generate an iCalendar object for the specified email address and date
	 * range
	 */
	@Test
	public void getCalendarsTest() {
		assertNotNull(exchangeCalendarDataDao);

		Calendar exchangeCalendar = exchangeCalendarDataDao.getCalendar(
				ownerCalendarAccount1, startDate, endDate);
		CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(
				exchangeCalendar, "exchangeCalendarDataTest");

		log.debug(exchangeCalendarWithURI);
	}

	@Test
	public void getMultipleCalendarsTest() {
		Map<String, String> msolcals = exchangeCalendarDataDao
				.listCalendars(ownerCalendarAccount1);
		List<String> msoCalIds = new ArrayList<String>();
		Iterator<Entry<String, String>> it = msolcals.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();
			log.debug("Found MS calendar [" + pairs.getValue() + " = "
					+ pairs.getKey() + "]");
			msoCalIds.add(pairs.getKey());
		}

		Calendar exchangeCalendar = exchangeCalendarDataDao.getCalendar(
				ownerCalendarAccount1, startDate, endDate, msoCalIds);
		CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(
				exchangeCalendar, "exchangeCalendarDataTest");

		log.debug(exchangeCalendarWithURI);
	}

	/**
	 * Assert Exception is thrown but calendar is still returned
	 */
	@Test(expected=org.jasig.schedassist.impl.exchange.ExchangeCalendarDataAccessException.class)
	public void getCalendarBadIdTest() {
		String uri = "exchangeCalendarDataTest";
		String id = "GIBBERISHIDHEREAAMkADQ3ZmU3ZjYwLTllNjEtNDAzZi04ODM0LTllZTFkOGE4YzM3NAAuAAAAAAB/QZc5IOmFS4w/sb90KyZcAQDtFHiPdqQ8T4ySs/EY0e9MAAANKVQlAAA=";
		List<String> ids = new ArrayList<String>();
		ids.add(id);
		Calendar emptyCalendar = new Calendar();
		CalendarWithURI emptyCalendarWithUri = new CalendarWithURI(emptyCalendar, uri);
		
		Calendar exchangeCalendar = exchangeCalendarDataDao.getCalendar(
				ownerCalendarAccount1, startDate, endDate, ids);
		CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(
				exchangeCalendar, uri);
		
		//calendar should be empty
		assertEquals(exchangeCalendar, emptyCalendar );
		assertEquals(exchangeCalendarWithURI, emptyCalendarWithUri);		
		
	}

	/**
	 * 
	 * Obtain a Map<String=CalendarId, String=CalendarName> of all calendars for an ownerCalendarAccount1.
	 * Issue one findItem request for all CalendarIds 
	 * Issue a getItem request for every eventId returned
	 * Verify every event returned contains a X-CALNEDAR_MATCH property with a valid calendarId
	 * A calendarId is only valid if it is contained in map.keySet() and has a corresponding calendar (i.e. map.get(calendarId) != null )
	 * 
	 */
	@Test
	public void getCalendarByIdTest() {
		Map<String, String> msolcals = exchangeCalendarDataDao
				.listCalendars(ownerCalendarAccount1);
		List<String> msoCalIds = new ArrayList<String>();
		Iterator<Entry<String, String>> it = msolcals.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();

			
				log.debug("Found MS calendar [" + pairs.getValue() + " = "
						+ pairs.getKey() + "]");
				msoCalIds.add(pairs.getKey());
			
		}

		Calendar exchangeCalendar = exchangeCalendarDataDao.getCalendar(
				ownerCalendarAccount1, startDate, endDate, msoCalIds);
		
		assert(!exchangeCalendar.getComponents(VEvent.VEVENT).isEmpty());
		
		for(Iterator<?> i = exchangeCalendar.getComponents(VEvent.VEVENT).iterator(); i.hasNext(); ) {
			VEvent event = (VEvent) i.next();
			Property eventProperty = event.getProperties().getProperty(CalendarMatchPreference.CALENDAR_MATCH_PROPERTY_NAME);
			
			log.debug(CalendarMatchPreference.CALENDAR_MATCH_PROPERTY_NAME +" = "+ eventProperty.getValue());
			log.debug("Event summary = "+ event.getSummary());
						
			assertNotNull(eventProperty);
			assert(msoCalIds.contains(eventProperty.getValue()));
			assert(msolcals.containsKey(eventProperty.getValue()));
			assertNotNull(msolcals.get(eventProperty.getValue()));
			
			String calendarName = msolcals.get(eventProperty.getValue());
			log.debug("CalName="+calendarName);
		}
		
		CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(
				exchangeCalendar, "exchangeCalendarDataTest");

		log.debug(exchangeCalendarWithURI);
	}

	@Test
	public void listCalendarsCacheTest() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		Map<String, String> msolcals = exchangeCalendarDataDao.listCalendars(ownerCalendarAccount1);
		for (int i = 0; i < 10; i++) {
			msolcals = exchangeCalendarDataDao.listCalendars(ownerCalendarAccount1);
		}
		
		long time = stopWatch.getTime();
		log.debug("Retrieved 10 Calendar Lists in "+time+" msecs");
		
		log.debug("CacheManager status: "+cacheManager.getStatus());
		for(String c : cacheManager.getCacheNames()){
			log.debug(c+" stats: "+ cacheManager.getCache(c));
		}
	}

	/**
	 * List non primary calendar names and id's
	 */
	@Test
	public void listCalendarsTest() {
		Map<String, String> msolcals = exchangeCalendarDataDao
				.listCalendars(ownerCalendarAccount1);
		assertNotNull(msolcals);

		Iterator<Entry<String, String>> it = msolcals.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();

			log.debug(pairs.getValue() + " = " + pairs.getKey());

		}
	}
}
