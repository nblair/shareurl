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
package edu.wisc.wisccal.shareurl.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: FreeBusyReadRequestDetailsTest.java 1442 2009-12-18 18:24:26Z npblair $
 */
public class FreeBusyReadRequestDetailsTest {

	@Test
	public void testEmpty() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
		FreeBusyReadRequestDetails details = new FreeBusyReadRequestDetails(request);
		Assert.assertEquals("", details.getShareKey());
		Assert.assertEquals(FreeBusyReadRequestDetails.DEFAULT_FORMAT, details.getFormat());
		Assert.assertEquals(42, details.getPeriod().getDuration().getDays());
	}
	
	@Test
	public void testShareKey() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/12345abcde");
		FreeBusyReadRequestDetails details = new FreeBusyReadRequestDetails(request);
		Assert.assertEquals("12345abcde", details.getShareKey());
		Assert.assertEquals(FreeBusyReadRequestDetails.DEFAULT_FORMAT, details.getFormat());
		Assert.assertEquals(42, details.getPeriod().getDuration().getDays());
	}
	
	@Test
	public void testInvalidStart() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/12345abcde");
		request.setParameter("start", "invalid");
		try {
			new FreeBusyReadRequestDetails(request);
			Assert.fail("FreeBusyParameterFormatException not thrown for invalid start");
		} catch (FreeBusyParameterFormatException e) {
			//success
		}
	}
	
	@Test
	public void testValidStart() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/12345abcde");
		request.setParameter("start", "2009-12-01T00:00:00-0000");
		FreeBusyReadRequestDetails details = new FreeBusyReadRequestDetails(request);
		Assert.assertEquals("12345abcde", details.getShareKey());
		Date expectedDate = makeDateTime("20091201-0000-0000");
		Assert.assertEquals(expectedDate, details.getStartDate());
	}
	
	@Test
	public void testInvalidEnd() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/12345abcde");
		request.setParameter("end", "invalid");
		try {
			new FreeBusyReadRequestDetails(request);
			Assert.fail("FreeBusyParameterFormatException not thrown for invalid end");
		} catch (FreeBusyParameterFormatException e) {
			//success
		}
	}
	
	@Test
	public void testValidEnd() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/12345abcde");
		request.setParameter("end", "2009-12-01T00:00:00-0000");
		FreeBusyReadRequestDetails details = new FreeBusyReadRequestDetails(request);
		Assert.assertEquals("12345abcde", details.getShareKey());
		Date expectedDate = makeDateTime("20091201-0000-0000");
		Assert.assertEquals(expectedDate, details.getEndDate());
	}
	
	@Test
	public void testEndAndPeriod() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/12345abcde");
		request.setParameter("end", "invalid");
		request.setParameter("period", "otherinvalid");
		try {
			new FreeBusyReadRequestDetails(request);
			Assert.fail("FreeBusyParameterFormatException not thrown for both period and end");
		} catch (FreeBusyParameterFormatException e) {
			//success
		}
	}
	
	@Test
	public void testInterpretedEnd() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/12345abcde");
		request.setParameter("start", "2009-12-01T00:00:00-0000");
		request.setParameter("period", "P7D");
		FreeBusyReadRequestDetails details = new FreeBusyReadRequestDetails(request);
		Assert.assertEquals("12345abcde", details.getShareKey());
		Date expectedStart = makeDateTime("20091201-0000-0000");
		Assert.assertEquals(expectedStart, details.getStartDate());
		Date expectedEnd = makeDateTime("20091208-0000-0000");
		Assert.assertEquals(expectedEnd, details.getEndDate());
	}
	
	private java.util.Date makeDateTime(String value) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmZ");
		return DateUtils.truncate(df.parse(value), Calendar.MINUTE);
	}
}
