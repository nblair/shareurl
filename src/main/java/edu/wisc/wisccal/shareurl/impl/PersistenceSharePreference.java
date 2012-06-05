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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((preferenceKey == null) ? 0 : preferenceKey.hashCode());
		result = prime * result
				+ ((preferenceType == null) ? 0 : preferenceType.hashCode());
		result = prime * result
				+ ((preferenceValue == null) ? 0 : preferenceValue.hashCode());
		result = prime * result
				+ ((shareKey == null) ? 0 : shareKey.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersistenceSharePreference other = (PersistenceSharePreference) obj;
		if (preferenceKey == null) {
			if (other.preferenceKey != null)
				return false;
		} else if (!preferenceKey.equals(other.preferenceKey))
			return false;
		if (preferenceType == null) {
			if (other.preferenceType != null)
				return false;
		} else if (!preferenceType.equals(other.preferenceType))
			return false;
		if (preferenceValue == null) {
			if (other.preferenceValue != null)
				return false;
		} else if (!preferenceValue.equals(other.preferenceValue))
			return false;
		if (shareKey == null) {
			if (other.shareKey != null)
				return false;
		} else if (!shareKey.equals(other.shareKey))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersistenceSharePreference [shareKey=" + shareKey
				+ ", preferenceType=" + preferenceType + ", preferenceKey="
				+ preferenceKey + ", preferenceValue=" + preferenceValue + "]";
	}


}
