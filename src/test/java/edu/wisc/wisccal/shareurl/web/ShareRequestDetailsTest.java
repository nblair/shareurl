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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.jasig.schedassist.model.CommonDateOperations;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import edu.wisc.wisccal.shareurl.web.ShareRequestDetails.Client;

/**
 * Test harness for {@link ShareRequestDetails}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ShareRequestDetailsTest.java 3437 2011-10-25 15:29:17Z npblair $
 */
public class ShareRequestDetailsTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestConstructorFirefox() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/123456789/dr(-10,140)");
		request.addParameter("ical", "");
		request.addHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.1.7) Gecko/20100106 Ubuntu/9.10 (karmic) Firefox/3.5.7");
		ShareRequestDetails details = new ShareRequestDetails(request);
		Date [] expectedDates = createExpectedDates(-10, 140);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-10,140)", details.getDatePhrase());
		assertNull(details.getEventId());
		assertEquals("123456789", details.getShareKey());
		assertFalse(details.requiresBreakRecurrence());
		assertFalse(details.requiresConvertClass());
		assertEquals(ShareDisplayFormat.ICAL, details.getDisplayFormat());
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestConstructorGooglebot() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/123456789/dr(-10,140)");
		request.addParameter("ical", "");
		request.addHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		Date [] expectedDates = createExpectedDates(-10, 140);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-10,140)", details.getDatePhrase());
		assertNull(details.getEventId());
		assertEquals("123456789", details.getShareKey());
		assertFalse(details.requiresBreakRecurrence());
		assertTrue(details.requiresConvertClass());
		assertEquals(ShareDisplayFormat.ICAL, details.getDisplayFormat());
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestConstructoriphone() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/123456789/dr(-10,140)");
		request.addParameter("ical", "");
		request.addHeader("User-Agent", "DataAccess/1.0 (7E18)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		Date [] expectedDates = createExpectedDates(-10, 140);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-10,140)", details.getDatePhrase());
		assertNull(details.getEventId());
		assertEquals("123456789", details.getShareKey());
		assertTrue(details.requiresBreakRecurrence());
		assertFalse(details.requiresConvertClass());
		assertEquals(ShareDisplayFormat.ICAL, details.getDisplayFormat());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestConstructoriOS5() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/123456789/dr(-10,140)");
		request.addParameter("ical", "");
		request.addHeader("User-Agent", "Mozilla/5.0 (iPod; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
		ShareRequestDetails details = new ShareRequestDetails(request);
		Date [] expectedDates = createExpectedDates(-10, 140);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-10,140)", details.getDatePhrase());
		assertNull(details.getEventId());
		assertEquals("123456789", details.getShareKey());
		assertTrue(details.requiresBreakRecurrence());
		assertFalse(details.requiresConvertClass());
		assertEquals(ShareDisplayFormat.ICAL, details.getDisplayFormat());
	}
	
	@Test
	public void testRequestConstructoriCal10_7() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/123456789/dr(-10,140)");
		request.addParameter("ical", "");
		request.addHeader("User-Agent", "CalendarStore/5.0.1 (1139.14); iCal/5.0.1 (1547.4); Mac OS X/10.7.2 (11C74) ");
		ShareRequestDetails details = new ShareRequestDetails(request);
		Date [] expectedDates = createExpectedDates(-10, 140);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-10,140)", details.getDatePhrase());
		assertNull(details.getEventId());
		assertEquals("123456789", details.getShareKey());
		assertTrue(details.requiresBreakRecurrence());
		assertFalse(details.requiresConvertClass());
		assertEquals(ShareDisplayFormat.ICAL, details.getDisplayFormat());
	}
	
	
	/**
	 * pass "/" into constructor, expect defaults for dates and null for event and share ids.
	 * @throws Exception
	 */
	@Test
	public void testDefaults() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(0, 0);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals(ShareRequestDetails.DEFAULT_DATE_PHRASE, details.getDatePhrase());
		assertNull(details.getEventId());
		assertEquals("", details.getShareKey());
		assertFalse(details.requiresBreakRecurrence());
		assertFalse(details.requiresConvertClass());
		assertEquals(ShareDisplayFormat.HTML, details.getDisplayFormat());
	}
	
	/**
	 * pass "/someshareid", expect defaults for dates, "someshareid" for share id, and null for event id.
	 * @throws Exception
	 */
	@Test
	public void testJustShareId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(0, 0);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals(ShareRequestDetails.DEFAULT_DATE_PHRASE, details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	/**
	 * pass "someshareid", expect defaults for dates, "someshareid" for share id, and null for event id.
	 * @throws Exception
	 */
	@Test
	public void testJustShareIdNoPrecedingSlash() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "someshareid");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(0, 0);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals(ShareRequestDetails.DEFAULT_DATE_PHRASE, details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * pass "/someshareid/dr(1,1)", expect proper dates, "someshareid" for share id, and null for event id.
	 * @throws Exception
	 */
	@Test
	public void testShareIdAndValidDr() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(1,1)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(1, 1);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(1,1)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * pass "/someshareid/dr(1,1)/someeventid", expect proper dates, "someshareid" for share id, and "someeventid" for event id.
	 * @throws Exception
	 */
	@Test
	public void testShareIdValidDrAndEventId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(1,1)/someeventid");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(1, 1);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(1,1)", details.getDatePhrase());
		
		assertEquals("someeventid", details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	/**
	 * pass "/someshareid/dr(1,1)/someeventid", expect proper dates, "someshareid" for share id, and "someeventid" for event id.
	 * @throws Exception
	 */
	@Test
	public void testShareIdValidDrEventIdAndRecurrenceId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(1,1)/someeventid/recurid");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(1, 1);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(1,1)", details.getDatePhrase());
		
		assertEquals("someeventid", details.getEventId());
		assertEquals("someshareid", details.getShareKey());
		assertEquals("recurid", details.getRecurrenceId());
	}	
	
	/**
	 * pass malformed date range "/someshareid/d(1,2)", expect defaults for date.
	 * d(1,2) will actually show up in event id.
	 * @throws Exception
	 */
	@Test
	public void testBadFormatDateRange() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/d(1,2)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(0, 0);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals(ShareRequestDetails.DEFAULT_DATE_PHRASE, details.getDatePhrase());
		
		assertEquals("d(1,2)",details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * pass malformed date range "/someshareid/d(1,2)/someeventid", expect defaults for date.
	 * Expect someeventid to come through properly.
	 * @throws Exception
	 */
	@Test
	public void testBadFormatDateRangeIncludeEventId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/d(1,2)/someeventid");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(0, 0);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(0,0)", details.getDatePhrase());
		// since d(1,2) doesn't match the DR pattern, this token will get interpreted as the event Id
		assertEquals("d(1,2)", details.getEventId());
		assertEquals("someeventid", details.getRecurrenceId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * Test dr(-1,1)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeThroughZero() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(-1,1)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(-1, 1);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-1,1)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
		
	}
	
	/**
	 * Test dr(1,10)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeBothPositive() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(1,10)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(1, 10);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(1,10)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * Test dr(-20,-1)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeBothNegative() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(-20,-1)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(-20, -1);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-20,-1)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}
	
	/**
	 * pass(-241,-181), expect (-241,-181)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeLow() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(-241,-181)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(-241, -181);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-241,-181)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}
	
	/**
	 * pass(-300,-100), expect (-300,-120)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeLowExceedRange() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(-300,-100)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(-300, -120);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-300,-120)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}
	
	/**
	 * pass(121,181), expect (121,181)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeHigh() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(121,181)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(121,181);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(121,181)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}
	
	/**
	 * pass(100,300), expect (100,220)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeHighExceedRange() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(100,300)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(100, 280);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(100,280)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}
	
	/**
	 * pass(-91,91), expect (-91,89)
	 * @throws Exception
	 */
	@Test
	public void testDateRangeWidest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid/dr(-91,91)");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date [] expectedDates = createExpectedDates(-91,89);
		assertEquals(expectedDates[0], details.getStartDate());
		assertEquals(expectedDates[1], details.getEndDate());
		assertEquals("dr(-91,89)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartParameterNoEndParameter() throws Exception {
		// adding a query String to the second argument for this constructor does not seem to set the parameter accordingly
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.setParameter("start", "2012-01-01");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date expectedStart = CommonDateOperations.parseDatePhrase("20120101");
		Date expectedEnd = CommonDateOperations.endOfDay(expectedStart);
		assertEquals(expectedStart, details.getStartDate());
		assertEquals(expectedEnd, details.getEndDate());
		assertEquals("dr(0,0)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartParameterAlternateFormat() throws Exception {
		// adding a query String to the second argument for this constructor does not seem to set the parameter accordingly
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.setParameter("start", "20120101");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date expectedStart = CommonDateOperations.parseDatePhrase("20120101");
		Date expectedEnd = CommonDateOperations.endOfDay(expectedStart);
		assertEquals(expectedStart, details.getStartDate());
		assertEquals(expectedEnd, details.getEndDate());
		assertEquals("dr(0,0)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartAndEndParameter() throws Exception {
		// adding a query String to the second argument for this constructor does not seem to set the parameter accordingly
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.setParameter("start", "2012-01-01");
		request.setParameter("end", "2012-01-09");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date expectedStart = CommonDateOperations.parseDatePhrase("20120101");
		Date expectedEnd = CommonDateOperations.endOfDay(CommonDateOperations.parseDatePhrase("20120109"));
		assertEquals(expectedStart, details.getStartDate());
		assertEquals(expectedEnd, details.getEndDate());
		assertEquals("dr(0,0)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStartAndInvalidEndParameter() throws Exception {
		// adding a query String to the second argument for this constructor does not seem to set the parameter accordingly
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.setParameter("start", "2012-01-01");
		request.setParameter("end", "2012-0109");
		ShareRequestDetails details = new ShareRequestDetails(request);
		
		Date expectedStart = CommonDateOperations.parseDatePhrase("20120101");
		// since end parameter is invalid, should see end of day start
		Date expectedEnd = CommonDateOperations.endOfDay(expectedStart);
		assertEquals(expectedStart, details.getStartDate());
		assertEquals(expectedEnd, details.getEndDate());
		assertEquals("dr(0,0)", details.getDatePhrase());
		
		assertNull(details.getEventId());
		assertEquals("someshareid", details.getShareKey());
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDisplayFormatDefault() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		ShareRequestDetails details = new ShareRequestDetails(request);
		assertEquals(ShareDisplayFormat.HTML, details.getDisplayFormat());
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDisplayFormatRss() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.addParameter("rss", "");
		ShareRequestDetails details = new ShareRequestDetails(request);
		assertEquals(ShareDisplayFormat.RSS, details.getDisplayFormat());
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDisplayFormatICal() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.addParameter("ical", "");
		ShareRequestDetails details = new ShareRequestDetails(request);
		assertEquals(ShareDisplayFormat.ICAL, details.getDisplayFormat());
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDisplayFormatICalAsText() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.addParameter("ical", "");
		request.addParameter("asText", "");
		ShareRequestDetails details = new ShareRequestDetails(request);
		assertEquals(ShareDisplayFormat.ICAL_ASTEXT, details.getDisplayFormat());
	}
	
	/**
	 * Assert Internet Explorer, Firefox user-agents return {@link Client#OTHER}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPredictClientDefault() throws Exception {
		String firefoxUserAgent = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.1.7) Gecko/20100106 Ubuntu/9.10 (karmic) Firefox/3.5.7";
		assertEquals(Client.OTHER, ShareRequestDetails.predictClientFromUserAgent(firefoxUserAgent));
		
		String ieUserAgent = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)";
		assertEquals(Client.OTHER, ShareRequestDetails.predictClientFromUserAgent(ieUserAgent));
	}
	
	/**
	 * Assert Apple iCal user agent detected.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPredictClientApple() throws Exception {
		String userAgent = "DAVKit/3.0.6 (661); CalendarStore/3.0.8 (860); iCal/3.0.8 (1287); Mac OS X/10.5.8 (9L30)";
		assertEquals(Client.APPLE, ShareRequestDetails.predictClientFromUserAgent(userAgent));
	}
	
	/**
	 * Assert Apple iPhone/iPod Touch user agent detected.
	 * @throws Exception
	 */
	@Test
	public void testPredictClientIpod() throws Exception {
		String userAgent = "DataAccess/1.0 (7E18)";
		assertEquals(Client.APPLE, ShareRequestDetails.predictClientFromUserAgent(userAgent));
	}
	@Test
	public void testPredictClientiOS5() {
		String userAgent = "iOS/5.0 (9A334) Preferences/1.0";
		assertEquals(Client.APPLE, ShareRequestDetails.predictClientFromUserAgent(userAgent));
		userAgent = "iOS/5.0 (9A334) dataaccessd/1.0 ";
		assertEquals(Client.APPLE, ShareRequestDetails.predictClientFromUserAgent(userAgent));
	}
	/**
	 * Assert Mozilla Thunderbird Lightning plugin properly detected.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPredictClientMozilla() throws Exception {
		String userAgent = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.8.1.23) Gecko/20090817 Lightning/0.9 Thunderbird/2.0.0.23";
		assertEquals(Client.MOZILLA, ShareRequestDetails.predictClientFromUserAgent(userAgent));
	}
	
	/**
	 * Assert Mozilla Sunbird client properly detected.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPredictClientSunbird() throws Exception {
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13pre) Gecko/20080331 Sunbird/0.8";
		assertEquals(Client.MOZILLA, ShareRequestDetails.predictClientFromUserAgent(userAgent));
	}
	
	/**
	 * Assert Google user-agent properly detected.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPredictClientGoogle() throws Exception {
		String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
		assertEquals(Client.GOOGLE, ShareRequestDetails.predictClientFromUserAgent(userAgent));
	}
	
	@Test
	public void testCompatEmpty() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.addParameter("ical", "");
		request.addParameter("compat", "");
		ShareRequestDetails details = new ShareRequestDetails(request);
		assertFalse(details.requiresBreakRecurrence());
		assertFalse(details.requiresNoRecurrence());
		assertFalse(details.requiresConvertClass());
	}
	
	@Test
	public void testCompatNr() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.addParameter("ical", "");
		request.addParameter("compat", "nr");
		ShareRequestDetails details = new ShareRequestDetails(request);
		assertFalse(details.requiresBreakRecurrence());
		assertTrue(details.requiresNoRecurrence());
		assertFalse(details.requiresConvertClass());
	}
	
	@Test
	public void testMultiValueCompat() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/someshareid");
		request.addParameter("ical", "");
		request.addParameter("compat", "nr");
		request.addParameter("compat", "cc");
		ShareRequestDetails details = new ShareRequestDetails(request);
		assertFalse(details.requiresBreakRecurrence());
		assertTrue(details.requiresNoRecurrence());
		assertTrue(details.requiresConvertClass());
	}
	
	
	/**
	 * Convenience method to calculate expected {@link Date} objects.
	 * @param first
	 * @param second
	 * @return
	 */
	private Date[] createExpectedDates(final int first, final int second) {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		// one to represent the end of the day
		// use clone to ensure that start and end are the same date (DD-MM-YYYY)
		Calendar end = (Calendar) start.clone();	
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 0);
		start.add(Calendar.DATE, first);
		end.add(Calendar.DATE, second);
		return new Date[] { start.getTime(), end.getTime() };
	}
}
