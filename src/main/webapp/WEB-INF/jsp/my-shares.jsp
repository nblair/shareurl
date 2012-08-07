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
<%@ include file="/WEB-INF/jsp/theme/head-elements.jsp" %>
<rs:resourceURL var="keyImg" value="/rs/famfamfam/silk/1.3/key.png"/>
<style type="text/css">
.key { color:green;}
.large { font-size:120%;}
li.share { list-style-image: url(${keyImg});padding-bottom: 1em;}
li.share a:hover{ text-decoration: underline;color:green;}
.details { font-style:italic;}
#footnotewarning { font-size:65%;margin: 0 3em 0 3em;}
</style>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(function() {
	$("input[type=submit]").removeAttr("disabled");
	
	$('#createpublic').submit(function(event) {
		event.preventDefault();
		$('#psubmit').attr('disabled', 'disabled');
		postCreatePublic();		
	});
	$('#createtraditional').submit(function(event) {
        event.preventDefault();
        $('#tsubmit').attr('disabled', 'disabled');
        postCreateTraditional();     
    });
});

function refreshMyShares(fadeIn) {
	$.get('<c:url value="/my-shares"><c:param name="format" value="json"/></c:url>', 
			{ },
			function(data) {
				if(data.shares) {
					$('#myshares').empty();
					var ul = $('<ul/>');
					ul.addClass('sharelist');
					if (data.shares.length == 0) {
						$('<li>You currently have no ShareURLs; click Generate ShareURL or generate Public URL to get started.</li>').appendTo(ul);
					} else {
						for(var i = 0; i < data.shares.length; i++) {
							var share = data.shares[i];
							if(share.freeBusyOnly) {
								var liText = '<li class="share"><a title="View Details and/or Edit Options for ' + share.key +'" href="manage?id=' + share.key + '"><span class="key large">View and/or Edit Options for ' + share.key;
								if(share.guessable) {
									liText += ' (Public ShareURL)';
								}
								liText += '</span></a>:&nbsp;<span class="details">Free Busy only.</span></li>';
								var li = $(liText);
								if(fadeIn) {
									li.appendTo(ul).fadeIn();
								} else {
									li.appendTo(ul);
								}
							} else if (share.eventFilterCount == 0) {
								var details = 'All Calendar Data';
								if(share.includeParticipants) {
									details += ', Include Participants';
								}
								details += '.';
								$('<li class="share"><a title="View Details and/or Manage ' + share.key +'" href="manage?id=' + share.key + '"><span class="key large">View and/or Edit Options for ' + share.key + 
								'</span></a>:&nbsp;<span class="details">' + details + '</span></li>').appendTo(ul);
							} else {
								var details = share.sharePreferences.filterDisplay;
								if(share.includeParticipants) {
									details += ', Include Participants';
								}
								details += '.';
								$('<li class="share"><a title="View Details and/or Manage ' + share.key +'" href="manage?id=' + share.key + '"><span class="key large">View and/or Edit Options for ' + share.key + 
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
	$.post('<c:url value="/rest/create-public"/>',
			{ },
			function(data) {
				if(data.success) {
					refreshMyShares(true);
					$('#guessableInner').empty();
					$('<p><span class="large">Your Public ShareURL is enabled</span>, use the link below to change how much of your calendar data is displayed.</p>')
						.appendTo('#guessableInner').fadeIn();
				} else {
					alert('failed to create public share: ' + data.message);
				}
			}, 'json');
};
function postCreateTraditional() {
    $.post('<c:url value="/rest/create-traditional"/>',
            { },
            function(data) {
                if(data.success) {
                    refreshMyShares(true);   
                } else {
                    alert('failed to create traditional share: ' + data.message);
                }
                setTimeout(function() {
                    $('#tsubmit').removeAttr('disabled');
                }, 30000);
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
<p>
<span class="large">Traditional ShareURLs</span> use a randomly generated string of letters and numbers to identify your account. 
These are intended for the privacy-conscious that don't like to or cannot advertise their email address. You can have several different
traditional ShareURLs with different options.</p>
<form action="<c:url value="/rest/create-traditional"/>" method="post" id="createtraditional">
<fieldset>
<input id="tsubmit" type="submit" value="Create a new Traditional ShareURL"/>
</fieldset>
</form>
<hr/>
<div id="guessableInner">
<c:choose>
<c:when test="${not hasGuessable}">
<p><span class="large">Public ShareURLs</span> (new!) work just like traditional ShareURLs, however the link contains your email address instead of a random alpha-numeric string.</p>
<div class="publicshareform">
<form action="<c:url value="/rest/create-public"/>" method="post" id="createpublic">
<fieldset>
<input id="psubmit" type="submit" value="Create my Public ShareURL"/>
</fieldset>
</form>
</div>
</c:when>
<c:otherwise>
<p><span class="large">Your Public ShareURL is enabled</span>, use the link below to change how much of your calendar data is displayed.</p>
</c:otherwise>
</c:choose>
</div> <!-- end id=guessableInner -->

</div> <!-- end id=controls -->

<h2>My ShareURLs</h2>
<div id="myshares">
 
<c:if test="${not empty shares}">
<ul>
<c:forEach items="${shares}" var="share">
<c:url var="manageUrl" value="manage">
<c:param name="id" value="${share.key}"/>
</c:url>
<c:choose>
<c:when test="${share.guessable }">
<c:set var="linkText" value="View and/or Edit Options for ${share.key } (Public ShareURL)"/>
</c:when>
<c:otherwise>
<c:set var="linkText" value="View and/or Edit Options for ${share.key }"/>
</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${share.freeBusyOnly == true}">
<li class="share"><a title="View Details and/or Manage ${share.key}" href="${manageUrl}"><span class="key large">${linkText}</span></a>:&nbsp;<span class="details">Free Busy only.</span> 
</li>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${share.eventFilterCount == 0}">
<li class="share"><a title="View Details and/or Manage ${share.key}" href="${manageUrl}"><span class="key large">${linkText}</span></a>:&nbsp;<span class="details">All Calendar Data<c:if test="${share.includeParticipants}">, Include Participants</c:if>.</span> 
</li>
</c:when>
<c:otherwise>
<li class="share"><a title="View Details and/or Manage ${share.key}" href="${manageUrl}"><span class="key large">${linkText}</span></a>:&nbsp;<span class="details">${share.sharePreferences.filterDisplay}<c:if test="${share.includeParticipants}">, Include Participants</c:if>.</span>
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