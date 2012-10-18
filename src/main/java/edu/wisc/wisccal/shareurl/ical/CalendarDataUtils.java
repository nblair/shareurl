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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Action;
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
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.stereotype.Service;

import edu.wisc.wisccal.shareurl.web.IShareRequestDetails;

/**
 * {@link CalendarDataProcessor} implementation.
 *  
 * @author Nicholas Blair
 */
@Service
public final class CalendarDataUtils implements CalendarDataProcessor {

	private static final String MAILTO_PREFIX = "mailto:";

	static final String BUSY = "Busy";

	static final String FREE = "Free";

	protected static final String X_SHAREURL_RECURRENCE_EXPAND = "X-SHAREURL-RECURRENCE-EXPAND";

	public static final String X_SHAREURL_OLD_RECURRENCE_ID = "X-SHAREURL-OLD-RECUR-ID";

	public static final long MILLISECS_PER_MINUTE = 60*1000;

	public static final String UW_SEPARATOR = "_UW_";

	public static final String SHAREURL_PROD_ID = "-//ShareURL//WiscCal//EN";

	private static final Log LOG = LogFactory.getLog(CalendarDataUtils.class);

	protected final Set<String> retainedPropertyNamesOnStripDetails = new HashSet<String>(
			Arrays.asList(new String[] { Uid.UID, DtStart.DTSTART, DtEnd.DTEND, DtStamp.DTSTAMP, 
					RecurrenceId.RECURRENCE_ID, Status.STATUS, Clazz.CLASS, Created.CREATED, LastModified.LAST_MODIFIED,
					X_SHAREURL_OLD_RECURRENCE_ID, X_SHAREURL_RECURRENCE_EXPAND, Transp.TRANSP }));

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
	public Calendar convertToFreeBusy(final Calendar original, final java.util.Date start, final java.util.Date end, final ICalendarAccount calendarAccount) {
		ComponentList resultComponents = new ComponentList();
		VFreeBusy vFreeBusy = new VFreeBusy();
		// add dtstart from arguments
		vFreeBusy.getProperties().add(new DtStart(new DateTime(start)));
		// add dtend from arguments
		vFreeBusy.getProperties().add(new DtEnd(new DateTime(end)));

		// for each original event, add a FreeBUSY 
		//Collections.sort(original.getComponents(), new PreferRecurrenceComponentComparator());

		for(Iterator<?> i = original.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent e = (VEvent) component;

				if(Transp.TRANSPARENT.equals(e.getTransparency())) {
					// skip transparent events in free busy
					continue;
				}
				EventParticipation participation = getEventParticipation(e, calendarAccount);
				if(participation.isAttendee() && !EventParticipation.ATTENDEE_ACCEPTED.equals(participation)) {
					// is an attendee, but not accepted, skip
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
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#stripEventDetails(net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void stripEventDetails(final Calendar original, final ICalendarAccount calendarAccount) {
		for(Iterator<?> i = original.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				Property transp = component.getProperty(Transp.TRANSP);
				if(Transp.TRANSPARENT.equals(transp)) {
					i.remove();
				} else {
					EventParticipation participation = getEventParticipation((VEvent) component, calendarAccount); 
					if(participation.isAttendee() && !participation.equals(EventParticipation.ATTENDEE_ACCEPTED)) {
						// calendarAccount is an attendee of this event, but hasn't accepted - remove!
						i.remove();
					} else{
						for(Iterator<?> j = component.getProperties().iterator(); j.hasNext();) {
							Property property = (Property) j.next();
							if(!retainedPropertyNamesOnStripDetails.contains(property.getName())){
								j.remove();
							}
						}
						component.getProperties().add(new Summary(BUSY));
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#removeAllAlarms(net.fortuna.ical4j.model.Calendar)
	 */
	@Override
	public void removeEmailAlarms(Calendar calendar) {
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				ComponentList alarms = ((VEvent) component).getAlarms();
				for(Iterator<?> j = alarms.iterator(); j.hasNext();) {
					VAlarm alarm = (VAlarm) j.next();
					if(Action.EMAIL.equals(alarm.getAction())) {
						j.remove();
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#removeDeclined(net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public void removeDeclined(Calendar calendar,
			ICalendarAccount calendarAccount) {
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				EventParticipation participation = getEventParticipation(event, calendarAccount);
				if (participation.equals(EventParticipation.ATTENDEE_DECLINED)) {
					i.remove();
				} 
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
	 * 
	 * @param period
	 * @return true if the period represents a full day
	 */
	public static boolean isAllDayPeriod(Period period) {
		Date start = period.getStart();
		Date end = period.getEnd();

		Date expectedStart = DateUtils.truncate(start, java.util.Calendar.DATE);
		Date expectedEnd = DateUtils.addDays(start, 1);

		return start.equals(expectedStart) && end.equals(expectedEnd);
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
		final DtStart newDtStart;
		final DtEnd newDtEnd;
		final boolean isAllDayEvent = isDayEvent(original);
		if(isAllDayEvent) {
			newDtStart = new DtStart(truncate(period.getStart()));
			newDtEnd = new DtEnd(truncate(period.getEnd()));
		} else {
			newDtStart = new DtStart(period.getStart());
			newDtEnd = new DtEnd(period.getEnd());
		}
		copy.getProperties().add(newDtStart);
		copy.getProperties().add(newDtEnd);
		copy.getProperties().add(new XProperty(X_SHAREURL_RECURRENCE_EXPAND, period.toString()));
		if(setRecurrenceId) {
			copy.getProperties().add(propertyCopy(original.getUid()));
			final RecurrenceId newRecurrenceId;
			if(isAllDayEvent) {
				newRecurrenceId = new RecurrenceId(truncate(period.getStart()));
			} else {
				newRecurrenceId = new RecurrenceId(period.getStart());
			}
			copy.getProperties().add(newRecurrenceId);
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
		if(original.getTransparency() != null) {
			copy.getProperties().add(propertyCopy(original.getTransparency()));
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
	 * Truncates the {@link DateTime} argument to just a {@link net.fortuna.ical4j.model.Date}.
	 * 
	 * @param dateTime
	 * @return
	 */
	protected net.fortuna.ical4j.model.Date truncate(DateTime dateTime) {
		net.fortuna.ical4j.model.Date result = new net.fortuna.ical4j.model.Date(dateTime);
		return result;
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
						if(isWithinRange(recurrenceInstance, start, end)) {
							EventCombinationId comboId = new EventCombinationId(recurrenceInstance);
							eventMap.put(comboId, recurrenceInstance);
						}
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
			Collections.sort(calendar.getComponents(), new PostRecurrenceExpansionComparator());
		}
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#noRecurrence(net.fortuna.ical4j.model.Calendar, java.util.Date, java.util.Date, boolean)
	 */
	public void noRecurrence(final Calendar calendar, Date start, Date end, boolean preserveParticipants) {
		expandRecurrence(calendar, start, end, preserveParticipants);
		breakRecurrence(calendar);
	}
	/**
	 * 
	 * @param event
	 * @param start
	 * @param end
	 * @return true if this event is within the range specified by the 2 date arguments
	 */
	public static boolean isWithinRange(VEvent event, Date start, Date end) {
		net.fortuna.ical4j.model.Date eventStart = event.getStartDate().getDate();
		return eventStart.getTime() == start.getTime() || eventStart.getTime() == end.getTime() || (eventStart.after(start) && eventStart.before(end));
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
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#getEventParticipation(net.fortuna.ical4j.model.component.VEvent, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public EventParticipation getEventParticipation(VEvent event, ICalendarAccount calendarAccount) {
		Organizer organizer = event.getOrganizer();
		PropertyList attendees = event.getProperties(Attendee.ATTENDEE);
		if(null == organizer || attendees.size() == 0) {
			return EventParticipation.PERSONAL_EVENT;
		}

		if(organizer.getValue().equalsIgnoreCase(mailto(calendarAccount.getEmailAddress()))) {
			return EventParticipation.ORGANIZER;
		}

		for(Object o: attendees) {
			Attendee attendee = (Attendee) o;
			if(attendee.getValue().equals(mailto(calendarAccount.getEmailAddress()))) {
				Parameter partstat = attendee.getParameter(PartStat.PARTSTAT);
				if(partstat == null || PartStat.NEEDS_ACTION.equals(partstat)) {
					return EventParticipation.ATTENDEE_NEEDSACTION;
				} else if (PartStat.ACCEPTED.equals(partstat)) {
					return EventParticipation.ATTENDEE_ACCEPTED;
				} else if (PartStat.TENTATIVE.equals(partstat)) {
					return EventParticipation.ATTENDEE_TENTATIVE;
				} else {
					return EventParticipation.ATTENDEE_DECLINED;
				}
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
				if(!participation.isAttendee()) {
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
				if(!participation.equals(EventParticipation.PERSONAL_EVENT)) {
					i.remove();
					if(LOG.isDebugEnabled()) {
						LOG.debug("personalOnly: removed " + getDebugId(component) + ", " + participation + " from agenda for " + calendarAccount);
					}
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.ical.CalendarDataProcessor#filterAgendaForDateRange(net.fortuna.ical4j.model.Calendar, edu.wisc.wisccal.shareurl.web.IShareRequestDetails)
	 */
	@Override
	public void filterAgendaForDateRange(Calendar agenda,
			IShareRequestDetails requestDetails) {
		requestDetails.getStartDate();

		for(Iterator<?> i = agenda.getComponents().iterator(); i.hasNext() ;){
			Component c = (Component) i.next();
			if(VEvent.VEVENT.equals(c.getName())) {
				VEvent event = (VEvent) c;
				if(!isWithinRange(event, requestDetails.getStartDate(), requestDetails.getEndDate())) {
					LOG.debug("removing event " + CalendarDataUtils.staticGetDebugId(event) + " since startdate falls outside of requestDetails window " + requestDetails);
					i.remove();
				}
			}
		}
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
		return wrapEvent(event, SHAREURL_PROD_ID);
	}

	/**
	 * Wrap the event in a {@link net.fortuna.ical4j.model.Calendar}.
	 * If the {@link VEvent} indicates a {@link TzId} parameter on it's DTSTART,
	 * this will include the corresponding VTIMEZONE.
	 * 
	 * @param event the event to wrap
	 * @param prodId the value to use for the {@link ProdId} property on the result
	 * @return the calendar
	 */
	public static Calendar wrapEvent(VEvent event, String prodId) {
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
		result.getProperties().add(new ProdId(prodId));
		return result;
	}

	/**
	 * 
	 * @param emailAddress
	 * @return
	 */
	public static String mailto(String emailAddress) {
		return MAILTO_PREFIX + emailAddress;
	}

	public static String removeMailto(String mailto) {
		return StringUtils.remove(mailto, MAILTO_PREFIX);
	}

	/**
	 * 
	 * @param component
	 * @return
	 */
	public static String safeGetUid(Component component) {
		if(component == null) {
			return null;
		}
		Property uid = component.getProperty(Uid.UID);
		if(uid == null) {
			return null;
		}
		return uid.getValue();
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public static String getParticipantEmailAddress(Property property) {
		if(property == null) {
			return null;
		}
		if(property.getValue().startsWith(MAILTO_PREFIX)) {
			return removeMailto(property.getValue());
		}

		return null;
	}
	/**
	 * 
	 * @param property
	 * @return
	 */
	public static String getParticipantDisplayName(Property property) {
		if(property == null) {
			return null;
		}
		Parameter cn = property.getParameter(Cn.CN);
		if(cn != null) {
			return cn.getValue();
		}

		return null;
	}
	public static PropertyList getAttendees(VEvent event) {
		if(event == null) {
			return null;
		}

		return event.getProperties(Attendee.ATTENDEE);
	}
	/**
	 * 
	 * @param property
	 */
	public static String getParticipationStatus(Property property) {
		if(property == null) {
			return null;
		}
		Parameter partstat = property.getParameter(PartStat.PARTSTAT);
		if(partstat != null) {
			if(PartStat.ACCEPTED.equals(partstat)) {
				return "Attending";
			} else if (PartStat.DECLINED.equals(partstat)){
				return "Declined";
			} else if (PartStat.TENTATIVE.equals(partstat)) {
				return "Maybe";
			} else if (PartStat.NEEDS_ACTION.equals(partstat)) {
				return "Invited";
			} else {
				return partstat.getValue();
			}
		}

		return null;
	}
	public static String getParticipationStatusStyle(Property property) {
		if(property == null) {
			return null;
		}
		Parameter partstat = property.getParameter(PartStat.PARTSTAT);
		if(partstat != null) {
			if(PartStat.ACCEPTED.equals(partstat)) {
				return "accepted";
			} else if (PartStat.DECLINED.equals(partstat)){
				return "declined";
			} else if (PartStat.NEEDS_ACTION.equals(partstat)) {
				return "invited";
			} else {
				return "other";
			}
		}

		return null;
	}
	/**
	 * {@link Comparator} for {@link Component#getName()}.
	 * @author Nicholas Blair
	 */
	static class ComponentNameComparator implements Comparator<Component> {
		@Override
		public int compare(Component o1, Component o2) {
			return new CompareToBuilder().append(o1.getName(), o2.getName()).toComparison();
		}
	}
}
