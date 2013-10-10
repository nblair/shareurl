/**
 * 
 */
package edu.wisc.wisccal.shareurl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;

import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;

import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * @author ctcudd
 *
 */
public interface IShareCalendarDataDao extends ICalendarDataDao {

	Calendar getCalendar(ICalendarAccount account, Share share, Date startDate, Date endDate);
	
	Map<String, String> listCalendars(ICalendarAccount owner);

	Calendar getCalDavCalendars(ICalendarAccount account, Date start, Date end);

	Calendar getCalDavCalendars(ICalendarAccount account, Date start, Date end,
			String accountUri);

	List<CalendarWithURI> peekAtAvailableScheduleReflections(
			IScheduleOwner owner1, Date start, Date end);

	Map<String, String> getCalendarMap(ICalendarAccount activeAccount);
	

}
