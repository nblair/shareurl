/**
 * 
 */
package edu.wisc.wisccal.shareurl.ical;

/**
 * Enum to represent an account's participation in an event.
 * 
 * @author Nicholas Blair
 */
public enum EventParticipation {
	PERSONAL_EVENT,
	ORGANIZER,
	ATTENDEE_ACCEPTED,
	ATTENDEE_DECLINED,
	ATTENDEE_NEEDSACTION,
	ATTENDEE_TENTATIVE,
	NOT_INVOLVED;
	
	/**
	 * 
	 * @return true if this equals any of the ATTENDEE prefixed values
	 */
	public boolean isAttendee() {
		return this.equals(ATTENDEE_ACCEPTED) || this.equals(ATTENDEE_DECLINED) || this.equals(ATTENDEE_NEEDSACTION) || this.equals(ATTENDEE_TENTATIVE);
	}
}