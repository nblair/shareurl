<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<title>Missing Input Parameter</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<div id="content" class="main col">
		<p class="alert">
			The link you requested requires certain parameters that are missing.
			<br /> It's ok, just <a href="<c:url value="/my-shares"/>">Return
				to My Shares</a> and try again.
		</p>
	</div>
	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>
