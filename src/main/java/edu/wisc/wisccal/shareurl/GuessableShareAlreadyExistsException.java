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
package edu.wisc.wisccal.shareurl;

/**
 * {@link Exception} raised when a valid "guessable" share already exists
 * and an attempt to create another is made.
 * 
 * @author Nicholas Blair
 */
public class GuessableShareAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8486905820036319786L;

	/**
	 * 
	 */
	public GuessableShareAlreadyExistsException() {
	}

	/**
	 * @param message
	 */
	public GuessableShareAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GuessableShareAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GuessableShareAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
