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
package edu.wisc.wisccal.shareurl.impl.ldap;

import org.apache.commons.lang.StringUtils;

import edu.wisc.wisccal.shareurl.ICalendarAccount;

/**
 *
 * @author Nicholas Blair
 * @version $Id: LDAPCalendarAccountImpl.java $
 */
class LDAPCalendarAccountImpl implements ICalendarAccount {

	private String name;
	private String username;
	private String emailAddress;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccount#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccount#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccount#getCalendarLoginId()
	 */
	@Override
	public String getCalendarLoginId() {
		return getEmailAddress();
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccount#getCalendarUniqueId()
	 */
	@Override
	public String getCalendarUniqueId() {
		return getEmailAddress();
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccount#isEligible()
	 */
	@Override
	public boolean isEligible() {
		return StringUtils.isNotBlank(getEmailAddress());
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LDAPCalendarAccountImpl other = (LDAPCalendarAccountImpl) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LDAPCalendarAccountImpl [name=" + name + ", username="
				+ username + ", emailAddress=" + emailAddress + "]";
	}

}
