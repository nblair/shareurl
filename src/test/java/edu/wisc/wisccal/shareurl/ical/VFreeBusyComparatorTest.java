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

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VFreeBusy;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link VFreeBusyComparator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VFreeBusyComparatorTest.java 1722 2010-02-15 22:01:26Z npblair $
 */
public class VFreeBusyComparatorTest {

	@Test
	public void testControl() throws Exception {
		VFreeBusy left = new VFreeBusy(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")));
		VFreeBusy right = new VFreeBusy(new DateTime(makeDateTime("20100210-1000")), new DateTime(makeDateTime("20100210-1100")));
		
		VFreeBusyComparator comparator = new VFreeBusyComparator();
		Assert.assertTrue(comparator.compare(left, right) < 0);
	}
	
	@Test
	public void testEquals() throws Exception {
		VFreeBusy left = new VFreeBusy(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")));
		VFreeBusy right = new VFreeBusy(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")));
		
		VFreeBusyComparator comparator = new VFreeBusyComparator();
		Assert.assertTrue(comparator.compare(left, right) == 0);
	}
	
	@Test
	public void testSameStart() throws Exception {
		VFreeBusy left = new VFreeBusy(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")));
		VFreeBusy right = new VFreeBusy(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1100")));
		
		VFreeBusyComparator comparator = new VFreeBusyComparator();
		Assert.assertTrue(comparator.compare(left, right) < 0);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	private java.util.Date makeDateTime(String value) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		return DateUtils.truncate(df.parse(value), java.util.Calendar.MINUTE);
	}
}
