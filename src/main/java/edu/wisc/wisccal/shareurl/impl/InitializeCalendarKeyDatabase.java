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

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Simple class to help initialize the database.
 * 
 * The main method of this class attempts to load a Spring
 * {@link ApplicationContext} from the filename on the classpath
 * specified in the <i>edu.wisc.wisccal.calendarkey.impl.InitializeCalendarKeyDatabase.CONFIG</i>
 * {@link System} property.
 * The default value for this property is "database-SAMPLE.xml" (root package on the classpath).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: InitializeCalendarKeyDatabase.java 1441 2009-12-17 19:09:21Z npblair $
 */
public final class InitializeCalendarKeyDatabase {

	public static final String CONFIG = System.getProperty(
			"edu.wisc.wisccal.calendarkey.impl.InitializeCalendarKeyDatabase.CONFIG", 
			"database-SAMPLE.xml");
	private static Log LOG = LogFactory.getLog(InitializeCalendarKeyDatabase.class);
	private JdbcTemplate jdbcTemplate;
	/**
	 * 
	 * @param dataSource
	 */
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		LOG.info("loading applicationContext: " + CONFIG);
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		
		InitializeCalendarKeyDatabase init = new InitializeCalendarKeyDatabase();
		init.setDataSource((DataSource) context.getBean("dataSource"));

		Resource createDdl = (Resource) context.getBean("createDdl");
		Resource destroyDdl = (Resource) context.getBean("destroyDdl");
		
		String destroySql = IOUtils.toString(destroyDdl.getInputStream());
		String createSql = IOUtils.toString(createDdl.getInputStream());
		
		try {
			init.executeDdl(destroySql);
			LOG.warn("existing tables removed");
		} catch (Exception e) {
			// ignore, database has no data
			LOG.info("destroyDdl skipped; no existing tables");
		}
		
		init.executeDdl(createSql);
		LOG.info("database initialization complete");
	}

	/**
	 * 
	 * @param sql
	 */
	protected void executeDdl(final String sql) {
		LOG.debug("attempting to execute: " + sql);
		this.jdbcTemplate.execute(sql);
	}
}
