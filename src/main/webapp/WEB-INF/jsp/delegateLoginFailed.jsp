<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<title>WiscCal ShareURL - Resource Login Failed</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<style type="text/css"></style>
</head>

<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<%@ include file="/WEB-INF/jsp/login-info.jsp"%>
	<div id="content" class="main col">

		<div class="alert">
			<p>
				<strong>Your attempt to log in as a Resource failed for one
					or more of the following reasons:</strong>
			</p>
			<ul>
				<li>The Resource does not exist.</li>
				<li>You do not have permission to administer this Resource.</li>
				<li>The Resource account is not eligible for ShareURL.</li>
			</ul>
			<p>
				Please see the <a
					href="http://kb.wisc.edu/helpdesk/page.php?id=5538">Help Desk
					documentation regarding ShareURL for WiscCal Resources</a>.
			</p>
			<a href="<c:url value="/delegate-login.html"/>">Log in again as a
				Resource</a>, <a href="<c:url value="/"/>">Return to ShareURL Home</a>,
			or <a href="<c:url value="/logout.html"/>">Log out</a>
		</div>
	</div>
	<!--  content -->

	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>