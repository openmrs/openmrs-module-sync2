var jq = jQuery;
var timeout;

jq(function() {
    jq('.required').keyup(function() {
        if(timeout){
            clearTimeout(timeout);
        }
        timeout = setTimeout(function() {
            validate();
        }, 300);
    });

    validate();
});

jq(document).ajaxError(function() {
    jq('#server-error-msg').show();
});

jq(document).on('change','#json-file' , function() {
    jq('#import-button').removeAttr('disabled');
});

function requireValues() {
    if(jq.trim(jq('#json-field').val()) != '') {
        return true;
    }
    return false;
}

function toggleFields(isJsonValid) {
    if (isJsonValid && requireValues()) {
        jq('#save-button').removeAttr('disabled');
        jq('#json-field').css("color", "black");
        jq('#errorMsg').hide();
    } else {
        jq('#save-button').attr('disabled','disabled');
        jq('#json-field').css("color", "red");
        jq('#errorMsg').show();
    }
}

function validate() {
    var json = jq('#json-field').val();
    if(jq.trim(json) == '') {
        toggleFields(false);
        return;
    }

    jq.post("verifyJson.htm",
        {"json": json},
        function(data) {
            jq('#server-error-msg').hide();
            toggleFields(data.isValid);
        },
        "json"
    );
}
