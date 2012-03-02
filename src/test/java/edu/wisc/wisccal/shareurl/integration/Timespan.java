/*******************************************************************************
 *  Copyright 2008-2010 The Board of Regents of the University of Wisconsin System.
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
package edu.wisc.wisccal.shareurl.integration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/**
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: Timespan.java $
 */
public class Timespan {

	private final Date startDate;
	private final Date endDate;
	/**
	 * @param startDate
	 * @param endDate
	 */
	public Timespan(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Timespan createTimespan(String start, String end) {
		return createTimespan(start, end, "yyyyMMdd");
	}
	/**
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Timespan createTimespan(String start, String end, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			Date startDate = df.parse(start);
			Date endDate = df.parse(end);
		
			// set start to beginning of day
			startDate = DateUtils.truncate(startDate, Calendar.DATE);
			// set end to the end of the day (truncate, add 24 hours, subtract 1 second)
			endDate = DateUtils.truncate(endDate, Calendar.DATE);
			endDate = DateUtils.addDays(endDate, 1);
			endDate = DateUtils.addSeconds(endDate, -1);
			return new Timespan(startDate, endDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException("unable to parse " + start + ", end " + end + " with format " + format, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Timespan)) {
			return false;
		}
		Timespan other = (Timespan) obj;
		if (endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!endDate.equals(other.endDate)) {
			return false;
		}
		if (startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!startDate.equals(other.startDate)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Timespan [startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append("]");
		return builder.toString();
	}
}
