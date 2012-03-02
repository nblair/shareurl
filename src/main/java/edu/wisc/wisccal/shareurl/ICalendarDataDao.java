/*******************************************************************************
*  Copyright 2007-2010 The Board of Regents of the University of Wisconsin System.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*******************************************************************************/
package edu.wisc.wisccal.shareurl;

import java.util.Date;

import net.fortuna.ical4j.model.Calendar;

/**
 * Interface defines operations for retrieving calendar data 
 * for {@link ICalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ICalendarDataDao.java 3371 2011-09-30 21:15:09Z npblair $
 */
public interface ICalendarDataDao {

	/**
	 * Retrieve the calendar data for for the {@link ICalendarAccount} between
	 * the specified {@link Date}s as an iCal4j {@link Calendar}.
	 * 
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Calendar getCalendarData(ICalendarAccount account, Date startDate, Date endDate);
	
	/**
	 * Retrieve the "raw" calendar data for the {@link ICalendarAccount} between
	 * the specified {@link Date}s as a {@link String}.
	 * 
	 * There is no guarantee of the format of this data; implementers should document
	 * clearly and callers should not assume iCalendar is the result.
	 * 
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public String getRawCalendarData(ICalendarAccount account, Date startDate, Date endDate);

	/**
	 * 
	 * @param account
	 * @param startDate
	 * @param endDate
	 */
	public void clearCalendarCache(ICalendarAccount account, Date startDate, Date endDate);

}
