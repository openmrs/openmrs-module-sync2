package org.openmrs.module.sync2.web.controller.rest;

import com.google.gson.JsonParseException;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.enums.Action;
import org.openmrs.module.sync2.api.model.enums.Resources;
import org.openmrs.module.sync2.api.model.enums.Status;
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
    private SyncAuditService syncAuditService;

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
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam(value = "success", required = false) String successEnum,
            @RequestParam(value = "action", required = false) String actionEnum,
            @RequestParam(value = "resourceName", required = false) String resourceNameEnum) {
        LOGGER.debug("Get messages list with " + pageIndex + " page index and " + pageSize + " page size");

        Boolean success = extractStatus(successEnum);
        String action = extractAction(actionEnum);
        String resource = extractResourceName(resourceNameEnum.toUpperCase());

        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return syncAuditService.getPaginatedMessages(pageIndex, pageSize, success, action, resource);
        }
        return null;
    }

    private Boolean extractStatus(String enumValue) {
        try {
            return Status.valueOf(enumValue).isSuccess();
        } catch(IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable status: %s.", enumValue));
        }
    }

    private String extractAction(String enumValue) {

        try {
            return Action.valueOf(enumValue).name() == Action.ALL.name() ?
                    "" : Action.valueOf(enumValue).name();

        } catch(IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable action: %s.", enumValue));
        }
    }

    private String extractResourceName(String enumValue) {
        try {
            return Resources.valueOf(enumValue).name() == Resources.ALL.name() ?
                    "" : Resources.valueOf(enumValue).getName();
        } catch(IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable resource: %s.", enumValue));
        }
    }

}