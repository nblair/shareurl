package edu.wisc.wisccal.shareurl.ical;

import java.util.Comparator;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * {@link Comparator} for {@link Component}s intended to sort those with 
 * recurrence properties (RRULE, RDATE, EXRULE, EXDATE) to the front.
 * {@link Component}s with RECURRENCE-ID properties should be sorted to the end.
 * 
 * {@link VTimeZone} components are also sorted to the very end.
 * @author Nicholas Blair
 */
public class PreferRecurrenceComponentComparator implements
		Comparator<Component> {

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Component o1, Component o2) {		
		// 1 or more of the components are events, compare on recurrence properties
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(o1.getName(), o2.getName());
		builder.append(-(o1.getProperties(RRule.RRULE).size() + o1.getProperties(RDate.RDATE).size()), 
				-(o2.getProperties(RRule.RRULE).size() + o2.getProperties(RDate.RDATE).size()));
		builder.append(o1.getProperties(RecurrenceId.RECURRENCE_ID).size(), o2.getProperties(RecurrenceId.RECURRENCE_ID).size());
		return builder.toComparison();
	}
}
