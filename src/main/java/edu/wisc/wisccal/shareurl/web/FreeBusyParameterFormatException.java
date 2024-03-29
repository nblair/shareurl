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
package edu.wisc.wisccal.shareurl.web;

/**
 * Thrown in the event any of the freebusy read url parameters do not match the 
 * expected format.
 * This exception should raise a 400 HTTP status code per the specification.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: FreeBusyParameterFormatException.java 1442 2009-12-18 18:24:26Z npblair $
 */
public class FreeBusyParameterFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/**
	 * 
	 */
	public FreeBusyParameterFormatException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FreeBusyParameterFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FreeBusyParameterFormatException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FreeBusyParameterFormatException(Throwable cause) {
		super(cause);
	}
	
}
