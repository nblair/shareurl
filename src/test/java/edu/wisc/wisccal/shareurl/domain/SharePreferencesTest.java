/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
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

package edu.wisc.wisccal.shareurl.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nicholas Blair
 */
public class SharePreferencesTest {

	@Test
	public void testGetFilterDisplayControl() {
		SharePreferences preferences = new SharePreferences();
		String display = preferences.getFilterDisplay();
		Assert.assertEquals("", display);
	}
	
	@Test
	public void testGetFilterDisplayPropertyMatch() {
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new PropertyMatchPreference("SUMMARY", "soccer"));
		String display = preferences.getFilterDisplay();
		Assert.assertEquals("Title contains soccer", display);
		
		preferences.addPreference(new PropertyMatchPreference("DESCRIPTION", "soccer"));
		display = preferences.getFilterDisplay();
		Assert.assertEquals("Title contains soccer" + SharePreferences.FILTER_DISPLAY_SEPARATOR + "Description contains soccer", display);
	}
	
	@Test
	public void testGetFilterDisplayAccessClassification() {
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new AccessClassificationMatchPreference(AccessClassification.PUBLIC));
		String display = preferences.getFilterDisplay();
		Assert.assertEquals("Privacy matches 'Public'", display);
		
		preferences.addPreference(new AccessClassificationMatchPreference(AccessClassification.PRIVATE));
		display = preferences.getFilterDisplay();
		Assert.assertEquals("Privacy matches 'Private'" + SharePreferences.FILTER_DISPLAY_SEPARATOR + "Privacy matches 'Public'", display);
		preferences.addPreference(new AccessClassificationMatchPreference(AccessClassification.CONFIDENTIAL));
		
		display = preferences.getFilterDisplay();
		Assert.assertEquals("Privacy matches 'Show Date and Time Only'" + SharePreferences.FILTER_DISPLAY_SEPARATOR + "Privacy matches 'Private'" + SharePreferences.FILTER_DISPLAY_SEPARATOR + "Privacy matches 'Public'", display);
	}
	
	@Test
	public void testGetFilterDisplayMix() {
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new PropertyMatchPreference("SUMMARY", "soccer"));
		String display = preferences.getFilterDisplay();
		Assert.assertEquals("Title contains soccer", display);
		
		preferences.addPreference(new AccessClassificationMatchPreference(AccessClassification.PUBLIC));
		display = preferences.getFilterDisplay();
		Assert.assertEquals("Title contains soccer" + SharePreferences.FILTER_DISPLAY_SEPARATOR + "Privacy matches 'Public'", display);
	}
	
	/**
	 * Make sure Include Participants preference doesn't show up.
	 */
	@Test
	public void testGetFilterDisplayMixIncludeParticipants() {
		SharePreferences preferences = new SharePreferences();
		preferences.addPreference(new IncludeParticipantsPreference(true));
		preferences.addPreference(new PropertyMatchPreference("SUMMARY", "soccer"));
		String display = preferences.getFilterDisplay();
		Assert.assertEquals("Title contains soccer", display);
		
		preferences.addPreference(new AccessClassificationMatchPreference(AccessClassification.PUBLIC));
		display = preferences.getFilterDisplay();
		Assert.assertEquals("Title contains soccer" + SharePreferences.FILTER_DISPLAY_SEPARATOR + "Privacy matches 'Public'", display);
	}
}
