function conflictResolution() {

    jq.getJSON('/' + OPENMRS_CONTEXT_PATH + '/sync2/AuditDetails/conflictResolution.action',
    {
        conflictLogUuid:$('#retryLogUuid').text()
    }).success(function(data) {
        window.location.replace('/' + OPENMRS_CONTEXT_PATH + data['url'])
    });
}

function setCustomValue(key, customValue) {
    document.getElementById(key).value = customValue.value;
}

function setValueOfKey(obj, current, pathKey, pathValue) {
    for(var key in obj) {
        var value = obj[key];
        var newKey = (current ? current + "." + key : key);
        if(value && typeof value === "object") {
            setValueOfKey(value, newKey, pathKey, pathValue);
        } else {
            if(newKey == pathKey) {
                obj[key] = pathValue;
            }
        }
    }
    return obj;
}

function dotNotate(obj, current, dotNotatedObj) {
    for(var key in obj) {
        var value = obj[key];
        var newKey = (current ? current + "." + key : key);
        if(value && typeof value === "object") {
            dotNotate(value, newKey, dotNotatedObj);
        } else {
            dotNotatedObj[newKey] = value;
        }
    }
    return dotNotatedObj;
}

function compareObj(localObj, foreignObj, objectMergeTableId) {
    for(var i in foreignObj) {
        if(typeof foreignObj[i] === 'object') {
            compareObj (localObj[i], foreignObj[i]);
        } else {
            if(foreignObj[i] !== localObj[i]) {
                appendFieldChoice(i, localObj[i], foreignObj[i], objectMergeTableId);
            }
        }
    }
};

function appendFieldChoice(key, value1, value2, objectMergeTableId) {
    var fieldChoice =
        `<tr>
            <td>
                <legend><b>${key}</b></legend>
            </td>
            <td>
                <input type='radio' value=${value1} name=${key} required>${value1}
            </td>
            <td>
                <input type='radio' value=${value2} name=${key}>${value2}
            </td>
            <td>
                <input type='radio' id=${key} name=${key} value="">
                <input type='text' onchange="setCustomValue(\'${key}\', this);">
            </td>
        </tr>`;
    jQuery("#" + objectMergeTableId).append(fieldChoice);
}
