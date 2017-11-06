package org.openmrs.module.sync2.api.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.Sync2Configuration;
import org.openmrs.module.sync2.api.model.configuration.Sync2MethodConfiguration;
import org.openmrs.module.sync2.api.utils.Sync2Utils;

import java.util.Arrays;
import java.util.List;

public class Sync2ConfigurationServiceImplTest {

    private static final String sampleFeedConfigurationPath = "sampleSyncConfiguration.json";
    private static final String sampleFeedConfigurationPath2 = "sampleSyncConfiguration2.json";

    @Before
    public void setUp() throws Sync2Exception {
    }

    @Test
    public void saveConfiguration_shouldLoadTheSyncConfigurationFromObjectCorrectly() throws Sync2Exception {
        final Sync2Configuration expectedSyncConfiguration = new Sync2Configuration();

        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress");
        expectedSyncConfiguration.setGeneral(general);

        ClassConfiguration locationClass = new ClassConfiguration("Location",
                "location", "org.openmrs.Location", true);
        ClassConfiguration observationClass = new ClassConfiguration("Observation",
                "observation", "org.openmrs.Obs", true);
        List<ClassConfiguration> classes = Arrays.asList(locationClass, observationClass);

        Sync2MethodConfiguration push = new Sync2MethodConfiguration(true, 12, classes);
        expectedSyncConfiguration.setPush(push);

        Sync2MethodConfiguration pull = new Sync2MethodConfiguration(true, 12, classes);
        expectedSyncConfiguration.setPull(pull);

        Sync2Configuration syncConfiguration = Sync2Utils.parseJsonFileToSyncConfiguration(sampleFeedConfigurationPath);
        Sync2ConfigurationServiceImpl sync2ConfigurationService = new Sync2ConfigurationServiceImpl();
        sync2ConfigurationService.saveConfiguration(syncConfiguration);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSync2Configuration());
    }

    @Test
    public void saveConfiguration_shouldLoadTheSyncConfigurationFromStringCorrectly() throws Sync2Exception {
        final Sync2Configuration expectedSyncConfiguration = new Sync2Configuration();

        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress2");
        expectedSyncConfiguration.setGeneral(general);

        ClassConfiguration encounterClass = new ClassConfiguration("Encounter",
                "encounter", "org.openmrs.Encounter", false);
        ClassConfiguration visitClass = new ClassConfiguration("Visit",
                "visit", "org.openmrs.Visit", false);
        List<ClassConfiguration> classes = Arrays.asList(encounterClass, visitClass);

        Sync2MethodConfiguration push = new Sync2MethodConfiguration(false, 24, classes);
        expectedSyncConfiguration.setPush(push);

        Sync2MethodConfiguration pull = new Sync2MethodConfiguration(false, 24, classes);
        expectedSyncConfiguration.setPull(pull);

        String json = Sync2Utils.readResourceFile(sampleFeedConfigurationPath2);
        Sync2ConfigurationServiceImpl sync2ConfigurationService = new Sync2ConfigurationServiceImpl();
        sync2ConfigurationService.saveConfiguration(json);

        Assert.assertEquals(expectedSyncConfiguration, sync2ConfigurationService.getSync2Configuration());
    }

}