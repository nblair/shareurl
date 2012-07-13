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
package edu.wisc.wisccal.shareurl.domain;

import net.fortuna.ical4j.model.component.VEvent;


/**
 * Interface for describing individual share preferences.
 * A {@link Share} may have 0 to N {@link ISharePreference}s
 * associated with it.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ISharePreference.java 1677 2010-02-08 18:31:01Z npblair $
 */
public interface ISharePreference {

	/**
	 * 
	 * @return the type of preference (may be identical to the key in some implementations)
	 */
	String getType();
	
	/**
	 * 
	 * @return the key for this preference
	 */
	String getKey();
	
	/**
	 * 
	 * @return the customer supplied value for this preference
	 */
	String getValue();
	
	/**
	 * 
	 * @return a customer-friendly display name for this preference
	 */
	String getDisplayName();
	
	/**
	 * 
	 * @return true if this preference participants in event filtering.
	 */
	boolean participatesInFiltering();
	
	/**
	 * This method is required if and only if {@link #participatesInFiltering()} returns
	 * true.
	 * 
	 * @param event
	 * @return true if the event matches the criteria for this preference
	 */
	boolean matches(VEvent event);
	
	/**
	 * Callback to cleanup and resources retained by instances.
	 */
	void dispose();
}
