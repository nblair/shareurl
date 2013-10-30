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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.wisccal.shareurl.support.ProblematicRecurringEventSharePreference;

/**
 * Bean to represent the set of preferences associated with a {@link Share}.
 *  
 * @author Nicholas Blair
 */
public class SharePreferences implements Serializable {

	
	public static final String FILTER_DISPLAY_SEPARATOR = ", ";
	private static final Log LOG = LogFactory.getLog(SharePreferences.class);
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
	/**
	 * 
	 * @param pref
	 * @return true if the preference was successfully removed.
	 */
	public boolean removePreference(ISharePreference pref) {
		if(null != pref) {
			return this.preferences.remove(pref);
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return the {@link Set} of {@link ISharePreference}s that return true for {@link ISharePreference#participatesInFiltering()}.
	 */
	public Set<ISharePreference> getFilterPreferences() {
		Set<ISharePreference> results = new HashSet<ISharePreference>();
		for(ISharePreference p:preferences) {
			if(p.participatesInFiltering()) {
				results.add(p);
			}
		}
		return results;
	}
	/**
	 * Never null, but potentially empty.
	 * 
	 * @return a copy of this instance's {@link Set} of {@link ISharePreference}s.
	 */
	public Set<ISharePreference> getPreferences() {
		return new HashSet<ISharePreference>(preferences);
	}
	
	public Set<ISharePreference> getPropertyMatchPreferences() {
		return getPreferencesByType(PropertyMatchPreference.PROPERTY_MATCH);
	}
	
	public Set<ISharePreference> getCalendarMatchPreferences(){
		return getPreferencesByType(CalendarMatchPreference.CALENDAR_MATCH);
	}
	/**
	 * Never null, but potentially empty.
	 * 
	 * @param type
	 * @return a set of {@link ISharePreference}s in this instance with the specified type
	 */
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
	 * Return the {@link ISharePreference} from this set of the specified
	 * type if and only if 1 instance of that type exists.
	 * 
	 * Returns null if 0 or more than 1 preference of the specified type exists.
	 * 
	 * @param type
	 * @return
	 */
	public ISharePreference getPreferenceByType(String type) {
		Set<ISharePreference> typeSet = getPreferencesByType(type);
		if(typeSet.size() == 1) {
			return typeSet.iterator().next();
		}
		
		return null;
	}
	
	/**
	 * Short cut to determine if this object
	 * has the FreeBusy preference.
	 * @return
	 */
	public boolean isFreeBusyOnly() {
		return containsAny(FreeBusyPreference.FREE_BUSY);
	}
	
	public boolean isRevocable() {
		return !containsAny(NonRevocablePreference.NON_REVOCABLE);
	}
	
	
	public boolean isIncludeSourceCalendar() {
		Set<ISharePreference> prefs = getPreferencesByType(IncludeSourceCalendarPreference.INCLUDE_SOURCE_CALENDAR);
		for(ISharePreference pref: prefs) {
			return Boolean.parseBoolean(pref.getValue());
		}
		//preference not present, default is false
		return false;
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
	
	/**
	 * Short cut to determine if this share has a CALENDAR_MATCH preference.
	 * 
	 * @return true if a calendarMatch pref exists, false otherwise
	 */
	public boolean isCalendarSelect() {
		Set<ISharePreference> prefs = getPreferencesByType(CalendarMatchPreference.CALENDAR_MATCH);
		return (prefs.size() > 0);
	}
	/**
	 * 
	 * @return true if contains {@link GuessableSharePreference}
	 */
	public boolean isGuessable() {
		return containsAny(GuessableSharePreference.GUESSABLE);
	}
	/**
	 * 
	 * @return
	 */
	public boolean containsProblemRecurringPreference() {
		return containsAny(ProblematicRecurringEventSharePreference.PROBLEM_RECURRENCE_SUPPORT);
	}
	/**
	 * 
	 * @param type
	 * @return true if this instance contains any preferences with the specified preference type
	 */
	protected boolean containsAny(String type) {
		Set<ISharePreference> prefs = getPreferencesByType(type);
		return prefs.size() > 0;
	}
	/**
	 * 
	 * @return
	 */
	public int getEventFilterCount() {
		int filteringPreferenceCount = 0;
		for(ISharePreference pref : preferences) {
			if(PropertyMatchPreference.PROPERTY_MATCH.equals(pref.getType()) || AccessClassificationMatchPreference.CLASS_ATTRIBUTE.equals(pref.getType())) {
				filteringPreferenceCount++;
			}
		}
		return filteringPreferenceCount;
	}
	
	/**
	 * 
	 * @return a human readable display of the filter type properties associated with these preferences
	 */
	public String getFilterDisplay() {
		StringBuilder display = new StringBuilder();
		Set<ISharePreference> prefs = getPreferencesByType(PropertyMatchPreference.PROPERTY_MATCH);
		for(Iterator<ISharePreference> i = prefs.iterator(); i.hasNext();) {
			ISharePreference p = i.next();
			display.append(p.getDisplayName());
			if(i.hasNext()) {
				display.append(FILTER_DISPLAY_SEPARATOR);
			}
		}
		
		prefs = getPreferencesByType(AccessClassificationMatchPreference.CLASS_ATTRIBUTE);
		if(prefs.size() != 0 && display.length() != 0) {
			display.append(FILTER_DISPLAY_SEPARATOR);
		}
		for(Iterator<ISharePreference> i = prefs.iterator(); i.hasNext();) {
			ISharePreference p = i.next();
			display.append(p.getDisplayName());
			if(i.hasNext()) {
				display.append(FILTER_DISPLAY_SEPARATOR);
			}
		}
		
		return display.toString();
		
	}
	/**
	 * 
	 * @return
	 */
	public List<String> getClassificationFilters() {
		List<String> results = new ArrayList<String>();
		Set<ISharePreference> preferences = getPreferencesByType(AccessClassificationMatchPreference.CLASS_ATTRIBUTE);
		for(ISharePreference pref: preferences) {
			results.add(pref.getValue());
		}
		return results;
	}
	
	
	public List<ContentFilter> getCalendarFilters(){
		Map<String, CalendarFilterImpl> map = new HashMap<String, CalendarFilterImpl>();
		Set<ISharePreference> filterPreferences = getPreferencesByType(CalendarMatchPreference.CALENDAR_MATCH);
		for(final ISharePreference pref: filterPreferences){
			CalendarFilterImpl filter = map.get(pref.getKey());
			if(filter == null){
				map.put(pref.getKey(), new CalendarFilterImpl(pref));
			}else{
				filter.addMatchValue(pref.getValue());
			}
		}
		return new ArrayList<ContentFilter>(map.values());
	}
	
	/**
	 * 
	 * @return
	 */
	public List<ContentFilter> getContentFilters() {
		Map<String, ContentFilterImpl> map = new HashMap<String, ContentFilterImpl>();
		Set<ISharePreference> filterPreferences = getPreferencesByType(PropertyMatchPreference.PROPERTY_MATCH);
		for(final ISharePreference pref: filterPreferences) {
			ContentFilterImpl filter = map.get(pref.getKey());
			if(filter == null) {
				map.put(pref.getKey(), new ContentFilterImpl(pref));
			} else {
				filter.addMatchValue(pref.getValue());
			}
		}
		return new ArrayList<ContentFilter>(map.values());
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
	
	/**
	 * Invoke {@link ISharePreference#dispose()} on all preferences.
	 */
	public void disposeAll() {
		for(ISharePreference p: preferences) {
			p.dispose();
		}
	}

	/**
	 * Construct a concrete {@link ISharePreference} from the 3 string arguments.
	 * Will return null if the preferenceType is unknown, or if the key is an unsupported value.
	 * 
	 * @param preferenceType the value for {@link ISharePreference#getType()}
	 * @param preferenceKey the value for {@link ISharePreference#getKey()}
	 * @param preferenceValue the value for {@link ISharePreference#getValue()}
	 * @return one of the {@link ISharePreference} implementations 
	 */
	public static ISharePreference construct(String preferenceType, String preferenceKey, String preferenceValue) {
		if(FreeBusyPreference.FREE_BUSY.equals(preferenceType)) {
			return new FreeBusyPreference();
		} else if(GuessableSharePreference.GUESSABLE.equals(preferenceType)) {
			return new GuessableSharePreference();
		} else if(AccessClassificationMatchPreference.CLASS_ATTRIBUTE.equals(preferenceType)) {
			AccessClassification access = AccessClassification.valueOf(preferenceValue);
			if(access == null) {
				return null;
			}
			return new AccessClassificationMatchPreference(access);
		} else if(PropertyMatchPreference.PROPERTY_MATCH.equals(preferenceType)){
			return new PropertyMatchPreference(preferenceKey, preferenceValue);
		} else if (IncludeParticipantsPreference.INCLUDE_PARTICIPANTS.equals(preferenceType)) {
			return new IncludeParticipantsPreference(Boolean.parseBoolean(preferenceValue));
		}else if( CalendarMatchPreference.CALENDAR_MATCH.equals(preferenceType)){
			LOG.trace("new CalendarMatchPreference(preferenceKey="+preferenceKey+", preferenceValue="+preferenceValue+")");
			return new CalendarMatchPreference(preferenceKey, preferenceValue);
		}else if(IncludeSourceCalendarPreference.INCLUDE_SOURCE_CALENDAR.equals(preferenceType)) {
			return new IncludeSourceCalendarPreference(Boolean.parseBoolean(preferenceValue));
		} else {
			LOG.warn("could not match any preference types for type=" + preferenceType + ", key=" + preferenceKey + ", value=" + preferenceValue + ", returning null");
			return null;
		}
	}
	
	/**
	 * 
	 * @author Nicholas Blair
	 */
	static class ContentFilterImpl implements ContentFilter {
		private final ISharePreference preference;
		private final List<String> matchValues = new ArrayList<String>();
		/**
		 * @param preference
		 */
		ContentFilterImpl(ISharePreference preference) {
			this.preference = preference;
			addMatchValue(preference.getValue());
		}
		/*
		 * (non-Javadoc)
		 * @see edu.wisc.wisccal.shareurl.domain.ContentFilter#getPropertyName()
		 */
		@Override
		public String getPropertyName() {
			return preference.getKey();
		}
		/*
		 * (non-Javadoc)
		 * @see edu.wisc.wisccal.shareurl.domain.ContentFilter#getMatchValue()
		 */
		@Override
		public List<String> getMatchValues() {
			return matchValues;
		}
		
		/**
		 * 
		 * @param value
		 * @return
		 */
		public void addMatchValue(String value) {
			matchValues.add(value);
		}
	}
	/**
	 * 
	 * @author ctcudd
	 *
	 */
	static class CalendarFilterImpl implements ContentFilter {
		private final ISharePreference preference;
		private final List<String> matchValues = new ArrayList<String>();
		
		/**
		 * 
		 * @param preference
		 */
		CalendarFilterImpl(ISharePreference preference){
			this.preference = preference;
			addMatchValue(preference.getValue());
		}
		
		@Override
		public String getPropertyName() {
			return preference.getKey();
		}

		@Override
		public List<String> getMatchValues() {
			return matchValues;
		}
		
		/**
		 * 
		 * @param value
		 * @return
		 */
		public void addMatchValue(String value) {
			matchValues.add(value);
		}
	}
}
