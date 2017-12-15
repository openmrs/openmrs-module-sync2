package org.openmrs.module.sync2.api.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.dao.SyncAuditDao;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SyncAuditServiceImplTest {

    private static final String AUDIT_MESSAGE_JSON = "/audit/sampleAuditMessage.json";
    private static final String PAGINATED_AUDIT_MESSAGE_RESPONSE_JSON = "/audit/sampleAuditMessages.json";
    private static final String AUDIT_ACTION = "testAction";
    private static final String AUDIT_DETAILS = "testDetails";
    private static final String AUDIT_NAME = "test";
    private static final String AUDIT_USED_URL = "/test/test";

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
    public void getJsonMessageById() throws Exception {
        Integer id = 1;

        when(dao.getMessageById(id)).thenReturn(prepareAuditMessage(false));

        String expected = readJsonFromFile(AUDIT_MESSAGE_JSON);
        String fetched = auditService.getJsonMessageById(id);

        Assert.assertEquals(expected, fetched);
    }

    @Test
    public void getPaginatedMessages() throws Exception {
        Integer page = 1;
        Integer pageSize = 100;

        List<AuditMessage> messages = prepareAuditMessages();
        when(dao.getPaginatedMessages(page, pageSize, null, "", "")).thenReturn(messages);
        when(dao.getCountOfMessages()).thenReturn((long) messages.size());
        String expected = readJsonFromFile(PAGINATED_AUDIT_MESSAGE_RESPONSE_JSON);
        String fetched = auditService.getPaginatedMessages(page, pageSize, null, "", "");

        Assert.assertEquals(expected, fetched);
    }

    @Test
    public void saveFailedAudit_shouldSave() {
        AuditMessage auditMessage = new AuditMessage();

        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(true);

        AuditMessage fetched = auditService.saveFailedAudit(AUDIT_NAME, AUDIT_USED_URL, AUDIT_ACTION, AUDIT_DETAILS);

        Assert.assertNotNull(fetched);
    }

    @Test
    public void saveFailedAudit_shouldNotSave() {
        AuditMessage auditMessage = new AuditMessage();

        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);

        AuditMessage fetched = auditService.saveFailedAudit(AUDIT_NAME, AUDIT_USED_URL, AUDIT_ACTION, AUDIT_DETAILS);

        Assert.assertNull(fetched);
    }

    @Test
    public void saveSuccessfulAudit_shouldSave() {
        AuditMessage auditMessage = new AuditMessage();

        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(true);

        AuditMessage fetched = auditService.saveSuccessfulAudit(AUDIT_NAME, AUDIT_USED_URL, AUDIT_ACTION, AUDIT_DETAILS);

        Assert.assertNotNull(fetched);
    }

    @Test
    public void saveSuccessfulAudit_shouldNotSave() {
        AuditMessage auditMessage = new AuditMessage();

        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);

        AuditMessage fetched = auditService.saveSuccessfulAudit(AUDIT_NAME, AUDIT_USED_URL, AUDIT_ACTION, AUDIT_DETAILS);

        Assert.assertNull(fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldSaveSuccessfulAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(true);
        
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(true);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
    
        AuditMessage fetched = auditService.saveAuditMessage(auditMessage);
        
        Assert.assertEquals(auditMessage, fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldNotSaveSuccessfulAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(true);
    
    
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
        
        AuditMessage fetched = auditService.saveAuditMessage(auditMessage);
        
        Assert.assertNull(fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldSaveFailureAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(false);
        
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(true);
        
        AuditMessage fetched = auditService.saveAuditMessage(auditMessage);
        
        Assert.assertEquals(auditMessage, fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldNotSaveFailureAudit() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(false);
        
        
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(false);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
        
        AuditMessage fetched = auditService.saveAuditMessage(auditMessage);
        
        Assert.assertNull(fetched);
    }
    
    @Test
    public void saveAuditMessage_shouldNotSaveAuditWithNotNullTimestamp() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setSuccess(true);
    
        when(dao.saveItem(any())).thenReturn(auditMessage);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistSuccessAudit()).thenReturn(true);
        when(configurationService.getSyncConfiguration().getGeneral().isPersistFailureAudit()).thenReturn(false);
    
        AuditMessage fetched = auditService.saveAuditMessage(auditMessage);
    
        Assert.assertEquals(auditMessage, fetched);
        Assert.assertNotNull(fetched.getTimestamp());
    }

    private AuditMessage prepareAuditMessage(Boolean success) throws ParseException {
        AuditMessage newMessage = new AuditMessage();
        newMessage.setId(1);
        newMessage.setAction(AUDIT_ACTION);
        newMessage.setDetails(AUDIT_DETAILS);
        newMessage.setResourceName(AUDIT_NAME);
        newMessage.setUsedResourceUrl(AUDIT_USED_URL);
        newMessage.setSuccess(success);

        String createDate = "2017-12-07 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate = dateFormat.parse(createDate);

        newMessage.setTimestamp(new java.sql.Timestamp(parsedDate.getTime()));

        newMessage.setUuid("9f3dccc9-6bc3-4a2b-862d-af4ce41caa28");
        return newMessage;
    }

    private List<AuditMessage> prepareAuditMessages() throws ParseException {
        List<AuditMessage> result = new ArrayList<>();
        result.add(prepareAuditMessage(true));
        result.add(prepareAuditMessage(false));

        result.get(0).setResourceName("Test 1");
        result.get(0).setUuid("9f3dccc9-6bc3-4a2b-862d-af4ce41caa28");

        result.get(1).setResourceName("Test 2");
        result.get(1).setUuid("74e75d4a-393c-4611-a903-883a0fd5fc6f");

        return result;
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