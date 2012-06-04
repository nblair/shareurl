/**
 * 
 */

package edu.wisc.wisccal.shareurl.web;

import java.util.Iterator;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;

/**
 * @author Nicholas Blair
 */
public class ShareHelper {

	private static final Log LOG = LogFactory.getLog(ShareHelper.class);
	/**
	 * Mutative method to inspect all {@link VEvent} components in the agenda argument
	 * and remove those that have DTSTARTs that fall outside of the date range in the 
	 * {@link ShareRequestDetails}.
	 * 
	 * @param agenda
	 * @param requestDetails
	 */
	protected static void filterAgendaForDateRange(Calendar agenda, IShareRequestDetails requestDetails) {
		requestDetails.getStartDate();
		
		for(Iterator<?> i = agenda.getComponents().iterator(); i.hasNext() ;){
			Component c = (Component) i.next();
			if(VEvent.VEVENT.equals(c.getName())) {
				VEvent event = (VEvent) c;
				if(requestDetails.getStartDate().after(event.getStartDate().getDate()) || 
						requestDetails.getEndDate().before(event.getStartDate().getDate())) {
					LOG.debug("removing event " + CalendarDataUtils.nullSafeGetDebugId(event) + " since startdate falls outside of requestDetails window " + requestDetails);
					i.remove();
				}
			}
		}
	}
}
