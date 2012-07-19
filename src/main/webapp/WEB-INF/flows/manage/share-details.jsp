<%-- 
  Copyright 2007-2010 The Board of Regents of the University of Wisconsin System.

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
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>Share your WiscCal Calendar - Manage ShareURL</title>


<rs:resourceURL var="feedIcon" value="/rs/famfamfam/silk/1.3/feed.png"/>
<rs:resourceURL var="htmlIcon" value="/rs/famfamfam/silk/1.3/html.png"/>
<rs:resourceURL var="icalIcon" value="/rs/famfamfam/silk/1.3/calendar.png"/>
<rs:resourceURL var="fbIcon" value="/rs/famfamfam/silk/1.3/clock.png"/>
<rs:resourceURL var="helpIcon" value="/rs/famfamfam/silk/1.3/arrow_right.png"/>
<rs:resourceURL var="revokeIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<style type="text/css">
.examples li {
padding-bottom: 1em;
}
.examples li.rss {
list-style-image: url(${feedIcon});
}
.examples li.html {
list-style-image: url(${htmlIcon});
}
.examples li.ical {
list-style-image: url(${icalIcon});
}
.examples li.fb {
list-style-image: url(${fbIcon});
}
.examples li.help {
list-style-image: url(${helpIcon});
}
.margin3scHelp { margin: 3px; }
.bordered { border: 1px solid black; }
.clear { clear:both; }
.scBox { float: left; position: relative; width: 26%; margin: 3px; border: 1px solid gray; padding: 2em;}
</style>
<c:url value="/shareDetails" var="shareDetails">
<c:param name="shareKey" value="${share.key}"/>
</c:url>
<c:url value="/includeP" var="includeP">
</c:url>
<c:url value="/excludeP" var="excludeP">
</c:url>
<c:url value="/toac" var="toac">
</c:url>
<c:url value="/tofb" var="tofb">
</c:url>
<script type="text/javascript">
$(function() {
	$('.revokeform').submit(function(e) {
		e.preventDefault();
		var confirmed = confirm('This ShareURL will be permanently deleted. Are you sure?');
		if(confirmed) {
			$('.revokeform').unbind();
			$('.revokeform').submit();
		}
	});
	
	applySubmitHandlerIfPresent('#toac', '<c:url value="toac"/>');
	applySubmitHandlerIfPresent('#tofb', '<c:url value="tofb"/>');
	applySubmitHandlerIfPresent('#includeP', '<c:url value="includeP"/>');
	applySubmitHandlerIfPresent('#excludeP', '<c:url value="excludeP"/>');
});

function applySubmitHandlerIfPresent(element, url) {
	var el = $(element);
	if(el) {
		el.submit(function(event) {
			event.preventDefault();
			postAndRenderPreferences(url);
		});
	}
};
function renderShareControls(share) {
	$('#shareControls').empty();
	
};
function renderSharePreferences(share, fadeIn) {
	$('#shareDetails').empty();
	var ul = $('<ul/>');
	if(share.includeParticipants) {
		$('<li>Include Event Participants.</li>').appendTo(ul);
	}
	if(share.freeBusyOnly) {
		$('<li>Free Busy only.</li>').appendTo(ul);
	}
	if(share.eventFilterCount == 0) {
		$('<li>All Calendar Data</li>').appendTo(ul);
	} else {
		$('<li>' + share.sharePreferences.filterDisplay + '</li>').appendTo(ul);
	}
	if(fadeIn) {
		ul.appendTo('#shareDetails').fadeIn();
	} else {
		ul.appendTo('#shareDetails');
	}
};
function refreshDetails(fadeIn) {
	$.get('${shareDetails}',
			{ },
			function(data) {
				if(data.share) {
					renderSharePreferences(data.share, fadeIn);
				}
			},
			"json");
};

function postAndRenderPreferences(url) {
	$.post(url,
			{ shareKey: '${share.key}'},
			function(data) {
				if(data.share) {
					renderSharePreferences(data.share, true);
				}
			},
			"json");
};
</script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>

<div id="content" class="main col">

