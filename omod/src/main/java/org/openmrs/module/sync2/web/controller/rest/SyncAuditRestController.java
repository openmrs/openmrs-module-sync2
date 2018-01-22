package org.openmrs.module.sync2.web.controller.rest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.InstanceIds;
import org.openmrs.module.sync2.api.model.enums.Operation;
import org.openmrs.module.sync2.api.model.enums.Resources;
import org.openmrs.module.sync2.api.model.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<String> getJsonMessageByUuid(@PathVariable String uuid) {
        LOGGER.debug("Get single message with " + uuid + "uuid via REST API");
        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            String json = syncAuditService.getJsonMessageByUuid(uuid);
            LOGGER.debug("Get Single message reached by message uuid");
            return ResponseEntity.ok(json);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    
    @RequestMapping(value = "/messages", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createAuditMessage(@RequestBody AuditMessage auditMessage) {
        LOGGER.debug("Fetched POST request for creating AuditMessage: {}", auditMessage);
        
        if (syncAuditService.getMessageByUuid(auditMessage.getUuid()) != null) {
            return new ResponseEntity<>("The entity already exists", HttpStatus.FORBIDDEN);
        } else if (!Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            LOGGER.error("Tried to post AuditMessage without '{}' privilege", SyncModuleConfig.SYNC_AUDIT_PRIVILEGE);
            return new ResponseEntity<>(
                    String.format("You don't have %s privilege", SyncModuleConfig.SYNC_AUDIT_PRIVILEGE),
                    HttpStatus.UNAUTHORIZED);
        } else {
            auditMessage.setId(null);
            syncAuditService.saveAuditMessage(auditMessage);
            LOGGER.info("Created AuditMessage with {} uuid", auditMessage.getUuid());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "/messages/{uuid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAuditMessage(@PathVariable String uuid, @RequestBody AuditMessage auditMessage) {
        LOGGER.debug("Fetched POST request for updating AuditMessage: {}", auditMessage);
        AuditMessage alreadyExistingAuditMessage = syncAuditService.getMessageByUuid(uuid);
        
        if (!StringUtils.equals(uuid, auditMessage.getUuid())) {
            return new ResponseEntity<>("Sent UUID and object's UUID don't match", HttpStatus.FORBIDDEN);
        } else if (alreadyExistingAuditMessage == null) {
            return new ResponseEntity<>("The entity doesn't exists", HttpStatus.FORBIDDEN);
        } else if (!Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            LOGGER.error("Tried to post AuditMessage without '{}' privilege", SyncModuleConfig.SYNC_AUDIT_PRIVILEGE);
            return new ResponseEntity<>(
                    String.format("You don't have %s privilege", SyncModuleConfig.SYNC_AUDIT_PRIVILEGE),
                    HttpStatus.UNAUTHORIZED);
        } else {
            auditMessage.setId(alreadyExistingAuditMessage.getId());
            syncAuditService.saveAuditMessage(auditMessage);
            LOGGER.info("Updated AuditMessage with {} uuid", auditMessage.getUuid());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getMessagesList(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam(value = "success") String successEnum,
            @RequestParam(value = "operation") String operationEnum,
            @RequestParam(value = "resourceName") String resourceNameEnum,
            @RequestParam(value = "creatorInstanceId") String creatorInstanceId) {
        LOGGER.debug("Get messages list with " + pageIndex + " page index and " + pageSize + " page size");

        String creatorInstanceRegex = extractCreatorInstanceRegex(creatorInstanceId);
        Boolean success = extractStatus(successEnum);
        String operation = extractOperation(operationEnum);
        String resource = extractResourceName(resourceNameEnum.toUpperCase());

        if (Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return ResponseEntity.ok(syncAuditService.getPaginatedMessages(pageIndex, pageSize, success, operation,
                    resource, creatorInstanceRegex));
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    private Boolean extractStatus(String enumValue) {
        try {
            return Status.valueOf(enumValue).isSuccess();
        } catch (IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable status: %s.", enumValue), e);
        }
    }

    private String extractOperation(String enumValue) {
        try {
            return Operation.valueOf(enumValue).name().equals(Operation.ALL.name()) ?
                    "" : Operation.valueOf(enumValue).name();
        } catch (IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable action: %s.", enumValue), e);
        }
    }

    private String extractResourceName(String enumValue) {
        try {
            return Resources.valueOf(enumValue).name().equals(Resources.ALL.name()) ?
                    "" : Resources.valueOf(enumValue).getName();
        } catch (IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable resource: %s.", enumValue), e);
        }
    }

    private String extractCreatorInstanceRegex(String enumValue) {
        try {
            return InstanceIds.valueOf(enumValue).getRegex();
        } catch (IllegalArgumentException e) {
            if (StringUtils.isNotBlank(enumValue)) {
                LOGGER.info("Used creatorInstanceId with value: {}.", enumValue);
                return enumValue;
            } else {
                throw new SyncException(String.format("There is no suitable creatorInstanceId: %s.", enumValue), e);
            }
        }
    }
}