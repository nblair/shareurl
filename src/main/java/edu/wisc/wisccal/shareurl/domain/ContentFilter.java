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
/**
 * 
 */

package edu.wisc.wisccal.shareurl.domain;

import java.util.List;

/**
 * Interface describing share preferences that filter content.
 * 
 * @author Nicholas Blair
 */
public interface ContentFilter {

	/**
	 * 
	 * @return the name of the calendar property this filter is intended to inspect
	 */
	String getPropertyName();
	
	/**
	 * 
	 * @return the content that will result in a "match"
	 */
	List<String> getMatchValues();
}
