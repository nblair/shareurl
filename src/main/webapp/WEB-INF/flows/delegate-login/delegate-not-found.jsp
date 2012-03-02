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
<title>WiscCal Resource Not Found</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>
<div id="content" class="main col">
<p class="alert">
The Resource either does not exist, or you do not have permission to administer it.
<br/><br/>
Please see the <a href="http://kb.wisc.edu/helpdesk/page.php?id=5538">Help Desk documentation regarding ShareURLs for WiscCal Resources</a>.
<br/><br/>
<a href="<c:url value="/delegate-login"/>">Log in as another WiscCal Resource&raquo;</a>, or <a href="<c:url value="/"/>">Back to ShareURL Home&raquo;</a>
</p>
</div>
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>
