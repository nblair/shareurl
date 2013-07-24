/**
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
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
/**
 * 
 */
package edu.wisc.wisccal.shareurl.domain.simple;

import java.util.List;



/**
 * Interface for converting ical4j calendar data to the simplified
 * model in this package.
 * 
 * @author Nicholas Blair
 */
public interface SimpleCalendars {

	/**
	 * Convert the ical4j {@link net.fortuna.ical4j.model.Calendar} argument to the simplified representation.
	 * 
	 * @param calendar 
	 * @param removeParticipants if false, the result will not contain any {@link EventParticipant}s.
	 * @return the simplified representation
	 */
	Calendar simplify(net.fortuna.ical4j.model.Calendar calendar, boolean includeParticipants);
	
	/**
	 * 
	 * @param vevent
	 * @param removeParticipants
	 * @return
	 */
	Event convert(net.fortuna.ical4j.model.component.VEvent vevent, boolean includeParticipants);
	
	/**
	 * 
	 * @param vfreebusy
	 * @return
	 */
	List<FreeBusy> convert(net.fortuna.ical4j.model.component.VFreeBusy vfreebusy);
}
