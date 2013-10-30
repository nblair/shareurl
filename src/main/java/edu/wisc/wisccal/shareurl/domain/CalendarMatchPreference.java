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
package edu.wisc.wisccal.shareurl.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CalendarMatchPreference extends AbstractSharePreference {

	
	private final String propertyName;
	private final String propertyValue;
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	
	public static final String CALENDAR_MATCH =  "CALENDAR_MATCH";
	public static final String CALENDAR_MATCH_PROPERTY_NAME = "X-CALENDAR_MATCH";
	
	public static final String EXCHANGE_CALENDAR_IDENTIFIER = "Office 365";
	public static final String ORACLE_CALENDAR_IDENTIFIER = "WiscCal";
	
	public boolean filterEnabled = true;
	
	public CalendarMatchPreference(final String propertyName, final String propertyValue) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}
	
	
	@Override
	public String getType() {
		return CALENDAR_MATCH;
	}

	@Override
	public String getKey() {
		return this.propertyName;
	}

	@Override
	public String getValue() {
		return this.propertyValue;
	}

	@Override
	public String getDisplayName() {
		try {
			return URLDecoder.decode(getKey(), "UTF-8");
		} catch (Exception e) {
			return getKey();
		}
	}

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
		
		Property eventProperty = event.getProperties().getProperty(CALENDAR_MATCH_PROPERTY_NAME);
				
		if(null != eventProperty) {
			//log.debug("\nEvent contains: "+CALENDAR_MATCH_PROPERTY_NAME+" = "+eventProperty.getValue());
			//log.debug("CalendarMatch preference "+ this.getKey() + " = "+ this.getValue());
			
			if(!filterEnabled) return true;
			
			//if exchange calendar properties should be equal
			if(StringUtils.equals(eventProperty.getValue(), this.getValue())){
				//log.debug("Exchange match");
				return true;
			}
			
			if(StringUtils.contains(eventProperty.getValue(), this.getValue())){
				for(String part : eventProperty.getValue().split("/") ){
					if(StringUtils.equals(part+"/", this.getValue())){
						//log.debug("WiscCal match");
						return true;
					}
				}
			}
		}
		return false;
	}

}
