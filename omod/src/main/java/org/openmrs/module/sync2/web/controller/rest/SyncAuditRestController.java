package org.openmrs.module.sync2.web.controller.rest;

import com.google.gson.JsonParseException;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.enums.Operation;
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

    @RequestMapping(value = "/messages/{uuid}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getJsonMessageByUuid(@PathVariable String uuid) throws JsonParseException {
        LOGGER.debug("Get single message with " + uuid + "uuid via REST API");
        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            String json = syncAuditService.getJsonMessageByUuid(uuid);
            LOGGER.debug("Get Single message reached by message uuid");
            return json;
        }
        return null;
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    @ResponseBody
    public String getMessagesList(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam(value = "success", required = false) String successEnum,
            @RequestParam(value = "operation", required = false) String operationEnum,
            @RequestParam(value = "resourceName", required = false) String resourceNameEnum) {
        LOGGER.debug("Get messages list with " + pageIndex + " page index and " + pageSize + " page size");

        Boolean success = extractStatus(successEnum);
        String operation = extractOperation(operationEnum);
        String resource = extractResourceName(resourceNameEnum.toUpperCase());

        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return syncAuditService.getPaginatedMessages(pageIndex, pageSize, success, operation, resource);
        }
        return null;
    }

    private Boolean extractStatus(String enumValue) {
        try {
            return Status.valueOf(enumValue).isSuccess();
        } catch(IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable status: %s.", enumValue), e);
        }
    }

    private String extractOperation(String enumValue) {
        try {
            return Operation.valueOf(enumValue).name().equals(Operation.ALL.name()) ?
                    "" : Operation.valueOf(enumValue).name();

        } catch(IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable action: %s.", enumValue), e);
        }
    }

    private String extractResourceName(String enumValue) {
        try {
            return Resources.valueOf(enumValue).name().equals(Resources.ALL.name()) ?
                    "" : Resources.valueOf(enumValue).getName();
        } catch(IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable resource: %s.", enumValue), e);
        }
    }

}