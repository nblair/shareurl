/**
 * 
 */
package edu.wisc.wisccal.shareurl.domain.simple;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Clazz;

import org.apache.commons.lang.StringUtils;

import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;

/**
 * @author Nicholas Blair
 *
 */
public class SimpleCalendarsImpl implements SimpleCalendars {

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.simple.SimpleCalendars#simplify(net.fortuna.ical4j.model.Calendar, boolean)
	 */
	@Override
	public Calendar simplify(net.fortuna.ical4j.model.Calendar calendar, boolean includeParticipants) {
		if(calendar == null) {
			return null;
		}
		edu.wisc.wisccal.shareurl.domain.simple.Calendar result = new edu.wisc.wisccal.shareurl.domain.simple.Calendar();
		result.setProductId(CalendarDataUtils.SHAREURL_PROD_ID);
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

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.simple.SimpleCalendars#convert(net.fortuna.ical4j.model.component.VEvent, boolean)
	 */
	@Override
	public Event convert(net.fortuna.ical4j.model.component.VEvent vevent, boolean includeParticipants) {
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
				o.setEmailAddress(CalendarDataUtils.removeMailto(organizer.getValue()));
				o.setDesignateOrganizer(nullSafeParameterValue(organizer.getParameter(SentBy.SENT_BY)));
				event.setOrganizer(o);
			}
			PropertyList attendees = vevent.getProperties(Attendee.ATTENDEE);
			for(Object o: attendees) {
				Attendee attendee = (Attendee) o;

				edu.wisc.wisccal.shareurl.domain.simple.Attendee a = new edu.wisc.wisccal.shareurl.domain.simple.Attendee();
				a.setDisplayName(nullSafeParameterValue(attendee.getParameter(Cn.CN)));
				a.setEmailAddress(CalendarDataUtils.removeMailto(attendee.getValue()));
				a.setParticipationStatus(nullSafeParameterValue(attendee.getParameter(PartStat.PARTSTAT)));

				event.getAttendees().add(a);
			}
		}
		event.setRecurrenceId(nullSafePropertyValue(vevent.getRecurrenceId()));
		if(event.getRecurrenceId() == null) {
			// try the X-UW version
			event.setRecurrenceId(nullSafePropertyValue(vevent.getProperty(CalendarDataUtils.X_SHAREURL_OLD_RECURRENCE_ID)));
			if(event.getRecurrenceId() != null) {
				// restore original UID
				event.setUid(StringUtils.substringBefore(event.getUid(), CalendarDataUtils.UW_SEPARATOR));
			}
		}
		event.setRecurring(CalendarDataUtils.isEventRecurring(vevent));
		event.setTransparency(nullSafePropertyValue(vevent.getTransparency()));
		event.setEventStatus(nullSafePropertyValue(vevent.getStatus()));
		return event;
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.simple.SimpleCalendars#convert(net.fortuna.ical4j.model.component.VFreeBusy)
	 */
	@Override
	public List<FreeBusy> convert(net.fortuna.ical4j.model.component.VFreeBusy vfreebusy) {
		List<FreeBusy> result = new ArrayList<FreeBusy>();
		String uid = nullSafePropertyValue(vfreebusy.getUid());
		PropertyList freebusy = vfreebusy.getProperties(net.fortuna.ical4j.model.property.FreeBusy.FREEBUSY);
		for(Object o : freebusy) {
			net.fortuna.ical4j.model.property.FreeBusy fb = (net.fortuna.ical4j.model.property.FreeBusy) o;
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
				instance.setShowTimeAs(status);
				instance.setUid(uid);
				result.add(instance);
			}
		}
		return result;
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
	
}
