package edu.wisc.wisccal.shareurl.domain;

public class IncludeSourceCalendarPreference extends AbstractSharePreference {

	public static final String INCLUDE_SOURCE_CALENDAR = "INCLUDE_SOURCE_CALENDAR";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5855143549140370510L;
	
	private final boolean includeSourceCalendar;
	

	public IncludeSourceCalendarPreference(boolean includeSourceCalendar) {
		super();
		this.includeSourceCalendar = includeSourceCalendar;
	}
	
	@Override
	public String getType() {
		return INCLUDE_SOURCE_CALENDAR;
	}

	@Override
	public String getKey() {
		return INCLUDE_SOURCE_CALENDAR;
	}

	@Override
	public String getValue() {
		return Boolean.toString(includeSourceCalendar);
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Include Source Calendar";
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public final boolean participatesInFiltering() {
		return false;
	}

}
