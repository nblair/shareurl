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

import org.apache.commons.lang.builder.CompareToBuilder;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;

/**
 * @author Nicholas Blair
 */
public class PostRecurrenceExpansionComparator implements Comparator<Component> {

	@Override
	public int compare(Component o1, Component o2) {
		
		CompareToBuilder builder = new CompareToBuilder();
		String name1 = o1.getName();
		String name2 = o2.getName();
		builder.append(name1, name2);
		if(VEvent.VEVENT.equals(name1) && VEvent.VEVENT.equals(name2)) {
			Property start1 = o1.getProperty(DtStart.DTSTART);
			Property start2 = o2.getProperty(DtStart.DTSTART);
			builder.append(start1.getValue(), start2.getValue());
		}
		return builder.toComparison();
	}

}
