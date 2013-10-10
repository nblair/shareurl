/**
 * 
 */

package edu.wisc.wisccal.shareurl.web;

import java.util.Collections;
import java.util.Set;

import net.fortuna.ical4j.model.property.Clazz;

import org.junit.Assert;
import org.junit.Test;

import edu.wisc.wisccal.shareurl.domain.AccessClassificationMatchPreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 * @author Nicholas Blair
 */
public class EditSharePreferencesControllerTest {

	@Test
	public void testConstructDesiredPrivacyPreferences() {
		EditSharePreferencesController controller = new EditSharePreferencesController();
		Assert.assertEquals(Collections.emptySet(), controller.constructDesiredPrivacyPreferences(true, true, true));
		Assert.assertEquals(Collections.emptySet(), controller.constructDesiredPrivacyPreferences(false, false, false));
		
		Set<ISharePreference> set = controller.constructDesiredPrivacyPreferences(true, true, false);
		Assert.assertEquals(2, set.size());
		Assert.assertTrue(set.contains(SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, Clazz.PUBLIC.getValue())));
		Assert.assertTrue(set.contains(SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, Clazz.CONFIDENTIAL.getValue())));
		
		set = controller.constructDesiredPrivacyPreferences(true, false, false);
		Assert.assertEquals(1, set.size());
		Assert.assertTrue(set.contains(SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, Clazz.PUBLIC.getValue())));
		
		
		set = controller.constructDesiredPrivacyPreferences(false, false, true);
		Assert.assertEquals(1, set.size());
		Assert.assertTrue(set.contains(SharePreferences.construct(AccessClassificationMatchPreference.CLASS_ATTRIBUTE, Clazz.CLASS, Clazz.PRIVATE.getValue())));
	}
}
