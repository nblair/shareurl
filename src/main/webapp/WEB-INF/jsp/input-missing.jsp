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
<title>Missing Input Parameter</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>
<div id="content" class="main col">
<p class="alert">
The link you requested requires certain parameters that are missing.
<br/>
It's ok, just <a href="<c:url value="/my-shares"/>">&laquo;Return to My Shares</a> and try again.
</p>
</div>
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>
