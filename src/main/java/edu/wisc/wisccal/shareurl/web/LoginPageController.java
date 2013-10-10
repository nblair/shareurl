package edu.wisc.wisccal.shareurl.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Nicholas Blair
 * @version $Id: LoginPageController.java $
 */
@Controller
public class LoginPageController {

	@RequestMapping("/login-choice.html")
	public String showLogin() {
		return "uw-login-choice";
	}
	
	@RequestMapping("/mailplus-logout.html")
	public String showMailPlusLogout() {
		return "mailplus-logout";
	}
	
	@RequestMapping("/delegateLoginFailed.html")
	public String showDelegateLoginFailed() {
		return "delegateLoginFailed";
	}
	
	/**
	 * View shown when the user requests a feature that 
	 * is not accessible to resource accounts.
	 * @return the name of the view
	 */
	@RequestMapping("/resource-logout-first.html")
	public String showResourceLogoutFirst() {
		return "security/resource-log-out-first";
	}
}
