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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Helper methods for iCalendar data.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarDataUtils.java 1691 2010-02-10 18:58:13Z npblair $
 */
public final class CalendarDataUtils {

	private static final long MILLISECS_PER_DAY = 24*60*60*1000;
	
	private static final String UW_SEPARATOR = "_UW_";

	public static final String SHAREURL_PROD_ID = "-//ShareURL//WiscCal//EN";

	/**
	 * The presence of this property with a value of TRUE indicates the event was created by the Scheduling Assistant.
	 */
	public static final String UW_AVAILABLE_APPOINTMENT = "X-UW-AVAILABLE-APPOINTMENT";

	private static final Log LOG = LogFactory.getLog(CalendarDataUtils.class);
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

	/**
	 * Convert the {@link VEvent}s within the {@link Calendar} argument to a
	 * single {@link VFreeBusy} object with {@link FreeBusy} properties
	 * corresponding with each event start/end time.
	 * 
	 * @param original
	 * @return
	 */
	public static Calendar convertToFreeBusy(final Calendar original, final java.util.Date start, final java.util.Date end) {
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

	/**
	 * Mutative method.
	 * Convert the {@link Clazz} property for all {@link VEvent}s in the 
	 * {@link Calendar} argument to {@link Clazz#PUBLIC}.
	 * 
	 * @param original
	 * @return the same calendar with all values for the {@link Clazz} property set to {@link Clazz#PUBLIC}.
	 */
	public static void convertClassPublic(final Calendar original) {
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
		return event.getProperties(RDate.RDATE).size() > 0 || event.getProperties(RRule.RRULE).size() > 0;
	}

	/**
	 * 
	 * @param event
	 * @param startBoundary
	 * @param endBoundary
	 * @return
	 */
	public static PeriodList calculateRecurrence(VEvent event,
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
	public static VEvent constructRecurrenceInstance(VEvent original, Period period) {
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
	
	public static VEvent cheapRecurrenceCopy(VEvent original, Period period) {
		
		VEvent copy = new VEvent();
		copy.getProperties().add(new DtStart(period.getStart()));
		copy.getProperties().add(new DtEnd(period.getEnd()));
		copy.getProperties().add(new RecurrenceId(period.getStart()));
		if(original.getUid() != null) {
			copy.getProperties().add(propertyCopy(original.getUid()));
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
		
		return copy;
	}
	
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
	/**
	 * Remove all RDATE/RRULE/EXDATE/EXRULE properties from the event.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public static void removeRecurrenceProperties(VEvent event) {
		event.getProperties().removeAll(event.getProperties(RDate.RDATE));
		event.getProperties().removeAll(event.getProperties(RDate.RRULE));
		event.getProperties().removeAll(event.getProperties(RDate.EXDATE));
		event.getProperties().removeAll(event.getProperties(RDate.EXRULE));
	}

	/**
	 * Remove all ATTENDEE and ORGANIZER properties from the events in the calendar.
	 * 
	 * @param event
	 * @param account
	 */
	@SuppressWarnings("unchecked")
	public static void removeParticipants(Calendar calendar, ICalendarAccount account) {
		for(Iterator<?> i = calendar.getComponents(VEvent.VEVENT).iterator(); i.hasNext();) {
			VEvent event = (VEvent) i.next();
			event.getProperties().removeAll(event.getProperties(Attendee.ATTENDEE));
			event.getProperties().removeAll(event.getProperties(Organizer.ORGANIZER));
			event.getAlarms().clear();
		}
	}

	/**
	 * Method to return a String to help uniquely identity an event.
	 * If the event is not recurring, simply returns the value of the UID property.
	 * If recurring, the value returned is UID/RECURRENCE-ID.
	 * 
	 * Gracefully handles null situations.
	 * 
	 * @param component
	 * @return never null, will be "N/A" if any null condition exists
	 */
	public static String nullSafeGetDebugId(Component component) {
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

	/**
	 * Mutative method.
	 * Implements the "breakRecurrence" algorithm:
	 * Scan each VEVENT in the calendar argument. If the event
	 * has a RECURRENCE-ID property, remove it and append it to the
	 * UID property.
	 * 
	 * @param original
	 */
	public static void breakRecurrence(final Calendar original) {
		for (Iterator<?> i = original.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				convertToCombinationUid(event);
			}  
		}
	}
	/**
	 * Mutative method.
	 * Implements the "no recurrence" algorithm.
	 * Expand recurrence for all events in the calendar and remove all recurrence properties.
	 * 
	 * @see PreferRecurrenceComponentComparator
	 * @param calendar
	 */
	@SuppressWarnings("unchecked")
	public static void noRecurrence(final Calendar calendar, Date start, Date end) {
		Collections.sort(calendar.getComponents(), new PreferRecurrenceComponentComparator());
		
		Map<EventCombinationId, VEvent> eventMap = new HashMap<EventCombinationId, VEvent>();
		
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VEvent.VEVENT.equals(component.getName()) ){
				VEvent event = (VEvent) component;
				if(CalendarDataUtils.isEventRecurring(event)) {
					PeriodList recurringPeriods = CalendarDataUtils.calculateRecurrence(event, start, end);
					
					for(Object o: recurringPeriods) {
						Period period = (Period) o;
						//VEvent recurrenceInstance = CalendarDataUtils.constructRecurrenceInstance(event, period);
						VEvent recurrenceInstance = CalendarDataUtils.cheapRecurrenceCopy(event, period);
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
			event.getProperties().remove(event.getRecurrenceId());
		}
	}
	
	/**
	 * Returns the approximate difference in DAYS between start and end.
	 * 
	 * @param start
	 * @param end
	 * @return the approximate number of days between the 2 dates
	 */
	public static long approximateDifference(Date start, Date end) {
		java.util.Calendar s = java.util.Calendar.getInstance();
		s.setTime(start);
		java.util.Calendar e = java.util.Calendar.getInstance();
		e.setTime(end);

		long endL   =  e.getTimeInMillis() +  e.getTimeZone().getOffset(e.getTimeInMillis());
		long startL = s.getTimeInMillis() + s.getTimeZone().getOffset(s.getTimeInMillis());
		
		return (endL - startL) / MILLISECS_PER_DAY;
	}
	/**
	 * Class to represnt the combination id for an event: UID and RECURRENCE-ID.
	 * 
	 * @author Nicholas Blair
	 */
	protected static class EventCombinationId {
		
		private String uid;
		private String recurrenceId;
		/**
		 * 
		 * @param event
		 */
		protected EventCombinationId(VEvent event) {
			this.uid = event.getUid().getValue();
			this.recurrenceId = event.getRecurrenceId().getValue();
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
}
