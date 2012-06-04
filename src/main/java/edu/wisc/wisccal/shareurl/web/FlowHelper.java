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
package edu.wisc.wisccal.shareurl.web;

import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.RequestContext;

import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.AccessClassification;
import edu.wisc.wisccal.shareurl.domain.AccessClassificationMatchPreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.IncludeParticipantsPreference;
import edu.wisc.wisccal.shareurl.domain.PropertyMatchPreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;

/**
 * Integration point between Spring Web Flow and calendarkey.
 * Exposes shorthand methods for flows.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: FlowHelper.java 1731 2010-02-16 16:55:22Z npblair $
 */
@Service
public class FlowHelper {
	
	private IShareDao shareDao;

	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
	}
	
	/**
	 * 
	 * @param preferences
	 * @return
	 */
	public Share generateNewShare(SharePreferences preferences) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		return shareDao.generateNewShare(activeAccount, preferences);
	}
	
	/**
	 * 
	 * @param share
	 */
	public void revokeShare(Share share) {
		shareDao.revokeShare(share);
	}
	
	public ISharePreference newClassPublicPreference() {
		return new AccessClassificationMatchPreference(AccessClassification.PUBLIC);
	}
	
	public ISharePreference newClassConfidentialPreference() {
		return new AccessClassificationMatchPreference(AccessClassification.CONFIDENTIAL);
	}
	public ISharePreference newClassPrivatePreference() {
		return new AccessClassificationMatchPreference(AccessClassification.PRIVATE);
	}
	public ISharePreference newIncludeParticipantsPreference() {
		return new IncludeParticipantsPreference(true);
	}
	
	/**
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	public PropertyMatchPreference newPropertyMatchPreference(String propertyName, String propertyValue) {
		PropertyMatchPreference result = new PropertyMatchPreference(propertyName, propertyValue);
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @param context
	 * @return
	 */
	public boolean lookupShareForCurrentUserByKey(final String key, final RequestContext context) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		Share target = shareDao.retrieveByKey(key);
		if(null != target && target.getOwnerCalendarUniqueId().equals(activeAccount.getCalendarUniqueId())) {
			context.getFlowScope().put("share", target);
			return true;
		} else {
			return false;
		}
	}
}
