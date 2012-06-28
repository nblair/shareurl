/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
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

package edu.wisc.wisccal.shareurl.domain.simple;

/**
 * Simplified representation of an event Attendee.
 * 
 * @author Nicholas Blair
 */
public class Attendee extends EventParticipant {

	private String participationStatus;

	/**
	 * @return the participationStatus
	 */
	public String getParticipationStatus() {
		return participationStatus;
	}
	/**
	 * @param participationStatus the participationStatus to set
	 */
	public void setParticipationStatus(String participationStatus) {
		this.participationStatus = participationStatus;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((participationStatus == null) ? 0 : participationStatus
						.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attendee other = (Attendee) obj;
		if (participationStatus == null) {
			if (other.participationStatus != null)
				return false;
		} else if (!participationStatus.equals(other.participationStatus))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Attendee [participationStatus=" + participationStatus
				+ ", toString()=" + super.toString() + "]";
	}
	
}
