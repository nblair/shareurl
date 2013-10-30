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
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 * Event filtering interface.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IEventFilter.java 1441 2009-12-17 19:09:21Z npblair $
 */
public interface IEventFilter {

	/**
	 * Return a {@link Calendar} that contains a subset of the events
	 * of the {@link Calendar} argument based on the {@link SharePreferences}
	 * argument.
	 * 
	 * @param original
	 * @param preferences
	 * @return
	 */
	Calendar filterEvents(Calendar original, SharePreferences preferences);
}
