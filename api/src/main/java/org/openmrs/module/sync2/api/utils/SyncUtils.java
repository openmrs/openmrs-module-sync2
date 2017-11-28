package org.openmrs.module.sync2.api.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;

public class SyncUtils {

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


    public static Map<String, String> getLinks(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};

        try {
            HashMap<String, String> result = mapper.readValue(json, typeRef);

            return result;
        } catch (IOException e) {
            throw new SyncException(String.format("Invalid resource links JSON Object: %s  ", json), e);
        }
    }

    public static String getBaseUrl(String stringUri) {

        try {
            URI uri = new URI(stringUri);

            return uri.getScheme() + "://" + uri.getAuthority() + "/";
        } catch(URISyntaxException e) {
            throw new SyncException(String.format("Bad Atomfeed URI: %s. ", stringUri), e);
        }

    }

    public static String getCategoryAndActionByTag(String tag) {
        Map<String, String> map = parseTag(tag);
        return map.get("Category.term");
    }

    private static HashMap<String, String> parseTag(String tag) {
        final HashMap<String, String> map = new HashMap<>();

        for (String pair : tag.split("\n")) {
            String[] keyValue = pair.split("=");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }
}
