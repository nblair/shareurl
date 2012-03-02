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
package edu.wisc.wisccal.shareurl;

import java.io.Serializable;

/**
 * Interface to describe common properties of 
 * accounts with a remote calendar system.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ICalendarAccount.java 1470 2010-01-07 19:11:29Z npblair $
 */
public interface ICalendarAccount extends Serializable {

	/**
	 * The "display name" for the account.
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * The common "username" for the account.
	 * May or may not be identical to {@link #getCalendarLoginId()}, depending
	 * on the calendar system (or account type).
	 * @return
	 */
	String getUsername();
	
	/**
	 * Get the ID (as a {@link String}) used to authenticate with the calendar system.
	 * 
	 * @return
	 */
	String getCalendarLoginId();
	
	/**
	 * Get the unique identifier (as a {@link String}) for this account in
	 * the calendar system.
	 * May or may not be identical to {@link #getCalendarLoginId()}, depending
	 * on the calendar system (or account type).
	 * 
	 * @return
	 */
	String getCalendarUniqueId();
	
	/**
	 * 
	 * @return true if this account is eligible for calendar service
	 */
	boolean isEligible();
}
