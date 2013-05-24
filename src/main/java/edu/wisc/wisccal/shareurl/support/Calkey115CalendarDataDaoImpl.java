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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDao;
import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDaoImpl;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;

import edu.wisc.wisccal.shareurl.IShareCalendarDataDao;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.CalendarMatchPreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;

/**
 * See https://jira.doit.wisc.edu/jira/browse/CALKEY-115
 * 
 * @author Nicholas Blair
 */
public class Calkey115CalendarDataDaoImpl extends CaldavCalendarDataDaoImpl implements IShareCalendarDataDao {
	private IShareDao shareDao;
	private TimeZoneRegistry _registry = TimeZoneRegistryFactory.getInstance().createRegistry();
	private CacheManager cacheManager;
	
	
	private ExchangeCalendarDataDao exchangeCalendarDataDao;
	
	/**
	 * @return the exchangeCalendarDataDao
	 */
	public ExchangeCalendarDataDao getExchangeCalendarDataDao() {
		return exchangeCalendarDataDao;
	}
	/**
	 * @param exchangeCalendarDataDao the exchangeCalendarDataDao to set
	 */
	@Autowired
	public void setExchangeCalendarDataDao(ExchangeCalendarDataDao exchangeCalendarDataDao) {
		this.exchangeCalendarDataDao = exchangeCalendarDataDao;
	}
	/**
	 * 
	 * @return
	 */
	public IShareDao getShareDao() {
		return shareDao;
	}
	/**
	 * 
	 * @param shareDao
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
	}

	
	public CacheManager getCacheManager() {
		return cacheManager;
	}


	@Autowired
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	protected List<CalendarWithURI> getCalendarsInternal(ICalendarAccount calendarAccount, Share share, Date startDate, Date endDate) {
		List<CalendarWithURI> calendars = new ArrayList<CalendarWithURI>();
		List<String> exchangeCalendarIds = new ArrayList<String>();
		List<String> caldavCalendarIds = new ArrayList<String>();
		String shareKey =share.getKey();
		
		log.debug("getCalendarsInternal for shareKey="+shareKey+", acccount="+calendarAccount.getCalendarUniqueId() +" from "+startDate +" to "+ endDate );
		
		Set<ISharePreference> prefs = share.getSharePreferences().getCalendarMatchPreferences();
		if(!prefs.isEmpty()){
			for(ISharePreference p: prefs){
				//p.getKey.StartsWith()
				if(p.getKey().startsWith(CalendarMatchPreference.ORACLE_CALENDAR_IDENTIFIER)){
					//this is a calDav calendar
					StringBuilder accountUri = new StringBuilder(this.getCaldavDialect().getAccountHome(calendarAccount));
					accountUri.append("/");
					accountUri.append(p.getValue());
					caldavCalendarIds.add(accountUri.toString());
					///if 
				}else if(p.getKey().startsWith(CalendarMatchPreference.EXCHANGE_CALENDAR_IDENTIFIER)){
					//this is an exchange calendar
					exchangeCalendarIds.add(p.getValue());
				}else{
					log.warn("Calendar type not recognized and will be ignored");		
				}
			}
		}
		
		StringBuilder message = new StringBuilder();
		
		//if any exchangeIds were found, retrieve the corresponding calendars.
		if(!exchangeCalendarIds.isEmpty()){
			message.append("Retrieving exchange calendars for the following ids: ");
			for(String s : exchangeCalendarIds){
				message.append("\n"+s);
			}
			CalendarWithURI exchangeCalendarWithURI = getExchangeCalendars(calendarAccount, startDate, endDate,exchangeCalendarIds);
			if(exchangeCalendarWithURI !=null) calendars.add(exchangeCalendarWithURI);

		}
		
		//if any calDav calendars were found....
		if(!caldavCalendarIds.isEmpty()){
			message.append("Retrieving caldav calendars for the following ids: ");
			for(String cid: caldavCalendarIds){
				message.append("\n"+cid);
				List<CalendarWithURI> caldavCalendarWithURI =  super.getCalendarsInternal(calendarAccount, startDate, endDate,cid );
				if(caldavCalendarWithURI != null)calendars.addAll(caldavCalendarWithURI);
			}
		}
		
		//if no calendarIds are found retrieve the default 
		if(exchangeCalendarIds.isEmpty() && caldavCalendarIds.isEmpty()){
			message.append("No Calendar IDs found.  Retrieving default calDav calendar.");
			List<CalendarWithURI> caldavCalendarWithURI =  super.getCalendarsInternal(calendarAccount, startDate, endDate );
			if(caldavCalendarWithURI != null)calendars.addAll(caldavCalendarWithURI);
		}
		
		log.debug(message.toString());
		
		for(String c : cacheManager.getCacheNames()){
			log.debug(c+" stats: "+ cacheManager.getCache(c));
		}
		
		assert(null != calendars);
		//the calendarid must be added to every event returned
		for(CalendarWithURI cwu : calendars){
			log.debug("GetCalendarsInternal returned calendar with URI: "+cwu.getUri().toString());
		}
		
		log.debug("GetCalendarsInternal returned: "+ calendars.toString());	
		return calendars;
	}
	
	
	/* (non-Javadoc)
	 * 
	 * 
	 * Retrieve every calendar that a user has created a CALENDAR_MATCH preference for.  
	 * If no preferences are defined return only the default WiscCal calendar
	 * 
	 * @see org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl#getCalendarsInternal(org.jasig.schedassist.model.ICalendarAccount, java.util.Date, java.util.Date)
	 */
	@Override
	protected List<CalendarWithURI> getCalendarsInternal(ICalendarAccount calendarAccount, Date startDate, Date endDate) {
		
		List<CalendarWithURI> calendars = new ArrayList<CalendarWithURI>();
		List<String> exchangeCalendarIds = new ArrayList<String>();
		List<String> caldavCalendarIds = new ArrayList<String>();
		
		//retrieve shares
		List<Share> shares = shareDao.retrieveByOwner(calendarAccount);
		if(shares.size() > 0){
			for( Share s : shares){
				Set<ISharePreference> prefs = s.getSharePreferences().getCalendarMatchPreferences();
				//if a CALENDAR_MATCH pref exists, retrieve multiple calendars
				if(!prefs.isEmpty()){
					for(ISharePreference p: prefs){
						//p.getKey.StartsWith()
						if(p.getKey().startsWith(CalendarMatchPreference.ORACLE_CALENDAR_IDENTIFIER)){
							//this is a calDav calendar
							StringBuilder accountUri = new StringBuilder(this.getCaldavDialect().getAccountHome(calendarAccount));
							accountUri.append("/");
							accountUri.append(p.getValue());
							caldavCalendarIds.add(accountUri.toString());
							///if 
						}else if(p.getKey().startsWith(CalendarMatchPreference.EXCHANGE_CALENDAR_IDENTIFIER)){
							//this is an exchange calendar
							exchangeCalendarIds.add(p.getValue());
						}else{
							log.warn("Calendar type not recognized and will be ignored");		
						}
					}
				}
			}
		}
		
		StringBuilder message = new StringBuilder();
		
		//if any exchangeIds were found, retrieve the corresponding calendars.
		if(!exchangeCalendarIds.isEmpty()){
			message.append("Retrieving exchange calendars for the following ids: ");
			for(String s : exchangeCalendarIds){
				message.append("\n"+s);
			}
			CalendarWithURI exchangeCalendarWithURI = getExchangeCalendars(calendarAccount, startDate, endDate,exchangeCalendarIds);
			if(exchangeCalendarWithURI !=null) calendars.add(exchangeCalendarWithURI);

		}
		
		//if any calDav calendars were found....
		if(!caldavCalendarIds.isEmpty()){
			message.append("Retrieving caldav calendars for the following ids: ");
			for(String cid: caldavCalendarIds){
				message.append("\n"+cid);
				List<CalendarWithURI> caldavCalendarWithURI =  super.getCalendarsInternal(calendarAccount, startDate, endDate,cid );
				if(caldavCalendarWithURI != null)calendars.addAll(caldavCalendarWithURI);
			}
		}
		
		//if no calendarIds are found retrieve the default 
		if(exchangeCalendarIds.isEmpty() && caldavCalendarIds.isEmpty()){
			message.append("No Calendar IDs found.  Retrieving default calDav calendar.");
			List<CalendarWithURI> caldavCalendarWithURI =  super.getCalendarsInternal(calendarAccount, startDate, endDate );
			if(caldavCalendarWithURI != null)calendars.addAll(caldavCalendarWithURI);
		}
		
		log.debug(message.toString());
		
		for(String c : cacheManager.getCacheNames()){
			log.debug(c+" stats: "+ cacheManager.getCache(c));
		}
		
		assert(null != calendars);
		//the calendarid must be added to every event returned
		for(CalendarWithURI cwu : calendars){
			log.debug("GetCalendarsInternal returned calendar with URI: "+cwu.getUri().toString());
		}
		
		log.debug("GetCalendarsInternal returned: "+ calendars.toString());	
		return calendars;
	}
		

	
	protected CalendarWithURI getExchangeCalendars(ICalendarAccount calendarAccount, Date startDate, Date endDate, List<String> calendarIds){
		CalendarWithURI exchangeCalendarWithURI = null;
		if(!calendarAccount.getAttributeValues("wiscedumsolupn").isEmpty() && null != calendarAccount.getAttributeValues("wiscedumsolupn").get(0)){
			
//			ApplicationContext ewsContext = 
//					new ClassPathXmlApplicationContext("classpath:/org/jasig/schedassist/impl/exchange/calendarData-exchange.xml");
//			ExchangeCalendarDataDaoImpl exchangeCalendarDataDao =  (ExchangeCalendarDataDaoImpl) ewsContext.getBean("exchangeCalendarDataDao");
//			
			
			log.debug("getExchangeCalendar for "+ calendarAccount.getAttributeValues("wiscedumsolupn").get(0) + " from " + startDate + " to " + endDate);
			
			Calendar exchangeCalendar = exchangeCalendarDataDao.getCalendar(calendarAccount, startDate, endDate,calendarIds);
			log.debug("exchange returned this: " + exchangeCalendar);
			assert(null != exchangeCalendar);
			exchangeCalendarWithURI = new CalendarWithURI(exchangeCalendar, "exchangeCalendarDataTest");
			
	
		}
		return exchangeCalendarWithURI;
		
	}
	
