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
 * @author Nicholas Blair
 */
public class Organizer extends EventParticipant {

	private String designateOrganizer;

	/**
	 * @return the designateOrganizer
	 */
	public String getDesignateOrganizer() {
		return designateOrganizer;
	}

	/**
	 * @param designateOrganizer the designateOrganizer to set
	 */
	public void setDesignateOrganizer(String designateOrganizer) {
		this.designateOrganizer = designateOrganizer;
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
				+ ((designateOrganizer == null) ? 0 : designateOrganizer
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
		Organizer other = (Organizer) obj;
		if (designateOrganizer == null) {
			if (other.designateOrganizer != null)
				return false;
		} else if (!designateOrganizer.equals(other.designateOrganizer))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Organizer [designateOrganizer=" + designateOrganizer
				+ ", toString()=" + super.toString() + "]";
	}

	
}
