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

import org.jasig.schedassist.model.ICalendarAccount;

import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 * Interface for generating, retrieving, and destroying {@link Share}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IShareDao.java 1441 2009-12-17 19:09:21Z npblair $
 */
public interface IShareDao {

	/**
	 * Create a new {@link Share} for the specified {@link ICalendarAccount} with the 
	 * {@link SharePreferences}.
	 * 
	 * @param account
	 * @param preferences
	 * @return the persisted {@link Share}
	 */
	Share generateNewShare(ICalendarAccount account, SharePreferences preferences);
	
	/**
	 * Retrieve the VALID {@link Share}s for the {@link ICalendarAccount}.
	 * Never returns null; may return an empty {@link List}.
	 * 
	 * @param account
	 * @return
	 */
	List<Share> retrieveByOwner(ICalendarAccount account);
	
	/**
	 * Retrieve a VALID {@link Share} with the specified key, if any.
	 * May return null (if share is invalid or doesn't exist).
	 * 
	 * @param key
	 * @return
	 */
	Share retrieveByKey(String key);
	
	/**
	 * Revoke the specified {@link Share} (mark as INVALID), if it exists.
	 * 
	 * @param share
	 */
	void revokeShare(Share share);
}
