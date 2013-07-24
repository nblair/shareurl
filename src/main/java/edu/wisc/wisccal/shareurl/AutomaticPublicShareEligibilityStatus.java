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
/**
 * 
 */

package edu.wisc.wisccal.shareurl;

/**
 * Enum to represent a person's eligibility for automatic public share eligibility.
 * 
 * @author Nicholas Blair
 */
public enum AutomaticPublicShareEligibilityStatus {

	ELIGIBLE("N/A"),
	CALENDAR_INELIGIBLE("Public ShareURL has not been enabled for your account because it is ineligible for Calendar service."),
	CALENDAR_UNSEARCHABLE("Public ShareURL has not been enabled for your account because it has been hidden from 'search by CalDAV discovery.'"),
	OPTED_OUT("N/A"),
	HAS_FERPA_HOLD("Public ShareURL has not been enabled for your account because you have requested privacy protection for your email address through FERPA.");
	
	private String display;
	
	private AutomaticPublicShareEligibilityStatus(String display) {
		this.display = display;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isIneligibleFromExternalSource() {
		return this.equals(CALENDAR_INELIGIBLE) || this.equals(CALENDAR_UNSEARCHABLE) || this.equals(HAS_FERPA_HOLD);
	}
	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}
}
