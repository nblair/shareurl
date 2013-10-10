package edu.wisc.wisccal.shareurl.web;

import java.util.Date;

/**
 * @author Nicholas Blair
 */
public interface IShareRequestDetails {

	/**
	 * Return the "key" that identifies the share targeted by this request.
	 * 
	 * @return the "key" that identifies the share targeted by this request
	 */
	public String getShareKey();
	/**
	 * 
	 * @return the start timestamp of the date range targeted by this request
	 */
	public Date getStartDate();
	/**
	 * 
	 * @return the end timestamp of the date range targeted by this request
	 */
	public Date getEndDate();
	/**
	 * A "public" shareurl is the shareurl that includes the account owner's email address.
	 * 
	 * @return true if this request is targeting the account's "public" shareurl.
	 */
	public boolean isPublicUrl();
}
