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
.key {
color:green;
}
.large {
font-size:120%;
}
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
.margin3 { margin: 3px; }
.padding1 { padding: 1em; }
.padding2 { padding: 2em; }
.bordered { border: 1px solid black; }
.clear { clear:both; }
.scBox { float: left; position: relative; width: 26%; margin: 3px; border: 1px solid gray; padding: 2em;}
.removable { border: 1px solid #C7CEF9; background-color: #E2E6FF;}
.resetHandle {position:relative; top:3px;}
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
<script type="text/javascript" src="<c:url value="/js/jquery.serializeObject.js"/>"></script>
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
	
	var initShare = { "freeBusyOnly": ${share.freeBusyOnly}, 
			"includeParticipants": ${share.includeParticipants}, 
			"sharePreferences": {
				"classificationFilters": ${viewhelper:classificationFiltersToJSON(share.sharePreferences.classificationFilters)},
				"contentFilters": ${viewhelper:contentFiltersToJSON(share.sharePreferences.contentFilters)}
			} };
	
	setupResetFiltersHandler();
	renderShareControls(initShare, false);
});

function getAvailablePrivacyFilters(share) {
	var options = { "PUBLIC": "Public", "CONFIDENTIAL": "Show Date and Time Only", "PRIVATE": "Private" };
	if($.isEmptyObject(share.sharePreferences.classificationFilters)) {
		return options;
	}
	$.each(share.sharePreferences.classificationFilters, function() {
		delete options[this];
	});
	return options;
};
function applySubmitHandlerIfPresent(element, url) {
	var el = $(element);
	if(el) {
		el.submit(function(event) {
			event.preventDefault();
			postAndRenderPreferences(url, element);
		});
	}
};
function renderShareControls(share, fade) {
	$('#scCalendarData').empty();
	if(share.freeBusyOnly) {
		if(fade) {
			$('#scFilters').fadeOut();
			$('#scIncludeParticipants').fadeOut();
		} else {
			$('#scFilters').hide();
			$('#scIncludeParticipants').hide();
		}
		$('<form action="${toac }" method="post" id="toac"><fieldset><input type="submit" value="Convert to All Calendar"/></fieldset></form>').appendTo('#scCalendarData');
		applySubmitHandlerIfPresent('#toac', '<c:url value="toac"/>');
	} else {
		$('#scIncludeParticipants').empty();
		if(share.includeParticipants) {
			$('<form action="${excludeP }" method="post" id="excludeP"><fieldset><input type="submit" value="Exclude Participants"/></fieldset></form>').appendTo('#scIncludeParticipants');
			applySubmitHandlerIfPresent('#excludeP', '<c:url value="excludeP"/>');
		} else {
			$('<form action="${includeP }" method="post" id="includeP"><fieldset><input type="submit" value="Include Participants"/></fieldset></form>').appendTo('#scIncludeParticipants');
			applySubmitHandlerIfPresent('#includeP', '<c:url value="includeP"/>');
		}
		if(fade) {
			$('#scFilters').fadeIn();
			$('#scIncludeParticipants').fadeIn();
		} else {
			$('#scFilters').show();
			$('#scIncludeParticipants').show();
		}
		
		$('#scFilters').empty();
		$('<form action="${tofb }" method="post" id="tofb"><fieldset><input type="submit" value="Convert to Free Busy"/></fieldset></form>').appendTo('#scCalendarData');
		applySubmitHandlerIfPresent('#tofb', '<c:url value="tofb"/>');
		
		var options = getAvailablePrivacyFilters(share);
		$('<form action="" method="post" id="privacyFilter"><fieldset><label for="privacyValue">Include:&nbsp;</label><select id="privacyOptions" name="privacyValue"></select><input type="submit" value="Save Filter"/></fieldset></form>').appendTo('#scFilters');
		
		$.each(options, function(key, value) {
			var o = $('<option></option>');
			o.val(key);
			o.html(value);
			o.appendTo('#privacyOptions');
		});
		
		applySubmitHandlerIfPresent('#privacyFilter', '<c:url value="addPrivacyFilter"/>');
		$('<hr/>').appendTo('#scFilters');
		$('<form action="" method="post" id="contentFilter"><fieldset><label for="propertyName">Include:&nbsp;</label><select name="propertyName"><option value="SUMMARY">Title</option><option value="LOCATION">Location</option><option value="DESCRIPTION">Description</option></select><label for="propertyValue">&nbsp;contains&nbsp;</label><input type="text" name="propertyValue"/><input type="submit" value="Save Filter"/></fieldset></form>').appendTo('#scFilters');
		applySubmitHandlerIfPresent('#contentFilter', '<c:url value="addContentFilter"/>');
	}
	
};
function setupResetFiltersHandler() {
	$('.resetHandle').unbind('click', resetFilters);
	setTimeout(function() {
		$('.resetHandle').bind('click', resetFilters);
	}, 1000);
};
function resetFilters(event) {
	var confirmed = confirm('Reset all Content Filters for this ShareURL?');
	if(confirmed) {
		$.post('<c:url value="resetFilters"/>',
				{ "shareKey": "${share.key}"} ,
				function(data) {
					if(data.share) {
						renderSharePreferences(data.share, true);
						renderShareControls(data.share, true);
					}
				},
				"json");
	}
};
function renderSharePreferences(share, fadeIn) {
	$('#shareDetails').empty();
	var ul = $('<ul/>');
	if(share.includeParticipants) {
		$('<li><strong>Include Event Participants.</strong></li>').appendTo(ul);
	}
	if(share.freeBusyOnly) {
		$('<li>Free Busy only.</li>').appendTo(ul);
	} else {
		if(share.eventFilterCount == 0) {
			$('<li>All Calendar Data</li>').appendTo(ul);
		} else {
			$('<li><span class="removable">' + share.sharePreferences.filterDisplay + ' <img src="${revokeIcon}" title="Reset content filters" alt="Reset content filters" class="resetHandle"/></span></li>').appendTo(ul);
			setupResetFiltersHandler();
		}
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
function postAndRenderPreferences(url, form) {
	var data = $(form).serializeObject();
	data["shareKey"] = '${share.key}';
	$.post(url,
			data,
			function(data) {
				if(data.share) {
					renderSharePreferences(data.share, true);
					renderShareControls(data.share, true);
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

<h2>Options for ShareURL <span class="key large">${share.key }</span></h2>
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
<li><span class="removable">${share.sharePreferences.filterDisplay}&nbsp;<img src="${revokeIcon}" title="Remove this attribute" alt="Remove this attribute" class="resetHandle"/></span></li>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>
</ul>
</div>

<h2>Change Options for this ShareURL</h2>
<div id="shareControls" class="bordered padding2">
<div id="scCalendarData" class="scBox"></div>
<div id="scFilters" class="scBox"></div>
<div id="scIncludeParticipants" class="scBox"></div>
<div class="clear"></div>
</div> <!--  end id=shareControls -->

<div id="revoke" class="bordered margin3 padding2">
<form:form action="${flowExecutionUrl}&_eventId=revoke" cssClass="revokeform">
<input type="submit" class="revokebutton" value="Revoke this ShareURL"/>
</form:form>
</div>

<h2>Example Uses</h2>
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