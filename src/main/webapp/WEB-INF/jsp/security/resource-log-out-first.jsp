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
<title>Share your WiscCal Calendar - Feature not accessible by Resources</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<div class="alert">
<p>You are currently logged in as a Resource (<security:authentication property="principal.activeDisplayName"/>), and you must return to your original account to access the requested feature.</p>
<a href="<c:url value="/delegate_switch_exit"/>">Log Out as WiscCal Resource and return to your personal account&nbsp;&#187;</a><br/>
</div>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>