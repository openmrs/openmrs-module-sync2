<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/sync2.css"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/sync2.js"/>
<spring:htmlEscape defaultHtmlEscape="true"/>

<h2>
    <h2><spring:message code="sync2.configuration.label" /></h2>
</h2>
<div id="message">
    <c:if test="${not empty alertMessage}">
        <span <c:if test="${success == true}">class="success-msg"</c:if>
                <c:if test="${success == false}">class="failure-msg"</c:if>>
            <spring:message code="${alertMessage}"/>
        </span>
        <script>
            jQuery('#message').fadeOut(5000);
        </script>
    </c:if>
</div>
<fieldset>
    <form action="importSyncConfiguration.form" method="POST" enctype="multipart/form-data">
        <p>
            <label for="json-file">
                <span>
                    <spring:message code="sync2.configuration.import.file.label"/>
                </span>
            </label>
            <br/><br/>
            <input id="json-file" type="file" name="file" accept=".json"/>
        </p>
        <input type="submit" id="import-button" class="confirm right" value="<spring:message code='sync2.configuration.import.label'/>" disabled="disabled"/>
    </form>
</fieldset>
<br/>
<fieldset>
    <form method="POST" action="${pageContext.request.contextPath}/module/sync2/saveConfiguration.form">
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
            <br/>
            <textarea id="json-field" class="required" name="json" rows="15" cols="80">${configuration}</textarea>
        </p>
        <input type="button" class="cancel" value="<spring:message code='general.cancel'/>" onclick="javascript:window.location='${pageContext.request.contextPath}/module/sync2/sync2.form'" />
        <input type="submit" class="confirm right" id="save-button" value="<spring:message code='general.save'/>" />
    </form>
</fieldset>
