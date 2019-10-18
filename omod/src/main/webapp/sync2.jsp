<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/sync2.css"/>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm" redirect="/module/sync2/sync2.form"/>

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
    <a class="button big ${emptyURI || pushtoogle == false ? 'disabled' : ''}"
        <c:if test = "${emptyURI == true}">
            href = "#"
            title = "<spring:message code='sync2.configuration.parentUrl.empty'/>"
        </c:if>
        <c:if test = "${pushtoogle == false}">
            href = "#"
            title = "<spring:message code='sync2.globalProperty.parentpush.false'/>"
        </c:if>
         <c:if test = "${emptyURI && !pushtoogle == false}">
             href = "${pageContext.request.contextPath}/module/sync2/manualPull.form"
         </c:if>>
        <spring:message code="sync2.sync.manual.pull.label"/>
    </a>
    <a class="button big ${emptyURI || pulltoogle == false ? 'disabled' : ''}"
        <c:if test = "${emptyURI == true}">
            href = "#"
            title = "<spring:message code='sync2.configuration.parentUrl.empty'/>"
        </c:if>
        <c:if test = "${pulltoogle == false}">
            href = "#"
            title = "<spring:message code='sync2.globalProperty.parentpull.false'/>"
        </c:if>
         <c:if test = "${emptyURI && !pulltoogle == false}">
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
