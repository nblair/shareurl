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

package edu.wisc.wisccal.shareurl.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.wisc.wisccal.shareurl.domain.ContentFilter;

/**
 * @author Nicholas Blair
 */
public class ViewHelperTest {

	@Test
	public void testFiltersToJson() {
		List<ContentFilter> list = new ArrayList<ContentFilter>();
		Assert.assertEquals("{  }", ViewHelper.filtersToJson(list));
		
		list.add(new ContentFilterImpl("CLASS", "PUBLIC"));
		Assert.assertEquals("{ 'CLASS': 'PUBLIC' }", ViewHelper.filtersToJson(list));
		
		list.add(new ContentFilterImpl("CLASS", "PRIVATE"));
		Assert.assertEquals("{ 'CLASS': 'PUBLIC', 'CLASS': 'PRIVATE' }", ViewHelper.filtersToJson(list));
		
		list.add(new ContentFilterImpl("LOCATION", "test"));
		Assert.assertEquals("{ 'CLASS': 'PUBLIC', 'CLASS': 'PRIVATE', 'LOCATION': 'test' }", ViewHelper.filtersToJson(list));
		
	}
	
	private static class ContentFilterImpl implements ContentFilter {
		private final String propertyName;
		private final String matchValue;		
		/**
		 * @param propertyName
		 * @param matchValue
		 */
		private ContentFilterImpl(String propertyName, String matchValue) {
			this.propertyName = propertyName;
			this.matchValue = matchValue;
		}
		@Override
		public String getPropertyName() {
			return propertyName;
		}
		@Override
		public String getMatchValue() {
			return matchValue;
		}
	}
}
