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

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
	/**
	 * 
	 * @return the name of the view for the delegate login controller
	 */
	@RequestMapping("/delegate-login.html")
	protected String viewLoginForm() {
		CalendarAccountUserDetails currentPrincipal = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(currentPrincipal instanceof DelegateCalendarAccountUserDetailsImpl) {
			return "redirect:/resource-logout-first.html";
		}
		return FORM_VIEW_NAME;
	}

}
