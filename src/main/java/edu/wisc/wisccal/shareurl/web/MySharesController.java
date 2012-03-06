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

import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;

/**
 * Implementation of Controller to display Share information and provide management
 * links.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MySharesController.java 1663 2010-02-05 17:55:45Z npblair $
 */
@Controller
public class MySharesController  {

	public static final String LOGOUT_DELEGATE = "logoutDelegate";
	private Log LOG = LogFactory.getLog(this.getClass());
	private IShareDao shareDao;

	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
	}
	
	@RequestMapping("/my-shares")
	public String showView(ModelMap model, @RequestParam(value="logoutDelegate",required=false,defaultValue="false") boolean logoutDelegate) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(logoutDelegate) {
			//currentUser.clearActingOnBehalfOf();
		}
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(LOG.isDebugEnabled()) {
			LOG.debug("displaying manage share form for " + activeAccount);
		}
		List<Share> shares = shareDao.retrieveByOwner(activeAccount);
		boolean activeIsDelegate = currentUser.isDelegate();
		model.put("activeIsDelegate", activeIsDelegate);
		model.put("shares", shares);
		return "my-shares";
	}
}
