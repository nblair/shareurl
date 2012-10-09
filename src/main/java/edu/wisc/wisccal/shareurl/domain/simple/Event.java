/**
 * 
 */
package edu.wisc.wisccal.shareurl.domain.simple;

import java.util.ArrayList;
import java.util.List;

/**
 * Simplified representation of an Event.
 * 
 * @author Nicholas Blair
 */
public class Event extends CalendarEntry {

	private String summary;
	private String location;
	private String description;
	private Organizer organizer;
	private List<Attendee> attendees = new ArrayList<Attendee>();
	private String recurrenceId;
	private String privacy;
	private boolean recurring;
	private String eventStatus;
	
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}
	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the organizer
	 */
	public Organizer getOrganizer() {
		return organizer;
	}
	/**
	 * @param organizer the organizer to set
	 */
	public void setOrganizer(Organizer organizer) {
		this.organizer = organizer;
	}
	/**
	 * @return the attendees
	 */
	public List<Attendee> getAttendees() {
		return attendees;
	}
	/**
	 * @param attendees the attendees to set
	 */
	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}
	/**
	 * @return the recurrenceId
	 */
	public String getRecurrenceId() {
		return recurrenceId;
	}
	/**
	 * @param recurrenceId the recurrenceId to set
	 */
	public void setRecurrenceId(String recurrenceId) {
		this.recurrenceId = recurrenceId;
	}
	/**
	 * @return the privacy
	 */
	public String getPrivacy() {
		return privacy;
	}
	/**
	 * @param privacy the privacy to set
	 */
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	/**
	 * @return the recurring
	 */
	public boolean isRecurring() {
		return recurring;
	}
	/**
	 * @param recurring the recurring to set
	 */
	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}
	/**
	 * @return the eventStatus
	 */
	public String getEventStatus() {
		return eventStatus;
	}
	/**
	 * @param eventStatus the eventStatus to set
	 */
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attendees == null) ? 0 : attendees.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((eventStatus == null) ? 0 : eventStatus.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((organizer == null) ? 0 : organizer.hashCode());
		result = prime * result + ((privacy == null) ? 0 : privacy.hashCode());
		result = prime * result
				+ ((recurrenceId == null) ? 0 : recurrenceId.hashCode());
		result = prime * result + (recurring ? 1231 : 1237);
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Event)) {
			return false;
		}
		Event other = (Event) obj;
		if (attendees == null) {
			if (other.attendees != null) {
				return false;
			}
		} else if (!attendees.equals(other.attendees)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (eventStatus == null) {
			if (other.eventStatus != null) {
				return false;
			}
		} else if (!eventStatus.equals(other.eventStatus)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (organizer == null) {
			if (other.organizer != null) {
				return false;
			}
		} else if (!organizer.equals(other.organizer)) {
			return false;
		}
		if (privacy == null) {
			if (other.privacy != null) {
				return false;
			}
		} else if (!privacy.equals(other.privacy)) {
			return false;
		}
		if (recurrenceId == null) {
			if (other.recurrenceId != null) {
				return false;
			}
		} else if (!recurrenceId.equals(other.recurrenceId)) {
			return false;
		}
		if (recurring != other.recurring) {
			return false;
		}
		if (summary == null) {
			if (other.summary != null) {
				return false;
			}
		} else if (!summary.equals(other.summary)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event [summary=");
		builder.append(summary);
		builder.append(", location=");
		builder.append(location);
		builder.append(", description=");
		builder.append(description);
		builder.append(", organizer=");
		builder.append(organizer);
		builder.append(", attendees=");
		builder.append(attendees);
		builder.append(", recurrenceId=");
		builder.append(recurrenceId);
		builder.append(", privacy=");
		builder.append(privacy);
		builder.append(", recurring=");
		builder.append(recurring);
		builder.append(", eventStatus=");
		builder.append(eventStatus);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
	
	
}
