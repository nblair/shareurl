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


}
