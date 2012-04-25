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
<title>Share your WiscCal Calendar - Generate a new ShareURL - Step 3</title>
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
#include {
float:left;
margin:2px;
padding:3px;
border: 1px solid gray;
width:45%;
}
#exclude {
float:left;
margin:2px;
padding:3px;
border: 1px solid gray;
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
<h3>Generate a Share URL - Step 3</h3>
<p class="alert">
<strong>Security note</strong><br/>
The intent of this WiscCal feature is to allow you to share your WiscCal calendar data publicly.
All information that you store in WiscCal, or that other faculty, staff, or students submit to your calendar, 
will be viewable by any external user. The University of Wisconsin-Madison assumes no responsibility for how you 
share this information or how it is used.
</p>

<p class="info">
Choose whether you wish the output for this ShareURL to include the name and email address of all of the
event participants (organizer and attendees) or not.<br/>
<strong>Consider this choice carefully; if you plan to share this ShareURL publicly you should choose to "Exclude Event Participants."</strong>
</p>

<div id="formscontainer">

<div id="include">
<form:form>
<input type="submit" value="Include Event Participants" name="_eventId_include" />
</form:form>
</div>
<div id="exclude">
<form:form>
<input type="submit" value="Exclude Event Participants" name="_eventId_exclude" />
</form:form>
</div>

</div> <!-- end formscontainer -->

<div id="clear"></div>

<a href="${flowExecutionUrl}&_eventId=cancel">Cancel and Return to My Shares&raquo;</a>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>