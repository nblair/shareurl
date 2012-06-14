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
package edu.wisc.wisccal.shareurl.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;
import edu.wisc.wisccal.shareurl.ical.IEventFilter;
import edu.wisc.wisccal.shareurl.ical.VEventComparator;

/**
 * Controller for displaying "shared" calendars.
 * This controller has a number of features for the format of the output.
 * 
 * <p>The url format for accessing a share is:</p>
 * <pre>
 * [web application context root]/u/[SHAREID]
 * </pre>
 * <p>Accessing a url in the base format will return the specified share's calendar for "today" in
 * HTML format.</p>
 * <p>You can also display the share's calendar for a wider date range, using the following format:</p>
 * <pre>
 * [web application context root]/u/[SHAREID]/dr([days backward, days forward])
 * </pre>
 * <p>The "days backward" and "days forward" parameters are integers less than or equal to MAX_RANGE (60 days by default).</p>
 * <p>An individual event within a calendar can be viewed by tacking the value of it's iCalendar UID to the env of the url:</p>
 * <pre>
 * [web application context root]/u/[SHAREID]/event-uid
 * [web application context root]/u/[SHAREID]/dr([days backward, days forward])/event-uid
 * </pre>
 * <p>The individual event will only be shown if it exists within the specified date range.</p>
 * <p>If you prefer to receive the data in Really Simple Syndication (RSS) format, simply add the request parameter "rss":</p>
 * <pre>
 * [web application context root]/u/[SHAREID]?rss
 * [web application context root]/u/[SHAREID]/dr([days backward, days forward])?rss
 * </pre>
 * <p>If you prefer to receive the data in RFC 2445 (iCalendar) format, simply add the request parameter "ical":</p>
 * <pre>
 * [web application context root]/u/[SHAREID]?ical
 * [web application context root]/u/[SHAREID]/dr([days backward, days forward])?ical
 * </pre>
 * <p>
 * This will return the data (as is from the CalendarDao) with a MIME type of "text/calendar" - which allows
 * iCalendar handlers to be executed from clicking the link, and is the expected type for iCalendar compatible clients.
 * </p>
 * <p>
 * If you would like to see the iCalendar data, but as "text/plain" (so it can be rendered "in-browser"), add the
 * request parameter "asText":
 * <pre>
 * [web application context root]/u/[SHAREID]?ical&asText
 * [web application context root]/u/[SHAREID]/dr([days backward, days forward])?ical&asText
 * </pre>
 * "asText" only registers in conjunction with the presence of the "ical" request parameter.
 * </p>
 * 
 * @see ShareRequestDetails
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SharedCalendarController.java 3543 2011-12-13 22:33:19Z npblair $
 */
@Controller
public class SharedCalendarController {

	//private static final String TEXT_HTML = "text/html";

	private static final String UTF_8 = "UTF-8";

	private Log LOG = LogFactory.getLog(this.getClass());

	private static final String ICS = ".ics";
	private static final String IFB = ".ifb";

