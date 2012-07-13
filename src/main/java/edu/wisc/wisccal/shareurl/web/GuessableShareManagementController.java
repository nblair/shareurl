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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wisc.wisccal.shareurl.GuessableShareAlreadyExistsException;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;

/**
 * @author Nicholas Blair
 */
@Controller
public class GuessableShareManagementController {

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
	
	@RequestMapping(value="/my-public",method=RequestMethod.GET )
	public String getMyGuessable(ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling getMyGuessable request for " + activeAccount);
		}
		
		Share share = shareDao.retrieveGuessableShare(activeAccount);
		model.put("share", share);
		return "jsonView";
	}
	
	@RequestMapping(value="/create-public",method=RequestMethod.POST )
	public String createDefaultGuessable(ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling createDefaultGuessable request for " + activeAccount);
		}
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new FreeBusyPreference());
		try {
			Share share = shareDao.generateGuessableShare(activeAccount, preferences);
			model.addAttribute("share", share);
			model.addAttribute("success", true);
		} catch (GuessableShareAlreadyExistsException e) {
			model.addAttribute("success", false);
			model.addAttribute("message", "Public ShareURL already exists.");
		}
		
		return "jsonView";
	}
	
	@RequestMapping(value="/remove-public", method=RequestMethod.POST)
	public String removeGuessable(ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling removeGuessable request for " + activeAccount);
		}
		
		Share share = shareDao.retrieveGuessableShare(activeAccount);
		if(share != null) {
			shareDao.revokeShare(share);
		}
		model.addAttribute("success", true);
		return "jsonView";
	}
}
