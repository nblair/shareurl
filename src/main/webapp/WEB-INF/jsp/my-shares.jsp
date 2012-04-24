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
<title>Share your WiscCal Calendar - My ShareURLs</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<p class="alert">
<strong>Security note</strong><br/>
The intent of this WiscCal feature is to allow you to share your WiscCal calendar data publicly.
All information that you store in WiscCal, or that other faculty, staff, or students submit to your calendar, 
will be viewable by any external user. The University of Wisconsin-Madison assumes no responsibility for how you 
share this information or how it is used.
</p>
<h4>How to Share Your Personal WiscCal Calendar</h4>
<p>Click on the "Generate a New ShareURL" link below to create a new ShareURL. Each Share represents your authorization to share the data 
stored in your personal WiscCal account.
You can find out more about how this works at <a href="http://kb.wisc.edu/wisccal/page.php?id=5336">our Help Desk instructions</a>. 
<strong>It is recommended that you periodically:</strong></p>
<ul>
<li>Evaluate with whom you have shared your Share URL</li>
<li>Review the information stored and shared in your calendar</li>
</ul>
<p>Revoke your ShareURL(s) if your calendar contains sensitive personal information about yourself or others, or if your Share
URL(s) reach an audience larger than your comfort level.</p>
<h3>Share Status - Your WiscCal Account</h3>
<p class="info">
Logged in as:&nbsp;<security:authentication property="principal.activeDisplayName"/><br/><br/>
<c:choose>
<c:when test="${activeIsDelegate}">
<%-- 
<a href="<c:url value="/my-shares?logoutDelegate=true"/>">Log Out as WiscCal Resource and return to your personal account&nbsp;&#187;</a><br/>
--%>
<a href="<c:url value="/delegate_switch_exit"/>">Log Out as WiscCal Resource and return to your personal account&nbsp;&#187;</a><br/>
<a href="<c:url value="/delegate-login.html?diff"/>">Log in as a different WiscCal Resource&nbsp;&#187;</a>
</c:when>
<c:otherwise>
<a href="<c:url value="/delegate-login.html"/>">Log in as a WiscCal Resource&nbsp;&#187;</a>
</c:otherwise>
</c:choose>
</p>
<c:choose>
<c:when test="${empty shares}">
<p><i>No active ShareURLs associated with this account.</i></p>
</c:when>
<c:otherwise>
<ul>
<c:forEach items="${shares}" var="share">
<c:choose>
<c:when test="${share.freeBusyOnly == true}">
<li><a title="Manage <c:out value="${share.key}"/>" href="<c:url value="manage?id=${share.key}"/>">Manage <c:out value="${share.key }"/> (FreeBusy only)</a></li>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${share.eventFilterCount == 0}">
<li><a title="Manage <c:out value="${share.key}"/>" href="<c:url value="manage?id=${share.key}"/>">Manage <c:out value="${share.key }"/> (All Calendar Data)</a></li>
</c:when>
<c:otherwise>
<li><a title="Manage <c:out value="${share.key}"/>" href="<c:url value="manage?id=${share.key}"/>">Manage <c:out value="${share.key }"/> 
(<c:forEach items="${share.sharePreferences.preferences}" var="pref" varStatus="status">
<c:out value="${pref.displayName}"/><c:if test="${not status.last}">,&nbsp;</c:if>
</c:forEach>)
</a></li>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>

</c:forEach>
</ul>
</c:otherwise>
</c:choose>

<a href="<c:url value="/generate"/>">Generate a new ShareURL&raquo;</a>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>