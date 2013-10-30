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
package edu.wisc.wisccal.shareurl.domain;


/**
 * Subclass of {@link AbstractSharePreference} that
 * marks the attached share as "free busy only".
 *  
 * @author Nicholas Blair
 */
public final class FreeBusyPreference extends AbstractSharePreference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	public static final String FREE_BUSY = "FREE_BUSY";
	public static final String FB_DISPLAYNAME = "Free-Busy Only";

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.AbstractSharePreference#getKey()
	 */
	@Override
	public final String getKey() {
		return FREE_BUSY;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ISharePreference#getType()
	 */
	@Override
	public final String getType() {
		return FREE_BUSY;
	}
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ISharePreference#getValue()
	 */
	@Override
	public String getValue() {
		return Boolean.TRUE.toString();
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public final boolean participatesInFiltering() {
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return FB_DISPLAYNAME;
	}
	
	
	
}
