<!DOCTYPE html>
<html>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<head>
<title>WiscCal ShareURL - Find a Resource</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
</head>

<body>
<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>
<div id="content" class="main col">
<div class="ownerform">
<form:form>
<fieldset>
<legend>Find a Resource</legend>
<p class="info">A list of resources will appear as you begin typing; you must start with the 3-6 letter group identifier for the resource.</p>
<div class="formerror"><form:errors path="*"/></div>
<label for="searchText">Resource Name:</label>&nbsp;
<form:input path="searchText"/>
<br/>
<br/>
<input type="submit" value="Search"/>
</fieldset>
</form:form>
</div>
<a href="<c:url value="/delegate-login.html"/>">&laquo;Return to Resource Log in form</a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>