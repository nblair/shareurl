<%@ include file="/WEB-INF/jsp/includes.jsp"%><%@ page
	contentType="text/plain; charset=UTF-8"%><c:forEach items="${results}"
	var="delegate">
${delegate.displayName}<% out.println(); %>
</c:forEach>