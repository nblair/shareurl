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

package edu.wisc.wisccal.shareurl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.time.DateUtils;

/**
 * @author Nicholas Blair
 */
public class Tests {

	/**
	 * Create a date using the format "yyyyMMdd-HHmm".
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date makeDateTime(String value) {
		return makeDateTime(value, "yyyyMMdd-HHmm");
	}
	/**
	 * Create a date using the value and the specified format.
	 * The returned date will be truncated to the Minute.
	 * 
	 * @see DateUtils#truncate(java.util.Date, int)
	 * @param value
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date makeDateTime(String value, String format) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		try {
			return DateUtils.truncate(df.parse(value), java.util.Calendar.MINUTE);
		} catch (ParseException e) {
			throw new IllegalArgumentException("failed to makeDateTime for " + value + " and format " + format, e);
		}
	}
}
