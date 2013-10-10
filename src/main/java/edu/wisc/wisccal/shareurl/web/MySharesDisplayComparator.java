/**
 * 
 */

package edu.wisc.wisccal.shareurl.web;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * @author Nicholas Blair
 */
public class MySharesDisplayComparator implements Comparator<Share> {
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Share o1, Share o2) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(!o1.isGuessable(), !o2.isGuessable());
		builder.append(o1.getKey(), o2.getKey());
		return builder.toComparison();
	}

}
