package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.Sync2Configuration;
import org.openmrs.module.sync2.api.model.configuration.Sync2MethodConfiguration;

import java.util.Arrays;
import java.util.List;

public class Sync2UtilsTest {

    private static final String sampleSyncConfigurationPath = "sampleSyncConfiguration.json";
    private static final String incorrectSyncConfigurationPath = "incorrectSyncConfiguration.json";
    private static final String notExistingFilePath = "pathToNotExistingFile";
    private static final Sync2Configuration expectedConfiguration = new Sync2Configuration();

    @Before
    public void setUp() throws Exception {
        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress");
        expectedConfiguration.setGeneral(general);

        ClassConfiguration locationClass = new ClassConfiguration("Location", "org.openmrs.Location", true);
        ClassConfiguration observationClass = new ClassConfiguration("Observation", "org.openmrs.Obs", true);
        List<ClassConfiguration> classes = Arrays.asList(locationClass, observationClass);

        Sync2MethodConfiguration push = new Sync2MethodConfiguration(true, 12, classes);
        expectedConfiguration.setPush(push);

        Sync2MethodConfiguration pull = new Sync2MethodConfiguration(true, 12, classes);
        expectedConfiguration.setPull(pull);
    }

    @Test
    public void readResourceFile_shouldReadSampleFile() throws Sync2Exception {
        final String sampleResourcePath = "sampleTextFile.txt";
        final String expectedResult = "sampleContent";

        String result = Sync2Utils.readResourceFile(sampleResourcePath);
        Assert.assertEquals(result, expectedResult);
    }

    @Test(expected = Sync2Exception.class)
    public void readResourceFile_shouldThrowIoExceptionIfFileDoesNotExist() throws Sync2Exception {
        Sync2Utils.readResourceFile(notExistingFilePath);
    }

    @Test
    public void parseJsonFileToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws Sync2Exception {
        Sync2Configuration result = Sync2Utils.parseJsonFileToSyncConfiguration(sampleSyncConfigurationPath);
        Assert.assertEquals(expectedConfiguration, result);
    }

    @Test(expected = Sync2Exception.class)
    public void parseJsonFileToSyncConfiguration_shouldThrowJsonParseException() throws Sync2Exception {
        Sync2Utils.parseJsonFileToSyncConfiguration(incorrectSyncConfigurationPath);
    }

    @Test
    public void parseJsonStringToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws Sync2Exception {
        String json = Sync2Utils.readResourceFile(sampleSyncConfigurationPath);
        Sync2Configuration result = Sync2Utils.parseJsonStringToSyncConfiguration(json);
        Assert.assertEquals(expectedConfiguration, result);
    }

    @Test(expected = Sync2Exception.class)
    public void parseJsonStringToSyncConfiguration_shouldThrowJsonParseException() throws Sync2Exception {
        String json = Sync2Utils.readResourceFile(incorrectSyncConfigurationPath);
        Sync2Utils.parseJsonStringToSyncConfiguration(json);
    }

    @Test
    public void isValidateJson_correct() throws Sync2Exception {
        String json = Sync2Utils.readResourceFile(sampleSyncConfigurationPath);
        Assert.assertTrue(Sync2Utils.isValidateJson(json));
    }

    @Test
    public void isValidateJson_incorrect() throws Sync2Exception {
        String json = Sync2Utils.readResourceFile(incorrectSyncConfigurationPath);
        Assert.assertFalse(Sync2Utils.isValidateJson(json));
    }

    @Test
    public void writeSyncConfigurationToJsonString() throws Sync2Exception {
        String result = Sync2Utils.writeSyncConfigurationToJsonString(expectedConfiguration);
        String expected = Sync2Utils.readResourceFile(sampleSyncConfigurationPath);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void writeSyncConfigurationToJsonFile() throws Sync2Exception {
        final String path = "newFile.txt";
        Sync2Utils.writeSyncConfigurationToJsonFile(expectedConfiguration, path);

        String expected = Sync2Utils.readResourceFile(sampleSyncConfigurationPath);
        String result = Sync2Utils.readResourceFile(path);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void resourceFileExists_exist() throws Sync2Exception {
        Assert.assertTrue(Sync2Utils.resourceFileExists(sampleSyncConfigurationPath));
    }

    @Test
    public void resourceFileExists_notExist() throws Sync2Exception {
        Assert.assertFalse(Sync2Utils.resourceFileExists(notExistingFilePath));
    }
}