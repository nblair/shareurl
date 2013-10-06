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
package edu.wisc.wisccal.shareurl.integration;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/contexts/applicationContext-test.xml" })
public class AbstractCalendarIntegrationTest {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	protected DateTime currentDt = new DateTime();
	protected DateTime startDt = currentDt.minusDays(7);
	protected DateTime endDt   = currentDt.plusDays(7);
	protected Date startDate = startDt.toDate();
	protected Date endDate = endDt.toDate();
	
	@Autowired
	@Qualifier("owner1")
	protected MockCalendarAccount ownerCalendarAccount1;

	@Test
	public void testOwnerCalendarAccount(){
		Assert.assertNotNull(ownerCalendarAccount1);
	}
}
