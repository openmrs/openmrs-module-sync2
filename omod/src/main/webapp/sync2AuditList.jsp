<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm" redirect="/module/sync2/auditList.form"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/jquery-1.12.4.min.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/sync2.audit.controller.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/jsGrid.min.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/jsGrid.css"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/theme.css"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/sync2.css"/>
<spring:htmlEscape defaultHtmlEscape="true"/>

<%@ include file="template/alertMessage.jsp" %>

<script type="text/javascript">
    var titles = [
        "<spring:message code='sync2.log.header.creatorInstanceId' />",
        "<spring:message code='sync2.log.header.resource' />",
        "<spring:message code='sync2.log.header.timestamp' />",
        "<spring:message code='sync2.log.header.url' />",
        "<spring:message code='sync2.log.header.status' />",
        "<spring:message code='sync2.log.header.operation' />"
    ];

    var originName = [
        { id: "${localInstanceId}", name: "<spring:message code='sync2.log.origin.thisInstance' />" },
        { id: "ALL", name: "<spring:message code='sync2.log.resource.all' />" },
        <c:forEach var="it" items="${creatorIds}">
            { id: "${it}", name: "${it}"},
        </c:forEach>
    ];

    var syncResourceName = [
        <c:forEach var="it" items="${resourcesInfo}">
            { id: "${it.name}", name: "<spring:message code='${it.messageKey}' />"},
        </c:forEach>
    ];

    var syncStatus = [
        { id: "ALL", name: "<spring:message code='sync2.log.status.all' />" },
        { id: "SUCCESS", name: "<spring:message code='sync2.log.status.success' />" },
        { id: "FAILURE", name: "<spring:message code='sync2.log.status.failure' />" }
    ];

    var syncOperation = [
        { id: "ALL", name: "<spring:message code='sync2.log.operation.all' />" },
        { id: "PUSH", name: "<spring:message code='sync2.log.operation.push' />" },
        { id: "PULL", name: "<spring:message code='sync2.log.operation.pull' />" }
    ];

    var pageIndex = ${pageIndex};

    var auditDetailsUrl = "${pageContext.request.contextPath}/module/sync2/auditDetails.form";
</script>
<fieldset>
<c:choose>
    <c:when test="${configurationValidationErrors.hasErrors()}">
        <h2><spring:message code='sync2.error.validationError' /></h2>
        <c:forEach var="it" items="${configurationValidationErrors.getErrorsCodes()}">
            <div style="margin-left: 15px" class="field-error"><spring:message code='${it}' /></div>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div id="jsGrid" class="jsgrid" style="position: relative; height: auto; width: 100%;"></div>
    </c:otherwise>
</c:choose>
</fieldset>
