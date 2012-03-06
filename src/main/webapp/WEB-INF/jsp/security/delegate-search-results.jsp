<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title>WiscCal Oracle Calendar Export - Resource Search Results</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>
<div id="content" class="main col">
<div id="status">
<p>Resource Search results for &nbsp;<i><c:out value="${searchText}"/></i></p>

<ul>
<c:forEach items="${results}" var="delegate">
<li><c:out value="${delegate.name}"/>
<form action="<c:url value="/delegate_switch_user"/>" method="post">
<fieldset>
<input type="hidden" name="j_username" value="${delegate.username }"/>
<input type="submit" value="Login" />
</fieldset>
</form></li>
</c:forEach>
</ul>
<a href="<c:url value="/delegate-search.html"/>">&laquo;Return to search form</a>
</div>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>