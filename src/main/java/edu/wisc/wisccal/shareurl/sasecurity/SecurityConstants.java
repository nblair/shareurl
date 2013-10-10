package edu.wisc.wisccal.shareurl.sasecurity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

/**
 * This class defines the {@link GrantedAuthority} instances that constitute
 * the roles within this application.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SecurityConstants.java 2979 2011-01-25 19:24:44Z npblair $
 */
public final class SecurityConstants {

	/**
	 * Role that grants access to the application.
	 */
	public static final GrantedAuthority CALENDAR_ELIGIBLE = new GrantedAuthorityImpl("ROLE_CALENDAR_ELIGIBLE");
	/**
	 * Role given to "delegate" (resource) accounts.
	 */
	public static final GrantedAuthority DELEGATE_ACCOUNT = new GrantedAuthorityImpl("ROLE_DELEGATE_ACCOUNT");
	/**
	 * Role that grants access to the application administrative functions.
	 */
	public static final GrantedAuthority ADMINISTRATOR = new GrantedAuthorityImpl("ROLE_ADMINISTRATOR");

}
