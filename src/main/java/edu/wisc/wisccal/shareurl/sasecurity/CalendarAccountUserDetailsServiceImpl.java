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

package edu.wisc.wisccal.shareurl.sasecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * {@link UserDetailsService} for person {@link ICalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarAccountUserDetailsServiceImpl.java 2979 2011-01-25 19:24:44Z npblair $
 */
@Service("userDetailsService")
public class CalendarAccountUserDetailsServiceImpl implements
		UserDetailsService {

	private static final String NONE_PROVIDED = "NONE_PROVIDED";
	private ICalendarAccountDao calendarAccountDao;

	private List<String> administrators = new ArrayList<String>();
	private String identifyingAttributeName = "uid";
	private String activeDisplayNameAttribute = "uid";
	protected final Log LOG = LogFactory.getLog(this.getClass());
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param administrators the administrators to set
	 */
	public void setAdministratorListProperty(String propertyValue) {
		String [] admins = StringUtils.commaDelimitedListToStringArray(propertyValue);
		this.administrators = Arrays.asList(admins);
	}
	/**
	 * 
	 * @param identifyingAttributeName
	 */
	@Value("${users.visibleIdentifierAttributeName:uid}")
	public void setIdentifyingAttributeName(String identifyingAttributeName) {
		this.identifyingAttributeName = identifyingAttributeName;
	}
	/**
	 * 
	 * @return
	 */
	public String getActiveDisplayNameAttribute() {
		return activeDisplayNameAttribute;
	}
	/**
	 * 
	 * @param activeDisplayNameAttribute
	 */
	public void setActiveDisplayNameAttribute(String activeDisplayNameAttribute) {
		this.activeDisplayNameAttribute = activeDisplayNameAttribute;
	}
	/**
	 * 
	 * @return the attribute used to commonly uniquely identify an account
	 */
	public String getIdentifyingAttributeName() {
		return identifyingAttributeName;
	}
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public final UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException, DataAccessException {
		if(NONE_PROVIDED.equals(username)) {
			LOG.debug("caught NONE_PROVIDED being passed into loadUserByUsername");
			throw new UsernameNotFoundException(NONE_PROVIDED);
		}
		ICalendarAccount calendarAccount = calendarAccountDao.getCalendarAccount(this.identifyingAttributeName, username);
		if(null == calendarAccount) {
			throw new UsernameNotFoundException("no calendar account found for " + username);
		}
		CalendarAccountUserDetailsImpl result = new CalendarAccountUserDetailsImpl(calendarAccount);
		result.setActiveDisplayNameAttribute(this.activeDisplayNameAttribute);
		
		final String id = calendarAccount.getAttributeValue(this.activeDisplayNameAttribute);
		if(this.administrators.contains(id)) {
			result.setAdministrator(true);
		}
		return result;
	}
}
