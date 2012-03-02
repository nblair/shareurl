<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>WiscCal Oracle Calendar Export - Resource Lookup</title>

<rs:resourceURL var="jqueryUiCssPath" value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.css"/>
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}" media="all"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery.autocomplete.css"/>" media="all"/>

<style type="text/css">
#lookupform { margin-top: 4px; }
</style>

<rs:resourceURL var="jqueryPath" value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>
<rs:resourceURL var="jqueryUiPath" value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>
<script type="text/javascript" src="${jqueryPath}"></script>
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.autocomplete.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#delegateName").autocomplete('<c:url value="/delegate-search.html"/>', {
		width: 320,
		scroll: true
	});
	$(':submit').lockSubmit();
});
</script>
</head>
<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<h3>Resource Lookup</h3>

<div class="info">
<p>Use this form to log in to the WiscCal Oracle Calendar Export application as an Oracle Calendar Resource that you administer.</p>
<p>A list of resources will appear as you begin typing; you must start with the 3 or 4 letter group identifier for the resource.</p>
<p>If no resources are displayed for any inputs, you may not be designated as the primary contact - please see 
the <a href="http://kb.wisc.edu/wisccal/page.php?id=5538">Help Desk documentation</a>.</p>
</div>

<div id="lookupform">
<form:form>
<label for="delegateName">Resource name:</label>&nbsp;
<form:input path="delegateName"/><br/>
<input type="submit" value="Login" name="_eventId_attemptLogin"/>
</form:form>
</div>
</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>