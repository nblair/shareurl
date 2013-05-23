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