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


import java.util.List;
import java.util.Set;

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
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.IncludeParticipantsPreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;

/**
 * {@link Controller} for editing {@link SharePreferences}.
 * 
 * @author Nicholas Blair
 */
@Controller
public class EditSharePreferencesController {

	private static final String JSON_VIEW = "jsonView";

	private final Log log = LogFactory.getLog(this.getClass());

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
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/shareDetails", method=RequestMethod.GET)
	public String getShare(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling getShare request for " + activeAccount);
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null) {
			model.addAttribute("share", candidate);
		}
		return JSON_VIEW;
	}
	/**
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/toac", method=RequestMethod.POST)
	public String toAllCalendar(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling freeBusyToAllCalendar request for " + activeAccount);
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
	 * 
	 * @param shareKey
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/tofb", method=RequestMethod.POST)
	public String toFreeBusy(@RequestParam String shareKey, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling freeBusyToAllCalendar request for " + activeAccount);
		}
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate != null && !candidate.isFreeBusyOnly()) {
			Set<ISharePreference> prefs = candidate.getSharePreferences().getPreferences();
			for(ISharePreference pref: prefs) {
				candidate = shareDao.removeSharePreference(candidate, pref);
			}
			candidate = shareDao.addSharePreference(candidate, new FreeBusyPreference());
			model.addAttribute("share", candidate);
		}
		return JSON_VIEW;
	}
	
	/**
	 * 
	 * @param shareKey
	 * @param model
	 * @return
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
	 * 
	 * @param shareKey
	 * @param model
	 * @return
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
	 * 
	 * @param shareKey
	 * @param preferenceType
	 * @param preferenceKey
	 * @param preferenceValue
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/addPreference", method=RequestMethod.POST)
	public String addPreference(@RequestParam String shareKey, @RequestParam String preferenceType, 
			@RequestParam String preferenceKey, @RequestParam String preferenceValue, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling addPreference request for " + activeAccount);
		}
		
		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate == null) {
			model.addAttribute("message", "Share not found");
			model.addAttribute("success", false);
			return JSON_VIEW;
		}
		ISharePreference sharePreference = SharePreferences.construct(preferenceType, preferenceKey, preferenceValue);
		if(sharePreference == null) {
			model.addAttribute("message", "Invalid Share Preference");
			model.addAttribute("success", false);
			return JSON_VIEW;
		}
		for(ISharePreference existing: candidate.getSharePreferences().getPreferencesByType(sharePreference.getType())) {
			if(sharePreference.equals(existing)) {
				model.addAttribute("message", "Duplicate Share Preference already exists");
				model.addAttribute("success", false);
				return JSON_VIEW;
			}
		}

		// passed validation
		Share updated = shareDao.addSharePreference(candidate, sharePreference);
		model.addAttribute("share", updated);
		model.addAttribute("success", true);
		return JSON_VIEW;
	}

	/**
	 * 
	 * @param shareKey
	 * @param preferenceType
	 * @param preferenceKey
	 * @param preferenceValue
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/removePreference", method=RequestMethod.POST)
	public String removePreference(@RequestParam String shareKey, @RequestParam String preferenceType, 
			@RequestParam String preferenceKey, @RequestParam String preferenceValue, ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling removePreference request for " + activeAccount);
		}

		Share candidate = identifyCandidate(shareKey, activeAccount);
		if(candidate == null) {
			model.addAttribute("message", "Share not found");
			model.addAttribute("success", false);
			return JSON_VIEW;
		}
		ISharePreference sharePreference = SharePreferences.construct(preferenceType, preferenceKey, preferenceValue);
		if(sharePreference == null) {
			model.addAttribute("message", "Invalid Share Preference");
			model.addAttribute("success", false);
			return JSON_VIEW;
		}
		// passed validation
		Share updated = shareDao.removeSharePreference(candidate, sharePreference);
		model.addAttribute("share", updated);
		model.addAttribute("success", true);
		return JSON_VIEW;
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
