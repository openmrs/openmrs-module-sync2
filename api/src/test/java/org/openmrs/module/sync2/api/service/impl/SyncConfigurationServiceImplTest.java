package org.openmrs.module.sync2.api.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.WhitelistConfiguration;
import org.openmrs.module.sync2.api.mother.SyncConfigurationMother;
import org.openmrs.module.sync2.api.scheduler.SyncSchedulerService;
import org.openmrs.module.sync2.api.scheduler.impl.SyncSchedulerServiceImpl;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.RELATIVE;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonFileToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.readResourceFile;

public class SyncConfigurationServiceImplTest {

    private static final String SAMPLE_FEED_CONFIGURATION_PATH = "sampleSyncConfiguration.json";
    private static final String SAMPLE_FEED_CONFIGURATION_PATH2 = "sampleSyncConfiguration2.json";
    private static final String NO_WHITELIST_PROVIDED_CONFIGURATION_PATH = "noWhitelistConfiguration.json";

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
        final SyncConfiguration expectedSyncConfiguration = SyncConfigurationMother.creteInstance(true, false);

        SyncConfiguration syncConfiguration = parseJsonFileToSyncConfiguration(SAMPLE_FEED_CONFIGURATION_PATH, RELATIVE);
        sync2ConfigurationService.saveConfiguration(syncConfiguration);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSyncConfiguration());
    }

    @Test
    public void saveConfiguration_shouldLoadTheSyncConfigurationFromStringCorrectly() throws SyncException {
        final SyncConfiguration expectedSyncConfiguration = SyncConfigurationMother.creteInstance(false, true);

        String json = readResourceFile(SAMPLE_FEED_CONFIGURATION_PATH2);
        sync2ConfigurationService.saveConfiguration(json);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSyncConfiguration());
    }

    @Test
    public void saveConfiguration_shouldSavedConfigurationWithDefaultWhitelist() throws SyncException {
        final SyncConfiguration expectedSyncConfiguration = SyncConfigurationMother.creteInstance(false, false);

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
