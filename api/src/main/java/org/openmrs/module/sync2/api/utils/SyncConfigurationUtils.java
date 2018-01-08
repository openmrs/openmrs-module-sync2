package org.openmrs.module.sync2.api.utils;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SyncConfigurationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncConfigurationUtils.class);

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

    public static SyncConfiguration parseJsonFileToSyncConfiguration(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(readResourceFile(resourcePath), SyncConfiguration.class);
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

    public static void writeSyncConfigurationToJsonFile(SyncConfiguration syncConfigurations, String file)
            throws SyncException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            File resultFile = new File(SyncUtils.class.getClassLoader().getResource("").getPath() + file);
            writer.writeValue(resultFile, syncConfigurations);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static boolean resourceFileExists(String path) {
        InputStream in = SyncUtils.class.getClassLoader().getResourceAsStream(path);
        return in != null;
    }
}
