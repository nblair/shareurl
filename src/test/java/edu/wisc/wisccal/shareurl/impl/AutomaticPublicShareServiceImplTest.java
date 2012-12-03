/**
 * 
 */

package edu.wisc.wisccal.shareurl.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.impl.ldap.HasDistinguishedName;
import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

import edu.wisc.services.chub.v1_4.CurricularDataService;
import edu.wisc.services.chub.v1_4.QueryLimitExceededException;
import edu.wisc.services.ebo.curricular.v1_4.Student;
import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * @author Nicholas Blair
 */
public class AutomaticPublicShareServiceImplTest {

	
	@Test
	public void testGetAutomaticPublicShareNotHasDistinguishedName() {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl();
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getCalendarUniqueId()).thenReturn("uniqueid1");
		when(account.getEmailAddress()).thenReturn("bbadger@resources.wisc.edu");
		when(account.isEligible()).thenReturn(true);
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@resources.wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(false);
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		
		Share share = service.getAutomaticPublicShare("bbadger@resources.wisc.edu");
		Assert.assertNotNull("expected not null for mock that doesn't implement HasDistinguishedName (resource)", share);
		Assert.assertTrue(share.isFreeBusyOnly());
		Assert.assertEquals("bbadger@resources.wisc.edu", share.getKey());
		Assert.assertFalse(share.isRevocable());
	}
	/**
	 * Simulate {@link AutomaticPublicShareServiceImpl#getAutomaticPublicShare(String)} for an eligible account.
	 * Test account is "inside" the primary domain, so the 
	 * @throws QueryLimitExceededException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAutomaticPublicShareControl() throws QueryLimitExceededException {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl(new DistinguishedName("o=wiscmail.wisc.edu,o=isp"));
		TestCalendarAccount account = new TestCalendarAccount();
		account.setCalendarUniqueId("uniqueid1");
		account.setEligible(true);
		account.setEmailAddress("bbadger@wisc.edu");
		account.setDistinguishedName(new DistinguishedName("wwid=_fake_7E758681D7E758681D,ou=people,o=wiscmail.wisc.edu,o=isp"));
		account.setAttributeValue(service.getPviAttributeName(), "UW123ABC");
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(false);
		
		CurricularDataService curricularDataService = mock(CurricularDataService.class);
		List<Student> results = Collections.emptyList();
		when(curricularDataService.getStudents(isA(List.class))).thenReturn(results);
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		service.setCurricularDataService(curricularDataService);
		
		Share share = service.getAutomaticPublicShare("bbadger@wisc.edu");
		Assert.assertNotNull(share);
		Assert.assertTrue(share.isFreeBusyOnly());
		Assert.assertEquals("bbadger@wisc.edu", share.getKey());
		Assert.assertFalse(share.isRevocable());
	}
	/**
	 * Simulate {@link AutomaticPublicShareServiceImpl#getAutomaticPublicShare(String)} for an otherwise
	 * eligible account that is in the primary domain and missing a PVI.
	 * 
	 * Expects IllegalStateException.
	 * 
	 * @throws QueryLimitExceededException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAutomaticPublicSharePrimaryDomainMissingPVI() throws QueryLimitExceededException {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl(new DistinguishedName("o=wiscmail.wisc.edu,o=isp"));
		TestCalendarAccount account = new TestCalendarAccount();
		account.setCalendarUniqueId("uniqueid1");
		account.setEligible(true);
		account.setEmailAddress("bbadger@wisc.edu");
		account.setDistinguishedName(new DistinguishedName("wwid=_fake_7E758681D7E758681D,ou=people,o=wiscmail.wisc.edu,o=isp"));
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(false);
		
		CurricularDataService curricularDataService = mock(CurricularDataService.class);
		verify(curricularDataService, never()).getStudents(any(List.class));
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		service.setCurricularDataService(curricularDataService);
		
		try {
			service.getAutomaticPublicShare("bbadger@wisc.edu");
			Assert.fail("expected IllegalStateException for account in primary domain missing PVI");
		} catch(IllegalStateException e) {
			// success
		}
	}
	/**
	 * Since the account is outside of the primary domain, the curriculardataservice check (hasFerpaHold) is skipped.
	 * 
	 * @throws QueryLimitExceededException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAutomaticPublicShareOutsidePrimaryDomain() throws QueryLimitExceededException {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl(new DistinguishedName("o=wiscmail.wisc.edu,o=isp"));
		TestCalendarAccount account = new TestCalendarAccount();
		account.setCalendarUniqueId("uniqueid1");
		account.setEligible(true);
		account.setEmailAddress("bbadger@doit.wisc.edu");
		account.setDistinguishedName(new DistinguishedName("uid=bbadger,o=doit.wisc.edu,o=isp"));
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@doit.wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(false);
		
		CurricularDataService curricularDataService = mock(CurricularDataService.class);
		List<Student> results = Collections.emptyList();
		when(curricularDataService.getStudents(isA(List.class))).thenReturn(results);
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		service.setCurricularDataService(curricularDataService);
		
		Share share = service.getAutomaticPublicShare("bbadger@doit.wisc.edu");
		Assert.assertNotNull(share);
		Assert.assertTrue(share.isFreeBusyOnly());
		Assert.assertEquals("bbadger@doit.wisc.edu", share.getKey());
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
	 * @throws QueryLimitExceededException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAutomaticPublicShareOptOut() throws QueryLimitExceededException {
		AutomaticPublicShareServiceImpl service = new AutomaticPublicShareServiceImpl();
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getCalendarUniqueId()).thenReturn("uniqueid1");
		when(account.getEmailAddress()).thenReturn("bbadger@wisc.edu");
		when(account.isEligible()).thenReturn(true);
		
		ICalendarAccountDao calendarAccountDao = mock(ICalendarAccountDao.class);
		when(calendarAccountDao.getCalendarAccount(service.getMailAttributeName(), "bbadger@wisc.edu")).thenReturn(account);
		
		OptOutDao optOutDao = mock(OptOutDao.class);
		when(optOutDao.isOptOut(account)).thenReturn(true);
		
		CurricularDataService curricularDataService = mock(CurricularDataService.class);
		verify(curricularDataService, never()).getStudents(any(List.class));
		
		service.setCalendarAccountDao(calendarAccountDao);
		service.setOptOutDao(optOutDao);
		service.setCurricularDataService(curricularDataService);
		
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
	
	/**
	 * Special test implementation that implements both {@link ICalendarAccount} and {@link HasDistinguishedName}.
	 * 
	 * @author Nicholas Blair
	 */
	static class TestCalendarAccount implements ICalendarAccount, HasDistinguishedName {

		private Name distinguishedName;
		private String calendarUniqueId;
		private String emailAddress;
		private boolean eligible;
		private Map<String, String> singleValuedAttributes = new HashMap<String, String>();
		/**
		 * 
		 */
		private static final long serialVersionUID = 6909016620830696460L;
		/**
		 * @return the distinguishedName
		 */
		public Name getDistinguishedName() {
			return distinguishedName;
		}
		/**
		 * @param distinguishedName the distinguishedName to set
		 */
		public void setDistinguishedName(Name distinguishedName) {
			this.distinguishedName = distinguishedName;
		}
		/**
		 * @return the calendarUniqueId
		 */
		public String getCalendarUniqueId() {
			return calendarUniqueId;
		}
		/**
		 * @param calendarUniqueId the calendarUniqueId to set
		 */
		public void setCalendarUniqueId(String calendarUniqueId) {
			this.calendarUniqueId = calendarUniqueId;
		}
		/**
		 * @return the emailAddress
		 */
		public String getEmailAddress() {
			return emailAddress;
		}
		/**
		 * @param emailAddress the emailAddress to set
		 */
		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}
		/**
		 * @return the eligible
		 */
		public boolean isEligible() {
			return eligible;
		}
		/**
		 * @param eligible the eligible to set
		 */
		public void setEligible(boolean eligible) {
			this.eligible = eligible;
		}
		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#getAttributeValue(java.lang.String)
		 */
		@Override
		public String getAttributeValue(String attributeName) {
			return singleValuedAttributes.get(attributeName);
		}
		public void setAttributeValue(String attributeName, String attributeValue) {
			singleValuedAttributes.put(attributeName, attributeValue);
		}
		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			throw new UnsupportedOperationException();
		}
		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#getUsername()
		 */
		@Override
		public String getUsername() {
			throw new UnsupportedOperationException();
		}
		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#getCalendarLoginId()
		 */
		@Override
		public String getCalendarLoginId() {
			throw new UnsupportedOperationException();
		}
		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#getAttributeValues(java.lang.String)
		 */
		@Override
		public List<String> getAttributeValues(String attributeName) {
			throw new UnsupportedOperationException();
		}
		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#getAttributes()
		 */
		@Override
		public Map<String, List<String>> getAttributes() {
			throw new UnsupportedOperationException();
		}
		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#isDelegate()
		 */
		@Override
		public boolean isDelegate() {
			throw new UnsupportedOperationException();
		}
	}
}
