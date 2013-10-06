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
package edu.wisc.wisccal.shareurl.web;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.impl.caldav.CaldavCalendarDataDao;
import org.jasig.schedassist.impl.exchange.ExchangeCalendarDataDao;
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

	private ExchangeCalendarDataDao exchangeCalendarDataDao;
	private CaldavCalendarDataDao caldavCalendarDataDao;
	
	public ExchangeCalendarDataDao getExchangeCalendarDataDao() {
		return exchangeCalendarDataDao;
	}
	
	@Autowired
	public void setExchangeCalendarDataDao(ExchangeCalendarDataDao exchangeCalendarDataDao) {
		this.exchangeCalendarDataDao = exchangeCalendarDataDao;
	}
	public CaldavCalendarDataDao getCaldavCalendarDataDao() {
		return caldavCalendarDataDao;
	}
	
	@Autowired
	public void setCaldavCalendarDataDao(CaldavCalendarDataDao caldavCalendarDataDao) {
		this.caldavCalendarDataDao = caldavCalendarDataDao;
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
		
		Map<String, String> exchangeListCalendars = exchangeCalendarDataDao.listCalendars(activeAccount);
		Map<String, String> caldavListCalendars = caldavCalendarDataDao.listCalendars(activeAccount);
		Map<String, String> allCalendarsList = new TreeMap<String, String>();
		allCalendarsList.putAll(caldavListCalendars);
		allCalendarsList.putAll(exchangeListCalendars);
		
		model.addAttribute("allCalendarList", allCalendarsList);
		
		String defaultCalendarId = getDefaultCalendarId(allCalendarsList);
		model.addAttribute("defaultCalendarId",defaultCalendarId);
		
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

	public String getDefaultCalendarId(Map<String,String> calendarMap){
		if(calendarMap.containsKey(ICalendarDataDao.DEFAULT_CALENDAR_PATH)) return  ICalendarDataDao.DEFAULT_CALENDAR_PATH;
		if(calendarMap.containsValue(ICalendarDataDao.DEFAULT_EXCHANGE_CALENDAR)){
			for(String key : calendarMap.keySet()){
				String name = calendarMap.get(key);
				if(name.equals(ICalendarDataDao.DEFAULT_EXCHANGE_CALENDAR)) return key;
			}
		}
		//log.warn("NO DEFAULT CALENDAR FOUND!!!");
		return null;
		
	}
}
