package org.openmrs.module.sync2.api.service.impl;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.converter.AuditMessageToStringConverter;
import org.openmrs.module.sync2.api.converter.StringToAuditMessageConverter;
import org.openmrs.module.sync2.api.dao.SyncAuditDao;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.audit.PaginatedAuditMessages;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SyncAuditServiceImplTest {

    private static final String AUDIT_MESSAGE_JSON = "/audit/sampleAuditMessage.json";
    private static final String PAGINATED_AUDIT_MESSAGE_RESPONSE_JSON = "/audit/sampleAuditMessages.json";
    
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
    private static final String AUDIT_MERGE_CONFLICT_UUID = "test";
    private static final String AUDIT_MERGE_CONFLICT_UUID2 = "test2";

    private static final Integer PAGINATED_PAGE = 1;
    private static final Integer PAGINATED_PAGE_SIZE = 100;

    private StringToAuditMessageConverter stringToAuditMessageConverter = new StringToAuditMessageConverter();
    private AuditMessageToStringConverter auditMessageToStringConverter = new AuditMessageToStringConverter();

    @InjectMocks
    private SyncAuditServiceImpl auditService;

    @Mock
    private SyncAuditDao dao;

    @Mock
    private SyncConfigurationService configurationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SyncConfiguration syncConfiguration =  Mockito.mock(SyncConfiguration.class);
        when(configurationService.getSyncConfiguration()).thenReturn(syncConfiguration);
        GeneralConfiguration generalConfiguration = Mockito.mock(GeneralConfiguration.class);
        when(syncConfiguration.getGeneral()).thenReturn(generalConfiguration);
    }

    @Test
    public void getMessageById() throws ParseException {
        Integer id = 1;

        AuditMessage expected = prepareAuditMessage(true);
        when(dao.getMessageById(id)).thenReturn(expected);

        AuditMessage fetched = auditService.getMessageById(id);

        Assert.assertEquals(expected, fetched);
    }

    @Test
    public void getMessageByUuid() throws ParseException {
        AuditMessage expected = prepareAuditMessage(true);
        when(dao.getMessageByUuid(AUDIT_UUID)).thenReturn(expected);

        AuditMessage fetched = auditService.getMessageByUuid(AUDIT_UUID);

        Assert.assertEquals(expected, fetched);
    }

    @Test
    public void getJsonMessageById() throws Exception {
        when(dao.getMessageById(AUDIT_ID)).thenReturn(prepareAuditMessage(false));

        String expected = readJsonFromFile(AUDIT_MESSAGE_JSON);
        String fetched = auditService.getJsonMessageById(AUDIT_ID) + '\n';

        Assert.assertEquals(expected, fetched);
    }

    @Test
    public void getJsonMessageByUuid() throws Exception {
        when(dao.getMessageByUuid(AUDIT_UUID)).thenReturn(prepareAuditMessage(false));

        String expected = readJsonFromFile(AUDIT_MESSAGE_JSON);
        String fetched = auditService.getJsonMessageByUuid(AUDIT_UUID) + '\n';

        Assert.assertEquals(expected, fetched);
    }

    @Test
    public void getPaginatedMessages() throws Exception {
        Integer page = 1;
        Integer pageSize = 100;

        PaginatedAuditMessages messages = preparePaginatedAuditMessages();
        when(dao.getPaginatedAuditMessages(page, pageSize, null, "", "", ""))
                .thenReturn(messages);
        
        String expected = readJsonFromFile(PAGINATED_AUDIT_MESSAGE_RESPONSE_JSON);
        String fetched = auditService.getPaginatedMessages(
                page, pageSize, null, "", "", "") + '\n';

        Assert.assertEquals(expected, fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldSaveSuccessfulAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(true);
        
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(true);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
    
        AuditMessage fetched = auditService.saveAuditMessageDuringSync(auditMessage);
        
        Assert.assertEquals(auditMessage, fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldNotSaveSuccessfulAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(true);
    
    
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
        
        AuditMessage fetched = auditService.saveAuditMessageDuringSync(auditMessage);
        
        Assert.assertNull(fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldSaveFailureAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(false);
        
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(true);
        
        AuditMessage fetched = auditService.saveAuditMessageDuringSync(auditMessage);
        
        Assert.assertEquals(auditMessage, fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldNotFailedIfBooleanObjectIsNotSet() {
        AuditMessage auditMessage = new AuditMessage();
        
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(true);
        
        auditService.saveAuditMessageDuringSync(auditMessage);
    }
    
    @Test
    public void saveAuditMessage_shouldNotSaveFailureAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(false);
        
        
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
        
        AuditMessage fetched = auditService.saveAuditMessageDuringSync(auditMessage);
        
        Assert.assertNull(fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldNotSaveAuditWithNotNullTimestamp() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(true);
    
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(true);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
    
        AuditMessage fetched = auditService.saveAuditMessageDuringSync(auditMessage);
    
        Assert.assertEquals(auditMessage, fetched);
        Assert.assertNotNull(fetched.getTimestamp());
    }
    
    @Test
    public void convert_shouldDeserializeAuditMessage() throws Exception {
        final String toDeserialize = readJsonFromFile(AUDIT_MESSAGE_JSON);
        AuditMessage fetched = stringToAuditMessageConverter.convert(toDeserialize);
        AuditMessage expected = prepareAuditMessage(false);
        
        Assert.assertEquals(expected, fetched);
    }

    @Test
    public void convert_shouldSerializeAuditMessage() throws Exception {
        AuditMessage toSerialize = prepareAuditMessage(false);
        String fetched = auditMessageToStringConverter.convert(toSerialize) + '\n';
        final String expected = readJsonFromFile(AUDIT_MESSAGE_JSON);

        Assert.assertEquals(expected, fetched);
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
        newMessage.setMergeConflictUuid(AUDIT_MERGE_CONFLICT_UUID);

        String createDate = "2017-12-07 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate = dateFormat.parse(createDate);
    
        newMessage.setTimestamp(new java.sql.Timestamp(parsedDate.getTime()));
        return newMessage;
    }

    private PaginatedAuditMessages preparePaginatedAuditMessages() throws ParseException {
        List<AuditMessage> list = new ArrayList<>();
        list.add(prepareAuditMessage(true));
        list.add(prepareAuditMessage(false));

        list.get(0).setResourceName("Test 1");
        list.get(0).setId(AUDIT_ID);
        list.get(0).setUuid(AUDIT_UUID);
        list.get(0).setId(2);

        list.get(1).setResourceName("Test 2");
        list.get(1).setUuid("74e75d4a-393c-4611-a903-883a0fd5fc6f");
        list.get(1).setMergeConflictUuid(AUDIT_MERGE_CONFLICT_UUID2);


        return new PaginatedAuditMessages((long) list.size(), PAGINATED_PAGE, PAGINATED_PAGE_SIZE, list);
    }

    private String readJsonFromFile(String filename) throws Exception {
        Resource resource = new ClassPathResource(filename);
        String json;
        try(InputStream is = resource.getInputStream()) {
            json = IOUtils.toString(is);
        }

        return json;
    }
}
