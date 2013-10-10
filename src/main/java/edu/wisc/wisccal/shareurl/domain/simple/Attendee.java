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
