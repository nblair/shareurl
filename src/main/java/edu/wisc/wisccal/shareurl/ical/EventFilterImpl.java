package edu.wisc.wisccal.shareurl.ical;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.TzId;
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
	 *
	 *  Filter events based on SharePreferences.
	 *	Events matching any propertyMatch preference are returned
	 *  Events matching any calendarMatch preference are returned
	 *  If both calendarMatch and propertyMatch prefrences are defined, events must match at least one of each preferenceType
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public Calendar filterEvents(final Calendar original, final SharePreferences preferences) {
		final Set<ISharePreference> filterPreferences = preferences.getFilterPreferences();
		Set<ISharePreference> calendarFilters = preferences.getCalendarMatchPreferences();
		
		Set<ISharePreference> propertyFilters = filterPreferences;
		propertyFilters.removeAll(calendarFilters);
		
		log.debug("filterEvents prefrernces = "+ preferences.toString());
		
		if(filterPreferences.size() == 0 || preferences.isFreeBusyOnly()) {
			// return original unfiltered calendar if no preferences exist or is freebusy only
			return original;
		}

		// otherwise, share has property matches, only return elements that match
		ComponentList propertyMatchComponents = new ComponentList();
		Map<String, VTimeZone> timezones = new HashMap<String, VTimeZone>();

		for(Iterator<?> i = original.getComponents().iterator(); i.hasNext(); ) {
			net.fortuna.ical4j.model.Component component = (net.fortuna.ical4j.model.Component) i.next();
			if(VTimeZone.VTIMEZONE.equals(component.getName())) {
				VTimeZone tz = (VTimeZone) component;
				TzId tzid = tz.getTimeZoneId();
				if(tzid != null) {
					timezones.put(tzid.getValue(), tz);
				}
			} else if (VEvent.VEVENT.equals(component.getName())) {
				VEvent event = (VEvent) component;
				boolean kept = false;
				ISharePreference keeper = null;
				for(ISharePreference pref : propertyFilters) {
					if(pref.matches(event)) {
						kept = propertyMatchComponents.add(event);
						break;
					}
				}
				if(log.isDebugEnabled()) {
					if(kept) {
						log.trace("propertyFilterPreference retained: " + calendarDataProcessor.getDebugId(event));
					} else {
						log.trace("propertyFilterPreference dropped: " + calendarDataProcessor.getDebugId(event));
					}
				}
			}
		}
		
		ComponentList calendarAndPropertyMatchComponents = new ComponentList();
		if(calendarFilters.isEmpty()){
			calendarAndPropertyMatchComponents.addAll(propertyMatchComponents);
		}else{
			for(Iterator<?> i = propertyMatchComponents.iterator(); i.hasNext(); ) {
				net.fortuna.ical4j.model.Component component = (net.fortuna.ical4j.model.Component) i.next();
				VEvent event = (VEvent) component;
				boolean kept = false;
				ISharePreference keeper = null;
				for(ISharePreference pref : calendarFilters) {
					if(pref.matches(event)) {
						kept = calendarAndPropertyMatchComponents.add(event);
						break;
					}
				}
				if(log.isDebugEnabled()) {
					if(kept) {
						log.trace("calendarMatchPreference retained: " + calendarDataProcessor.getDebugId(event));
					} else {
						log.trace("calendarMatchPreference dropped: " + calendarDataProcessor.getDebugId(event));
					}
				}
			}	
		}
		
			

		preferences.disposeAll();
		Calendar result = new Calendar(calendarAndPropertyMatchComponents);
		result.getComponents().addAll(timezones.values());
		result.getProperties().add(Version.VERSION_2_0);
		result.getProperties().add(new ProdId("-//ShareURL//WiscCal//EN"));
		return result;
	}

}
