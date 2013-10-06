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

public class IncludeSourceCalendarPreference extends AbstractSharePreference {

	public static final String INCLUDE_SOURCE_CALENDAR = "INCLUDE_SOURCE_CALENDAR";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5855143549140370510L;
	
	private final boolean includeSourceCalendar;
	

	public IncludeSourceCalendarPreference(boolean includeSourceCalendar) {
		super();
		this.includeSourceCalendar = includeSourceCalendar;
	}
	
	@Override
	public String getType() {
		return INCLUDE_SOURCE_CALENDAR;
	}

	@Override
	public String getKey() {
		return INCLUDE_SOURCE_CALENDAR;
	}

	@Override
	public String getValue() {
		return Boolean.toString(includeSourceCalendar);
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Include Source Calendar";
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public final boolean participatesInFiltering() {
		return false;
	}

}
