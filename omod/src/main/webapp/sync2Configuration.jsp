<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin">
            <spring:message code="admin.title.short"/>
        </a>
    </li>
    <li class="second">
        <a href="${pageContext.request.contextPath}/module/sync2/sync2.form">
            <spring:message code="sync2.title"/>
        </a>
    </li>
    <li class="active">
        <a href="${pageContext.request.contextPath}/module/sync2/sync2Configuration.form">
            <spring:message code="sync2.configuration.label"/>
        </a>
    </li>
</ul>
<h2>
    <h2><spring:message code="sync2.configuration.label" /></h2>
</h2>

<form action="importSyncConfiguration.form" method="POST" enctype="multipart/form-data">
    <span >
        ${importStatus}
    </span>
    <p>
        <label for="json-file">
            <span>
                <spring:message code="sync2.configuration.import.file.label"/>
            </span>
        </label>
        <input id="json-file" type="file" name="file"/>
    </p>
    <input type="submit" id="import-button" class="confirm right" value="<spring:message code='sync2.configuration.import.label'/>" disabled="disabled"/>
</form>

<form class="simple-form-ui" method="POST" action="${pageContext.request.contextPath}/module/sync2/saveConfiguration.form">
    <span id="errorMsg" class="field-error" style="display: none">
        <spring:message code="sync2.configuration.errors.invalidJson"/>
    </span>
    <span id="server-error-msg" class="field-error" style="display: none">
        <spring:message code="sync2.configuration.errors.serverError"/>
    </span>
    <p>
        <label for="json-field">
            <span class="title">
                <spring:message code="sync2.configuration.json.label"/> (<spring:message code="emr.formValidation.messages.requiredField.label"/>)
            </span>
        </label>
        <textarea id="json-field" class="required" name="json" rows="15" cols="80">${configuration}</textarea>
    </p>

    <input type="button" class="cancel" value="<spring:message code='general.cancel'/>" onclick="javascript:window.location='/${ contextPath }/sync2/sync2'" />
    <input type="submit" class="confirm right" id="save-button" value="<spring:message code='general.save'/>" />
</form>
