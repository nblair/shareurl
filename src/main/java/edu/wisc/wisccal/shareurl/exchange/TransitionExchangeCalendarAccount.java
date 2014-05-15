package edu.wisc.wisccal.shareurl.exchange;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.CalendarMatchPreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

@Component
public class TransitionExchangeCalendarAccount {

	@Autowired
	private IShareDao shareDao;
	
	@Autowired @Qualifier("compositeCalendarDataDao")
	private ICalendarDataDao calendarDataDao;
	
	@Autowired @Qualifier("composite")
	private ICalendarAccountDao calendarAccountDao;

	@Autowired
	private AutomaticPublicShareService automaticPublicShareService;

	protected final Log log = LogFactory.getLog(this.getClass());
	
	public boolean transitionShare(Share share) {
		List<CalendarMatchPreference> caldavPreferences = getCaldavPreferences(share);
		
		
		if(!CollectionUtils.isEmpty(caldavPreferences)) {
			log.info(caldavPreferences.size()+" CalDav preferences will be removed from Share="+share.getKey()+" . CalDavPreferences to remove: \n"+caldavPreferences);
			removeCaldavPreferences(share);
		}
		
		return shareIsTransitioned(share);
	}
	
	
	/**
	 * Step 1 - consolidate shares from proxy addresses to a single owner
	 * 
	 * Assuming owner.isExchange()...
	 * retrieve accounts by: search_ldap mail=wiscedumsoladddresses
	 * foreach account retrieve shares
	 * foreach share update share owner
	 * 
	 * 
	 */
	private void transitionProxyAccounts(ICalendarAccount owner) {
		if(owner.isExchange()) {
			List<String> proxyAddresses = owner.getProxyAddresses();
			log.info(owner.getCalendarUniqueId() +" has "+ proxyAddresses.size() +" proxy addresses: "+ proxyAddresses);
			for(String proxy: proxyAddresses) {
				if(proxy.equals(owner.getEmailAddress())) continue;
				ICalendarAccount proxyAccount = calendarAccountDao.getCalendarAccount("mail", proxy);
				if(null != proxyAccount) {
					if(owner.isExchange() && proxyAccount.isExchange()) {
						//find shares owned by proxy addresses
						List<Share> proxyShares = shareDao.retrieveByOwner(proxyAccount);
						
						if(!proxyShares.isEmpty()) {
							log.info("found "+ proxyShares.size()+ " for "+ proxy);
							for(Share proxyShare: proxyShares) {
								//change ownership of the share
								log.info("\t changing ownership of share["+proxyShare.getKey()+"] to new owner="+owner.getCalendarUniqueId());
								shareDao.updateShareOwner(proxyShare, owner);	
							}
						}else {
							log.info(proxy+" is a valid exchange account, but there are no shares to migrate");
						}
					}else {
						log.info(proxy+" is not an exchange account, but it might be a wisccal one, or it might not be done migrating");
					}
				}else {
					log.info(proxy+" is not a real account, just a proxy address. ");
				}
			}
		}else {
			log.warn("owner is not exchange: "+owner);
		}
	}
	
	/**
	 * Step 2 - shares owned by proxy addresses must be transitioned first
	 * 
	 * Assuming owner.isExchange()...
	 * Retrieves all shares for owner
	 * foreach share retrieve all share preferences
	 * -remove caldav preferences
	 * -remove preferences containing an invlaid calendar id
	 * 
	 */
	private Map<String,Boolean> transitionSharePrefs(ICalendarAccount calendarAccount) {
		
		Map<String,Boolean> results =  new HashMap<String, Boolean>();
		if(calendarAccount.isExchange()) {
			Map<String, String> calMap = calendarDataDao.listCalendars(calendarAccount);
			Set<String> validCalendarIds = calMap.keySet();
			
			List<Share> shares = shareDao.retrieveByOwner(calendarAccount);
			
			for(Share s: shares) {
				SharePreferences sharePreferences = s.getSharePreferences();
				
				List<CalendarMatchPreference> calendarMatchPreferences = new ArrayList<CalendarMatchPreference>();
				List<CalendarMatchPreference> caldavPrefs = new ArrayList<CalendarMatchPreference>();
				List<CalendarMatchPreference> exchangePrefs = new ArrayList<CalendarMatchPreference>();
				List<CalendarMatchPreference> invalidPrefs = new ArrayList<CalendarMatchPreference>();
				
				if(null != sharePreferences) {
					calendarMatchPreferences = sharePreferences.getCalendarMatchPreferences();
					
					for(CalendarMatchPreference pMatchPreference: calendarMatchPreferences) {
						String calendarId = pMatchPreference.getValue();
						if(validCalendarIds.contains(calendarId)) {
							if(!pMatchPreference.isExchange()) {
								caldavPrefs.add(pMatchPreference);
							}else {
								exchangePrefs.add(pMatchPreference);
							}
						}else {
							invalidPrefs.add(pMatchPreference);
						}
					}
				}
				
				log.info("Found "+ calendarMatchPreferences.size() +" calendarMatchPreferences for share="+s.getKey());
				if(CollectionUtils.isEmpty(invalidPrefs)) {
					//nothing to do here
				}else {
					log.info("\t "+ invalidPrefs.size() +" invalid calendarMatchPreferences will be removed");
				}
				if(CollectionUtils.isEmpty(exchangePrefs)) {
					//no need to do anything, calendarDataDao will obtain data from default exchange calendar when account.isExchange()
				}else {
					log.info("\t "+exchangePrefs.size() + " exchange calendarMatchPreferences will be retained");
				}
				if(CollectionUtils.isEmpty(caldavPrefs)) {
					log.info("\t no caldav calendarMatchPreferences found, nothing to do here");
				}else {
					log.info("\t "+caldavPrefs.size()+" caldav calendarMatchPreferences will be removed.");
					invalidPrefs.addAll(caldavPrefs);
				}
				
				for(ISharePreference preference: invalidPrefs) {
					log.info("\t\t removing sharePreference: "+preference);
					shareDao.removeSharePreference(s, preference);
				}
				results.put(s.getKey(), shareIsTransitioned(s));
			}
			
		}
		return results;
	}
	
	

	
	
