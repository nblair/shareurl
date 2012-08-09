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
package edu.wisc.wisccal.shareurl.ical;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.stereotype.Service;

import edu.wisc.wisccal.shareurl.domain.simple.Event;
import edu.wisc.wisccal.shareurl.domain.simple.FreeBusyStatus;

/**
 * Helper methods for iCalendar data.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarDataUtils.java 1691 2010-02-10 18:58:13Z npblair $
 */
@Service
public final class CalendarDataUtils implements CalendarDataProcessor {

	protected static final String X_SHAREURL_RECURRENCE_EXPAND = "X-SHAREURL-RECURRENCE-EXPAND";

	protected static final String X_SHAREURL_OLD_RECURRENCE_ID = "X-SHAREURL-OLD-RECUR-ID";

	public static final long MILLISECS_PER_MINUTE = 60*1000;

	private static final String UW_SEPARATOR = "_UW_";

	public static final String SHAREURL_PROD_ID = "-//ShareURL//WiscCal//EN";

	private static final Log LOG = LogFactory.getLog(CalendarDataUtils.class);
	
	protected final Set<String> retainedPropertyNamesOnStripDetails = new HashSet<String>(
			Arrays.asList(new String[] { Uid.UID, DtStart.DTSTART, DtEnd.DTEND, DtStamp.DTSTAMP, 
					RecurrenceId.RECURRENCE_ID, Status.STATUS, Clazz.CLASS, Created.CREATED, LastModified.LAST_MODIFIED,
					X_SHAREURL_OLD_RECURRENCE_ID, X_SHAREURL_RECURRENCE_EXPAND }));
	/**
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 * @throws InvalidPropertyValueException 
	 */
	public static Property constructProperty(String propertyName, String propertyValue) throws InvalidPropertyValueException {
		PropertyFactory factory = PropertyFactoryImpl.getInstance();
		Property property = factory.createProperty(propertyName);
		try {
			property.setValue(propertyValue);
			return property;
		} catch (IOException e) {
			throw new InvalidPropertyValueException("invalid value (" + propertyValue + " for " + propertyName, e);
		} catch (URISyntaxException e) {
			throw new InvalidPropertyValueException("invalid value (" + propertyValue + " for " + propertyName, e);
		} catch (ParseException e) {
			throw new InvalidPropertyValueException("invalid value (" + propertyValue + " for " + propertyName, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#convertToFreeBusy(net.fortuna.ical4j.model.Calendar, java.util.Date, java.util.Date)
	 */
	@Override
	public Calendar convertToFreeBusy(final Calendar original, final java.util.Date start, final java.util.Date end) {
		ComponentList resultComponents = new ComponentList();
		VFreeBusy vFreeBusy = new VFreeBusy();
		// add dtstart from arguments
		vFreeBusy.getProperties().add(new DtStart(new DateTime(start)));
		// add dtend from arguments
		vFreeBusy.getProperties().add(new DtEnd(new DateTime(end)));

		// for each original event, add a FreeBUSY 
		ComponentList originalEvents = original.getComponents();

		for(Object o : originalEvents) {
			Component component = (Component) o;
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent e = (VEvent) component;
				if(isDayEvent(e)) {
					// skip day events in free busy
					continue;
				}

				if(Transp.TRANSPARENT.equals(e.getTransparency())) {
					// skip transparent events in free busy
					continue;
				}
				PeriodList freeBusyPeriodList = new PeriodList();
				Period eventPeriod = new Period(new DateTime(e.getStartDate().getDate()), 
						new DateTime(e.getEndDate(true).getDate()));
				freeBusyPeriodList.add(eventPeriod);

				FreeBusy freeBusy = new FreeBusy(freeBusyPeriodList); 
				vFreeBusy.getProperties().add(freeBusy);
			}
		}

		resultComponents.add(vFreeBusy);
		Calendar result = new Calendar(resultComponents);
		result.getProperties().add(Version.VERSION_2_0);
		result.getProperties().add(new ProdId(SHAREURL_PROD_ID));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#stripEventDetails(net.fortuna.ical4j.model.Calendar)
	 */
	@Override
	public void stripEventDetails(final Calendar original) {
		for(Iterator<?> i = original.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				for(Iterator<?> j = component.getProperties().iterator(); j.hasNext();) {
					Property property = (Property) j.next();
					if(!retainedPropertyNamesOnStripDetails.contains(property.getName())){
						j.remove();
					}
				}
				component.getProperties().add(new Summary("Busy"));
			}
		}
	}

	/**
	 * 
	 * @param calendar
	 * @param eventUid
	 * @return the {@link VEvent} within the calendar argument that has a matching UID
	 */
	public static VEvent getSingleEvent(final Calendar calendar, final String eventUid) {
		if(eventUid == null) return null;
		for (Iterator<?> i = calendar.getComponents(VEvent.VEVENT).iterator(); i.hasNext();) {
			VEvent component = (VEvent) i.next();
			if(eventUid.equals(component.getUid().getValue())) {
				return component;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#convertClassPublic(net.fortuna.ical4j.model.Calendar)
	 */
	@Override
	public void convertClassPublic(final Calendar original) {
		for (Iterator<?> i = original.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				Clazz classProperty = event.getClassification();
				if(!Clazz.PUBLIC.equals(classProperty)) {
					event.getProperties().remove(classProperty);
					event.getProperties().add(Clazz.PUBLIC);
				}
			}
		}
	}

	/**
	 * @param event
	 * @return true event is an all day event
	 */
	public static boolean isDayEvent(VEvent event) {
		return Value.DATE.equals(event.getStartDate().getParameter(Value.VALUE));
	}

	/**
	 * @param event
	 * @return true if the event has {@link Status#VEVENT_CANCELLED}.
	 */
	public static boolean isCancelled(VEvent event) {
		return Status.VEVENT_CANCELLED.equals(event.getStatus());
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	public static boolean isEventRecurring(VEvent event) {
		return event.getProperties(RDate.RDATE).size() > 0 || event.getProperties(RRule.RRULE).size() > 0 
				|| event.getProperties(X_SHAREURL_OLD_RECURRENCE_ID).size() > 0 || event.getProperties(X_SHAREURL_RECURRENCE_EXPAND).size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#calculateRecurrence(net.fortuna.ical4j.model.component.VEvent, java.util.Date, java.util.Date)
	 */
	@Override
	public PeriodList calculateRecurrence(VEvent event,
			Date startBoundary, Date endBoundary) {
		Period period = new Period(new DateTime(startBoundary), new DateTime(endBoundary));
		PeriodList periodList = event.calculateRecurrenceSet(period);
		return periodList;
	}

	/**
	 * Clone an event, using the {@link Period} argument for new start and end times.
	 * Returned event will include a {@link RecurrenceId} property.
	 * 
	 * @param original
	 * @param period
	 * @return
	 */
	public VEvent constructRecurrenceInstance(VEvent original, Period period) {
		try {
			VEvent copy = (VEvent) original.copy();
			copy.getStartDate().setDate(period.getStart());
			copy.getEndDate().setDate(period.getEnd());
			copy.getProperties().add(new RecurrenceId(period.getStart()));
			removeRecurrenceProperties(copy);
			return copy;
		} catch (ParseException e) {
			LOG.warn("caught ParseException attempting to copy " + original, e);
			return null;
		} catch (IOException e) {
			LOG.warn("caught IOException attempting to copy " + original, e);
			return null;
		} catch (URISyntaxException e) {
			LOG.warn("caught URISyntaxException attempting to copy " + original, e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#cheapRecurrenceCopy(net.fortuna.ical4j.model.component.VEvent, net.fortuna.ical4j.model.Period, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public VEvent cheapRecurrenceCopy(VEvent original, Period period, boolean preserveParticipants, boolean setRecurrenceId) {
		VEvent copy = new VEvent();
		copy.getProperties().add(new DtStart(period.getStart()));
		copy.getProperties().add(new DtEnd(period.getEnd()));
		copy.getProperties().add(new XProperty(X_SHAREURL_RECURRENCE_EXPAND, period.toString()));
		if(setRecurrenceId) {
			copy.getProperties().add(propertyCopy(original.getUid()));
			copy.getProperties().add(new RecurrenceId(period.getStart()));
		} else {
			// UID must be unique!
			StringBuilder uid = new StringBuilder();
			uid.append(original.getUid().getValue());
			uid.append(UW_SEPARATOR);
			uid.append(period.getStart().toString());
			copy.getProperties().add(new Uid(uid.toString()));
		}
		
		if(original.getSummary() != null) {
			copy.getProperties().add(propertyCopy(original.getSummary()));
		}
		if(original.getLocation() != null) {
			copy.getProperties().add(propertyCopy(original.getLocation()));
		}
		if(original.getDescription() != null) {
			copy.getProperties().add(propertyCopy(original.getDescription()));
		}
		if(original.getStatus() != null) {
			copy.getProperties().add(propertyCopy(original.getStatus()));
		}
		if(original.getClassification() != null) {
			copy.getProperties().add(propertyCopy(original.getClassification()));
		}
		if(preserveParticipants) {
			if(original.getOrganizer() != null) {
				copy.getProperties().add(propertyCopy(original.getOrganizer()));
			}
			copy.getProperties().addAll(original.getProperties(Attendee.ATTENDEE));
		}
		return copy;
	}
	
	/**
	 * Copy the value of the UID property from the original (argument 2) to the "copy" (argument 1).
	 * If the original didn't have a UID, make one.
	 * @param copy
	 * @param original
	 */
	protected void copyOrGenerateUid(VEvent copy, VEvent original) {
		if(original.getUid() != null) {
			copy.getProperties().add(propertyCopy(original.getUid()));
		}
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	protected static Property propertyCopy(Property property) {
		try {
			return property.copy();
		} catch (IOException e) {
			LOG.error("failed to copy property " + property, e);
			throw new IllegalArgumentException("failed to copy property " + property, e);
		} catch (URISyntaxException e) {
			LOG.error("failed to copy property " + property, e);
			throw new IllegalArgumentException("failed to copy property " + property, e);
		} catch (ParseException e) {
			LOG.error("failed to copy property " + property, e);
			throw new IllegalArgumentException("failed to copy property " + property, e);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#removeRecurrenceProperties(net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void removeRecurrenceProperties(VEvent event) {
		event.getProperties().removeAll(event.getProperties(RDate.RDATE));
		event.getProperties().removeAll(event.getProperties(RDate.RRULE));
		event.getProperties().removeAll(event.getProperties(RDate.EXDATE));
		event.getProperties().removeAll(event.getProperties(RDate.EXRULE));
		event.getProperties().removeAll(event.getProperties(RecurrenceId.RECURRENCE_ID));
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#removeParticipants(net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void removeParticipants(Calendar calendar, ICalendarAccount account) {
		for(Iterator<?> i = calendar.getComponents(VEvent.VEVENT).iterator(); i.hasNext();) {
			VEvent event = (VEvent) i.next();
			removeParticipants(event);
			event.getAlarms().clear();
		}
	}

	/**
	 * Remove all ATTENDEE and ORGANIZER properties from the component.
	 * 
	 * @param component
	 */
	@SuppressWarnings("unchecked")
	protected void removeParticipants(Component component) {
		if(component == null) {
			return;
		}
		component.getProperties().removeAll(component.getProperties(Attendee.ATTENDEE));
		component.getProperties().removeAll(component.getProperties(Organizer.ORGANIZER));
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#getDebugId(net.fortuna.ical4j.model.Component)
	 */
	@Override
	public String getDebugId(Component component) {
		return staticGetDebugId(component);
	}

	/**
	 * Statically callable implementation of {@link #getDebugId(Component)}.
	 * 
	 * @see #getDebugId(Component)
	 * @param component
	 * @return
	 */
	public static String staticGetDebugId(Component component) {
		if(component == null) {
			return "N/A";
		}
		Property uid = component.getProperty(Uid.UID);
		String uidValue = uid != null ? uid.getValue() : "N/A";
		Property recurrenceId = component.getProperty(RecurrenceId.RECURRENCE_ID);
		if(recurrenceId == null) {
			return uidValue;
		}
		return uidValue + "/" +recurrenceId.getValue();
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#breakRecurrence(net.fortuna.ical4j.model.Calendar)
	 */
	@Override
	public void breakRecurrence(final Calendar original) {
		for (Iterator<?> i = original.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				convertToCombinationUid(event);
			}  
		}
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#expandRecurrence(net.fortuna.ical4j.model.Calendar, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void expandRecurrence(Calendar calendar, Date start, Date end, boolean preserveParticipants) {
		Collections.sort(calendar.getComponents(), new PreferRecurrenceComponentComparator());

		Map<EventCombinationId, VEvent> eventMap = new HashMap<EventCombinationId, VEvent>();

		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName()) ){
				VEvent event = (VEvent) component;
				if(CalendarDataUtils.isEventRecurring(event)) {
					PeriodList recurringPeriods = calculateRecurrence(event, start, end);
					for(Object o: recurringPeriods) {
						Period period = (Period) o;
						VEvent recurrenceInstance = cheapRecurrenceCopy(event, period, preserveParticipants, true);
						EventCombinationId comboId = new EventCombinationId(recurrenceInstance);
						eventMap.put(comboId, recurrenceInstance);
					}
					// remove the "parent" event" now that we have individual recurrence instances
					if(!recurringPeriods.isEmpty()) {
						i.remove();
					}
				} else if (event.getProperty(RecurrenceId.RECURRENCE_ID) != null) {
					EventCombinationId comboId = new EventCombinationId(event);
					eventMap.put(comboId, event);
					i.remove();
				}
			}
		}

		if(!eventMap.values().isEmpty()) {
			calendar.getComponents().addAll(eventMap.values());
			// sort once more to shift timezones to the bottom
			Collections.sort(calendar.getComponents(), new Comparator<Component>() {
				@Override
				public int compare(Component o1, Component o2) {
					return new CompareToBuilder().append(o1.getName(), o2.getName()).toComparison();
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#noRecurrence(net.fortuna.ical4j.model.Calendar, java.util.Date, java.util.Date, boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void noRecurrence(final Calendar calendar, Date start, Date end, boolean preserveParticipants) {
		Collections.sort(calendar.getComponents(), new PreferRecurrenceComponentComparator());

		Map<EventCombinationId, VEvent> eventMap = new HashMap<EventCombinationId, VEvent>();

		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName()) ){
				VEvent event = (VEvent) component;
				if(CalendarDataUtils.isEventRecurring(event)) {
					PeriodList recurringPeriods = calculateRecurrence(event, start, end);
					for(Object o: recurringPeriods) {
						Period period = (Period) o;
						VEvent recurrenceInstance = cheapRecurrenceCopy(event, period, preserveParticipants, false);
						EventCombinationId comboId = new EventCombinationId(recurrenceInstance);
						eventMap.put(comboId, recurrenceInstance);
						CalendarDataUtils.convertToCombinationUid(recurrenceInstance);
					}
					// remove the "parent" event" now that we have individual recurrence instances
					if(!recurringPeriods.isEmpty()) {
						i.remove();
					}
				} else if (event.getProperty(RecurrenceId.RECURRENCE_ID) != null) {
					EventCombinationId comboId = new EventCombinationId(event);
					eventMap.put(comboId, event);

					CalendarDataUtils.convertToCombinationUid(event);
					i.remove();
				}


			}
		}

		if(!eventMap.values().isEmpty()) {
			calendar.getComponents().addAll(eventMap.values());
			// sort once more to shift timezones to the bottom
			Collections.sort(calendar.getComponents(), new Comparator<Component>() {
				@Override
				public int compare(Component o1, Component o2) {
					return new CompareToBuilder().append(o1.getName(), o2.getName()).toComparison();
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#organizerOnly(net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void organizerOnly(Calendar calendar, ICalendarAccount calendarAccount) {
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				EventParticipation participation = getEventParticipation(event, calendarAccount);
				if(!participation.equals(EventParticipation.ORGANIZER)) {
					i.remove();
					if(LOG.isDebugEnabled()) {
						LOG.debug("organizerOnly: removed " + getDebugId(component) + ", " + participation + " from agenda for " + calendarAccount);
					}
				}
			}
		}
	}
	EventParticipation getEventParticipation(VEvent event, ICalendarAccount calendarAccount) {
		Organizer organizer = event.getOrganizer();
		PropertyList attendees = event.getProperties(Attendee.ATTENDEE);
		if(null == organizer && attendees.size() == 0) {
			return EventParticipation.PERSONAL_EVENT;
		}

		if(!organizer.getValue().equalsIgnoreCase(mailto(calendarAccount.getEmailAddress()))) {
			return EventParticipation.ORGANIZER;
		}

		for(Object o: attendees) {
			Attendee attendee = (Attendee) o;
			if(attendee.getValue().equals(mailto(calendarAccount.getEmailAddress()))) {
				return EventParticipation.ATTENDEE;
			}
		}

		return EventParticipation.NOT_INVOLVED;
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#attendeeOnly(net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void attendeeOnly(Calendar calendar, ICalendarAccount calendarAccount) {
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				EventParticipation participation = getEventParticipation(event, calendarAccount);
				if(!participation.equals(EventParticipation.ATTENDEE)) {
					i.remove();
					if(LOG.isDebugEnabled()) {
						LOG.debug("attendeeOnly: removed " + getDebugId(component) + ", " + participation + " from agenda for " + calendarAccount);
					}
				}
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#personalOnly(net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void personalOnly(Calendar calendar, ICalendarAccount calendarAccount) {
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				EventParticipation participation = getEventParticipation(event, calendarAccount);
				if(participation.equals(EventParticipation.PERSONAL_EVENT)) {
					i.remove();
					if(LOG.isDebugEnabled()) {
						LOG.debug("personalOnly: removed " + getDebugId(component) + ", " + participation + " from agenda for " + calendarAccount);
					}
				}
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#simplify(net.fortuna.ical4j.model.Calendar, boolean)
	 */
	@Override
	public edu.wisc.wisccal.shareurl.domain.simple.Calendar simplify(
			Calendar calendar, boolean includeParticipants) {
		if(calendar == null) {
			return null;
		}
		edu.wisc.wisccal.shareurl.domain.simple.Calendar result = new edu.wisc.wisccal.shareurl.domain.simple.Calendar();
		result.setProductId(SHAREURL_PROD_ID);
		result.setVersion(nullSafePropertyValue(calendar.getVersion()));
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				Event event = convert((VEvent) component, includeParticipants);
				result.getEntries().add(event);
			} else if (VFreeBusy.VFREEBUSY.equals(component.getName())) {
				List<edu.wisc.wisccal.shareurl.domain.simple.FreeBusy> freeBusy = convert((VFreeBusy) component);
				result.getEntries().addAll(freeBusy);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param vfreebusy
	 * @return
	 */
	public List<edu.wisc.wisccal.shareurl.domain.simple.FreeBusy> convert(VFreeBusy vfreebusy) {
		List<edu.wisc.wisccal.shareurl.domain.simple.FreeBusy> result = new ArrayList<edu.wisc.wisccal.shareurl.domain.simple.FreeBusy>();
		String uid = nullSafePropertyValue(vfreebusy.getUid());
		PropertyList freebusy = vfreebusy.getProperties(FreeBusy.FREEBUSY);
		for(Object o : freebusy) {
			FreeBusy fb = (FreeBusy) o;
			FreeBusyStatus status = FreeBusyStatus.BUSY;
			FbType type = (FbType) fb.getParameter(FbType.FBTYPE);
			if(type != null && type.getValue().startsWith("FREE")) {
				status = FreeBusyStatus.FREE;
			}
			PeriodList periods = fb.getPeriods();
			for(Object p: periods) {
				Period period = (Period) p;
				edu.wisc.wisccal.shareurl.domain.simple.FreeBusy instance = new edu.wisc.wisccal.shareurl.domain.simple.FreeBusy();
				instance.setStartTime(period.getRangeStart());
				instance.setEndTime(period.getRangeEnd());
				instance.setStatus(status);
				instance.setUid(uid);
				result.add(instance);
			}
		}
		return result;
	}
	/**
	 * 
	 * @param vevent
	 * @param removeParticipants
	 * @return
	 */
	public Event convert(VEvent vevent, boolean includeParticipants) {
		Event event = new Event();
		event.setUid(nullSafePropertyValue(vevent.getUid()));
		event.setStartTime(vevent.getStartDate().getDate());
		event.setEndTime(vevent.getEndDate(true).getDate());
		event.setTimezone(nullSafeParameterValue(vevent.getStartDate().getParameter(TzId.TZID)));
		event.setSummary(nullSafePropertyValue(vevent.getSummary()));
		event.setDescription(nullSafePropertyValue(vevent.getDescription()));
		Clazz clazz = vevent.getClassification();
		if(clazz == null) {
			clazz = Clazz.PUBLIC;
		}
		event.setPrivacy(nullSafePropertyValue(clazz));
		event.setLocation(nullSafePropertyValue(vevent.getLocation()));
		if(includeParticipants) {
			net.fortuna.ical4j.model.property.Organizer organizer = vevent.getOrganizer();
			if(organizer != null) {
				edu.wisc.wisccal.shareurl.domain.simple.Organizer o = new edu.wisc.wisccal.shareurl.domain.simple.Organizer();
				o.setDisplayName(nullSafeParameterValue(organizer.getParameter(Cn.CN)));
				o.setEmailAddress(removeMailto(organizer.getValue()));
				o.setDesignateOrganizer(nullSafeParameterValue(organizer.getParameter(SentBy.SENT_BY)));
				event.setOrganizer(o);
			}
			PropertyList attendees = vevent.getProperties(Attendee.ATTENDEE);
			for(Object o: attendees) {
				Attendee attendee = (Attendee) o;

				edu.wisc.wisccal.shareurl.domain.simple.Attendee a = new edu.wisc.wisccal.shareurl.domain.simple.Attendee();
				a.setDisplayName(nullSafeParameterValue(attendee.getParameter(Cn.CN)));
				a.setEmailAddress(removeMailto(attendee.getValue()));
				a.setParticipationStatus(nullSafeParameterValue(attendee.getParameter(PartStat.PARTSTAT)));

				event.getAttendees().add(a);
			}
		}
		event.setRecurrenceId(nullSafePropertyValue(vevent.getRecurrenceId()));
		if(event.getRecurrenceId() == null) {
			// try the X-UW version
			event.setRecurrenceId(nullSafePropertyValue(vevent.getProperty(X_SHAREURL_OLD_RECURRENCE_ID)));
			if(event.getRecurrenceId() != null) {
				// restore original UID
				event.setUid(StringUtils.substringBefore(event.getUid(), UW_SEPARATOR));
			}
		}
		event.setRecurring(isEventRecurring(vevent));
		if(Transp.TRANSPARENT.equals(vevent.getProperty(Transp.TRANSP))) {
			event.setStatus(FreeBusyStatus.FREE);
		}
		return event;
	}

	String nullSafePropertyValue(Property property) {
		if(property == null) {
			return null;
		}

		return property.getValue();
	}
	String nullSafeParameterValue(Parameter parameter) {
		if(parameter == null) { 
			return null;
		}

		return parameter.getValue();
	}
	/**
	 * 
	 * @param event
	 */
	public static void convertToCombinationUid(VEvent event) {
		RecurrenceId recurrenceId = event.getRecurrenceId();
		if(recurrenceId != null) {
			StringBuilder newUid = new StringBuilder();
			newUid.append(event.getUid().getValue());
			newUid.append(UW_SEPARATOR);
			newUid.append(event.getRecurrenceId().getValue());
			event.getUid().setValue(newUid.toString());
			event.getProperties().add(new XProperty(X_SHAREURL_OLD_RECURRENCE_ID, event.getRecurrenceId().getValue()));
			event.getProperties().remove(event.getRecurrenceId());
		}
	}

	/**
	 * Returns the approximate difference in Minutes between start and end.
	 * 
	 * @param start
	 * @param end
	 * @return the approximate number of minutes between the 2 dates
	 */
	public static long approximateDifferenceInMinutes(Date start, Date end) {
		java.util.Calendar s = java.util.Calendar.getInstance();
		s.setTime(start);
		java.util.Calendar e = java.util.Calendar.getInstance();
		e.setTime(end);

		long endL   =  e.getTimeInMillis() +  e.getTimeZone().getOffset(e.getTimeInMillis());
		long startL = s.getTimeInMillis() + s.getTimeZone().getOffset(s.getTimeInMillis());

		return (endL - startL) / MILLISECS_PER_MINUTE;
	}

	/**
	 * Round the date argument down to the nearest minute using the increment argument.
	 * Examples:
	 * <ul>
	 * <li>date=08:37 AM, increment=30; result=08:30 AM</li>
	 * <li>date=08:37 AM, increment=10; result=08:30 AM</li>
	 * <li>date=08:37 AM, increment=5; result=08:35 AM</li>
	 * <li>date=08:37 AM, increment=1; result=08:37 AM</li>
	 * <li>date=08:59 AM, increment=58, result=08:58 AM</li>
	 * <li>date=08:39 AM, increment=58, result=08:00 AM</li>
	 * </ul>
	 * @param date
	 * @param increment
	 * @return
	 */
	public static Date roundDownToNearestIncrement(Date date, int increment) {
		if(increment <= 0) {
			throw new IllegalArgumentException("increment argument must be positive");
		}
		if(increment == 1) {
			return date;
		}
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(date);

		int minutesField = cal.get(java.util.Calendar.MINUTE);
		int toRemove = minutesField % increment;

		if(toRemove != 0) {
			cal.add(java.util.Calendar.MINUTE, -toRemove);
		}

		return cal.getTime();
	}
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	public static Calendar wrapEvent(VEvent event) {
		ComponentList components = new ComponentList();
		DtStart dtstart = event.getStartDate();
		Parameter tzid = dtstart.getParameter(TzId.TZID);
		if(tzid != null) {
			// make sure we add the right timezone to the calendar
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			TimeZone timeZone = registry.getTimeZone(tzid.getValue());
			if(timeZone != null) {
				components.add(timeZone.getVTimeZone());
			} else {
				LOG.warn("could not find TimeZone with id " + tzid.getValue() + " on " + staticGetDebugId(event));
			}
		}
		
		components.add(event);
		net.fortuna.ical4j.model.Calendar result = new net.fortuna.ical4j.model.Calendar(components);
		result.getProperties().add(Version.VERSION_2_0);
		result.getProperties().add(new ProdId(SHAREURL_PROD_ID));
		return result;
	}

	/**
	 * 
	 * @param emailAddress
	 * @return
	 */
	static String mailto(String emailAddress) {
		return "mailto:" + emailAddress;
	}

	static String removeMailto(String mailto) {
		return StringUtils.remove(mailto, "mailto:");
	}
}
