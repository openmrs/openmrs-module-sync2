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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonFileToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonStringToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.readResourceFile;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.isValidateJson;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonFile;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonString;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.resourceFileExists;
import static org.openmrs.module.sync2.api.utils.SyncUtils.serializeMapToPrettyJson;



public class SyncConfigurationUtilsTest {

    private static final String sampleSyncConfigurationPath = "sampleSyncConfiguration.json";
    private static final String incorrectSyncConfigurationPath = "incorrectSyncConfiguration.json";
    private static final String sampleSerializedMap = "sampleSerializedMap.json";

    private static final String notExistingFilePath = "pathToNotExistingFile";
    private static final SyncConfiguration expectedConfiguration = new SyncConfiguration();

    @Before
    public void setUp() {
        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress", false, false);
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

        String result = readResourceFile(sampleResourcePath);
        Assert.assertEquals(result, expectedResult);
    }

    @Test(expected = SyncException.class)
    public void readResourceFile_shouldThrowIoExceptionIfFileDoesNotExist() throws SyncException {
        readResourceFile(notExistingFilePath);
    }

    @Test
    public void parseJsonFileToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws SyncException {
        SyncConfiguration result = parseJsonFileToSyncConfiguration(sampleSyncConfigurationPath);
        Assert.assertEquals(expectedConfiguration, result);
    }

    @Test(expected = SyncException.class)
    public void parseJsonFileToSyncConfiguration_shouldThrowJsonParseException() throws SyncException {
        parseJsonFileToSyncConfiguration(incorrectSyncConfigurationPath);
    }

    @Test
    public void parseJsonStringToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws SyncException {
        String json = readResourceFile(sampleSyncConfigurationPath);
        SyncConfiguration result = parseJsonStringToSyncConfiguration(json);
        Assert.assertEquals(expectedConfiguration, result);
    }

    @Test(expected = SyncException.class)
    public void parseJsonStringToSyncConfiguration_shouldThrowJsonParseException() throws SyncException {
        String json = readResourceFile(incorrectSyncConfigurationPath);
        parseJsonStringToSyncConfiguration(json);
    }

    @Test
    public void isValidateJson_correct() throws SyncException {
        String json = readResourceFile(sampleSyncConfigurationPath);
        Assert.assertTrue(isValidateJson(json));
    }

    @Test
    public void isValidateJson_incorrect() throws SyncException {
        String json = readResourceFile(incorrectSyncConfigurationPath);
        Assert.assertFalse(isValidateJson(json));
    }

    @Test
    public void shouldWriteSyncConfigurationToJsonString() throws SyncException {
        String result = writeSyncConfigurationToJsonString(expectedConfiguration);
        String expected = readResourceFile(sampleSyncConfigurationPath);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldWriteSyncConfigurationToJsonFile() throws SyncException {
        final String path = "newFile.txt";
        writeSyncConfigurationToJsonFile(expectedConfiguration, path);

        String expected = readResourceFile(sampleSyncConfigurationPath);
        String result = readResourceFile(path);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void resourceFileExists_exist() throws SyncException {
        Assert.assertTrue(resourceFileExists(sampleSyncConfigurationPath));
    }

    @Test
    public void resourceFileExists_notExist() throws SyncException {
        Assert.assertFalse(resourceFileExists(notExistingFilePath));
    }

    @Test
    public void serializeMapToPrettyJson_shouldSerializeMapWithStrings() throws SyncException {
        Assert.assertEquals(readResourceFile(sampleSerializedMap), serializeMapToPrettyJson(createSampleMap()));
    }

    @Test
    public void serializeMapToPrettyJson_shouldDeserializeMapWithStrings() throws SyncException {
        Assert.assertEquals(createSampleMap(),
                SyncUtils.deserializeJsonToStringsMap(readResourceFile(sampleSerializedMap)));
    }

    private Map<String, String> createSampleMap() {
        Map<String, String> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        return map;
    }
}
