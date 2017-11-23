package org.openmrs.module.sync2.api.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.utils.SyncUtils;

import java.util.Arrays;
import java.util.List;

public class SyncConfigurationServiceImplTest {

    private static final String sampleFeedConfigurationPath = "sampleSyncConfiguration.json";
    private static final String sampleFeedConfigurationPath2 = "sampleSyncConfiguration2.json";

    @Before
    public void setUp() throws SyncException {
    }

    @Test
    public void saveConfiguration_shouldLoadTheSyncConfigurationFromObjectCorrectly() throws SyncException {
        final SyncConfiguration expectedSyncConfiguration = new SyncConfiguration();

        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress");
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

        SyncConfiguration syncConfiguration = SyncUtils.parseJsonFileToSyncConfiguration(sampleFeedConfigurationPath);
        SyncConfigurationServiceImpl sync2ConfigurationService = new SyncConfigurationServiceImpl();
        sync2ConfigurationService.saveConfiguration(syncConfiguration);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSyncConfiguration());
    }

    @Test
    public void saveConfiguration_shouldLoadTheSyncConfigurationFromStringCorrectly() throws SyncException {
        final SyncConfiguration expectedSyncConfiguration = new SyncConfiguration();

        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress2");
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

        String json = SyncUtils.readResourceFile(sampleFeedConfigurationPath2);
        SyncConfigurationServiceImpl sync2ConfigurationService = new SyncConfigurationServiceImpl();
        sync2ConfigurationService.saveConfiguration(json);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSyncConfiguration());
    }

}