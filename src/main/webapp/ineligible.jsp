<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<title>Ineligible for WiscCal Service</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<div id="content" class="main col">
		<div class="alert">
			<strong>Your account is not eligible for this service.</strong>
			<p>
				If you are attempting to log in to WiscCal ShareURL with your UW
				Madison NetID and see this page, please see the <a
					href="https://kb.wisc.edu/helpdesk/page.php?id=15039">Help Desk
					documentation about WiscCal Eligibility</a>.
			</p>
			<a href="<c:url value="/logout.html"/>">Log Out Completely&raquo;</a>
		</div>
	</div>
	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>