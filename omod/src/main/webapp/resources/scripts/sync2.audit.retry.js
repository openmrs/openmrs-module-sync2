function retry() {

	var url = unifyUri(RETRY_URI) + "?retryLogUuid=" + jQuery('#retryLogUuid').text()

	 if (AUDIT_BACK_PAGE_INDEX !== 'undefined') {
		url = url + "&backPageIndex=" + AUDIT_BACK_PAGE_INDEX;
	}

    window.location.replace(url);
}

function unifyUri(input) {
	var uri = input;
	var character = uri.charAt(0)
	if (character != '/') {
		uri = '/' + uri;
	}
	return uri;
}
