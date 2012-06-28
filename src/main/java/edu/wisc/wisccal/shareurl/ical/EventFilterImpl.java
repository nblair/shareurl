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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 * Standard {@link IEventFilter} implementation.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EventFilterImpl.java 1718 2010-02-15 21:53:22Z npblair $
 */
@Component
public class EventFilterImpl implements IEventFilter {

	protected final Log log = LogFactory.getLog(this.getClass());
	private CalendarDataProcessor calendarDataProcessor;
	/**
	 * @return the calendarDataProcessor
	 */
	public CalendarDataProcessor getCalendarDataProcessor() {
		return calendarDataProcessor;
	}
	/**
	 * @param calendarDataProcessor the calendarDataProcessor to set
	 */
	@Autowired
	public void setCalendarDataProcessor(CalendarDataProcessor calendarDataProcessor) {
		this.calendarDataProcessor = calendarDataProcessor;
	}


	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ical.IEventFilter#filterEvents(net.fortuna.ical4j.model.Calendar, edu.wisc.wisccal.calendarkey.SharePreferences)
	 */
	@Override
	public Calendar filterEvents(final Calendar original, final SharePreferences preferences) {
		if(preferences.getPreferences().size() == 0 || preferences.isFreeBusyOnly()) {
			// return original unfiltered calendar if no preferences exist or is freebusy only
			return original;
		}

		// otherwise, share has property matches, only return elements that match
		ComponentList components = original.getComponents(VEvent.VEVENT);
		ComponentList resultComponents = new ComponentList();

		for(Object c : components) {
			VEvent event = (VEvent) c;
			boolean kept = false;
			ISharePreference keeper = null;
			for(ISharePreference pref : preferences.getPreferences()) {
				if(pref.matches(event)) {
					kept = resultComponents.add(event);
					break;
				}
			}
			if(log.isDebugEnabled()) {
				if(kept) {
					log.debug(keeper + " retained " + calendarDataProcessor.getDebugId(event));
				} else {
					log.debug("dropping " + calendarDataProcessor.getDebugId(event));
				}
			}
		}

		Calendar result = new Calendar(resultComponents);
		result.getProperties().add(Version.VERSION_2_0);
		result.getProperties().add(new ProdId("-//ShareURL//WiscCal//EN"));
		return result;
	}

}
