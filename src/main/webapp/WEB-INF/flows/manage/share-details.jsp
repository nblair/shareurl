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
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>WiscCal ShareURL - Manage ${share.key }</title>
<rs:resourceURL var="revokeIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<style type="text/css">
.key { color:green;}
.large { font-size:120%;}
.margin3 { margin: 3px; }
.padding1 { padding: 1em; }
.padding2 { padding: 2em; }
.bordered { border: 1px solid #990000; }
.clear { clear:both; }
.scBox { float: left; position: relative; width: 26%; margin: 0px 3px 0px 3px; border: 1px solid #666152; padding: 1.25em;}
.removable { border: 1px solid #C7CEF9; background-color: #E2E6FF;}
.resetHandle {position:relative; top:3px;}
.sharelink { border: 1px dotted #C7CEF9; background-color: #E2E6FF; padding: 0.5em 0.5em 0.5em 1em;}
.sharelinktext { color: blue; font-size: 130%;}
#sharelinktag { color: blue;}
#examples { line-height:200%;}
</style>
<c:url value="/u/${share.key}" var="baseShareUrl"/>
<c:url value="/rest/shareDetails" var="shareDetails">
<c:param name="shareKey" value="${share.key}"/>
</c:url>

<c:url value="/rest/includeP" var="includeP"/>
<c:url value="/rest/excludeP" var="excludeP"/>
<c:url value="/rest/toac" var="toac"/>
<c:url value="/rest/tofb" var="tofb"/>
<c:url value="/rest/addPrivacyFilter" var="addPrivacyFilter"/>
<c:url value="/rest/addContentFilter" var="addContentFilter"/>
<c:url value="/rest/resetFilters" var="resetFilters"/>

<c:set var="revokeMessage" value="This ShareURL will be permanently deleted. Are you sure?"/>
<c:if test="${share.guessable}">
<c:set var="revokeMessage" value="Your Public ShareURL will revert to the default (Free/Busy Only). Are you sure?"/>
</c:if>
<rs:resourceURL var="jqueryUiCssPath" value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.min.css"/>
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}" media="all"/>
<rs:resourceURL var="jqueryUiPath" value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.serializeObject.js"/>"></script>
<script type="text/javascript">
$(function() {
	$('.revokeform').submit(function(e) {
		e.preventDefault();
		var confirmed = confirm('${revokeMessage}');
		if(confirmed) {
			$('.revokeform').unbind();
			$('.revokeform').submit();
		}
	});
	$("#datex").datepicker({ dateFormat: 'yy-mm-dd' });
	$("#datey").datepicker({ dateFormat: 'yy-mm-dd' });
	$('#clientselect').change(function(e) { renderShareUrlExample(); });
	$('#x').change(function(e) { renderShareUrlExample(); });
	$('#negatex').change(function(e) { renderShareUrlExample(); });
	$('#y').change(function(e) { renderShareUrlExample(); });
	$('#datex').change(function(e) { renderShareUrlExample(); });
	$('#datey').change(function(e) { renderShareUrlExample(); });
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
		applySubmitHandlerIfPresent('#toac', '${toac}');
	} else {
		$('#scIncludeParticipants').empty();
		if(share.includeParticipants) {
			$('<form action="${excludeP }" method="post" id="excludeP"><fieldset><input type="submit" value="Exclude Participants"/></fieldset></form>').appendTo('#scIncludeParticipants');
			applySubmitHandlerIfPresent('#excludeP', '${excludeP}');
		} else {
			$('<form action="${includeP }" method="post" id="includeP"><fieldset><input type="submit" value="Include Participants"/></fieldset></form>').appendTo('#scIncludeParticipants');
			applySubmitHandlerIfPresent('#includeP', '${includeP}');
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
		applySubmitHandlerIfPresent('#tofb', '${tofb}');
		
		var options = getAvailablePrivacyFilters(share);
		$('<form action="" method="post" id="privacyFilter"><fieldset><label for="privacyValue">Include:&nbsp;</label><select id="privacyOptions" name="privacyValue"></select><input type="submit" value="Save Filter"/></fieldset></form>').appendTo('#scFilters');
		
		$.each(options, function(key, value) {
			var o = $('<option></option>');
			o.val(key);
			o.html(value);
			o.appendTo('#privacyOptions');
		});
		
		applySubmitHandlerIfPresent('#privacyFilter', '${addPrivacyFilter}');
		$('<hr/>').appendTo('#scFilters');
		$('<form action="" method="post" id="contentFilter"><fieldset><label for="propertyName">Include:&nbsp;</label><select name="propertyName"><option value="SUMMARY">Title</option><option value="LOCATION">Location</option><option value="DESCRIPTION">Description</option></select><label for="propertyValue">&nbsp;contains&nbsp;</label><input type="text" name="propertyValue"/><input type="submit" value="Save Filter"/></fieldset></form>').appendTo('#scFilters');
		applySubmitHandlerIfPresent('#contentFilter', '${addContentFilter}');
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
		$.post('${resetFilters}',
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
	renderShareUrlExample();
};
function renderShareUrlExample() {
	var c = $("#clientselect option:selected").val();
	if('native' == c || 'google' == c) {
		$('#queryParameters').text('').fadeOut();
		$('#icsSuffix').text('').fadeOut();
		$('#queryParameters').text('?ical').fadeIn();
	} else if ('browser' == c) {
		$('#queryParameters').text('').fadeOut();
		$('#icsSuffix').text('').fadeOut();
	} else if ('news' == c) {
		$('#queryParameters').text('').fadeOut();
		$('#icsSuffix').text('').fadeOut();
		$('#queryParameters').text('?rss').fadeIn();
	} else if ('json' == c) {
		$('#queryParameters').text('').fadeOut();
		$('#icsSuffix').text('').fadeOut();
		$('#queryParameters').text('?json').fadeIn();
	} else if ('ics' == c) {
		$('#queryParameters').text('').fadeOut();
		$('#icsSuffix').text('.ics').fadeIn();
	} else if ('iphone' == c) {
		$('#queryParameters').text('').fadeOut();
		$('#icsSuffix').text('').fadeOut();
		$('#queryParameters').text('?mobileconfig').fadeIn();
	}
	var datex = $('#datex').val();
	var datey = $('#datey').val();
	if(datex != '' && datey != '') {
		$('#x').val('');
		$('#y').val('');
		$('#dateRange').text('').fadeOut();
		var qp = $('#queryParameters').text();
		if(qp == ''){
			qp = '?start=' + datex + '&end=' + datey;
		} else {
			qp += '&start=' + datex + '&end=' + datey;
		}
		$('#queryParameters').text('').fadeOut();
		$('#queryParameters').text(qp).fadeIn();
	} else {
		var x = $('#x').val();
		var y = $('#y').val();
		if(x != '' && y != '') {
			var negate = $("#negatex option:selected").val();
			if(negate == 'negate') {
				x = -x;
			}
			if(y - x > 180) {
				y = x + 180;
				$('#y').val(y);
				alert("Your date range is greater than 180 days, which is the maximum allowed. It's been reset to " + y);
			}
			$('#dateRange').text('').fadeOut();
			if(x != 0 || y != 0) {
				$('#dateRange').text('/dr(' + x + ',' + y + ')').fadeIn();
			}
		}
	}
	$('#sharelinktag').attr('href', '${baseShareUrl}' + $('#dateRange').text() + $('#icsSuffix').text() + $('#queryParameters').text());
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
<div id="shareControls" class="bordered margin3 padding1">
<div id="scCalendarData" class="scBox"></div>
<div id="scFilters" class="scBox"></div>
<div id="scIncludeParticipants" class="scBox"></div>
<div class="clear"></div>
</div> <!--  end id=shareControls -->

<div id="revoke" class="bordered margin3 padding1">
<form:form action="${flowExecutionUrl}&_eventId=revoke" cssClass="revokeform">
<input type="submit" class="revokebutton" value="Revoke this ShareURL"/>
</form:form>
</div>

<div id="examples" class="margin3 padding1">
<p>This ShareURL can be viewed with the following link:</p>
<div class="sharelink">
<a id="sharelinktag" href="${baseShareUrl }/dr(-14,30)?ical"><span class="sharelinktext">${viewhelper:getVirtualServerAddress(pageContext.request)}<span>${baseShareUrl}</span><span id="dateRange">/dr(-14,30)</span><span id="icsSuffix"></span><span id="queryParameters">?ical</span></span></a>
</div>

<p><label for="client">I want to view my ShareURL in </label>
<select name="client" id="clientselect">
<option value="native" selected="selected">Microsoft Outlook, Mozilla Thunderbird, or Apple iCal</option>
<option value="iphone">an iPhone or iPad</option>
<option value="browser">a Web Browser, like Firefox, Chrome, or Internet Explorer</option>
<option value="google">Google Calendar</option>
<option value="ics">an ICS (iCalendar) file</option>
<option value="news">a News (RSS) Reader</option>
<option value="json">Javascript Object Notation (JSON)</option>
</select>
</p>

<p><label for="x">I would like to see data from </label><input id="x" type="number" name="x" value="14" min="-999" max="999"/> days <select id="negatex"><option value="negate" selected="selected">back</option><option value="">forward</option></select> <label for="y">through </label><input id="y" type="number" name="y" value="30"  min="-999" max="999"/> days forward.</p>
<p>OR, <label for="datex">I would like to see data from specifically </label><input id="datex" type="text" name="datex"/><label for="datey"> through </label><input id="datey" type="text" name="datey"/></p>

<p>I would like to see <a title="View ShareURL Options (Opens new window)" target="_new_help" href="http://kb.wisc.edu/wisccal/page.php?id=13322">all available options for ShareURLs&raquo;</a>, including options for web developers.</p>

</div>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>