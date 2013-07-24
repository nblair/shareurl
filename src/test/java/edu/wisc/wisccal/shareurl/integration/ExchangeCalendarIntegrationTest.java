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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXBElement;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.sf.ehcache.CacheManager;

import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.microsoft.exchange.DateHelp;
import com.microsoft.exchange.ExchangeEventConverter;
import com.microsoft.exchange.ExchangeRequestHelp;
import com.microsoft.exchange.impl.ExchangeWebServicesClient;
import com.microsoft.exchange.messages.FindItem;
import com.microsoft.exchange.messages.FindItemResponse;
import com.microsoft.exchange.messages.FindItemResponseMessageType;
import com.microsoft.exchange.messages.GetItem;
import com.microsoft.exchange.messages.GetItemResponse;
import com.microsoft.exchange.messages.ItemInfoResponseMessageType;
import com.microsoft.exchange.messages.ResponseMessageType;
import com.microsoft.exchange.types.ArrayOfRealItemsType;
import com.microsoft.exchange.types.CalendarItemType;
import com.microsoft.exchange.types.DefaultShapeNamesType;
import com.microsoft.exchange.types.FindItemParentType;
import com.microsoft.exchange.types.ItemType;

import edu.wisc.wisccal.shareurl.domain.CalendarMatchPreference;

import org.apache.commons.lang.time.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/contexts/exchangeContext-test.xml"})
public class ExchangeCalendarIntegrationTest extends
		AbstractCalendarIntegrationTest {

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
				account, startDate, endDate);
		CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(
				exchangeCalendar, "exchangeCalendarDataTest");

		log.debug(exchangeCalendarWithURI);
	}

	@Test
	public void getMultipleCalendarsTest() {
		Map<String, String> msolcals = exchangeCalendarDataDao
				.listCalendars(account);
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
				account, startDate, endDate, msoCalIds);
		CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(
				exchangeCalendar, "exchangeCalendarDataTest");

		log.debug(exchangeCalendarWithURI);
	}

	@Test
	public void getCalendarBadIdTest() {
		String uri = "exchangeCalendarDataTest";
		String id = "GIBBERISHIDHEREAAMkADQ3ZmU3ZjYwLTllNjEtNDAzZi04ODM0LTllZTFkOGE4YzM3NAAuAAAAAAB/QZc5IOmFS4w/sb90KyZcAQDtFHiPdqQ8T4ySs/EY0e9MAAANKVQlAAA=";
		List<String> ids = new ArrayList<String>();
		ids.add(id);
		Calendar emptyCalendar = new Calendar();
		CalendarWithURI emptyCalendarWithUri = new CalendarWithURI(emptyCalendar, uri);
		
		Calendar exchangeCalendar = exchangeCalendarDataDao.getCalendar(
				account, startDate, endDate, ids);
		CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(
				exchangeCalendar, uri);
		
		//calendar should be empty
		assertEquals(exchangeCalendar, emptyCalendar );
		assertEquals(exchangeCalendarWithURI, emptyCalendarWithUri);		
		
	}

	/**
	 * 
	 * Obtain a Map<String=CalendarId, String=CalendarName> of all calendars for an account.
	 * Issue one findItem request for all CalendarIds 
	 * Issue a getItem request for every eventId returned
	 * Verify every event returned contains a X-CALNEDAR_MATCH property with a valid calendarId
	 * A calendarId is only valid if it is contained in map.keySet() and has a corresponding calendar (i.e. map.get(calendarId) != null )
	 * 
	 */
	@Test
	public void getCalendarByIdTest() {
		Map<String, String> msolcals = exchangeCalendarDataDao
				.listCalendars(account);
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
				account, startDate, endDate, msoCalIds);
		
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
		
		Map<String, String> msolcals = exchangeCalendarDataDao.listCalendars(account);
		for (int i = 0; i < 10; i++) {
			msolcals = exchangeCalendarDataDao.listCalendars(account);
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
				.listCalendars(account);
		assertNotNull(msolcals);

		Iterator<Entry<String, String>> it = msolcals.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();

			log.debug(pairs.getValue() + " = " + pairs.getKey());

		}
	}

	@Deprecated
	@Test
	public void getExchangeCalendars() {
		ArrayList<CalendarWithURI> eCalendars = new ArrayList<CalendarWithURI>();
		ApplicationContext ewsContext = new ClassPathXmlApplicationContext(
				"classpath:com/microsoft/exchange/exchangeContext-usingImpersonation.xml");
		ExchangeWebServicesClient ewsClient = ewsContext
				.getBean(ExchangeWebServicesClient.class);
		String startDateString = "2013-04-12";
		String endDateString = "2013-04-13";
		String emailAddress = "ctcudd@ad-test.wisc.edu";

		ExchangeEventConverter eec = new ExchangeEventConverter();
		ExchangeRequestHelp erh = new ExchangeRequestHelp();

		erh.initializeCredentials(emailAddress);
		FindItem request = erh.constructFindItemRequest(
				DateHelp.makeDate(startDateString),
				DateHelp.makeDate(endDateString), emailAddress,
				DefaultShapeNamesType.ID_ONLY);
		FindItemResponse response = ewsClient.findItem(request);

		List<JAXBElement<? extends ResponseMessageType>> responseList = response
				.getResponseMessages()
				.getCreateItemResponseMessagesAndDeleteItemResponseMessagesAndGetItemResponseMessages();

		// iterate over responses
		for (JAXBElement<? extends ResponseMessageType> rm : responseList) {
			FindItemResponseMessageType itemType = (FindItemResponseMessageType) rm
					.getValue();
			FindItemParentType rootFolder = itemType.getRootFolder();
			ArrayOfRealItemsType itemArray = rootFolder.getItems();
			List<ItemType> items = itemArray
					.getItemsAndMessagesAndCalendarItems();

			// iterate over items in each response
			for (ItemType item : items) {
				CalendarItemType calItem = (CalendarItemType) item;
				GetItem getItemRequest = erh.constructGetItemRequest(calItem);
				GetItemResponse getItemResponse = ewsClient
						.getItem(getItemRequest);
				log.info("GetItemResponse " + getItemResponse.toString());

				// iterate over getItemResponseMessages
				List<JAXBElement<? extends ResponseMessageType>> getItemResponseList = getItemResponse
						.getResponseMessages()
						.getCreateItemResponseMessagesAndDeleteItemResponseMessagesAndGetItemResponseMessages();

				// iterate over getItemResponseList
				for (JAXBElement<? extends ResponseMessageType> getItemResponseMessage : getItemResponseList) {
					ItemInfoResponseMessageType itemInfoResponseMessageType = (ItemInfoResponseMessageType) getItemResponseMessage
							.getValue();
					ArrayOfRealItemsType itemsArray = itemInfoResponseMessageType
							.getItems();
					for (ItemType currentCalItem : itemsArray
							.getItemsAndMessagesAndCalendarItems()) {
						eec.add((CalendarItemType) currentCalItem);
					}
				}
			}
			eCalendars.add(new CalendarWithURI(eec.ical, "microsoftEWSTest"));
		}
		log.debug("GetCalenderObjects returned: " + eec.ical.toString());

		assertNotNull(eCalendars);
	}
}
