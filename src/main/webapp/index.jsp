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
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>WiscCal ShareURL</title>
<style type="text/css">
#briefdescr{ font-weight:bold;}
#content acronym { border-bottom:1px dotted;}
.login {font-weight: bold;}
#loginlinks {margin: 1em 0em 1em 0em; border:1px solid gray;padding:1em;}
</style>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<div id="briefdescr">
<p>ShareURL is an enhancement for WiscCal that allows you to create a unique <acronym title="Uniform Resource Locator; an Internet address">URL</acronym> that allows you and anyone else to view your WiscCal event data.</p>
</div>

<p>
Once you create your ShareURL, it can be used to retrieve your WiscCal event data in a number of different formats:</p>
<ul>
<li><acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym>, used by many Desktop Calendar clients and Google Calendar</li>
<li>HTML, view your Calendar in a web browser</li>
<li><acronym title="Really Simple Syndication">RSS</acronym>, subscribe to your calendar in a News Reader like any other feed</li>
<li><acronym title="JavaScript Object Notation">JSON</acronym>, the data format for the web and web developers</li>
</ul>

<p>You are in control of what events or details of events are displayed by your ShareURL. You can create ShareURLs that share just the times when you are busy, 
or some of the events in your agenda, like events that are marked "Public", or events that contain the word "soccer" in the title, or all of the events and their details.</p>

<div id="loginlinks">
<a class="login" href="<c:url value="/my-shares"/>">Log in to Manage ShareURLs for your personal or role based account&nbsp;&#187;</a><br/>
<a class="login" href="<c:url value="/delegate-login.html"/>">Log in Manage ShareURLs for a WiscCal <acronym title="Conference Room, Projector, etc.">Resource</acronym>&nbsp;&#187;</a>
</div>


<p>Your Share URL can provide a read-only link to all your WiscCal calendar data. <strong>If you have ANY sensitive data (e.g., personal, financial, 
medical information, or <a href="https://kb.wisc.edu/helpdesk/page.php?id=24155">data marked private that you do not wish to share</a>), then you should use caution with this feature.</strong> 
If you post your Share URL on another public Web site, anyone who stumbles across it will be able to use it to read all appointments, daily notes, and day events in 
your WiscCal account regardless of the access level you set when you created the event. If someone else has invited you to a confidential 
meeting, that meeting may appear in your agenda and be viewable using this Share URL.</p>

<%-- 
<div class="info" id="news" style="margin: 1em 0 1em 0; font-weight: bold;">
<p>This instance of ShareURL is "pre-production" until June 4 2012; please continue to use <a href="https://tools.wisccal.wisc.edu/cal/">the old WiscCal instance</a> until that date.</p>
</div>
--%>


</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>