/**
 * 
 */

package edu.wisc.wisccal.shareurl.impl;

import org.jasig.schedassist.model.ICalendarAccount;

import edu.wisc.wisccal.shareurl.AutomaticPublicShareService;

/**
 * Internal interface providing means to "opt out" (or opt back in) to participating
 * with the {@link AutomaticPublicShareService}.
 * 
 * @author Nicholas Blair
 */
interface OptOutDao {

	void optOut(ICalendarAccount account);
	
	void optIn(ICalendarAccount account);
	
	boolean isOptOut(ICalendarAccount account);
}
