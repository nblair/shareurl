/**
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
