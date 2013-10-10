package edu.wisc.wisccal.shareurl.sasecurity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * {@link UserDetailsService} that returns {@link DelegateCalendarAccountUserDetailsImpl}
 * instances.
 * 
 * Requires that a valid {@link CalendarAccountUserDetailsImpl} be in the
 * current {@link SecurityContext}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateCalendarAccountUserDetailsServiceImpl.java 2045 2010-04-30 15:55:52Z npblair $
 */
public class DelegateCalendarAccountUserDetailsServiceImpl implements
		UserDetailsService {

	private static final String NONE_PROVIDED = "NONE_PROVIDED";
	private IDelegateCalendarAccountDao delegateCalendarAccountDao;
	protected final Log LOG = LogFactory.getLog(this.getClass());
	
	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired
	public void setDelegateCalendarAccountDao(
			IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException, DataAccessException {
		if(NONE_PROVIDED.equals(username)) {
			LOG.debug("caught NONE_PROVIDED being passed into loadUserByUsername");
			throw new UsernameNotFoundException(NONE_PROVIDED);
		}
		
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IDelegateCalendarAccount delegate = this.delegateCalendarAccountDao.getDelegate(username, currentUser.getCalendarAccount());
		if(null == delegate) {
			throw new UsernameNotFoundException("no delegate account found with name " + username);
		}
		DelegateCalendarAccountUserDetailsImpl result = new DelegateCalendarAccountUserDetailsImpl(delegate);
		return result;
	}

}
