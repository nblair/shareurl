/**
 * 
 */
package edu.wisc.wisccal.shareurl.ical;

/**
 * Enum to represent an account's participation in an event.
 * 
 * @author Nicholas Blair
 */
enum EventParticipation {
	PERSONAL_EVENT,
	ORGANIZER,
	ATTENDEE_ACCEPTED,
	ATTENDEE_DECLINED,
	ATTENDEE_NEEDSACTION,
	NOT_INVOLVED;
	
	/**
	 * 
	 * @return
	 */
	boolean isAttendee() {
		return this.equals(ATTENDEE_ACCEPTED) || this.equals(ATTENDEE_DECLINED) || this.equals(ATTENDEE_NEEDSACTION);
	}
}