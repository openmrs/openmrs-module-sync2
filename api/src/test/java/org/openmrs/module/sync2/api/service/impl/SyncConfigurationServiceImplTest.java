package org.openmrs.module.sync2.api.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.model.configuration.WhitelistConfiguration;
import org.openmrs.module.sync2.api.model.enums.ResourcePathType;
import org.openmrs.module.sync2.api.scheduler.SyncSchedulerService;
import org.openmrs.module.sync2.api.scheduler.impl.SyncSchedulerServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.RELATIVE;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonFileToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.readResourceFile;

public class SyncConfigurationServiceImplTest {

    private static final String SAMPLE_FEED_CONFIGURATION_PATH = "sampleSyncConfiguration.json";
    private static final String SAMPLE_FEED_CONFIGURATION_PATH2 = "sampleSyncConfiguration2.json";
    private static final String NO_WHITELIST_PROVIDED_CONFIGURATION_PATH = "noWhitelistConfiguration.json";

    private static final String SAMPLE_LOCAL_INSTANCE_ID = "localInstanceId";
    private static final String SAMPLE_LOCAL_INSTANCE_ID_2 = "localInstanceId2";
    
    @Mock
    private SyncSchedulerService syncSchedulerService;

    @InjectMocks
    private SyncConfigurationService sync2ConfigurationService;

    @Before
    public void setUp() throws SyncException {
        sync2ConfigurationService = new SyncConfigurationServiceImpl();
        syncSchedulerService = new SyncSchedulerServiceImpl();

        initMocks(this);
    }

    @Test
    public void saveConfiguration_shouldLoadTheSyncConfigurationFromObjectCorrectly() throws SyncException {
        final SyncConfiguration expectedSyncConfiguration = new SyncConfiguration();

        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress",
                SAMPLE_LOCAL_INSTANCE_ID, false, false);
        expectedSyncConfiguration.setGeneral(general);

        ClassConfiguration locationClass = new ClassConfiguration("Location",
                "location", "org.openmrs.Location", true);
        ClassConfiguration observationClass = new ClassConfiguration("Observation",
                "observation", "org.openmrs.Obs", true);
        List<ClassConfiguration> classes = Arrays.asList(locationClass, observationClass);

        SyncMethodConfiguration push = new SyncMethodConfiguration(true, 12, classes);
        expectedSyncConfiguration.setPush(push);

        SyncMethodConfiguration pull = new SyncMethodConfiguration(true, 12, classes);
        expectedSyncConfiguration.setPull(pull);

        List<String> instanceIds = new ArrayList<>();
        instanceIds.add("childInstanceId");
        WhitelistConfiguration whitelist = new WhitelistConfiguration(true, instanceIds);
        expectedSyncConfiguration.setWhitelist(whitelist);

        SyncConfiguration syncConfiguration = parseJsonFileToSyncConfiguration(SAMPLE_FEED_CONFIGURATION_PATH, RELATIVE);
        sync2ConfigurationService.saveConfiguration(syncConfiguration);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSyncConfiguration());
    }

    @Test
    public void saveConfiguration_shouldLoadTheSyncConfigurationFromStringCorrectly() throws SyncException {
        final SyncConfiguration expectedSyncConfiguration = new SyncConfiguration();

        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress2",
                SAMPLE_LOCAL_INSTANCE_ID_2, false, false);
        expectedSyncConfiguration.setGeneral(general);

        ClassConfiguration encounterClass = new ClassConfiguration("Encounter",
                "encounter", "org.openmrs.Encounter", false);
        ClassConfiguration visitClass = new ClassConfiguration("Visit",
                "visit", "org.openmrs.Visit", false);

        List<ClassConfiguration> classes = Arrays.asList(encounterClass, visitClass);

        SyncMethodConfiguration push = new SyncMethodConfiguration(false, 24, classes);
        expectedSyncConfiguration.setPush(push);

        SyncMethodConfiguration pull = new SyncMethodConfiguration(false, 24, classes);
        expectedSyncConfiguration.setPull(pull);

        WhitelistConfiguration whitelist = new WhitelistConfiguration(false, new ArrayList<>());
        expectedSyncConfiguration.setWhitelist(whitelist);

        String json = readResourceFile(SAMPLE_FEED_CONFIGURATION_PATH2);
        sync2ConfigurationService.saveConfiguration(json);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSyncConfiguration());
    }

    @Test
    public void saveConfiguration_shouldSavedConfigurationWithDefaultWhitelist() throws SyncException {
        final SyncConfiguration expectedSyncConfiguration = new SyncConfiguration();

        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress2",
                SAMPLE_LOCAL_INSTANCE_ID_2, false, false);
        expectedSyncConfiguration.setGeneral(general);

        ClassConfiguration encounterClass = new ClassConfiguration("Encounter",
                "encounter", "org.openmrs.Encounter", false);
        ClassConfiguration visitClass = new ClassConfiguration("Visit",
                "visit", "org.openmrs.Visit", false);

        List<ClassConfiguration> classes = Arrays.asList(encounterClass, visitClass);

        SyncMethodConfiguration push = new SyncMethodConfiguration(false, 24, classes);
        expectedSyncConfiguration.setPush(push);

        SyncMethodConfiguration pull = new SyncMethodConfiguration(false, 24, classes);
        expectedSyncConfiguration.setPull(pull);

        WhitelistConfiguration whitelist = new WhitelistConfiguration(false, new ArrayList<>());
        expectedSyncConfiguration.setWhitelist(whitelist);

        String json = readResourceFile(NO_WHITELIST_PROVIDED_CONFIGURATION_PATH);
        sync2ConfigurationService.saveConfiguration(json);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSyncConfiguration());
    }

    @Test
    public void shouldReadDefaultWhitelistIfNotProvided() throws SyncException {
        String defaultWhitelistJson = readResourceFile(SAMPLE_FEED_CONFIGURATION_PATH2);
        sync2ConfigurationService.saveConfiguration(defaultWhitelistJson);
        WhitelistConfiguration defaultWhitelist = sync2ConfigurationService.getSyncConfiguration().getWhitelist();

        String noWhitelistJson = readResourceFile(NO_WHITELIST_PROVIDED_CONFIGURATION_PATH);
        sync2ConfigurationService.saveConfiguration(noWhitelistJson);
        WhitelistConfiguration noWhitelist = sync2ConfigurationService.getSyncConfiguration().getWhitelist();

        Assert.assertEquals(defaultWhitelist, noWhitelist);
    }

}
