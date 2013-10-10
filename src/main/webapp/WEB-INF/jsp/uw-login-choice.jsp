<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html>
<head>
<title>WiscCal ShareURL - Log In</title>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<style type="text/css">
#logincontainer {
	margin: 1em;
}

.loginbox {
	text-align: center;
	height: 200px;
}

a.netidloginlink:hover {
	text-decoration: none;
}

.netidlogintext {
	text-decoration: underline;
}

#netidlogin {
	float: left;
	width: 40%;
	font-size: 125%;
	font-weight: bold;
	border: 2px solid #B70101;
	position: relative;
}

.loginimg {
	padding-left: 4em;
}

.centerlogin {
	position: absolute;
	top: 15%;
	padding-left: 5em;
}

#spacer {
	float: left;
	width: 19%;
	font-weight: bold;
	font-size: 150%;
	position: relative;
}

.centerspacer {
	position: absolute;
	top: 40%;
	padding-left: 3em;
}

#mailpluslogin {
	float: left;
	width: 40%;
	border: 1px gray solid;
	padding-top: 1em;
}

#loginForm {
	padding: 1em;
}

#loginForm legend {
	font-weight: bold;
	color: #B70101;
}

#loginclear {
	clear: both;
}
</style>
</head>

<body>

	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>

	<div id="content" class="main col">

		<div class="info">
			<p>Click "Log in with My NetID" to sign in via your UW-Madison
				NetID, or if logging in with a WiscMail plus account enter your
				email address and password.</p>
		</div>

		<div id="logincontainer">
			<div id="netidlogin" class="loginbox">

				<p class="centerlogin">
					<a class="netidloginlink" href="<c:url value="/security_check"/>"
						alt="Click here to Log in with your NetID"
						title="Click here to Log in with your NetID"> <img
						src="${crest }" alt="UW-Madison crest" width="50" height="75"
						class="loginimg" /><br />
					<span class="netidlogintext">Log in with My NetID</span>
					</a>
				</p>
			</div>

			<div id="spacer" class="loginbox">
				<p class="centerspacer">-OR-</p>
			</div>
			<div id="mailpluslogin" class="loginbox">
				<c:choose>
					<c:when test="${param.login_error == 1}">
						<div class="alert">
							<p>Authentication failed.</p>
						</div>
					</c:when>
					<c:otherwise>

					</c:otherwise>
				</c:choose>

				<c:url value="/mailplus_security_check" var="login" />
				<div id="loginForm">
					<form action="${login}" method="post">
						<fieldset>
							<legend>Log in to my WiscMail Plus Account</legend>
							<label for="j_username">WiscMail Plus email
								address:&nbsp;</label><input name="j_username" type="text" /><br /> <label
								for="j_username">WiscMail Plus password:&nbsp;</label><input
								name="j_password" type="password" /><br /> <input type="submit"
								name="submit" value="Log in" />
						</fieldset>
					</form>
				</div>
				<!-- end loginform -->
			</div>
			<!-- end mailpluslogin -->

			<div id="loginclear"></div>
		</div>
		<!-- end logincontainer -->
	</div>
	<!-- content -->

	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>