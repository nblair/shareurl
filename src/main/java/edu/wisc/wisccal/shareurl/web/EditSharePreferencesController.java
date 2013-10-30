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
package edu.wisc.wisccal.shareurl.web;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.impl.caldav.CaldavCalendarDataDao;
import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.WebContentGenerator;

import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
import edu.wisc.wisccal.shareurl.GuessableShareAlreadyExistsException;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.AccessClassificationMatchPreference;
import edu.wisc.wisccal.shareurl.domain.CalendarMatchPreference;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.GuessableSharePreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.IncludeParticipantsPreference;
import edu.wisc.wisccal.shareurl.domain.IncludeSourceCalendarPreference;
import edu.wisc.wisccal.shareurl.domain.PropertyMatchPreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;

/**
 * {@link Controller} for editing {@link SharePreferences}.
 * 
 * @author Nicholas Blair
 */
@Controller
public class EditSharePreferencesController extends WebContentGenerator {

	private static final String ON = "on";

	private static final String JSON_VIEW = "jsonView";

	private final Log log = LogFactory.getLog(this.getClass());

	private final List<String> allowedContentFilterPropertyNames = Collections.unmodifiableList(Arrays.asList(new String[] { Location.LOCATION, Summary.SUMMARY, Description.DESCRIPTION }));
	private final List<String> allowedPrivacyFilterValues = Collections.unmodifiableList(Arrays.asList(new String[] { Clazz.PRIVATE.getValue(), Clazz.CONFIDENTIAL.getValue(), Clazz.PUBLIC.getValue()} ));
	private IShareDao shareDao;
	private AutomaticPublicShareService automaticPublicShareService;
	
	
	private ExchangeCalendarDataDao exchangeCalendarDataDao;
	private CaldavCalendarDataDao caldavCalendarDataDao;
	
