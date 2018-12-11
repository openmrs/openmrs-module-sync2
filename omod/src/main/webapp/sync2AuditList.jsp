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
        <a href="${pageContext.request.contextPath}/module/sync2/sync2AuditList.form">
            <spring:message code="sync2.audit.list.label"/>
        </a>
    </li>
</ul>
<h2>
    <h2><spring:message code="sync2.audit.list.label" /></h2>
</h2>

Hello!
