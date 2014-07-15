<%--

    Copyright 2012, Board of Regents of the University of
    Wisconsin System. See the NOTICE file distributed with
    this work for additional information regarding copyright
    ownership. Board of Regents of the University of Wisconsin
    System licenses this file to you under the Apache License,
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
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>ShareURL - Log in as a Resource account</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>

<rs:resourceURL var="jqueryUiCssPath"
	value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.css" />
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}"
	media="all" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/jquery.autocomplete.css"/>" media="all" />
<style type="text/css">
#lookupform {
	margin-top: 4px;
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
<script type="text/javascript"
	src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
	$(document).ready(
			function() {
				$("#delegateName").autocomplete(
						'<c:url value="/delegate-search.html"/>', {
							width : 320,
							scroll : true
						});
				$(':submit').lockSubmit();
			});
	function submitLogin(email){
		$("#delegateName").val(email.trim());
		$("#loginForm").submit();
	}
	
	function submitLinkedLogin(e){
		var linkedEmail = $(e).text().trim();
		$("#delegateName").val(linkedEmail);
		$("#loginForm").submit();
	}
</script>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<%-- <%@ include file="/WEB-INF/jsp/login-info.jsp" %> --%>
	<div id="content" class="main col">
<!-- 		<h3>Log in as a Resource account</h3> -->
<h3>Select an account:</h3>
		<div class="info">
<!-- 			<p>Use this form to log in as an WiscCal Resource Account that -->
<!-- 				you administer. A list of resources will appear as you begin typing; -->
<!-- 				you must start with the 3-6 letter group identifier for the -->
<!-- 				resource.</p> -->
<!-- 			<br /> -->
<!-- 			<p> -->
<!-- 				If no resources are displayed for your input, you may not be -->
<!-- 				designated as the primary contact - please read the <a -->
<!-- 					href="http://kb.wisc.edu/wisccal/page.php?id=5538">Help Desk -->
<!-- 					documentation</a>. -->
<!-- 			</p> -->
			<p>
				If no accounts are displayed please read the <a
					href="http://kb.wisc.edu/wisccal/page.php?id=5538">Help Desk
					documentation</a>.
			</p>
		</div>
		<div id="listAccounts">
			<h4>Primary Account</h4>
			<ul>
				<li>
					<span class="linkedAccount">
						<a class='loginLink' onclick='submitLogin("${ownerEmail}");'>
							<c:out value="${ownerPrimaryEmail}" />
						</a>
					</span>
				</li>
			</ul>
					
			<c:if test="${not empty ownerLinkedEmails }">
				<h4>Linked Accounts</h4>
				<ul>
				<c:forEach items="${ownerLinkedEmails}" var="email">
					<li>
						<span class="linkedAccount"> 
							<a class='loginLink' onclick='submitLinkedLogin(this);'><c:out value="${email}" /></a>
						</span>
					</li>	
				</c:forEach>
				</ul>
			</c:if>
			
			<c:if test="${not empty ownerDelegagteEmails }">
				<h4>Resource Accounts</h4>
				<ul>
				<c:forEach items="${ownerDelegagteEmails}" var="email">
					<li>
						<span class="linkedAccount"> 
							<a class='loginLink' onclick='submitLinkedLogin(this);'><c:out value="${email}" /></a>
						</span>
					</li>					
				</c:forEach>
				</ul>
			</c:if>
		</div>


		<div id="lookupform">
			<form id="loginForm" action="<c:url value="/delegate_switch_user"/>"
				method="post">
				<input type="hidden" id="delegateName" name="j_username" />
			</form>
		</div>
		
	</div>
	<!--  content -->

	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>