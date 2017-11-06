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
    <a class="button app big" href="${ui.pageLink("sync2", "loadSync2Config")}"
       id="sync.configuration">
        <i class="icon-calendar"></i>
        ${ ui.message("sync2.configuration.label") }
    </a>
</div>

<% } %>