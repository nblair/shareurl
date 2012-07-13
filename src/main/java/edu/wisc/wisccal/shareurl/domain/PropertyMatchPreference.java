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

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Subclass of {@link AbstractSharePreference} that
 * matches an iCal4j {@link Property}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PropertyMatchPreference.java 1677 2010-02-08 18:31:01Z npblair $
 */
public class PropertyMatchPreference extends AbstractSharePreference {

	private final String propertyName;
	private final String propertyValue;
	
	public PropertyMatchPreference(final String propertyName, final String propertyValue) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/**
	 * 
	 */
	public static final String PROPERTY_MATCH =  "PROPERTY_MATCH";

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public boolean participatesInFiltering() {
		return true;
	}

	/**
	 * 
	 * @param event
	 * @return true if the event matches this preference
	 */
	public boolean matches(final VEvent event) {
		Validate.notNull(event, "event argument must not be null");
		Property eventProperty = event.getProperties().getProperty(propertyName);
		if(null == eventProperty) {
			return false;
		} else {
			return StringUtils.containsIgnoreCase(eventProperty.getValue(), this.getValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ISharePreference#getKey()
	 */
	@Override
	public String getKey() {
		return this.propertyName;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ISharePreference#getType()
	 */
	@Override
	public String getType() {
		return PROPERTY_MATCH;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ISharePreference#getValue()
	 */
	@Override
	public String getValue() {
		return this.propertyValue;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		if(Summary.SUMMARY.equals(getKey())) {
			return "Title contains " + getValue();
		} else if (Description.DESCRIPTION.equals(getKey())) {
			return "Description contains " + getValue();
		} else if (Location.LOCATION.equals(getKey())) {
			return "Location contains " + getValue();
		} else {
			return getKey() + " contains " + getValue();
		}
	}
	
	
}
