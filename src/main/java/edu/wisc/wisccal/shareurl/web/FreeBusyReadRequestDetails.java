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
package edu.wisc.wisccal.shareurl.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.model.CommonDateOperations;
import org.springframework.web.util.UrlPathHelper;

import edu.wisc.wisccal.shareurl.ical.CalendarDataUtils;


/**
 * Immutable java bean to extract the FreeBusy Read URL parameters from
 * an {@link HttpServletRequest}.
 *  
 * Construction is performed solely with the 1 public constructor.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: FreeBusyReadRequestDetails.java 1722 2010-02-15 22:01:26Z npblair $
 */
public class FreeBusyReadRequestDetails implements IShareRequestDetails {

	/**
	 * Name of query parameter to define the desired start date.
	 * RFC3349 Date/Time format (e.g. 2009-12-31T14:30:00-06:00)
	 */
	public static final String START_PARAM = "start";
	/**
	 * Name of query parameter to define the desired end date.
	 * RFC3349 Date/Time format (e.g. 2009-12-31T14:30:00-06:00)
	 */
	public static final String END_PARAM = "end";
	/**
	 * Name of query parameter to define the period.
	 * RFC2445 Duration (e.g. P42D)
	 */
	public static final String PERIOD_PARAM = "period";
	/**
	 * Name of query parameter the define the desired output format.
	 * Supports "text/calendar" (default), "text/html"
	 */
	public static final String FORMAT_PARAM = "format";
	/**
	 * RFC3349 Date/Time format (e.g. 2009-12-31T14:30:00-06:00)
	 */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	/**
	 * Default duration is 42 days per the specification.
	 */
	public static final String DEFAULT_DURATION = "P42D";
	/**
	 * Default output format is "text/calendar" per the specification
	 */
	public static final String DEFAULT_FORMAT = "text/calendar";
	public static final List<String> SUPPORTED_FORMATS = Arrays.asList(new String[] { DEFAULT_FORMAT, "text/html" });

	private final String shareKey;
	private final Date startDate;
	private Date endDate;
	private Period period;
	private final String format;

	/**
	 * Construct a new instance from the request.
	 * 
	 * @param request
	 * @throws FreeBusyParameterFormatException 
	 */
	public FreeBusyReadRequestDetails(final HttpServletRequest request) throws FreeBusyParameterFormatException {
		this(new UrlPathHelper().getPathWithinServletMapping(request), request.getParameterMap());
	}

	/**
	 * 
	 * @param uri
	 * @param parameterMap
	 * @throws FreeBusyParameterFormatException 
	 */
	private FreeBusyReadRequestDetails(String uri, Map<?,?> parameterMap) throws FreeBusyParameterFormatException {
		if(uri.startsWith("/")) {
			uri = uri.substring(1, uri.length());
			uri = uri.trim();
		}

		String [] tokens = uri.split("/");
		// shareKey is always the first token
		this.shareKey = tokens[0];

		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

		String startParam = extractParameterValue(parameterMap, START_PARAM);
		String endParam = extractParameterValue(parameterMap, END_PARAM);
		String periodParam = extractParameterValue(parameterMap, PERIOD_PARAM);
		String formatParam = extractParameterValue(parameterMap, FORMAT_PARAM);

		if(StringUtils.isNotEmpty(endParam) && StringUtils.isNotEmpty(periodParam)) {
			throw new FreeBusyParameterFormatException("cannot specify both end and period parameters, use only one");
		}

		if(StringUtils.isEmpty(startParam)) {
			this.startDate = new Date();
		} else {
			try {
				this.startDate = dateFormat.parse(startParam);
			} catch (ParseException e) {
				throw new FreeBusyParameterFormatException("start parameter does not match expected format: " + DATE_TIME_FORMAT, e);
			}
		}

		if(StringUtils.isNotEmpty(endParam)) {
			try {
				this.endDate = dateFormat.parse(endParam);
			} catch (ParseException e) {
				throw new FreeBusyParameterFormatException("end parameter does not match expected format: " + DATE_TIME_FORMAT, e);
			}
		} else {
			if(StringUtils.isEmpty(periodParam)) {
				periodParam = DEFAULT_DURATION;
			} 
			// treat periodParam as an iCal4j DUR
			Dur dur = new Dur(periodParam);
			this.period = new Period(new DateTime(this.startDate), dur);
		}

		if(SUPPORTED_FORMATS.contains(formatParam)) {
			this.format = formatParam;
		} else {
			this.format = DEFAULT_FORMAT;
		}

	}

	/**
	 * @return the shareKey
	 */
	public String getShareKey() {
		return shareKey;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return new Date(startDate.getTime());
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		if(null == this.endDate) {
			// interpret endDate from period
			return new Date(period.getEnd().getTime());
		} else {
			return new Date(endDate.getTime());
		}
	}

	/* (non-Javadoc)
	 * @see edu.wisc.wisccal.shareurl.web.IShareRequestDetails#isPublicUrl()
	 */
	@Override
	public boolean isPublicUrl() {
		return getShareKey().contains("@");
	}

	/**
	 * @return the period
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isICalendarOutput() {
		return DEFAULT_FORMAT.equals(this.format);
	}

	/**
	 * Converts the startDate and endDate fields into the 
	 * "dr(X,Y)" format used by {@link ShareRequestDetails}.
	 * 
	 * @see CalendarDataUtils#approximateDifference(Date, Date)
	 * @return the date phrase
	 */
	public String toShareRequestDatePhrase() {
		Date now = new Date();
		StringBuilder result = new StringBuilder();
		result.append("dr(");
		result.append(CommonDateOperations.approximateDifference(now, startDate));
		result.append(",");
		result.append(CommonDateOperations.approximateDifference(endDate, now));
		result.append(")");
		return result.toString();
	}
	/**
	 * The return value for {@link ServletRequest#getParameterMap()} is a 
	 * {@link Map} with {@link String}s for keys and {@link String} ARRAYS for values.
	 * 
	 * This method inspects the {@link Map} for presence of a value for the parameterName
	 * argument.
	 * If the returned String array is null or 0 length, this method returns null.
	 * If the returned String array is not null, the first element (index 0) is returned.
	 * 
	 * @param parameterMap
	 * @param parameterName
	 */
	private String extractParameterValue(Map<?,?> parameterMap, String parameterName) {
		String[] parameterValues = (String []) parameterMap.get(parameterName);
		if(null == parameterValues || parameterValues.length == 0) {
			return null;
		} else {
			return parameterValues[0];
		}
	}
}
