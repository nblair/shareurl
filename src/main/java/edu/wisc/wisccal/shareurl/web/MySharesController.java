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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import edu.wisc.wisccal.shareurl.AutomaticPublicShareEligibilityStatus;
import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;

/**
 * Implementation of Controller to display Share information and provide management
 * links.
 * 
 * @author Nicholas Blair
 */
@Controller
public class MySharesController  {

	private Log LOG = LogFactory.getLog(this.getClass());
	private IShareDao shareDao;
	private AutomaticPublicShareService automaticPublicShareService;

	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
	}
	/**
	 * @param automaticPublicShareService the automaticPublicShareService to set
	 */
	@Autowired
	public void setAutomaticPublicShareService(
			AutomaticPublicShareService automaticPublicShareService) {
		this.automaticPublicShareService = automaticPublicShareService;
	}

	/**
	 * 
	 * @param model
	 * @param format
	 * @return
	 */
	@RequestMapping("/my-shares")
	public String showView(ModelMap model, @RequestParam(required=false, defaultValue="") String format) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(LOG.isDebugEnabled()) {
			LOG.debug("displaying manage share form for " + activeAccount + ", format=" + format);
		}
		model.put("optedOut", automaticPublicShareService.hasOptedOut(activeAccount));
		AutomaticPublicShareEligibilityStatus eligibilityStatus = automaticPublicShareService.getEligibilityStatus(activeAccount);
		if(eligibilityStatus.isIneligibleFromExternalSource()) {
			model.put("ineligibleStatus", eligibilityStatus);
		}
		List<Share> shares = shareDao.retrieveByOwner(activeAccount);
		boolean hasGuessable = false;
		boolean guessableModified = false;
		for(Share share: shares) {
			if(share.isGuessable()) {
				hasGuessable = true;
				if(!share.isFreeBusyOnly()) {
					guessableModified = true;
				}
				break;
			}
		}
		model.put("hasGuessable", hasGuessable);
		model.put("guessableModified", guessableModified);
		if(!hasGuessable && !guessableModified && eligibilityStatus.equals(AutomaticPublicShareEligibilityStatus.ELIGIBLE)) {
			// add the person's public share to the list of shares
			Share autoPublic = automaticPublicShareService.getAutomaticPublicShare(activeAccount.getEmailAddress());
			if(autoPublic != null) {
				shares.add(autoPublic);
			}
		}
		Collections.sort(shares, new MySharesDisplayComparator());
		model.put("shares", shares);
		
		boolean activeIsDelegate = currentUser.isDelegate();
		model.put("activeIsDelegate", activeIsDelegate);
		
		if("json".equals(format)) {
			return "jsonView";
		}
		return "my-shares";
	}
}
