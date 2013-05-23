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
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/extras.css"/>" />
<title>Calendar Data Unavailable</title>
</head>
<body>
	<p class="alert">
		Calendar data is temporarily unavailable. This error may appear if the
		WiscCal service is offline for repair or maintenance.<br /> Please <a
			href="https://kb.wisc.edu/helpdesk/page.php?id=1">contact the
			DoIT Help Desk</a> if you continue to see this error message.
		<!-- 

this file has to be larger than 512 bytes, thanks-> http://support.microsoft.com/kb/q218155/

-->
	</p>
</body>
</html>