	protected CalendarWithURI getExchangeCalendars(ICalendarAccount calendarAccount, Date startDate, Date endDate){
		return getExchangeCalendars(calendarAccount, startDate, endDate, null);
	}
	
	/**
	 * Calls the super, then passes the result through {@link #checkEventsAndInspectTimeZones(Calendar)}.
	 *  (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl#consolidate(java.util.List)
	 */
	@Override
	protected Calendar consolidate(List<CalendarWithURI> calendars) {
		Calendar result = super.consolidate(calendars);
		checkEventsAndInspectTimeZones(result);
		return result;
	}

	/**
	 * Potentially mutative method for components within the calendar argument.
	 * Performs 2 operations:
	 * <ol>
	 * <li>Looks for events damaged by WMG-1507. If found and has {@link Transp#OPAQUE} and sets a SUMMARY property of "Busy". If has {@link Transp#TRANSPARENT}, event is removed from output.</li>
	 * <li>Looks for damaged {@link VTimeZone}s and replaces them with the correct instance.</li>
	 * </ol>
	 * 
	 * @param calendar
	 */
	@SuppressWarnings("unchecked")
	protected void checkEventsAndInspectTimeZones(Calendar calendar) {
		Set<VTimeZone> toAdd = new HashSet<VTimeZone>();
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VTimeZone.VTIMEZONE.equals(component.getName())) {
				VTimeZone vtimezone = (VTimeZone) component;
				if(vtimezone.getObservances().size() == 0) {
					log.debug("detected damaged vtimezone for CALKEY-115 workaround: " + vtimezone );
					TimeZone timezone = _registry.getTimeZone(vtimezone.getTimeZoneId().getValue());
					toAdd.add(timezone.getVTimeZone());
					i.remove();
				}
			} else if (VEvent.VEVENT.equals(component.getName())) {
				VEvent vevent = (VEvent) component;
				if(vevent.getSummary() == null && vevent.getLastModified() == null) {
					log.debug("detected damaged event for CALKEY-115 workaround: " + CalendarDataUtils.staticGetDebugId(component) );
					Transp transp = vevent.getTransparency();
					if(transp == null || Transp.OPAQUE.equals(transp)) {
						vevent.getProperties().add(new Summary("Busy"));
					} else {
						i.remove();
					}
				}
			}
		}
		if(!toAdd.isEmpty()) {
			calendar.getComponents().addAll(toAdd);
		}
	}
	


	public Calendar getCalDavCalendars(ICalendarAccount account, Date start, Date end) {
		return getCalDavCalendars(account, start, end, null);
	}
	
	public Calendar getCalDavCalendars(ICalendarAccount calendarAccount, Date startDate, Date endDate, String accountUri){
		if(null == accountUri){
			return consolidate(
					getCalendarsInternal(
							calendarAccount, startDate, endDate));
		}
		return consolidate(
				getCalendarsInternal(
							calendarAccount, startDate, endDate,accountUri));
	}
	@Override
	public Calendar getCalendar(ICalendarAccount account, Share share,
			Date startDate, Date endDate) {
		
		List<CalendarWithURI> calendarsInternal = getCalendarsInternal( account,  share,  startDate,  endDate);
		return consolidate(calendarsInternal);
	}

}
