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
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<title>Ineligible for Calendar Service</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<div id="content" class="main col">
		<div class="alert">
			<strong>Your account is not eligible for this service.</strong>
			<p>
				If you are attempting to log in to ShareURL with your UW
				Madison NetID and see this page, please see the <a
					href="https://kb.wisc.edu/helpdesk/page.php?id=15039">Help Desk
					documentation about eligibility</a>.
			</p>
			<a href="<c:url value="/logout.html"/>">Log Out Completely&raquo;</a>
		</div>
	</div>
	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>