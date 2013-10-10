package edu.wisc.wisccal.shareurl.impl.mock;

import java.util.List;

import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;

public class MockExchangeDelegateCalendarAccount implements
		IDelegateCalendarAccountDao {

	
	private String calendarUniqueId;
	private String displayName;
	private String emailAddress;
	private String username;
	private boolean eligible;
	
	@Override
	public List<IDelegateCalendarAccount> searchForDelegates(String searchText,
			ICalendarAccount owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IDelegateCalendarAccount> searchForDelegates(String searchText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDelegateCalendarAccount getDelegate(String accountName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDelegateCalendarAccount getDelegate(String accountName,
			ICalendarAccount owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDelegateCalendarAccount getDelegate(String attributeName,
			String attributeValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDelegateCalendarAccount getDelegateByUniqueId(String accountUniqueId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDelegateCalendarAccount getDelegateByUniqueId(
			String accountUniqueId, ICalendarAccount owner) {
		// TODO Auto-generated method stub
		return null;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCalendarUniqueId() {
		return calendarUniqueId;
	}

	public void setCalendarUniqueId(String calendarUniqueId) {
		this.calendarUniqueId = calendarUniqueId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
