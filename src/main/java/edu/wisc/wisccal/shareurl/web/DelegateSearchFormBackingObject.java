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

import java.io.Serializable;

/**
 * Form backing object for delegate search form.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateSearchFormBackingObject.java 1441 2009-12-17 19:09:21Z npblair $
 */
public class DelegateSearchFormBackingObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	private String searchText;
	private String delegateName;
	private boolean returnForAutocomplete = false;
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}
	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	/**
	 * @return the delegateName
	 */
	public String getDelegateName() {
		return delegateName;
	}
	/**
	 * @param delegateName the delegateName to set
	 */
	public void setDelegateName(String delegateName) {
		this.delegateName = delegateName;
	}
	/**
	 * @return the returnForAutocomplete
	 */
	public boolean isReturnForAutocomplete() {
		return returnForAutocomplete;
	}
	/**
	 * @param returnForAutocomplete the returnForAutocomplete to set
	 */
	public void setReturnForAutocomplete(boolean returnForAutocomplete) {
		this.returnForAutocomplete = returnForAutocomplete;
	}
	
	
}
