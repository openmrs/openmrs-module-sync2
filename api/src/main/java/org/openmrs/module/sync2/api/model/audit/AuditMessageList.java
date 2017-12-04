package org.openmrs.module.sync2.api.model.audit;

import com.google.gson.annotations.Expose;

import java.util.List;

public class AuditMessageList {

    @Expose
    private final Long itemsCount;

    @Expose
    private final Integer page;

    @Expose
    private final Integer pageSize;

    @Expose
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
