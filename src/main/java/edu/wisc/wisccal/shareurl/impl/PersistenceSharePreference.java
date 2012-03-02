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

import edu.wisc.wisccal.shareurl.domain.ISharePreference;

/**
 * Persistence representation of an {@link ISharePreference}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistenceSharePreference.java 1677 2010-02-08 18:31:01Z npblair $
 */
public final class PersistenceSharePreference  {

	private String shareKey;
	private String preferenceType;
	private String preferenceKey;
	private String preferenceValue;
	
	/**
	 * @return the shareKey
	 */
	public String getShareKey() {
		return shareKey;
	}

	/**
	 * @param shareKey the shareKey to set
	 */
	public void setShareKey(String shareKey) {
		this.shareKey = shareKey;
	}
	
	/**
	 * @return the preferenceType
	 */
	public String getPreferenceType() {
		return preferenceType;
	}

	/**
	 * @param preferenceType the preferenceType to set
	 */
	public void setPreferenceType(String preferenceType) {
		this.preferenceType = preferenceType;
	}

	/**
	 * @return the preferenceKey
	 */
	public String getPreferenceKey() {
		return preferenceKey;
	}

	/**
	 * @param preferenceKey the preferenceKey to set
	 */
	public void setPreferenceKey(String preferenceKey) {
		this.preferenceKey = preferenceKey;
	}

	/**
	 * @return the preferenceValue
	 */
	public String getPreferenceValue() {
		return preferenceValue;
	}

	/**
	 * @param preferenceValue the preferenceValue to set
	 */
	public void setPreferenceValue(String preferenceValue) {
		this.preferenceValue = preferenceValue;
	}


}
