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
package edu.wisc.wisccal.shareurl.web;

import java.util.Date;

/**
 * @author Nicholas Blair
 */
public interface IShareRequestDetails {

	/**
	 * Return the "key" that identifies the share targeted by this request.
	 * 
	 * @return the "key" that identifies the share targeted by this request
	 */
	public String getShareKey();
	/**
	 * 
	 * @return the start timestamp of the date range targeted by this request
	 */
	public Date getStartDate();
	/**
	 * 
	 * @return the end timestamp of the date range targeted by this request
	 */
	public Date getEndDate();
	/**
	 * A "public" shareurl is the shareurl that includes the account owner's email address.
	 * 
	 * @return true if this request is targeting the account's "public" shareurl.
	 */
	public boolean isPublicUrl();
}
