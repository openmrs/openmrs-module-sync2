package org.openmrs.module.sync2.web.controller.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.converter.StringToAuditMessageConverter;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.InstanceId;
import org.openmrs.module.sync2.api.model.enums.Resources;
import org.openmrs.module.sync2.api.model.enums.Status;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

@Controller("sync2.SyncAuditRestController")
@RequestMapping(value = "/rest/sync2")
public class SyncAuditRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(SyncAuditRestController.class);

    private final static String INVALID_JSON = "Incorrect AuditMessage JSON given";

    private final static ResponseEntity<String> NOT_FOUND_RESPONSE = new ResponseEntity<>(
            "The entity doesn't exists", HttpStatus.NOT_FOUND);

    private final static ResponseEntity<String> ENTITY_ALREADY_EXISTS_RESPONSE = new ResponseEntity<>(
            "The entity already exists", HttpStatus.FORBIDDEN);

    private final static ResponseEntity<String> MISSING_PRIVILEGE_RESPONSE = new ResponseEntity<>(
            String.format("Tried to post AuditMessage without '%s' privilege",
                    SyncModuleConfig.SYNC_AUDIT_PRIVILEGE),
            HttpStatus.UNAUTHORIZED);

    @Autowired
    private SyncAuditService syncAuditService;
    
    @Autowired
    private StringToAuditMessageConverter stringToAuditMessageConverter;

    @RequestMapping(value = "/messages/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getJsonMessageByUuid(@PathVariable String uuid) {
        LOGGER.debug("Get single message with " + uuid + "uuid via REST API");
        if (!Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return MISSING_PRIVILEGE_RESPONSE;
        } else {
            String json = syncAuditService.getJsonMessageByUuid(uuid);
            if (StringUtils.isEmpty(json)) {
                return NOT_FOUND_RESPONSE;
            } else {
                LOGGER.debug("Get Single message reached by message uuid");
                return buildSuccessResponse(json);
            }
        }
    }

    @RequestMapping(value = "/messages", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createAuditMessage(@RequestBody String auditMessageJson) {
        LOGGER.debug("Fetched POST request for creating AuditMessage: {}", auditMessageJson);
        AuditMessage auditMessage;
        try {
            auditMessage = stringToAuditMessageConverter.convert(auditMessageJson);
        } catch (Exception ex) {
            return new ResponseEntity<>(INVALID_JSON + "\n"
                    + ExceptionUtils.getFullStackTrace(ex), HttpStatus.BAD_REQUEST);
        }

        if (syncAuditService.getMessageByUuid(auditMessage.getUuid()) != null) {
            return ENTITY_ALREADY_EXISTS_RESPONSE;
        } else if (!Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return MISSING_PRIVILEGE_RESPONSE;
        } else {
            auditMessage.setId(null);
            AuditMessage message = syncAuditService.saveAuditMessageDuringSync(auditMessage);
            LOGGER.info("Created AuditMessage with {} uuid", message.getUuid());
            return buildSuccessResponse(syncAuditService.getJsonMessage(message));
        }
    }

    @RequestMapping(value = "/messages/{uuid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAuditMessage(@PathVariable String uuid, @RequestBody String auditMessageJson) {
        LOGGER.debug("Fetched POST request for updating AuditMessage: {}", auditMessageJson);
        AuditMessage auditMessage;
        try {
            auditMessage = stringToAuditMessageConverter.convert(auditMessageJson);
        } catch (Exception ex) {
            return new ResponseEntity<>(INVALID_JSON + "\n"
                    + ExceptionUtils.getFullStackTrace(ex), HttpStatus.BAD_REQUEST);
        }

        AuditMessage alreadyExistingAuditMessage = syncAuditService.getMessageByUuid(uuid);
        if (!StringUtils.equals(uuid, auditMessage.getUuid())) {
            return new ResponseEntity<>("Sent UUID and object's UUID don't match", HttpStatus.BAD_REQUEST);
        } else if (alreadyExistingAuditMessage == null) {
            return NOT_FOUND_RESPONSE;
        } else if (!Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return MISSING_PRIVILEGE_RESPONSE;
        } else {
            auditMessage.setId(alreadyExistingAuditMessage.getId());
            AuditMessage message = syncAuditService.saveAuditMessageDuringSync(auditMessage);
            LOGGER.info("Updated AuditMessage with {} uuid", message.getUuid());
            return buildSuccessResponse(syncAuditService.getJsonMessage(message));
        }
    }

    @RequestMapping(value = "/messages/{uuid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteAuditMessage(@PathVariable String uuid) {
        LOGGER.debug("Fetched POST request for deleting AuditMessage with {} uuid", uuid);
        AuditMessage alreadyExistingAuditMessage = syncAuditService.getMessageByUuid(uuid);

        if (alreadyExistingAuditMessage == null) {
            return NOT_FOUND_RESPONSE;
        } else if (!Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return MISSING_PRIVILEGE_RESPONSE;
        } else {
            alreadyExistingAuditMessage.setVoided(true);
            AuditMessage message = syncAuditService.saveAuditMessage(alreadyExistingAuditMessage);
            LOGGER.info("Deleted AuditMessage with {} uuid", alreadyExistingAuditMessage.getUuid());
            return buildSuccessResponse(syncAuditService.getJsonMessage(message));
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

        if (!Context.hasPrivilege(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)) {
            return MISSING_PRIVILEGE_RESPONSE;
        } else {
            String body = syncAuditService.getPaginatedMessages(pageIndex, pageSize, success,
                    operation, resource, creatorInstanceRegex);
            return buildSuccessResponse(body);
        }
    }

    private ResponseEntity<String> buildSuccessResponse(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
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
            SyncOperation operation = SyncOperation.getByValue(enumValue);
            return operation.equals(SyncOperation.ALL) ? "" : operation.name();
        } catch (IllegalArgumentException e) {
            throw new SyncException(String.format("There is no suitable operation: %s.", enumValue), e);
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
            return InstanceId.valueOf(enumValue).getRegex();
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
