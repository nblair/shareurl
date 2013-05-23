package edu.wisc.wisccal.shareurl.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.joda.time.DateTime;

import edu.wisc.wisccal.shareurl.impl.AbstractDatabaseDependentTest;

public class AbstractCalendarIntegrationTest {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	protected DateTime currentDt = new DateTime();
	protected DateTime startDt = currentDt.minusDays(7);
	protected DateTime endDt   = currentDt.plusDays(7);
	protected Date startDate = startDt.toDate();
	protected Date endDate = endDt.toDate();

	protected ICalendarAccount account = new ICalendarAccount() {
		
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
			attribList.add("ctcudd@ad-test.wisc.edu");
			attribMap.put("wiscedumsolupn",attribList);
			return null;
		}
		
		@Override
		public List<String> getAttributeValues(String attributeName) {
            List<String> l =  new ArrayList<String>();
            if(attributeName.equals("wiscedumsolupn"))l.add("ctcudd@ad-test.wisc.edu");
            return l;

		}
		
		@Override
		public String getAttributeValue(String attributeName) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
}
