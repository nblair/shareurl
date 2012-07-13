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
package edu.wisc.wisccal.shareurl.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.CommonDateOperations;
import org.springframework.web.util.UrlPathHelper;

import edu.wisc.wisccal.shareurl.domain.Share;

/**
 * Class that binds {@link HttpServletRequest} attributes and
 * parameters to {@link Share}s and features.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ShareRequestDetails.java 3437 2011-10-25 15:29:17Z npblair $
 */
public final class ShareRequestDetails implements IShareRequestDetails {

	public static final String START = "start";
	public static final String END = "end";
	public static final String PERSONAL = "personal";
	public static final String ATTENDING = "attending";
	public static final String ORGANIZING = "organizing";

	private static Log LOG = LogFactory.getLog(ShareRequestDetails.class);

	public static final String RSS = "rss";
	public static final String ICAL = "ical";
	public static final String JSON = "json";
	public static final String ASTEXT = "asText";

	public static final String USER_AGENT = "User-Agent";
	public static final String COMPATIBILITY_PARAM = "compat";
	public static final String BREAK_RECURRENCE = "br";
	public static final String CONVERT_CLASS = "cc";
	public static final String NO_RECURRENCE = "nr";

	static final String UW_SUPPORT_RDATE = "uw-support-rdate";

	public static final String CLIENT_PARAM = "client";

	public static final String GOOGLEBOT_REGEX = ".*googlebot.*";
	private static final Pattern GOOGLEBOT_PATTERN = Pattern.compile(GOOGLEBOT_REGEX, Pattern.CASE_INSENSITIVE);
	private static final String GOOGLE_CLIENT = "google";

	public static final String APPLE_ICAL_REGEX = ".*iCal\\/.*Mac OS X\\/10.*";
	private static final Pattern APPLE_ICAL_PATTERN = Pattern.compile(APPLE_ICAL_REGEX, Pattern.CASE_INSENSITIVE);
	private static final String APPLE_CLIENT = "apple";

	public static final String APPLE_IPOD_REGEX = "^DataAccess\\/1.0\\s\\(.*\\)$";
	private static final Pattern APPLE_IPOD_PATTERN = Pattern.compile(APPLE_IPOD_REGEX, Pattern.CASE_INSENSITIVE);

	public static final String APPLE_IOS5_REGEX = "^iOS\\/\\d\\.\\d.*";
	private static final Pattern APPLE_IOS5_PATTERN = Pattern.compile(APPLE_IOS5_REGEX, Pattern.CASE_INSENSITIVE);

	public static final String APPLE_IOS_SAFARI_REGEX = "^.*iPhone OS.*Mac OS X.*";
	private static final Pattern APPLE_IOS_SAFARI_PATTERN = Pattern.compile(APPLE_IOS_SAFARI_REGEX, Pattern.CASE_INSENSITIVE);

	public static final String MOZILLA_REGEX = "^Mozilla.*(Sunbird|Lightning|Thunderbird)+.*";
	private static final Pattern MOZILLA_PATTERN = Pattern.compile(MOZILLA_REGEX);
	private static final String MOZILLA_CLIENT = "mozilla";

	private static final String MINUS = "-";
	/**
	 * Default date phrase is "dr(0,0)", which translates to "today".
	 */
	public static final String DEFAULT_DATE_PHRASE = "dr(0,0)";
	/**
	 * Max range is how many days a request may span.
	 */
	public static final int MAX_RANGE = 180;
	public static final String DR_REGEX = "^dr\\((-?)(\\d+),(-?)(\\d+)\\)$";
	private static final Pattern DR_PATTERN = Pattern.compile(DR_REGEX);

	public static final String DATE_REGEX_1 = "\\d{4}-\\d{2}-\\d{2}";
	public static final String DATE_REGEX_2 = "\\d{8}";
	private static final Pattern DATE_PATTERN_1 = Pattern.compile(DATE_REGEX_1);
	private static final Pattern DATE_PATTERN_2 = Pattern.compile(DATE_REGEX_2);

	private final PathData pathData;
	private Client client;
	private final ShareDisplayFormat displayFormat;
	private boolean overrideBreakRecurrence = false;
	private boolean overrideConvertClass = false;
	private boolean requestNoRecurrence = false;
	private boolean organizerOnly = false;
	private boolean attendeeOnly = false;
	private boolean personalOnly = false;
	private boolean requiresProblemRecurringPreference = false;

