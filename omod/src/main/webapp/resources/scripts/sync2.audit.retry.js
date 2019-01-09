function retry() {

	var contextPath = OPENMRS_CONTEXT_PATH;
	var character = contextPath.charAt(0)
	if (character != '/') {
		contextPath = '/' + contextPath;
	}

    jQuery.getJSON(contextPath + '/sync2/AuditDetails/retry.action',
    {
        retryLogUuid:jQuery('#retryLogUuid').text()
    }).success(function(data) {
        window.location.replace(contextPath + data['url']);
    });
}
