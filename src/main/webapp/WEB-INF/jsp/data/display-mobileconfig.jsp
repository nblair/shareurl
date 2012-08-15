<%@ page contentType="application/x-apple-aspen-config" %>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<c:url value="/u/${share.key}" var="baseShareUrl"/>

<c:choose>
<c:when test="${requestDetails.canonical}">
<fmt:formatDate value="${requestDetails.startDate}" type="time" pattern="yyyy-MM-dd" var="startFormatted"/>
<fmt:formatDate value="${requestDetails.endDate}" type="time" pattern="yyyy-MM-dd" var="endFormatted"/>
<c:set value="${viewhelper:getVirtualServerAddress(pageContext.request)}${baseShareUrl}?ical&amp;start=${startFormatted}&amp;end=${endFormatted}" var="subscribeUrl"/>
</c:when>
<c:otherwise>
<c:set value="${viewhelper:getVirtualServerAddress(pageContext.request)}${baseShareUrl}/${requestDetails.datePhrase}?ical" var="subscribeUrl"/>
</c:otherwise>
</c:choose>
			
<c:choose>
<c:when test="${share.freeBusyOnly}">
<c:set var="description" value="Free/Busy Only"/>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${share.eventFilterCount == 0}">
<c:set var="description" value="All Calendar Data"/>
</c:when>
<c:otherwise>
<c:set var="description" value="${share.sharePreferences.filterDisplay}"/>
</c:otherwise>
</c:choose>
</c:otherwise>
</c:choose>

<plist version="1.0">
<dict>
	<key>PayloadContent</key>
	<array>
		<dict>
			<key>PayloadDescription</key>
			<string>${description}</string>
			<key>PayloadDisplayName</key>
			<string>Subscribed Calendar (WiscCal ShareURL - ${share.key})</string>
			<key>PayloadIdentifier</key>
			<string>edu.wisc.wisccal.shareurl.mobileconfig.${share.key}</string>
			<key>PayloadOrganization</key>
			<string>University of Wisconsin-Madison</string>
			<key>PayloadType</key>
			<string>com.apple.subscribedcalendar.account</string>
			<key>PayloadUUID</key>
			<string>WiscCal-ShareURL-${share.key}</string>
			<key>PayloadVersion</key>
			<integer>1</integer>
			<key>SubCalAccountDescription</key>
			<string>WiscCal ShareURL - ${share.key}</string>
			<key>SubCalAccountHostName</key>
			<string>${subscribeUrl}</string>
			<key>SubCalAccountUseSSL</key>
			<false/>
		</dict>
	</array>
	<key>PayloadDescription</key>
	<%-- displayed on Install Profile page --%>
	<string>WiscCal ShareURL iOS configuration for ${share.key}.</string>
	<key>PayloadDisplayName</key>
	<string>WiscCal ShareURL - ${share.key}</string>
	<key>PayloadIdentifier</key>
	<string>edu.wisc.wisccal.shareurl.mobileconfig</string>
	<key>PayloadOrganization</key>
	<string>University of Wisconsin-Madison</string>
	<key>PayloadRemovalDisallowed</key>
	<false/>
	<key>PayloadType</key>
	<string>Configuration</string>
	<key>PayloadUUID</key>
	<string>WiscCal-ShareURL-MobileConfig-${share.key}</string>
	<key>PayloadVersion</key>
	<integer>1</integer>
</dict>
</plist>