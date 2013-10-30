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
package edu.wisc.wisccal.shareurl.domain.simple;

import java.util.Date;

import net.fortuna.ical4j.model.property.Transp;

import org.apache.commons.lang.time.DateUtils;

import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;


/**
 * Generic entry in a calendar.
 * Default {@link FreeBusyStatus} is BUSY.
 * 
 * @author Nicholas Blair
 */
public class CalendarEntry {

	private String uid;
	private Date startTime;
	private Date endTime;
	private String timezone;
	private String transparency;
	
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}
	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	/**
	 * @return the transparency
	 */
	public String getTransparency() {
		return transparency;
	}
	/**
	 * @param transparency the transparency to set
	 */
	public void setTransparency(String transparency) {
		this.transparency = transparency;
	}
	/**
	 * 
	 * @return FREE if transparent, BUSY otherwise
	 */
	public FreeBusyStatus getShowTimeAs() {
		if(Transp.TRANSPARENT.getValue().equals(getTransparency())) {
			return FreeBusyStatus.FREE;
		}
		return FreeBusyStatus.BUSY;
	}
	/**
	 * Calls {@link #setTransparency(String)} with the appropriate value.
	 * 
	 * @see CalendarEntry#setTransparency(String)
	 * @param freeBusyStatus
	 */
	public void setShowTimeAs(FreeBusyStatus freeBusyStatus) {
		if(FreeBusyStatus.FREE.equals(freeBusyStatus)) {
			setTransparency(Transp.TRANSPARENT.getValue());
		} else {
			setTransparency(Transp.OPAQUE.getValue());
		}
		
	}
	/**
	 * Use {@link #getShowTimeAs()} or {@link #getTransparency()} instead.
	 * 
	 * @return the status
	 */
	@Deprecated
	public FreeBusyStatus getStatus() {
		return getShowTimeAs();
	}

	/**
	 * Derived; if {@link #getStartTime()} and {@link #getEndTime()}
	 * return timestamps that equal the truncated self, this returns true.
	 * @return the allDay
	 */
	public boolean isAllDay() {
		Date start = getStartTime();
		Date end = getEndTime();
		assert(start != null && end != null);
		Date startTruncated = DateUtils.truncate(start, java.util.Calendar.DATE);
		Date endTruncated = DateUtils.truncate(end, java.util.Calendar.DATE);
		return start.equals(startTruncated) && end.equals(endTruncated);
	}
	/**
	 * Derived; return the difference between start and end times in minutes.
	 * @return
	 */
	public long getDurationInMinutes() {
		return CalendarDataUtils.approximateDifferenceInMinutes(getStartTime(), getEndTime());
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result
				+ ((timezone == null) ? 0 : timezone.hashCode());
		result = prime * result
				+ ((transparency == null) ? 0 : transparency.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
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
		if (!(obj instanceof CalendarEntry)) {
			return false;
		}
		CalendarEntry other = (CalendarEntry) obj;
		if (endTime == null) {
			if (other.endTime != null) {
				return false;
			}
		} else if (!endTime.equals(other.endTime)) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		if (timezone == null) {
			if (other.timezone != null) {
				return false;
			}
		} else if (!timezone.equals(other.timezone)) {
			return false;
		}
		if (transparency == null) {
			if (other.transparency != null) {
				return false;
			}
		} else if (!transparency.equals(other.transparency)) {
			return false;
		}
		if (uid == null) {
			if (other.uid != null) {
				return false;
			}
		} else if (!uid.equals(other.uid)) {
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
		builder.append("CalendarEntry [uid=");
		builder.append(uid);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", timezone=");
		builder.append(timezone);
		builder.append(", transparency=");
		builder.append(transparency);
		builder.append("]");
		return builder.toString();
	}
	
}
