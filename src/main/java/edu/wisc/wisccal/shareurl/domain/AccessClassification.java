package edu.wisc.wisccal.shareurl.domain;

import net.fortuna.ical4j.model.property.Clazz;

/**
 * Enum to bind the iCalendar {@link Clazz} (CLASS) property with it's
 * display names in Oracle Communications Suite.
 * 
 * @author Nicholas Blair
 * @version $Id: AccessClassification.java $
 */
public enum AccessClassification {

	PUBLIC(Clazz.PUBLIC.getValue(), "Public"),
	PRIVATE(Clazz.PRIVATE.getValue(), "Private"),
	CONFIDENTIAL(Clazz.CONFIDENTIAL.getValue(), "Show Date and Time Only");
	
	private String classValue;
	private String displayName;
	private AccessClassification(String classValue, String displayName) {
		this.classValue = classValue;
		this.displayName = displayName;
	}
	/**
	 * @return the classValue
	 */
	public String getClassValue() {
		return classValue;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	
}
