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
package edu.wisc.wisccal.shareurl.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.AbstractCalendarAccount;
import org.jasig.schedassist.model.ICalendarAccount;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import edu.wisc.wisccal.shareurl.impl.AbstractDatabaseDependentTest;

public class AbstractCalendarIntegrationTest {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	protected DateTime currentDt = new DateTime();
	protected DateTime startDt = currentDt.minusDays(7);
	protected DateTime endDt   = currentDt.plusDays(7);
	protected Date startDate = startDt.toDate();
	protected Date endDate = endDt.toDate();

	protected AbstractCalendarAccount account = new AbstractCalendarAccount(){
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -5880683601258160239L;

		@Override
		public boolean isEligible() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isDelegate() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public String getUsername() {
			// TODO Auto-generated method stub
			return "ctcudd";
		}
		
		@Override
		public String getEmailAddress() {
			// TODO Auto-generated method stub
			return "ctcudd@wisctest.wisc.edu";
		}
		
		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "Cudd,Collin";
		}
		
		@Override
		public String getCalendarUniqueId() {
			// TODO Auto-generated method stub
			return "ctcudd12345";
		}
		
		@Override
		public String getCalendarLoginId() {
			// TODO Auto-generated method stub
			return "ctcudd@wisctest.wisc.edu";
		}
		
		@Override
		public Map<String, List<String>> getAttributes() {
			Map<String, List<String>> attribMap = new HashMap<String, List<String>>();
			List<String> attribList = new ArrayList<String>();
			attribList.add("ctcudd@o365preview.wisctest.wisc.edu");
			attribMap.put("wiscedumsolupn",attribList);
			return null;
		}
		
	};
	
}
