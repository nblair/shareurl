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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean to represent the set of preferences associated with a {@link Share}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SharePreferences.java 1677 2010-02-08 18:31:01Z npblair $
 */
public class SharePreferences implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	private Set<ISharePreference> preferences = new HashSet<ISharePreference>();
	
	/**
	 * 
	 */
	public SharePreferences() {
	}
	/**
	 * 
	 * @param preferences
	 */
	public SharePreferences(Set<ISharePreference> preferences) {
		this.preferences = preferences;
	}
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addPreference(ISharePreference pref) {
		if(null != pref) {
			this.preferences.add(pref);
		}
	}
	
	public Set<ISharePreference> getPreferences() {
		return new HashSet<ISharePreference>(preferences);
	}
	
	public Set<ISharePreference> getPreferencesByType(String type) {
		Set<ISharePreference> result = new HashSet<ISharePreference>();
		for(ISharePreference p : this.preferences) {
			if(p.getType().equals(type)) {
				result.add(p);
			}
		}
		return result;
	}
	
	/**
	 * Short cut to determine if this object
	 * has the FreeBusy preference.
	 * @return
	 */
	public boolean isFreeBusyOnly() {
		Set<ISharePreference> prefs = getPreferencesByType(FreeBusyPreference.FREE_BUSY);
		if(prefs.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Short cut to determine if this share has an
	 * IncludeParticipants preference set to true.
	 * 
	 * @return the value of the IncludeParticipants preference, or false if not set
	 */
	public boolean isIncludeParticipants() {
		Set<ISharePreference> prefs = getPreferencesByType(IncludeParticipantsPreference.INCLUDE_PARTICIPANTS);
		for(ISharePreference pref: prefs) {
			return Boolean.parseBoolean(pref.getValue());
		}
		// preference not present, default is false
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("preferences", preferences);
		return builder.toString();
	}
	
	
}
