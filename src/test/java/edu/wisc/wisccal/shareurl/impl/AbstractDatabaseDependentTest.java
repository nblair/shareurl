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
/**
 * 
 */

package edu.wisc.wisccal.shareurl.impl;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Nicholas Blair
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:database-test.xml", "classpath:applicationContext-test.xml" })
public abstract class AbstractDatabaseDependentTest extends AbstractJUnit4SpringContextTests {
	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void createDatabase() throws Exception {
		Resource createDdl = (Resource) this.applicationContext.getBean("createDdl");

		String sql = IOUtils.toString(createDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@After
	public void destroyDatabase()  throws Exception {
		Resource destroyDdl = (Resource) this.applicationContext.getBean("destroyDdl");
		
		String sql = IOUtils.toString(destroyDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
		System.out.println("done destroy");
	}
}
