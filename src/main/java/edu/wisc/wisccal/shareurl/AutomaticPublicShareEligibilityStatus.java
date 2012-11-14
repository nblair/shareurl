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
	CALENDAR_INELIGIBLE("This account is ineligible for Calendar service."),
	CALENDAR_UNSEARCHABLE("This account has been marked as hidden from search via 'CalDAV discovery.'"),
	OPTED_OUT("N/A"),
	HAS_FERPA_HOLD("This account has requested privacy for the Email Address attribute through FERPA.");
	
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
