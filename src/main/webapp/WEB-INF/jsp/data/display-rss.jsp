<%@ page contentType="text/xml;charset=UTF-8" %><?xml version="1.0"?>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<rss version="2.0">
  <channel>
    <title>Shared Calendar</title>
    <link><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/share/${shareId}/${datePhrase}"/></link>
    <description>Shared Calendar</description>
	<c:forEach var="event" items="${allEvents}" varStatus="itemCount">
    <item>
      <title><c:out value="${event.summary.value}"/>&#160;[<fmt:formatDate value="${event.startDate.date}" type="date" pattern="M/d"/>]</title> 
      <description>
      <![CDATA[
      <c:out value="${event.description.value}"/>
      <c:if test="${!empty event.location}">
      [LOCATION: <c:out value="${event.location.value}"/>]
      </c:if>
      ]]></description>
      <pubDate><fmt:formatDate value="${event.startDate.date}" type="time" pattern="EEE, dd MMM yyyy HH:mm:ss z"/></pubDate>
      <guid><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/share/${shareId}/${datePhrase}/${event.uid.value}"/></guid>   
      <link><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/share/${shareId}/${datePhrase}/${event.uid.value}"/></link>
    </item>
    </c:forEach>
  </channel>
</rss>
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