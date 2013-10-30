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
package edu.wisc.wisccal.schedassist.ldap;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.impl.ldap.LDAPAttributesKeyImpl;

/**
 * Subclass of {@link LDAPAttributesKeyImpl} with specific function for UW.
 * 
 * @author Nicholas Blair
 * @version $Id: UWLDAPAttributesKeyImpl.java $
 */
public class UWLDAPAttributesKeyImpl extends LDAPAttributesKeyImpl {

	private static final String ACTIVE = "active";
	
	/**
	 * Eligibility for calendar service is determined by whether or not the eligibility attribute
	 * equals the value {@link #ACTIVE} or the upn attribute is not null?
	 * 
	 *  (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKeyImpl#evaluateEligibilityAttributeValue(java.util.Map)
	 */
	@Override
	public boolean evaluateEligibilityAttributeValue(
			Map<String, List<String>> attributes) {
		if(attributes == null || attributes.isEmpty()) {
			return false;
		}
		boolean eligible = false;
		List<String> values = attributes.get(getEligibilityAttributeName());
		if(values != null && values.size() == 1) {
			final String eligibilityValue = values.get(0);
			eligible = ACTIVE.equalsIgnoreCase(eligibilityValue);
		} 
		//TODO evaluate ldap attribute here...
		if(!eligible){
			values = attributes.get(getUpnAttributeName());
			if(null != values) {
				final String upnValue = values.get(0);
				eligible = StringUtils.isNotBlank(upnValue);
			}	
		}
		
		return eligible;
	}

}
