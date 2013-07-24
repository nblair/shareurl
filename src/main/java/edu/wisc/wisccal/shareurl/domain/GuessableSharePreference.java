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
package edu.wisc.wisccal.shareurl.domain;


/**
 * @author Nicholas Blair
 */
public class GuessableSharePreference extends AbstractSharePreference {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6705182554381328964L;

	/**
	 * 
	 */
	public static final String GUESSABLE = "GUESSABLE";
	/**
	 * 
	 */
	public static final String GUESS_DISPLAYNAME = "Public";
	
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getType()
	 */
	@Override
	public String getType() {
		return GUESSABLE;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getKey()
	 */
	@Override
	public String getKey() {
		return GUESSABLE;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getValue()
	 */
	@Override
	public String getValue() {
		return Boolean.TRUE.toString();
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return GUESS_DISPLAYNAME;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public boolean participatesInFiltering() {
		return false;
	}
}
