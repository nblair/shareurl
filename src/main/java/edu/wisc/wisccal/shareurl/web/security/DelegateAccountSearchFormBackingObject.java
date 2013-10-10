package edu.wisc.wisccal.shareurl.web.security;

import java.io.Serializable;

import org.jasig.schedassist.model.IDelegateCalendarAccount;

/**
 * Form backing object used to search for {@link IDelegateCalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateAccountSearchFormBackingObject.java 1966 2010-04-20 17:44:20Z npblair $
 */
public class DelegateAccountSearchFormBackingObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	private String searchText;
	private String delegateName;
	private boolean returnForAutocomplete = false;
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}
	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	/**
	 * @return the delegateName
	 */
	public String getDelegateName() {
		return delegateName;
	}
	/**
	 * @param delegateName the delegateName to set
	 */
	public void setDelegateName(String delegateName) {
		this.delegateName = delegateName;
	}
	/**
	 * @return the returnForAutocomplete
	 */
	public boolean isReturnForAutocomplete() {
		return returnForAutocomplete;
	}
	/**
	 * @param returnForAutocomplete the returnForAutocomplete to set
	 */
	public void setReturnForAutocomplete(boolean returnForAutocomplete) {
		this.returnForAutocomplete = returnForAutocomplete;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DelegateAccountSearchFormBackingObject [searchText="
				+ searchText + ", delegateName=" + delegateName
				+ ", returnForAutocomplete=" + returnForAutocomplete + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((delegateName == null) ? 0 : delegateName.hashCode());
		result = prime * result + (returnForAutocomplete ? 1231 : 1237);
		result = prime * result
				+ ((searchText == null) ? 0 : searchText.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelegateAccountSearchFormBackingObject other = (DelegateAccountSearchFormBackingObject) obj;
		if (delegateName == null) {
			if (other.delegateName != null)
				return false;
		} else if (!delegateName.equals(other.delegateName))
			return false;
		if (returnForAutocomplete != other.returnForAutocomplete)
			return false;
		if (searchText == null) {
			if (other.searchText != null)
				return false;
		} else if (!searchText.equals(other.searchText))
			return false;
		return true;
	}

}
