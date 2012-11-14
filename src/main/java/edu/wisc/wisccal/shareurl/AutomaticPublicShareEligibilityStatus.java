/**
 * 
 */

package edu.wisc.wisccal.shareurl;

/**
 * Enum to represent a person's eligibility for automatic public share eligibility.
 * 
 * @author Nicholas Blair
 */
public enum AutomaticPublicShareEligibilityStatus {

	ELIGIBLE("N/A"),
	CALENDAR_INELIGIBLE("Public ShareURL has not been enabled for your account because it is ineligible for Calendar service."),
	CALENDAR_UNSEARCHABLE("Public ShareURL has not been enabled for your account because it has been hidden from 'search by CalDAV discovery.'"),
	OPTED_OUT("N/A"),
	HAS_FERPA_HOLD("Public ShareURL has not been enabled for your account because you have requested privacy protection for your email address through FERPA.");
	
	private String display;
	
	private AutomaticPublicShareEligibilityStatus(String display) {
		this.display = display;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isIneligibleFromExternalSource() {
		return this.equals(CALENDAR_INELIGIBLE) || this.equals(CALENDAR_UNSEARCHABLE) || this.equals(HAS_FERPA_HOLD);
	}
	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}
}
