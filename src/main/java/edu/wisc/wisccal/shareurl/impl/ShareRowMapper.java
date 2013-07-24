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
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import edu.wisc.wisccal.shareurl.domain.Share;
import edu.wisc.wisccal.shareurl.domain.SharePreferences;

/**
 * {@link ParameterizedRowMapper} implementation for {@link Share}
 * objects.
 * 
 * Does not set any {@link SharePreferences}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ShareRowMapper.java 1441 2009-12-17 19:09:21Z npblair $
 */
public class ShareRowMapper implements RowMapper<Share> {

	protected static final String VALID = "Y";
	/*
	 * (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public Share mapRow(ResultSet rs, int rowNum) throws SQLException {
		Share share = new Share();
		share.setKey(rs.getString("NAME"));
		share.setOwnerCalendarUniqueId(rs.getString("OWNER"));
		String valid = rs.getString("VALID");
		if(VALID.equals(valid)) {
			share.setValid(true);
		} else {
			share.setValid(false);
		}
		share.setLabel(rs.getString("LABEL"));
		return share;
	}

}
