package org.openmrs.module.sync2.web.controller.rest;

import com.google.gson.JsonParseException;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller("${artifactid}.SyncAuditRestController")
@RequestMapping(value = "/rest/sync2", produces = MediaType.APPLICATION_JSON_VALUE)
public class SyncAuditRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(SyncAuditRestController.class);

    @Autowired
    SyncAuditService syncAuditService;

    @RequestMapping(value = "/messages", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public AuditMessage getMessageById(@RequestParam(value = "id", required = true)  Integer id) {
        LOGGER.debug("Get single message with " + id + "id");
        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            LOGGER.debug("Get Single message reached by message id");
            return syncAuditService.getMessageById(id);
        }
        return null;
    }

    @RequestMapping(value = "/messages/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getJsonMessageById(@PathVariable Integer id) throws JsonParseException {
        LOGGER.debug("Get single message with " + id + "id");
        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            LOGGER.debug("Get Single message reached by message id");
            return syncAuditService.getJsonMessageById(id);
        }
        return null;
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    @ResponseBody
    public String getMessagesList(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {
        LOGGER.debug("Get messages list with " + pageIndex + " page index and " + pageSize + " page size");
        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return syncAuditService.getPaginatedMessages(pageIndex, pageSize);
        }
        return null;
    }
}