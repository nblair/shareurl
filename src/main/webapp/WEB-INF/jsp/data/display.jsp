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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width" /> 
<link rel="stylesheet" type="text/css" href="<c:url value="/css/calendar-theme2.css"/>" media="all"/>
<fmt:formatDate value="${startDate}" type="time" pattern="MM/dd/yyyy" var="startDateFormatted"/>
<fmt:formatDate value="${endDate}" type="time" pattern="MM/dd/yyyy" var="endDateFormatted"/>
<c:choose>
<c:when test="${requestDetails.numberDaysDisplayed > 0 }">
<title>Agenda for ${startDateFormatted} - ${endDateFormatted}</title>
</c:when>
<c:otherwise>
<title>Agenda for ${startDateFormatted}</title>
</c:otherwise>
</c:choose>
<link rel="alternate" title="Shared Calendar" href="<c:url value="/u/${shareId}/${datePhrase}?rss"/>" type="application/rss+xml" />
<style type="text/css">
.cancel {
text-decoration: line-through;
}
</style>
</head>
<body>
<div id="content">
<div id="timeHeader">
<div class="navrow1 sharedaterange">
<c:choose>
<c:when test="${requestDetails.numberDaysDisplayed > 0 }">
${startDateFormatted}&nbsp;-&nbsp;${endDateFormatted}
</c:when>
<c:otherwise>
${startDateFormatted}
</c:otherwise>
</c:choose>
</div>
</div>
<div id="calendarEvents">
<c:choose>
<c:when test="${empty allEvents}">
<span class="weak summary">No events.</span>
</c:when>	
<c:otherwise>		
	<!-- BEGIN appointments -->
	<c:forEach var="event" items="${allEvents}">
	<c:choose>
	<c:when test="${oevent:isCancelled(event)}">
	<c:set var="statusClass" value="cancel"/>
	</c:when>
	<c:otherwise>
	<c:set var="statusClass" value=""/>
	</c:otherwise>
	</c:choose>
	<div class="${statusClass}">
	<c:choose>
	<c:when test="${oevent:isDayEvent(event) == true}">
	<img src="${flagIcon}" alt="Event" title="Event"/>
	<span class="weak"><fmt:formatDate value="${event.startDate.date}" type="time" pattern="MM/dd/yyyy"/>&nbsp;All day</span>
	<br/>
	<a title="event details" href="<c:url value="/u/${shareId}/${datePhrase}/${event.uid.value}"/>"><span class="summary">${event.summary.value}</span></a>
	<br/>
	</c:when>
	<c:otherwise>
	<img src="${clockIcon}" alt="Meeting" title="Meeting"/>
	<span class="weak"><fmt:formatDate value="${event.startDate.date}" type="time" pattern="MM/dd/yyyy"/>&nbsp;<fmt:formatDate value="${event.startDate.date}" type="time" pattern="hh:mm a"/> - <fmt:formatDate value="${event.endDate.date}" type="time" pattern="hh:mm a"/></span>
	<br/>
	<a title="event details" href="<c:url value="/u/${shareId}/${datePhrase}/${event.uid.value}"/>"><span class="summary">${event.summary.value}</span></a>
	<br/>
	</c:otherwise>
	</c:choose>
	</div>
	</c:forEach>
	<!-- END appointments -->				
</c:otherwise>
</c:choose>	
</div>
</div>
</body>
</html>