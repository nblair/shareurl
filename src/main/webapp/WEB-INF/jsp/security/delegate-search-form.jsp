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
<!DOCTYPE html>
<html>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<head>
<title>ShareURL - Find a Resource</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<script type="text/javascript"
	src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
</head>

<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<div id="content" class="main col">
		<div class="ownerform">
			<form:form>
				<fieldset>
					<legend>Find a Resource</legend>
					<p class="info">A list of resources will appear as you begin
						typing; you must start with the 3-6 letter group identifier for
						the resource.</p>
					<div class="formerror">
						<form:errors path="*" />
					</div>
					<label for="searchText">Resource Name:</label>&nbsp;
					<form:input path="searchText" />
					<br /> <br /> <input type="submit" value="Search" />
				</fieldset>
			</form:form>
		</div>
		<a href="<c:url value="/delegate-login.html"/>">&laquo;Return to
			Resource Log in form</a>
	</div>
	<!--  content -->

	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>