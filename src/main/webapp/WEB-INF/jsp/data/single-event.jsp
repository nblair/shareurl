<%-- 
  Copyright 2007-2010 The Board of Regents of the University of Wisconsin System.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/calendar-theme2.css"/>" media="all"/>
<title>${event.summary.value}</title>
</head>
<body>
<div id="content">
<div id="eventNav">
<c:choose>
<c:when test="${not empty lastNonSingleEventRequestDetails}">
<a href="<c:url value="/u/${lastNonSingleEventRequestDetails.urlSegment}"/>" title="return to agenda">&#171;Return</a>
</c:when>
<c:otherwise>
<a href="<c:url value="/u/${requestDetails.shareKey}"/>" title="View Today's Schedule">View Today's Schedule</a>
</c:otherwise>
</c:choose>
<rs:resourceURL var="calendarIcon" value="/rs/famfamfam/silk/1.3/calendar.png"/>
<c:url value="?${requestScope['javax.servlet.forward.query_string']}" var="ics">
<c:param name="ical"/>
</c:url>
&nbsp;<a href="${ics}" title="Download as iCalendar (ICS)">Download ICS&nbsp;<img src="${calendarIcon}"/></a>
</div> <!-- close eventNav -->
<div class="event">
<p>
<span class="label">Summary:&nbsp;</span>
<span class="summary">${event.summary.value}<c:if test="${oevent:isCancelled(event)}"> - CANCELLED</c:if></span>
</p>
<div class="when">
<p>
<span class="label">When:&nbsp;</span>
<c:choose>
<c:when test="${oevent:isDayEvent(event) == true}">
<span class="dtstart"><fmt:formatDate value="${event.startDate.date}" type="date" pattern="EEE dd MMM yyyy"/></span>
</c:when>
<c:otherwise>
<span class="dtstart"><fmt:formatDate value="${event.startDate.date}" type="time" pattern="EEE dd MMM yyyy hh:mm a"/></span> to <span class="dtend"><fmt:formatDate value="${event.endDate.date}" type="time" pattern="EEE dd MMM yyyy hh:mm a"/></span>
</c:otherwise>
</c:choose>
</p>
</div>

<c:if test="${not empty event.location.value}">
<p>
<span class="label">Location:&nbsp;</span>
<span class="location">${event.location.value}</span>
</p>
</c:if>

<c:if test="${not empty descriptionSections}">
<div id="description">
<span class="label">Description:&nbsp;</span>
<c:forEach var="descriptionSection" items="${descriptionSections}" varStatus="itemCount">
${descriptionSection}<br/>
</c:forEach>
</div> <!-- close description div -->
</c:if>

<c:if test="${includeParticipants }">
<c:if test="${not empty event.organizer}">
<p>
<span class="label">Organizer:&nbsp;</span>
<span class="organizer">${oevent:getParticipantDisplayName(event.organizer)} (<a href="${event.organizer.value}">${oevent:getParticipantEmailAddress(event.organizer)}</a>)</span>
</p>
</c:if>

<c:if test="${not empty oevent:getAttendees(event)}">
<p><span class="label">Attendees:&nbsp;</span></p>
<ul>
<c:forEach items="${oevent:getAttendees(event)}" var="attendee">
<li><span class="attendee">${oevent:getParticipantDisplayName(attendee)} (<a href="${attendee.value}">${oevent:getParticipantEmailAddress(attendee)}</a>)</span>, <span class="partstat">${oevent:getParticipationStatus(attendee)}</span></li>
</c:forEach>
</ul>
</c:if>
</c:if>
</div> <!-- close event div -->
</div> <!-- close content div -->
</body>
</html>