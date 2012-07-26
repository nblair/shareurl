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

package edu.wisc.wisccal.shareurl.support;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;

import org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;

import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;

/**
 * See https://jira.doit.wisc.edu/jira/browse/CALKEY-115
 * 
 * @author Nicholas Blair
 */
public class Calkey115CalendarDataDaoImpl extends CaldavCalendarDataDaoImpl {

	private TimeZoneRegistry _registry = TimeZoneRegistryFactory.getInstance().createRegistry();
	
	/**
	 * Calls the super, then passes the result through {@link #checkEventsAndInspectTimeZones(Calendar)}.
	 *  (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavCalendarDataDaoImpl#consolidate(java.util.List)
	 */
	@Override
	protected Calendar consolidate(List<CalendarWithURI> calendars) {
		Calendar result = super.consolidate(calendars);
		checkEventsAndInspectTimeZones(result);
		return result;
	}

	/**
	 * Potentially mutative method for components within the calendar argument.
	 * Performs 2 operations:
	 * <ol>
	 * <li>Looks for events damaged by WMG-1507. If found and has {@link Transp#OPAQUE} and sets a SUMMARY property of "Busy". If has {@link Transp#TRANSPARENT}, event is removed from output.</li>
	 * <li>Looks for damaged {@link VTimeZone}s and replaces them with the correct instance.</li>
	 * </ol>
	 * 
	 * @param calendar
	 */
	@SuppressWarnings("unchecked")
	protected void checkEventsAndInspectTimeZones(Calendar calendar) {
		Set<VTimeZone> toAdd = new HashSet<VTimeZone>();
		for(Iterator<?> i = calendar.getComponents().iterator(); i.hasNext(); ) {
			Component component = (Component) i.next();
			if(VTimeZone.VTIMEZONE.equals(component.getName())) {
				VTimeZone vtimezone = (VTimeZone) component;
				if(vtimezone.getObservances().size() == 0) {
					log.debug("detected damaged vtimezone for CALKEY-115 workaround: " + vtimezone );
					TimeZone timezone = _registry.getTimeZone(vtimezone.getTimeZoneId().getValue());
					toAdd.add(timezone.getVTimeZone());
					i.remove();
				}
			} else if (VEvent.VEVENT.equals(component.getName())) {
				VEvent vevent = (VEvent) component;
				if(vevent.getSummary() == null && vevent.getLastModified() == null) {
					log.debug("detected damaged event for CALKEY-115 workaround: " + CalendarDataUtils.staticGetDebugId(component) );
					Transp transp = vevent.getTransparency();
					if(transp == null || Transp.OPAQUE.equals(transp)) {
						vevent.getProperties().add(new Summary("Busy"));
					} else {
						i.remove();
					}
				}
			}
		}
		if(!toAdd.isEmpty()) {
			calendar.getComponents().addAll(toAdd);
		}
	}
	

}
