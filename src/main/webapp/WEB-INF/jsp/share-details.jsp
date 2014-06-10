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
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp"%>
<title>WiscCal ShareURL - Manage ${share.key }</title>
<rs:resourceURL var="revokeIcon"
	value="/rs/famfamfam/silk/1.3/cross.png" />
<rs:resourceURL var="tickIcon" value="/rs/famfamfam/silk/1.3/tick.png" />
<rs:resourceURL var="helpIcon" value="/rs/famfamfam/silk/1.3/help.png" />
<rs:resourceURL var="alertIcon"
	value="/rs/famfamfam/silk/1.3/exclamation.png" />
<style type="text/css">
.key {
	color: green;
}

.large {
	font-size: 120%;
}

.margin3 {
	margin: 3px;
}

.padTop {
	padding-top: 5px;
}

.padding1 {
	padding: 1em;
}

.padding2 {
	padding: 2em;
}

.bordered {
	border: 1px solid #990000;
}

.clear {
	clear: both;
}

.fleft {
	float: left;
}

.fright {
	float: right;
}

.scBox {
	float: left;
	position: relative;
	width: 26%;
	margin: 0px 3px 0px 3px;
	border: 1px solid #666152;
	padding: 1.25em;
}

.removable {
	border: 1px solid #C7CEF9;
	background-color: #E2E6FF;
}

.revokeHandle {
	position: relative;
	top: 3px;
}

.sharelink {
	border: 1px dotted #C7CEF9;
	background-color: #E2E6FF;
	padding: 0.5em 0.5em 0.5em 1em;
}

.sharelinktext {
	color: blue;
	font-size: 130%;
}

#sharelinktag {
	color: blue;
}

#examples {
	line-height: 200%;
}

.notselected {
	opacity: 0.75;
}

.inlineblock {
	display: inline-block;
}

#includeParticipantsHelp {
	font-size: 80%;
}
#includeSourceCalendarHelp {
	font-size: 80%;
}
</style>
<c:url value="/u/${share.key}" var="baseShareUrl" />
<c:url value="/rest/shareDetails" var="shareDetails">
	<c:param name="shareKey" value="${share.key}" />
</c:url>

<c:url value="/rest/includeP" var="includeP" />
<c:url value="/rest/excludeP" var="excludeP" />

<c:url value="/rest/includeSC" var="includeSC" />
<c:url value="/rest/excludeSC" var="excludeSC" />

<c:url value="/rest/toac" var="toac" />
<c:url value="/rest/tofb" var="tofb" />
<c:url value="/rest/addPrivacyFilter" var="addPrivacyFilter" />
<c:url value="/rest/addContentFilter" var="addContentFilter" />
<c:url value="/rest/removeContentFilter" var="removeContentFilter" />
<c:url value="/rest/resetFilters" var="resetFilters" />

<c:url value="/rest/addCalendarFilter" var="addCalendarFilter" />
<c:url value="/rest/removeCalendarFilter" var="removeCalendarFilter" />
<c:url value="/rest/tocs" var="tocs" />
<c:url value="/rest/tocd" var="tocd" />


<c:set var="revokeMessage"
	value="This ShareURL will be permanently deleted. Are you sure?" />
<c:if test="${share.guessable}">
	<c:choose>
		<c:when test="${ ineligibleStatus.ineligibleFromExternalSource}">
			<c:set var="revokeMessage"
				value="Are you sure you wish to disable your Public ShareURL?" />
		</c:when>
		<c:otherwise>
			<c:set var="revokeMessage"
				value="Your Public ShareURL will revert to the default (Free/Busy Only). Are you sure?" />
		</c:otherwise>
	</c:choose>
</c:if>

<rs:resourceURL var="jqueryUiCssPath"
	value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.min.css" />
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}"
	media="all" />
<rs:resourceURL var="jqueryUiPath"
	value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js" />
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.serializeObject.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/edit-share.js"/>"></script>

<script type="text/javascript">
    function setCalType(){
    	var calendarName = $('#allCalendarSelect option:selected').text();
    	var calendarId   = $('#allCalendarSelect option:selected').val();
    //	var calType = calendarName.substring(0, calendarName.indexOf("-")).trim();
    	//alert("type="+calType);
    	//$("#calendarType").val(calType);
		//return false;
    }
