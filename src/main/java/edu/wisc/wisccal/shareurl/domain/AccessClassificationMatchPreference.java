/**
 * Copyright 2012 Board of Regents - University of Wisconsin System.
 */
package edu.wisc.wisccal.shareurl.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Clazz;

/**
 * {@link AbstractMatchPreference} to bind to the CLASS property.
 * 
 * @author Nicholas Blair
 * @version $Id: AccessLevelMatchPreference.java $
 */
public class AccessClassificationMatchPreference extends AbstractSharePreference {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4168853507989696424L;
	private final AccessClassification access;
	public static final String CLASS_ATTRIBUTE = "CLASS_ATTRIBUTE";
	protected static final String DISPLAY_NAME_PREFIX = "Privacy matches ";
	/**
	 * 
	 * @param access
	 */
	public AccessClassificationMatchPreference(AccessClassification access) {
		this.access = access;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getType()
	 */
	@Override
	public String getType() {
		return CLASS_ATTRIBUTE;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getKey()
	 */
	@Override
	public String getKey() {
		return Clazz.CLASS;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getValue()
	 */
	@Override
	public String getValue() {
		return this.access.getClassValue();
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
		sb.append(DISPLAY_NAME_PREFIX);
		sb.append("'");
		sb.append(this.access.getDisplayName());
		sb.append("'");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public boolean participatesInFiltering() {
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#matches(net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	public boolean matches(VEvent event) {
		Validate.notNull(event, "VEvent argument must not be null");
		Property p = event.getProperty(Clazz.CLASS);
		if(p == null) {
			// per RFC 2445, the default value for Classification is "PUBLIC"
			p = Clazz.PUBLIC;
		}
		if(StringUtils.equalsIgnoreCase(this.access.getClassValue(), p.getValue())) {
			return true;
		}
		
		return false;
	}

}
