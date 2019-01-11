<spring:htmlEscape defaultHtmlEscape="true"/>
<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin">
            <spring:message code="admin.title.short"/>
        </a>
    </li>
    <li <c:if test='<%= request.getRequestURI().contains("/sync2/sync2.") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/sync2/sync2.form">
            <spring:message code="sync2.title"/>
        </a>
    </li>
    <li <c:if test='<%= request.getRequestURI().contains("/sync2Configuration") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/sync2/configuration.form">
            <spring:message code="sync2.configuration.label"/>
        </a>
    </li>
</ul>
