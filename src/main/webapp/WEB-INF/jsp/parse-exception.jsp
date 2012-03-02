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
<link rel="stylesheet" type="text/css" href="<c:url value="/css/extras.css"/>"/>
<title>WiscCal Information Unavailable</title>
</head>
<body>
<p class="alert">
<i>If you receive this error message, there is an event in your calendar which could not be displayed.</i><br/>
Please see the explanation at this <a href="http://kb.wisc.edu/wisccal/page.php?id=5059" target="new">Help Desk Document</a>.
<!--
<c:out value="${exception}"/>
-->
</p>  
</body>
</html>