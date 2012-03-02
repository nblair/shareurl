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
package edu.wisc.wisccal.shareurl.impl;

import org.springframework.beans.factory.annotation.Required;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.ICalendarAccountDao;
import edu.wisc.wisccal.shareurl.IDelegateCalendarAccountDao;

/**
 * Composite {@link ICalendarAccountDao} implementation that requires
 * an {@link ICalendarAccountDao} and an {@link IDelegateCalendarAccountDao}.
 * 
 * All methods consult the {@link ICalendarAccountDao} first, returning any non-null values immediately.
 * If the first method call returned null, the {@link IDelegateCalendarAccountDao} is hit next, and whatever
 * it returns is then returned.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CompositeCalendarAccountDaoImpl.java 1441 2009-12-17 19:09:21Z npblair $
 */
public final class CompositeCalendarAccountDaoImpl implements ICalendarAccountDao {

	private ICalendarAccountDao calendarAccountDao;
	private IDelegateCalendarAccountDao delegateAccountDao;
	
	
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Required
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param delegateAccountDao the delegateAccountDao to set
	 */
	@Required
	public void setDelegateAccountDao(IDelegateCalendarAccountDao delegateAccountDao) {
		this.delegateAccountDao = delegateAccountDao;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccountDao#getCalendarAccount(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccount(String username) {
		ICalendarAccount result = calendarAccountDao.getCalendarAccount(username);
		if(null != result) {
			return result;
		} else {
			return delegateAccountDao.getCalendarAccount(username);
		}
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccountDao#getCalendarAccountByUniqueId(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccountByUniqueId(String uniqueId) {
		ICalendarAccount result = calendarAccountDao.getCalendarAccountByUniqueId(uniqueId);
		if(null != result) {
			return result;
		} else {
			return delegateAccountDao.getCalendarAccountByUniqueId(uniqueId);
		}
	}
	
}
