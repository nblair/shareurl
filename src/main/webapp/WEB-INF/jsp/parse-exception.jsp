<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/extras.css"/>" />
<title>WiscCal Information Unavailable</title>
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