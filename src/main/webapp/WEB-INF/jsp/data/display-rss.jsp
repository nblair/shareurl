<%@ page contentType="text/xml;charset=UTF-8" %><?xml version="1.0"?>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<rss version="2.0">
  <channel>
    <title>Shared Calendar</title>
    <link><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${shareId}/${datePhrase}"/></link>
    <description>Shared Calendar</description>
	<c:forEach var="event" items="${allEvents}" varStatus="itemCount">
    <item>
      <title>[<fmt:formatDate value="${event.startDate.date}" type="date" pattern="M/d"/>]&#160;<c:if test="${oevent:isCancelled(event)}">CANCELLED: </c:if><c:out value="${event.summary.value}"/></title> 
      <description>
      <![CDATA[
      <c:out value="${event.description.value}"/>
      <c:if test="${!empty event.location}">
      [LOCATION: <c:out value="${event.location.value}"/>]
      </c:if>
      ]]></description>
      <pubDate><fmt:formatDate value="${event.startDate.date}" type="time" pattern="EEE, dd MMM yyyy HH:mm:ss z"/></pubDate>
      <guid><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${shareId}/${datePhrase}/${event.uid.value}"/></guid>   
      <link><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${shareId}/${datePhrase}/${event.uid.value}"/></link>
    </item>
    </c:forEach>
  </channel>
</rss>