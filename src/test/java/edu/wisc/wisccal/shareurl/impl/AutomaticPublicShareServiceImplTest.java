/**
 * 
 */

package edu.wisc.wisccal.shareurl.impl;

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * @author Nicholas Blair
 */
public class AutomaticPublicShareServiceImplTest {

	/**
	 * Simulate {@link AutomaticPublicShareServiceImpl#getAutomaticPublicShare(String)} for an eligible account.
	 */
	@Test
	public void testGetAutomaticPublicShareControl() {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl();
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getCalendarUniqueId()).thenReturn("uniqueid1");
		when(account.getEmailAddress()).thenReturn("bbadger@wisc.edu");
		when(account.isEligible()).thenReturn(true);
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(false);
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		
		Share share = service.getAutomaticPublicShare("bbadger@wisc.edu");
		Assert.assertNotNull(share);
		Assert.assertTrue(share.isFreeBusyOnly());
		Assert.assertEquals("bbadger@wisc.edu", share.getKey());
		Assert.assertFalse(share.isRevocable());
	}
	
	/**
	 * Simulate {@link AutomaticPublicShareServiceImpl#getAutomaticPublicShare(String)} for an ineligible account.
	 */
	@Test
	public void testGetAutomaticPublicShareIneligible() {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl();
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getCalendarUniqueId()).thenReturn("uniqueid1");
		when(account.getEmailAddress()).thenReturn("bbadger@wisc.edu");
		when(account.isEligible()).thenReturn(false);
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(false);
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		
		Share share = service.getAutomaticPublicShare("bbadger@wisc.edu");
		Assert.assertNull(share);
	}
	/**
	 * Simulate {@link AutomaticPublicShareServiceImpl#getAutomaticPublicShare(String)} for an eligible account that
	 * has opted out.
	 */
	@Test
	public void testGetAutomaticPublicShareOptOut() {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl();
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getCalendarUniqueId()).thenReturn("uniqueid1");
		when(account.getEmailAddress()).thenReturn("bbadger@wisc.edu");
		when(account.isEligible()).thenReturn(true);
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(true);
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		
		Share share = service.getAutomaticPublicShare("bbadger@wisc.edu");
		Assert.assertNull(share);
	}
	
	/**
	 * Simulate {@link AutomaticPublicShareServiceImpl#getAutomaticPublicShare(String)} for an eligible account that
	 * has the unsearchable attribute set.
	 */
	@Test
	public void testGetAutomaticPublicShareUnsearchable() {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl();
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getCalendarUniqueId()).thenReturn("uniqueid1");
		when(account.getEmailAddress()).thenReturn("bbadger@wisc.edu");
		when(account.isEligible()).thenReturn(true);
		when(account.getAttributeValue(service.getUnsearchableAttributeName())).thenReturn("Y");
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(false);
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		
		Share share = service.getAutomaticPublicShare("bbadger@wisc.edu");
		Assert.assertNull(share);
	}
}
