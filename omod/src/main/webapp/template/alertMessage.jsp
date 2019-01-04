<openmrs:htmlInclude file="/moduleResources/sync2/styles/alertMessage.css"/>
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
