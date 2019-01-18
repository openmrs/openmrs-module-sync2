<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>
<openmrs:require anyPrivilege="Sync2 Audit Privilege" otherwise="/login.htm" redirect="/module/sync2/auditList.form"/>
<openmrs:htmlInclude file="/moduleResources/sync2/styles/sync2.css"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/sync2.audit.retry.js"/>
<openmrs:htmlInclude file="/moduleResources/sync2/scripts/sync2.conflict.js"/>
<spring:htmlEscape defaultHtmlEscape="true"/>

<%@ include file="template/alertMessage.jsp" %>

<style>
.label {
	width: 20%;
}
</style>
<script type="text/javascript">
	var OPENMRS_CONTEXT_PATH = "${pageContext.request.contextPath}";
	var AUDIT_DETAILS_BACK_PAGE = OPENMRS_CONTEXT_PATH + "/module/sync2/auditDetails.form";
	var AUDIT_BACK_PAGE_INDEX = <%= request.getParameter("backPageIndex") %>;
	var RETRY_URI = OPENMRS_CONTEXT_PATH + "/module/sync2/retry.form"
</script>
<fieldset>
<c:choose>
	<c:when test="${auditLog != null}">
		<table>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.details.creatorInstanceId' /></th>
				<td>${ auditLog.creatorInstanceId }</td>
			</tr>
			<span id="retryLogUuid" hidden>${auditLog.uuid}</span>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.resource' /></th>
				<td>${ auditLog.resourceName }</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.timestamp' /></th>
				<td>${ auditLog.timestamp }</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.parentUrl' /></th>
				<td>${ auditLog.parentUrl }</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.localUrl' /></th>
				<td>${ auditLog.localUrl }</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.usedUrl' /></th>
				<td>${ auditLog.usedResourceUrl }</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.availableResourceUrls' /></th>
				<td>
					<textarea rows="4" style="width:100%; color: #999999; background-color: #eeeeee;" readonly>
					${ auditLog.availableResourceUrls }</textarea>
				</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.status' /></th>
				<td>
					<c:choose>
						<c:when test="${auditLog.success}">
							<spring:message code='sync2.log.header.details.status.success' />
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${auditLog.mergeConflictUuid == null}">
									<spring:message code='sync2.log.header.details.status.failure' />
								</c:when>
								<c:otherwise>
									<spring:message code='sync2.log.header.details.status.conflict' />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.operation' /></th>
				<td>${ auditLog.operation }</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.action' /></th>
				<td>${ auditLog.action }</td>
			</tr>
			<tr>
				<th class="label"><spring:message code='sync2.log.header.message' /></th>
				<td>
					<textarea rows="1" style="width:100%; color: #999999; background-color: #eeeeee;" readonly>${ auditLog.details }</textarea>
				</td>
			</tr>
		</table>
		</fieldset>
		<fieldset>
		<c:if test="${auditLog != null && !auditLog.success}">
			<c:choose>
				<c:when test="${auditLog.nextMessageUuid != null}">
					<a class="button right" href="${pageContext.request.contextPath}/module/sync2/auditDetails.form?
						messageUuid=${auditLog.nextMessageUuid}
						&backPageIndex=<%= request.getParameter("backPageIndex") %>">
						<spring:message code='sync2.log.header.nextMessage' />
					</a>
				</c:when>
				<c:otherwise>
					<c:if test="${localInstanceId.equals(auditLog.creatorInstanceId)}">
						<a class="button confirm right" onClick="retry();">
							<spring:message code='sync2.log.header.retry' />
						</a>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:if>

		<c:if test="${auditLog.mergeConflictUuid != null && auditLog.nextMessageUuid == null}">
			<a class="button confirm right" onClick="conflictResolution();">
				<spring:message code='sync2.conflict.resolution.title' />
			</a>
		</c:if>
</c:when>
	<c:otherwise>
		<tr>
			<th><spring:message code='sync2.log.header.details.messageNotFound' /></th>
		</tr>
	</c:otherwise>
</c:choose>
<a class="button cancel"
	href="${pageContext.request.contextPath}/module/sync2/auditList.form?backPageIndex=<%= request.getParameter("backPageIndex") %>">
	<spring:message code='general.cancel' />
</a>
</fieldset>
