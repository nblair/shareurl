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
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>Share your WiscCal Calendar - Manage ShareURL</title>


<rs:resourceURL var="feedIcon" value="/rs/famfamfam/silk/1.3/feed.png"/>
<rs:resourceURL var="htmlIcon" value="/rs/famfamfam/silk/1.3/html.png"/>
<rs:resourceURL var="icalIcon" value="/rs/famfamfam/silk/1.3/calendar.png"/>
<rs:resourceURL var="fbIcon" value="/rs/famfamfam/silk/1.3/clock.png"/>
<rs:resourceURL var="helpIcon" value="/rs/famfamfam/silk/1.3/arrow_right.png"/>
<rs:resourceURL var="revokeIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<style type="text/css">
.examples li {
padding-bottom: 1em;
}
.examples li.rss {
list-style-image: url(${feedIcon});
}
.examples li.html {
list-style-image: url(${htmlIcon});
}
.examples li.ical {
list-style-image: url(${icalIcon});
}
.examples li.fb {
list-style-image: url(${fbIcon});
}
.examples li.help {
list-style-image: url(${helpIcon});
}
</style>

<rs:resourceURL var="jqueryPath" value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>

<script type="text/javascript" src="${jqueryPath}"></script>
<script type="text/javascript">
$(function() {
	$('.revokeform').submit(function(e) {
		e.preventDefault();
		var confirmed = confirm('This ShareURL will be permanently deleted. Are you sure?');
		if(confirmed) {
			$('.revokeform').unbind();
			$('.revokeform').submit();
		}
	});
});
</script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<p class="info">
Logged in as:&nbsp;<security:authentication property="principal.activeDisplayName"/><br/>
</p>
<h2>ShareURL Details for <i>${share.key }</i></h2>

<ul>
<c:if test="${share.includeParticipants}">
<li><strong>Include Event Participants.</strong></li>
</c:if>
<c:if test="${share.freeBusyOnly}">
<li>Free Busy only.</li>
</c:if>
<c:choose>
<c:when test="${share.eventFilterCount == 0}">
<li>No event filters - all calendar data returned.</li>
</c:when>
<c:otherwise>
<li>${sharePreferences.filterDisplay}</li>
</c:otherwise>
</c:choose>
</ul>

<h2>Example Share URLs</h2>
<ul class="examples">
<li class="html">Your calendar for "today" in HTML:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}"/></a></li>
<c:if test="${share.freeBusyOnly == false}">
<li class="rss">Your calendar for "today" in <acronym title="Really Simple Syndication">RSS</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?rss"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?rss"/></a></li>
</c:if>
<li class="ical">Your calendar for "today" in <acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?ical"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?ical"/></a></li>
<li class="ical">Your calendar for "tomorrow" in <acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(1,1)?ical"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(1,1)?ical"/></a></li>
<li class="ical">Your calendar for "14 days ago through 14 days ahead" in <acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(-14,14)?ical"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(-14,14)?ical"/></a></li>
<c:if test="${share.freeBusyOnly }">
<li class="fb">Free/Busy Read URL (<a href="http://kb.wisc.edu/wisccal/page.php?id=13313">Learn how to use within Microsoft Outlook</a>):<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/freebusy/${share.key}"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/freebusy/${share.key}"/></a></li>
</c:if>
<li class="help">To see all other options, read the "Using a ShareURL" documentation in our <a href="http://kb.wisc.edu/wisccal/page.php?id=13322">Help Desk instructions&nbsp;&#187;</a></li>
</ul>

<p>
<form:form action="${flowExecutionUrl}&_eventId=revoke" cssClass="revokeform">
<input type="submit" class="revokebutton" value="Revoke this ShareURL"/>
</form:form>
</p>

<p>
<a href="<c:url value="/my-shares"/>">&laquo;Return to My Shares</a>
</p>
</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>