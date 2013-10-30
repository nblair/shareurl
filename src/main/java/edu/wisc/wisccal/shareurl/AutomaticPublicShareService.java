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

import org.jasig.schedassist.model.ICalendarAccount;

import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * This service encapsulates the logic for returning automatic
 * {@link Share}s for email addresses.
 * 
 * @author Nicholas Blair
 */
public interface AutomaticPublicShareService {

	/**
	 * Implementations should check the emailAddress for eligibility to return
	 * "automatic" Public Shares.
	 * Will return null if the address doesn't exist, or isn't eligible, or if the account
	 * has "opted out" of this feature.
	 * 
	 * Returned {@link Share}s should be Free/Busy only.
	 * 
	 * @param emailAddress the email address of the target account.
	 * @return a {@link Share}.
	 */
	Share getAutomaticPublicShare(String emailAddress);
	/**
	 * 
	 * @param calendarAccount
	 * @return the {@link AutomaticPublicShareEligibilityStatus} for the account
	 */
	AutomaticPublicShareEligibilityStatus getEligibilityStatus(ICalendarAccount calendarAccount);
	/**
	 * 
	 * @param calendarAccount
	 * @return true if {@link #optOut(ICalendarAccount)} has been called previously (and is still effective)
	 */
	boolean hasOptedOut(ICalendarAccount calendarAccount);
	/**
	 * Opt this account out; will result in {@link #getAutomaticPublicShare(String)} returning
	 * null for this account.
	 * 
	 * @param calendarAccount
	 */
	void optOut(ICalendarAccount calendarAccount);
	/**
	 * Opt this account back in; will result in {@link #getAutomaticPublicShare(String)} returning
	 * a {@link Share} for this account.
	 * 
	 * @param calendarAccount
	 */
	void optIn(ICalendarAccount calendarAccount);
}
