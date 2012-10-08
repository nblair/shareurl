/**
 * 
 */

package edu.wisc.wisccal.shareurl.impl;

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.NonRevocablePreference;
import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * Default {@link AutomaticPublicShareService} implementation.
 * 
 * @author Nicholas Blair
 */
@Service
public class AutomaticPublicShareServiceImpl implements
		AutomaticPublicShareService {

	private static final String Y = "Y";
	private IShareDao shareDao;
	private OptOutDao optOutDao;
	private ICalendarAccountDao calendarAccountDao;
	private String mailAttributeName = "mail";
	private String unsearchableAttributeName = "wisceducalunsearchable";
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
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
	 * @param optOutDao the optOutDao to set
	 */
	@Autowired
	public void setOptOutDao(OptOutDao optOutDao) {
		this.optOutDao = optOutDao;
	}
	/**
	 * @return the mailAttributeName
	 */
	public String getMailAttributeName() {
		return mailAttributeName;
	}
	/**
	 * @param mailAttributeName the mailAttributeName to set
	 */
	public void setMailAttributeName(String mailAttributeName) {
		this.mailAttributeName = mailAttributeName;
	}
	/**
	 * @return the unsearchableAttributeName
	 */
	public String getUnsearchableAttributeName() {
		return unsearchableAttributeName;
	}
	/**
	 * @param unsearchableAttributeName the unsearchableAttributeName to set
	 */
	public void setUnsearchableAttributeName(String unsearchableAttributeName) {
		this.unsearchableAttributeName = unsearchableAttributeName;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.AutomaticPublicShareService#getAutomaticPublicShare(java.lang.String)
	 */
	@Override
	public Share getAutomaticPublicShare(String emailAddress) {
		ICalendarAccount calendarAccount = locateEligibleAccountForEmailAddress(emailAddress);
		if(calendarAccount != null) {
			Share share = new Share();
			share.setKey(emailAddress);
			share.setOwnerCalendarUniqueId(calendarAccount.getCalendarUniqueId());
			share.setValid(true);
			share.getSharePreferences().addPreference(new FreeBusyPreference());
			// mark to prevent revokeShare from working
			share.getSharePreferences().addPreference(new NonRevocablePreference());
			return share;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.AutomaticPublicShareService#optOut(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void optOut(ICalendarAccount calendarAccount) {
		// 1. first revoke the guessable, if present
		Share guessable = shareDao.retrieveGuessableShare(calendarAccount);
		if(null != guessable) {
			shareDao.revokeShare(guessable);
		}
		// 2. persist the "opt out" flag
		optOutDao.optOut(calendarAccount);
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.AutomaticPublicShareService#optIn(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void optIn(ICalendarAccount calendarAccount) {
		optOutDao.optIn(calendarAccount);
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.AutomaticPublicShareService#hasOptedOut(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public boolean hasOptedOut(ICalendarAccount calendarAccount) {
		return optOutDao.isOptOut(calendarAccount);
	}
	/**
	 * 
	 * @param emailAddress
	 * @return
	 */
	protected ICalendarAccount locateEligibleAccountForEmailAddress(String emailAddress) {
		ICalendarAccount result = calendarAccountDao.getCalendarAccount(getMailAttributeName(), emailAddress);
		return isEligible(result) ? result : null;
	}
	/**
	 * Perform 3 eligibility checks:
	 * <ol>
	 * <li>Is the person eligible for calendar service? If not, return false.</li>
	 * <li>Has the person opted out? If so, return false.</li>
	 * <li>Does the person have a FERPA hold on their email address? If so, return false.</li>
	 * </ol>
	 * @param calendarAccount
	 * @return true if the account is eligible
	 */
	protected boolean isEligible(ICalendarAccount calendarAccount) {
		return calendarAccount != null && calendarAccount.isEligible() 
				&& !isUnsearchable(calendarAccount) && !hasFerpaHold(calendarAccount) 
				&& !hasOptedOut(calendarAccount);
	}
	
	/**
	 * 
	 * @param calendarAccount must not be null
	 * @return true if the account is "unsearchable"
	 */
	protected boolean isUnsearchable(ICalendarAccount calendarAccount) {
		return Y.equals(calendarAccount.getAttributeValue(getUnsearchableAttributeName()));
	}
	
	/**
	 * TODO implement hasFerpaHold
	 * 
	 * @param calendarAccount
	 * @return true if the account has a FERPA hold specifically on the email address attribute
	 */
	protected boolean hasFerpaHold(ICalendarAccount calendarAccount) {
		return false;
	}
}
