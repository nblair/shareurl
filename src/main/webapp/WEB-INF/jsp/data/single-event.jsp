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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/calendar-theme2.css"/>" media="all"/>
<title>${event.summary.value}</title>
</head>
<body>
<div id="content">
<div id="eventNav">
<a href="<c:url value="/u/${shareId}/${datePhrase}"/>" title="return to agenda">&#171;Return</a>
</div> <!-- close eventNav -->
<div class="event">
<p>
<span class="label">Summary:&nbsp;</span>
<span class="summary">${event.summary.value}</span>
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

</div> <!-- close event div -->
</div> <!-- close content div -->
</body>
</html>