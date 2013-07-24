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
/**
 * 
 */

package edu.wisc.wisccal.shareurl.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * @author Nicholas Blair
 */
@Controller
@RequestMapping("/search")
public class PublicShareSearchController {

	private ICalendarAccountDao calendarAccountDao;
	private IShareDao shareDao;
	private AutomaticPublicShareService autoPublicShareService;
	
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
	}
	/**
	 * @param autoPublicShareService the autoPublicShareService to set
	 */
	@Autowired
	public void setAutoPublicShareService(
			AutomaticPublicShareService autoPublicShareService) {
		this.autoPublicShareService = autoPublicShareService;
	}
	/**
	 * 
	 * @return
	 */
	@RequestMapping
	public String showForm(@RequestParam(required=false) String login, ModelMap model) {
		if(login != null) {
			model.put("showLogin", true);
		}
		return "public-share-search";
	}
	/**
	 * 
	 * @param query
	 * @param model
	 * @return
	 */
	@RequestMapping(params="q")
	public String search(@RequestParam(value="q") String query, ModelMap model) {
		if(StringUtils.length(query) >= 3) {
			List<ICalendarAccount> accounts = calendarAccountDao.searchForCalendarAccounts(query);
			model.put("shares", locateValidPublicShareKeys(accounts));
		}
		return "public-share-results";
	}
	/**
	 * 
	 * @param target
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String submit(@RequestParam String shareKey) {
		return "redirect:/u/" + shareKey;
	}
	/**
	 * 
	 * @param accounts
	 * @return
	 */
	protected List<String> locateValidPublicShareKeys(List<ICalendarAccount> accounts) {
		List<String> results = new ArrayList<String>();
		for(ICalendarAccount account: accounts) {
			Share custom = shareDao.retrieveByKey(account.getEmailAddress());
			if(custom == null) {
				custom = autoPublicShareService.getAutomaticPublicShare(account.getEmailAddress());
			} 
			
			if(custom != null) {
				results.add(account.getEmailAddress());
			}
		}
		return results;
	}
}
