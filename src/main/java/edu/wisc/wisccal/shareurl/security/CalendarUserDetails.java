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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.IDelegateCalendarAccount;

/**
 * {@link UserDetails} implementation used by {@link CalendarUserDetailsServiceImpl}.
 * Holds a {@link ICalendarAccount} object (or subclass).
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarUserDetails.java 3129 2011-03-08 15:20:32Z npblair $
 */
public class CalendarUserDetails implements UserDetails {

	/**
	 * Name of Calendar Eligibility role
	 */
	public static final String CALENDAR_ELIGIBLE = "ROLE_CALENDAR_ELIGIBLE";

	private static final long serialVersionUID = 53706L;

	private final ICalendarAccount calendarAccount;
	private ICalendarAccount actingOnBehalfOf = null;

	/**
	 * @param calendarAccount
	 */
	public CalendarUserDetails(ICalendarAccount calendarAccount) {
		this.calendarAccount = calendarAccount;
	}

	/**
	 * @return the actingOnBehalfOf
	 */
	public ICalendarAccount getActingOnBehalfOf() {
		return actingOnBehalfOf;
	}
	/**
	 * @param actingOnBehalfOf the actingOnBehalfOf to set
	 */
	protected void setActingOnBehalfOf(ICalendarAccount actingOnBehalfOf) {
		this.actingOnBehalfOf = actingOnBehalfOf;
	}
	/**
	 * Short cut to set this instance's actingOnBehalfField to null.
	 */
	public void clearActingOnBehalfOf() {
		this.actingOnBehalfOf = null;
	}
	/**
	 * 
	 * @return true if this instance has a non-null "acting on behalf of" {@link ICalendarAccount}
	 */
	public boolean isActingAsDelegate() {
		return null != this.actingOnBehalfOf;
	}

	/**
	 * First tests actingOnBehalfOf for eligibility, returning null if actingOnBehalfOf is not eligible.
	 * 
	 * If actingOnBehalfOf is null, tests calendarAccount similarly.
	 * 
	 * (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
	 */
	public Collection<GrantedAuthority> getAuthorities() {
		ICalendarAccount active = getActiveAccount();

		if(null != active && active.isEligible()) {
			GrantedAuthority authority = new GrantedAuthorityImpl(CALENDAR_ELIGIBLE);
			List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
			result.add(authority);
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * We don't have the password in this application.
	 * Pubcookie obviates the need for it. Other SSO applications
	 * (like CAS) do the same.
	 * 
	 *  (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#getPassword()
	 */
	public String getPassword() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getUsername()
	 */
	public String getUsername() {
		ICalendarAccount active = getActiveAccount();
		return null == active ? null : active.getUsername();
	}

	/**
	 * Always true.
	 * 
	 *  (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isAccountNonExpired()
	 */
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Always return true.
	 * 
	 *  (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isAccountNonLocked()
	 */
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Always true.
	 * 
	 *  (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled() {
		ICalendarAccount active = getActiveAccount();

		if(null == active) {
			return false;
		} else {
			return active.isEligible();
		}
	}

	/**
	 * @return the calendarAccount
	 */
	public ICalendarAccount getCalendarAccount() {
		return calendarAccount;
	}

	/**
	 * Returns the actingOnBehalfOf account if it is not null, otherwise
	 * the calendarAccount.
	 * 
	 * @return the active {@link ICalendarAccount}
	 */
	public ICalendarAccount getActiveAccount() {
		return null != actingOnBehalfOf ? actingOnBehalfOf : calendarAccount;
	}

	/**
	 * Return a formatted string that identifies the active account.
	 * @return
	 */
	public String getActiveDisplayName() {
		ICalendarAccount active = getActiveAccount();
		if(null == active) {
			return "unknown (not logged in)";
		} else if(active instanceof IDelegateCalendarAccount) {
			StringBuilder display = new StringBuilder();
			display.append(active.getName());
			display.append(" (managed by ");
			display.append(calendarAccount.getUsername());
			display.append(")");
			return display.toString();
		} else {
			return active.getUsername();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("calendarAccount", calendarAccount);
		builder.append("actingOnBehalfOf", actingOnBehalfOf);
		return builder.toString();
	}
	
	
}