<h2>ShareURL Details for <i>${share.key }</i></h2>
<div id="shareDetails">
<ul>
<c:if test="${share.includeParticipants}">
<li><strong>Include Event Participants.</strong></li>
</c:if>
<c:choose>
<c:when test="${share.freeBusyOnly}">
<li>Free Busy only.</li>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${share.eventFilterCount == 0}">
<li>All calendar data.</li>
</c:when>
<c:otherwise>
<li>${share.sharePreferences.filterDisplay}</li>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>
</ul>
</div>
<div id="revoke" class="bordered margin3">
<form:form action="${flowExecutionUrl}&_eventId=revoke" cssClass="revokeform">
<input type="submit" class="revokebutton" value="Revoke this ShareURL"/>
</form:form>
</div>

<div id="shareControls" class="bordered">
<%-- 

shareControls div should be completely rendered via javascript

<div id="scHelp" class="info margin3"><p>Use the controls below to change the settings for this ShareURL:</p></div>
<c:choose>
<c:when test="${share.freeBusyOnly}">
<div class="scBox">
<form action="${toac }" method="post" id="toac">
<fieldset>
<input type="submit" value="Convert to All Calendar"/>
</fieldset>
</form>
</div>
</c:when>
<c:otherwise>

<div id="scFreeBusy" class="scBox">
<form action="${tofb }" method="post" id="tofb">
<fieldset>
<input type="submit" value="Convert to Free Busy only"/>
</fieldset>
</form>

</div>

<div id="scFilters" class="scBox">
<form action="" method="post" id="privacyFilter">
<fieldset>
<label for="privacy">Include:&nbsp;</label>
<select name="privacy">
<option value="PUBLIC">Public</option>
<option value="CONFIDENTIAL">Show Date and Time Only</option>
<option value="PRIVATE">Private</option>
</select>
</fieldset>
</form>

<hr/>

<form action="" method="post" id="otherFilter">
<fieldset>
<label for="field">Include:&nbsp;</label>
<select name="field">
<option value="SUMMARY">Title</option>
<option value="LOCATION">Location</option>
<option value="DESCRIPTION">Description</option>
</select>
<label for="fieldValue">&nbsp;contains&nbsp;</label><input type="text" name="fieldValue"/>

</fieldset>
</form>
</div>
<div id="scIncludeParticipants" class="scBox">

<c:choose>
<c:when test="${share.includeParticipants }">
<form action="${excludeP }" method="post" id="excludeP">
<fieldset>
<input type="submit" value="Exclude Participants"/>
</fieldset>
</form>
</c:when>
<c:otherwise>
<form action="${includeP }" method="post" id="includeP">
<fieldset>
<input type="submit" value="Include Participants"/>
</fieldset>
</form>
</c:otherwise>
</c:choose>
</div>

</c:otherwise>
</c:choose>
<div class="clear"></div>
--%>
</div> <!--  end id=shareControls -->

<h2>Example Share URLs</h2>
<ul class="examples">
<li class="html">Your calendar for "today" in HTML:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}"/></a></li>
<c:if test="${share.freeBusyOnly == false}">
<li class="rss">Your calendar for "today" in <acronym title="Really Simple Syndication">RSS</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?rss"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?rss"/></a></li>
</c:if>
<li class="ical">Your calendar for "today" in <acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?ical"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}?ical"/></a></li>
<li class="ical">Your calendar for "tomorrow" in <acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(1,1)?ical"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(1,1)?ical"/></a></li>
<li class="ical">Your calendar for "14 days ago through 14 days ahead" in <acronym title="Internet Standard for Calendar data exchange - RFC2445">iCalendar</acronym>:<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(-14,14)?ical"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/u/${share.key}/dr(-14,14)?ical"/></a></li>
<c:if test="${share.freeBusyOnly }">
<li class="fb">Free/Busy Read URL (<a href="http://kb.wisc.edu/wisccal/page.php?id=13313">Learn how to use within Microsoft Outlook</a>):<br/><a href="<c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/freebusy/${share.key}"/>"><c:out value="${viewhelper:getVirtualServerAddress(pageContext.request)}"/><c:url value="/freebusy/${share.key}"/></a></li>
</c:if>
<li class="help">To see all other options, read the "Using a ShareURL" documentation in our <a href="http://kb.wisc.edu/wisccal/page.php?id=13322">Help Desk instructions&nbsp;&#187;</a></li>
</ul>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>