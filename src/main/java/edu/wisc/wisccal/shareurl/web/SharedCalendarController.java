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
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.util.UrlPathHelper;

import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.domain.simple.SimpleCalendars;
import edu.wisc.wisccal.shareurl.domain.simple.SimpleCalendarsImpl;
import edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor;
import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;
import edu.wisc.wisccal.shareurl.ical.EventParticipation;
import edu.wisc.wisccal.shareurl.ical.IEventFilter;
import edu.wisc.wisccal.shareurl.ical.VEventComparator;
import edu.wisc.wisccal.shareurl.support.ProblematicRecurringEventSharePreference;

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
 * <p>If you prefer to receive the data in JavaScript Object Notation format (JSON), simply add the request parameter "json":</p>
 * <pre>
 * [web application context root]/u/[SHAREID]?json
 * [web application context root]/u/[SHAREID]/dr([days backward, days forward])?json
 * </pre>
 * <p>
 * This will return the data (as is from the CalendarDao) converted to a simple JSON model with a MIME type of "application/json."
 * </p>
 * 
 * @see ShareRequestDetails
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SharedCalendarController.java 3543 2011-12-13 22:33:19Z npblair $
 */
@Controller
public class SharedCalendarController {

	private static final String JSON_VIEW = "jsonView";
	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final String UTF_8 = "UTF-8";

	private Log LOG = LogFactory.getLog(this.getClass());

	private static final String ICS = ".ics";
	private static final String VFB = ".vfb";

