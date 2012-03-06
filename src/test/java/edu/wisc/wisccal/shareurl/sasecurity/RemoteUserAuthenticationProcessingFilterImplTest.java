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
package edu.wisc.wisccal.shareurl.sasecurity;

import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import edu.wisc.wisccal.shareurl.sasecurity.RemoteUserAuthenticationProcessingFilterImpl;

/**
 * Tests for {@link PubcookieProcessingFilterImpl}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PubcookieProcessingFilterImplTest.java 1722 2010-02-15 22:01:26Z npblair $
 */
public class RemoteUserAuthenticationProcessingFilterImplTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		//ICalendarAccount account = EasyMock.createMock(ICalendarAccount.class);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setRemoteUser("username1");
		//CalendarUserDetails details = new CalendarUserDetails(account);
		
		Object fakeDetails = new Object();
		AuthenticationDetailsSource mockAuthenticationDetailsSource = EasyMock.createMock(AuthenticationDetailsSource.class);
		EasyMock.expect(mockAuthenticationDetailsSource.buildDetails(EasyMock.isA(Object.class))).andReturn(fakeDetails);
		
		AuthenticationManager mockAuthnManager = EasyMock.createMock(AuthenticationManager.class);
		EasyMock.expect(mockAuthnManager.authenticate(EasyMock.isA(UsernamePasswordAuthenticationToken.class))).andReturn(null);
		
		EasyMock.replay(mockAuthenticationDetailsSource, mockAuthnManager);
		RemoteUserAuthenticationProcessingFilterImpl filter = new RemoteUserAuthenticationProcessingFilterImpl();
		filter.setAuthenticationDetailsSource(mockAuthenticationDetailsSource);
		filter.setAuthenticationManager(mockAuthnManager);
		filter.attemptAuthentication(request, response);
		
		EasyMock.verify(mockAuthenticationDetailsSource, mockAuthnManager);
	}
}
