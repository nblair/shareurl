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

package edu.wisc.wisccal.shareurl.web;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.XProperty;
import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;
import edu.wisc.wisccal.shareurl.ical.EventParticipation;

/**
 * {@link VEvent} subclass to provide a handy reference to the {@link EventParticipation} for account
 * that contains the event.
 * 
 * @author Nicholas Blair
 */
public class VEventWithAccountEventParticipation extends VEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4317249225106683091L;
	private final EventParticipation accountEventParticipation;
	/**
	 * @param properties
	 * @param alarms
	 */
	public VEventWithAccountEventParticipation(PropertyList properties,
			ComponentList alarms, EventParticipation accountEventParticipation) {
		super(properties, alarms);
		this.accountEventParticipation = accountEventParticipation;
	}
	
	public VEventWithAccountEventParticipation(PropertyList properties,
			ComponentList alarms, EventParticipation accountEventParticipation, ShareRequestDetails requestDetails) {
		this(properties, alarms, accountEventParticipation);
		this.getProperties().add( new XProperty(CalendarDataUtils.X_SHAREURL_REQUEST_DETAILS, requestDetails.toString()));
	}
	
	/**
	 * @return the accountEventParticipation
	 */
	public EventParticipation getAccountEventParticipation() {
		return accountEventParticipation;
	}
	/**
	 * Helper method for JSTL.
	 * 
	 * @return true if this instance's {@link EventParticipation} equals {@link EventParticipation#ATTENDEE_ACCEPTED}
	 */
	public boolean isAcceptedAttendee() {
		return EventParticipation.ATTENDEE_ACCEPTED.equals(accountEventParticipation);
	}	
	/**
	 * Helper method for JSTL.
	 * 
	 * @return true if this instance's {@link EventParticipation} equals {@link EventParticipation#ATTENDEE_DECLINED}
	 */
	public boolean isDeclinedAttendee() {
		return EventParticipation.ATTENDEE_DECLINED.equals(accountEventParticipation);
	}
	/**
	 * Helper method for JSTL.
	 * 
	 * @return true if this instance's {@link EventParticipation} equals {@link EventParticipation#ATTENDEE_NEEDSACTION}
	 */
	public boolean isNeedsActionAttendee() {
		return EventParticipation.ATTENDEE_NEEDSACTION.equals(accountEventParticipation);
	}
	/**
	 *  Helper method for JSTL.
	 * 
	 * @return true if this instance's {@link EventParticipation} equals {@link EventParticipation#ATTENDEE_TENTATIVE}
	 */
	public boolean isTentativeAttendee() {
		return EventParticipation.ATTENDEE_TENTATIVE.equals(accountEventParticipation);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((accountEventParticipation == null) ? 0
						: accountEventParticipation.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VEventWithAccountEventParticipation other = (VEventWithAccountEventParticipation) obj;
		if (accountEventParticipation != other.accountEventParticipation)
			return false;
		return true;
	}
}
