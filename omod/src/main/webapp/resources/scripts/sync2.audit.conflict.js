function conflictResolution() {

    jq.getJSON('/' + OPENMRS_CONTEXT_PATH + '/sync2/AuditDetails/conflictResolution.action',
    {
        conflictLogUuid:$('#retryLogUuid').text()
    }).success(function(data) {
        window.location.replace('/' + OPENMRS_CONTEXT_PATH + data['url'])
    });
}
