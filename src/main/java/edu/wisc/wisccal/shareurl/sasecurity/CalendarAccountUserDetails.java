package edu.wisc.wisccal.shareurl.sasecurity;

import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Extension of {@link UserDetails} to carry available-specific fields.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarAccountUserDetails.java 2306 2010-07-28 17:20:12Z npblair $
 */
public interface CalendarAccountUserDetails extends UserDetails {

	/**
	 * 
	 * @return a friendly display name for this account
	 */
	String getActiveDisplayName();
	
	/**
	 * 
	 * @return the {@link ICalendarAccount} for this account
	 */
	ICalendarAccount getCalendarAccount();
	
	/**
	 * 
	 * @return true if this account is a delegate
	 */
	boolean isDelegate();
}
