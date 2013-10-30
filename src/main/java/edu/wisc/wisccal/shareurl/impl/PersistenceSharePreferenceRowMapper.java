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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistenceSharePreferenceRowMapper.java 1677 2010-02-08 18:31:01Z npblair $
 */
public class PersistenceSharePreferenceRowMapper implements
		RowMapper<PersistenceSharePreference> {

	protected static final String PREF_TYPE_COLUMN = "PREFERENCE_TYPE";
	protected static final String PREF_KEY_COLUMN = "PREFERENCE_KEY";
	protected static final String PREF_VALUE_COLUMN = "PREFERENCE_VALUE";
	protected static final String SHARE_KEY_COLUMN = "SHAREKEY";
	/*
	 * (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public PersistenceSharePreference mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		PersistenceSharePreference result = new PersistenceSharePreference();
		result.setPreferenceType(rs.getString(PREF_TYPE_COLUMN));
		result.setPreferenceKey(rs.getString(PREF_KEY_COLUMN));
		result.setPreferenceValue(rs.getString(PREF_VALUE_COLUMN));
		result.setShareKey(rs.getString(SHARE_KEY_COLUMN));
		return result;
	}

}
