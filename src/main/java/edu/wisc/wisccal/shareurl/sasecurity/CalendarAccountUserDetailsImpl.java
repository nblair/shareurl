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


package edu.wisc.wisccal.shareurl.sasecurity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@link CalendarAccountUserDetails} implementation for standard people {@link ICalendarAccount}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarAccountUserDetailsImpl.java 2979 2011-01-25 19:24:44Z npblair $
 */
public class CalendarAccountUserDetailsImpl implements CalendarAccountUserDetails {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private final ICalendarAccount calendarAccount;
	private boolean administrator = false;
	private String activeDisplayNameAttribute = "mail";
	private static final String EMPTY = "";

	/**
	 * Construct a new {@link CalendarAccountUserDetailsImpl} instance
	 * and set the {@link ICalendarAccount} field.
	 *
	 * @param calendarAccount
	 */
	CalendarAccountUserDetailsImpl(final ICalendarAccount calendarAccount) {
		this.calendarAccount = calendarAccount;
	}
	

	CalendarAccountUserDetailsImpl(final ICalendarAccount calendarAccount, String activeDisplayNameAttribute) {
		this.calendarAccount = calendarAccount;
		this.activeDisplayNameAttribute = activeDisplayNameAttribute;
	}

	/**
	 * @return the activeDisplayNameAttribute
	 */
	public String getActiveDisplayNameAttribute() {
		return activeDisplayNameAttribute;
	}

	/**
	 * @param activeDisplayNameAttribute the activeDisplayNameAttribute to set
	 */
	public void setActiveDisplayNameAttribute(String activeDisplayNameAttribute) {
		this.activeDisplayNameAttribute = activeDisplayNameAttribute;
	}

	/**
	 * Returns an array of {@link GrantedAuthority}s based on which fields are set:
	 * <ol>
	 * <li>if the "unregistered" {@link ICalendarAccount} is set and {@link ICalendarAccount#isEligible()}, adds {@link SecurityConstants#REGISTER}.</li>
	 * <li>if the {@link IScheduleVisitor} field is set and is eligible, adds {@link SecurityConstants#VISITOR}.</li>
	 * <li>if the {@link IScheduleOwner} field is set and is eligible, adds {@link SecurityConstants#OWNER}.</li>
	 * </ol>
	 * 
	 * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
	 */
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if(null != this.calendarAccount) {
			if(this.calendarAccount.isEligible()) {
				authorities.add(SecurityConstants.CALENDAR_ELIGIBLE);
			}
		}
		
		if(this.administrator) {
			authorities.add(SecurityConstants.ADMINISTRATOR);
		}

		return Collections.unmodifiableList(authorities);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getPassword()
	 */
	public String getPassword() {
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getUsername()
	 */
	public String getUsername() {
		return this.calendarAccount.getUsername();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
	 */
	public boolean isAccountNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
	 */
	public boolean isAccountNonLocked() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled() {
		return null != this.calendarAccount ? this.calendarAccount.isEligible() : false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getCalendarAccount()
	 */
	@Override
	public ICalendarAccount getCalendarAccount() {
		return calendarAccount;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#isDelegate()
	 */
	@Override
	public final boolean isDelegate() {
		return false;
	}

	/**
	 * @return the administrator
	 */
	boolean isAdministrator() {
		return this.administrator;
	}
	/**
	 * @param administrator the administrator to set
	 */
	void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getActiveDisplayName()
	 */
	public String getActiveDisplayName() {
		StringBuilder display = new StringBuilder();
		display.append(this.calendarAccount.getDisplayName());
		display.append(" (");
		display.append(this.calendarAccount.getAttributeValue(activeDisplayNameAttribute));
		display.append(")");
		return display.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CalendarAccountUserDetailsImpl [calendarAccount=");
		builder.append(calendarAccount);
		builder.append(", administrator=");
		builder.append(administrator);
		builder.append("]");
		return builder.toString();
	}
	
}
