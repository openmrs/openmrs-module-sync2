<%
    def messagesPrefix = "sync2.log.header"
    def detailViewProvider = "sync2"
    ui.includeJavascript("sync2", "sync2.audit.retry.js")
%>

<style>
.label {
    width: 20%;
}
</style>

<table>
    <% if (auditLog != null) { %>
    <span id="retryLogUuid" hidden>${auditLog.uuid}</span>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".resource") }</th>
        <td>${ auditLog.resourceName }</td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".timestamp") }</th>
        <td>${ auditLog.timestamp }</td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".parentUrl") }</th>
        <td>${ auditLog.parentUrl }</td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".localUrl") }</th>
        <td>${ auditLog.localUrl }</td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".usedUrl") }</th>
        <td>${ auditLog.usedResourceUrl }</td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".availableResourceUrls") }</th>
        <td>
            <textarea rows="4" style="width:100%; color: #999999; background-color: #eeeeee;" readonly>${ auditLog.availableResourceUrls }</textarea>
        </td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".status") }</th>
        <td>
            <%=
            auditLog.success
                ? ui.message(messagesPrefix + ".details.status.success")
                : ui.message(messagesPrefix + ".details.status.failure")
            %>
        </td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".operation") }</th>
        <td>${ auditLog.operation }</td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".action") }</th>
        <td>${ auditLog.action }</td>
    </tr>
    <tr>
        <th class="label">${ ui.message(messagesPrefix + ".message") }</th>
        <td>
            <textarea rows="1" style="width:100%; color: #999999; background-color: #eeeeee;" readonly>${ auditLog.details }</textarea>
        </td>
    </tr>

    <% } else { %>
    <tr>
        <th>${ ui.message(messagesPrefix + '.details.messageNotFound') }</th>
    </tr>
    <% } %>
</table>

<% if (auditLog != null && !auditLog.success) { %>
    <br />
    <% if (auditLog.nextMessageUuid != null) { %>
        <a class="button right" href="${ ui.pageLink(detailViewProvider, "details",
            [messageUuid: auditLog.nextMessageUuid, backPage: param.backPage[0], backPageIndex: param.backPageIndex]) }">
            <i class="icon-chevron-right"></i>
            ${ ui.message(messagesPrefix + '.nextMessage') }
        </a>
    <% } else {  %>
        <a class="button confirm right" onClick="retry();">
            <i class="icon-retweet"></i>
            ${ ui.message(messagesPrefix + '.retry') }
        </a>
    <% } %>
<% } %>