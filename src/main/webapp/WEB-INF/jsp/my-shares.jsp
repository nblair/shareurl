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
<rs:resourceURL var="keyImg" value="/rs/famfamfam/silk/1.3/key.png"/>
<style type="text/css">
.key {
color:green;
}
.large {
font-size:130%;
}
li.share {
list-style-image: url(${keyImg});
padding-bottom: 1em;
}
li.share a:hover{
text-decoration: underline;
color:green;
}
.details {
font-style:italic;
}
#footnotewarning {
font-size:65%;
margin: 0 1em 0 1em;
}
</style>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(function() {
	//$(':submit').lockSubmit();
	/*
	$('#createpublic').submit(function(event) {
		alert('submit');
		event.preventDefault();
		postCreatePublic();		
	});
	*/
	
	refreshMyShares();
});

function refreshMyShares() {
	//showChangeInProgress("#schedulechangestatus", '<spring:message code="updating.availability.schedule"/>');
	$.get('<c:url value="/my-shares"><c:param name="format" value="json"/></c:url>', 
			{ },
			function(data) {
				if(!data.shares) {
					alert('no shares set in response: ' + data);
				} else {
					$('#myshares').empty();
					var ul = $('<ul/>');
					ul.addClass('sharelist');
					if (data.shares.length == 0) {
						$('<li>You currently have no ShareURLs; click Generate ShareURL or generate Public URL to get started.</li>').appendTo(ul);
					} else {
						for(var i = 0; i < data.shares.length; i++) {
							var share = data.shares[i];
							if(share.freeBusyOnly) {
								$('<li class="share"><a title="View Details and/or Manage ' + share.key +'" href="manage?id=' + share.key + '"><span class="key large">' + share.key + 
										'</span></a>:&nbsp;<span class="details">Free Busy only.</span></li>').appendTo(ul);
							} else if (share.eventFilterCount == 0) {
								var details = 'All Calendar Data';
								if(share.includeParticipants) {
									details += ', Include Participants';
								}
								details += '.';
								$('<li class="share"><a title="View Details and/or Manage ' + share.key +'" href="manage?id=' + share.key + '"><span class="key large">' + share.key + 
								'</span></a>:&nbsp;<span class="details">' + details + '</span></li>').appendTo(ul);
							} else {
								var details = share.sharePreferences.filterDisplay;
								if(share.includeParticipants) {
									details += ', Include Participants';
								}
								details += '.';
								$('<li class="share"><a title="View Details and/or Manage ' + share.key +'" href="manage?id=' + share.key + '"><span class="key large">' + share.key + 
								'</span></a>:&nbsp;<span class="details">' + details + '</span></li>').appendTo(ul);
							}
						}
					}
					ul.appendTo('#myshares');
				}	
			},
			'json');
};

function postCreatePublic() {
	//showChangeInProgress("#schedulechangestatus", '<spring:message code="updating.availability.schedule"/>');
	$.post('<c:url value="/create-public"/>',
			{ },
			function(data) {
				if(data.success) {
					//showChangeSuccess("#schedulechangestatus", '<spring:message code="schedule.successfully.updated.for"/> ' + startTime);
					refreshMyShares();
					//$('.publicshareform').hide();
				} else {
					alert('failed to create public share');
				}
			}, 'json');
};
</script>
<title>Share your WiscCal Calendar - My ShareURLs</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/theme/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>

<div id="content" class="main col">

<div id="controls" class="info">
<p>A ShareURL is a special link to your WiscCal account that returns your calendar event data in a number of different formats. There are 2 types:</p>
<ol>
<li><a href="<c:url value="/generate"/>" class="large">Generate a traditional ShareURL</a>: These URLs include a randomly generated string of letters and numbers. 
These are more intended for the privacy-conscious that don't like to or cannot advertise their email address. You can have several different
traditional ShareURLs with different options.</li>
<li>Public ShareURLs (new!) work just like traditional ShareURLs, however the link include your email address instead of a random alpha-numeric string.
<c:choose>
<c:when test="${hasGuessable}">
</c:when>
<c:otherwise>
<div class="publicshareform">
<form action="<c:url value="/create-public"/>" method="post" id="createpublic">
<fieldset>
<input type="submit" value="Create my Public ShareURL"/>&nbsp;<span class="inprogressplaceholder"/>
</fieldset>
</form>
</div>
</c:otherwise>
</c:choose>
</li>
</ol>
</div>

<h2>My ShareURLs</h2>
<div id="myshares">
 
<c:if test="${not empty shares}">
<ul>
<c:forEach items="${shares}" var="share">
<c:url var="manageUrl" value="manage">
<c:param name="id" value="${share.key}"/>
</c:url>
<c:choose>
<c:when test="${share.freeBusyOnly == true}">
<li class="share"><a title="View Details and/or Manage ${share.key}" href="${manageUrl}"><span class="key large">${share.key }</span></a>:&nbsp;<span class="details">Free Busy only.</span> 
</li>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${share.eventFilterCount == 0}">
<li class="share"><a title="View Details and/or Manage ${share.key}" href="${manageUrl}"><span class="key large">${share.key }</span></a>:&nbsp;<span class="details">All Calendar Data<c:if test="${share.includeParticipants}">, Include Participants</c:if>.</span> 
</li>
</c:when>
<c:otherwise>
<li class="share"><a title="View Details and/or Manage ${share.key}" href="${manageUrl}"><span class="key large">${share.key }</span></a>:&nbsp;<span class="details">${share.sharePreferences.filterDisplay}<c:if test="${share.includeParticipants}">, Include Participants</c:if>.</span>
</li>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>
</c:forEach>
</ul>
</c:if>

</div>




</div> <!-- content -->
<div class="alert" id="footnotewarning">
<p>
<strong>Security note</strong><br/>
The intent of this WiscCal feature is to allow you to share your WiscCal calendar data publicly.
All information that you store in WiscCal, or that other faculty, staff, or students submit to your calendar, 
will be viewable by any external user. The University of Wisconsin-Madison assumes no responsibility for how you 
share this information or how it is used.
</p>
</div>
<%@ include file="/WEB-INF/jsp/theme/body-end.jsp" %>
</body>
</html>