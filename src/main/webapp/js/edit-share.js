function renderShareControls2(share) {
	resetPrivacyControls();
	
	if(share.freeBusyOnly) {
		enableFreeBusy();
		disableAllCalendar();
		disableFilteredCalendar();
	} else {
		disableFreeBusy();
		if(share.eventFilterCount > 0) {
			disableAllCalendar();
			enableFilteredCalendar();
		} else {
			disableFilteredCalendar();
			enableAllCalendar();
		}
	}
}
function resetPrivacyControls() {
	$('#fbRadio').attr('checked', '');
	$('#acRadio').attr('checked', '');
	$('#filterRadio').attr('checked', '');
	$('#scFreeBusy').removeClass('notselected');
	$('#scAllCalendar').removeClass('notselected');
	$('#scFilteredCalendar').removeClass('notselected');
}
function enableFreeBusy() {
	// noop?
}
function disableFreeBusy() {
	$('#fbRadio').attr('checked', '');
	$('#scFreeBusy').addClass('notselected');
}
function enableAllCalendar() {
	//noop
}
function disableAllCalendar() {
	$('#acRadio').attr('checked', '');
	$('#scAllCalendar').addClass('notselected');
}
function enableFilteredCalendar() {
	$('#addFilter').attr('disabled', '');
	$('#filterPropertyName').attr('disabled', '');
	$('.classFilterCheckbox').attr('disabled', '');
}
function disableFilteredCalendar() {
	$('#filterRadio').attr('checked', '');
	$('#scFilteredCalendar').addClass('notselected');
	
	$('#addFilter').attr('disabled', 'disabled');
	$('#filterPropertyName').attr('disabled', 'disabled');
	$('.classFilterCheckbox').attr('disabled', 'disabled');
}