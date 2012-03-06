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
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>Share your WiscCal Calendar - Generate a new ShareURL - Review</title>
<rs:resourceURL var="jqueryPath" value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>
<script type="text/javascript" src="${jqueryPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<h3>Generate a Share URL - Review</h3>
<p class="alert">
<strong>Security note</strong><br/>
The intent of this WiscCal feature is to allow you to share your WiscCal calendar data publicly.
All information that you store in WiscCal, or that other faculty, staff, or students submit to your calendar, 
will be viewable by any external user. The University of Wisconsin-Madison assumes no responsibility for how you 
share this information or how it is used.
</p>

<p class="info">
Review the preferences you have chosen for this ShareURL and click Generate to create it.
</p>

<c:choose>
<c:when test="${sharePreferences.freeBusyOnly}">
<p><i>This ShareURL will contain only Free/Busy information.</i></p>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${empty sharePreferences.preferences}">
<p><i>No event filters selected, return all events in my Calendar.</i></p>
</c:when>
<c:otherwise>
<h4>Share URL will include only events that meet the following criteria:</h4>
<ul>
<c:forEach items="${sharePreferences.preferences}" var="pref" varStatus="status">
<li><c:out value="${pref.displayName}"/>&nbsp;<c:if test="${not status.last}"><strong>OR</strong></c:if></li>
</c:forEach>
</ul>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>

<form:form >
<input type="submit" name="_eventId_generate" value="Generate" onclick=""/>
<input type="submit" name="_eventId_cancel" value="Cancel" />
</form:form>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>