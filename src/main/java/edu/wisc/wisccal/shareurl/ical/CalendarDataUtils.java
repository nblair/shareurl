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
import java.util.Date;
import java.util.Iterator;

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
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper methods for iCalendar data.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarDataUtils.java 1691 2010-02-10 18:58:13Z npblair $
 */
public final class CalendarDataUtils {

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
	 * Convert the {@link Clazz} property for all {@link VEvent}s in the 
	 * {@link Calendar} argument to {@link Clazz#PUBLIC}.
	 * 
	 * @param original
	 * @return the same calendar with all values for the {@link Clazz} property set to {@link Clazz#PUBLIC}.
	 */
	public static Calendar convertClassPublic(final Calendar original) {
		ComponentList resultComponents = new ComponentList();
		for (Iterator<?> i = original.getComponents(VEvent.VEVENT).iterator(); i.hasNext();) {
			VEvent event = (VEvent) i.next();  
			Property classProperty = event.getProperty(Clazz.CLASS);
			if(!Clazz.PUBLIC.equals(classProperty)) {
				event.getProperties().remove(classProperty);
				event.getProperties().add(Clazz.PUBLIC);
			}
			resultComponents.add(event);
		}
		Calendar result = new Calendar(resultComponents);
		result.getProperties().add(Version.VERSION_2_0);
		result.getProperties().add(new ProdId(SHAREURL_PROD_ID));
		return result;
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
}
