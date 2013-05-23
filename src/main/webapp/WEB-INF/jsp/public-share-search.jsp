<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<title>WiscCal ShareURL - Search for Calendars</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>

<rs:resourceURL var="jqueryUiCssPath"
	value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.css" />
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}"
	media="all" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/jquery.autocomplete.css"/>" media="all" />
<style type="text/css">
#help {
	margin-bottom: 1em;
	font-size: 125%;
}

#shareKey {
	width: 65%;
	font-size: 125%;
}

.ac_results li {
	font-size: 110% !important;
}
</style>
<rs:resourceURL var="jQueryScript"
	value="/rs/jquery/1.4.2/jquery-1.4.2.min.js" />
<script type="text/javascript" src="${jQueryScript}"></script>
<rs:resourceURL var="jqueryUiPath"
	value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js" />
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.autocomplete.min.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#shareKey").autocomplete('<c:url value="/search"/>', {
		scroll: true
	});
	$(':input:visible:enabled:first').focus();
});
</script>
</head>

<body>

	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>

	<div id="content" class="main col">
		<c:if test="${showLogin }">
			<div class="alert">
				<p>
					Looking for WiscCal? <a href="https://wiscmail.wisc.edu/">Use
						this link to Log in to your WiscCal account!</a>
				</p>
			</div>
			<hr />
		</c:if>
		<div id="searchInner">
			<div id="help" class="info">
				<p>Looking for someone's WiscCal agenda? Start typing the email
					address of the person you are looking for, suggestions will appear
					as you type.</p>
			</div>

			<div id="searchForm">
				<form action="search" method="post">
					<fieldset>
						<input id="shareKey" name="shareKey" type="text" />&nbsp;<input
							type="submit" value="Go" />
					</fieldset>
				</form>
			</div>
		</div>
		<hr />
		<p>
			<a href="https://kb.wisc.edu/helpdesk/page.php?id=27557">Not
				finding the person you are looking for?</a>
		</p>
	</div>
	<!-- content -->

	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>