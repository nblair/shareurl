/**
 * 
 */

package edu.wisc.wisccal.shareurl.domain;

import java.util.List;

/**
 * Interface describing share preferences that filter content.
 * 
 * @author Nicholas Blair
 */
public interface ContentFilter {

	/**
	 * 
	 * @return the name of the calendar property this filter is intended to inspect
	 */
	String getPropertyName();
	
	/**
	 * 
	 * @return the content that will result in a "match"
	 */
	List<String> getMatchValues();
}
