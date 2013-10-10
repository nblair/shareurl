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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PropertyMatchPreferenceTest.java 1677 2010-02-08 18:31:01Z npblair $
 */
public class PropertyMatchPreferenceTest {

	@Test
	public void testMatchSummary() throws Exception {
		PropertyMatchPreference p = new PropertyMatchPreference(Summary.SUMMARY, "soccer");
		Assert.assertEquals(PropertyMatchPreference.PROPERTY_MATCH, p.getType());
		Assert.assertEquals(Summary.SUMMARY, p.getKey());
		Assert.assertEquals("soccer", p.getValue());
		
		VEvent eventMatch1 = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		Assert.assertTrue(p.matches(eventMatch1));
		VEvent eventMatch2 = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "game of soccer");
		Assert.assertTrue(p.matches(eventMatch2));
		
		VEvent nomatch = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "basketball");
		Assert.assertFalse(p.matches(nomatch));
	}
	
	
	
	private java.util.Date makeDateTime(String value) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		return DateUtils.truncate(df.parse(value), Calendar.MINUTE);
	}
}
