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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.FreeBusy;

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
import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;

/**
 * <p>FreeBusy shares support an additional URL syntax defined as "Free Busy Read URL" (Calconnect Document CD0903, Version 1.0).
 * In addition to the "share" prefix above, FreeBusy shares can be accessed through this controller at (default configuration):
 * <pre>
 * [web application context root]/freebusy/[SHAREID]
 * </pre>
 * The data for that share will be converted into a {@link VFreeBusy} instance with {@link FreeBusy} properties
 * for each meeting within the date range. The FreeBusy Read URL defines the following standard URL query parameters (all supported
 * by this controller):
 * <ul>
 * <li>start - RFC3349 Date/Time format (e.g. 2009-12-31T14:30:00-06:00)</li>
 * <li>end - RFC3349 Date/Time format (e.g. 2009-12-31T14:30:00-06:00)</li>
 * <li>period - RFC2445 Duration (e.g. P42D)</li>
 * <li>format - supports "text/calendar" (default) and "text/html"</li>
 * </ul>
 * </p>
 * 
 * None of the parameters are required. Requesting the default url will return data starting at the time of the request up to 42 days later in
 * "text/calendar" format.
 * If start is defined, only one of end or period can be defined. If start, end, and period are all defined, the controller will
 * return a 400 error as defined in the specification.
 * Invalid values for format are ignored (text/calendar will be returned).
 * 
 * "token" and "user" parameters also defined in the Free Busy Read URL specification are ignored; identity comes from
 * the share key in the URL.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: FreeBusyReadURLController.java 1655 2010-02-04 18:37:00Z npblair $
 */
@Controller
public class FreeBusyReadURLController  {

	public static final String AS_TEXT = "asText";

	private static final String IFB = ".ifb";
	
	private IShareDao shareDao;
	private ICalendarDataDao calendarDataDao;
	private ICalendarAccountDao calendarAccountDao;
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
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/freebusy/**")
	public String handleRequest(final ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			FreeBusyReadRequestDetails requestDetails = new FreeBusyReadRequestDetails(request);

			Share share = shareDao.retrieveByKey(requestDetails.getShareKey());
			if(null != share && share.isFreeBusyOnly()) {
				ICalendarAccount account = calendarAccountDao.getCalendarAccountFromUniqueId(share.getOwnerCalendarUniqueId());
				Calendar calendar = calendarDataDao.getCalendar(account, requestDetails.getStartDate(), requestDetails.getEndDate());
				ShareHelper.filterAgendaForDateRange(calendar, requestDetails);
				Calendar freeBusy = CalendarDataUtils.convertToFreeBusy(calendar, requestDetails.getStartDate(), requestDetails.getEndDate());

				model.put("startDate", requestDetails.getStartDate());
				model.put("endDate", requestDetails.getEndDate());
				model.put("shareKey", requestDetails.getShareKey());
				if(requestDetails.isICalendarOutput()) {
					model.put("ical", freeBusy.toString());
					if(null != request.getParameter(AS_TEXT)) {
						return "fb/display-ical-astext";
					} else {
						HTTPHelper.addContentDispositionHeader(response, share.getKey() + IFB);
						return "fb/display-ical";
					}	
				} else {
					VFreeBusy vFreeBusy = (VFreeBusy) freeBusy.getComponent(VFreeBusy.VFREEBUSY);
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
					return "fb/display";
				}
			} else {
				response.setStatus(404);
				return"share-not-found";
			}
		} catch (FreeBusyParameterFormatException e) {
			// spec officially designates 400 as the error code for parameter format problems
			response.setStatus(400);
			model.put("message", e.getMessage());
			return "fb/incorrect-parameters";
		}
	}

}
