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

/**
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: InvalidPropertyValueException.java 1441 2009-12-17 19:09:21Z npblair $
 */
public class InvalidPropertyValueException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/**
	 * 
	 */
	public InvalidPropertyValueException() {
	}

	/**
	 * @param message
	 */
	public InvalidPropertyValueException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidPropertyValueException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidPropertyValueException(String message, Throwable cause) {
		super(message, cause);
	}

}
