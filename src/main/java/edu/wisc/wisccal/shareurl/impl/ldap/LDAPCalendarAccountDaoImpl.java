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
package edu.wisc.wisccal.shareurl.impl.ldap;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.ICalendarAccountDao;

/**
 *
 * @author Nicholas Blair
 * @version $Id: LDAPCalendarAccountDaoImpl.java $
 */
public class LDAPCalendarAccountDaoImpl implements ICalendarAccountDao {

	private static final String UID = "uid";
	private static final String MAIL = "mail";
	private static final String DISPLAYNAME = "cn";
	
	private Log LOG = LogFactory.getLog(this.getClass());
	private LdapOperations ldapTemplate;
	private String baseDn = "o=isp";
	private long searchResultsLimit = 25L;
	private int searchTimeLimit = 5000;

	/**
	 * @param ldapTemplate the ldapTemplate to set
	 */
	@Required
	public void setLdapTemplate(LdapOperations ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	/**
	 * @param baseDn the baseDn to set
	 */
	@Required
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}


	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccountDao#getCalendarAccount(java.lang.String)
	 */
	@Override
	@Cacheable(cacheName="CalendarAccountCache", 
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",properties=@Property(name="includeMethod",value="true")))
	public ICalendarAccount getCalendarAccount(String username) {

		EqualsFilter searchFilter = new EqualsFilter(UID, username);
		List<LDAPCalendarAccountImpl> results = executeSearchReturnList(searchFilter);
		LDAPCalendarAccountImpl user = (LDAPCalendarAccountImpl) DataAccessUtils.singleResult(results);
		return user;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccountDao#getCalendarAccountByUniqueId(java.lang.String)
	 */
	@Override
	@Cacheable(cacheName="CalendarAccountCache", 
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",properties=@Property(name="includeMethod",value="true")))
	public ICalendarAccount getCalendarAccountByUniqueId(String uniqueId) {
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter(MAIL, uniqueId));
		searchFilter.and(new LikeFilter(UID, "*"));
		List<LDAPCalendarAccountImpl> results = executeSearchReturnList(searchFilter);
		LDAPCalendarAccountImpl user = (LDAPCalendarAccountImpl) DataAccessUtils.singleResult(results);
		return user;
	}

	/**
	 * 
	 * @param searchFilter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<LDAPCalendarAccountImpl> executeSearchReturnList(final Filter searchFilter) {
		SearchControls searchControls = new SearchControls();
		searchControls.setCountLimit(searchResultsLimit);
		searchControls.setTimeLimit(searchTimeLimit);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<LDAPCalendarAccountImpl> results = ldapTemplate.search(
				baseDn, 
				searchFilter.toString(), 
				searchControls, 
				new AttributesMapper() {
			@Override
			public Object mapFromAttributes(Attributes attributes)
				throws NamingException {
				LDAPCalendarAccountImpl user = new LDAPCalendarAccountImpl();
				user.setEmailAddress(getAttributeValue(attributes, MAIL));
				user.setName(getAttributeValue(attributes, DISPLAYNAME));
				user.setUsername(getAttributeValue(attributes, UID));
				return user;
			}
		});
		if(LOG.isDebugEnabled()) {
			LOG.debug("search " + searchFilter + " returned " + results.size() + " results");
		}
		return results;
	}

	/**
	 * Get the specified attribute, or null.
	 * If the attribute is not empty, it's value is {@link String#trim()}'d.
	 * 
	 * @param attributes
	 * @param attributeName
	 * @return
	 * @throws NamingException 
	 */
	String getAttributeValue(Attributes attributes, String attributeName) throws NamingException  {
		Attribute attribute = attributes.get(attributeName);
		if(null != attribute) {
			String value = (String) attribute.get();
			if(null != value) {
				value = value.trim();
			}
			return value;
		}
		return null;
	}

}
