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
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>WiscCal ShareURL - Generate a new ShareURL - Review</title>
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


<ul>
<c:if test="${sharePreferences.includeParticipants}">
<li><strong>Include Event Participants.</strong></li>
</c:if>
<c:if test="${sharePreferences.freeBusyOnly}">
<li>Free Busy only.</li>
</c:if>
<c:choose>
<c:when test="${sharePreferences.eventFilterCount == 0}">
<li>No event filters - all calendar data returned.</li>
</c:when>
<c:otherwise>
<li>${sharePreferences.filterDisplay}</li>
</c:otherwise>
</c:choose>
</ul>

<form:form >
<input type="submit" name="_eventId_generate" value="Generate" onclick=""/>
<input type="submit" name="_eventId_cancel" value="Cancel" />
</form:form>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>