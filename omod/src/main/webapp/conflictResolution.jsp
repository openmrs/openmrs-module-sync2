<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/conflictResolution.css"/>
<openmrs:htmlInclude file="/moduleResources/fhir/jquery.json-viewer.css"/>
<openmrs:htmlInclude file="/moduleResources/fhir/jquery.json-viewer.js"/>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm" redirect="/module/sync2/auditList.page"/>

<p>Resolution of conflict id: <b>${conflictUuid}</b></p>
<p>Conflicted objects of class: <b>${className}</b></p>
<br/><br/>

<h3>Conflicted object properties:</h3>
<p><i>Choose one option for each field and click 'Apply changes'</i></p>
<form id="objectMergeForm">
    <table id="objectMergeTable">
        <tr>
            <th>Field</th>
            <th>Local object</th>
            <th>Foreign object</th>
            <th>Input new value</th>
        </tr>
    </table>
    <input type="submit" class="applyButton" value="Apply changes"/>
</form>

<br/><br/><br/>

<p><i>If you need more details, below you can see whole objects</i></p>
<h3>Conflicted objects:</h3>
<table id="objectComparison">
    <tr>
        <th>Local object</th>
        <th>Foreign object</th>
    </tr>
    <tr>
        <td>
            <div id="localObjJson"></div>
        </td>
        <td>
            <div id="foreignObjJson"></div>
        </td>
    </tr>
</table>

<script type="text/javascript">

    window.addEventListener("load", function() {

        var localObjJson = ${localObjJson};
        var foreignObjJson = ${foreignObjJson};

        jQuery("#localObjJson").jsonViewer(localObjJson);
        jQuery("#foreignObjJson").jsonViewer(foreignObjJson);

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

        var localObj = dotNotate(localObjJson, null, {});
        var foreignObj = dotNotate(foreignObjJson, null, {});

        var localObjConflictedProps;

        var compareObj = function(localObj, foreignObj) {
            for(var i in foreignObj) {
                if(typeof foreignObj[i] === 'object') {
                    compareObj (localObj[i], foreignObj[i]);
                } else {
                    if(foreignObj[i] !== localObj[i]) {
                        appendFieldChoice(i, localObj[i], foreignObj[i]);
                    }
                }
            }
        };

        function appendFieldChoice(key, value1, value2) {
            var fieldChoice =
                `<tr>
                    <td>
                        <legend><b>\${key}</b></legend>
                    </td>
                    <td>
                        <input type='radio' value=\${value1} name=\${key} required>\${value1}
                    </td>
                    <td>
                        <input type='radio' value=\${value2} name=\${key}>\${value2}
                    </td>
                    <td>
                        <input type='radio' name=\${key}>
                        <input type='text'>
                    </td>
                </tr>`;

            jQuery("#objectMergeTable").append(fieldChoice);
        }

        compareObj(localObj, foreignObj);

        function setObjProperty(obj, path, value) {
            path = (typeof path === "string") ? path.split(".") : path;
            const key = path.shift();

            if (path.length === 0)
            {
                obj[key] = value;
                return;
            }

            setObjProperty(obj, path, value);
        }

        var form = document.getElementById('objectMergeForm');
        form.addEventListener("submit", function(e) {
            e.preventDefault();

            for(var i = 0; i < form.elements.length; i++) {
                console.log(form.elements.item(i).name);
                console.log(form.elements.item(i).value);
                setObjProperty(localObjJson, form.elements.item(i).name, form.elements.item(i).value)
                console.log(localObjJson);
            };
        })
    });

</script>
