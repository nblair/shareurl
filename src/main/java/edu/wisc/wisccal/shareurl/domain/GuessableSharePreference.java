package edu.wisc.wisccal.shareurl.domain;


/**
 * @author Nicholas Blair
 */
public class GuessableSharePreference extends AbstractSharePreference {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6705182554381328964L;

	/**
	 * 
	 */
	public static final String GUESSABLE = "GUESSABLE";
	/**
	 * 
	 */
	public static final String GUESS_DISPLAYNAME = "Public";
	
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getType()
	 */
	@Override
	public String getType() {
		return GUESSABLE;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getKey()
	 */
	@Override
	public String getKey() {
		return GUESSABLE;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getValue()
	 */
	@Override
	public String getValue() {
		return Boolean.TRUE.toString();
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return GUESS_DISPLAYNAME;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public boolean participatesInFiltering() {
		return false;
	}
}
