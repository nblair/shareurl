<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<title>WiscCal ShareURL - Feature not accessible by Resources</title>
</head>

<body>

	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>

	<div id="content" class="main col">
		<div class="alert">
			<p>
				You are currently logged in as a Resource (
				<security:authentication property="principal.activeDisplayName" />
				), and you must return to your original account to access the
				requested feature.
			</p>
			<a href="<c:url value="/delegate_switch_exit"/>">Log Out as
				WiscCal Resource and return to your personal account&nbsp;&#187;</a><br />
		</div>

	</div>
	<!-- content -->
	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>