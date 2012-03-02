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
package edu.wisc.wisccal.shareurl.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import edu.wisc.wisccal.shareurl.IDelegateCalendarAccount;
import edu.wisc.wisccal.shareurl.IDelegateCalendarAccountDao;

/**
 * Helper class for delegate-login web flow.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateLoginHelper.java 1876 2010-04-07 17:01:09Z npblair $
 */
@Component
public class DelegateLoginHelper {

	private Log LOG = LogFactory.getLog(this.getClass());
	private IDelegateCalendarAccountDao delegateAccountDao;
	
	private static final String SUCCESS = "success";
	private static final String FAILURE = "failure";
	/**
	 * @param delegateAccountDao the delegateAccountDao to set
	 */
	@Autowired
	public void setDelegateAccountDao(IDelegateCalendarAccountDao delegateAccountDao) {
		this.delegateAccountDao = delegateAccountDao;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws DelegateNotFoundException
	 */
	public IDelegateCalendarAccount getDelegate(final String name) throws DelegateNotFoundException{
		SecurityContext context = SecurityContextHolder.getContext();
		
		Authentication auth = context.getAuthentication();
		if(auth == null) {
			throw new IllegalStateException("context not currently authenticated");
		}
		CalendarUserDetails currentUser = (CalendarUserDetails) auth.getPrincipal();
		IDelegateCalendarAccount target = delegateAccountDao.getDelegate(name, currentUser.getCalendarAccount());
		
		return target;
	}
	
	/**
	 * 
	 * @param delegate
	 */
	public String loginAsDelegate(final IDelegateCalendarAccount delegate) {
		if(null == delegate) {
			return FAILURE;
		}
		SecurityContext context = SecurityContextHolder.getContext();
		
		Authentication auth = context.getAuthentication();
		if(auth == null) {
			throw new IllegalStateException("context not currently authenticated");
		}
		CalendarUserDetails currentUser = (CalendarUserDetails) auth.getPrincipal();
		
		currentUser.setActingOnBehalfOf(delegate);
		LOG.warn("loginAsDelegate complete for " + currentUser);
		return SUCCESS;
	}
	
	/**
	 * 
	 */
	public void logoutDelegate() {
		SecurityContext context = SecurityContextHolder.getContext();
		
		Authentication auth = context.getAuthentication();
		if(auth == null) {
			throw new IllegalStateException("context not currently authenticated");
		}
		CalendarUserDetails currentUser = (CalendarUserDetails) auth.getPrincipal();
		currentUser.clearActingOnBehalfOf();
		LOG.warn("logoutDelegate complete for " + currentUser);
	}
}