	/** 
	 * verify share is valid.
	 * -retrieve share by key
	 * -retrieve owner
	 * -ensure owner isExchange
	 * -retrieve prefs for share
	 * ensure all calendarMatchPreferences are valid (they point at a calendar that exists in Exchange)
	 * 
	 * @param share
	 * @return
	 */
	public boolean shareIsTransitioned(Share share) {
		String key = share.getKey();
		if(StringUtils.isBlank(key)) {
			log.debug("share has no key: "+share);
			return false;
		}
		Share retrievedShare = shareDao.retrieveByKey(key);
		if(null == retrievedShare) {
			log.debug("failed to retrieve shareByKey:" + share);
			return false;
		}
		
		if(key.equals(retrievedShare.getKey())) {
			String ownerCalendarUniqueId = retrievedShare.getOwnerCalendarUniqueId();
			if(StringUtils.isNotBlank(ownerCalendarUniqueId)) {
				
				ICalendarAccount owner = calendarAccountDao.getCalendarAccountFromUniqueId(ownerCalendarUniqueId);
				
				if(null != owner && owner.isExchange()) {
				
					Map<String, String> calMap = calendarDataDao.listCalendars(owner);
					Set<String> calendarIds = calMap.keySet();
					List<CalendarMatchPreference> calendarMatchPreferences = retrievedShare.getSharePreferences().getCalendarMatchPreferences();
					
					for(CalendarMatchPreference preference: calendarMatchPreferences) {
						if(!calendarIds.contains(preference.getValue())) {
							log.debug("Preference contains an invalid calendarId: "+ preference );
							return false;
						}
						
						if(!preference.isExchange()) {
							log.debug("Preference contains a non-exchange calendarId:" + preference);
							return false;
						}
					}
					return true;
				}
			}else {
				log.error("share["+key+"] has no ownerUniqueId");
			}
		}else {
			log.debug("retrievedShare["+retrievedShare+"] does not match originalShare["+share+"]");
		}
		
		return false;
	}
	
	private List<CalendarMatchPreference> getCaldavPreferences(Share share){
		List<CalendarMatchPreference> caldavPrefs = new ArrayList<CalendarMatchPreference>();
		if(null != share) {
			SharePreferences sharePreferences = share.getSharePreferences();
			if(null != sharePreferences) {
				List<CalendarMatchPreference> calendarMatchPreferences = sharePreferences.getCalendarMatchPreferences();
				if(!CollectionUtils.isEmpty(calendarMatchPreferences)) {
					for(CalendarMatchPreference p: calendarMatchPreferences) {
						if(!p.isExchange()) {
							caldavPrefs.add(p);
						}
					}
				}
			}
		}
		return caldavPrefs;
	}
	
	public boolean removeCaldavPreferences(Share share) {
		List<CalendarMatchPreference> caldavPrefs = getCaldavPreferences(share);
		if(!CollectionUtils.isEmpty(caldavPrefs)) {
			for(CalendarMatchPreference p : caldavPrefs) {
				shareDao.removeSharePreference(share, p);
			}
		}
		return shareIsTransitioned(share);
	}

	
	public CalendarMatchPreference getDefaultCalendarPreference(ICalendarAccount calendarAccount) {
		CalendarMatchPreference preference = null;
		
		String defaultCalendarId = calendarDataDao.getDefaultCalendarId(calendarAccount);
		if(StringUtils.isNotBlank(defaultCalendarId)) {
			Map<String, String> calendarMap = calendarDataDao.listCalendars(calendarAccount);
			if(!CollectionUtils.isEmpty(calendarMap)) {
				if(calendarMap.containsKey(defaultCalendarId) && StringUtils.isNotBlank(calendarMap.get(defaultCalendarId))) {
					preference = new CalendarMatchPreference(calendarMap.get(defaultCalendarId), defaultCalendarId);
				}else {
					log.error("calendarMap does not contain defaultCalendarId["+defaultCalendarId+"] for ICalendarAccount="+calendarAccount);
				}
			}else {
				log.error("calendarMap empty for ICalendarAccount="+calendarAccount);
			}
		}else {
			log.error("defaultCalendarId not found for ICalendarAccount="+calendarAccount);
		}
		
		log.debug("ICalendarAccount="+calendarAccount+", has defaultCalendarPref="+preference);

		return preference;
	}
}
