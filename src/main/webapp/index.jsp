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
<title>Share your WiscCal Calendar</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>

<div id="content" class="main col">
<p>This feature allows you to generate unique Share URLs that allow anyone who visits to access your WiscCal agenda, daily notes, and day events. 
Use these URLs to post a public Web page for anyone (friends, family, etc.) to view with a browser or to display your WiscCal data in:</p>
<ul>
<li>Google Calendar - <a href="http://kb.wisc.edu/wisccal/page.php?id=5353">Help Desk setup instructions</a></li>
<li>Mozilla Sunbird - <a href="http://kb.wisc.edu/wisccal/page.php?id=5337">Help Desk setup instructions</a></li>
<li>Apple iCal - <a href="http://kb.wisc.edu/wisccal/page.php?id=5348">Help Desk setup instructions</a></li> 
<li>Windows Vista Calendar - <a href="http://kb.wisc.edu/wisccal/page.php?id=5346">Help Desk setup instructions</a></li>
<li>Outlook 2007 - <a href="http://kb.wisc.edu/wisccal/page.php?id=5488">Help Desk setup instructions</a></li>
</ul>
<p>Many more uses are possible. For example, it's easy to embed the HTML view of a ShareURL in an <acronym title="Inline Frame">iframe</acronym> element in a personal web site. 
You can also use the <acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym> data option and format the data on your own. 
More information about ShareURLs and how they can be used can be found in our <a href="http://kb.wisc.edu/wisccal/page.php?id=5336">Help Desk documentation&raquo;</a>.
</p>

<p>Your Share URL provides a read-only link to all your WiscCal calendar data. <strong>If you have ANY sensitive data (e.g., personal, financial, 
medical information, or <a href="http://kb.wisc.edu/helpdesk/page.php?id=3249">data set to a sensitive access level</a>), then you should not use this feature.</strong> 
If you post your Share URL on another public Web site, anyone who stumbles across it will be able to use it to read all appointments, daily notes, and day events in 
your WiscCal account regardless of the access level you set when you created the event. If someone else has invited you to a confidential 
meeting, that meeting may appear in your agenda and be viewable using this Share URL.</p>

<ul>
<li><strong><a href="<c:url value="/my-shares?logoutDelegate=true"/>">Manage ShareURLs for your personal or role based account&nbsp;&#187;</a></strong></li>
<li><strong><a href="<c:url value="/delegate-login.html"/>">Manage ShareURLs for a WiscCal <acronym title="Conference Room, Projector, etc.">Resource</acronym>&nbsp;&#187;</a></strong></li>
</ul>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>