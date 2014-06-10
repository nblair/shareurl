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
package edu.wisc.wisccal.shareurl.support;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.impl.exchange.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.microsoft.exchange.*;
import com.microsoft.exchange.impl.ExchangeOnlineThrottlingPolicy;
import com.microsoft.exchange.impl.ExchangeWebServicesClient;
import com.microsoft.exchange.impl.ThreadLocalImpersonationConnectingSIDSourceImpl;
import com.microsoft.exchange.messages.FindItem;
import com.microsoft.exchange.messages.FindItemResponse;
import com.microsoft.exchange.messages.FindItemResponseMessageType;
import com.microsoft.exchange.messages.ResponseMessageType;
import com.microsoft.exchange.types.ArrayOfRealItemsType;
import com.microsoft.exchange.types.CalendarItemType;
import com.microsoft.exchange.types.CalendarViewType;
import com.microsoft.exchange.types.ConnectingSIDType;
import com.microsoft.exchange.types.DefaultShapeNamesType;
import com.microsoft.exchange.types.DistinguishedFolderIdNameType;
import com.microsoft.exchange.types.DistinguishedFolderIdType;
import com.microsoft.exchange.types.EmailAddressType;
import com.microsoft.exchange.types.FindItemParentType;
import com.microsoft.exchange.types.FolderIdType;
import com.microsoft.exchange.types.ItemQueryTraversalType;
import com.microsoft.exchange.types.ItemResponseShapeType;
import com.microsoft.exchange.types.ItemType;
import com.microsoft.exchange.types.NonEmptyArrayOfBaseFolderIdsType;

import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDaoImpl;
/**
 * 
 * @author Nicholas Blair
 */
public final class Calendars {

	public static final String CONFIG = System.getProperty(
			"edu.wisc.wisccal.shareurl.support.Calendars.CONFIG", 
			"cli.xml");
	private static final Log MAIN_LOG = LogFactory.getLog(Calendars.class);
	private Calkey115CalendarDataDaoImpl calendarDataDao;
	private ICalendarAccountDao calendarAccountDao;
	private String targetAccount;
	private Date startDate;
	private Date endDate;
	private String dateFormat = "yyyyMMdd";
	
	
	/**
	 * @return the calendarDataDao
	 */
	public Calkey115CalendarDataDaoImpl getCalendarDataDao() {
		return calendarDataDao;
	}
	/**
	 * @param calendarDataDao the calendarDataDao to set
	 */
	@Autowired
	public void setCalendarDataDao(Calkey115CalendarDataDaoImpl calendarDataDao) {
		this.calendarDataDao = calendarDataDao;
	}
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @return the targetAccount
	 */
	public String getTargetAccount() {
		return targetAccount;
	}
	/**
	 * @param targetAccount the targetAccount to set
	 */
	@Value("${support.targetAccount.emailAddress}")
	public void setTargetAccount(String targetAccount) {
		this.targetAccount = targetAccount;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	@Value("${support.dateformat:yyyyMMdd}")
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * 
	 * @param startDateValue
	 * @throws ParseException
	 */
	@Value("${support.startdate}")
	public void setStartDateValue(String startDateValue) throws ParseException {
		if(StringUtils.isNotBlank(startDateValue)) {
			SimpleDateFormat df = new SimpleDateFormat(getDateFormat());
			setStartDate(CommonDateOperations.beginningOfDay(df.parse(startDateValue)));
		}
	}
	/**
	 * 
	 * @param endDateValue
	 * @throws ParseException
	 */
	@Value("${support.enddate}")
	public void setEndDateValue(String endDateValue) throws ParseException {
		if(StringUtils.isNotBlank(endDateValue)) {
			SimpleDateFormat df = new SimpleDateFormat(getDateFormat());
			setEndDate(CommonDateOperations.endOfDay(df.parse(endDateValue)));
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
				
		Calendars instance = context.getBean(Calendars.class);
		Calkey115CalendarDataDaoImpl cInstance = instance.getCalendarDataDao();
		ICalendarAccount account = instance.getCalendarAccountDao().getCalendarAccount(instance.getTargetAccount());
		
		Date startDate = instance.getStartDate();
		Date endDate = instance.getEndDate();
		
		assert(null != startDate);
		assert(null != endDate);
		assert(null != instance);
		assert(null != cInstance);
		assert(null != account);
		
		
		MAIN_LOG.info("calling getCalendarsInternal for " + account.getEmailAddress() + " and date range " + startDate + " through " + endDate);
		Calendar calendar = cInstance.getCalendar(account,startDate,endDate);
	
		MAIN_LOG.info("getCalendars returns: " + calendar);
//		Calendar consolidated = cInstance.consolidate(calendar);
//		MAIN_LOG.info("after consolidate: " + consolidated);
		
		System.exit(0);
		}
	
	
	
	
}
