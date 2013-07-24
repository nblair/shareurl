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
package edu.wisc.wisccal.shareurl.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.wisc.wisccal.shareurl.domain.ContentFilter;

/**
 * @author Nicholas Blair
 */
public class ViewHelperTest {

	@Test
	public void testContentFiltersToJson() {
		List<ContentFilter> list = new ArrayList<ContentFilter>();
		Assert.assertEquals("{  }", ViewHelper.contentFiltersToJson(list));
		
		list.add(new ContentFilterImpl("LOCATION", new String [] {"test1"}));
		Assert.assertEquals("{ \"LOCATION\": [ \"test1\" ] }", ViewHelper.contentFiltersToJson(list));
		
		((ContentFilterImpl) list.get(0)).addMatchValue("test2");
		Assert.assertEquals("{ \"LOCATION\": [ \"test1\", \"test2\" ] }", ViewHelper.contentFiltersToJson(list));
		
		list.add(new ContentFilterImpl("DESCRIPTION", new String [] {"blah"}));
		Assert.assertEquals("{ \"LOCATION\": [ \"test1\", \"test2\" ], \"DESCRIPTION\": [ \"blah\" ] }", ViewHelper.contentFiltersToJson(list));
	}
	
	@Test
	public void testClassificationFiltersToJson() {
		List<String> list = new ArrayList<String>();
		Assert.assertEquals("[  ]", ViewHelper.classificationFiltersToJson(list));
		list.add("PUBLIC");
		Assert.assertEquals("[ \"PUBLIC\" ]", ViewHelper.classificationFiltersToJson(list));
		list.add("PRIVATE");
		Assert.assertEquals("[ \"PUBLIC\", \"PRIVATE\" ]", ViewHelper.classificationFiltersToJson(list));
	}
	
	private static class ContentFilterImpl implements ContentFilter {
		private final String propertyName;
		private final List<String> matchValues;		
		/**
		 * @param propertyName
		 * @param matchValues
		 */
		private ContentFilterImpl(String propertyName, String [] values) {
			this.propertyName = propertyName;
			this.matchValues = new ArrayList<String>(Arrays.asList(values));
		}
		@Override
		public String getPropertyName() {
			return propertyName;
		}
		@Override
		public List<String> getMatchValues() {
			return matchValues;
		}
		public void addMatchValue(String value) {
			matchValues.add(value);
		}
	}
}
