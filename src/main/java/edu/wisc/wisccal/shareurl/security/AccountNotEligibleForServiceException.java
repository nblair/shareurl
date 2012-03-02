/*******************************************************************************
 *  Copyright 2008-2010 The Board of Regents of the University of Wisconsin System.
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
package edu.wisc.wisccal.shareurl.security;

import edu.wisc.wisccal.shareurl.ICalendarAccount;

/**
 * May be thrown when encountering an {@link ICalendarAccount} that returns
 * false for it's {@link ICalendarAccount#isEligible()} function.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AccountNotEligibleForServiceException.java $
 */
public class AccountNotEligibleForServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2870192654282919013L;

	/**
	 * 
	 */
	public AccountNotEligibleForServiceException() {
	}

	/**
	 * @param message
	 */
	public AccountNotEligibleForServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AccountNotEligibleForServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AccountNotEligibleForServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
