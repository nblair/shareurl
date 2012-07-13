/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
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

package edu.wisc.wisccal.shareurl.web;

import java.io.Serializable;

/**
 * Form backing object for generate-guessable web flow.
 * 
 * @author Nicholas Blair
 */
public class GuessableShareFormBackingObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2396055341877199702L;
	private boolean includePublic;
	private boolean includeConfidential;
	private boolean includePrivate;
	/**
	 * @return the includePublic
	 */
	public boolean isIncludePublic() {
		return includePublic;
	}
	/**
	 * @param includePublic the includePublic to set
	 */
	public void setIncludePublic(boolean includePublic) {
		this.includePublic = includePublic;
	}
	/**
	 * @return the includeConfidential
	 */
	public boolean isIncludeConfidential() {
		return includeConfidential;
	}
	/**
	 * @param includeConfidential the includeConfidential to set
	 */
	public void setIncludeConfidential(boolean includeConfidential) {
		this.includeConfidential = includeConfidential;
	}
	/**
	 * @return the includePrivate
	 */
	public boolean isIncludePrivate() {
		return includePrivate;
	}
	/**
	 * @param includePrivate the includePrivate to set
	 */
	public void setIncludePrivate(boolean includePrivate) {
		this.includePrivate = includePrivate;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GuessableShareFormBackingObject [includePublic="
				+ includePublic + ", includeConfidential="
				+ includeConfidential + ", includePrivate=" + includePrivate
				+ "]";
	}
	
	
}
