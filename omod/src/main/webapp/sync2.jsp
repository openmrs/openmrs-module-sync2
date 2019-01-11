<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/sync2.css"/>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm" redirect="/module/sync2/auditList.page"/>

<h2>
    <h2><spring:message code="sync2.title" /></h2>
</h2>
<br/><br/>
<%@ include file="template/alertMessage.jsp" %>

<div id="apps">
    <a class="button big"
        href="${pageContext.request.contextPath}/module/sync2/configuration.form">
        <spring:message code="sync2.configuration.label"/>
    </a>
    <a class="button big ${emptyURI ? 'disabled' : ''}"
        <c:if test = "${emptyURI == true}">
            href = "#"
            title = "<spring:message code='sync2.configuration.parentUrl.empty'/>"
        </c:if>
         <c:if test = "${emptyURI == false}">
             href = "${pageContext.request.contextPath}/module/sync2/manualPull.form"
         </c:if>>
        <spring:message code="sync2.sync.manual.pull.label"/>
    </a>
    <a class="button big ${emptyURI ? 'disabled' : ''}"
        <c:if test = "${emptyURI == true}">
            href = "#"
            title = "<spring:message code='sync2.configuration.parentUrl.empty'/>"
        </c:if>
         <c:if test = "${emptyURI == false}">
             href="${pageContext.request.contextPath}/module/sync2/manualPush.form"
         </c:if>>
        <spring:message code="sync2.sync.manual.push.label"/>
    </a>
    <a class="button big"
        href="${pageContext.request.contextPath}/module/sync2/auditList.form">
        <spring:message code="sync2.audit.list.label"/>
    </a>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
