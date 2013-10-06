/**
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
