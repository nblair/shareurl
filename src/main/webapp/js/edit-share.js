
function applySubmitHandlerIfPresent(element, url, indicator, callback) {
	var el = $(element);
	if(el) {
		el.unbind('submit');
		el.submit(function(event) {
			event.preventDefault();
			postAndRenderPreferences(url, element, indicator, callback);
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
		if(!$.isEmptyObject(share.sharePreferences.classificationFilters)) {
			$.each(share.sharePreferences.classificationFilters, function(i, obj) {
				if(obj == 'PUBLIC') {
					 $('#publicClass').attr('checked', 'checked');
				} else if (obj == 'CONFIDENTIAL') {
					$('#confidClass').attr('checked', 'checked');
				} else if (obj == 'PRIVATE') {
					$('#privateClass').attr('checked', 'checked');
				}
			});
		} else {
			$('#publicClass').attr('checked', 'checked');
			$('#confidClass').attr('checked', 'checked');
			$('#privateClass').attr('checked', 'checked');
		}
	}
}
function renderFilterPreferences(share, revokeIconPath, formAction, displayEmptySet) {
	$('#contentFilters').empty();
	if(!share.freeBusyOnly) {
		if(!$.isEmptyObject(share.sharePreferences.propertyMatchPreferences)) {
			$.each(share.sharePreferences.propertyMatchPreferences, function(i, obj) {
				$('<li><span class="removable">' + obj.displayName + '</span>&nbsp;<form class="removeContentFilter inlineblock" action="' + formAction + '" method="post"><fieldset><input type="hidden" name="propertyName" value="' + obj.key + '"/><input type="hidden" name="propertyValue" value="' + obj.value + '"/></fieldset><img src="' + revokeIconPath + '" title="Remove this filter" class="revokeHandle"/></form></li>').appendTo('#contentFilters');
			});
		} else if (displayEmptySet){
			$('<li>No filters: all events returned.</li>').appendTo('#contentFilters');
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

function renderShareUrlExample(baseShareUrl) {
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
	$('#sharelinktag').attr('href', baseShareUrl + $('#dateRange').text() + $('#icsSuffix').text() + $('#queryParameters').text());
};