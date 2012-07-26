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
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.wisc.wisccal.shareurl.GuessableShareAlreadyExistsException;
import edu.wisc.wisccal.shareurl.IShareDao;
import edu.wisc.wisccal.shareurl.domain.GuessableSharePreference;
import edu.wisc.wisccal.shareurl.domain.ISharePreference;
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
		return storeNewShare(newKey, ownerUniqueId, preferences);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.IShareDao#generateGuessableShare(org.jasig.schedassist.model.ICalendarAccount, edu.wisc.wisccal.shareurl.domain.SharePreferences)
	 */
	@Override
	public Share generateGuessableShare(ICalendarAccount account,
			SharePreferences preferences)  throws GuessableShareAlreadyExistsException {
		String key = account.getEmailAddress();
		Share existing = internalRetrieveByKey(key);
		if(null != existing) {
			if(existing.isValid()) {
				throw new GuessableShareAlreadyExistsException();
			} else {
				//reset existing!
				Share reset = resetGuessableShare(existing, preferences);
				return reset;
			}
		} else {
			preferences.addPreference(new GuessableSharePreference());
			final String ownerUniqueId = account.getCalendarUniqueId();
			return storeNewShare(key, ownerUniqueId, preferences);
		}
	}
	
	/**
	 * Insert a new record in the shares table, and persist
	 * the {@link SharePreferences}
	 * 
	 * @see #storePreference(String, ISharePreference)
	 * @param key
	 * @param ownerId
	 * @param preferences
	 * @return the share
	 */
	protected Share storeNewShare(String key, String ownerId, SharePreferences preferences) {
		this.getSimpleJdbcTemplate().update(
				"insert into shares (name, owner, valid) values (?, ?, ?)", 
				key,
				ownerId,
				VALID);

		for(ISharePreference pref : preferences.getPreferences()) {
			storePreference(key, pref);
		}
		
		Share share = new Share();
		share.setKey(key);
		share.setOwnerCalendarUniqueId(ownerId);
		share.setSharePreferences(preferences);
		LOG.info("generated new share: " + share);
		return share;
	}
	
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.IShareDao#retrieveGuessableShare(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public Share retrieveGuessableShare(ICalendarAccount account) {
		Share share = internalRetrieveByKey(account.getEmailAddress(), VALID);
		if(share != null && !share.getSharePreferences().isGuessable()) {
			throw new IllegalStateException("found " + share + " with key matching email address (" + account + ") that is missing GuessableSharePreference");
		}
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

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.IShareDao#addSharePreference(edu.wisc.wisccal.shareurl.domain.Share, edu.wisc.wisccal.shareurl.domain.ISharePreference)
	 */
	@Override
	public Share addSharePreference(Share share,
			ISharePreference sharePreference) {
		if(sharePreference instanceof GuessableSharePreference) {
			// ignore
			LOG.error("ignoring addSharePreference invocation to add GuessableSharePreference for " + share);
			return share;
		}
		storePreference(share.getKey(), sharePreference);
		share.getSharePreferences().addPreference(sharePreference);
		return share;
	}
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.IShareDao#removeSharePreference(edu.wisc.wisccal.shareurl.domain.Share, edu.wisc.wisccal.shareurl.domain.ISharePreference)
	 */
	@Override
	public Share removeSharePreference(Share share,
			ISharePreference sharePreference) {
		if(sharePreference instanceof GuessableSharePreference) {
			// ignore
			LOG.error("ignoring removeSharePreference invocation to add GuessableSharePreference for " + share);
			return share;
		}
		int rows = this.getSimpleJdbcTemplate().update(
				"delete from share_preferences where sharekey=? and preference_type=? and preference_key=? and preference_value=?",
				share.getKey(),
				sharePreference.getType(),
				sharePreference.getKey(),
				sharePreference.getValue());
		if(rows == 1) {
			share.getSharePreferences().removePreference(sharePreference);
			LOG.info("successfully removed " + sharePreference + " from " + share);
		}
		
		return share;
	}
	/**
	 * Called if and only if:
	 * <ol>
	 * <li>Account owner previously created guessable share.</li>
	 * <li>Account owner previously revoked guessable share.</li>
	 * </ol>
	 * Steps 1 and 2 can be repeated any number of times; this method should only be run
	 * on guessable shares that are invalid.
	 * 
	 * Performs the following:
	 * <ol>
	 * <li>Removes the old share preferences.</li>
	 * <li>Stores the new share preferences from the argument.</li>
	 * <li>Set share valid column to 'Y'.
	 * 
	 * @param share
	 * @param preferences
	 */
	protected Share resetGuessableShare(Share share, SharePreferences preferences) {
		// step 1: delete old preferences
		this.simpleJdbcTemplate.update("delete from share_preferences where sharekey=?", share.getKey());
		// step 2: store new preferences:
		preferences.addPreference(new GuessableSharePreference());
		for(ISharePreference pref : preferences.getPreferences()) {
			storePreference(share.getKey(), pref);
		}
		// step 3: mark share as valid
		this.simpleJdbcTemplate.update("update shares set valid='Y' where name=?", share.getKey());
		
		Share result = new Share();
		result.setKey(share.getKey());
		result.setOwnerCalendarUniqueId(share.getOwnerCalendarUniqueId());
		result.setValid(true);
		result.setSharePreferences(preferences);
		LOG.info("reset guessable share complete; was: " + share + ", now: " + result);
		return result;
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
			LOG.info("stored preference " + preference + " for key " + shareKey);
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
	 * @see SharePreferences#construct(String, String, String)
	 * @param persistencePref
	 * @return
	 */
	protected ISharePreference castAppropriately(final PersistenceSharePreference persistencePref) {
		return SharePreferences.construct(persistencePref.getPreferenceType(), persistencePref.getPreferenceKey(), persistencePref.getPreferenceValue());
	}
}
