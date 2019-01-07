<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm" redirect="/module/sync2/auditList.form"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/jquery-1.12.4.min.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/sync2.audit.controller.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/jsGrid.min.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/jsGrid.css"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/theme.css"/>
<spring:htmlEscape defaultHtmlEscape="true"/>

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
        { id: "ALL", name: "<spring:message code='sync2.log.resource.all' />" },
        { id: "patient", name: "<spring:message code='sync2.log.resource.patient' />" },
        { id: "location", name: "<spring:message code='sync2.log.resource.location' />" },
        { id: "encounter", name: "<spring:message code='Encounter.title' />" },
        { id: "visit", name: "<spring:message code='Encounter.visit' />" },
        { id: "observation", name: "<spring:message code='Obs.title' />" },
        { id: "privilege", name: "<spring:message code='sync2.log.resource.privilege' />" },
        { id: "audit_message", name: "<spring:message code='sync2.log.resource.auditMessage' />" },
        { id: "person", name: "<spring:message code='sync2.log.resource.person' />" },
        { id: "allergy", name: "<spring:message code='sync2.log.resource.allergy' />" },
        { id: "form", name: "<spring:message code='sync2.log.resource.form' />" },
        { id: "cohort", name: "<spring:message code='sync2.log.resource.cohort' />" },
        { id: "drug_order", name: "<spring:message code='sync2.log.resource.drug_order' />" },
        { id: "test_order", name: "<spring:message code='sync2.log.resource.test_order' />" }
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
</script>
<fieldset>
<c:choose>
    <c:when test="${configurationValidationErrors.hasErrors()}">
        <h2><spring:message code='sync2.error.validationError' /></h2>
        <c:forEach var="it" items="${configurationValidationErrors.getErrorsCodes()}">
            <div style="margin-left: 15px"><spring:message code='${it}' /></div>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div id="jsGrid" class="jsgrid" style="position: relative; height: auto; width: 100%;"></div>
    </c:otherwise>
</c:choose>
</fieldset>
