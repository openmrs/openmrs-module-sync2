package org.openmrs.module.sync2.api.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;
import org.openmrs.module.sync2.api.model.configuration.Sync2Configuration;

public class Sync2Utils {

    public static String readResourceFile(String file) throws Sync2Exception {
        try (InputStream in = Sync2Utils.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new Sync2Exception("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new Sync2Exception(e);
        }
    }

    public static Sync2Configuration parseJsonFileToSyncConfiguration(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(readResourceFile(resourcePath), Sync2Configuration.class);
        } catch (IOException e) {
            throw new Sync2Exception(e);
        }
    }

    public static Sync2Configuration parseJsonStringToSyncConfiguration(String value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(value, Sync2Configuration.class);
        } catch (IOException e) {
            throw new Sync2Exception(e);
        }
    }

    public static boolean isValidateJson(String json) throws Sync2Exception {
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
            while (parser.nextToken() != null) {}
        } catch (JsonParseException jpe) {
            return false;
        } catch (IOException e) {
            throw new Sync2Exception(e);
        }
        return true;
    }

    public static String writeSyncConfigurationToJsonString(Sync2Configuration sync2Configuration) throws Sync2Exception{
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            return writer.writeValueAsString(sync2Configuration);
        } catch (IOException e) {
            throw new Sync2Exception(e);
        }
    }

    public static void writeSyncConfigurationToJsonFile(Sync2Configuration syncConfigurations, String file)
            throws Sync2Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            File resultFile = new File(Sync2Utils.class.getClassLoader().getResource("").getPath() + file);
            writer.writeValue(resultFile, syncConfigurations);
        } catch (IOException e) {
            throw new Sync2Exception(e);
        }
    }

    public static boolean resourceFileExists(String path) {
        InputStream in = Sync2Utils.class.getClassLoader().getResourceAsStream(path);
        return in != null;
    }
}
