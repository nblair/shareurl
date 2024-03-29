<%--

    Copyright 2012, Board of Regents of the University of
    Wisconsin System. See the NOTICE file distributed with
    this work for additional information regarding copyright
    ownership. Board of Regents of the University of Wisconsin
    System licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/extras.css"/>" />
<title>Information Unavailable</title>
</head>
<body>
	<p class="alert">
		<i>If you receive this error message, there is an event in your
			calendar which could not be displayed.</i><br /> Please see the
		explanation at this <a
			href="http://kb.wisc.edu/helpdesk/page.php?id=5059" target="new">Help
			Desk Document</a>.
		<!--
<c:out value="${exception}"/>
-->
	</p>
</body>
</html>