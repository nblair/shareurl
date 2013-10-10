/**
 * 
 */

package edu.wisc.wisccal.shareurl.domain;

/**
 * Non persisted {@link AbstractSharePreference} that indicates a share 
 * is not revocable.
 * 
 * @author Nicholas Blair
 */
public class NonRevocablePreference extends AbstractSharePreference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NON_REVOCABLE = "NON_REVOCABLE";
	
	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getType()
	 */
	@Override
	public String getType() {
		return NON_REVOCABLE;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#getKey()
	 */
	@Override
	public String getKey() {
		return NON_REVOCABLE;
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
		return NON_REVOCABLE;
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#participatesInFiltering()
	 */
	@Override
	public boolean participatesInFiltering() {
		return false;
	}

}
