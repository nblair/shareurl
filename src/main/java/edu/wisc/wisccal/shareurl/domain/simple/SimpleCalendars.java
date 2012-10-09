/**
 * 
 */
package edu.wisc.wisccal.shareurl.domain.simple;

import java.util.List;



/**
 * Interface for converting ical4j calendar data to the simplified
 * model in this package.
 * 
 * @author Nicholas Blair
 */
public interface SimpleCalendars {

	/**
	 * Convert the ical4j {@link net.fortuna.ical4j.model.Calendar} argument to the simplified representation.
	 * 
	 * @param calendar 
	 * @param removeParticipants if false, the result will not contain any {@link EventParticipant}s.
	 * @return the simplified representation
	 */
	Calendar simplify(net.fortuna.ical4j.model.Calendar calendar, boolean includeParticipants);
	
	/**
	 * 
	 * @param vevent
	 * @param removeParticipants
	 * @return
	 */
	Event convert(net.fortuna.ical4j.model.component.VEvent vevent, boolean includeParticipants);
	
	/**
	 * 
	 * @param vfreebusy
	 * @return
	 */
	List<FreeBusy> convert(net.fortuna.ical4j.model.component.VFreeBusy vfreebusy);
}
