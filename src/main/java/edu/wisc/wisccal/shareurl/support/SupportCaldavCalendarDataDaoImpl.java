/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
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

package edu.wisc.wisccal.shareurl.support;

import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;

import org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Simple subclass of {@link CaldavCalendarDataDaoImpl} to expose some of it's protected methods
 * to this package.
 * 
 * @author Nicholas Blair
 */
class SupportCaldavCalendarDataDaoImpl extends CaldavCalendarDataDaoImpl {

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl#getCalendarsInternal(org.jasig.schedassist.model.ICalendarAccount, java.util.Date, java.util.Date)
	 */
	@Override
	protected List<CalendarWithURI> getCalendarsInternal(
			ICalendarAccount calendarAccount, Date startDate, Date endDate) {
		return super.getCalendarsInternal(calendarAccount, startDate, endDate);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl#consolidate(java.util.List)
	 */
	@Override
	protected Calendar consolidate(List<CalendarWithURI> calendars) {
		return super.consolidate(calendars);
	}

	
}
