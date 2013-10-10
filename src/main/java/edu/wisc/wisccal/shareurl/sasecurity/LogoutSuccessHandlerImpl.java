package edu.wisc.wisccal.shareurl.sasecurity;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author Nicholas Blair
 * @version $Id: LogoutSuccessHandlerImpl.java $
 */
public class LogoutSuccessHandlerImpl extends AbstractAuthenticationTargetUrlRequestHandler
	implements LogoutSuccessHandler {

	private Log log = LogFactory.getLog(this.getClass());
	private String netidLogout = "/netid_logout";
	private String mailPlusLogout = "/mailplus-logout.html";
	
	/**
	 * @return the netidLogout
	 */
	public String getNetidLogout() {
		return netidLogout;
	}
	/**
	 * @param netidLogout the netidLogout to set
	 */
	public void setNetidLogout(String netidLogout) {
		this.netidLogout = netidLogout;
	}
	/**
	 * @return the mailPlusLogout
	 */
	public String getMailPlusLogout() {
		return mailPlusLogout;
	}
	/**
	 * @param mailPlusLogout the mailPlusLogout to set
	 */
	public void setMailPlusLogout(String mailPlusLogout) {
		this.mailPlusLogout = mailPlusLogout;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.web.authentication.logout.LogoutSuccessHandler#onLogoutSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
		log.debug("onLogoutSuccess for " + authentication + ", REMOTE_USER= " + request.getRemoteUser());
		CalendarAccountUserDetails account = (CalendarAccountUserDetails) authentication.getPrincipal();
		
		String targetUrl = determineLogoutUrl(account);
		
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
	
	/**
	 * 
	 * @param account
	 * @return
	 */
	protected String determineLogoutUrl(CalendarAccountUserDetails account) {
		List<String> objectclasses = account.getCalendarAccount().getAttributeValues("objectclass");
		if(objectclasses != null && objectclasses.contains("wiscedumailplusaccount")) {
			return mailPlusLogout;
		}
		return netidLogout;
	}

}
