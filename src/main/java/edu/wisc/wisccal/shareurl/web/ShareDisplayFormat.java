package edu.wisc.wisccal.shareurl.web;

/**
 * Enum to track possible display output formats.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ShareDisplayFormat.java 3131 2011-03-08 15:21:27Z npblair $
 */
public enum ShareDisplayFormat {
	HTML ("html"),
	RSS ("rss"),
	ICAL ("ical"),
	ICAL_ASTEXT("ical+asText"),
	VFB_LEGACY("vfb"),
	JSON("json"),
	MOBILECONFIG("mobileconfig");

	private String displayType;

	ShareDisplayFormat(String displayType) {
		this.displayType = displayType;
	}

	/**
	 * @return the display type
	 */
	public String getDisplayType() {
		return displayType;
	}

	/**
	 * @return true if this.equals(ICAL) or this.equals(ICAL_ASTEXT)
	 */
	public boolean isIcalendar() {
		return this.equals(ICAL) || this.equals(ICAL_ASTEXT) || this.equals(VFB_LEGACY);
	}

	/**
	 * @return true if this.equals(HTML) or this.equals(RSS) or JSON
	 */
	public boolean isMarkupLanguage() {
		return this.equals(HTML) || this.equals(RSS) || this.equals(JSON);
	}
}