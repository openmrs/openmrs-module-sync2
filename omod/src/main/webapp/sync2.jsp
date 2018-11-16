<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp" %>

<p>Hello ${user.systemId}!</p>

<a href="${pageContext.request.contextPath}/module/sync2/conflictResolution.form">
    <spring:message code="sync2.conflict.resolution.title"/>
</a>

<%@ include file="/WEB-INF/template/footer.jsp"%>
