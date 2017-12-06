<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("sync2.configuration.label") ])
    if (context.hasPrivilege("Load Sync2 config")) {
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("sync2.label")}" }
    ];
</script>

<div id="apps">
    <a class="button app big" href="${ui.pageLink("sync2", "LoadSyncConfig")}"
       id="sync.configuration">
        <i class="icon-calendar"></i>
        ${ ui.message("sync2.configuration.label") }
    </a>
    <a class="button app big" href="${ui.pageLink("sync2", "ManualSync")}"
       id="sync.sync">
        <i class="icon-random"></i>
        ${ ui.message("sync2.sync.manual.label") }
    </a>
    <a class="button app big" href="${ ui.pageLink("sync2", "auditList") }"
       id="outgoing-message-exceptions.ccd">
        <i class="icon-calendar"></i>
        ${ ui.message("sync2.audit.list.label") }
    </a>
</div>

<% } %>