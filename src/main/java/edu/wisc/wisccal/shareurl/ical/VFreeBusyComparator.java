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
import net.fortuna.ical4j.model.component.VFreeBusy;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * {@link Comparator} for {@link VFreeBusy} objects.
 *  
 * Compares start and end dates.
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VFreeBusyComparator.java 1722 2010-02-15 22:01:26Z npblair $
 */
public class VFreeBusyComparator implements Comparator<VFreeBusy>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(VFreeBusy first, VFreeBusy second) {
		CompareToBuilder builder = new CompareToBuilder();
		Date firstStartDate = first.getStartDate().getDate();
		Date secondStartDate = second.getStartDate().getDate();
		builder.append(firstStartDate, secondStartDate);
		
		Date firstEndDate = first.getEndDate().getDate();
		Date secondEndDate = second.getEndDate().getDate();
		builder.append(firstEndDate, secondEndDate);
		
		return builder.toComparison();
	}

}
