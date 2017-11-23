package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;

import java.util.Arrays;
import java.util.List;

public class SyncUtilsTest {

    private static final String sampleSyncConfigurationPath = "sampleSyncConfiguration.json";
    private static final String incorrectSyncConfigurationPath = "incorrectSyncConfiguration.json";
    private static final String notExistingFilePath = "pathToNotExistingFile";
    private static final SyncConfiguration expectedConfiguration = new SyncConfiguration();

    @Before
    public void setUp() throws Exception {
        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress");
        expectedConfiguration.setGeneral(general);

        ClassConfiguration locationClass = new ClassConfiguration("Location",
                "location", "org.openmrs.Location", true);
        ClassConfiguration observationClass = new ClassConfiguration("Observation",
                "observation", "org.openmrs.Obs", true);
        List<ClassConfiguration> classes = Arrays.asList(locationClass, observationClass);

        SyncMethodConfiguration push = new SyncMethodConfiguration(true, 12, classes);
        expectedConfiguration.setPush(push);

        SyncMethodConfiguration pull = new SyncMethodConfiguration(true, 12, classes);
        expectedConfiguration.setPull(pull);
    }

    @Test
    public void readResourceFile_shouldReadSampleFile() throws SyncException {
        final String sampleResourcePath = "sampleTextFile.txt";
        final String expectedResult = "sampleContent";

        String result = SyncUtils.readResourceFile(sampleResourcePath);
        Assert.assertEquals(result, expectedResult);
    }

    @Test(expected = SyncException.class)
    public void readResourceFile_shouldThrowIoExceptionIfFileDoesNotExist() throws SyncException {
        SyncUtils.readResourceFile(notExistingFilePath);
    }

    @Test
    public void parseJsonFileToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws SyncException {
        SyncConfiguration result = SyncUtils.parseJsonFileToSyncConfiguration(sampleSyncConfigurationPath);
        Assert.assertEquals(expectedConfiguration, result);
    }

    @Test(expected = SyncException.class)
    public void parseJsonFileToSyncConfiguration_shouldThrowJsonParseException() throws SyncException {
        SyncUtils.parseJsonFileToSyncConfiguration(incorrectSyncConfigurationPath);
    }

    @Test
    public void parseJsonStringToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws SyncException {
        String json = SyncUtils.readResourceFile(sampleSyncConfigurationPath);
        SyncConfiguration result = SyncUtils.parseJsonStringToSyncConfiguration(json);
        Assert.assertEquals(expectedConfiguration, result);
    }

    @Test(expected = SyncException.class)
    public void parseJsonStringToSyncConfiguration_shouldThrowJsonParseException() throws SyncException {
        String json = SyncUtils.readResourceFile(incorrectSyncConfigurationPath);
        SyncUtils.parseJsonStringToSyncConfiguration(json);
    }

    @Test
    public void isValidateJson_correct() throws SyncException {
        String json = SyncUtils.readResourceFile(sampleSyncConfigurationPath);
        Assert.assertTrue(SyncUtils.isValidateJson(json));
    }

    @Test
    public void isValidateJson_incorrect() throws SyncException {
        String json = SyncUtils.readResourceFile(incorrectSyncConfigurationPath);
        Assert.assertFalse(SyncUtils.isValidateJson(json));
    }

    @Test
    public void writeSyncConfigurationToJsonString() throws SyncException {
        String result = SyncUtils.writeSyncConfigurationToJsonString(expectedConfiguration);
        String expected = SyncUtils.readResourceFile(sampleSyncConfigurationPath);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void writeSyncConfigurationToJsonFile() throws SyncException {
        final String path = "newFile.txt";
        SyncUtils.writeSyncConfigurationToJsonFile(expectedConfiguration, path);

        String expected = SyncUtils.readResourceFile(sampleSyncConfigurationPath);
        String result = SyncUtils.readResourceFile(path);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void resourceFileExists_exist() throws SyncException {
        Assert.assertTrue(SyncUtils.resourceFileExists(sampleSyncConfigurationPath));
    }

    @Test
    public void resourceFileExists_notExist() throws SyncException {
        Assert.assertFalse(SyncUtils.resourceFileExists(notExistingFilePath));
    }
}