	/**
	 * 
	 * @param request
	 */
	public ShareRequestDetails(final HttpServletRequest request) {
		String requestPath = new UrlPathHelper().getPathWithinServletMapping(request);
		if(requestPath.startsWith("/")) {
			requestPath = requestPath.substring(1, requestPath.length());
		}
		requestPath = requestPath.trim();

		// sharekey[, eventId, daterange]
		this.pathData = extractPathData(requestPath);
		// if dr == 0,0, check for start/end query parameters 
		if(DEFAULT_DATE_PHRASE.equals(getDatePhrase())) {
			String startValue = request.getParameter(START);
			DateFormat possibleFormat = getDateFormat(startValue);
			if(possibleFormat != null) {
				Date startDate = safeParse(possibleFormat.getSimpleDateFormat(), startValue);
				if(startDate != null) {
					this.pathData.setStartDate(CommonDateOperations.beginningOfDay(startDate));
					String endValue = request.getParameter(END);
					possibleFormat = getDateFormat(endValue);
					if(possibleFormat == null) {
						this.pathData.setEndDate(CommonDateOperations.endOfDay(startDate));
					} else {
						Date endDate = safeParse(possibleFormat.getSimpleDateFormat(), endValue);
						if(endDate != null) {
							this.pathData.setEndDate(CommonDateOperations.endOfDay(endDate));
						} else {
							this.pathData.setEndDate(CommonDateOperations.endOfDay(startDate));
						}
					}
				}
			}
		}
		// identify displayFormat
		this.displayFormat = determineDisplayFormat(request);

		// only care about user-agent and request parameters if iCalendar is the output format
		if(this.displayFormat.isIcalendar()) {
			// sniff user-agent to predict client
			String userAgent = request.getHeader(USER_AGENT);
			this.client = predictClientFromUserAgent(userAgent);

			// examine request parameters to override
			String [] compatValues = request.getParameterValues(COMPATIBILITY_PARAM);
			if(null != compatValues) {
				for(String compatValue : compatValues) {
					if(BREAK_RECURRENCE.equals(compatValue)) {
						overrideBreakRecurrence = true;
					} else if(CONVERT_CLASS.equals(compatValue)) {
						overrideConvertClass = true;
					} else if (NO_RECURRENCE.equals(compatValue)) {
						requestNoRecurrence = true;
					}
				}
			}
			String clientValue = request.getParameter(CLIENT_PARAM);
			if(StringUtils.isNotBlank(clientValue)) {
				if(GOOGLE_CLIENT.equals(clientValue)) {
					this.client = Client.GOOGLE;
				} else if(APPLE_CLIENT.equals(clientValue)) {
					this.client = Client.APPLE;
				} else if (MOZILLA_CLIENT.equals(clientValue)) {
					this.client = Client.MOZILLA;
				}
			}
		}

		if(request.getParameter(ORGANIZING) != null) {
			this.organizerOnly = true;
		} else if (request.getParameter(ATTENDING) != null) {
			this.attendeeOnly = true;
		} else if (request.getParameter(PERSONAL) != null) {
			this.personalOnly = true;
		}

		if(request.getParameter(UW_SUPPORT_RDATE) != null) {
			this.requiresProblemRecurringPreference = true;
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(this);
		}
	}

	/**
	 * @param pathData
	 * @param client
	 * @param displayFormat
	 * @param overrideBreakRecurrence
	 * @param overrideConvertClass
	 */
	ShareRequestDetails(PathData pathData, Client client,
			ShareDisplayFormat displayFormat, boolean overrideBreakRecurrence,
			boolean overrideConvertClass) {
		this.pathData = pathData;
		this.client = client;
		this.displayFormat = displayFormat;
		this.overrideBreakRecurrence = overrideBreakRecurrence;
		this.overrideConvertClass = overrideConvertClass;
	}

