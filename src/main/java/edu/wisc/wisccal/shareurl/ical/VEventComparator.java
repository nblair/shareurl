/*******************************************************************************
*  Copyright 2007-2010 The Board of Regents of the University of Wisconsin System.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*******************************************************************************/
package edu.wisc.wisccal.shareurl.ical;

import java.io.Serializable;
import java.util.Comparator;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Compares VEvent objects first by start date, end date, then by summary.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VEventComparator.java 1722 2010-02-15 22:01:26Z npblair $
 */
public class VEventComparator implements Comparator<VEvent>, Serializable {

	private static final long serialVersionUID = 53706L;

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(VEvent first, VEvent second) {
		// compare start dates
		// then compare summaries
		CompareToBuilder builder = new CompareToBuilder();
		Date firstStartDate = first.getStartDate().getDate();
		Date secondStartDate = second.getStartDate().getDate();
		builder.append(firstStartDate, secondStartDate);
		
		Date firstEndDate = first.getEndDate(true).getDate();
		Date secondEndDate = second.getEndDate(true).getDate();
		builder.append(firstEndDate, secondEndDate);
		
		String firstSummary = first.getSummary() != null ? first.getSummary().getValue() : null;
		String secondSummary = second.getSummary() != null ? second.getSummary().getValue() : null;
		if(null != firstSummary || null != secondSummary) {
			builder.append(firstSummary, secondSummary);
		}
		return builder.toComparison();
	}
}
