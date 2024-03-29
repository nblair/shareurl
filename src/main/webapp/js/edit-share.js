/*
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
function applySubmitHandlerIfPresent(element, url, indicator, callback) {
	var el = $(element);
	if(el) {
		el.unbind('submit');
		el.submit(function(event) {
			event.preventDefault();
			postAndRenderPreferences(url, element, indicator, callback);
		});
	}else{
		alert("not found");
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
	
	if(share.calendarSelect){
		$('#csRadio').attr('checked','checked');
		enableCalendarSelect();
		disableCalendarDefault();
	}else{
		$('#cdRadio').attr('checked','checked');
		enableCalendarDefault();
		disableCalendarSelect();		
	}
	
}

function renderFilterCalendarPreferences(share, revokeIconPath, formAction,calendarMap, displayEmptySet) {
	if(share.calendarSelect) {
		$('#calendarFilters').empty();
		//if calendarMatch prefs exist
		if(!$.isEmptyObject(share.sharePreferences.calendarMatchPreferences)) {
			$.each(share.sharePreferences.calendarMatchPreferences, function(i, obj) {
				$('<li><span class="removable">' + calendarMap[obj.value] + '</span>&nbsp;<form class="removeCalendarFilter inlineblock" action="' + formAction + '" method="post"><fieldset><input type="hidden" name="calendarName" value="' + obj.key + '"/><input type="hidden" name="calendarId" value="' + obj.value + '"/></fieldset><img src="' + revokeIconPath + '" title="Remove this filter" class="revokeHandle"/></form></li>').appendTo('#calendarFilters');
			});
		} else if (displayEmptySet){
			$('<li>No filters: all events returned.</li>').appendTo('#calendarFilters');
		}
		
	}
}
function renderFilterPreferences(share, revokeIconPath, formAction, displayEmptySet) {
	$('#contentFilters').empty();
	if(!share.freeBusyOnly) {
		//if propertymatchprefs exist
		if(!$.isEmptyObject(share.sharePreferences.propertyMatchPreferences)) {
			
			//iterate over propMatch prefs
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
	
	//ctcudd
	$('#csRadio').attr('checked', '');
	$('#cdRadio').attr('checked', '');
	$('#scCalDefaultInner').removeClass('notselected');
	$('#scCalSelectInner').removeClass('notselected');
}

function enableCalendarDefault(){
	//noop?
}

function disableCalendarDefault(){
	$('#cdRadio').attr('checked', '');
	$('#scCalDefaultInner').addClass('notselected');
}
function enableCalendarSelect(){
	$('#calSelectFilters').attr('disabled', '');
	$('.calSelectDDL').attr('disabled', '');
	$('.calSelectButton').attr('disabled', '');
}
function disableCalendarSelect(){
	$('#csRadio').attr('checked', '');
	$('#scCalSelectInner').addClass('notselected');
	
	$('#calSelectFilters').attr('disabled', 'disabled');
	$('.calSelectDDL').attr('disabled', 'disabled');
	$('.calSelectButton').attr('disabled', 'disabled');
}




function enableFreeBusy() {
	// noop?
	$('#ip').attr('disabled', 'disabled');
	$('#isc').attr('disabled', 'disabled');
}
function disableFreeBusy() {
	$('#fbRadio').attr('checked', '');
	$('#scFreeBusyInner').addClass('notselected');
	$('#ip').attr('disabled', '');
	$('#isc').attr('disabled', '');
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