package edu.wisc.wisccal.shareurl.impl.mock;

import java.util.List;

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;

public class MockExchangeCalendarAccount implements ICalendarAccountDao {

	private String calendarUniqueId;
	private String displayName;
	private String emailAddress;
	private String username;
	private boolean eligible;
	
	
	@Override
	public ICalendarAccount getCalendarAccount(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICalendarAccount getCalendarAccountFromUniqueId(
			String calendarUniqueId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICalendarAccount getCalendarAccount(String attributeName,
			String attributeValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ICalendarAccount> searchForCalendarAccounts(String searchText) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCalendarUniqueId() {
		return calendarUniqueId;
	}

	public void setCalendarUniqueId(String calendarUniqueId) {
		this.calendarUniqueId = calendarUniqueId;
	}

	public boolean isEligible() {
		return eligible;
	}

	public void setEligible(boolean eligible) {
		this.eligible = eligible;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
