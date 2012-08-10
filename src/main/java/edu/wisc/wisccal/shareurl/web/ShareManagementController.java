/**
 *
 */

package edu.wisc.wisccal.shareurl.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wisc.wisccal.shareurl.GuessableShareAlreadyExistsException;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;
import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;

/**
 * {@link Controller} for creating {@link Share}s.
 * 
 * @author Nicholas Blair
 */
@Controller
@RequestMapping("/rest")
public class ShareManagementController {

	private final Log log = LogFactory.getLog(this.getClass());
	private IShareDao shareDao;

	/**
	 * @return the shareDao
	 */
	public IShareDao getShareDao() {
		return shareDao;
	}
	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(IShareDao shareDao) {
		this.shareDao = shareDao;
	}

	/**
	 * Will invoke {@link IShareDao#generateGuessableShare(ICalendarAccount, SharePreferences)}
	 * for the authenticated user.
	 *  
	 * @param model
	 * @return the name of the json view
	 */
	@RequestMapping(value="/create-public",method=RequestMethod.POST )
	public String createPublic(ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling createPublic request for " + activeAccount);
		}
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new FreeBusyPreference());
		try {
			Share share = shareDao.generateGuessableShare(activeAccount, preferences);
			model.addAttribute("share", share);
			model.addAttribute("success", true);
		} catch (GuessableShareAlreadyExistsException e) {
			model.addAttribute("success", false);
			model.addAttribute("message", "Public ShareURL already exists.");
		}

		return "jsonView";
	}

	/**
	 * Will invoke {@link IShareDao#generateNewShare(ICalendarAccount, SharePreferences)}
	 * for the authenticated user.
	 * @param model
	 * @return the name of the json view
	 */
	@RequestMapping(value="/create-traditional",method=RequestMethod.POST )
	public String createTraditional(ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling createTraditional request for " + activeAccount);
		}
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new FreeBusyPreference());

		Share share = shareDao.generateNewShare(activeAccount, preferences);
		model.addAttribute("share", share);
		model.addAttribute("success", true);

		return "jsonView";
	}

}