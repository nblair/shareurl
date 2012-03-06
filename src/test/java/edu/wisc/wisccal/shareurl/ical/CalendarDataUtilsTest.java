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
import java.util.Iterator;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test harness for {@link CalendarDataUtils}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarDataUtilsTest.java 1722 2010-02-15 22:01:26Z npblair $
 */
public class CalendarDataUtilsTest {

	/**
	 * Verify the {@link CalendarDataUtils#constructProperty(String, String)} properly creates
	 * a {@link Summary} property.
	 * @throws Exception
	 */
	@Test
	public void testConstructPropertySummary() throws Exception {
		Property prop = CalendarDataUtils.constructProperty(Summary.SUMMARY, "event summary");
		Assert.assertNotNull(prop);
		Assert.assertEquals(Summary.SUMMARY, prop.getName());
		Assert.assertEquals("event summary", prop.getValue());
	}
	
	/**
	 * Assert calendar with events already containing CLASS:PUBLIC are unchanged.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvertClassPublicControl() throws Exception {
		ComponentList components = new ComponentList();
		VEvent event = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "a");
		event.getProperties().add(Clazz.PUBLIC);
		
		components.add(event);
		
		Calendar original = new Calendar(components);
		original.getProperties().add(Version.VERSION_2_0);
		original.getProperties().add(new ProdId(CalendarDataUtils.SHAREURL_PROD_ID));
		
		Calendar result = CalendarDataUtils.convertClassPublic(original);
		Assert.assertEquals(result, original);
	}
	
	/**
	 * Assert calendar with events with CLASS properties not equal to PUBLIC are appropriately
	 * modified by {@link CalendarDataUtils#convertClassPublic(Calendar)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvertClassPublic1() throws Exception {
		ComponentList components = new ComponentList();
		VEvent privateEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "private event");
		privateEvent.getProperties().add(Clazz.PRIVATE);
		components.add(privateEvent);
		
		VEvent confidentialEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "confidential event");
		confidentialEvent.getProperties().add(Clazz.CONFIDENTIAL);
		components.add(confidentialEvent);
		
		Calendar original = new Calendar(components);
		Calendar result = CalendarDataUtils.convertClassPublic(original);
		Assert.assertNotSame(result, original);
		
		for (Iterator<?> i = result.getComponents().iterator(); i.hasNext();) {
			VEvent event = (VEvent) i.next();  
			Assert.assertEquals(Clazz.PUBLIC, event.getProperty(Clazz.CLASS));
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSingleEventNull() throws Exception {
		Assert.assertNull(CalendarDataUtils.getSingleEvent(null, null));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSingleEventControl() throws Exception {
		ComponentList components = new ComponentList();
		VEvent privateEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "event");
		privateEvent.getProperties().add(new Uid("12345"));
		components.add(privateEvent);
		Calendar calendar = new Calendar(components);
		
		VEvent result = CalendarDataUtils.getSingleEvent(calendar, "12345");
		Assert.assertNotNull(result);
		Assert.assertEquals(privateEvent, result);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSingleEventNotFound() throws Exception {
		ComponentList components = new ComponentList();
		VEvent privateEvent = new VEvent(new DateTime(makeDateTime("20100210-0900")), new DateTime(makeDateTime("20100210-1000")), "event");
		privateEvent.getProperties().add(new Uid("123456"));
		components.add(privateEvent);
		Calendar calendar = new Calendar(components);
		
		VEvent result = CalendarDataUtils.getSingleEvent(calendar, "12345");
		Assert.assertNull(result);
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
