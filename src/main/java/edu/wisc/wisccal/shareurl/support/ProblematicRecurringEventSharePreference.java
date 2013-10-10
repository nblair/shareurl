package edu.wisc.wisccal.shareurl.support;

import java.util.HashSet;
import java.util.Set;

import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.Uid;
import edu.wisc.wisccal.shareurl.domain.AbstractSharePreference;

/**
 * {@link AbstractSharePreference} that only can be used when the
 * uw-support-rdate compatibility option is triggered.
 * 
 * The purpose of this implementation is to include only events
 * that use the RDATE property for recurrence.
 * 
 * @author Nicholas Blair
 */
public class ProblematicRecurringEventSharePreference extends
		AbstractSharePreference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2912301466748927542L;

	public static final String PROBLEM_RECURRENCE_SUPPORT = "PROBLEM_RECURRENCE_SUPPORT";
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getType()
	 */
	@Override
	public String getType() {
		return PROBLEM_RECURRENCE_SUPPORT;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getKey()
	 */
	@Override
	public String getKey() {
		return PROBLEM_RECURRENCE_SUPPORT;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getValue()
	 */
	@Override
	public String getValue() {
		return PROBLEM_RECURRENCE_SUPPORT;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return PROBLEM_RECURRENCE_SUPPORT;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public boolean participatesInFiltering() {
		return true;
	}

	/**
	 * This implementation is sensitive to ordering!
	 * 
	 *  (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#matches(net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	public boolean matches(VEvent event) {
		PropertyList rdates = event.getProperties(RDate.RDATE);
		Uid uid = event.getUid();
		if(rdates.size() > 0) {
			// track event uid
			if(uid != null) {
				RecurringEventUidTracker.trackUid(uid.getValue());
			}
			return true;
		} else if(event.getRecurrenceId() != null && uid != null) {
			// event has RecurrenceId, check tracked UIDs to see if we need to include it
			return RecurringEventUidTracker.isTracked(uid.getValue());
		}
		
		return false;
	}

	/**
	 * @see RecurringEventUidTracker#dispose()
	 * @see edu.wisc.wisccal.shareurl.domain.AbstractSharePreference#dispose()
	 */
	@Override
	public void dispose() {
		RecurringEventUidTracker.dispose();
	}

	/**
	 * Tracker class that uses a {@link ThreadLocal} to remember which Uids
	 * have been visited in this thread of execution.
	 * 
	 * @author Nicholas Blair
	 */
	static class RecurringEventUidTracker {
		private static final ThreadLocal<Set<String>> trackedUids = 
				new ThreadLocal<Set<String>>() {
					/* (non-Javadoc)
					 * @see java.lang.ThreadLocal#initialValue()
					 */
					@Override
					protected Set<String> initialValue() {
						return new HashSet<String>();
					}
		};
		
		static void trackUid(String value) {
			trackedUids.get().add(value);
		}
		
		static boolean isTracked(String value) {
			return trackedUids.get().contains(value);
		}
		
		static void dispose() {
			trackedUids.remove();
		}
	}
}
