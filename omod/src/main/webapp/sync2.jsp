<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/sync2.css"/>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm" redirect="/module/sync2/auditList.page"/>

<div id="apps">
    <a class="button app big"
       href=""
       id="sync.configuration">
        <i class="icon-calendar"></i>
        <spring:message code="sync2.configuration.label"/>
    </a>
    <a class="button app big"
        href="${pageContext.request.contextPath}/module/sync2/manualPull.form"
        title = ""
        id="sync.pull">
        <i class="icon-random"></i>
        <spring:message code="sync2.sync.manual.pull.label"/>
    </a>
    <a class="button app big"
        href="${pageContext.request.contextPath}/module/sync2/manualPush.form"
        title = ""
        id="sync.push">
        <i class="icon-random"></i>
        <spring:message code="sync2.sync.manual.push.label"/>
    </a>
    <a class="button app big"
       href=""
       id="outgoing-message-exceptions.ccd">
        <i class="icon-calendar"></i>
        <spring:message code="sync2.audit.list.label"/>
    </a>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
