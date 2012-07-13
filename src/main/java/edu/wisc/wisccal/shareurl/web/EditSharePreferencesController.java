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
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
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
			return "jsonView";
		}
		ISharePreference sharePreference = SharePreferences.construct(preferenceType, preferenceKey, preferenceValue);
		if(sharePreference == null) {
			model.addAttribute("message", "Invalid Share Preference");
			model.addAttribute("success", false);
			return "jsonView";
		}
		for(ISharePreference existing: candidate.getSharePreferences().getPreferencesByType(sharePreference.getType())) {
			if(sharePreference.equals(existing)) {
				model.addAttribute("message", "Duplicate Share Preference already exists");
				model.addAttribute("success", false);
				return "jsonView";
			}
		}

		// passed validation
		Share updated = shareDao.addSharePreference(candidate, sharePreference);
		model.addAttribute("share", updated);
		model.addAttribute("success", true);
		return "jsonView";
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
			return "jsonView";
		}
		ISharePreference sharePreference = SharePreferences.construct(preferenceType, preferenceKey, preferenceValue);
		if(sharePreference == null) {
			model.addAttribute("message", "Invalid Share Preference");
			model.addAttribute("success", false);
			return "jsonView";
		}
		// passed validation
		Share updated = shareDao.removeSharePreference(candidate, sharePreference);
		model.addAttribute("share", updated);
		model.addAttribute("success", true);
		return "jsonView";
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
		List<Share> shares = shareDao.retrieveByOwner(activeAccount);
		for(Share s: shares) {
			if(s.getKey().equals(shareKey)) {
				return s;
			}
		}
		
		return null;
	}
	
}
