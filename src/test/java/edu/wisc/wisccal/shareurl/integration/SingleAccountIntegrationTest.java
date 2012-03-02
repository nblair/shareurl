/*******************************************************************************
 *  Copyright 2008-2010 The Board of Regents of the University of Wisconsin System.
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
package edu.wisc.wisccal.shareurl.integration;

import javax.annotation.Resource;

import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.wisc.wisccal.shareurl.ICalendarAccount;
import edu.wisc.wisccal.shareurl.ICalendarAccountDao;
import edu.wisc.wisccal.shareurl.ICalendarDataDao;

/**
 * Simple integration test to retrieve and parse the calendar data for an account.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SingleAccountIntegrationTest.java $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:integration-test.xml"})
public class SingleAccountIntegrationTest {

	@Resource 
	private String accountUsername;
	@Autowired
	private ICalendarAccountDao calendarAccountDao;
	@Autowired
	private ICalendarDataDao calendarDataDao;
	@Autowired
	private Timespan timespan;
	private Log log = LogFactory.getLog(this.getClass());
	
	@Test
	public void testFetchAndParseAgenda() {
		Assert.assertFalse("accountUsername property was not properly defined; try adding -DaccountUsername=netid", StringUtils.isBlank(accountUsername));
		Assert.assertFalse("accountUsername property was not properly defined; try adding -DaccountUsername=netid", "${accountUsername}".equals(accountUsername));
		log.info("begin testFetchAndParseAgenda for " + accountUsername + " and " + timespan);
		ICalendarAccount account = calendarAccountDao.getCalendarAccount(accountUsername);
		Assert.assertNotNull("no account found for " + accountUsername, account);
		log.info(accountUsername + " resolves to " + account);
		Calendar calendar = calendarDataDao.getCalendarData(account, timespan.getStartDate(), timespan.getEndDate());
		Assert.assertNotNull("null calendar returned for " + account, calendar);
	}
}