	private IShareDao shareDao;
	private ICalendarDataDao calendarDataDao;
	private ICalendarAccountDao calendarAccountDao;
	private IEventFilter eventFilter;
	private CalendarDataProcessor calendarDataProcessor;
	private SimpleCalendars simpleCalendars = new SimpleCalendarsImpl();
	private AutomaticPublicShareService automaticPublicShareService;

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
	 * @param calendarDataProcessor the calendarDataProcessor to set
	 */
	@Autowired
	public void setCalendarDataProcessor(CalendarDataProcessor calendarDataProcessor) {
		this.calendarDataProcessor = calendarDataProcessor;
	}
	/**
	 * @return the simpleCalendars
	 */
	public SimpleCalendars getSimpleCalendars() {
		return simpleCalendars;
	}
	/**
	 * @param simpleCalendars the simpleCalendars to set
	 */
	public void setSimpleCalendars(SimpleCalendars simpleCalendars) {
		this.simpleCalendars = simpleCalendars;
	}
	/**
	 * @return the automaticPublicShareService
	 */
	public AutomaticPublicShareService getAutomaticPublicShareService() {
		return automaticPublicShareService;
	}
	/**
	 * @param automaticPublicShareService the automaticPublicShareService to set
	 */
	@Autowired
	public void setAutomaticPublicShareService(
			AutomaticPublicShareService automaticPublicShareService) {
		this.automaticPublicShareService = automaticPublicShareService;
	}
	/**
	 * @return the calendarDataProcessor
	 */
	public CalendarDataProcessor getCalendarDataProcessor() {
		return calendarDataProcessor;
	}
	/**
	 * @return the shareDao
	 */
	public IShareDao getShareDao() {
		return shareDao;
	}
	/**
	 * @return the calendarDataDao
	 */
	public ICalendarDataDao getCalendarDataDao() {
		return calendarDataDao;
	}
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	/**
	 * @return the eventFilter
	 */
	public IEventFilter getEventFilter() {
		return eventFilter;
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
	protected boolean prepareModel(SharePreferences sharePreferences, ShareRequestDetails requestDetails, 
			Calendar agenda, ICalendarAccount account, ModelMap model) {
		final ShareDisplayFormat display = requestDetails.getDisplayFormat();

		if(requestDetails.isOrganizerOnly()) {
			calendarDataProcessor.organizerOnly(agenda, account);
		} else if (requestDetails.isAttendeeOnly()) {
			calendarDataProcessor.attendeeOnly(agenda, account);
		} else if (requestDetails.isPersonalOnly()) {
			calendarDataProcessor.personalOnly(agenda, account);
		}
		// are we targeting a markup display?
		if(display.isMarkupLanguage()) {
			// - expandrecurrence first priority, want VEVENTs for each recurrence instance
			calendarDataProcessor.expandRecurrence(agenda, requestDetails.getStartDate(), requestDetails.getEndDate(), true);

			// - filter VEvents to those only with DTSTART within requestDetails start/end
			calendarDataProcessor.filterAgendaForDateRange(agenda, requestDetails);

			if(null != requestDetails.getEventId()) {
				VEvent matchingEvent = findMatchingEvent(agenda, requestDetails);
				if(matchingEvent != null) {
					if(ShareDisplayFormat.JSON.equals(display)) {
						model.put("calendar", simpleCalendars.simplify(CalendarDataUtils.wrapEvent(matchingEvent), sharePreferences.isIncludeParticipants()));
					} else {
						EventParticipation participation = calendarDataProcessor.getEventParticipation(matchingEvent, account);
						// wrap the event with the participation bundled
						matchingEvent = new VEventWithAccountEventParticipation(matchingEvent.getProperties(), matchingEvent.getAlarms(), participation);
						if(null != matchingEvent.getDescription()) {
							String descriptionValue = matchingEvent.getDescription().getValue();
							String [] descriptionSections = descriptionValue.split("\n");
							model.put("descriptionSections", descriptionSections);
						}
						model.put("event", matchingEvent);
						model.put("includeParticipants", sharePreferences.isIncludeParticipants());
					}
				} else {
					// signal 404
					return false;
				}
				
			} else {
				ComponentList components = agenda.getComponents(VEvent.VEVENT);
				List<VEvent> allEvents = new ArrayList<VEvent>();
				for(Iterator<?> i = components.iterator(); i.hasNext(); ) {
					VEvent event = (VEvent) i.next();
					EventParticipation participation = calendarDataProcessor.getEventParticipation(event, account);
					allEvents.add(new VEventWithAccountEventParticipation(event.getProperties(), event.getAlarms(), participation));
				}
				Collections.sort(allEvents, new VEventComparator());
				
				
				if(ShareDisplayFormat.JSON.equals(display)) {
					model.put("calendar", simpleCalendars.simplify(agenda, sharePreferences.isIncludeParticipants()));
				} else {
					model.put("empty", components.size() == 0);
					model.put("allEvents", allEvents);
					model.put("startDate", requestDetails.getStartDate());
					model.put("endDate", requestDetails.getEndDate());
					model.put("shareId", requestDetails.getShareKey());
					model.put("datePhrase", requestDetails.getDatePhrase());	
				}
			}
		} else {
			if(null != requestDetails.getEventId()) {
				calendarDataProcessor.expandRecurrence(agenda, requestDetails.getStartDate(), requestDetails.getEndDate(), sharePreferences.isIncludeParticipants());
				// targeting a specific event
				// set agenda to point to a single event
				VEvent matchingEvent = findMatchingEvent(agenda, requestDetails);
				if(matchingEvent == null) {
					return false;
				} else {
					agenda = CalendarDataUtils.wrapEvent(matchingEvent);
				}
			}
			
			// we are targeting iCalendar format
			// - remove participants if necessary
			if(!sharePreferences.isIncludeParticipants()) {
				calendarDataProcessor.removeParticipants(agenda, account);
			}
			// - adjust recurrence if necessary
			if(!requestDetails.isKeepRecurrence()) {
				calendarDataProcessor.noRecurrence(agenda, requestDetails.getStartDate(), requestDetails.getEndDate(), sharePreferences.isIncludeParticipants());
				calendarDataProcessor.filterAgendaForDateRange(agenda, requestDetails);
			} else if(requestDetails.requiresBreakRecurrence()) {
				calendarDataProcessor.breakRecurrence(agenda);
			}

			// - convert class if necessary
			if(requestDetails.requiresConvertClass()) {
				calendarDataProcessor.convertClassPublic(agenda);
			}

			model.put("ical", agenda.toString());
		}
		return true;
	}
	/**
	 * 
	 * @param calendar
	 * @param requestDetails
	 * @return
	 */
	protected VEvent findMatchingEvent(Calendar calendar, ShareRequestDetails requestDetails) {
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent current = (VEvent) component;
				if(eventMatchesRequestedEventId(current, requestDetails)) {
					return current;
				}
			}
		}
		return null;
	}
	
	/**
	 * Main Request handler for ShareURL requests.
	 */
	@RequestMapping("/u/**")
	public String getShareUrl(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		String requestPath = new UrlPathHelper().getPathWithinServletMapping(request);
		boolean coerceIcal = false;
		if(requestPath.endsWith(ICS)) {
			request = new IcsSuffixRemover(request);
			coerceIcal = true;
		} 
		final ShareRequestDetails requestDetails = new ShareRequestDetails(request);
		if(requestDetails.getEventId() == null) {
			// store non single event requestDetails in the session to track the "last" so we can render a "return" link
			request.getSession().setAttribute("lastNonSingleEventRequestDetails", requestDetails);
		}
		model.put("requestDetails", requestDetails);

		Share share = shareDao.retrieveByKey(requestDetails.getShareKey());
		if(null == share && requestDetails.isPublicUrl()) {
			share = automaticPublicShareService.getAutomaticPublicShare(requestDetails.getShareKey());
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug(requestDetails + " shareDao#retrieveByKey returns " + share);
		}
		if(null == share) {
			// share not found, return 404
			if(LOG.isDebugEnabled()) {
				LOG.debug("share not found with key: " + requestDetails.getShareKey());
			}
			response.setStatus(404);
			return "share-not-found";
		} else {
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
			response.setCharacterEncoding(UTF_8);	
			if(coerceIcal && !ShareDisplayFormat.ICAL_ASTEXT.equals(requestDetails.getDisplayFormat())) {
				requestDetails.setDisplayFormat(ShareDisplayFormat.ICAL);
			}
			if(ShareDisplayFormat.MOBILECONFIG.equals(requestDetails.getDisplayFormat())) {
				// don't need data, short circuit
				model.put("share", share);
				String filename = buildMobileconfigFilename(requestDetails);
				HTTPHelper.addContentDispositionHeader(response, filename);
				return "data/display-mobileconfig";
			}

			Calendar agenda = obtainAgenda(account, requestDetails, share.getSharePreferences());
			if(share.getSharePreferences().isFreeBusyOnly()) {
				// this share is free busy only
				return handleFreeBusyShare(agenda, requestDetails, response, model, account);
			}

			boolean success = prepareModel(share.getSharePreferences(), requestDetails, agenda, account, model);
			if(LOG.isDebugEnabled()) {
				List<String> eventUids = eventDebugIds(agenda);
				LOG.debug("post prepareModel " + requestDetails + "; " + account + " has " + eventUids.size() + " VEVENTs; " + eventUids.toString());
			}
			if(LOG.isTraceEnabled()) {
				LOG.trace("post prepareModel " + requestDetails + "; " + account + " has raw agenda " + agenda);
			}
			if(!success) {
				response.setStatus(404);
				return "event-not-found";
			}
			// determine the view
			String viewName = pickEventDetailViewName(requestDetails, response);
			if(JSON_VIEW.equals(viewName)) {
				// drop requestDetails and descriptionSections from JSON model
				model.remove("requestDetails");
				model.remove("descriptionSections");
			}
			return viewName;
		} 
	}
	/**
	 * Retrieve the agenda, and perform base required filtering.
	 * 
	 * @see IEventFilter#filterEvents(Calendar, SharePreferences)
	 * @param account
	 * @param requestDetails
	 * @param share
	 * @return the {@link Calendar}, post filtering
	 */
	protected Calendar obtainAgenda(ICalendarAccount account, ShareRequestDetails requestDetails, SharePreferences preferences) {
		Calendar agenda = calendarDataDao.getCalendar(account, requestDetails.getStartDate(), requestDetails.getEndDate());
		if(LOG.isDebugEnabled()) {
			List<String> eventUids = eventDebugIds(agenda);
			LOG.debug("begin processing " + requestDetails + "; " + account + " has " + eventUids.size() + " VEVENTs; " + eventUids.toString());
		}
		if(LOG.isTraceEnabled()) {
			LOG.trace("begin processing " + requestDetails + "; " + account + " has raw agenda " + agenda);
		}
		
		if(requestDetails.requiresProblemRecurringPreference()) {
			ProblematicRecurringEventSharePreference pref = new ProblematicRecurringEventSharePreference();
			preferences.addPreference(pref);
			LOG.info(ShareRequestDetails.UW_SUPPORT_RDATE + " parameter detected, added " + pref + " to " + requestDetails);
		}
		agenda = eventFilter.filterEvents(agenda, preferences);

		if(LOG.isDebugEnabled()) {
			List<String> eventUids = eventDebugIds(agenda);
			LOG.debug("post filterEvents for " + requestDetails + "; " + account + " has " + eventUids.size() + " VEVENTs; " + eventUids.toString());
		}
		if(LOG.isTraceEnabled()) {
			LOG.trace("post filterEvents for " + requestDetails + "; " + account + " has raw agenda " + agenda);
		}
		return agenda;
	}
	
	
	List<String> eventDebugIds(Calendar calendar) {
		if(calendar == null) {
			return Collections.emptyList();
		}
		List<String> eventUids = new ArrayList<String>();
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext();) {
			Component c = (Component) i.next();
			if(VEvent.VEVENT.equals(c.getName())) {
				eventUids.add(calendarDataProcessor.getDebugId(c));
			}
		}
		return eventUids;
	}
	boolean eventMatchesRequestedEventId(VEvent event, ShareRequestDetails requestDetails) {
		Uid currentUid = event.getUid();
		if(null != currentUid && requestDetails.getEventId().equals(currentUid.getValue())) {
			// match UID, now safely match recurrenceId
			RecurrenceId recurId = event.getRecurrenceId();
			if(recurId == null && StringUtils.isBlank(requestDetails.getRecurrenceId())) {
				return true;
			} else if (recurId != null && recurId.getValue().equals(requestDetails.getRecurrenceId())) {
				return true;
			}

		}

		return false;
	}

	/**
	 * 
	 * @param requestDetails
	 * @param response
	 * @return
	 */
	protected String pickEventDetailViewName(ShareRequestDetails requestDetails, HttpServletResponse response) {
		String viewName;
		switch(requestDetails.getDisplayFormat()) {
		case HTML:
			if(StringUtils.isNotBlank(requestDetails.getEventId())) {
				viewName = "data/single-event";
			} else {
				viewName = "data/display";
			}
			break;
		case ICAL:
			viewName = "data/display-ical";
			String filename = buildIcsFilename(requestDetails);
			HTTPHelper.addContentDispositionHeader(response, filename);
			break;
		case ICAL_ASTEXT:
			viewName = "data/display-ical-astext";
			break;
		case RSS:
			viewName = "data/display-rss";
			break;
		case JSON:
			response.setContentType(JSON_CONTENT_TYPE);
			viewName = JSON_VIEW;
			break;
		default:
			if(StringUtils.isNotBlank(requestDetails.getEventId())) {
				viewName = "data/single-event";
			} else {
				viewName = "data/display";
			}
			break;
		}
		return viewName;
	}
	
	String buildIcsFilename(ShareRequestDetails requestDetails) {
		StringBuilder filename = new StringBuilder();
		filename.append(requestDetails.getShareKey());
		if(StringUtils.isNotBlank(requestDetails.getEventId())) {
			filename.append("_");
			filename.append(requestDetails.getEventId());
		}
		if(StringUtils.isNotBlank(requestDetails.getRecurrenceId())) {
			filename.append("_");
			filename.append(requestDetails.getRecurrenceId());
		}
		filename.append(ICS);
		return filename.toString();
	}
	String buildMobileconfigFilename(ShareRequestDetails requestDetails) {
		StringBuilder filename = new StringBuilder();
		filename.append(requestDetails.getShareKey());
		filename.append(".mobileconfig");
		return filename.toString();
	}
	/**
	 * 
	 * @param agenda
	 * @param details
	 * @param displayFormat
	 * @return
	 */
	protected String handleFreeBusyShare(final Calendar agenda, final ShareRequestDetails requestDetails, final HttpServletResponse response,
			final ModelMap model, final ICalendarAccount calendarAccount) {
		final ShareDisplayFormat displayFormat = requestDetails.getDisplayFormat();

		calendarDataProcessor.noRecurrence(agenda, requestDetails.getStartDate(), requestDetails.getEndDate(), true);
		calendarDataProcessor.filterAgendaForDateRange(agenda, requestDetails);
		
		if(displayFormat.isMarkupLanguage()) {
			if(ShareDisplayFormat.JSON.equals(displayFormat)) {
				calendarDataProcessor.stripEventDetails(agenda, calendarAccount);
				model.put("calendar", simpleCalendars.simplify(agenda, false));
			} else {
				Calendar freebusy = calendarDataProcessor.convertToFreeBusy(agenda, requestDetails.getStartDate(), requestDetails.getEndDate(), calendarAccount);
				VFreeBusy vFreeBusy = (VFreeBusy) freebusy.getComponent(VFreeBusy.VFREEBUSY);
				PeriodList periodList = new PeriodList();
				if(vFreeBusy != null) {
					for(Object o : vFreeBusy.getProperties(FreeBusy.FREEBUSY)) {
						FreeBusy fb = (FreeBusy) o;
						periodList.addAll(fb.getPeriods());
					}
				}
				if(periodList.size() == 0) {
					model.put("noEvents", true);
				} else {
					model.put("busyPeriods", periodList);
				}
				model.put("startDate", requestDetails.getStartDate());
				model.put("endDate", requestDetails.getEndDate());
				model.put("shareId", requestDetails.getShareKey());
				model.put("datePhrase", requestDetails.getDatePhrase());
			}
		} else {	
			if(ShareDisplayFormat.VFB_LEGACY.equals(displayFormat)) {
				Calendar freebusy = calendarDataProcessor.convertToFreeBusy(agenda, requestDetails.getStartDate(), requestDetails.getEndDate(), calendarAccount);
				model.put("ical", freebusy.toString());
			} else {
				// want iCalendar output with VEVENTs, but no details
				calendarDataProcessor.stripEventDetails(agenda, calendarAccount);
				// - convert class if necessary
				if(requestDetails.requiresConvertClass()) {
					calendarDataProcessor.convertClassPublic(agenda);
				}
				
				model.put("ical", agenda.toString());
			}
		} 
		// determine the view
		String viewName;
		switch(displayFormat) {
		case HTML:
			viewName = "fb/display";
			break;
		case VFB_LEGACY:
			viewName = "fb/display-ical";
			HTTPHelper.addContentDispositionHeader(response, requestDetails.getShareKey() + VFB);
			break;
		case ICAL:
			viewName = "data/display-ical";
			HTTPHelper.addContentDispositionHeader(response, requestDetails.getShareKey() + ICS);
			break;
		case ICAL_ASTEXT:
			viewName = "data/display-ical-astext";
			break;
		case JSON:
			viewName = JSON_VIEW;
			response.setContentType(JSON_CONTENT_TYPE);
			model.remove("requestDetails");
			break;
		default:
			viewName = "fb/display";
			break;
		}
		return viewName;
	}

	/**
	 * {@link HttpServletRequestWrapper} that overrides {@link #getRequestURI()} to
	 * strip ".ics" from the end, if present.
	 * 
	 * @author Nicholas Blair
	 */
	static final class IcsSuffixRemover extends HttpServletRequestWrapper {
		public IcsSuffixRemover(HttpServletRequest request) {
			super(request);
		}
		@Override
		public String getRequestURI() {
			return StringUtils.removeEnd(super.getRequestURI(), ICS);
		}
	}
}