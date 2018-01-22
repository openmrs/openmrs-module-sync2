package org.openmrs.module.sync2.api.model.audit;

import java.util.List;

public class AuditMessageList {

    private final Long itemsCount;

    private final Integer page;

    private final Integer pageSize;

    private final List<AuditMessage> data;

    public AuditMessageList(Long itemsCount, Integer page, Integer pageSize, List<AuditMessage> data) {
        this.itemsCount = itemsCount;
        this.page = page;
        this.pageSize = pageSize;
        this.data = data;
    }

    public Long getItemsCount() {
        return itemsCount;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<AuditMessage> getData() {
        return data;
    }
}
