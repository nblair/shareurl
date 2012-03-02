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
package edu.wisc.wisccal.shareurl.security;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.IDelegateCalendarAccount;

/**
 * Tests for {@link DelegateLoginHelper}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateLoginHelperTest.java 1441 2009-12-17 19:09:21Z npblair $
 */
public class DelegateLoginHelperTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginControl() throws Exception {
		ICalendarAccount mockAccount = EasyMock.createMock(ICalendarAccount.class);
		IDelegateCalendarAccount mockDelegate = EasyMock.createMock(IDelegateCalendarAccount.class);
		EasyMock.expect(mockDelegate.isEligible()).andReturn(true);
		EasyMock.replay(mockAccount, mockDelegate);

		CalendarUserDetails currentUser = new CalendarUserDetails(mockAccount);		
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, ""));
		SecurityContextHolder.setContext(context);
		
		DelegateLoginHelper helper = new DelegateLoginHelper();
		helper.loginAsDelegate(mockDelegate);
		
		currentUser = (CalendarUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Assert.assertNotNull(currentUser);
		Assert.assertEquals(mockDelegate, currentUser.getActingOnBehalfOf());
		Assert.assertEquals(mockDelegate, currentUser.getActiveAccount());
		Assert.assertTrue(currentUser.isEnabled());
		EasyMock.verify(mockAccount, mockDelegate);
		
	}
	
	/**
	 * test logoutDelegate when no delegate has been set.
	 * @throws Exception
	 */
	@Test
	public void testLogoutDelegateNull() throws Exception {
		ICalendarAccount mockAccount = EasyMock.createMock(ICalendarAccount.class);
		EasyMock.expect(mockAccount.isEligible()).andReturn(true);
		EasyMock.replay(mockAccount);

		CalendarUserDetails currentUser = new CalendarUserDetails(mockAccount);		
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, ""));
		SecurityContextHolder.setContext(context);
		
		DelegateLoginHelper helper = new DelegateLoginHelper();
		// no initial call to loginAsDelegate
		helper.logoutDelegate();
		
		currentUser = (CalendarUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Assert.assertNotNull(currentUser);
		Assert.assertEquals(null, currentUser.getActingOnBehalfOf());
		Assert.assertEquals(mockAccount, currentUser.getActiveAccount());
		Assert.assertTrue(currentUser.isEnabled());
		EasyMock.verify(mockAccount);
	}
	/**
	 * Login as calendarAccount.
	 * Login delegate account, verify it becomes the active account.
	 * Logout delegate, assert original calendarAccount becomes active.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLogoutDelegateControl() throws Exception {
		ICalendarAccount mockAccount = EasyMock.createMock(ICalendarAccount.class);
		EasyMock.expect(mockAccount.isEligible()).andReturn(true);
		IDelegateCalendarAccount mockDelegate = EasyMock.createMock(IDelegateCalendarAccount.class);
		EasyMock.expect(mockDelegate.isEligible()).andReturn(true);
		EasyMock.replay(mockAccount, mockDelegate);

		CalendarUserDetails currentUser = new CalendarUserDetails(mockAccount);		
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, ""));
		SecurityContextHolder.setContext(context);
		
		DelegateLoginHelper helper = new DelegateLoginHelper();
		helper.loginAsDelegate(mockDelegate);
		currentUser = (CalendarUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		// verify current active login is the delegate
		Assert.assertEquals(mockDelegate, currentUser.getActingOnBehalfOf());
		Assert.assertEquals(mockDelegate, currentUser.getActiveAccount());
		Assert.assertTrue(currentUser.isEnabled());

		// logout the delegate
		helper.logoutDelegate();
	
		currentUser = (CalendarUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// verify current active login returns to the calendarAccount
		Assert.assertNotNull(currentUser);
		Assert.assertEquals(null, currentUser.getActingOnBehalfOf());
		Assert.assertEquals(mockAccount, currentUser.getActiveAccount());
		Assert.assertTrue(currentUser.isEnabled());
		
		EasyMock.verify(mockAccount, mockDelegate);
	}
}
