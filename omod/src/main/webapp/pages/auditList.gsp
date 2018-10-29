<%
    ui.decorateWith("appui", "standardEmrPage")
    if (context.hasPrivilege("Sync2 Audit Privilege")) {
%>
<% ui.includeJavascript("sync2", "sync2.audit.controller.js") %>
<% ui.includeJavascript("sync2", "jsGrid.min.js") %>

<link href="/${ ui.contextPath() }/ms/uiframework/resource/sync2/styles/jsGrid.css"  rel="stylesheet" type="text/css" />
<link href="/${ ui.contextPath() }/ms/uiframework/resource/sync2/styles/theme.css"  rel="stylesheet" type="text/css" />
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        {
            label: "${ ui.message("sync2.label") }",
            link: "${ ui.pageLink("sync2", "sync2") }"
        },
        { label: "${ ui.message('sync2.audit.label') }" }
    ];
    var titles = [
        "${ ui.message('sync2.log.header.creatorInstanceId') }",
        "${ ui.message('sync2.log.header.resource') }",
        "${ ui.message('sync2.log.header.timestamp') }",
        "${ ui.message('sync2.log.header.url') }",
        "${ ui.message('sync2.log.header.status') }",
        "${ ui.message('sync2.log.header.operation') }"
    ];

    var originName = [
        { id: "<%= localInstanceId %>", name: "${ ui.message('sync2.log.origin.thisInstance') }" },
        { id: "ALL", name: "${ ui.message('sync2.log.resource.all') }" },
        <% creatorIds.each { %>
            { id: "<%= it %>", name: "<%= it %>"},
        <% } %>
    ];

    var syncResourceName = [
        { id: "ALL", name: "${ ui.message('sync2.log.resource.all') }" },
        { id: "patient", name: "${ ui.message('sync2.log.resource.patient') }" },
        { id: "location", name: "${ ui.message('sync2.log.resource.location') }" },
        { id: "encounter", name: "${ ui.message('Encounter.title') }" },
        { id: "visit", name: "${ ui.message('Encounter.visit') }" },
        { id: "observation", name: "${ ui.message('Obs.title') }" },
        { id: "privilege", name: "${ ui.message('sync2.log.resource.privilege') }" },
        { id: "audit_message", name: "${ ui.message('sync2.log.resource.auditMessage') }" },
        { id: "person", name: "${ ui.message('sync2.log.resource.person') }" }
    ];

    var syncStatus = [
        { id: "ALL", name: "${ ui.message('sync2.log.status.all') }" },
        { id: "SUCCESS", name: "${ ui.message('sync2.log.status.success') }" },
        { id: "FAILURE", name: "${ ui.message('sync2.log.status.failure') }" }
    ];

    var syncOperation = [
        { id: "ALL", name: "${ ui.message('sync2.log.operation.all') }" },
        { id: "PUSH", name: "${ ui.message('sync2.log.operation.push') }" },
        { id: "PULL", name: "${ ui.message('sync2.log.operation.pull') }" }
    ];
</script>
<% if (configurationValidationErrors.hasErrors()) { %>
    <h2><%= ui.message("sync2.error.validationError") %></h2>
    <% configurationValidationErrors.errorCodes.each { %>
        <div style="margin-left: 15px"><%= ui.message(it) %></div>
    <% } %>
<% } else { %>
    <div id="jsGrid" class="jsgrid" style="position: relative; height: auto; width: 100%;"></div>
<% } %>


<% } %>
