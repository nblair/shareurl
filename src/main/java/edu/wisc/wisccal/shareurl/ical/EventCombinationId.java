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
package edu.wisc.wisccal.shareurl.ical;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RecurrenceId;

/**
 * Class to represnt the combination id for an event: UID and RECURRENCE-ID.
 * 
 * @author Nicholas Blair
 */
class EventCombinationId {
	
	private String uid;
	private String recurrenceId;
	/**
	 * 
	 * @param event
	 */
	protected EventCombinationId(VEvent event) {
		this.uid = event.getUid().getValue();
		RecurrenceId recurrenceId = event.getRecurrenceId();
		if(recurrenceId != null) {
			this.recurrenceId = event.getRecurrenceId().getValue();
		} 
	}
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
				+ ((recurrenceId == null) ? 0 : recurrenceId.hashCode());
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
		if (!(obj instanceof EventCombinationId)) {
			return false;
		}
		EventCombinationId other = (EventCombinationId) obj;
		if (recurrenceId == null) {
			if (other.recurrenceId != null) {
				return false;
			}
		} else if (!recurrenceId.equals(other.recurrenceId)) {
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
		builder.append("EventCombinationId [uid=");
		builder.append(uid);
		builder.append(", recurrenceId=");
		builder.append(recurrenceId);
		builder.append("]");
		return builder.toString();
	}
	
}