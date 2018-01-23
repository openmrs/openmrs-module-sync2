package org.openmrs.module.sync2.api.converter;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StringToAuditMessageConverterTest {
    
    private static final String AUDIT_MESSAGE_JSON = "/audit/sampleAuditMessage.json";
    
    private static final Integer AUDIT_ID = 1;
    private static final String AUDIT_ACTION = "testAction";
    private static final String AUDIT_DETAILS = "testDetails";
    private static final String AUDIT_NAME = "test";
    private static final String AUDIT_USED_URL = "/test/test";
    private static final String AUDIT_OPERATION = "testOperation";
    private static final String AUDIT_PARENT_URL = "parentUrl";
    private static final String AUDIT_LOCAL_URL = "localUrl";
    private static final String AUDIT_LINK_TYPE = "test";
    private static final String AUDIT_NEXT_MESSAGE_UUID = "next_message_uuid";
    private static final String AUDIT_UUID = "9f3dccc9-6bc3-4a2b-862d-af4ce41caa28";
    private static final String AUDIT_CREATOR_INSTANCE = "sampleCreatorInstance1";
    

    
    private AuditMessage prepareAuditMessage(Boolean success) throws ParseException {
        AuditMessage newMessage = new AuditMessage();
        newMessage.setId(AUDIT_ID);
        newMessage.setAvailableResourceUrls(prepareDummyAvailableResourcesUrls());
        newMessage.setOperation(AUDIT_OPERATION);
        newMessage.setAction(AUDIT_ACTION);
        newMessage.setDetails(AUDIT_DETAILS);
        newMessage.setResourceName(AUDIT_NAME);
        newMessage.setParentUrl(AUDIT_PARENT_URL);
        newMessage.setLocalUrl(AUDIT_LOCAL_URL);
        newMessage.setUsedResourceUrl(AUDIT_USED_URL);
        newMessage.setLinkType(AUDIT_LINK_TYPE);
        newMessage.setNextMessageUuid(AUDIT_NEXT_MESSAGE_UUID);
        newMessage.setCreatorInstanceId(AUDIT_CREATOR_INSTANCE);
        newMessage.setUuid(AUDIT_UUID);
        newMessage.setSuccess(success);
        
        String createDate = "2017-12-07 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate = dateFormat.parse(createDate);
        
        newMessage.setTimestamp(new java.sql.Timestamp(parsedDate.getTime()));
        return newMessage;
    }
    
    private String prepareDummyAvailableResourcesUrls() {
        Map<String, String> availableResourceUrls = new HashMap<>();
        availableResourceUrls.put("fhir", "testUrl1");
        availableResourceUrls.put("rest", "testUrl2");
        try {
            return new ObjectMapper().writeValueAsString(availableResourceUrls);
        } catch (IOException ex) {
            return null;
        }
    }
}