	/**
	 * @return the shareDao
	 */
	public IShareDao getShareDao() {
		return shareDao;
	}
	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
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
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/manage.html") 
	public String display(@RequestParam(required=false, value="id") String shareKey, ModelMap model, HttpServletResponse response) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling display request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		if(StringUtils.isBlank(shareKey)) {
			return "input-missing";
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
				
		if(candidate != null) {
			Map<String, String> exchangeListCalendars = exchangeCalendarDataDao.listCalendars(activeAccount);
			Map<String, String> caldavListCalendars = caldavCalendarDataDao.listCalendars(activeAccount);
			Map<String, String> allCalendarsList = new TreeMap<String, String>();
			allCalendarsList.putAll(caldavListCalendars);
			allCalendarsList.putAll(exchangeListCalendars);
			
			model.addAttribute("allCalendarList", allCalendarsList);
			Map<String, String> calendarMap = getCalendarMap(activeAccount);
			if(null != calendarMap){
				String defaultCalendarId = getDefaultCalendarId(calendarMap);
				model.addAttribute("defaultCalendarId",defaultCalendarId);
			}
			model.addAttribute("exchangeCalendarList", exchangeListCalendars);
			model.addAttribute("caldavCalendarList", caldavListCalendars);
			
			log.debug("share object: "+ candidate.toString());
			model.addAttribute("share", candidate);
			model.addAttribute("calendarMap", calendarMap);
			// also grab eligibility
			model.addAttribute("ineligibleStatus", automaticPublicShareService.getEligibilityStatus(activeAccount));
			preventCaching(response);
			return "share-details";
		}
		
		if(log.isDebugEnabled()){
			log.debug("Could not find share");
		}
		return "redirect:/error-404.jsp";
	}
	/**
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/revoke.html", method=RequestMethod.POST) 
	public String revoke(@RequestParam(value="id") String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling revoke request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && candidate.isRevocable()) {	
			shareDao.revokeShare(candidate);
		}
		return "redirect:/my-shares";
	}
	
	
	/**
	 * Get details for the share identified by shareKey, if and only if
	 * the share is owned by the current authenticated user.
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/shareDetails", method=RequestMethod.GET)
	public String getShareDetails(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling getShareDetails request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null) {
			model.addAttribute("share", candidate);
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		return JSON_VIEW;
	}
	/**
	 * Convert the share identified by shareKey to "All Calendar".
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 * @throws GuessableShareAlreadyExistsException 
	 */
	@RequestMapping(value="/rest/toac", method=RequestMethod.POST)
	public String toAllCalendar(@RequestParam String shareKey, ModelMap model) throws GuessableShareAlreadyExistsException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling toAllCalendar request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && candidate.isFreeBusyOnly()) {
			candidate = justInTimeReplace(candidate, activeAccount);
			ISharePreference freeBusyPref = candidate.getSharePreferences().getPreferenceByType(FreeBusyPreference.FREE_BUSY);
			candidate = shareDao.removeSharePreference(candidate, freeBusyPref);
			model.addAttribute("share", candidate);
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		return JSON_VIEW;
	}
	
	/**
	 * Convert the share to "Free Busy Only".
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/tofb", method=RequestMethod.POST)
	public String toFreeBusy(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling toFreeBusy request request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}

		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly()) {
			Set<ISharePreference> prefs = candidate.getSharePreferences().getPreferences();
			for(ISharePreference pref: prefs) {
				if(!GuessableSharePreference.GUESSABLE.equals(pref.getType()) 
						&& !CalendarMatchPreference.CALENDAR_MATCH.equals(pref.getType())) {
					candidate = shareDao.removeSharePreference(candidate, pref);
				}		
			}
			candidate = shareDao.addSharePreference(candidate, new FreeBusyPreference());
			model.addAttribute("share", candidate);
			
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		return JSON_VIEW;
	}
	
	/**
	 * Convert the share to support Calendar Selection
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 * @throws GuessableShareAlreadyExistsException 
	 */
	@RequestMapping(value="/rest/tocs", method=RequestMethod.POST)
	public String toCalSelect(@RequestParam String shareKey, ModelMap model) throws GuessableShareAlreadyExistsException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling toCalendarSelect request request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		//if candidate is not null and calendar isCalendarSelect()
		if(candidate != null && !candidate.isCalendarSelect()){
			//add CalendarMatchPref for default calendar
			candidate = justInTimeReplace(candidate, activeAccount);
			Map<String, String> calendarMap = getCalendarMap(activeAccount);
			if(null != calendarMap){
				String defaultCalendarId = getDefaultCalendarId(calendarMap);
				String defaultCalendarName = calendarMap.get(defaultCalendarId);
				candidate = shareDao.addSharePreference(candidate, new CalendarMatchPreference(defaultCalendarName, defaultCalendarId));
			}else{
				log.warn("I've made a huge mistake");
			}
			
			model.addAttribute("share", candidate);
			
		}else{
			StringBuilder err = new StringBuilder(" Failed to set CalendarSelect. ");
			if(candidate == null) err.append("Candidate not found. ");
			if(candidate.isCalendarSelect()) err.append("Candidate is already calendarSelect. ");
			
			model.addAttribute("error",err.toString());
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		return JSON_VIEW;
	}
	
	/**
	 * Convert the share to display only the Calendar Default (WiscCal -calendar)
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/rest/tocd", method=RequestMethod.POST)
	public String toCalDefault(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling toCalendarDefault request request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		//if candidate is not null and calendar isCalendarSelect()
		if(candidate != null && candidate.isCalendarSelect()){
			//delete all calendarMatch prefs
			Set<ISharePreference> prefs = candidate.getSharePreferences().getCalendarMatchPreferences();
			for(ISharePreference pref: prefs) {
				candidate = shareDao.removeSharePreference(candidate, pref);
			}
			model.addAttribute("share", candidate);
		}else{
			StringBuilder err = new StringBuilder(" Failed to set CalendarDefault. ");
			if(candidate == null) err.append("Candidate not found. ");
			if(candidate.isCalendarSelect()) err.append("Candidate is already calendarDefault. ");
			
			model.addAttribute("error",err.toString());
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		return JSON_VIEW;
	}
	
	
	
	/**
	 * Add the {@link IncludeParticipantsPreference}, if not present.
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/includeP", method=RequestMethod.POST)
	public String includeParticipants(@RequestParam String shareKey, @RequestParam(required=false) String includeParticipants, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling includeParticipants request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate == null || candidate.isFreeBusyOnly()) {
			StringBuilder err = new StringBuilder("Failed to include participants (");
			if(candidate == null) err.append("Candidate is null, ");
			if(candidate.isFreeBusyOnly()) err.append("Candidate is FB only");
			err.append(")");
			return JSON_VIEW;
		}
		
		if(ON.equalsIgnoreCase(includeParticipants) && !candidate.isIncludeParticipants()) {
			candidate = shareDao.addSharePreference(candidate, new IncludeParticipantsPreference(true));
			model.addAttribute("share", candidate);
		} else if (!ON.equalsIgnoreCase(includeParticipants) && candidate.isIncludeParticipants()) {
			candidate = shareDao.removeSharePreference(candidate, new IncludeParticipantsPreference(true));
			model.addAttribute("share", candidate);
		}
		
		return JSON_VIEW;
	}
	
	/**
	 * Add the {@link IncludeSourceCalendarPreference}, if not present.
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/includeSC", method=RequestMethod.POST)
	public String includeSourceCalendar(@RequestParam String shareKey, @RequestParam(required=false) String includeSourceCalendar, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling includeParticipants request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate == null || candidate.isFreeBusyOnly()) {
			StringBuilder err = new StringBuilder("Failed to include source calendar (");
			if(candidate == null) err.append("Candidate is null, ");
			if(candidate.isFreeBusyOnly()) err.append("Candidate is FB only");
			err.append(")");
			return JSON_VIEW;
		}
		
		IncludeSourceCalendarPreference sharePreference = new IncludeSourceCalendarPreference(true);
		if(ON.equalsIgnoreCase(includeSourceCalendar) && !candidate.isIncludeSourceCalendar()) {
			candidate = shareDao.addSharePreference(candidate, sharePreference);
			model.addAttribute("share", candidate);
		} else if (!ON.equalsIgnoreCase(includeSourceCalendar) && candidate.isIncludeSourceCalendar()) {
			candidate = shareDao.removeSharePreference(candidate, sharePreference);
			model.addAttribute("share", candidate);
		}
		
		return JSON_VIEW;
	}
	
	/**
	 * Add a "privacy" filter.
	 * 
	 * @see AccessClassificationMatchPreference
	 * @param shareKey
	 * @param privacyValue
	 * @param model
	 * @return the json view
	 * @throws GuessableShareAlreadyExistsException 
	 */
	@RequestMapping(value="/rest/addPrivacyFilter", method=RequestMethod.POST)
	public String addPrivacyFilter(@RequestParam String shareKey, @RequestParam(required=false) String includePublic, 
			@RequestParam(required=false) String includeConfidential, @RequestParam(required=false) String includePrivate, ModelMap model) throws GuessableShareAlreadyExistsException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("Handling addPrivacyFilter request for shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId());
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		if(candidate != null && !candidate.isFreeBusyOnly()) {
			final boolean includePublicB = checkboxParameterToBoolean(includePublic);
			final boolean includeConfid = checkboxParameterToBoolean(includeConfidential);
			final boolean includePrivateB = checkboxParameterToBoolean(includePrivate);
			if(!includePublicB && !includeConfid && !includePrivateB) {
				//unchecking all 3 is invalid state
				model.addAttribute("error", "You must have at least one Visibility entry checked.");
				return JSON_VIEW;
			}
			Set<ISharePreference> desired = constructDesiredPrivacyPreferences(includePublicB,
					includeConfid, includePrivateB);
			final int desiredSize = desired.size();
			Set<ISharePreference> existing = candidate.getSharePreferences().getPreferencesByType(AccessClassificationMatchPreference.CLASS_ATTRIBUTE);
			final int existingSize = existing.size();
			if(desiredSize == 0 & existingSize != 0) {
				for(ISharePreference classPref: existing) {
					candidate = shareDao.removeSharePreference(candidate, classPref);
				}
			} else if (desired.size() > 0) {
				for(ISharePreference classPref: existing) {
					candidate = shareDao.removeSharePreference(candidate, classPref);
				}
				for(ISharePreference newPref: desired) {
					candidate = shareDao.addSharePreference(candidate, newPref);
				}
			}
			model.addAttribute("share", candidate);
			model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		}
		return JSON_VIEW;
	}
	
	protected Set<ISharePreference> constructDesiredPrivacyPreferences(boolean includePublic, boolean includeConfidential, boolean includePrivate) {
		if(includePublic && includeConfidential && includePrivate) {
			return Collections.emptySet();
		}
		
		Set<ISharePreference> results = new HashSet<ISharePreference>();
		if(includePublic) {
			results.add(SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, Clazz.PUBLIC.getValue()));
		}
		if(includeConfidential) {
			results.add(SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, Clazz.CONFIDENTIAL.getValue()));
		}
		if(includePrivate) {
			results.add(SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, Clazz.PRIVATE.getValue()));
		}
		
		return results;
	}
	
	/**
	 * Add a "calendar" filter
	 * 
	 * @see CalendarMatchPreference
	 * @param shareKey
	 * @param propertyName
	 * @param propertyValue
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/addCalendarFilter", method=RequestMethod.POST)
	public String addCalendarFilter(@RequestParam String shareKey, @RequestParam String calendarId, @RequestParam String calendarType, ModelMap model) {
		
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		Map<String, String> calendarMap = getCalendarMap(activeAccount);
		model.addAttribute("calendarMap", calendarMap);
		if(log.isDebugEnabled()) {
			log.debug("Handling addCalendarFilter request for  shareKey="+shareKey+", account="+activeAccount.getCalendarUniqueId()+ 
					", calendarType="+calendarType+", calendarId="+calendarId);
		}
				
		Share candidate = identifyCandidate(shareKey, activeAccount);
		
		
		//FB candidates may still specify calendars...
		//if(candidate != null && !candidate.isFreeBusyOnly() && validatePropertyFilter(candidate, calendarType, calendarId)) {
		if(candidate != null  && calendarMap.containsKey(calendarId)) {
			
			//String calendarName = getCalendarName(activeAccount, calendarId);
			
			ISharePreference sharePreference = SharePreferences.construct(CalendarMatchPreference.CALENDAR_MATCH, calendarType, calendarId);
			candidate = shareDao.addSharePreference(candidate, sharePreference);
			model.addAttribute("share", candidate);
			model.addAttribute("newCalendarFilterDisplayName", sharePreference.getDisplayName());
			
			
		}else{
			StringBuilder err = new StringBuilder("Failed to add calendar filter");
			model.addAttribute("error",err.toString());
			if(log.isDebugEnabled()){
				err.append("(");
				if(candidate == null) err.append("Candidate is null, ");
				//if(candidate.isFreeBusyOnly()) err.append("Candidate is FB only, ");
				if(!validatePropertyFilter(candidate, calendarType, calendarId)) err.append("Validation failed");	
				err.append(")");
				log.debug(err.toString());
			}
		}
		return JSON_VIEW;
	}
	/**
	 * Remove a "calendar" filter
	 * 
	 * @see CalendarMatchPreference
	 * @param shareKey
	 * @param propertyName
	 * @param propertyValue
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/removeCalendarFilter", method=RequestMethod.POST)
	public String removeCalendarFilter(@RequestParam String shareKey, @RequestParam String calendarName, @RequestParam String calendarId, ModelMap model) {
	
		
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		if(log.isDebugEnabled()) {
			log.debug("handling removeCalendarFilter request for " + activeAccount.getEmailAddress() +",sharekey="+shareKey+", calendarName="+calendarName+",calendarId="+calendarId);
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		StringBuilder err = new StringBuilder("Failed to remove calendar filter");

		
		if(candidate != null) {
			//ISharePreference sharePreference = SharePreferences.construct(CalendarMatchPreference.CALENDAR_MATCH,calendarName, calendarId);
			Set<ISharePreference> calendarMatchPreferences = candidate.getSharePreferences().getCalendarMatchPreferences();
			
			ISharePreference found = null;
			for(ISharePreference p : calendarMatchPreferences){
				if(p.getValue().equals(calendarId)) found = p;
			}
			
			if(found != null) {
				
				if(calendarMatchPreferences.size() < 2) {
					//no calendars defined is invalid state
					model.addAttribute("error", "You must have at least one Calendar selected.");
					return JSON_VIEW;
				}				
				
				candidate = shareDao.removeSharePreference(candidate, found);
				model.addAttribute("share", candidate);
				model.addAttribute("removeCalendarFilter", true);
			}else{
				model.addAttribute("error",err.toString());
				if(log.isDebugEnabled()){
					err.append("Candidate not found.  SharePreference does not exist.");
					log.debug(err.toString());
				}
			}
			
		}else{
			model.addAttribute("error",err.toString());
			if(log.isDebugEnabled()){
				err.append("(");
				if(candidate == null) err.append("Candidate is null, ");
				
				//FB does not matter in this case.
				//if(candidate.isFreeBusyOnly()) err.append("Candidate is FB only, ");
				
				//if(!validatePropertyFilter(candidate, calendarName, calendarId)) err.append("Validation failed");	
				err.append(")");
				log.debug(err.toString());
			}		
		}
		
		return JSON_VIEW;
	}
	
	
	/**
	 * Add a "content" filter
	 * 
	 * @see PropertyMatchPreference
	 * @param shareKey
	 * @param propertyName
	 * @param propertyValue
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/addContentFilter", method=RequestMethod.POST)
	public String addContentFilter(@RequestParam String shareKey, @RequestParam String propertyName, @RequestParam String propertyValue, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		if(log.isDebugEnabled()) {
			log.debug("handling addContentFilter request for " + activeAccount);
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly() && validatePropertyFilter(candidate, propertyName, propertyValue)) {
			ISharePreference sharePreference = SharePreferences.construct(PropertyMatchPreference.PROPERTY_MATCH, propertyName, propertyValue);
			candidate = shareDao.addSharePreference(candidate, sharePreference);
			model.addAttribute("share", candidate);
			model.addAttribute("newContentFilterDisplayName", sharePreference.getDisplayName());
		}
		return JSON_VIEW;
	}
	/**
	 * Add a "content" filter
	 * 
	 * @see PropertyMatchPreference
	 * @param shareKey
	 * @param propertyName
	 * @param propertyValue
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/rest/removeContentFilter", method=RequestMethod.POST)
	public String removeContentFilter(@RequestParam String shareKey, @RequestParam String propertyName, @RequestParam String propertyValue, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		if(log.isDebugEnabled()) {
			log.debug("handling removeContentFilter request for " + activeAccount);
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly() && validatePropertyFilter(candidate, propertyName, propertyValue)) {
			ISharePreference sharePreference = SharePreferences.construct(PropertyMatchPreference.PROPERTY_MATCH, propertyName, propertyValue);
			if(candidate.getSharePreferences().getPreferences().contains(sharePreference)) {
				candidate = shareDao.removeSharePreference(candidate, sharePreference);
			}
			model.addAttribute("share", candidate);
			model.addAttribute("removeContentFilter", true);
		}
		return JSON_VIEW;
	}
	
	/**
	 * Remove all {@link ISharePreference} that return true for {@link ISharePreference#participatesInFiltering()}.
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/rest/resetFilters", method=RequestMethod.POST)
	public String resetContentFilters(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling resetContentFilters request for " + activeAccount);
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly()) {
			boolean removed = false;
			for(ISharePreference pref : candidate.getSharePreferences().getFilterPreferences()) {
				if(pref.participatesInFiltering()) {
					candidate = shareDao.removeSharePreference(candidate, pref);
					removed = true;
				}
			}
			if(removed) {
				model.addAttribute("share", candidate);
			}
		}
		model.addAttribute("calendarMap", getCalendarMap(activeAccount));
		return JSON_VIEW;
	}
	
	/**
	 * 
	 * @param shareKey
	 * @param label
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/rest/set-label",method=RequestMethod.POST) 
	public String setLabel(@RequestParam String shareKey, @RequestParam String label, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling setLabel request for " + activeAccount);
		}
		if(validateLabel(label)) {
			Share candidate = identifyCandidate(shareKey, activeAccount);
			if(candidate != null && candidate.isRevocable() && !candidate.isGuessable()) {
				candidate = shareDao.setLabel(candidate, label);
				model.addAttribute("share", candidate);
			}
		} else {
			model.addAttribute("error", "Label invalid");
		}
		Map<String, String> calendarMap = getCalendarMap(activeAccount);
		model.addAttribute("calendarMap", calendarMap);
		
		return JSON_VIEW;
	}
	/**
	 * 
	 * @param label
	 * @return
	 */
	protected boolean validateLabel(String label) {
		if(StringUtils.isBlank(label)) {
			return true;
		}
		return label.length() < 64;
	}
	/**
	 * 
	 * @param candidate
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	protected boolean validatePropertyFilter(Share candidate, String propertyName, String propertyValue) {
		
		
		if(Clazz.CLASS.equals(propertyName)) {
			return allowedPrivacyFilterValues.contains(propertyValue);
		}else if(allowedContentFilterPropertyNames.contains(propertyName) && StringUtils.isNotBlank(propertyValue)){
			return true;
		}else {
			CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			ICalendarAccount activeAccount = currentUser.getCalendarAccount();
			
			if(propertyName.startsWith(CalendarMatchPreference.EXCHANGE_CALENDAR_IDENTIFIER)){
				Map<String, String> exchangeListCalendars = exchangeCalendarDataDao.listCalendars(activeAccount);
			return exchangeListCalendars.keySet().contains(propertyValue);	
			}
			
			if(propertyName.startsWith(CalendarMatchPreference.ORACLE_CALENDAR_IDENTIFIER)){
				Map<String, String> caldavListCalendars = caldavCalendarDataDao.listCalendars(activeAccount);
				return caldavListCalendars.keySet().contains(propertyValue);
			}
			
		}
		
		return false;
	}
	
	private boolean validateCalendarId(ICalendarAccount account, String calendarId){
		Map<String, String> calendarMap = getCalendarMap(account);
		return calendarMap.containsKey(calendarId);
	}
	
	protected String getCalendarName(ICalendarAccount activeAccount, String calendarId){
		Map<String, String> calList = caldavCalendarDataDao.listCalendars(activeAccount);
		calList.putAll(exchangeCalendarDataDao.listCalendars(activeAccount));
		if(calList.containsKey(calendarId)){
			return calList.get(calendarId);
		}
		return null;
	}
	
	/**
	 * 
	 * @param calId
	 * @return true only if calId is valid
	 */
	protected boolean validateExchangeCalendarId(String calId){
		return true;
	}
	
	/**
	 * 
	 * @param calId
	 * @return true if calId is a valid calendarPath
	 */
	protected boolean validateWiscCalCalendarId(String calId){
		return true;
	}
		
	/**
	 * Locate the {@link Share} from the key in the request.
	 * Returns null if the specified {@link ICalendarAccount} does not have a matching share.
	 * 
	 * @param shareKey
	 * @param activeAccount
	 * @return the matching share, or null
	 */
	protected Share identifyCandidate(String shareKey, ICalendarAccount activeAccount) {
		if(activeAccount == null || StringUtils.isBlank(shareKey)) {
			return null;
		}
		List<Share> shares = shareDao.retrieveByOwner(activeAccount);
		for(Share s: shares) {
			if(s.getKey().equals(shareKey)) {
				return s;
			}
		}
		//sharekey not found, look for upn 
		for(Share s: shares) {
			if(s.getKey().equals(activeAccount.getUpn())) {
				return s;
			}
		}
		
		Share publicShare = automaticPublicShareService.getAutomaticPublicShare(activeAccount.getEmailAddress());
		if(publicShare != null && publicShare.getKey().equals(shareKey)) {
			return publicShare;
		}
		
		return null;
	}

	/**
	 * If the share in question {@link Share#isGuessable()} and has {@link Share#isRevocable()} equal to false, 
	 * call the shareDao and generate the guessable and return it.
	 * 
	 * @param share
	 * @param account 
	 * @return
	 * @throws GuessableShareAlreadyExistsException 
	 */
	protected Share justInTimeReplace(Share share, ICalendarAccount account) throws GuessableShareAlreadyExistsException {
		if(share.isGuessable() && !share.isRevocable()) {
			if(log.isDebugEnabled()) {
				log.debug("calling just-in-time replace for " + share);
			}
			return shareDao.generateGuessableShare(account);
		}
		
		return share;
	}
	
	public Map<String, String> getCalendarMap(ICalendarAccount activeAccount ){
		Map<String, String> exchangeListCalendars = exchangeCalendarDataDao.listCalendars(activeAccount);
		Map<String, String> caldavListCalendars = caldavCalendarDataDao.listCalendars(activeAccount);
		Map<String, String> allCalendarsList = new TreeMap<String, String>();
		allCalendarsList.putAll(caldavListCalendars);
		allCalendarsList.putAll(exchangeListCalendars);
		return allCalendarsList;
	}
	
	public String getDefaultCalendarId(Map<String,String> calendarMap){
		if(calendarMap.containsKey(ICalendarDataDao.DEFAULT_CALENDAR_PATH)) return  ICalendarDataDao.DEFAULT_CALENDAR_PATH;
		if(calendarMap.containsValue(ICalendarDataDao.DEFAULT_EXCHANGE_CALENDAR)){
			for(String key : calendarMap.keySet()){
				String name = calendarMap.get(key);
				if(name.equals(ICalendarDataDao.DEFAULT_EXCHANGE_CALENDAR)) return key;
			}
		}
		log.warn("NO DEFAULT CALENDAR FOUND!!!");
		return null;
		
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	boolean checkboxParameterToBoolean(String value) {
		return ON.equalsIgnoreCase(value);
	}
	public ExchangeCalendarDataDao getExchangeCalendarDataDao() {
		return exchangeCalendarDataDao;
	}
	
	@Autowired
	public void setExchangeCalendarDataDao(ExchangeCalendarDataDao exchangeCalendarDataDao) {
		this.exchangeCalendarDataDao = exchangeCalendarDataDao;
	}
	public CaldavCalendarDataDao getCaldavCalendarDataDao() {
		return caldavCalendarDataDao;
	}
	
	@Autowired
	public void setCaldavCalendarDataDao(CaldavCalendarDataDao caldavCalendarDataDao) {
		this.caldavCalendarDataDao = caldavCalendarDataDao;
	}
}
