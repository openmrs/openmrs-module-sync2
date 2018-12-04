package org.openmrs.module.sync2.api.utils;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.exceptions.SyncValidationException;
import org.openmrs.module.sync2.api.model.configuration.ClientConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.enums.ResourcePathType;
import org.openmrs.module.sync2.api.validator.Errors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.ABSOLUTE;

public class SyncConfigurationUtils {

    public static void checkIfConfigurationIsValid() {
        Errors errors = SyncUtils.getSyncConfigurationService().validateConfiguration();
        if (errors.hasErrors()) {
            throw new SyncValidationException(String.format("It is impossible to invoke synchronization. " +
                    "Configuration validation errors occur. \n%s", errors), errors);
        }
    }

    public static String readResourceFile(String file) throws SyncException {
        try (InputStream in = SyncUtils.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new SyncException("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static String readResourceFileAbsolutePath(String file)  {
        File initialFile = new File(file);

        try (InputStream in = new FileInputStream(initialFile)) {
            if (in == null) {
                throw new SyncException("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }


    public static SyncConfiguration parseJsonFileToSyncConfiguration(String path, ResourcePathType absolutePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String resourceFile = absolutePath.equals(ABSOLUTE) ?
                    readResourceFileAbsolutePath(path) : readResourceFile(path);

            return mapper.readValue(resourceFile, SyncConfiguration.class);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static SyncConfiguration parseJsonStringToSyncConfiguration(String value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(value, SyncConfiguration.class);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static boolean isValidateJson(String json) throws SyncException {
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
            while (parser.nextToken() != null) {}
        } catch (JsonParseException jpe) {
            return false;
        } catch (IOException e) {
            throw new SyncException(e);
        }
        return true;
    }

    public static String writeSyncConfigurationToJsonString(SyncConfiguration syncConfiguration) throws SyncException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            return writer.writeValueAsString(syncConfiguration);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static void writeSyncConfigurationToJsonFile(SyncConfiguration syncConfigurations, String absolutePath)
            throws SyncException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            File resultFile = new File(absolutePath);
            writer.writeValue(resultFile, syncConfigurations);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static boolean resourceFileExists(String path) {
        InputStream in = SyncUtils.class.getClassLoader().getResourceAsStream(path);
        return in != null;
    }

    public static boolean customConfigExists(String path) {
        return new File(path).exists();
    }

    public static ClientConfiguration getClientConfiguration(String clientName) {
        ClientConfiguration configuration = null;
        LinkedHashMap<String, ClientConfiguration> availableConfigurations = SyncUtils.getSyncConfigurationService()
                .getSyncConfiguration().getGeneral().getClients();
        if (!availableConfigurations.isEmpty()) {
            configuration = availableConfigurations.get(clientName);
        }
        return configuration;
    }
}
