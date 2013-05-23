<%-- 
  Copyright 2007-2011 The Board of Regents of the University of Wisconsin System.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<title>WiscCal ShareURL</title>
<style type="text/css">
#briefdescr {
	font-weight: bold;
}

#content abbr {
	border-bottom: 1px dotted;
}

.login {
	font-weight: bold;
}

#loginlinks {
	margin: 1em 0em 1em 0em;
	border: 1px solid gray;
	padding: 1em;
}
</style>
</head>

<body>

	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>

	<div id="content" class="main col">
		<div id="briefdescr">
			<p>
				ShareURL is an enhancement for WiscCal that allows you to create a
				unique <abbr title="Uniform Resource Locator; a web address">URL</abbr>
				that allows you and anyone else to view your WiscCal event data.
			</p>
		</div>

		<p>Once you create your ShareURL, it can be used to retrieve your
			WiscCal event data in a number of different formats:</p>
		<ul>
			<li><abbr
				title="Internet Standard for Calendar data exchange - RFC 5545">iCalendar</abbr>,
				used by many Desktop Calendar clients and Google Calendar</li>
			<li>HTML, view your Calendar in a web browser</li>
			<li><abbr title="Really Simple Syndication">RSS</abbr>,
				subscribe to your calendar in a News Reader like any other feed</li>
			<li><abbr title="JavaScript Object Notation">JSON</abbr>, the
				data format for the web and web developers</li>
		</ul>

		<p>You are in control of what events or details of events are
			displayed by your ShareURL. You can create ShareURLs that share just
			the times when you are busy, or some of the events in your agenda,
			like events that are marked "Public", or events that contain the word
			"soccer" in the title, or all of the events and their details.</p>

		<div id="loginlinks">
			<a class="login" href="<c:url value="/my-shares"/>">Log in to
				Manage ShareURLs for your personal or role based account&nbsp;&#187;</a><br />
			<a class="login" href="<c:url value="/delegate-login.html"/>">Log
				in to Manage ShareURLs for a WiscCal <abbr
				title="Conference Room, Projector, etc.">Resource</abbr>&nbsp;&#187;
			</a>
		</div>


		<p>
			Your ShareURL can provide a read-only link to all your WiscCal
			calendar data. <strong>If you have ANY sensitive data (e.g.,
				personal, financial, medical information, or <a
				href="https://kb.wisc.edu/helpdesk/page.php?id=24155">data
					marked private that you do not wish to share</a>), then you should use
				caution with this feature.
			</strong>
		</p>

		<%-- 
<div class="info" id="news" style="margin: 1em 0 1em 0; font-weight: bold;">
<p>This instance of ShareURL is "pre-production" until June 4 2012; please continue to use <a href="https://tools.wisccal.wisc.edu/cal/">the old WiscCal instance</a> until that date.</p>
</div>
--%>


	</div>
	<!-- content -->
	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>