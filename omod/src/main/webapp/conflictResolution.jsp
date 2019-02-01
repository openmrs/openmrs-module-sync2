<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/jquery-1.12.4.min.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/conflictResolution.css"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/sync2.conflict.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/jquery.json-viewer.css"/>
<openmrs:htmlInclude file="/moduleResources/fhir/jquery.json-viewer.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/sync2.css"/>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm"
	redirect="/module/sync2/conflictResolution.form?messageUuid=${messageUuid}" />

<a class="button cancel" onClick="history.back()">
	<spring:message code='general.cancel' />
</a>

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
        var mergedObjJson = ${localObjJson};

        jQuery("#localObjJson").jsonViewer(localObjJson);
        jQuery("#foreignObjJson").jsonViewer(foreignObjJson);

        var localObj = jsonToDotNotation(localObjJson, null, {});
        var foreignObj = jsonToDotNotation(foreignObjJson, null, {});

        compareObj(localObj, foreignObj, "objectMergeTable");
        compareObj(foreignObj, localObj, "objectMergeTable");

        var form = document.getElementById('objectMergeForm');
        form.addEventListener("submit", function(e) {
            e.preventDefault();

            for(var i = 0; i < form.elements.length; i++) {
                if(form.elements.item(i).checked) {
                    var key = form.elements.item(i).name;
                    var value = form.elements.item(i).value;
                    mergedObjJson = changeValueOfJsonKey(mergedObjJson, null, key, value);
                }
            };
            var postUrl = document.location.origin + "/openmrs/ws/rest/sync2/conflict/resolve?conflictUuid=" +
                "${conflictUuid}";

            if ("${auditBackPage}" !== "") {
                postUrl = postUrl + "&auditBackPage=${auditBackPage}&backPageIndex=${backPageIndex}";
            }

            jQuery.ajax({
                type: "POST",
                url: postUrl,
                data: JSON.stringify(mergedObjJson),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function(data) {
                    window.location.replace(document.location.origin + data['url']);
                },
                failure: function() {
                    jQuery('#server-error-msg').show();
                }
            });
        });
    });

</script>
