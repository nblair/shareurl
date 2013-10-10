package edu.wisc.wisccal.shareurl.web.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.wisc.wisccal.shareurl.sasecurity.CalendarAccountUserDetails;
import edu.wisc.wisccal.shareurl.sasecurity.DelegateCalendarAccountUserDetailsImpl;

/**
 * Simple {@link Controller} to display the delegate-login form.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 */
@Controller
public class DelegateLoginController  {
	protected static final String FORM_VIEW_NAME = "security/delegate-login-form";
	/**
	 * 
	 * @return the name of the view for the delegate login controller
	 */
	@RequestMapping("/delegate-login.html")
	protected String viewLoginForm() {
		CalendarAccountUserDetails currentPrincipal = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(currentPrincipal instanceof DelegateCalendarAccountUserDetailsImpl) {
			return "redirect:/resource-logout-first.html";
		}
		return FORM_VIEW_NAME;
	}

}
