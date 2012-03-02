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
package edu.wisc.wisccal.shareurl.impl.caldav;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.Calendars;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.ICalendarDataDao;
import edu.wisc.wisccal.shareurl.impl.caldav.xml.ReportResponseHandlerImpl;

/**
 * CalDAV backed implementation of {@link ICalendarDataDao}.
 * Depends on an {@link HttpClient}.
 * 
 * @author Nicholas Blair
 * @version $Id: CaldavCalendarDataDaoImpl.java $
 */
@Service("calendarDataDao")
public class CaldavCalendarDataDaoImpl implements ICalendarDataDao, InitializingBean {

	private static final String CONTENT_LENGTH_HEADER = "Content-Length";

	private static final Header DEPTH_HEADER = new BasicHeader("Depth", "1");
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private AbstractHttpClient httpClient;
	private HttpHost httpHost;
	private Credentials caldavAdminCredentials;
	private AuthScope caldavAdminAuthScope;
	private CaldavDialect caldavDialect;
	/**
	 * @param httpClient the httpClient to set
	 */
	@Autowired
	public void setHttpClient(AbstractHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	/**
	 * @param httpHost the httpHost to set
	 */
	@Autowired
	public void setHttpHost(HttpHost httpHost) {
		this.httpHost = httpHost;
	}
	/**
	 * @param caldavAdminCredentials the caldavAdminCredentials to set
	 */
	@Autowired
	public void setCaldavAdminCredentials(Credentials caldavAdminCredentials) {
		this.caldavAdminCredentials = caldavAdminCredentials;
	}
	/**
	 * @param caldavAdminAuthScope the caldavAdminAuthScope to set
	 */
	@Autowired
	public void setCaldavAdminAuthScope(AuthScope caldavAdminAuthScope) {
		this.caldavAdminAuthScope = caldavAdminAuthScope;
	}
	/**
	 * @param caldavDialect the caldavDialect to set
	 */
	@Autowired
	public void setCaldavDialect(CaldavDialect caldavDialect) {
		this.caldavDialect = caldavDialect;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.httpClient.getCredentialsProvider().setCredentials(
				this.caldavAdminAuthScope, this.caldavAdminCredentials);
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarDataDao#getCalendarData(edu.wisc.wisccal.calendarkey.ICalendarAccount, java.util.Date, java.util.Date)
	 */
	@Override
	@Cacheable(cacheName="CalendarScheduleDataCache", selfPopulatingTimeout=10000,
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",
					properties={@Property(name="includeMethod",value="true")}))
	public Calendar getCalendarData(ICalendarAccount account, Date startDate,
			Date endDate) {
		List<CalendarWithURI> calendars = getCalendarsInternal(account, startDate, endDate);
		Calendar result = consolidate(calendars);
		return result;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarDataDao#getRawCalendarData(edu.wisc.wisccal.calendarkey.ICalendarAccount, java.util.Date, java.util.Date)
	 */
	@Override
	@Cacheable(cacheName="CalendarScheduleDataCache", selfPopulatingTimeout=10000,
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",properties=@Property(name="includeMethod",value="true")))
	public String getRawCalendarData(ICalendarAccount account, Date startDate,
			Date endDate) {
		Calendar calendar = getCalendarData(account, startDate, endDate);
		return calendar.toString();
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarDataDao#clearCalendarCache(edu.wisc.wisccal.calendarkey.ICalendarAccount, java.util.Date, java.util.Date)
	 */
	@Override
	@TriggersRemove(cacheName="CalendarScheduleDataCache", 
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",properties=@Property(name="includeMethod",value="false")))
	public final void clearCalendarCache(ICalendarAccount account, Date startDate,
			Date endDate) {
		// nothing to do
	}

	/**
	 * Consolidate the {@link Calendar}s within the argument, returning 1.
	 * 
	 * @see Calendars#merge(Calendar, Calendar)
	 * @param calendars
	 * @return never null
	 * @throws ParserException
	 */
	protected Calendar consolidate(List<CalendarWithURI> calendars) {
		final int size = calendars.size();
		if(size == 0) {
			return new Calendar();
		} else if(size == 1) {
			return calendars.get(0).getCalendar();
		} else if (size == 2) {
			return Calendars.merge(calendars.get(0).getCalendar(), calendars.get(1).getCalendar());
		} else {
			Calendar main = Calendars.merge(calendars.get(0).getCalendar(), calendars.get(1).getCalendar());
			for(int i = 2; i < size; i++) {
				main = Calendars.merge(main, calendars.get(i).getCalendar());
			}
			return main;
		}
	}
	
	/**
	 * 
	 * @param calendarAccount
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	protected List<CalendarWithURI> getCalendarsInternal(ICalendarAccount calendarAccount,
			Date startDate, Date endDate) {

		String accountUri = this.caldavDialect.getCalendarAccountHome(calendarAccount);
		HttpEntity requestEntity = caldavDialect.generateGetCalendarRequestEntity(startDate, endDate);
		ReportMethod method = new ReportMethod(accountUri);
		method.setEntity(requestEntity);
		method.addHeader(CONTENT_LENGTH_HEADER, Long.toString(requestEntity.getContentLength()));
		method.addHeader(DEPTH_HEADER);
		if(log.isDebugEnabled()) {
			log.debug("getCalendarsInternal executing " + methodToString(method) + " for " + calendarAccount + ", start " + startDate + ", end " + endDate);
		}
		
		HttpEntity responseEntity = null;
		try {
			HttpResponse response = this.httpClient.execute(httpHost, method);
			responseEntity = method.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			log.debug("getCalendarsInternal status code: " + statusCode);
			if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_MULTI_STATUS) {
				InputStream content = responseEntity.getContent();
				if(content != null) {
					ReportResponseHandlerImpl reportResponseHandler = new ReportResponseHandlerImpl();
					List<CalendarWithURI> calendars = reportResponseHandler.extractCalendars(content);
					return calendars;
				} else {
					return Collections.emptyList();
				}
			} else {
				throw new CaldavDataAccessException("unexpected status code: " + statusCode);
			}
		} catch (IOException e) {
			log.error("an IOException occurred in getCalendarsInternal for " + calendarAccount + ", " + startDate + ", " + endDate);
			throw new CaldavDataAccessException(e);
		} finally {
			quietlyConsume(responseEntity);
		}
	}
	
	/**
	 * Basic toString for {@link HttpRequest} to output method name and path.
	 * 
	 * @param method
	 * @return
	 */
	String methodToString(HttpRequest method) {
		return method.getRequestLine().toString();
	}
	
	/**
	 * 
	 * @param entity
	 */
	void quietlyConsume(HttpEntity entity) {
		try {
			EntityUtils.consume(entity);
		} catch (IOException e) {
			log.info("caught IOException from EntityUtils#consume", e);
		}
	}
}
