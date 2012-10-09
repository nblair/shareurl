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

package edu.wisc.wisccal.shareurl.web;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.AccessClassificationMatchPreference;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.GuessableSharePreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.IncludeParticipantsPreference;
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
@RequestMapping("/rest")
public class EditSharePreferencesController {

	private static final String JSON_VIEW = "jsonView";

	private final Log log = LogFactory.getLog(this.getClass());

	private final List<String> allowedContentFilterPropertyNames = Collections.unmodifiableList(Arrays.asList(new String[] { Location.LOCATION, Summary.SUMMARY, Description.DESCRIPTION }));
	private final List<String> allowedPrivacyFilterValues = Collections.unmodifiableList(Arrays.asList(new String[] { Clazz.PRIVATE.getValue(), Clazz.CONFIDENTIAL.getValue(), Clazz.PUBLIC.getValue()} ));
	private IShareDao shareDao;
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
	 * Get details for the share identified by shareKey, if and only if
	 * the share is owned by the current authenticated user.
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/shareDetails", method=RequestMethod.GET)
	public String getShareDetails(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling shareDetails request for " + activeAccount);
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null) {
			model.addAttribute("share", candidate);
		}
		return JSON_VIEW;
	}
	/**
	 * Convert the share identified by shareKey to "All Calendar".
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/toac", method=RequestMethod.POST)
	public String toAllCalendar(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling toAllCalendar request for " + activeAccount);
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && candidate.isFreeBusyOnly()) {
			ISharePreference freeBusyPref = candidate.getSharePreferences().getPreferenceByType(FreeBusyPreference.FREE_BUSY);
			candidate = shareDao.removeSharePreference(candidate, freeBusyPref);
			model.addAttribute("share", candidate);
		}
		return JSON_VIEW;
	}
	
	/**
	 * Convert the share to "Free Busy Only".
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/tofb", method=RequestMethod.POST)
	public String toFreeBusy(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling toFreeBusy request for " + activeAccount);
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly()) {
			Set<ISharePreference> prefs = candidate.getSharePreferences().getPreferences();
			for(ISharePreference pref: prefs) {
				if(!GuessableSharePreference.GUESSABLE.equals(pref.getType())) {
					candidate = shareDao.removeSharePreference(candidate, pref);
				}		
			}
			candidate = shareDao.addSharePreference(candidate, new FreeBusyPreference());
			model.addAttribute("share", candidate);
		}
		return JSON_VIEW;
	}
	
	/**
	 * Add the {@link IncludeParticipantsPreference}, if not present.
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/includeP", method=RequestMethod.POST)
	public String includeParticipants(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling includeParticipants request for " + activeAccount);
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly() && !candidate.isIncludeParticipants()) {
			candidate = shareDao.addSharePreference(candidate, new IncludeParticipantsPreference(true));
			model.addAttribute("share", candidate);
		}
		return JSON_VIEW;
	}
	
	/**
	 * Remove the {@link IncludeParticipantsPreference}, if set.
	 * 
	 * @param shareKey
	 * @param model
	 * @return the json view
	 */
	@RequestMapping(value="/excludeP", method=RequestMethod.POST)
	public String excludeParticipants(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling excludeParticipants request for " + activeAccount);
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly() && candidate.isIncludeParticipants()) {
			ISharePreference pref = candidate.getSharePreferences().getPreferenceByType(IncludeParticipantsPreference.INCLUDE_PARTICIPANTS);
			candidate = shareDao.removeSharePreference(candidate, pref);
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
	 */
	@RequestMapping(value="/addPrivacyFilter", method=RequestMethod.POST)
	public String addPrivacyFilter(@RequestParam String shareKey, @RequestParam String privacyValue, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling addPrivacyFilter request for " + activeAccount);
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly() && validatePropertyFilter(candidate, Clazz.CLASS, privacyValue)) {
			ISharePreference sharePreference = SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, privacyValue);
			if(newPrivacyFilterWouldCompleteTheSet(candidate, sharePreference)) {
				// customer has requested this share include PUBLIC, CONFIDENTIAL, and PRIVATE
				// do them a favor and remove those preferences
				Set<ISharePreference> classPrefs = candidate.getSharePreferences().getPreferencesByType(AccessClassificationMatchPreference.CLASS_ATTRIBUTE);
				for(ISharePreference classPref: classPrefs) {
					shareDao.removeSharePreference(candidate, classPref);
				}
			} else {
				candidate = shareDao.addSharePreference(candidate, sharePreference);
			}
			
			model.addAttribute("share", candidate);
		}
		return JSON_VIEW;
	}
	
	/**
	 * 
	 * @param share
	 * @param preference
	 * @return true if adding the {@link ISharePreference} would result in the share having all 3 values for CLASS property.
	 */
	protected boolean newPrivacyFilterWouldCompleteTheSet(Share share, ISharePreference preference) {
		Set<ISharePreference> classPrefs = share.getSharePreferences().getPreferencesByType(AccessClassificationMatchPreference.CLASS_ATTRIBUTE);
		if(classPrefs.size() != 2) {
			return false;
		}
		
		Set<String> currentValues = new HashSet<String>();
		for(ISharePreference classPref : classPrefs) {
			currentValues.add(classPref.getValue());
		}
		
		currentValues.add(preference.getValue());
		
		if(currentValues.equals(new HashSet<String>(allowedPrivacyFilterValues))) {
			return true;
		}
		
		return false;
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
	@RequestMapping(value="/addContentFilter", method=RequestMethod.POST)
	public String addContentFilter(@RequestParam String shareKey, @RequestParam String propertyName, @RequestParam String propertyValue, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling addContentFilter request for " + activeAccount);
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly() && validatePropertyFilter(candidate, propertyName, propertyValue)) {
			ISharePreference sharePreference = SharePreferences.construct(PropertyMatchPreference.PROPERTY_MATCH, propertyName, propertyValue);
			candidate = shareDao.addSharePreference(candidate, sharePreference);
			model.addAttribute("share", candidate);
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
	@RequestMapping(value="/resetFilters", method=RequestMethod.POST)
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
		return JSON_VIEW;
	}
	
	/**
	 * 
	 * @param shareKey
	 * @param label
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/set-label",method=RequestMethod.POST) 
	public String setLabel(@RequestParam String shareKey, @RequestParam String label, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling setLabel request for " + activeAccount);
		}
		if(validateLabel(label)) {
			Share candidate = identifyCandidate(shareKey, activeAccount);
			if(candidate != null && !candidate.isRevocable() && !candidate.isGuessable()) {
				candidate = shareDao.setLabel(candidate, label);
				model.addAttribute("share", candidate);
			}
		} else {
			model.addAttribute("error", "Label invalid");
		}
		
		return JSON_VIEW;
	}
	/**
	 * 
	 * @param label
	 * @return
	 */
	protected boolean validateLabel(String label) {
		return StringUtils.isNotBlank(label) && label.length() < 96;
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
		}
		
		return allowedContentFilterPropertyNames.contains(propertyName);
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
		
		return null;
	}
	
}
