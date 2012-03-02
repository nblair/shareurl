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

import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.TimeLimitExceededException;
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
import edu.wisc.wisccal.shareurl.IDelegateCalendarAccount;
import edu.wisc.wisccal.shareurl.IDelegateCalendarAccountDao;

/**
 *
 * @author Nicholas Blair
 * @version $Id: LDAPResourceAccountDaoImpl.java $
 */
public class LDAPResourceAccountDaoImpl implements IDelegateCalendarAccountDao {

	private static final String MAIL = "mail";
	private static final String CN = "cn";
	private static final String RESOURCEOWNER = "wisceducalresourceownerid";
	
	private static final String WILDCARD = "*";
	
	private Log LOG = LogFactory.getLog(this.getClass());
	private LdapOperations ldapTemplate;
	private String baseDn = "o=isp";
	private long searchResultsLimit = 25L;
	private int searchTimeLimit = 5000;
	
	/**
	 * @param ldapTemplate the ldapTemplate to set
	 */
	@Autowired
	public void setLdapTemplate(LdapOperations ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	/**
	 * @param baseDn the baseDn to set
	 */
	@Value("${ldap.baseDn}")
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	/**
	 * @param searchResultsLimit the searchResultsLimit to set
	 */
	@Value("${ldap.searchResultsLimit:25}")
	public void setSearchResultsLimit(long searchResultsLimit) {
		this.searchResultsLimit = searchResultsLimit;
	}
	/**
	 * @param searchTimeLimit the searchTimeLimit to set
	 */
	@Value("${ldap.searchTimeLimit:5000}")
	public void setSearchTimeLimit(int searchTimeLimit) {
		this.searchTimeLimit = searchTimeLimit;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.IDelegateCalendarAccountDao#getDelegate(java.lang.String, edu.wisc.wisccal.calendarkey.ICalendarAccount)
	 */
	@Override
	@Cacheable(cacheName="DelegateCalendarAccountCache", 
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",properties=@Property(name="includeMethod",value="true")))
	public IDelegateCalendarAccount getDelegate(final String accountName,
			final ICalendarAccount owner) {
		
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter(CN, accountName));
		searchFilter.and(new EqualsFilter(RESOURCEOWNER, owner.getUsername()));
		searchFilter.and(new LikeFilter(MAIL, WILDCARD));
		
		List<IDelegateCalendarAccount> results = executeSearchReturnList(searchFilter, owner);
		IDelegateCalendarAccount resource = (IDelegateCalendarAccount) DataAccessUtils.singleResult(results);
		return resource;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.IDelegateCalendarAccountDao#getDelegateByUniqueId(java.lang.String, edu.wisc.wisccal.calendarkey.ICalendarAccount)
	 */
	@Override
	@Cacheable(cacheName="DelegateCalendarAccountCache", 
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",properties=@Property(name="includeMethod",value="true")))
	public IDelegateCalendarAccount getDelegateByUniqueId(
			final String accountUniqueId, final ICalendarAccount owner) {
		
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter(MAIL, accountUniqueId));
		searchFilter.and(new EqualsFilter(RESOURCEOWNER, owner.getUsername()));
		
		List<IDelegateCalendarAccount> results = executeSearchReturnList(searchFilter, owner);
		IDelegateCalendarAccount resource = (IDelegateCalendarAccount) DataAccessUtils.singleResult(results);
		return resource;		

	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.IDelegateCalendarAccountDao#searchForDelegates(java.lang.String, edu.wisc.wisccal.calendarkey.ICalendarAccount)
	 */
	@Override
	@Cacheable(cacheName="DelegateCalendarAccountCache", 
			keyGenerator=@KeyGenerator(name="ListCacheKeyGenerator",properties=@Property(name="includeMethod",value="true")))
	public List<IDelegateCalendarAccount> searchForDelegates(final String searchText,
			final ICalendarAccount owner) {
		
		String searchTextInternal = searchText.replace(" ", WILDCARD);
		if(!searchTextInternal.endsWith(WILDCARD)) {
			searchTextInternal += WILDCARD;
		}
		
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new LikeFilter(CN, searchTextInternal));
		searchFilter.and(new EqualsFilter(RESOURCEOWNER, owner.getUsername()));
		searchFilter.and(new LikeFilter(MAIL, WILDCARD));
		
		List<IDelegateCalendarAccount> results = executeSearchReturnList(searchFilter, owner);
		return results;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccountDao#getCalendarAccount(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccount(String username) {
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter(CN, username));
		//searchFilter.and(new EqualsFilter(RESOURCEOWNER, owner.getUsername()));
		searchFilter.and(new LikeFilter(MAIL, WILDCARD));
		
		List<IDelegateCalendarAccount> results = executeSearchReturnList(searchFilter);
		IDelegateCalendarAccount resource = (IDelegateCalendarAccount) DataAccessUtils.singleResult(results);
		return resource;

	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.ICalendarAccountDao#getCalendarAccountByUniqueId(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccountByUniqueId(String uniqueId) {
		EqualsFilter searchFilter = new EqualsFilter(MAIL, uniqueId);
		List<IDelegateCalendarAccount> results = executeSearchReturnList(searchFilter);
		IDelegateCalendarAccount resource = (IDelegateCalendarAccount) DataAccessUtils.singleResult(results);
		return resource;		
	}
	
	/**
	 * 
	 * @see #executeSearchReturnList(Filter, ICalendarAccount)
	 * @param searchFilter
	 * @return
	 */
	protected List<IDelegateCalendarAccount> executeSearchReturnList(final Filter searchFilter) {
		return executeSearchReturnList(searchFilter, null);
	}
	/**
	 * Will return 0 results if searchResultsLimit or searchTimeLimit exceeded.
	 * 
	 * @param searchFilter
	 * @param owner
	 * @return a never null, but possibly empty {@link List} of matching {@link IDelegateCalendarAccount}s.
	 */
	@SuppressWarnings("unchecked")
	protected List<IDelegateCalendarAccount> executeSearchReturnList(final Filter searchFilter, final ICalendarAccount owner) {
		SearchControls searchControls = new SearchControls();
		searchControls.setCountLimit(searchResultsLimit);
		searchControls.setTimeLimit(searchTimeLimit);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		List<IDelegateCalendarAccount> results = Collections.emptyList();
		try {
			results = ldapTemplate.search(
					baseDn, 
					searchFilter.toString(), 
					searchControls, 
					new LDAPResourceAccountAttributesMapper(owner));
			if(LOG.isDebugEnabled()) {
				LOG.debug("search " + searchFilter + " returned " + results.size() + " results");
			}
		} catch (SizeLimitExceededException e) {
			LOG.debug("search filter exceeded size limit (" + searchResultsLimit + "): " + searchFilter);
		} catch (TimeLimitExceededException e) {
			LOG.debug("search filter exceeded time limit(" + searchTimeLimit + " milliseconds): " + searchFilter);
		}
		return results;
	}
	
	/**
	 * 
	 *
	 *  
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: OracleLdapCalendarResourceAccountDaoImpl.java 3187 2011-05-19 18:50:27Z npblair $
	 */
	static class LDAPResourceAccountAttributesMapper implements AttributesMapper {

		private final ICalendarAccount owner;

		public LDAPResourceAccountAttributesMapper() {
			owner = null;
		}
		/**
		 * @param owner
		 */
		public LDAPResourceAccountAttributesMapper(
				ICalendarAccount owner) {
			this.owner = owner;
		}

		@Override
		public Object mapFromAttributes(Attributes attributes)
		throws NamingException {
			LDAPResourceAccountImpl user = new LDAPResourceAccountImpl(owner);
			user.setEmailAddress(getAttributeValue(attributes, MAIL));
			user.setName(getAttributeValue(attributes, CN));
			return user;
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

}
