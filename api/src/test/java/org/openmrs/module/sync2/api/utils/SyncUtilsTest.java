package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.model.enums.AtomfeedTagContent;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.mother.SyncConfigurationMother;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.service.impl.SyncConfigurationServiceImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class SyncUtilsTest {

    private static final String GLOBAL_CLIENT = SyncConstants.REST_CLIENT;

    private static final String LOCATION_PREFERRED_CLIENT = SyncConstants.FHIR_CLIENT;

    @Mock
    private SyncConfigurationServiceImpl syncConfigurationServiceImpl;

    @Mock
    private AdministrationService administrationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(syncConfigurationServiceImpl.getSyncConfiguration()).thenReturn(
                SyncConfigurationMother.creteInstance(true, true));
        Mockito.when(syncConfigurationServiceImpl.getClassConfiguration(Mockito.any(), Mockito.any())).thenCallRealMethod();

        Mockito.when(administrationService.getGlobalProperty(SyncConstants.RESOURCE_PREFERRED_CLIENT,
                SyncConstants.DEFAULT_SYNC_2_CLIENT)).thenReturn(GLOBAL_CLIENT);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.getService(SyncConfigurationService.class)).thenReturn(syncConfigurationServiceImpl);
        PowerMockito.when(Context.getAdministrationService()).thenReturn(administrationService);
    }

    @Test
    public void checkIfParamIsEventAction_shouldReturnTrueIfParamIsEventAction() {
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("UPDATED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("CREATED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("RETIRED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("UNRETIRED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("UNVOIDED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("DELETED"));
    }

    @Test
    public void checkIfParamIsEventAction_shouldReturnFalseIfParamIsNotEventAction() {
        Assert.assertFalse(SyncUtils.checkIfParamIsEventAction("updated"));
        Assert.assertFalse(SyncUtils.checkIfParamIsEventAction("somethingElse"));
    }

    @Test
    public void getValueOfAtomfeedEventTag_shouldReturnCategoryDespiteItsOrderInList() {
        final String expected = "patient";
        String result1 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags1(), AtomfeedTagContent.CATEGORY);
        String result2 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags2(), AtomfeedTagContent.CATEGORY);
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
    }

    @Test
    public void getValueOfAtomfeedEventTag_shouldReturnEventActionDespiteItsOrderInList() {
        final String expected = "CREATED";
        String result1 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags1(),
                AtomfeedTagContent.EVENT_ACTION);
        String result2 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags2(),
                AtomfeedTagContent.EVENT_ACTION);
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
    }

    @Test
    public void selectAppropriateClientName_shouldReturnClassClientIfExist() throws Exception {
        String expected = LOCATION_PREFERRED_CLIENT;
        String actual = SyncUtils.selectAppropriateClientName(prepareDummyLinksTemplate(), "location",
                SyncOperation.PULL);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void selectAppropriateClientName_shouldReturnFirstFromLinkTemplateIfGlobalAndClassNotExist() throws Exception {
        Mockito.when(administrationService.getGlobalProperty(SyncConstants.RESOURCE_PREFERRED_CLIENT,
                SyncConstants.DEFAULT_SYNC_2_CLIENT)).thenReturn(null);

        String expected = SyncConstants.FHIR_CLIENT;
        String actual = SyncUtils.selectAppropriateClientName(prepareDummyLinksTemplate(), "observation",
                SyncOperation.PULL);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void selectAppropriateClientName_shouldReturnGlobalClientIfClassClientNotExist() throws Exception {
        String expected = GLOBAL_CLIENT;
        String actual = SyncUtils.selectAppropriateClientName(prepareDummyLinksTemplate(), "observation",
                SyncOperation.PUSH);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    private Map<String, String> prepareDummyLinksTemplate() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        result.put(SyncConstants.FHIR_CLIENT, "fhirUrl");
        result.put(SyncConstants.REST_CLIENT, "restUrl");
        return result;
    }

    private List<Object> prepareDummyAtomfeedTags1() {
        List list = new ArrayList();
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=CREATED\nCategory.label=null\n");
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=patient\nCategory.label=null\n");
        return list;
    }

    private List<Object> prepareDummyAtomfeedTags2() {
        List list = new ArrayList();
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=patient\nCategory.label=null\n");
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=CREATED\nCategory.label=null\n");
        return list;
    }
}
