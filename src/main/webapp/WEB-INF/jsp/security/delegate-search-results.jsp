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
<title>WiscCal ShareURL - Resource Search Results</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
</head>

<body>
	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<div id="content" class="main col">
		<div id="status">
			<p>
				Resource Search results for &nbsp;<i><c:out
						value="${searchText}" /></i>
			</p>

			<ul>
				<c:forEach items="${results}" var="delegate">
					<li>${delegate.displayName}&nbsp;(${delegate.emailAddress})
						<form action="<c:url value="/delegate_switch_user"/>"
							method="post">
							<fieldset>
								<input type="hidden" name="j_username"
									value="${delegate.username }" /> <input type="submit"
									value="Login" />
							</fieldset>
						</form>
					</li>
				</c:forEach>
			</ul>
			<a href="<c:url value="/delegate-search.html"/>">&laquo;Return to
				search form</a>
		</div>

	</div>
	<!--  content -->

	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>