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

package edu.wisc.wisccal.shareurl.ical;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.FreeBusy;

import org.jasig.schedassist.model.ICalendarAccount;

import edu.wisc.wisccal.shareurl.domain.simple.EventParticipant;

/**
 * Interface defining the operations that execute the data formatting
 * and processing logic for ShareURL's features.
 * 
 * Implementations MUST be thread safe, as the intended use is as a singleton.
 * 
 * @author Nicholas Blair
 */
public interface CalendarDataProcessor {
	/**
	 * Convert the {@link VEvent}s within the {@link Calendar} argument to a
	 * single {@link VFreeBusy} object with {@link FreeBusy} properties
	 * corresponding with each event start/end time.
	 * 
	 * @param original must not be null
	 * @param start must not be null
	 * @param end must not be null
	 * @return a never null calendar that contains a freebusy representation of the original
	 */
	Calendar convertToFreeBusy(final Calendar original, final java.util.Date start, final java.util.Date end);
	
	/**
	 * Mutative method. Convert the {@link Clazz} property of all
	 * {@link VEvent} components in the calendar to {@link Clazz#PUBLIC}.
	 * @param original
	 */
	void convertClassPublic(final Calendar original);
	
	/**
	 * Calculate the recurrence set for the event across the start and end date arguments.
	 * 
	 * @param event
	 * @param startBoundary
	 * @param endBoundary
	 * @return a never null, but potentially empty, {@link PeriodList}
	 */
	PeriodList calculateRecurrence(VEvent event, java.util.Date startBoundary, java.util.Date endBoundary);
	
	/**
	 * {@link Component#copy()} can be very CPU/RAM expensive.
	 * This is a cheaper alternative, that constructs a new event and only copies over
	 * specific properties.
	 * All X-Properties are ignored, as well as recurrence related properties.
	 * 
	 * @param original
	 * @param period
	 * @param preserveParticipants if false, ORGANIZER and ATTENDEE properties are not copied
	 * @return a copy of the event, less some properties
	 */
	VEvent cheapRecurrenceCopy(VEvent original, Period period, boolean preserveParticipants);
	
	/**
	 * Mutative method.
	 * Remove all RDATE/RRULE/EXDATE/EXRULE properties from the event.
	 * 
	 * @param event
	 */
	void removeRecurrenceProperties(VEvent event);
	
	/**
	 * Method to return a String to help uniquely identity an event.
	 * If the event is not recurring, simply returns the value of the UID property.
	 * If recurring, the value returned is UID/RECURRENCE-ID.
	 * 
	 * Implementations must gracefully handles null situations.
	 * 
	 * @param component
	 * @return a string that uniquely identifies the event
	 */
	String getDebugId(Component component);
	
	/**
	 *  Mutative method.
	 * Implements the "breakRecurrence" algorithm:
	 * Scan each VEVENT in the calendar argument. If the event
	 * has a RECURRENCE-ID property, remove it and append it to the
	 * UID property.
	 * 
	 * @param original
	 */
	void breakRecurrence(final Calendar original);
	
	/**
	 * Mutative method.
	 * Implements the "no recurrence" algorithm.
	 * Expand recurrence for all events in the calendar and remove all recurrence properties.
	 * 
	 * @see PreferRecurrenceComponentComparator
	 * @param calendar
	 * @param start
	 * @param end
	 * @param preserveParticipants
	 */
	void noRecurrence(final Calendar calendar, java.util.Date start, java.util.Date end, boolean preserveParticipants);
	
	/**
	 * Mutative method.
	 * Leaves only events that the calendarAccount has {@link EventParticipation#ORGANIZER}.
	 * 
	 * @param calendar
	 * @param calendarAccount
	 */
	void organizerOnly(Calendar calendar, ICalendarAccount calendarAccount);
	
	/**
	 * Mutative method.
	 * Leaves only events that the calendarAccount has {@link EventParticipation#ATTENDEE}.
	 * 
	 * @param calendar
	 * @param calendarAccount
	 */
	void attendeeOnly(Calendar calendar, ICalendarAccount calendarAccount);
	
	/**
	 * Mutative method.
	 * Leaves only events that the calendarAccount has {@link EventParticipation#PERSONAL_EVENT}.
	 * 
	 * @param calendar
	 * @param calendarAccount
	 */
	void personalOnly(Calendar calendar, ICalendarAccount calendarAccount);
	
	/**
	 * Mutative method.
	 * Remove all ATTENDEE and ORGANIZER properties from the events in the calendar.
	 * 
	 * @param event
	 * @param account
	 */
	void removeParticipants(Calendar calendar, ICalendarAccount account);
	
	/**
	 * Convert the ical4j {@link Calendar} argument to the simplified representation.
	 * 
	 * @param calendar 
	 * @param removeParticipants if false, the result will not contain any {@link EventParticipant}s.
	 * @return the simplified representation
	 */
	edu.wisc.wisccal.shareurl.domain.simple.Calendar simplify(Calendar calendar, boolean includeParticipants);
}
