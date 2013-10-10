package edu.wisc.wisccal.shareurl.domain;


/**
 * @author Nicholas Blair
 */
public class IncludeParticipantsPreference extends AbstractSharePreference {

	public static final String INCLUDE_PARTICIPANTS = "INCLUDE_PARTICIPANTS";

	/**
	 * 
	 */
	private static final long serialVersionUID = -3186156396469538805L;

	private final boolean includeParticipants;
	
	
	/**
	 * @param includeParticipants
	 */
	public IncludeParticipantsPreference(boolean includeParticipants) {
		super();
		this.includeParticipants = includeParticipants;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getType()
	 */
	@Override
	public String getType() {
		return INCLUDE_PARTICIPANTS;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getKey()
	 */
	@Override
	public String getKey() {
		return INCLUDE_PARTICIPANTS;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getValue()
	 */
	@Override
	public String getValue() {
		return Boolean.toString(includeParticipants);
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Include Participants";
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public final boolean participatesInFiltering() {
		return false;
	}

}
