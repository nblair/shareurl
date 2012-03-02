/*******************************************************************************
 *  Copyright 2007-2010 The Board of Regents of the University of Wisconsin System.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
package edu.wisc.wisccal.shareurl.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.AccessClassification;
import edu.wisc.wisccal.shareurl.domain.AccessClassificationMatchPreference;
import edu.wisc.wisccal.shareurl.domain.FreeBusyPreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
import edu.wisc.wisccal.shareurl.domain.PropertyMatchPreference;
import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 * Spring JDBC backed implementation of {@link IShareDao}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SpringJDBCShareDaoImpl.java 1677 2010-02-08 18:31:01Z npblair $
 */
@Transactional
@Repository
public class SpringJDBCShareDaoImpl implements
IShareDao {

	private Log LOG = LogFactory.getLog(this.getClass());

	private static final int SHARE_ID_LENGTH = 16;
	private static final String VALID = "Y";
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	/**
	 * 
	 * @param dataSource
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	/**
	 * @return the simpleJdbcTemplate
	 */
	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.IShareDao#generateNewShare(edu.wisc.wisccal.calendarkey.ICalendarAccount, edu.wisc.wisccal.calendarkey.SharePreferences)
	 */
	@Override
	public Share generateNewShare(ICalendarAccount account,
			SharePreferences preferences) {
		String newKey = RandomStringUtils.randomAlphanumeric(SHARE_ID_LENGTH);
		while(null != internalRetrieveByKey(newKey)) {
			LOG.warn("randomAlphanumeric generated a key that already exists in shares (" + newKey + "), trying again");
			newKey = RandomStringUtils.randomAlphanumeric(SHARE_ID_LENGTH);
		}
		final String ownerUniqueId = account.getCalendarUniqueId();
		this.getSimpleJdbcTemplate().update(
				"insert into shares (name, owner, valid) values (?, ?, ?)", 
				newKey,
				ownerUniqueId,
				VALID);

		for(ISharePreference pref : preferences.getPreferences()) {
			storePreference(newKey, pref);
		}
		
		Share share = new Share();
		share.setKey(newKey);
		share.setOwnerCalendarUniqueId(ownerUniqueId);
		share.setSharePreferences(preferences);
		LOG.info("generated new share: " + share);
		return share;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.IShareDao#retreiveByKey(java.lang.String)
	 */
	@Override
	public Share retrieveByKey(String key) {
		return internalRetrieveByKey(key, VALID);
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.IShareDao#retrieveByOwner(edu.wisc.wisccal.calendarkey.ICalendarAccount)
	 */
	@Override
	public List<Share> retrieveByOwner(ICalendarAccount account) {
		return internalRetrieveByOwner(account, VALID);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.calendarkey.IShareDao#revokeShare(edu.wisc.wisccal.calendarkey.Share)
	 */
	@Override
	public void revokeShare(Share share) {
		int rows = this.getSimpleJdbcTemplate().update(
				"update shares set valid = 'N' where name = ? and owner = ?",
				share.getKey(),
				share.getOwnerCalendarUniqueId());
		if(rows == 1 && LOG.isInfoEnabled()) {
			LOG.info("successfully revoked share: " + share);
		} else if (rows != 1) {
			LOG.warn("revoke share " + share + " returned unexpected number of rows affected: " + rows);
		}
	}

	/**
	 * 
	 * @param shareKey
	 * @param preference
	 */
	protected void storePreference(final String shareKey, final ISharePreference preference) {
		int rows = this.getSimpleJdbcTemplate().update(
				"insert into share_preferences (sharekey, preference_type, preference_key, preference_value) values (?, ?, ?, ?)", 
				shareKey,
				preference.getType(),
				preference.getKey(),
				preference.getValue());
		if(rows == 1) { 
			if(LOG.isDebugEnabled()) {
				LOG.debug("stored preference " + preference + " for key " + shareKey);
			}
		} else {
			LOG.warn("unexpected result for storePreference: " + rows + " (preference: " + preference + ", key: " + shareKey + ")");
		}
	}
	
	/**
	 * 
	 * @param shareKey
	 * @return
	 */
	protected List<PersistenceSharePreference> retrieveSharePreferences(final String shareKey) {
		List<PersistenceSharePreference> results = this.getSimpleJdbcTemplate().query(
				"select * from share_preferences where sharekey = ?", 
				new PersistenceSharePreferenceRowMapper(),
				shareKey);
		
		return results;
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	protected Share internalRetrieveByKey(final String key) {
		List<Share> shares = this.getSimpleJdbcTemplate().query(
				"select * from shares where name = ?",
				new ShareRowMapper(), 
				key);

		Share result = (Share) DataAccessUtils.singleResult(shares);
		if(null != result) {
			List<PersistenceSharePreference> storedPreferences = retrieveSharePreferences(key);
			SharePreferences prefs = new SharePreferences();
			for(PersistenceSharePreference pref : storedPreferences) {
				prefs.addPreference(castAppropriately(pref));
			}
			result.setSharePreferences(prefs);
		}
		return result;
	}
	/**
	 * 
	 * @param key
	 * @param valid
	 * @return
	 */
	protected Share internalRetrieveByKey(final String key, final String validity) {
		List<Share> shares = this.getSimpleJdbcTemplate().query(
				"select * from shares where name = ? and valid like ?",
				new ShareRowMapper(), 
				key, 
				validity);

		Share result = (Share) DataAccessUtils.singleResult(shares);
		if(null != result) {
			List<PersistenceSharePreference> storedPreferences = retrieveSharePreferences(key);
			SharePreferences prefs = new SharePreferences();
			for(PersistenceSharePreference pref : storedPreferences) {
				ISharePreference p = castAppropriately(pref);
				prefs.addPreference(p);
			}
			result.setSharePreferences(prefs);
		}
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @param valid
	 * @return
	 */
	protected List<Share> internalRetrieveByOwner(final ICalendarAccount owner, final String validity) {
		List<Share> shares = this.getSimpleJdbcTemplate().query(
				"select * from shares where owner = ? and valid like ?",
				new ShareRowMapper(), 
				owner.getCalendarUniqueId(), 
				validity);

		for(Share s : shares) {
			List<PersistenceSharePreference> storedPreferences = retrieveSharePreferences(s.getKey());
			SharePreferences prefs = new SharePreferences();
			for(PersistenceSharePreference pref : storedPreferences) {
				ISharePreference p = castAppropriately(pref);
				prefs.addPreference(p);
			}
			s.setSharePreferences(prefs);
		}
		return shares;
	}
	
	/**
	 * 
	 * @param persistencePref
	 * @return
	 */
	protected ISharePreference castAppropriately(final PersistenceSharePreference persistencePref) {
		if(FreeBusyPreference.FREE_BUSY.equals(persistencePref.getPreferenceType())) {
			return new FreeBusyPreference();
		} else if(AccessClassificationMatchPreference.CLASS_ATTRIBUTE.equals(persistencePref.getPreferenceType())) {
			AccessClassification access = AccessClassification.valueOf(persistencePref.getPreferenceValue());
			return new AccessClassificationMatchPreference(access);
		} else if(PropertyMatchPreference.PROPERTY_MATCH.equals(persistencePref.getPreferenceType())){
			return new PropertyMatchPreference(persistencePref.getPreferenceKey(), persistencePref.getPreferenceValue());
		} else {
			LOG.warn("could not match any preference types for " + persistencePref + ", returning null");
			return null;
		}
	}
}
