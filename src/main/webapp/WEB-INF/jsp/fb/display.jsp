<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<rs:resourceURL var="clockIcon" value="/rs/famfamfam/silk/1.3/clock.png" />
<rs:resourceURL var="nextIcon"
	value="/rs/famfamfam/silk/1.3/resultset_next.png" />
<rs:resourceURL var="prevIcon"
	value="/rs/famfamfam/silk/1.3/resultset_previous.png" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/calendar-theme2.css"/>" media="all" />
<fmt:formatDate value="${startDate}" type="time" pattern="MM/dd/yyyy"
	var="startDateFormatted" />
<fmt:formatDate value="${endDate}" type="time" pattern="MM/dd/yyyy"
	var="endDateFormatted" />
<c:choose>
	<c:when test="${requestDetails.numberDaysDisplayed > 0 }">
		<c:set var="title"
			value="Free/Busy for ${startDateFormatted} - ${endDateFormatted}" />
	</c:when>
	<c:otherwise>
		<c:set var="title" value="Free/Busy for ${startDateFormatted}" />
	</c:otherwise>
</c:choose>
<title>${title}</title>
<style type="text/css">
.cancel {
	text-decoration: line-through;
}

.white {
	color: #fff !important;
}
</style>
</head>
<body>
	<div id="content">
		<div id="timeHeader">
			<div class="navrow1 sharedaterange">
				<c:choose>
					<c:when test="${requestDetails.numberDaysDisplayed == 0}">
						<c:choose>
							<c:when test="${requestDetails.canonical }">
								<span class="nowshowing">${startDateFormatted}</span>
							</c:when>
							<c:otherwise>
								<a
									href="<c:url value="/u/${shareId}/${requestDetails.prevDatePhrase}"/>"
									title="previous day"><img src="${prevIcon}"
									alt="previous day" /></a>
&nbsp;<span class="nowshowing">${startDateFormatted}</span>&nbsp;
<a
									href="<c:url value="/u/${shareId}/${requestDetails.nextDatePhrase}"/>"
									title="next day"><img src="${nextIcon}" alt="next day" /></a>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<span class="nowshowing">${startDateFormatted}&nbsp;-&nbsp;${endDateFormatted}</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div id="calendarEvents">
			<c:choose>
				<c:when test="${noEvents}">
					<span class="weak summary">No events.</span>
				</c:when>
				<c:otherwise>
					<c:forEach var="period" items="${busyPeriods}">
						<img src="${clockIcon}" alt="Busy" title="Busy" />
						<c:choose>
							<c:when test="${oevent:isAllDayPeriod(period)}">
								<span class="weak"><fmt:formatDate
										value="${period.start}" type="date" pattern="MM/dd/yyyy" />&nbsp;All
									Day</span>
							</c:when>
							<c:otherwise>
								<span class="weak"><fmt:formatDate
										value="${period.start}" type="date" pattern="MM/dd/yyyy" />&nbsp;<fmt:formatDate
										value="${period.start}" type="time" pattern="hh:mm a" />&nbsp;-&nbsp;<fmt:formatDate
										value="${period.end}" type="time" pattern="hh:mm a" /></span>
							</c:otherwise>
						</c:choose>
						<br />
						<span class="summary">Busy</span>
						<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</body>
</html>