	/**
	 * 
	 * @param pathData
	 * @param client
	 * @param displayFormat
	 * @param overrideBreakRecurrence
	 * @param overrideConvertClass
	 * @param requestNoRecurrence
	 * @param organizerOnly
	 * @param attendeeOnly
	 * @param personalOnly
	 * @param requiresProblemRecurringPreference
	 */
	ShareRequestDetails(PathData pathData, Client client,
			ShareDisplayFormat displayFormat, boolean overrideBreakRecurrence,
			boolean overrideConvertClass, boolean requestNoRecurrence,
			boolean organizerOnly, boolean attendeeOnly, boolean personalOnly,
			boolean requiresProblemRecurringPreference) {
		this.pathData = pathData;
		this.client = client;
		this.displayFormat = displayFormat;
		this.overrideBreakRecurrence = overrideBreakRecurrence;
		this.overrideConvertClass = overrideConvertClass;
		this.requestNoRecurrence = requestNoRecurrence;
		this.organizerOnly = organizerOnly;
		this.attendeeOnly = attendeeOnly;
		this.personalOnly = personalOnly;
		this.requiresProblemRecurringPreference = requiresProblemRecurringPreference;
	}



	/**
	 * @return the shareKey (never null)
	 */
	public String getShareKey() {
		return this.pathData.getShareKey();
	}

	/**
	 * @return the eventId requested in the path, if defined (may return null)
	 */
	public String getEventId() {
		return this.pathData.getEventId();
	}
	/**
	 * 
	 * @return the recurrenceId requested in the path, if defined (may return null)
	 */
	public String getRecurrenceId() {
		return this.pathData.getRecurrenceId();
	}
	/**
	 * @return the endDate (never null)
	 */
	public Date getEndDate() {
		return this.pathData.getEndDate();
	}
	/**
	 * @return the startDate (never null)
	 */
	public Date getStartDate() {
		return this.pathData.getStartDate();
	}
	/**
	 * @return the datePhrase (never null)
	 */
	public String getDatePhrase() {
		return this.pathData.getDatePhrase();
	}
	/**
	 * 
	 * @return
	 */
	public ShareDisplayFormat getDisplayFormat() {
		return this.displayFormat;
	}
	/**
	 * 
	 * @return
	 */
	public boolean requiresNoRecurrence() {
		return requestNoRecurrence;
	}
	/**
	 * 
	 * @return
	 */
	public boolean requiresBreakRecurrence() {
		return overrideBreakRecurrence || Client.APPLE.equals(client) || Client.MOZILLA.equals(client);
	}
	/**
	 * 
	 * @return
	 */
	public boolean requiresConvertClass() {
		return overrideConvertClass || Client.GOOGLE.equals(client);
	}
	/**
	 * 
	 * @return
	 */
	public boolean requiresProblemRecurringPreference() {
		return requiresProblemRecurringPreference;
	}

	/**
	 * @return the organizerOnly
	 */
	public boolean isOrganizerOnly() {
		return organizerOnly;
	}
	/**
	 * @return the attendeeOnly
	 */
	public boolean isAttendeeOnly() {
		return attendeeOnly;
	}
	/**
	 * @return the personalOnly
	 */
	public boolean isPersonalOnly() {
		return personalOnly;
	}
	
	/**
	 * 
	 * @return the number of days displayed (0 or greater)
	 */
	public int getNumberDaysDisplayed() {
		return pathData.getEndDateIndex() - pathData.getStartDateIndex();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ShareRequestDetails [pathData=" + pathData + ", client="
				+ client + ", displayFormat=" + displayFormat
				+ ", overrideBreakRecurrence=" + overrideBreakRecurrence
				+ ", overrideConvertClass=" + overrideConvertClass
				+ ", getEndDate()=" + getEndDate() + ", getStartDate()="
				+ getStartDate() + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ShareRequestDetails)) {
			return false;
		}

		ShareRequestDetails rhs = (ShareRequestDetails) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(this.pathData, rhs.pathData);
		builder.append(this.displayFormat, rhs.displayFormat);
		builder.append(this.client, rhs.client);
		builder.append(this.overrideBreakRecurrence, rhs.overrideBreakRecurrence);
		builder.append(this.overrideConvertClass, rhs.overrideConvertClass);
		return builder.isEquals();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(this.pathData)
				.append(displayFormat)
				.append(client)
				.append(overrideBreakRecurrence)
				.append(overrideConvertClass)
				.toHashCode();
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	protected DateFormat getDateFormat(String value) {
		if(StringUtils.isBlank(value)) {
			return null;
		}

		Matcher m = DATE_PATTERN_1.matcher(value);
		if(m.matches()) {
			return DateFormat.PATTERN_1;
		}

		m = DATE_PATTERN_2.matcher(value);
		if(m.matches()) {
			return DateFormat.PATTERN_2;
		}

		return null;
	}

