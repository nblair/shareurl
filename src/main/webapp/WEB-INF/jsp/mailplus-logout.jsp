<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title>WiscCal ShareURL - Logged Out (WiscMail Plus)</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<style type="text/css">
#status p { margin-bottom: 1em; }
</style>
</head>

<body>
<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<div id="status" class="success">
<p>You have been logged out successfully.</p>
<a href="<c:url value="/"/>">Return to ShareURL Home&raquo;</a>
</div>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>