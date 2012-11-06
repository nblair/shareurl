
function applySubmitHandlerIfPresent(element, url, indicator) {
	var el = $(element);
	if(el) {
		el.submit(function(event) {
			event.preventDefault();
			postAndRenderPreferences(url, element, indicator);
		});
	}
};

function renderShareControls(share) {
	resetPrivacyControls();
	
	if(share.freeBusyOnly) {
		$('#fbRadio').attr('checked', 'checked');
		enableFreeBusy();
		disableAllCalendar();
	} else {
		$('#acRadio').attr('checked', 'checked');
		disableFreeBusy();
		enableAllCalendar();
		if(share.includeParticipants) {
			$('#ip').attr('checked', 'checked');
		}
	}
}
function resetPrivacyControls() {
	$('#fbRadio').attr('checked', '');
	$('#acRadio').attr('checked', '');
	$('#scFreeBusyInner').removeClass('notselected');
	$('#scAllCalendarInner').removeClass('notselected');
}
function enableFreeBusy() {
	// noop?
}
function disableFreeBusy() {
	$('#fbRadio').attr('checked', '');
	$('#scFreeBusyInner').addClass('notselected');
}
function enableAllCalendar() {
	$('#filters').attr('disabled', '');
	$('#filterPropertyName').attr('disabled', '');
	$('.classFilterCheckbox').attr('disabled', '');
}
function disableAllCalendar() {
	$('#acRadio').attr('checked', '');
	$('#scAllCalendarInner').addClass('notselected');
	
	$('#ip').attr('checked', '');
	$('#filters').attr('disabled', 'disabled');
	$('#filterPropertyName').attr('disabled', 'disabled');
	$('.classFilterCheckbox').attr('disabled', 'disabled');
}

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
				alert("Your date range is greater than 180 days, which is the maximum allowed. It's been reset to " + y + ".");
			}
			$('#dateRange').text('').fadeOut();
			if(x != 0 || y != 0) {
				$('#dateRange').text('/dr(' + x + ',' + y + ')').fadeIn();
			}
		}
	}
	$('#sharelinktag').attr('href', '${baseShareUrl}' + $('#dateRange').text() + $('#icsSuffix').text() + $('#queryParameters').text());
};