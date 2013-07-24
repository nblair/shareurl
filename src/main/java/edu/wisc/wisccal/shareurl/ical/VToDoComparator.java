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

import net.fortuna.ical4j.model.component.VToDo;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * {@link Comparator} for {@link VToDo}s.
 * Sort order:
 * <ol>
 * <li>due date</li>
 * <li>percent complete<li>
 * <li>summary</li>
 * </ol>
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VToDoComparator.java 1876 2010-04-07 17:01:09Z npblair $
 */
public class VToDoComparator implements Comparator<VToDo>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(VToDo first, VToDo second) {
		CompareToBuilder builder = new CompareToBuilder();
		
		String firstDueDate = first.getDue() != null ? first.getDue().getValue() : null;
		String secondDueDate = second.getDue() != null ? second.getDue().getValue() : null;
		builder.append(firstDueDate, secondDueDate);
		
		String firstPercentComplete = first.getPercentComplete() != null ? first.getPercentComplete().getValue() : null;
		String secondPercentComplete = second.getPercentComplete() != null ? second.getPercentComplete().getValue() : null;
		builder.append(firstPercentComplete, secondPercentComplete);
		
		String firstSummary = first.getSummary() != null ? first.getSummary().getValue() : null;
		String secondSummary = second.getSummary() != null ? second.getSummary().getValue() : null;
		builder.append(firstSummary, secondSummary);
		
		return builder.toComparison();
	}

}
