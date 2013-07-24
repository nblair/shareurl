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

package edu.wisc.wisccal.shareurl.web.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetailsImpl;
import edu.wisc.wisccal.shareurl.sasecurity.DelegateCalendarAccountUserDetailsImpl;

/**
 * {@link Controller} that provides a UI for searching for {@link IDelegateCalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateAccountSearchFormController.java 2039 2010-04-30 15:51:39Z npblair $
 */
@Controller
@RequestMapping("/delegate-search.html")
@SessionAttributes("command")
public class DelegateAccountSearchFormController {

	private IDelegateCalendarAccountDao delegateCalendarAccountDao;

	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired(required=true)
	public void setDelegateCalendarAccountDao(
			IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
	}

	/**
	 * If the qValue parameter is not blank, execute a search, and return
	 * the autocomplete results view name.
	 * Otherwise, return the form view name.
	 * @param qValue
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String onGet(@RequestParam(value="q",required=false) final String qValue, final ModelMap model) {
		CalendarAccountUserDetails currentPrincipal = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(currentPrincipal instanceof DelegateCalendarAccountUserDetailsImpl) {
			return "redirect:/resource-logout-first.html";
		}
		if(StringUtils.isBlank(qValue)) {
			DelegateAccountSearchFormBackingObject fbo = new DelegateAccountSearchFormBackingObject();
			model.addAttribute("command", fbo);
			return "security/delegate-search-form";
		}
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount currentAccount = currentUser.getCalendarAccount();
		model.addAttribute("searchText", qValue);
		List<IDelegateCalendarAccount> matches = new ArrayList<IDelegateCalendarAccount>();
		if(null != qValue && qValue.length() > 2) {
			matches = this.delegateCalendarAccountDao.searchForDelegates(qValue, currentAccount);
		}
		List<IDelegateCalendarAccount> results = filterForEligible(matches);
		model.addAttribute("results", results);
		return "security/delegate-search-results-ac";
	}

	/**
	 * 
	 * @param fbo
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String search(@ModelAttribute("command") DelegateAccountSearchFormBackingObject fbo, final ModelMap model) {
		CalendarAccountUserDetails currentPrincipal = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(currentPrincipal instanceof DelegateCalendarAccountUserDetailsImpl) {
			return "redirect:/resource-logout-first.html";
		}
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount currentAccount = currentUser.getCalendarAccount();
		model.addAttribute("searchText", fbo.getSearchText());
		List<IDelegateCalendarAccount> matches = new ArrayList<IDelegateCalendarAccount>();
		if(null != fbo.getSearchText() && fbo.getSearchText().length() > 2) {
			matches = this.delegateCalendarAccountDao.searchForDelegates(fbo.getSearchText(), currentAccount);
		}
		List<IDelegateCalendarAccount> results = filterForEligible(matches);
		model.addAttribute("results", results);
		return "security/delegate-search-results";
	}

	/**
	 * Filter out {@link IDelegateCalendarAccount} that return false for {@link IDelegateCalendarAccount#isEligible()}.
	 *
	 * @param matches
	 * @return
	 */
	protected List<IDelegateCalendarAccount> filterForEligible(List<IDelegateCalendarAccount> matches) {
		List<IDelegateCalendarAccount> results = new ArrayList<IDelegateCalendarAccount>();
		for(IDelegateCalendarAccount a: matches) {
			if(a.isEligible()) {
				results.add(a);
			}
		}
		return results;
	}
}
