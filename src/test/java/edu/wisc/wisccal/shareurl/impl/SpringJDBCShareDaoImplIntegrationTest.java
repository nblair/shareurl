package edu.wisc.wisccal.shareurl.impl;

import net.fortuna.ical4j.model.property.Summary;

import org.easymock.EasyMock;
import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.wisc.wisccal.shareurl.domain.AccessClassification;
import edu.wisc.wisccal.shareurl.domain.AccessClassificationMatchPreference;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.PropertyMatchPreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 * Test harness for {@link SpringJDBCShareDaoImpl}.
 *  
 * @author Nicholas Blair
 */
public class SpringJDBCShareDaoImplIntegrationTest extends AbstractDatabaseDependentTest {

	private SpringJDBCShareDaoImpl shareDao;

	/**
	 * @param shareDao the shareDao to set
	 */
	@Autowired
	public void setShareDao(SpringJDBCShareDaoImpl shareDao) {
		this.shareDao = shareDao;
	}

	/**
	 * Test a basic share with no preferences (default; data share, no property filters).
	 * @throws Exception
	 */
	@Test
	public void testGenerateShareDefault() throws Exception {
		ICalendarAccount mockAccount = EasyMock.createMock(ICalendarAccount.class);
		EasyMock.expect(mockAccount.getCalendarUniqueId()).andReturn("unique-id1");
		EasyMock.replay(mockAccount);

		SharePreferences defaultPreferences = new SharePreferences();
		Share result = shareDao.generateNewShare(mockAccount, defaultPreferences);

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getKey());
		Assert.assertEquals("unique-id1", result.getOwnerCalendarUniqueId());

		Assert.assertEquals(0, result.getSharePreferences().getPreferences().size());
		Assert.assertFalse(result.getSharePreferences().isFreeBusyOnly());
	}
	
	/**
	 * Test a basic share with no preferences (default; data share, no property filters).
	 * @throws Exception
	 */
	@Test
	public void testRevokeShareDefault() throws Exception {
		ICalendarAccount mockAccount = EasyMock.createMock(ICalendarAccount.class);
		EasyMock.expect(mockAccount.getCalendarUniqueId()).andReturn("unique-id1");
		EasyMock.replay(mockAccount);

		SharePreferences defaultPreferences = new SharePreferences();
		Share result = shareDao.generateNewShare(mockAccount, defaultPreferences);
		String generatedKey = result.getKey();
		
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getKey());
		Assert.assertEquals("unique-id1", result.getOwnerCalendarUniqueId());

		Assert.assertEquals(0, result.getSharePreferences().getPreferences().size());
		Assert.assertFalse(result.getSharePreferences().isFreeBusyOnly());
		
		shareDao.revokeShare(result);
		Share lookup = shareDao.retrieveByKey(generatedKey);
		Assert.assertNull(lookup);
		Share invalid = shareDao.internalRetrieveByKey(generatedKey, "N");
		Assert.assertEquals(generatedKey, invalid.getKey());
		Assert.assertEquals("unique-id1", invalid.getOwnerCalendarUniqueId());
		Assert.assertFalse(invalid.isValid());
	}

	/**
	 * Test generateNewShare with the Free/Busy preference.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGenerateShareFreeBusy() throws Exception {
		ICalendarAccount mockAccount = EasyMock.createMock(ICalendarAccount.class);
		EasyMock.expect(mockAccount.getCalendarUniqueId()).andReturn("unique-id1");
		EasyMock.replay(mockAccount);

		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new FreeBusyPreference());

		Share result = shareDao.generateNewShare(mockAccount, preferences);

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getKey());
		Assert.assertEquals("unique-id1", result.getOwnerCalendarUniqueId());

		Assert.assertEquals(1, result.getSharePreferences().getPreferences().size());
		Assert.assertTrue(result.getSharePreferences().getPreferences().contains(new FreeBusyPreference()));
		Assert.assertTrue(result.getSharePreferences().isFreeBusyOnly());
	}

	/**
	 * Test generateNewShare with multiple property match preferences.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGenerateSharePropertyMatch() throws Exception {
		ICalendarAccount mockAccount = EasyMock.createMock(ICalendarAccount.class);
		EasyMock.expect(mockAccount.getCalendarUniqueId()).andReturn("unique-id1");
		EasyMock.replay(mockAccount);

		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new PropertyMatchPreference(Summary.SUMMARY, "soccer"));
		preferences.addPreference(new AccessClassificationMatchPreference(AccessClassification.PUBLIC));

		Share result = shareDao.generateNewShare(mockAccount, preferences);

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getKey());
		Assert.assertEquals("unique-id1", result.getOwnerCalendarUniqueId());

		Assert.assertEquals(2, result.getSharePreferences().getPreferences().size());
		Assert.assertTrue(result.getSharePreferences().getPreferences().contains(new PropertyMatchPreference(Summary.SUMMARY, "soccer")));
		Assert.assertTrue(result.getSharePreferences().getPreferences().contains(new AccessClassificationMatchPreference(AccessClassification.PUBLIC)));
		Assert.assertFalse(result.getSharePreferences().isFreeBusyOnly());
	}

}
