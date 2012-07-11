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
import net.fortuna.ical4j.model.property.Clazz;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OracleAccessLevelMatchPreferenceTest.java 1680 2010-02-08 18:48:38Z npblair $
 */
public class AccessClassificationMatchPreferenceTest {

	@Test
	public void testPublic() throws Exception {
		AccessClassificationMatchPreference p = new AccessClassificationMatchPreference(AccessClassification.PUBLIC);
		Assert.assertEquals(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, p.getType());
		Assert.assertEquals(Clazz.CLASS, p.getKey());
		Assert.assertEquals(Clazz.PUBLIC.getValue(), p.getValue());
		
		// an event with no CLASS property defaults to CLASS:PUBLIC
		VEvent eventNoClass = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		Assert.assertTrue(p.matches(eventNoClass));
		
		VEvent eventMatch1 = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		eventMatch1.getProperties().add(Clazz.PUBLIC);
		Assert.assertTrue(p.matches(eventMatch1));
		
		VEvent eventNoMatch = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		eventNoMatch.getProperties().add(Clazz.PRIVATE);
		Assert.assertFalse(p.matches(eventNoMatch));
	}
	
	@Test
	public void testConfidential() throws Exception {
		AccessClassificationMatchPreference p = new AccessClassificationMatchPreference(AccessClassification.CONFIDENTIAL);
		Assert.assertEquals(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, p.getType());
		Assert.assertEquals(Clazz.CLASS, p.getKey());
		Assert.assertEquals(Clazz.CONFIDENTIAL.getValue(), p.getValue());
		
		VEvent eventNoClass = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		Assert.assertFalse(p.matches(eventNoClass));
		
		VEvent eventMatch1 = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		eventMatch1.getProperties().add(Clazz.CONFIDENTIAL);
		Assert.assertTrue(p.matches(eventMatch1));
		
		VEvent eventNoMatch = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		eventNoMatch.getProperties().add(Clazz.PRIVATE);
		Assert.assertFalse(p.matches(eventNoMatch));
	}
	
	@Test
	public void testPrivate() throws Exception {
		AccessClassificationMatchPreference p = new AccessClassificationMatchPreference(AccessClassification.PRIVATE);
		Assert.assertEquals(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, p.getType());
		Assert.assertEquals(Clazz.CLASS, p.getKey());
		Assert.assertEquals(Clazz.PRIVATE.getValue(), p.getValue());
		
		VEvent eventNoClass = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		Assert.assertFalse(p.matches(eventNoClass));
		
		VEvent eventMatch1 = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		eventMatch1.getProperties().add(Clazz.PRIVATE);
		Assert.assertTrue(p.matches(eventMatch1));
		
		VEvent eventNoMatch = new VEvent(new DateTime(makeDateTime("20100208-1000")), new DateTime(makeDateTime("20100208-1030")), "soccer game");
		eventNoMatch.getProperties().add(Clazz.PUBLIC);
		Assert.assertFalse(p.matches(eventNoMatch));
	}
	
	private java.util.Date makeDateTime(String value) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		return DateUtils.truncate(df.parse(value), Calendar.MINUTE);
	}
}
