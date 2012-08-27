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
<rs:resourceURL var="starIcon" value="/rs/famfamfam/silk/1.3/star.png"/>
<rs:resourceURL var="clockIcon" value="/rs/famfamfam/silk/1.3/clock.png"/>
<rs:resourceURL var="noteIcon" value="/rs/famfamfam/silk/1.3/note.png"/>
<rs:resourceURL var="flagIcon" value="/rs/famfamfam/silk/1.3/flag_blue.png"/>
<rs:resourceURL var="taskIcon" value="/rs/famfamfam/silk/1.3/table_edit.png"/>
<rs:resourceURL var="nextIcon" value="/rs/famfamfam/silk/1.3/resultset_next.png"/>
<rs:resourceURL var="prevIcon" value="/rs/famfamfam/silk/1.3/resultset_previous.png"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width" /> 
<link rel="stylesheet" type="text/css" href="<c:url value="/css/calendar-theme2.css"/>" media="all"/>
<fmt:formatDate value="${startDate}" type="time" pattern="MM/dd/yyyy" var="startDateFormatted"/>
<fmt:formatDate value="${endDate}" type="time" pattern="MM/dd/yyyy" var="endDateFormatted"/>
<c:choose>
<c:when test="${requestDetails.numberDaysDisplayed > 0 }">
<c:set var="title" value="Agenda for ${startDateFormatted} - ${endDateFormatted}"/>
</c:when>
<c:otherwise>
<c:set var="title" value="Agenda for ${startDateFormatted}"/>
</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${requestDetails.canonical}">
<c:url value="/u/${shareId}?rss&${requestDetails.canonicalStartEndEncoded}" var="rssFeed"/>
</c:when>
<c:otherwise>
<c:url value="/u/${shareId}/${datePhrase}?rss" var="rssFeed"/>
</c:otherwise>
</c:choose>
<title>${title}</title>
<link rel="alternate" title="${title}" href="${rssFeed}" type="application/rss+xml" />
<style type="text/css">
.cancel {text-decoration: line-through;}
.white {color: #fff !important;}
</style>
</head>
<body>
<div id="content">
<div id="timeHeader">
<div class="navrow1 sharedaterange">
<c:choose>
<c:when test="${requestDetails.numberDaysDisplayed == 0}">
<c:choose>
<c:when test="${requestDetails.canonical }">
<span class="nowshowing">${startDateFormatted}</span>
</c:when>
<c:otherwise>
<a href="<c:url value="/u/${shareId}/${requestDetails.prevDatePhrase}"/>" title="previous day"><img src="${prevIcon}" alt="previous day"/></a>
&nbsp;<span class="nowshowing">${startDateFormatted}</span>&nbsp;
<a href="<c:url value="/u/${shareId}/${requestDetails.nextDatePhrase}"/>" title="next day"><img src="${nextIcon}" alt="next day"/></a>
</c:otherwise>
</c:choose>
</c:when>
<c:otherwise>
<span class="nowshowing">${startDateFormatted}&nbsp;-&nbsp;${endDateFormatted}</span>
</c:otherwise>
</c:choose>
<a href="${rssFeed}" title="RSS Feed for this calendar"><img src="<c:url value="/img/feed_icon_16x16.gif"/>"/></a>
</div>
</div>
<div id="calendarEvents">
<c:choose>
<c:when test="${empty allEvents}">
<span class="weak summary">No events.</span>
</c:when>	
<c:otherwise>		
	
	<c:forEach var="event" items="${allEvents}">
	<%-- 
	<c:choose>
	<c:when test="${oevent:isCancelled(event) or event.declinedAttendee}">
	<c:set var="statusClass" value="cancel"/>
	</c:when>
	<c:otherwise>
	<c:set var="statusClass" value=""/>
	</c:otherwise>
	</c:choose>
	--%>
	
	<!-- begin ${event.uid.value} -->
	<div class="${statusClass}">
	<c:choose>
	<c:when test="${oevent:isDayEvent(event) == true}">
	<img src="${flagIcon}" alt="Event" title="Event"/>
	<span class="weak"><fmt:formatDate value="${event.startDate.date}" type="time" pattern="MM/dd/yyyy"/>&nbsp;All day</span>
	</c:when>
	<c:otherwise>
	<img src="${clockIcon}" alt="Meeting" title="Meeting"/>
	<span class="weak"><fmt:formatDate value="${event.startDate.date}" type="time" pattern="MM/dd/yyyy"/>&nbsp;<fmt:formatDate value="${event.startDate.date}" type="time" pattern="hh:mm a"/> - <fmt:formatDate value="${event.endDate.date}" type="time" pattern="hh:mm a"/></span>
	</c:otherwise>
	</c:choose>
	<br/>
	<fmt:formatDate value="${event.startDate.date}" type="time" pattern="yyyy-MM-dd" var="startParam"/>
	<fmt:formatDate value="${event.endDate.date}" type="time" pattern="yyyy-MM-dd" var="endParam"/>
	<c:choose>
	<c:when test="${empty event.recurrenceId}">
	<c:url value="/u/${shareId}/${event.uid.value}?start=${startParam}&end=${endParam}" var="eventUrl"/>
	</c:when>
	<c:otherwise>
	<c:url value="/u/${shareId}/${event.uid.value}/${event.recurrenceId.value}?start=${startParam}&end=${endParam}" var="eventUrl"/>
	</c:otherwise>
	</c:choose>
	
	<a title="event details" href="${eventUrl}">
	<span class="summary">
	<c:choose>
	<c:when test="${oevent:isCancelled(event)}"><del>CANCELLED:&nbsp;${event.summary.value}</del></c:when>
	<c:when test="${event.needsActionAttendee}"><i>Tentative</i>:&nbsp;${event.summary.value}</c:when>
	<c:otherwise>${event.summary.value}</c:otherwise>
	</c:choose>
	</span>
	</a>
	
	<br/>
	</div>
	<!-- end ${event.uid.value} -->
	
	</c:forEach>
				
</c:otherwise>
</c:choose>	
</div>
</div>
</body>
</html>