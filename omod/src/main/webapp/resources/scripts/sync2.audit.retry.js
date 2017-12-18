function retry() {

    jq.getJSON('/' + OPENMRS_CONTEXT_PATH + '/sync2/AuditDetails/retry.action',
    {
        retryLogId:$('#retryLogId').text()
    }).success(function(data) {
        window.location.replace('/' + OPENMRS_CONTEXT_PATH + data['url']);
    });
}