	private IShareDao shareDao;
	private ICalendarDataDao calendarDataDao;
	private ICalendarAccountDao calendarAccountDao;
	private IEventFilter eventFilter;

	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
	}
	/**
	 * @param calendarDataDao the calendarDataDao to set
	 */
	@Autowired
	public void setCalendarDataDao(ICalendarDataDao calendarDataDao) {
		this.calendarDataDao = calendarDataDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("composite") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param eventFilter the eventFilter to set
	 */
	@Autowired
	public void setEventFilter(IEventFilter eventFilter) {
		this.eventFilter = eventFilter;
	}

	/**
	 * Mutative method.
	 * 
	 * The purpose of this method is to inspect the share preferences and the request
	 * itself, alter the calendar data accordingly, and prepare the ModelMap argument.
	 * 
	 * This method alters the {@link Calendar} and the {@link ModelMap} arguments.
	 * 
	 * @param share
	 * @param requestDetails
	 * @param agenda
	 * @param account
	 * @param model
	 */
	protected void prepareModel(SharePreferences sharePreferences, ShareRequestDetails requestDetails, 
			Calendar agenda, ICalendarAccount account, ModelMap model) {
		final ShareDisplayFormat display = requestDetails.getDisplayFormat();

		// are we targeting a markup display?
		if(display.isMarkupLanguage()) {
			// - expandrecurrence first priority
			// - "addressable" UID properties for each event, including recurrence instances
			expandRecurrenceAndAlterUid(agenda, requestDetails.getStartDate(), requestDetails.getEndDate());

			// - filter VEvents to those only with DTSTART within requestDetails start/end
			ShareHelper.filterAgendaForDateRange(agenda, requestDetails);
			// - markup views don't care about recurrence properties proper
			
			ComponentList components = agenda.getComponents(VEvent.VEVENT);

			model.put("empty", components.size() == 0);
			@SuppressWarnings("unchecked")
			List<VEvent> allEvents = new ArrayList<VEvent>(components);

			Collections.sort(allEvents, new VEventComparator());

			model.put("allEvents", allEvents);
			model.put("startDate", requestDetails.getStartDate());
			model.put("endDate", requestDetails.getEndDate());
			model.put("shareId", requestDetails.getShareKey());
			model.put("datePhrase", requestDetails.getDatePhrase());	
		} else {
			// we are targeting iCalendar output
			// - don't expand recurrence! let the clients figure it out
			// - remove participants if necessary
			if(!sharePreferences.isIncludeParticipants()) {
				CalendarDataUtils.removeParticipants(agenda, account);
			}

			// - but, break recurrence if necessary
			if(requestDetails.requiresBreakRecurrence()) {
				CalendarDataUtils.breakRecurrence(agenda);
			}

			// - convert class if necessary
			if(requestDetails.requiresConvertClass()) {
				CalendarDataUtils.convertClassPublic(agenda);
			}
			
			model.put("ical", agenda.toString());
		}
	}

	/**
	 * 
	 * @param calendar
	 * @param start
	 * @param end
	 */
	@SuppressWarnings("unchecked")
	protected void expandRecurrenceAndAlterUid(Calendar calendar, Date start, Date end) {
		ComponentList newComponents = new ComponentList();
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName()) ){
				VEvent event = (VEvent) component;
				if(CalendarDataUtils.isEventRecurring(event)) {
					PeriodList recurringPeriods = CalendarDataUtils.calculateRecurrence(event, start, end);
					for(Object o: recurringPeriods) {
						Period period = (Period) o;
						VEvent recurrenceInstance = CalendarDataUtils.constructRecurrenceInstance(event, period);
						CalendarDataUtils.convertToCombinationUid(recurrenceInstance);
						newComponents.add(recurrenceInstance);
					}
				}
			}
		}
		
		if(!newComponents.isEmpty()) {
			calendar.getComponents().addAll(newComponents);
		}
	}
	/**
	 * Request handler.
	 */
	@RequestMapping("/u/**")
	public String getShareUrl(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		// controller
		final ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		// controller
		model.put("requestDetails", requestDetails);
		// controller
		Share share = shareDao.retrieveByKey(requestDetails.getShareKey());
		if(null != share) {
			//controller
			ICalendarAccount account = calendarAccountDao.getCalendarAccountFromUniqueId(share.getOwnerCalendarUniqueId());
			if(null == account) {
				// account not found for share, revoke and 404
				if(LOG.isWarnEnabled()) {
					LOG.warn("valid share exists, however calendarAccountDao returns no results for calendar id " + share.getOwnerCalendarUniqueId() + "; revoking share " + share.getKey());
				}
				shareDao.revokeShare(share);
				response.setStatus(404);
				return "share-not-found";
			}

			//controller
			response.setCharacterEncoding(UTF_8);	
			ShareDisplayFormat displayFormat = requestDetails.getDisplayFormat();

			Calendar agenda = calendarDataDao.getCalendar(account, requestDetails.getStartDate(), requestDetails.getEndDate());
			agenda = eventFilter.filterEvents(agenda, share.getSharePreferences());
			
			prepareModel(share.getSharePreferences(), requestDetails, agenda, account, model);

			// determine the view
			String viewName;
			switch(displayFormat) {
			case HTML:
				viewName = "data/display";
				//response.setContentType(TEXT_HTML);
				break;
			case ICAL:
				viewName = "data/display-ical";
				HTTPHelper.addContentDispositionHeader(response, requestDetails.getShareKey() + ICS);
				break;
			case ICAL_ASTEXT:
				viewName = "data/display-ical-astext";
				break;
			case RSS:
				viewName = "data/display-rss";
				break;
			default:
				viewName = "data/display";
				//response.setContentType(TEXT_HTML);
				break;
			}
			return viewName;
		} else {
			// share not found, return 404
			if(LOG.isDebugEnabled()) {
				LOG.debug("share not found with uniqueid: " + requestDetails.getShareKey());
			}
			response.setStatus(404);
			return "share-not-found";
		}
	}

	/**
	 * 
	 * @param agenda
	 * @param details
	 * @return
	 */
	protected String handleSingleEvent(final Calendar agenda, final ShareRequestDetails requestDetails, final ModelMap model, HttpServletResponse response) {
		expandRecurrenceAndAlterUid(agenda, requestDetails.getStartDate(), requestDetails.getEndDate());
		ComponentList events = agenda.getComponents(VEvent.VEVENT);
		VEvent matchingEvent = null;
		for(Object o : events) {
			VEvent current = (VEvent) o;
			Uid currentUid = current.getUid();
			if(null != currentUid && requestDetails.getEventId().equals(currentUid.getValue())) {
				matchingEvent = current;
			}
		}

		if(null != matchingEvent) {
			if(null != matchingEvent.getDescription()) {
				String descriptionValue = matchingEvent.getDescription().getValue();
				String [] descriptionSections = descriptionValue.split("\n");
				model.put("descriptionSections", descriptionSections);
			}
			model.put("shareId", requestDetails.getShareKey());
			model.put("datePhrase", requestDetails.getDatePhrase());
			model.put("event", matchingEvent);
			return "data/single-event";
		} else {
			response.setStatus(404);
			return "event-not-found";
		}
	}
	/**
	 * 
	 * @param agenda
	 * @param details
	 * @param displayFormat
	 * @return
	 */
	protected String handleFreeBusyShare(final Calendar agenda, final ShareRequestDetails requestDetails, final HttpServletResponse response,
			final ModelMap model) {
		model.put("requestDetails", requestDetails);
		final ShareDisplayFormat displayFormat = requestDetails.getDisplayFormat();

		Calendar freebusy = CalendarDataUtils.convertToFreeBusy(agenda, requestDetails.getStartDate(), requestDetails.getEndDate());
		if(displayFormat.isMarkupLanguage()) {

			VFreeBusy vFreeBusy = (VFreeBusy) freebusy.getComponent(VFreeBusy.VFREEBUSY);
			PeriodList periodList = new PeriodList();
			for(Object o : vFreeBusy.getProperties(FreeBusy.FREEBUSY)) {
				FreeBusy fb = (FreeBusy) o;
				periodList.addAll(fb.getPeriods());
			}
			if(periodList.size() == 0) {
				model.put("noEvents", true);
			} else {
				model.put("busyPeriods", periodList);
			}
			model.put("startDate", requestDetails.getStartDate());
			model.put("endDate", requestDetails.getEndDate());
			model.put("shareKey", requestDetails.getShareKey());
			model.put("datePhrase", requestDetails.getDatePhrase());

		} else if (displayFormat.isIcalendar()) {
			// stuff freebusy data in model
			model.put("ical", freebusy.toString());
		}
		// determine the view
		String viewName;
		switch(displayFormat) {
		case HTML:
			viewName = "fb/display";
			break;
		case ICAL:
			viewName = "fb/display-ical";
			HTTPHelper.addContentDispositionHeader(response, requestDetails.getShareKey() + IFB);
			break;
		case ICAL_ASTEXT:
			viewName = "fb/display-ical-astext";
			break;
		default:
			viewName = "fb/display";
			break;
		}
		return viewName;
	}

}