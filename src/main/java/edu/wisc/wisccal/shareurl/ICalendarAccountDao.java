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



/**
 * Interface defining operations for locating
 * {@link ICalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ICalendarAccountDao.java 1441 2009-12-17 19:09:21Z npblair $
 */
public interface ICalendarAccountDao {

	/**
	 * Retrieve an {@link ICalendarAccount} identified by it's
	 * username.
	 * May return null if no account can be found.
	 * 
	 * @param username
	 * @return
	 */
	ICalendarAccount getCalendarAccount(String username);
	
	/**
	 * Retrieve an {@link ICalendarAccount} identified by it's
	 * unique id.
	 * May return null if no account can be found.
	 * 
	 * @param uniqueId
	 * @return
	 */
	ICalendarAccount getCalendarAccountByUniqueId(String uniqueId);
	
}
