<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<title>Page Not Found (404)</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<div id="content" class="main col">
		<p class="alert">
			Page not found. <br /> <br /> <a href="<c:url value="/"/>">ShareURL
				Home&raquo;</a>
		</p>
	</div>
	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>
