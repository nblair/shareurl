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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.impl.ldap.LDAPPersonCalendarAccountImpl;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;
import edu.wisc.wisccal.shareurl.sasecurity.DelegateCalendarAccountUserDetailsImpl;

/**
 * Simple {@link Controller} to display the delegate-login form.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 */
@Controller
public class DelegateLoginController  {
	protected static final String FORM_VIEW_NAME = "security/delegate-login-form";

	private Log log = LogFactory.getLog(this.getClass());
	private ICalendarAccountDao calendarAccountDao;
	private IDelegateCalendarAccountDao delegateCalendarAccountDao;
	
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	@Qualifier("people")
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired
	public void setDelegateCalendarAccountDao(
			IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
	}  
	
	/**
	 * 
	 * @return the name of the view for the delegate login controller
	 */
	@RequestMapping("/delegate-login.html")
	protected String viewLoginForm(final ModelMap model) {
		CalendarAccountUserDetails currentPrincipal = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(currentPrincipal instanceof DelegateCalendarAccountUserDetailsImpl) {
			return "redirect:/resource-logout-first.html";
		}
		ICalendarAccount calendarAccount = currentPrincipal.getCalendarAccount();
		
		LDAPPersonCalendarAccountImpl owner = (LDAPPersonCalendarAccountImpl) calendarAccount;
		List<ICalendarAccount> linkedAccounts = calendarAccountDao.getLinkedAccounts(owner);
		List<IDelegateCalendarAccount> linkedDelegateAccounts = delegateCalendarAccountDao.getDelegateAccounts(owner.getDistinguishedName(), calendarAccount);
		
		model.addAttribute("ownerLinkedAccounts", linkedAccounts);
		model.addAttribute("ownerDelegagteAccounts", linkedDelegateAccounts);
		
		return FORM_VIEW_NAME;
	}

}
