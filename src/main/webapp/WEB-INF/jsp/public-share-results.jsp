<%@ include file="/WEB-INF/jsp/includes.jsp"%><%@ page
	contentType="text/plain; charset=UTF-8"%><c:forEach items="${shares}"
	var="share">
	<c:out value="${share}" />
	<% out.println(); %>
</c:forEach>