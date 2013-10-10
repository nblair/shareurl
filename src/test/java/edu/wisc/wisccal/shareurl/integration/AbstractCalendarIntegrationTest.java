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
