package edu.wisc.wisccal.shareurl.sasecurity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@link CalendarAccountUserDetails} implementation for {@link IDelegateCalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateCalendarAccountUserDetailsImpl.java 2306 2010-07-28 17:20:12Z npblair $
 */
public class DelegateCalendarAccountUserDetailsImpl implements CalendarAccountUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private static final String EMPTY = "";
	
	private final IDelegateCalendarAccount delegateCalendarAccount;
	
	/**
	 * @param delegateCalendarAccount
	 */
	public DelegateCalendarAccountUserDetailsImpl(
			IDelegateCalendarAccount delegateCalendarAccount) {
		this.delegateCalendarAccount = delegateCalendarAccount;
	}


	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
	 */
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if(null != this.delegateCalendarAccount && this.delegateCalendarAccount.isEligible()) {
			authorities.add(SecurityConstants.CALENDAR_ELIGIBLE);
			authorities.add(SecurityConstants.DELEGATE_ACCOUNT);
		}
		
		return Collections.unmodifiableList(authorities);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getPassword()
	 */
	public String getPassword() {
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getUsername()
	 */
	public String getUsername() {
		return this.delegateCalendarAccount.getUsername();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
	 */
	public boolean isAccountNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
	 */
	public boolean isAccountNonLocked() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled() {
		return null != this.delegateCalendarAccount ? this.delegateCalendarAccount.isEligible() : false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getActiveDisplayName()
	 */
	public String getActiveDisplayName() {
		StringBuilder display = new StringBuilder();
		display.append(this.delegateCalendarAccount.getDisplayName());
		display.append(" (managed by ");
		display.append(this.delegateCalendarAccount.getAccountOwner().getCalendarLoginId());
		display.append(")");
		return display.toString();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getCalendarAccount()
	 */
	@Override
	public ICalendarAccount getCalendarAccount() {
		return getDelegateCalendarAccount();
	}
	
	/**
	 * 
	 * @return the {@link IDelegateCalendarAccount}
	 */
	public IDelegateCalendarAccount getDelegateCalendarAccount() {
		return this.delegateCalendarAccount;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#isDelegate()
	 */
	@Override
	public final boolean isDelegate() {
		return true;
	}
}
