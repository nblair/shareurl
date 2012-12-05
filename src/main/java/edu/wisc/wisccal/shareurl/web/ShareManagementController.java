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

import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
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
	private AutomaticPublicShareService automaticPublicShareService;

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
	 * @return the automaticPublicShareService
	 */
	public AutomaticPublicShareService getAutomaticPublicShareService() {
		return automaticPublicShareService;
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
	 * Will invoke {@link IShareDao#generateGuessableShare(ICalendarAccount, SharePreferences)}
	 * for the authenticated user.
	 * Includes a {@link FreeBusyPreference} by default.
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
		try {
			Share share = shareDao.generateGuessableShare(activeAccount);
			model.addAttribute("share", share);
			model.addAttribute("success", true);
		} catch (GuessableShareAlreadyExistsException e) {
			model.addAttribute("success", false);
			model.addAttribute("message", "Public ShareURL already exists.");
		}

		return "jsonView";
	}

	/**
	 * Will invoke {@link AutomaticPublicShareService#optOut(ICalendarAccount)} for the 
	 * active calendar account.
	 *  
	 * @param model
	 * @return the name of the json view
	 */
	@RequestMapping(value="/opt-out",method=RequestMethod.POST )
	public String optOutPublic(ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling optOutPublic request for " + activeAccount);
		}
		Share customized = shareDao.retrieveGuessableShare(activeAccount);
		if(customized != null) {
			shareDao.revokeShare(customized);
		}
		automaticPublicShareService.optOut(activeAccount);
		model.addAttribute("success", true);
		return "redirect:/my-shares";
	}
	/**
	 * Will invoke {@link AutomaticPublicShareService#optIn(ICalendarAccount)} for the 
	 * active calendar account.
	 *  
	 * @param model
	 * @return the name of the json view
	 */
	@RequestMapping(value="/opt-in",method=RequestMethod.POST )
	public String optInPublic(ModelMap model) {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ICalendarAccount activeAccount = currentUser.getCalendarAccount();
		if(log.isDebugEnabled()) {
			log.debug("handling optInPublic request for " + activeAccount);
		}
		automaticPublicShareService.optIn(activeAccount);
		model.addAttribute("success", true);
		return "redirect:/my-shares";
	}
	
	/**
	 * Will invoke {@link IShareDao#generateNewShare(ICalendarAccount, SharePreferences)}
	 * for the authenticated user.
	 * Includes a {@link FreeBusyPreference} by default.
	 * 
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
