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
package edu.wisc.wisccal.shareurl.ical;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test harness for {@link VEventComparator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VEventComparatorTest.java 1441 2009-12-17 19:09:21Z npblair $
 */
public class VEventComparatorTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		VEvent dayEvent = new VEvent(new DateTime(makeDateTime("20091201-0900")), new DateTime(makeDateTime("20091201-1000")), "first event");
		VEvent other = new VEvent(new DateTime(makeDateTime("20091201-1000")), new DateTime(makeDateTime("20091201-1100")), "second event");
		
		VEventComparator comparator = new VEventComparator();
		int result = comparator.compare(dayEvent, other);
		Assert.assertTrue(result < 0);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSameStart() throws Exception {
		VEvent dayEvent = new VEvent(new DateTime(makeDateTime("20091201-0900")), new DateTime(makeDateTime("20091201-1000")), "first event");
		VEvent other = new VEvent(new DateTime(makeDateTime("20091201-0900")), new DateTime(makeDateTime("20091201-1100")), "second event");
		
		VEventComparator comparator = new VEventComparator();
		int result = comparator.compare(dayEvent, other);
		Assert.assertTrue(result < 0);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSameTimes() throws Exception {
		VEvent dayEvent = new VEvent(new DateTime(makeDateTime("20091201-0900")), new DateTime(makeDateTime("20091201-1000")), "a");
		VEvent other = new VEvent(new DateTime(makeDateTime("20091201-0900")), new DateTime(makeDateTime("20091201-1000")), "b");
		
		VEventComparator comparator = new VEventComparator();
		int result = comparator.compare(dayEvent, other);
		Assert.assertTrue(result < 0);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSameAll() throws Exception {
		VEvent dayEvent = new VEvent(new DateTime(makeDateTime("20091201-0900")), new DateTime(makeDateTime("20091201-1000")), "a");
		VEvent other = new VEvent(new DateTime(makeDateTime("20091201-0900")), new DateTime(makeDateTime("20091201-1000")), "a");
		
		VEventComparator comparator = new VEventComparator();
		int result = comparator.compare(dayEvent, other);
		Assert.assertTrue(result == 0);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOracleDayEvent() throws Exception {
		
		VEvent dayEvent = new VEvent(new Date("20091210"), new Date("20091211"), "day event summary");
		dayEvent.getProperties().add(new XProperty("X-ORACLE-EVENTTYPE", "DAY EVENT"));
		
		// the 2 lines are equivalent
		//VEvent other = new VEvent(new DateTime(makeDateTime("20091210-1900")), new DateTime(makeDateTime("20091211-0000")), "other event");
		VEvent other = new VEvent(new DateTime("20091210T010000Z"), new DateTime("20091210T060000Z"), "other event");
		
		other.getProperties().add(new XProperty("X-ORACLE-EVENTTYPE", "APPOINTMENT"));
		
		VEventComparator comparator = new VEventComparator();
		int result = comparator.compare(dayEvent, other);
		Assert.assertTrue(result > 0);
	}
	
	private java.util.Date makeDateTime(String value) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		return DateUtils.truncate(df.parse(value), Calendar.MINUTE);
	}
}
