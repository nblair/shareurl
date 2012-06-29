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

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.AbstractSharePreference#dispose()
	 */
	@Override
	public void dispose() {
		RecurringEventUidTracker.dispose();
	}

	/**
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
