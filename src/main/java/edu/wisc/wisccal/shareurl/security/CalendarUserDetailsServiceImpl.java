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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.ICalendarAccountDao;

/**
 * Acegi Integration point.
 * UserDetailsService implementation that depends on a UserDao.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarUserDetailsServiceImpl.java 1441 2009-12-17 19:09:21Z npblair $
 */
public class CalendarUserDetailsServiceImpl implements UserDetailsService {

	private static final String NONE_PROVIDED = "NONE_PROVIDED";
	private Log LOG = LogFactory.getLog(this.getClass());
	private ICalendarAccountDao accountDao;
	/**
	 * @param accountDao the accountDao to set
	 */
	@Required
	public void setAccountDao(ICalendarAccountDao accountDao) {
		this.accountDao = accountDao;
	}

	/**
	 * If the argument "NONE_PROVIDED" is passed into this method, we short-circuit
	 * the call to the userDao and throw UsernameNotFoundException immediately.
	 * For all other username fields, the userDao is consulted. If the userDao returns null,
	 * a UsernameNotFoundException is thrown, per contract.
	 * 
	 * "NONE_PROVIDED" is the default value for username as set by 
	 * {@link org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider}
	 *
	 *  (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException, DataAccessException {	
		if(NONE_PROVIDED.equals(username)) {
			LOG.debug("caught NONE_PROVIDED being passed into loadUserByUsername");
			throw new UsernameNotFoundException(NONE_PROVIDED);
		}
		ICalendarAccount user = accountDao.getCalendarAccount(username);
		if(null != user) {
			return new CalendarUserDetails(user);
		}
		else {
			throw new UsernameNotFoundException("User not found: " + username);
		}
	}

}
