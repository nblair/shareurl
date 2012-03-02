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

import java.util.List;


/**
 * Extension of {@link ICalendarAccountDao} that adds operations for
 * looking up and searching for {@link IDelegateCalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IDelegateCalendarAccountDao.java 1470 2010-01-07 19:11:29Z npblair $
 */
public interface IDelegateCalendarAccountDao extends ICalendarAccountDao {

	/**
	 * Return a {@link List} of {@link IDelegateCalendarAccount} that correspond
	 * to the searchText argument.
	 * Implementations may decide internally which account attributes are used
	 * to map searchText. searchText will contain the asterisk ('*') character
	 * to serve as a wildcard.
	 * 
	 * Implementations of this method must never return null; return an empty list if
	 * no matches can be found.
	 * @param searchText
	 * @param owner
	 * @return
	 */
	List<IDelegateCalendarAccount> searchForDelegates(String searchText, ICalendarAccount owner);
	
	/**
	 * Return the specified {@link IDelegateCalendarAccount} by name if the specified
	 * {@link ICalendarAccount} argument is the designated account owner.
	 * 
	 * @param accountName
	 * @param owner
	 * @return
	 */
	IDelegateCalendarAccount getDelegate(String accountName, ICalendarAccount owner);
	
	/**
	 * Return the specified {@link IDelegateCalendarAccount} by unique id if the specified
	 * {@link ICalendarAccount} argument is the designated account owner.
	 * 
	 * @param accountName
	 * @param owner
	 * @return
	 */
	IDelegateCalendarAccount getDelegateByUniqueId(String accountUniqueId, ICalendarAccount owner);
}
