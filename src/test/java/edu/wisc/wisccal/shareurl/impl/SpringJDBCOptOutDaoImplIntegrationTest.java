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

/**
 * @author Nicholas Blair
 */
public class SpringJDBCOptOutDaoImplIntegrationTest extends AbstractDatabaseDependentTest {

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
