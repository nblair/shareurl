<%-- 
  Copyright 2007-2012 The Board of Regents of the University of Wisconsin System.

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
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<title>WiscCal ShareURL - Manage ${share.key }</title>
<rs:resourceURL var="revokeIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<rs:resourceURL var="tickIcon" value="/rs/famfamfam/silk/1.3/tick.png"/>
<rs:resourceURL var="helpIcon" value="/rs/famfamfam/silk/1.3/help.png"/>
<rs:resourceURL var="alertIcon" value="/rs/famfamfam/silk/1.3/exclamation.png"/>
<style type="text/css">
.key { color:green;}
.large { font-size:120%;}
.margin3 { margin: 3px; }
.padding1 { padding: 1em; }
.padding2 { padding: 2em; }
.bordered { border: 1px solid #990000; }
.clear { clear:both; }
.fleft { float: left;}
.fright {float:right;}
.scBox { float: left; position: relative; width: 26%; margin: 0px 3px 0px 3px; border: 1px solid #666152; padding: 1.25em;}
.removable { border: 1px solid #C7CEF9; background-color: #E2E6FF;}
.revokeHandle {position:relative; top:3px;}
.sharelink { border: 1px dotted #C7CEF9; background-color: #E2E6FF; padding: 0.5em 0.5em 0.5em 1em;}
.sharelinktext { color: blue; font-size: 130%;}
#sharelinktag { color: blue;}
#examples { line-height:200%;}
.notselected {opacity:0.75;}
.inlineblock {display:inline-block;}
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
<c:url value="/rest/removeContentFilter" var="removeContentFilter"/>
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
<script type="text/javascript" src="<c:url value="/js/edit-share.js"/>"></script>
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
	setupFormHandlers();
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
	var lastShare = initShare;
	renderShareControls(initShare);
	$('#setLabel').submit(function(event) {
        event.preventDefault();
        $('#lsubmit').attr('disabled', 'disabled');
        postSetLabel(this);     
    });
	$('#justToday').click(function(e) {
		$('#x').val(0);
		$('#y').val(0);
		renderShareUrlExample();
	});
});
function setupFormHandlers() {
	applySubmitHandlerIfPresent('#tofb', '${tofb}', '#labelindicatorfb');
	applySubmitHandlerIfPresent('#toac', '${toac}', '#labelindicatorac');
	applySubmitHandlerIfPresent('#includeP1', '${includeP}', '#labelindicatorip');
	$('#fbRadio').click(function() {
	    $('#tofb').submit();
	});
	$('#acRadio').click(function() {
	    $('#toac').submit();
	});
	$('#ip').click(function() {
	    $('#includeP1').submit();
	});
	
	applySubmitHandlerIfPresent('#contentFilter', '${addContentFilter}', '#labelindicatorcf', function() {
		setupRemoveContentFilterForms();
	});
	applySubmitHandlerIfPresent('#privacyFilter', '${addPrivacyFilter}', '#labelindicatorcl');
	$('#publicClass').click(function() {
	    $('#privacyFilter').submit();
	});
	$('#confidClass').click(function() {
	    $('#privacyFilter').submit();
	});
	$('#privateClass').click(function() {
	    $('#privacyFilter').submit();
	});
	setupRemoveContentFilterForms();
}
function setupRemoveContentFilterForms() {
	$('.removeContentFilter').each(function(i) {
		applySubmitHandlerIfPresent($(this), '${removeContentFilter}', '#labelindicatorcf', function() {
			setupRemoveContentFilterForms();
		});
	});
	
	$('.revokeHandle').unbind('click');
	$('.revokeHandle').click(function(event) {
		var anchor = $(this);
		anchor.parent('form').submit();
	})
}
function postAndRenderPreferences(url, form, indicator, callback) {
	$('.ind').empty();
	$('<img src="<c:url value="/img/indicator.gif"/>"/>').appendTo(indicator);
	var formdata = $(form).serializeObject();
	formdata["shareKey"] = '${share.key}';
	$.post(url,
			formdata,
			function(responsedata) {
				if(responsedata.share) {
					lastShare = responsedata.share;
					$(indicator).empty();
                    $('<img src="${tickIcon}"/>').appendTo(indicator);
					renderShareControls(responsedata.share);
					renderFilterPreferences(responsedata.share, '${revokeIcon}', '${removeContentFilter}', responsedata.removeContentFilter);
					if(callback && typeof(callback) == 'function') {
						callback(responsedata);
					}
				} else {
					$(indicator).empty();
					$('<img src="${alertIcon}"/>').appendTo(indicator);
					if(responsedata.error) {
						alert(responsedata.error);
					}
					renderShareControls(lastShare);
					renderFilterPreferences(lastShare, '${revokeIcon}', '${removeContentFilter}', false);
				}
			},
			"json");
};
function postSetLabel(form) {
	$('.ind').empty();
	$('<img src="<c:url value="/img/indicator.gif"/>"/>').appendTo('#labelindicator');
    var data = $(form).serializeObject();
    data["shareKey"] = '${share.key}';
    $.post('<c:url value="/rest/set-label"/>',
            data,
            function(data) {
                if(data.share) {
                    $('#labelinput').val(data.share.label);
                    $('#labelindicator').empty();
                    $('<img src="${tickIcon}"/>').appendTo('#labelindicator');
                    $('#lsubmit').attr('disabled', '');
                } else {
                	alert('Invalid label value; make sure your label is less than 64 characters.');
                	$('#labelindicator').empty();
                	$('#lsubmit').attr('disabled', '');
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

<div id="shareDetails" class="margin3">
<p><strong>You are editing the privacy settings for:</strong></p>
<p class="key large padding1">${viewhelper:getVirtualServerAddress(pageContext.request)}${baseShareUrl }</p>
<p>The settings you select below will determine what calendar data will be accessible through your ShareURL.
To allow different access levels for different audiences, you can create multiple ShareURLs with different options.</p>
</div>

<div id="shareControls2">

<c:if test="${not share.guessable }">
<div class="margin3 padding1">
<form id="setLabel">
<fieldset><label for="label">Label (optional):</label>&nbsp;<input id="labelinput" name="label" type="text" value="${share.label}"><input id="lsubmit" type="submit" value="Change">&nbsp;<span id="labelindicator" class="ind"></span></fieldset>
<p>Adding a label can help you remember how you intend to use this ShareURL or with whom you have shared it's address. Your label will be visible to you only.</p>
</form>
</div>
</c:if>

<div id="scFreeBusy" class="bordered padding1 margin3">
<form action="${tofb }" method="post" id="tofb">
<fieldset>
<input id="fbRadio" type="radio" name="freebusyonly" value="true"/><label for="freebusyonly"><strong>Free/Busy only</strong></label>&nbsp;<span id="labelindicatorfb" class="ind"></span>
</fieldset>
</form>
<div id="scFreeBusyInner">
<p>Free/Busy only ShareURLs display only the start and end times for periods that you are busy, all other meeting details are withheld.</p>
</div>
</div> <!-- end scFreeBusy -->

<div id="scAllCalendar" class="bordered padding1 margin3">

<form action="${toac }" method="post" id="toac">
<fieldset>
<input id="acRadio" type="radio" name="allcalendar" value="true"/><label for="allcalendar"><strong>Include more event detail</strong></label>&nbsp;<span id="labelindicatorac" class="ind"></span>
<p>ShareURLs using this setting display meeting title, location, and details along with start and end times. Meeting participants are withheld by default.
</fieldset>
</form>

<div id="scAllCalendarInner">

<form action="${includeP}" method="post" id="includeP1">
<fieldset>
<input id="ip" type="checkbox" name="includeParticipants" <c:if test="${share.includeParticipants }">checked="checked"</c:if>/>&nbsp;<label for="includeParticipants">Include attendees and organizer on group appointments</label>&nbsp;<span id="labelindicatorip" class="ind"></span>&nbsp;<a href="#includeParticipantsHelp" title="Include Participants Option Help">What's this?</a> 
</fieldset>
</form>

<div id="filters" class="padding2">
<form action="${addPrivacyFilter}" method="post" id="privacyFilter">
<p>Limit results to include only events with the following <a href="https://kb.wisc.edu/helpdesk/page.php?id=24155">visibility</a>&nbsp;<span id="labelindicatorcl" class="ind"></span>:</p>
<fieldset>
<input id="publicClass" type="checkbox" name="includePublic" class="classFilterCheckbox"/>&nbsp;<label for="public">Public</label><br/>
<input id="confidClass" type="checkbox" name="includeConfidential" class="classFilterCheckbox"/>&nbsp;<label for="confidential">Show Date and Time Only</label><br/>
<input id="privateClass" type="checkbox" name="includePrivate" class="classFilterCheckbox"/>&nbsp;<label for="private">Private</label><br/>
</fieldset>
</form>
<form action="${addContentFilter}" method="post" id="contentFilter">
<fieldset>
<span>Include only events with </span>
<select id="propertyName" name="propertyName"><option value="SUMMARY">Title</option><option value="LOCATION">Location</option><option value="DESCRIPTION">Description</option></select>
<span>&nbsp;containing&nbsp;</span><input type="text" name="propertyValue"/>&nbsp;<input id="addFilter" type="submit" value="Add"/>&nbsp;<span id="labelindicatorcf" class="ind"></span><br/>
</fieldset>
</form>
<ul id="contentFilters">
<c:forEach items="${share.sharePreferences.propertyMatchPreferences }" var="pref" varStatus="status">
<li><span class="removable">${pref.displayName }</span>&nbsp;<form class="removeContentFilter inlineblock" action="${removeContentFilter}" method="post" id="removeContentFilter${status.index}"><fieldset><input type="hidden" name="propertyName" value="${pref.key}"/><input type="hidden" name="propertyValue" value="${pref.value}"/></fieldset><img class="revokeHandle" src="${revokeIcon }" title="Remove this filter"/></form></li>
</c:forEach>
</ul>
</div>

</div> <!-- end scAllCalendarInner -->
</div> <!-- end scAllCalendar -->

<div id="revoke" class="margin3 padding1">
<form:form action="${flowExecutionUrl}&_eventId=revoke" cssClass="revokeform">
<input type="submit" class="revokebutton" value="Delete this ShareURL"/>
</form:form>
</div>

</div> <!-- end shareControls2 -->


<!--  </div> --> <!--  end privacySettings -->

<hr/>

<div id="examples" class="margin3">
<h2>Using your ShareURL</h2>
<p>This ShareURL can be viewed with the following link. Use the form controls below to update the example for your use case:</p>
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
<p>OR, I would like to see data from <a id="justToday" href="#justToday">just "today"</a>.</p>
<p>OR, <label for="datex">I would like to see data from specifically </label><input id="datex" type="text" name="datex"/><label for="datey"> through </label><input id="datey" type="text" name="datey"/></p>

<p>I would like to see <a title="View ShareURL Options (Opens new window)" target="_new_help" href="http://kb.wisc.edu/wisccal/page.php?id=13322">all available options for ShareURLs&raquo;</a>, including options for web developers.</p>

</div>

</div> <!-- content -->
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>