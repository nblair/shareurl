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
package edu.wisc.wisccal.shareurl.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean representing the mapping between a unique key
 * and the customer's calendar system unique id.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: Share.java 1677 2010-02-08 18:31:01Z npblair $
 */
public class Share implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	private String ownerCalendarUniqueId;
	private String key;
	private SharePreferences sharePreferences = new SharePreferences();
	private boolean valid = true;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the ownerCalendarUniqueId
	 */
	public String getOwnerCalendarUniqueId() {
		return ownerCalendarUniqueId;
	}

	/**
	 * @param ownerCalendarUniqueId the ownerCalendarUniqueId to set
	 */
	public void setOwnerCalendarUniqueId(String ownerCalendarUniqueId) {
		this.ownerCalendarUniqueId = ownerCalendarUniqueId;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	/**
	 * @return the sharePreferences
	 */
	public SharePreferences getSharePreferences() {
		return sharePreferences;
	}

	/**
	 * @param sharePreferences the sharePreferences to set
	 */
	public void setSharePreferences(SharePreferences sharePreferences) {
		this.sharePreferences = sharePreferences;
	}

	/**
	 * 
	 * @return true if this share is a "Free Busy Only" share
	 */
	public boolean isFreeBusyOnly() {
		return this.sharePreferences.isFreeBusyOnly();
	}
	
	/**
	 * 
	 * @return true if this share will include event participants
	 */
	public boolean isIncludeParticipants() {
		return this.sharePreferences.isIncludeParticipants();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getEventFilterCount() {
		int filteringPreferenceCount = 0;
		for(ISharePreference pref : this.sharePreferences.getPreferences()) {
			if(!FreeBusyPreference.FREE_BUSY.equals(pref.getType())) {
				filteringPreferenceCount++;
			}
		}
		return filteringPreferenceCount;
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof Share)) {
			return false;
		}
		Share rhs = (Share) object;
		return new EqualsBuilder()
			.append(this.ownerCalendarUniqueId, rhs.ownerCalendarUniqueId)
			.append(this.key, rhs.key)
			.append(this.sharePreferences, rhs.sharePreferences)
			.isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(1899521601, -573056467)
			.append(this.ownerCalendarUniqueId)
			.append(this.key)
			.append(this.sharePreferences)
			.toHashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("ownerCalendarUniqueId", this.ownerCalendarUniqueId)
			.append("name", this.key)
			.append("sharePreferences", sharePreferences)
			.toString();
	}
	
}