</script>


<script type="text/javascript">
$(function() {
	$("#includeParticipantsHelp").dialog({ autoOpen: false, 
		buttons: { Ok: function() { $( this ).dialog( "close" ); } } ,
		modal: true,
		width: 500
	});
	$('#ipTrigger').click(function(e) {
		e.preventDefault();
	    $("#includeParticipantsHelp").dialog( "open" );
	});
	
	$("#includeSourceCalendarHelp").dialog({ autoOpen: false, 
		buttons: { Ok: function() { $( this ).dialog( "close" ); } } ,
		modal: true,
		width: 500
	});
	$('#iSCTrigger').click(function(e) {
		e.preventDefault();
	    $("#includeSourceCalendarHelp").dialog( "open" );
	});
	
	$('.revokeform').submit(function(e) {
		e.preventDefault();
		var confirmed = confirm('${revokeMessage}');
		if(confirmed) {
			$('.revokeform').unbind();
			$('.revokeform').submit();
		}
	});
	
	$('.optoutform').submit(function(event) {
		event.preventDefault();
		var confirmed = confirm('Any customizations to your Public ShareURL will be lost if you opt out. Are you sure you want to opt out?');
		if(confirmed) {
			$(this).unbind();
			$(this).submit();
		}
	});
	setupFormHandlers();
	$("#datex").datepicker({ dateFormat: 'yy-mm-dd' });
	$("#datey").datepicker({ dateFormat: 'yy-mm-dd' });
	$('#clientselect').change(function(e) { renderShareUrlExample('${baseShareUrl}'); });
	$('#x').change(function(e) { renderShareUrlExample('${baseShareUrl}'); });
	$('#negatex').change(function(e) { renderShareUrlExample('${baseShareUrl}'); });
	$('#y').change(function(e) { renderShareUrlExample('${baseShareUrl}'); });
	$('#datex').change(function(e) { renderShareUrlExample('${baseShareUrl}'); });
	$('#datey').change(function(e) { renderShareUrlExample('${baseShareUrl}'); });
	var initShare = { "freeBusyOnly": ${share.freeBusyOnly}, 
					  "includeParticipants": ${share.includeParticipants}, 
					  "calendarSelect": ${share.calendarSelect}, 
					  "sharePreferences": {
							"classificationFilters": ${viewhelper:classificationFiltersToJSON(share.sharePreferences.classificationFilters)},
							"contentFilters": ${viewhelper:contentFiltersToJSON(share.sharePreferences.contentFilters)},
							"calendarFilters": ${viewhelper:contentFiltersToJSON(share.sharePreferences.calendarFilters)},
							"test":"true",
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
		renderShareUrlExample('${baseShareUrl}');
	});
});
function setupFormHandlers() {
	applySubmitHandlerIfPresent('#tofb', '${tofb}', '#labelindicatorfb');
	applySubmitHandlerIfPresent('#toac', '${toac}', '#labelindicatorac');
	applySubmitHandlerIfPresent('#includeP1', '${includeP}', '#labelindicatorip');
	applySubmitHandlerIfPresent('#includeSC', '${includeSC}', '#labelindicatorsc');
	$('#fbRadio').click(function() {
	    $('#tofb').submit();
	});
	$('#acRadio').click(function() {
	    $('#toac').submit();
	});
	$('#ip').click(function() {
	    $('#includeP1').submit();
	});
	$('#isc').click(function() {
	    $('#includeSC').submit();
	});
	
	applySubmitHandlerIfPresent('#contentFilter', '${addContentFilter}', '#labelindicatorcf', function() {
		setupRemoveContentFilterForms();
	});
	
	applySubmitHandlerIfPresent('#tocd', '${tocd}', '#labelindicatorcd');
	applySubmitHandlerIfPresent('#tocs', '${tocs}', '#labelindicatorcs');
	$('#cdRadio').click(function() {
	    $('#tocd').submit();
	});
	$('#csRadio').click(function() {
	    $('#tocs').submit();
	});
	
// 	applySubmitHandlerIfPresent('#exchangeCalendarFilter', '${addCalendarFilter}', '#labelindicatorExchangeCalCalf', function() {
// 		setupRemoveCalendarFilterForms();
// 	});
// 	applySubmitHandlerIfPresent('#wiscCalCalendarFilter', '${addCalendarFilter}', '#labelindicatorWiscCalCalf', function() {
// 		setupRemoveCalendarFilterForms();
// 	});
	
	applySubmitHandlerIfPresent('#allCalendarFilterForm', '${addCalendarFilter}', '#calendarSelectLabelIndicator', function() {
		setupRemoveCalendarFilterForms();
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
	setupRemoveCalendarFilterForms();
}

function setupRemoveCalendarFilterForms(){
	$('.removeCalendarFilter').each(function(i) {
		applySubmitHandlerIfPresent($(this), '${removeCalendarFilter}', '#calendarSelectLabelIndicator', function() {
			setupRemoveCalendarFilterForms();
		});
// 		applySubmitHandlerIfPresent($(this), '${removeCalendarFilter}', '#labelindicatorExchangeCalCalf', function() {
// 			setupRemoveCalendarFilterForms();
// 		});
	});
	
	$('#calendarFilters .revokeHandle').unbind('click');
	$('#calendarFilters .revokeHandle').click(function(event) {
		var anchor = $(this);
		anchor.parent('form').submit();
	})
}

function setupRemoveContentFilterForms() {
	$('.removeContentFilter').each(function(i) {
		applySubmitHandlerIfPresent($(this), '${removeContentFilter}', '#labelindicatorcf', function() {
			setupRemoveContentFilterForms();
		});
	});

	$('#contentFilters .revokeHandle').unbind('click');
	$('#contentFilters .revokeHandle').click(function(event) {
		var anchor = $(this);
		anchor.parent('form').submit();
	})
}
function postAndRenderPreferences(url, form, indicator, callback) {
	//clear all indicators
	$('.ind').empty();
	//append gif to current indication
	$('<img src="<c:url value="/img/indicator.gif"/>"/>').appendTo(indicator);
	//serialize the formdata
	var formdata = $(form).serializeObject();
	//append sharkey to serialized data
	formdata["shareKey"] = '${share.key}';
	//see http://api.jquery.com/jQuery.post/
	$.post(url,
			formdata,
			function(responsedata) {
				if(responsedata.share) {
					
					lastShare = responsedata.share;
					$(indicator).empty();
                    $('<img src="${tickIcon}"/>').appendTo(indicator);
					renderShareControls(responsedata.share);
					renderFilterPreferences(responsedata.share, '${revokeIcon}', '${removeContentFilter}', responsedata.removeContentFilter);
					renderFilterCalendarPreferences(responsedata.share, '${revokeIcon}', '${removeCalendarFilter}', responsedata.calendarMap, responsedata.removeCalendarFilter);
					
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
					renderFilterCalendarPreferences(lastShare, '${revokeIcon}', '${removeCalendarFilter}', false);
				}
			},
			"json");
	//here?

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

	<%@ include file="/WEB-INF/jsp/theme/body-start.jsp"%>
	<%@ include file="/WEB-INF/jsp/login-info.jsp"%>

	<div id="content" class="main col">
		<h1 style='font-size:18px;'>Edit your ShareURL</h1><hr/><br/>
		<div id="shareDetails" class="margin3">
			<p>
				<strong>You are editing the privacy settings for:</strong>
			</p>
			<p class="key large padding1">${viewhelper:getVirtualServerAddress(pageContext.request)}<span>${baseShareUrl}</span></p>
			<p>The settings you select below will determine what calendar
				data will be accessible through your ShareURL. To allow different
				access levels for different audiences, you can create multiple
				ShareURLs with different options.</p>
		</div>

		<div id="shareControls2">

			<c:if test="${not share.guessable }">
				<div class="margin3 padding1">
					<form id="setLabel">
						<fieldset>
							<label for="label">Label (optional):</label>&nbsp;<input
								id="labelinput" name="label" type="text" value="${share.label}"><input
								id="lsubmit" type="submit" value="Change">&nbsp;<span
								id="labelindicator" class="ind"></span>
						</fieldset>
						<p>Adding a label can help you remember how you intend to use
							this ShareURL or with whom you have shared it's address. Your
							label will be visible to you only.</p>
					</form>
				</div>
			</c:if>

	<p class="padTop"><strong>Which calendar(s) do you want to include in your ShareURL?</strong></p>
	<div id="outerCalendarSelect" class="bordered padding1 margin3">

			<div id="calDefault" >
				<form action="${tocd }" method="post" id="tocd">
					<fieldset>
						<input id="cdRadio" type="radio" name="calDefault" value="true" /><label
							for="calDefault"><strong>Default WiscCal calendar Only</strong></label>&nbsp;<span
							id="labelindicatorcd" class="ind"></span>
					</fieldset>
				</form>
				<div id="scCalDefaultInner">
					<p>Only display events contained in your default WiscCal Calendar</p>
				</div>
			</div>
			<!-- end calDefault -->

			<br />
			<hr />
			<br />

			<div id="calSelect" >
				<form action="${tocs }" method="post" id="tocs">
					<fieldset>
						<input id="csRadio" type="radio" name="calSelect" value="true" /><label
							for="calSelect"><strong>Select one or more calendars</strong></label>&nbsp;<span
							id="labelindicatorcs" class="ind"></span>
					</fieldset>
				</form>
				<div id="scCalSelectInner">
					<p>Display events from selected Calendars</p>
				
					<div id="calSelectFilters">
							
							<form action="${addCalendarFilter}" method="post" id="allCalendarFilterForm">
								<fieldset>
									<span>Include events from the following calendar</span>
									<form:select path="share" name="calendarId" id="allCalendarSelect" class="calSelectDDL">
										<form:options items="${allCalendarList}" />
					 				</form:select>
					 				<input id="addCalFilter" type="submit" value="Add" class="calSelectButton" onclick="setCalType();" />&nbsp;
					 				<input id="calendarType" name="calendarType" type="hidden" value="" />
					 				<span id="calendarSelectLabelIndicator"	class="ind"></span><br />
								</fieldset>
							
							</form>
							 
							<ul id="calendarFilters">
								<c:choose>
									<c:when test="${empty share.sharePreferences.calendarMatchPreferences}" >
										<li><span class="removable"><c:out value="${allCalendarList[defaultCalendarId]} " /></span>&nbsp;
												<form class="removeCalendarFilter inlineblock"
													action="${removeCalendarFilter}" method="post"
													id="removeCalendarFilter${status.index}">
													<fieldset>
														<input type="hidden" name="calendarName" value="<c:out value="${allCalendarList[defaultCalendarId]}" />" /><input
															type="hidden" name="calendarId" value="<c:out value="${defaultCalendarId}" />" />
													</fieldset>
													<img class="revokeHandle" src="${revokeIcon }"
														title="Remove this filter" />
												</form></li>
									</c:when>
									<c:otherwise>
										<c:forEach
											items="${share.sharePreferences.calendarMatchPreferences }"
											var="pref" varStatus="status">
											<li><span class="removable"><c:out value="${allCalendarList[pref.value]}" /></span>&nbsp;
												<form class="removeCalendarFilter inlineblock"
													action="${removeCalendarFilter}" method="post"
													id="removeCalendarFilter${status.index}">
													<fieldset>
														<input type="hidden" name="calendarName" value="<c:out value="${allCalendarList[pref.value]}" />" /><input
															type="hidden" name="calendarId" value="${pref.value}" />
													</fieldset>
													<img class="revokeHandle" src="${revokeIcon }"
														title="Remove this filter" />
												</form></li>
										</c:forEach>								
									</c:otherwise>
								</c:choose>
							</ul>
						</div>
						<!-- end calSelectFilters -->
					</div>
					<!-- end scCalSelectInner -->
			</div>
			<!-- end calSelect -->
			
		</div>
		<!--  end outerCalendarSelect -->
		 
		 <p class="padTop"><strong>How much event information do you want to include in your ShareURL?</strong></p>
		 <div id="outerDetailsSelect" class="bordered padding1 margin3" >
			<div id="scFreeBusy" >
				<form action="${tofb }" method="post" id="tofb">
					<fieldset>
						<input id="fbRadio" type="radio" name="freebusyonly" value="true" /><label
							for="freebusyonly"><strong>Free/Busy only</strong></label>&nbsp;<span
							id="labelindicatorfb" class="ind"></span>
					</fieldset>
				</form>
				<div id="scFreeBusyInner">
					<p>Free/Busy only ShareURLs display only the start and end
						times for periods that you are busy, all other meeting details are
						withheld.</p>
				</div>
			</div>
			<!-- end scFreeBusy -->
			<br />
			<hr />
			<br />
			
			<div id="scAllCalendar" >

				<form action="${toac }" method="post" id="toac">
					<fieldset>
						<input id="acRadio" type="radio" name="allcalendar" value="true" /><label
							for="allcalendar"><strong>Include more event
								detail</strong></label>&nbsp;<span id="labelindicatorac" class="ind"></span>
						<p>ShareURLs using this setting display meeting title,
							location, and details along with start and end times. Meeting
							participants are withheld by default.
					</fieldset>
				</form>

				<div id="scAllCalendarInner">

					<form action="${includeP}" method="post" id="includeP1">
						<fieldset>
							<input id="ip" type="checkbox" name="includeParticipants"
								<c:if test="${share.includeParticipants }">checked="checked"</c:if> />&nbsp;<label
								for="includeParticipants">Include attendees and
								organizer on group appointments</label>&nbsp;<span id="labelindicatorip"
								class="ind"></span>&nbsp;<a id="ipTrigger"
								href="#includeParticipantsHelp"
								title="Include Participants Option Help">What's this?</a>
						</fieldset>
					</form>
					
					<form action="${includeSC}" method="post" id="includeSC">
						<fieldset>
							<input id="isc" type="checkbox" name="includeSourceCalendar"
								<c:if test="${share.includeSourceCalendar }">checked="checked"</c:if> />&nbsp;<label
								for="includeSourceCalendar">Include the name of the calendar which contains this event</label>&nbsp;<span id="labelindicatorsc"
								class="ind"></span>&nbsp;<a id="iSCTrigger"
								href="#includeSourceCalendarHelp"
								title="Include Source Calendar Option Help">What's this?</a>
						</fieldset>
					</form>

					<div id="filters" class="padding2">
						<form action="${addPrivacyFilter}" method="post"
							id="privacyFilter">
							<p>Only include events with the following <a
									target="_new_visibilityHelp"
									href="https://kb.wisc.edu/helpdesk/page.php?id=24155">visibility</a>&nbsp;<span
									id="labelindicatorcl" class="ind"></span>:
							</p>
							<fieldset>
								<input id="publicClass" type="checkbox" name="includePublic"
									class="classFilterCheckbox" />&nbsp;<label for="public">Public</label><br />
								<input id="confidClass" type="checkbox"
									name="includeConfidential" class="classFilterCheckbox" />&nbsp;<label
									for="confidential">Show Date and Time Only</label><br /> <input
									id="privateClass" type="checkbox" name="includePrivate"
									class="classFilterCheckbox" />&nbsp;<label for="private">Private</label><br />
							</fieldset>
						</form>
						<form action="${addContentFilter}" method="post"
							id="contentFilter">
							<fieldset>
								<span>Only include events with </span> <select id="propertyName"
									name="propertyName"><option value="SUMMARY">Title</option>
									<option value="LOCATION">Location</option>
									<option value="DESCRIPTION">Description</option></select> <span>&nbsp;containing&nbsp;</span><input
									type="text" name="propertyValue" />&nbsp;<input id="addFilter"
									type="submit" value="Add" />&nbsp;<span id="labelindicatorcf"
									class="ind"></span><br />
							</fieldset>
						</form>
						<ul id="contentFilters">
							<c:forEach
								items="${share.sharePreferences.propertyMatchPreferences }"
								var="pref" varStatus="status">
								<li><span class="removable">${pref.displayName }</span>&nbsp;
									<form class="removeContentFilter inlineblock"
										action="${removeContentFilter}" method="post"
										id="removeContentFilter${status.index}">
										<fieldset>
											<input type="hidden" name="propertyName" value="${pref.key}" /><input
												type="hidden" name="propertyValue" value="${pref.value}" />
										</fieldset>
										<img class="revokeHandle" src="${revokeIcon }"
											title="Remove this filter" />
									</form></li>
							</c:forEach>
						</ul>
					</div>

				</div>
				<!-- end scAllCalendarInner -->
			</div>
			<!-- end scAllCalendar -->
			
			</div>
		<!--  end outerDetailsSelect -->

			<div id="revoke" class="margin3 padding1">
				<c:choose>
					<c:when
						test="${share.guessable && not ineligibleStatus.ineligibleFromExternalSource}">
						<form action="<c:url value="/rest/opt-out"/>" method="post"
							class="optoutform">
							<fieldset>
								<input id="osubmit" type="submit"
									value="Opt out from Public ShareURL" />
							</fieldset>
						</form>
					</c:when>
					<c:otherwise>
						<c:url var="revokeUrl" value="revoke.html">
							<c:param name="id" value="${share.key }" />
						</c:url>

						<form:form action="${revokeUrl}" cssClass="revokeform"
							method="post">
							<input type="submit" class="revokebutton"
								value="Delete this ShareURL" />
						</form:form>
					</c:otherwise>
				</c:choose>
			</div>

		</div>
		<!-- end shareControls2 -->


		<!--  </div> -->
		<!--  end privacySettings -->

		<hr />

		<div id="examples" class="margin3">
			<h2>Using your ShareURL</h2>
			<p>This ShareURL can be viewed with the following link. Use the
				form controls below to update the example for your use case:</p>
			<div class="sharelink">
				<a id="sharelinktag" href="${baseShareUrl }/dr(-14,30)?ical"><span
					class="sharelinktext">${viewhelper:getVirtualServerAddress(pageContext.request)}<span>${baseShareUrl}</span><span
						id="dateRange">/dr(-14,30)</span><span id="icsSuffix"></span><span
						id="queryParameters">?ical</span></span></a>
			</div>

			<p>
				<label for="client">I want to view my ShareURL in </label> <select
					name="client" id="clientselect">
					<option value="native" selected="selected">Microsoft
						Outlook, Mozilla Thunderbird, or Apple iCal</option>
					<option value="iphone">an iPhone or iPad</option>
					<option value="browser">a Web Browser, like Firefox,
						Chrome, or Internet Explorer</option>
					<option value="google">Google Calendar</option>
					<option value="ics">an ICS (iCalendar) file</option>
					<option value="news">a News (RSS) Reader</option>
					<option value="json">Javascript Object Notation (JSON)</option>
				</select>
			</p>

			<p>
				<label for="x">I would like to see data from </label><input id="x"
					type="number" name="x" value="14" min="-999" max="999" /> days <select
					id="negatex"><option value="negate" selected="selected">back</option>
					<option value="">forward</option></select> <label for="y">through </label><input
					id="y" type="number" name="y" value="30" min="-999" max="999" />
				days forward.
			</p>
			<p>
				OR, I would like to see data from <a id="justToday"
					href="#justToday">just "today"</a>.
			</p>
			<p>
				OR, <label for="datex">I would like to see data from
					specifically </label><input id="datex" type="text" name="datex" /><label
					for="datey"> through </label><input id="datey" type="text"
					name="datey" />
			</p>

			<p>
				I would like to see <a
					title="View ShareURL Options (Opens new window)" target="_new_help"
					href="http://kb.wisc.edu/wisccal/page.php?id=13322">all
					available options for ShareURLs&raquo;</a>, including options for web
				developers.
			</p>

		</div>
		<div id="includeParticipantsHelp" title="Include Participants Help">
			<p>This setting controls whether or not the name and email
				address for meeting participants (attendees, organizer) is included
				in the data for group events displayed by your ShareURL.</p>
			<br />
			<p>
				Consider this option carefully: <strong>if you meet with
					students regularly, you should not enable this setting</strong>. This
				setting is best used on Traditional ShareURLs that are not shared
				with a wide audience. The WiscCal team recommends you avoid using
				this setting on your Public ShareURL.
			</p>
		</div>
		<div id="includeSourceCalendarHelp" title="Include Source Calendar Help">
			<p>This setting controls whether or not the name of your calendar is included
				in the data for events displayed by your ShareURL.</p>
		</div>
	</div>
	<!-- content -->
	<%@ include file="/WEB-INF/jsp/theme/body-end.jsp"%>
</body>
</html>