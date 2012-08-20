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
