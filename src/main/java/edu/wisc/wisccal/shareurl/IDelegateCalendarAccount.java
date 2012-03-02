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
 * Extenstion of {@link ICalendarAccount} that represents a (potentially non-human)
 * calendar account that can be administered by another {@link ICalendarAccount} (which
 * is the return value of {@link #getAccountOwner()}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IDelegateCalendarAccount.java 1470 2010-01-07 19:11:29Z npblair $
 */
public interface IDelegateCalendarAccount extends ICalendarAccount {

	/**
	 * 
	 * @return the {@link ICalendarAccount} responsible for administering this delegate account.
	 */
	ICalendarAccount getAccountOwner();
}
