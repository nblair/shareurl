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
<rs:resourceURL var="clockIcon" value="/rs/famfamfam/silk/1.3/clock.png"/>
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
<title>Free/Busy for ${startDateFormatted} - ${endDateFormatted}</title>
</c:when>
<c:otherwise>
<title>Free/Busy for ${startDateFormatted}</title>
</c:otherwise>
</c:choose>
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
<c:when test="${noEvents}">
<span class="weak summary">No events.</span>
</c:when>	
<c:otherwise>		
	<c:forEach var="period" items="${busyPeriods}">
	<img src="${clockIcon}" alt="Busy" title="Busy"/>
	<span class="weak"><fmt:formatDate value="${period.start}" type="date" pattern="MM/dd/yyyy"/>&nbsp;<fmt:formatDate value="${period.start}" type="time" pattern="hh:mm a"/>&nbsp;-&nbsp;<fmt:formatDate value="${period.end}" type="time" pattern="hh:mm a"/></span>
	<br/><span class="summary">Busy</span><br/>
	</c:forEach>	
</c:otherwise>
</c:choose>	
</div>
</div>
</body>
</html>