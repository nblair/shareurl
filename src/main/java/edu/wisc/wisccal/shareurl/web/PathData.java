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
package edu.wisc.wisccal.shareurl.web;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.model.CommonDateOperations;

/**
 * Java bean to represent data gleaned from the path of a share request.
 * 
 * @author Nicholas Blair
 */
class PathData {
	private String shareKey;
	private String eventId;
	private String recurrenceId;
	private String datePhrase;
	private Date startDate;
	private Date endDate;
	private int startDateIndex = 0;
	private int endDateIndex = 0;
	/**
	 * @return the shareKey
	 */
	public String getShareKey() {
		return shareKey;
	}
	/**
	 * @param shareKey the shareKey to set
	 */
	public void setShareKey(String shareKey) {
		this.shareKey = shareKey;
	}
	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	/**
	 * @return the datePhrase
	 */
	public String getDatePhrase() {
		return datePhrase;
	}
	/**
	 * @param datePhrase the datePhrase to set
	 */
	public void setDatePhrase(String datePhrase) {
		this.datePhrase = datePhrase;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		long diff = CommonDateOperations.approximateDifference(this.startDate, endDate);
		if(diff > ShareRequestDetails.MAX_RANGE) {
			this.endDate = CommonDateOperations.endOfDay(DateUtils.addDays(this.startDate, 180));
		} else {
			this.endDate = endDate;
		}
	}
	/**
	 * @return the startDateIndex
	 */
	public int getStartDateIndex() {
		return startDateIndex;
	}
	/**
	 * @param startDateIndex the startDateIndex to set
	 */
	public void setStartDateIndex(int startDateIndex) {
		this.startDateIndex = startDateIndex;
	}
	/**
	 * @return the endDateIndex
	 */
	public int getEndDateIndex() {
		return endDateIndex;
	}
	/**
	 * @param endDateIndex the endDateIndex to set
	 */
	public void setEndDateIndex(int endDateIndex) {
		this.endDateIndex = endDateIndex;
	}
	/**
	 * @return the recurrenceId
	 */
	public String getRecurrenceId() {
		return recurrenceId;
	}
	/**
	 * @param recurrenceId the recurrenceId to set
	 */
	public void setRecurrenceId(String recurrenceId) {
		this.recurrenceId = recurrenceId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datePhrase == null) ? 0 : datePhrase.hashCode());
		result = prime * result
				+ ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + endDateIndex;
		result = prime * result
				+ ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result
				+ ((recurrenceId == null) ? 0 : recurrenceId.hashCode());
		result = prime * result
				+ ((shareKey == null) ? 0 : shareKey.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + startDateIndex;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathData other = (PathData) obj;
		if (datePhrase == null) {
			if (other.datePhrase != null)
				return false;
		} else if (!datePhrase.equals(other.datePhrase))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (endDateIndex != other.endDateIndex)
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (recurrenceId == null) {
			if (other.recurrenceId != null)
				return false;
		} else if (!recurrenceId.equals(other.recurrenceId))
			return false;
		if (shareKey == null) {
			if (other.shareKey != null)
				return false;
		} else if (!shareKey.equals(other.shareKey))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (startDateIndex != other.startDateIndex)
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PathData [shareKey=" + shareKey + ", eventId=" + eventId
				+ ", recurrenceId=" + recurrenceId + ", datePhrase="
				+ datePhrase + ", startDate=" + startDate + ", endDate="
				+ endDate + ", startDateIndex=" + startDateIndex
				+ ", endDateIndex=" + endDateIndex + "]";
	}


}