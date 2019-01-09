function conflictResolution() {

	var contextPath = OPENMRS_CONTEXT_PATH;
	var character = contextPath.charAt(0)
	if (character != '/') {
		contextPath = '/' + contextPath;
	}

    jQuery.getJSON( contextPath + '/sync2/AuditDetails/conflictResolution.action',
    {
        conflictLogUuid:jQuery('#retryLogUuid').text()
    }).success(function(data) {
        window.location.replace( contextPath + data['url'])
    });
}

function setValueOfRadioButton(key, customValue) {
    document.getElementById(key).value = customValue.value;
}

function changeValueOfJsonKey(obj, current, pathKey, pathValue) {
    for(var key in obj) {
        var value = obj[key];
        var newKey = (current ? current + "." + key : key);
        if(value && typeof value === "object") {
            changeValueOfJsonKey(value, newKey, pathKey, pathValue);
        } else {
            if(newKey == pathKey) {
                obj[key] = pathValue;
            }
        }
    }
    return obj;
}

function jsonToDotNotation(obj, current, dotNotatedObj) {
    for(var key in obj) {
        var value = obj[key];
        var newKey = (current ? current + "." + key : key);
        if(value && typeof value === "object") {
            jsonToDotNotation(value, newKey, dotNotatedObj);
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
                appendFieldValueChoice(i, localObj[i], foreignObj[i], objectMergeTableId);
            }
        }
    }
};

function appendFieldValueChoice(key, value1, value2, objectMergeTableId) {
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
                <input type='text' onchange="setValueOfRadioButton(\'${key}\', this);">
            </td>
        </tr>`;
    jQuery("#" + objectMergeTableId).append(fieldChoice);
}
