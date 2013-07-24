<%--

    Copyright 2012, Board of Regents of the University of
    Wisconsin System. See the NOTICE file distributed with
    this work for additional information regarding copyright
    ownership. Board of Regents of the University of Wisconsin
    System licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ page contentType="text/xml;charset=UTF-8"%><?xml version="1.0"?>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<rss version="2.0"> <channel> <title>Shared
Calendar</title> <link>
<c:out
	value="${viewhelper:getVirtualServerAddress(pageContext.request)}" />
<c:url value="/u/${shareId}/${datePhrase}" /></link> <description>Shared
Calendar</description> <c:forEach var="event" items="${allEvents}"
	varStatus="itemCount">
	<item> <title>[<fmt:formatDate
		value="${event.startDate.date}" type="date" pattern="M/d" />]&#160; <c:choose>
		<c:when test="${oevent:isCancelled(event)}">CANCELLED: </c:when>
		<c:when test="${event.declinedAttendee}">Declined: </c:when>
		<c:when test="${event.tentativeAttendee}">Tentative: </c:when>
		<c:when test="${event.needsActionAttendee}">Invited: </c:when>
	</c:choose> <c:out value="${event.summary.value}" /> </title> <description>
	<![CDATA[
      <c:out value="${event.description.value}"/>
      <c:if test="${!empty event.location}">
      [LOCATION: <c:out value="${event.location.value}"/>]
      </c:if>
      ]]></description> <pubDate>
	<fmt:formatDate value="${event.startDate.date}" type="time"
		pattern="EEE, dd MMM yyyy HH:mm:ss z" /></pubDate> <c:choose>
		<c:when test="${empty event.recurrenceId}">
			<guid>
			<c:out
				value="${viewhelper:getVirtualServerAddress(pageContext.request)}" />
			<c:url value="/u/${shareId}/${datePhrase}/${event.uid.value}" /></guid>
			<link>
			<c:out
				value="${viewhelper:getVirtualServerAddress(pageContext.request)}" />
			<c:url value="/u/${shareId}/${datePhrase}/${event.uid.value}" /></link>
		</c:when>
		<c:otherwise>
			<guid>
			<c:out
				value="${viewhelper:getVirtualServerAddress(pageContext.request)}" />
			<c:url
				value="/u/${shareId}/${datePhrase}/${event.uid.value}/${event.recurrenceId.value}" /></guid>
			<link>
			<c:out
				value="${viewhelper:getVirtualServerAddress(pageContext.request)}" />
			<c:url
				value="/u/${shareId}/${datePhrase}/${event.uid.value}/${event.recurrenceId.value}" /></link>
		</c:otherwise>
	</c:choose> </item>
</c:forEach> </channel> </rss>