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

package edu.wisc.wisccal.shareurl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Nicholas Blair
 */
public class SpringJDBCOptOutDaoImplTest extends AbstractDatabaseDependentTest {

	@Autowired 
	private SpringJDBCOptOutDaoImpl optOutDao;
	
	/**
	 * 
	 */
	@Test
	public void testFindEffective() {
		SpringJDBCOptOutDaoImpl dao = new SpringJDBCOptOutDaoImpl();
		Assert.assertNull(dao.findEffective(null));
		List<OptOutRecord> records = new ArrayList<OptOutRecord>();
		Assert.assertNull(dao.findEffective(records));
		
		Date now = new Date();
		OptOutRecord one = new OptOutRecord();
		one.setAccount("bbadger@wisc.edu");
		one.setValue("Y");
		one.setTimestamp(now);
		
		records.add(one);
		Assert.assertEquals(one, dao.findEffective(records));
		
		// two is in the past
		OptOutRecord two = new OptOutRecord();
		two.setAccount("bbadger@wisc.edu");
		two.setValue("N");
		two.setTimestamp(DateUtils.addHours(now, -1));
		
		records.add(two);
		Assert.assertEquals(one, dao.findEffective(records));
		
		// three is in the future (most effective is the latest date)
		OptOutRecord three = new OptOutRecord();
		three.setAccount("bbadger@wisc.edu");
		three.setValue("N");
		three.setTimestamp(DateUtils.addHours(now, 1));
		
		records.add(three);
		Assert.assertEquals(three, dao.findEffective(records));
	}
	
	@Test
	public void testHasOptedOutControl() {
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		Mockito.when(account.getCalendarUniqueId()).thenReturn("uniqueid1");
		Assert.assertFalse(optOutDao.isOptOut(account));
	}
	
	
	@Test
	public void testOptOutControl() {
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		Mockito.when(account.getCalendarUniqueId()).thenReturn("uniqueid2");
		optOutDao.optOut(account);
		Assert.assertTrue(optOutDao.isOptOut(account));
	}
	
	@Test
	public void testOptInControl() throws InterruptedException {
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		Mockito.when(account.getCalendarUniqueId()).thenReturn("uniqueid3");
		optOutDao.optOut(account);
		Assert.assertTrue(optOutDao.isOptOut(account));
		// provide a small delay to make sure the timestamp for the next optoutrecord is later 
		Thread.sleep(50);
		optOutDao.optIn(account);
		Assert.assertFalse(optOutDao.isOptOut(account));
	}
	
	@Test
	public void testOptInFirstDoesNothing() {
		ICalendarAccount account = Mockito.mock(ICalendarAccount.class);
		Mockito.when(account.getCalendarUniqueId()).thenReturn("uniqueid4");
		optOutDao.optIn(account);
		Assert.assertFalse(optOutDao.isOptOut(account));
		
	}
}
