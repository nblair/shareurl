<%@ include file="/WEB-INF/jsp/includes.jsp"%><%@ page
	contentType="text/plain; charset=UTF-8"%><c:forEach
	items="${delegates}" var="delegate">
	<c:out value="${delegate.name}" />
	<% out.println(); %><%--|<c:out value="${user.username}"/> --%>
</c:forEach>