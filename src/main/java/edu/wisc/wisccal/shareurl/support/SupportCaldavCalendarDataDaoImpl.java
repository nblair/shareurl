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
import org.springframework.stereotype.Service;

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
