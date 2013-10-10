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
