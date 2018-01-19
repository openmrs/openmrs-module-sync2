package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.CONFIGURATION_DIR;
import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.RELATIVE;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonFileToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonStringToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.readResourceFile;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.isValidateJson;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.readResourceFileAbsolutePath;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonFile;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonString;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.resourceFileExists;
import static org.openmrs.module.sync2.api.utils.SyncUtils.serializeMapToPrettyJson;



public class SyncConfigurationUtilsTest {

    private static final String SAMPLE_SYNC_CONFIGURATION_PATH = "sampleSyncConfiguration.json";
    private static final String INCORRECT_SYNC_CONFIGURATION_PATH = "incorrectSyncConfiguration.json";
    private static final String SAMPLE_SERIALIZED_MAP = "sampleSerializedMap.json";

    private static final String NOT_EXISTING_FILE_PATH = "pathToNotExistingFile";
    private static final SyncConfiguration EXPECTED_CONFIGURATION = new SyncConfiguration();
    
    private static final String SAMPLE_LOCAL_INSTANCE_ID = "localInstanceId";
    
    @Before
    public void setUp() {
        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress",
                SAMPLE_LOCAL_INSTANCE_ID, false, false);
        EXPECTED_CONFIGURATION.setGeneral(general);

        ClassConfiguration locationClass = new ClassConfiguration("Location",
                "location", "org.openmrs.Location", true);
        ClassConfiguration observationClass = new ClassConfiguration("Observation",
                "observation", "org.openmrs.Obs", true);
        List<ClassConfiguration> classes = Arrays.asList(locationClass, observationClass);

        SyncMethodConfiguration push = new SyncMethodConfiguration(true, 12, classes);
        EXPECTED_CONFIGURATION.setPush(push);

        SyncMethodConfiguration pull = new SyncMethodConfiguration(true, 12, classes);
        EXPECTED_CONFIGURATION.setPull(pull);
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
        readResourceFile(NOT_EXISTING_FILE_PATH);
    }

    @Test
    public void parseJsonFileToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws SyncException {
        SyncConfiguration result = parseJsonFileToSyncConfiguration(SAMPLE_SYNC_CONFIGURATION_PATH, RELATIVE);
        Assert.assertEquals(EXPECTED_CONFIGURATION, result);
    }

    @Test(expected = SyncException.class)
    public void parseJsonFileToSyncConfiguration_shouldThrowJsonParseException() throws SyncException {
        parseJsonFileToSyncConfiguration(INCORRECT_SYNC_CONFIGURATION_PATH, RELATIVE);
    }

    @Test
    public void parseJsonStringToSyncConfiguration_shouldCorrectlyParseSampleConfiguration() throws SyncException {
        String json = readResourceFile(SAMPLE_SYNC_CONFIGURATION_PATH);
        SyncConfiguration result = parseJsonStringToSyncConfiguration(json);
        Assert.assertEquals(EXPECTED_CONFIGURATION, result);
    }

    @Test(expected = SyncException.class)
    public void parseJsonStringToSyncConfiguration_shouldThrowJsonParseException() throws SyncException {
        String json = readResourceFile(INCORRECT_SYNC_CONFIGURATION_PATH);
        parseJsonStringToSyncConfiguration(json);
    }

    @Test
    public void isValidateJson_correct() throws SyncException {
        String json = readResourceFile(SAMPLE_SYNC_CONFIGURATION_PATH);
        Assert.assertTrue(isValidateJson(json));
    }

    @Test
    public void isValidateJson_incorrect() throws SyncException {
        String json = readResourceFile(INCORRECT_SYNC_CONFIGURATION_PATH);
        Assert.assertFalse(isValidateJson(json));
    }

    @Test
    public void shouldWriteSyncConfigurationToJsonString() throws SyncException {
        String result = writeSyncConfigurationToJsonString(EXPECTED_CONFIGURATION);
        String expected = readResourceFile(SAMPLE_SYNC_CONFIGURATION_PATH);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldWriteSyncConfigurationToJsonFile() throws SyncException {
        final String path = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(
                CONFIGURATION_DIR).getAbsolutePath(), "sync2.json").getAbsolutePath();

        writeSyncConfigurationToJsonFile(EXPECTED_CONFIGURATION, path);

        String expected = readResourceFile(SAMPLE_SYNC_CONFIGURATION_PATH);
        String result = readResourceFileAbsolutePath(path);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void resourceFileExists_exist() throws SyncException {
        Assert.assertTrue(resourceFileExists(SAMPLE_SYNC_CONFIGURATION_PATH));
    }

    @Test
    public void resourceFileExists_notExist() throws SyncException {
        Assert.assertFalse(resourceFileExists(NOT_EXISTING_FILE_PATH));
    }

    @Test
    public void serializeMapToPrettyJson_shouldSerializeMapWithStrings() throws SyncException {
        Assert.assertEquals(readResourceFile(SAMPLE_SERIALIZED_MAP), serializeMapToPrettyJson(createSampleMap()));
    }

    @Test
    public void serializeMapToPrettyJson_shouldDeserializeMapWithStrings() throws SyncException {
        Assert.assertEquals(createSampleMap(),
                SyncUtils.deserializeJsonToStringsMap(readResourceFile(SAMPLE_SERIALIZED_MAP)));
    }

    private Map<String, String> createSampleMap() {
        Map<String, String> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        return map;
    }
}
