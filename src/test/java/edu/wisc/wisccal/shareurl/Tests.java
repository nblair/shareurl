package edu.wisc.wisccal.shareurl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.lang.time.DateUtils;

/**
 * @author Nicholas Blair
 */
public class Tests {

	/**
	 * Create a date using the format "yyyyMMdd".
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date makeDate(String value) {
		return makeDateTime(value, "yyyyMMdd");
	}
	
	/**
	 * Create a date using the format "yyyyMMdd-HHmm".
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date makeDateTime(String value) {
		return makeDateTime(value, "yyyyMMdd-HHmm");
	}
	/**
	 * Create a date using the value and the specified format.
	 * The returned date will be truncated to the Minute.
	 * 
	 * @see DateUtils#truncate(java.util.Date, int)
	 * @param value
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date makeDateTime(String value, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			return DateUtils.truncate(df.parse(value), java.util.Calendar.MINUTE);
		} catch (ParseException e) {
			throw new IllegalArgumentException("failed to makeDateTime for " + value + " and format " + format, e);
		}
	}
	
	/**
	 * Mock an event, will have a UID, DTSTART, DTEND, and SUMMARY.
	 * 
	 * @param start the start time "yyyyMMdd-HHmm"
	 * @param end the end time "yyyyMMdd-HHmm"
	 * @param the summary
	 * @param whether or not the times should include "America/Chicago" timezone reference
	 * @return the control event
	 */
	public static VEvent mockEvent(String start, String end, String summary, boolean includeTimezone) {
		VEvent event = new VEvent();
		event.getProperties().add(new Uid(UUID.randomUUID().toString()));
		DtStart dtStart = new DtStart(new DateTime(Tests.makeDateTime(start)));
		if(includeTimezone) {
			dtStart.getParameters().add(new TzId("America/Chicago"));
		} else {
			dtStart.setUtc(true);
		}
		event.getProperties().add(dtStart);
		DtEnd dtEnd = new DtEnd(new DateTime(Tests.makeDateTime(end)));
		if(includeTimezone) {
			dtEnd.getParameters().add(new TzId("America/Chicago"));
		} else {
			dtEnd.setUtc(true);
		}
		event.getProperties().add(dtEnd);
		event.getProperties().add(new Summary(summary));
		return event;
	}
	
	/**
	 * Mock an all day event, will have a UID, DTSTART, DTEND, and SUMMARY.
	 * 
	 * @param start the start time "yyyyMMdd"
	 * @param end the end time "yyyyMMdd"
	 * @param the summary
	 * @return the control event
	 */
	public static VEvent mockAllDayEvent(String start, String end, String summary) {
		VEvent event = new VEvent();
		event.getProperties().add(new Uid(UUID.randomUUID().toString()));
		DtStart dtStart = new DtStart(new DateTime(Tests.makeDate(start)));
		dtStart.getParameters().add(Value.DATE);
		event.getProperties().add(dtStart);
		DtEnd dtEnd = new DtEnd(new DateTime(Tests.makeDate(end)));
		dtEnd.getParameters().add(Value.DATE);
		event.getProperties().add(dtEnd);
		event.getProperties().add(new Summary(summary));
		return event;
	}
}
