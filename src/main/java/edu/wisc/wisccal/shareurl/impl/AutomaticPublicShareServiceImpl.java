/**
 * 
 */

package edu.wisc.wisccal.shareurl.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.impl.ldap.HasDistinguishedName;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.stereotype.Service;

import edu.wisc.services.chub.soap.v1_4.PersonQuery;
import edu.wisc.services.chub.v1_4.CurricularDataService;
import edu.wisc.services.chub.v1_4.QueryLimitExceededException;
import edu.wisc.services.ebo.curricular.v1_4.Student;
import edu.wisc.wisccal.shareurl.AutomaticPublicShareEligibilityStatus;
import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.GuessableSharePreference;
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

	private Log log = LogFactory.getLog(this.getClass());
	private static final String Y = "Y";
	private IShareDao shareDao;
	private OptOutDao optOutDao;
	private ICalendarAccountDao calendarAccountDao;
	private CurricularDataService curricularDataService;
	private String pviAttributeName = "wiscedupvi";
	private String mailAttributeName = "mail";
	private String unsearchableAttributeName = "wisceducalunsearchable";
	private Name primaryWiscmailBaseDn;
	/**
	 * Default constructor, best for use with IoC.
	 */
	public AutomaticPublicShareServiceImpl() {
		this(null);
	}
	/**
	 * Allows preset of {@link #getPrimaryWiscmailBaseDn()}.
	 * @param primaryWiscmailBaseDn
	 */
	public AutomaticPublicShareServiceImpl(Name primaryWiscmailBaseDn) {
		this.primaryWiscmailBaseDn = primaryWiscmailBaseDn;
	}
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
	 * @return the curricularDataService
	 */
	public CurricularDataService getCurricularDataService() {
		return curricularDataService;
	}
	/**
	 * @param curricularDataService the curricularDataService to set
	 */
	@Autowired
	public void setCurricularDataService(CurricularDataService curricularDataService) {
		this.curricularDataService = curricularDataService;
	}
	/**
	 * @return the optOutDao
	 */
	public OptOutDao getOptOutDao() {
		return optOutDao;
	}
	/**
	 * @return the pviAttributeName
	 */
	public String getPviAttributeName() {
		return pviAttributeName;
	}
	/**
	 * @param pviAttributeName the pviAttributeName to set
	 */
	public void setPviAttributeName(String pviAttributeName) {
		this.pviAttributeName = pviAttributeName;
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
	/**
	 * @return the primaryWiscmailBaseDn
	 */
	public Name getPrimaryWiscmailBaseDn() {
		return primaryWiscmailBaseDn;
	}
	/**
	 * @param primaryWiscmailBaseDn the primaryWiscmailBaseDn to set
	 */
	@Value("${ldap.primaryWiscmailDomainBaseDn}")
	public void setPrimaryWiscmailBaseDn(String primaryWiscmailBaseDn) {
		this.primaryWiscmailBaseDn = new DistinguishedName(primaryWiscmailBaseDn);
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
			String calendarUniqueId = calendarAccount.getCalendarUniqueId();
			String upn = calendarAccount.getUpn();
			share.setValid(true);
			if(StringUtils.isNotBlank(calendarUniqueId)) {
				share.setOwnerCalendarUniqueId(calendarUniqueId);
			}else if(StringUtils.isNotBlank(upn)){				
				share.setOwnerCalendarUniqueId(upn);
			}else{
				share.setValid(false);
			}
				
			share.getSharePreferences().addPreference(new FreeBusyPreference());
			share.getSharePreferences().addPreference(new GuessableSharePreference());
			// mark to prevent revokeShare from working
			share.getSharePreferences().addPreference(new NonRevocablePreference());
			return share;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.AutomaticPublicShareService#getEligibilityStatus(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public AutomaticPublicShareEligibilityStatus getEligibilityStatus(
			ICalendarAccount calendarAccount) {
		if(calendarAccount == null || !calendarAccount.isEligible() ) {
			return AutomaticPublicShareEligibilityStatus.CALENDAR_INELIGIBLE;
		}
		
		if(isUnsearchable(calendarAccount)) {
			return AutomaticPublicShareEligibilityStatus.CALENDAR_UNSEARCHABLE;
		}
		
		if(hasOptedOut(calendarAccount)) {
			return AutomaticPublicShareEligibilityStatus.OPTED_OUT;
		}
		
		if(hasFerpaHold(calendarAccount)) {
			return AutomaticPublicShareEligibilityStatus.HAS_FERPA_HOLD;
		}
		
		return AutomaticPublicShareEligibilityStatus.ELIGIBLE;
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
		return isEligibleForAutomaticPublicShare(result) ? result : null;
	}
	/**
	 * Perform 3 eligibility checks:
	 * <ol>
	 * <li>Is the person eligible for calendar service? If not, return false.</li>
	 * <li>Is 
	 * <li>Has the person opted out? If so, return false.</li>
	 * <li>Does the person have a FERPA hold on their email address? If so, return false.</li>
	 * </ol>
	 * @param calendarAccount
	 * @return true if the account is eligible
	 */
	protected boolean isEligibleForAutomaticPublicShare(ICalendarAccount calendarAccount) {
		return calendarAccount != null && calendarAccount.isEligible() 
				&& !isUnsearchable(calendarAccount) && !hasOptedOut(calendarAccount)
				&& !hasFerpaHold(calendarAccount);
	}

	/**
	 * 
	 * @param calendarAccount must not be null
	 * @return true if the account is "unsearchable"
	 */
	protected boolean isUnsearchable(ICalendarAccount calendarAccount) {
		return Y.equalsIgnoreCase(calendarAccount.getAttributeValue(getUnsearchableAttributeName()));
	}

	/**
	 * @see CurricularDataService#getStudents(List)
	 * @param calendarAccount
	 * @return true if the account has a FERPA hold specifically on the email address attribute
	 */
	protected boolean hasFerpaHold(ICalendarAccount calendarAccount) {
		if(calendarAccount instanceof HasDistinguishedName) {
			final Name dn = ((HasDistinguishedName) calendarAccount).getDistinguishedName();
			if(dn.startsWith(primaryWiscmailBaseDn)) {
				String pvi = calendarAccount.getAttributeValue(getPviAttributeName());
				if(StringUtils.isBlank(pvi)) {
					throw new IllegalStateException(calendarAccount + " is within " + primaryWiscmailBaseDn + " but does not have a value for " + getPviAttributeName());
				}
				
				PersonQuery q = new PersonQuery();
				q.setPvi(pvi);
				List<PersonQuery> queries = Arrays.asList(new PersonQuery[] { q });
				try {
					List<Student> students = curricularDataService.getStudents(queries);
					if(students.size() == 1) {
						Student student = students.get(0);
						return student.getFerpaAttributes().isEmail();
					}
					
					return false;
				} catch (QueryLimitExceededException e) {
					throw new IllegalStateException("unexpected QueryLimitExceededException thrown in hasFerpaHold for " + calendarAccount, e);
				}
			} else {
				if(log.isDebugEnabled()) {
					log.debug("skipping PVI check for " + calendarAccount + " since it's outside " + primaryWiscmailBaseDn);
				}
				return false;
			}
		} else {
			// there is an ICalendarAccount implementation that does not implement HasDistinguishedName
			// IDelegateCalendarAccount, a.k.a "resources"
			// we don't have to check resource accounts for FERPA holds
			if(log.isDebugEnabled()) {
				log.debug("skipping hasFerpaHold check for " + calendarAccount + " since it is not a HasDistinguishedName (it's likely a resource)");
			}
			return false;
		}
	}
}
