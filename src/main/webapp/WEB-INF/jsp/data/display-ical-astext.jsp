<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ page contentType="text/plain; charset=UTF-8"%>
<c:out escapeXml="false" value="${fn:trim(ical)}" />