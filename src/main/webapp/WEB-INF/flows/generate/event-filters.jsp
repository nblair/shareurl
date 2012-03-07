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
<title>Share your WiscCal Calendar - Generate a new ShareURL - Step 2</title>
<rs:resourceURL var="jqueryPath" value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>
<script type="text/javascript" src="${jqueryPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
<rs:resourceURL var="tickIcon" value="/rs/famfamfam/silk/1.3/tick.png"/>
<style type="text/css">
.shortcut {
background:transparent url(${tickIcon}) no-repeat scroll right center;
padding-right:15px;
}
#formscontainer {
overflow:hidden;
}
#selectedFilters {
float:left;
margin:2px;
padding:3px;
width:45%;
}
#addFilters {
float:left;
margin:2px;
padding:3px;
border-left: 1px solid gray;
width:45%;
}
#clear {
clear:both;
}
</style>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<h3>Generate a Share URL - Step 2</h3>
<p class="alert">
<strong>Security note</strong><br/>
The intent of this WiscCal feature is to allow you to share your WiscCal calendar data publicly.
All information that you store in WiscCal, or that other faculty, staff, or students submit to your calendar, 
will be viewable by any external user. The University of Wisconsin-Madison assumes no responsibility for how you 
share this information or how it is used.
</p>

<p class="info">
Use the form on the right to add filters that will return only the events that match your rules.
</p>

<div id="formscontainer">

<div id="selectedFilters">
<c:choose>
<c:when test="${empty sharePreferences.preferences}">
<span><i>No event filters selected, return all events in my Calendar.</i></span>
<form:form commandName="formBackingObject">
<input type="submit" name="_eventId_optionsComplete" value="Create Share without any Filters" />
</form:form>
</c:when>
<c:otherwise>
<h4>Share URL will include only events that meet the following criteria:</h4>
<ul>
<c:forEach items="${sharePreferences.preferences}" var="pref" varStatus="prefStatus">
<li><c:out value="${pref.displayName}"/>&nbsp;<c:if test="${not prefStatus.last}"><strong>OR</strong></c:if></li>
</c:forEach>
</ul>
<form:form commandName="formBackingObject">
<%-- <input type="submit" name="_eventId_clearFilters" value="Clear current Filters" />&nbsp; --%>
<input type="submit" name="_eventId_optionsComplete" value="Finished adding Filters" />
</form:form>
</c:otherwise>
</c:choose>
</div> <!-- end selectedFilters -->

<div id="addFilters">
<form:form commandName="formBackingObject">
<legend>Add an inclusion filter</legend>
<br/>
<label for="propertyName">Event Property:</label>
<form:select path="propertyName">
<form:option value="SUMMARY">Event Title</form:option>
<form:option value="LOCATION">Event Location</form:option>
<form:option value="DESCRIPTION">Event Description</form:option>
</form:select>
<br/>
<label for="propertyValue">Contains:</label>
<form:input path="propertyValue"/>
<br/>
<input type="submit" name="_eventId_addPreference" value="Add Preference" />&nbsp;&nbsp;
</form:form>

<h4>Additional inclusion filters for Event Type:</h4>
<ul>
<li><a class="shortcut" href="${flowExecutionUrl}&_eventId=addPublic">Include "Public" Events</a></li>
<li><a class="shortcut" href="${flowExecutionUrl}&_eventId=addConfidential">Include "Show Date and Time" Events</a></li>
<li><a class="shortcut" href="${flowExecutionUrl}&_eventId=addPrivate">Include "Private" Events</a></li>
</ul>
</div> <!-- end addFilters -->

</div> <!-- end formscontainer -->

<div id="clear"></div>

<a href="${flowExecutionUrl}&_eventId=cancel">Cancel and Return to My Shares&raquo;</a>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>