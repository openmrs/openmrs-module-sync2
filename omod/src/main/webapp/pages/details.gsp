
<%
    ui.decorateWith("appui", "standardEmrPage")
    def artifactId = "sync2"
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("sync2.label")}",
                    link: "${ui.pageLink("sync2", "sync2")}"
                },
        { label: "${ ui.message(artifactId + '.auditList.label') }",
            link: "${ ui.pageLink(artifactId, 'auditList', [pageIndex: param.backPageIndex]) }"
        },
        { label: "${ ui.message(artifactId + '.details.label') }" }
    ];
</script>

<div id="apps">
    ${ ui.includeFragment("sync2", "auditDetails", [messageUuid: param.messageUuid, pageIndex: param.backPageIndex]) }
    <a class="button cancel" href="${ ui.pageLink(artifactId, 'auditList', [pageIndex: param.backPageIndex]) }">
        ${ ui.message("general.cancel") }
    </a>
</div>
