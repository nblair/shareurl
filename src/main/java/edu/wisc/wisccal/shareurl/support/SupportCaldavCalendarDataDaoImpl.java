/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
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

package edu.wisc.wisccal.shareurl.support;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;

import org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDaoImpl;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple subclass of {@link CaldavCalendarDataDaoImpl} to expose some of it's protected methods
 * to this package.
 * 
 * @author Nicholas Blair
 */
class SupportCaldavCalendarDataDaoImpl extends CaldavCalendarDataDaoImpl {

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl#getCalendarsInternal(org.jasig.schedassist.model.ICalendarAccount, java.util.Date, java.util.Date)
	 */
	@Override
	protected List<CalendarWithURI> getCalendarsInternal(
			ICalendarAccount calendarAccount, Date startDate, Date endDate) {
		List<CalendarWithURI> calendars = null;
		calendars = super.getCalendarsInternal(calendarAccount, startDate, endDate);
		
		if(log.isTraceEnabled()){
			for(Map.Entry<String, List<String>> entry : calendarAccount.getAttributes().entrySet()){
				StringBuilder sb =  new StringBuilder(entry.getKey() + " = ");
				for(String s : entry.getValue()) sb.append(s+", ");
				log.trace("dumping calendarAccount attributes:\n"+sb.toString());
			}
		}
		
		
		if(!calendarAccount.getAttributeValues("wiscedumsolupn").isEmpty() && null != calendarAccount.getAttributeValues("wiscedumsolupn").get(0)){
			ApplicationContext ewsContext = 
					new ClassPathXmlApplicationContext("classpath:/org/jasig/schedassist/impl/exchange/calendarData-exchange.xml");
			ExchangeCalendarDataDaoImpl exchangeCalendarDataDao =  (ExchangeCalendarDataDaoImpl) ewsContext.getBean("exchangeCalendarDataDao");
			log.debug("getExchangeCalendar for "+ calendarAccount.getAttributeValues("wiscedumsolupn").get(0) + " from " + startDate + " to " + endDate);
			Calendar exchangeCalendar = exchangeCalendarDataDao.getCalendar(calendarAccount, startDate, endDate);
			log.debug("exchange returned this: " + exchangeCalendar);
			assert(null != exchangeCalendar);
			CalendarWithURI exchangeCalendarWithURI = new CalendarWithURI(exchangeCalendar, "exchangeCalendarDataTest");
			
			calendars.add(exchangeCalendarWithURI);
			log.debug("GetCalendarsInternal returned: "+ calendars.toString());	
		}
		

		assert(null != calendars);
		return calendars;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl#consolidate(java.util.List)
	 */
	@Override
	protected Calendar consolidate(List<CalendarWithURI> calendars) {
		return super.consolidate(calendars);
	}

	
	@Override
	public Map<String, String> listCalendars(ICalendarAccount account){
		return super.listCalendarsInternal(account);
	}

	
}