	/**
	 * Tries to return the result of {@link SimpleDateFormat#parse(String)}, returning
	 * null if a {@link ParseException} is thrown.
	 * @param df
	 * @param value
	 * @return the result of {@link SimpleDateFormat#parse(String)}, or null
	 */
	private Date safeParse(SimpleDateFormat df, String value) {
		try {
			return df.parse(value);
		} catch (ParseException e) {
			return null;
		}
	}
	/**
	 * 
	 * @param userAgent
	 * @return
	 */
	static Client predictClientFromUserAgent(String userAgent) {
		if(StringUtils.isBlank(userAgent)) {
			if(LOG.isDebugEnabled()) {
				LOG.debug(userAgent + " evaluates to " + Client.OTHER);
			}
			return Client.OTHER;
		} else {
			Matcher google = GOOGLEBOT_PATTERN.matcher(userAgent);
			if(google.matches()) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(userAgent + " evaluates to " + Client.GOOGLE);
				}
				return Client.GOOGLE;
			}
			Matcher ios5 = APPLE_IOS5_PATTERN.matcher(userAgent);
			if(ios5.matches()) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(userAgent + " evaluates to " + Client.APPLE);
				}
				return Client.APPLE;
			}
			Matcher ios = APPLE_IOS_SAFARI_PATTERN.matcher(userAgent);
			if(ios.matches()) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(userAgent + " evaluates to " + Client.APPLE);
				}
				return Client.APPLE;
			}
			Matcher ipod = APPLE_IPOD_PATTERN.matcher(userAgent);
			if(ipod.matches()) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(userAgent + " evaluates to " + Client.APPLE);
				}
				return Client.APPLE;
			}

			Matcher apple = APPLE_ICAL_PATTERN.matcher(userAgent);
			if(apple.matches()) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(userAgent + " evaluates to " + Client.APPLE);
				}
				return Client.APPLE;
			}

			Matcher mozilla = MOZILLA_PATTERN.matcher(userAgent);
			if(mozilla.matches()) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(userAgent + " evaluates to " + Client.MOZILLA);
				}
				return Client.MOZILLA;
			}

			// no match
			return Client.OTHER;
		}
	}

	/**
	 * 
	 * @param requestPath
	 * @return
	 */
	static PathData extractPathData(String requestPath) {
		PathData pathData = new PathData();

		String [] tokens = requestPath.split("/");
		// shareId is always the first token
		pathData.setShareKey(tokens[0]);

		String datePhraseCandidate = "";


		if(tokens.length == 4) {
			datePhraseCandidate = tokens[1];
			pathData.setEventId(tokens[2]);
			pathData.setRecurrenceId(tokens[3]);
		} else if(tokens.length == 3) {
			// 2nd token could be either date range or uid
			Matcher matcher = DR_PATTERN.matcher(tokens[1]);
			if(matcher.matches()) {
				datePhraseCandidate = tokens[1];
				pathData.setEventId(tokens[2]);
			} else {
				pathData.setEventId(tokens[1]);
				pathData.setRecurrenceId(tokens[2]);
			}
		} else if(tokens.length == 2){
			// 2nd token could be either date range or uid
			Matcher matcher = DR_PATTERN.matcher(tokens[1]);
			if(matcher.matches()) {
				// looks like a date range
				datePhraseCandidate = tokens[1];
				// no eventId
			} else {
				// doesn't match dr pattern, treat as eventId
				pathData.setEventId(tokens[1]);
			}
		} 

		// send the datePhraseCandidate and the PathData object
		processDatePhrase(datePhraseCandidate, pathData);

		return pathData;
	}
	/**
	 * 
	 * @param request
	 * @return
	 */
	static ShareDisplayFormat determineDisplayFormat(final HttpServletRequest request) {
		String jsonAttribute = (String) request.getParameter(JSON);
		if(null != jsonAttribute) {
			return ShareDisplayFormat.JSON;
		}
		String rssViewAttribute = (String) request.getParameter(RSS);
		if(null != rssViewAttribute) {
			return ShareDisplayFormat.RSS;
		}
		String icalViewAttribute = (String) request.getParameter(ICAL);
		if(null != icalViewAttribute) {
			String icalTextAttribute = (String) request.getParameter(ASTEXT);
			if(null != icalTextAttribute) {
				return ShareDisplayFormat.ICAL_ASTEXT;
			}
			else {
				return ShareDisplayFormat.ICAL;
			}
		}
		// neither ical or rss present, return the HTML view
		return ShareDisplayFormat.HTML;
	}

	/**
	 * Mutates the {@link PathData} argument, calling {@link PathData#setStartDate(Date)}
	 * and {@link PathData#setEndDate(Date)} as appropriate.
	 * 
	 * @param datePhraseCandidate
	 * @param pathData
	 */
	static void processDatePhrase(final String datePhraseCandidate, final PathData pathData) {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		// one to represent the end of the day
		// use clone to ensure that start and end are the same date (DD-MM-YYYY)
		Calendar end = (Calendar) start.clone();	
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 0);

		if(StringUtils.isBlank(datePhraseCandidate)) {
			//short circuit and return default
			pathData.setDatePhrase(DEFAULT_DATE_PHRASE);
			pathData.setStartDate(start.getTime());
			pathData.setEndDate(end.getTime());
			return;
		}

		int startDays = 0;
		int endDays = 0;
		Matcher matcher = DR_PATTERN.matcher(datePhraseCandidate);
		if(matcher.matches()) {
			try {
				// group 1 tests for presence of "-" before first digit
				String firstDash = matcher.group(1);
				// group 2 is the first digit (corresponds with startDate)
				startDays = Integer.parseInt(matcher.group(2));
				if(MINUS.equals(firstDash)) {
					// minus exists, negate startDays
					startDays = 0 - startDays;
				}
				// group 3 tests for presence of "-" before second digit
				String secondDash = matcher.group(3);
				// group 4 is the first digit (corresponds with startDate)
				endDays = Integer.parseInt(matcher.group(4));
				if(MINUS.equals(secondDash)) {
					// minus exists, negate endDays
					endDays = 0 - endDays;
				}
				// lastly test if we are spanning more than MAX_RANGE
				// if yes, then set endDays to startDays + MAX.
				if(endDays - startDays > MAX_RANGE) {
					int newEndDays = startDays + MAX_RANGE;
					LOG.debug("endDays (" + endDays + ") - startDays (" + startDays + ") is greater than maxRange, resetting endDays to " + newEndDays);
					endDays = newEndDays;
				}
				if(LOG.isDebugEnabled()) {
					LOG.debug("found dr: " + startDays + ", " + endDays);
				}
			} catch (NumberFormatException e) {
				startDays = 0;
				endDays = 0;
				LOG.warn("caught NumberFormatException, resetting to dr(0,0)", e);
			}
		}

		pathData.setStartDateIndex(startDays);
		pathData.setEndDateIndex(endDays);
		// roll start back specified days
		start.add(Calendar.DATE, startDays);
		// roll end forward specified days
		end.add(Calendar.DATE, endDays);

		// build valid date phrase from start and end
		StringBuilder datePhraseBuilder = new StringBuilder();
		datePhraseBuilder.append("dr(");
		datePhraseBuilder.append(startDays);
		datePhraseBuilder.append(",");
		datePhraseBuilder.append(endDays);
		datePhraseBuilder.append(")");

		// overwrite datePhrase
		pathData.setDatePhrase(datePhraseBuilder.toString());
		pathData.setStartDate(start.getTime());
		pathData.setEndDate(end.getTime());
	}

	/**
	 * 
	 *
	 *  
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: ShareRequestDetails.java 3437 2011-10-25 15:29:17Z npblair $
	 */
	protected static enum Client {
		GOOGLE,
		APPLE,
		MOZILLA,
		OTHER;
	}

	/**
	 * 
	 * @author Nicholas Blair
	 */
	protected static enum DateFormat {

		PATTERN_1("yyyy-MM-dd"),
		PATTERN_2("yyyyMMdd");

		private String format;

		/**
		 * @param format
		 */
		private DateFormat(String format) {
			this.format = format;
		}

		/**
		 * @return the format
		 */
		public String getFormat() {
			return format;
		}
		/**
		 * 
		 * @return a new {@link SimpleDateFormat} instance for this format
		 */
		public SimpleDateFormat getSimpleDateFormat() {
			return new SimpleDateFormat(format);
		}
	}
	/**
	 * 
	 *
	 *  
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: ShareRequestDetails.java 3437 2011-10-25 15:29:17Z npblair $
	 */
	protected static class PathData {
		private String shareKey;
		private String eventId;
		private String recurrenceId;
		private String datePhrase;
		private Date startDate;
		private Date endDate;
		private int startDateIndex = 0;
		private int endDateIndex = 0;
		/**
		 * @return the shareKey
		 */
		public String getShareKey() {
			return shareKey;
		}
		/**
		 * @param shareKey the shareKey to set
		 */
		public void setShareKey(String shareKey) {
			this.shareKey = shareKey;
		}
		/**
		 * @return the eventId
		 */
		public String getEventId() {
			return eventId;
		}
		/**
		 * @param eventId the eventId to set
		 */
		public void setEventId(String eventId) {
			this.eventId = eventId;
		}
		/**
		 * @return the datePhrase
		 */
		public String getDatePhrase() {
			return datePhrase;
		}
		/**
		 * @param datePhrase the datePhrase to set
		 */
		public void setDatePhrase(String datePhrase) {
			this.datePhrase = datePhrase;
		}
		/**
		 * @return the startDate
		 */
		public Date getStartDate() {
			return startDate;
		}
		/**
		 * @param startDate the startDate to set
		 */
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		/**
		 * @return the endDate
		 */
		public Date getEndDate() {
			return endDate;
		}
		/**
		 * @param endDate the endDate to set
		 */
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		/**
		 * @return the startDateIndex
		 */
		public int getStartDateIndex() {
			return startDateIndex;
		}
		/**
		 * @param startDateIndex the startDateIndex to set
		 */
		public void setStartDateIndex(int startDateIndex) {
			this.startDateIndex = startDateIndex;
		}
		/**
		 * @return the endDateIndex
		 */
		public int getEndDateIndex() {
			return endDateIndex;
		}
		/**
		 * @param endDateIndex the endDateIndex to set
		 */
		public void setEndDateIndex(int endDateIndex) {
			this.endDateIndex = endDateIndex;
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
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((datePhrase == null) ? 0 : datePhrase.hashCode());
			result = prime * result
					+ ((endDate == null) ? 0 : endDate.hashCode());
			result = prime * result + endDateIndex;
			result = prime * result
					+ ((eventId == null) ? 0 : eventId.hashCode());
			result = prime * result
					+ ((recurrenceId == null) ? 0 : recurrenceId.hashCode());
			result = prime * result
					+ ((shareKey == null) ? 0 : shareKey.hashCode());
			result = prime * result
					+ ((startDate == null) ? 0 : startDate.hashCode());
			result = prime * result + startDateIndex;
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PathData other = (PathData) obj;
			if (datePhrase == null) {
				if (other.datePhrase != null)
					return false;
			} else if (!datePhrase.equals(other.datePhrase))
				return false;
			if (endDate == null) {
				if (other.endDate != null)
					return false;
			} else if (!endDate.equals(other.endDate))
				return false;
			if (endDateIndex != other.endDateIndex)
				return false;
			if (eventId == null) {
				if (other.eventId != null)
					return false;
			} else if (!eventId.equals(other.eventId))
				return false;
			if (recurrenceId == null) {
				if (other.recurrenceId != null)
					return false;
			} else if (!recurrenceId.equals(other.recurrenceId))
				return false;
			if (shareKey == null) {
				if (other.shareKey != null)
					return false;
			} else if (!shareKey.equals(other.shareKey))
				return false;
			if (startDate == null) {
				if (other.startDate != null)
					return false;
			} else if (!startDate.equals(other.startDate))
				return false;
			if (startDateIndex != other.startDateIndex)
				return false;
			return true;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "PathData [shareKey=" + shareKey + ", eventId=" + eventId
					+ ", recurrenceId=" + recurrenceId + ", datePhrase="
					+ datePhrase + ", startDate=" + startDate + ", endDate="
					+ endDate + ", startDateIndex=" + startDateIndex
					+ ", endDateIndex=" + endDateIndex + "]";
		}


	}
}
