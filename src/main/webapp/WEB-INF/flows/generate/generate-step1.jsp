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
<title>Share your WiscCal Calendar - Generate a new ShareURL Step 1</title>
<rs:resourceURL var="jqueryPath" value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>
<script type="text/javascript" src="${jqueryPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
<style type="text/css">
#formscontainer {
overflow:hidden;
margin-top: 4px;
text-align: center !important;
}
#fulldata {
border: 1px solid gray;
float:left;
padding:5px;
width:30%;
margin-right:3px;
}
#filtered {
float:left;
padding:5px;
border: 1px solid gray;
width:30%;
margin-right:3px;
}
#freebusy {
float:left;
padding:5px;
border: 1px solid gray;
width:30%;
}
#clear {
clear:both;
}
</style>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<h3>Generate a Share URL - Step 1</h3>

<div class="info">
<p>A Full Calendar data ShareURL will contain all events (including title, location, details, etc) in your WiscCal agenda.</p>
<p>Selecting Filtered Calendar data allows you to create rules defining which events will be included in the data returned by the ShareURL.</p>
<p>A FreeBusy only ShareURL won't contain any details about your meetings (title, location, details, etc) - it will only show
what times you are "busy". FreeBusy only ShareURLs also provide an additional link that implements the Calendaring Standard
Specification named "FreeBusy Read URL."</p>
</div>

<div id="formscontainer">

<div id="fulldata">
<form:form>
<input type="submit" value="Full Calendar data" name="_eventId_full" />
</form:form>
</div>
<div id="filtered">
<form:form>
<input type="submit" value="Filtered Calendar data" name="_eventId_filtered" />
</form:form>
</div>
<div id="freebusy">
<form:form>
<input type="submit" value="FreeBusy only" name="_eventId_freeBusyOnly" />
</form:form>
</div>
</div>
<div id="clear"></div>
<a href="${flowExecutionUrl}&_eventId=cancel">Cancel and Return to My Shares&raquo;</a>
</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>