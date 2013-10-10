<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<div id="logininfo">
	<p id="loginline">
		You are logged in as&nbsp;<span class="userid"><security:authentication
				property="principal.activeDisplayName" /></span>.&nbsp;&nbsp;&nbsp; <a
			href="<c:url value="/my-shares"/>">My Shares</a>&nbsp;
		<security:authorize ifAllGranted="ROLE_DELEGATE_ACCOUNT">
			<a href="<c:url value="/delegate_switch_exit"/>">Log out as
				Resource&raquo;</a>
		</security:authorize>
		<security:authorize ifNotGranted="ROLE_DELEGATE_ACCOUNT">
			<a href="<c:url value="/logout.html"/>">Log out&raquo;</a>
		</security:authorize>
	</p>
</div>