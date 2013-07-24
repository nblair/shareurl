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
<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<title>WiscCal ShareURL - Logged Out (WiscMail Plus)</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<style type="text/css">
#status p {
	margin-bottom: 1em;
}
</style>
</head>

<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>

	<div id="content" class="main col">
		<div id="status" class="success">
			<p>You have been logged out successfully.</p>
			<a href="<c:url value="/"/>">Return to ShareURL Home&raquo;</a>
		</div>
	</div>
	<!--  content -->

	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>